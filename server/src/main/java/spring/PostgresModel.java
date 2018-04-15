/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package spring;


import com.google.gson.*;
import exceptions.*;
import model.database.DealState;
import model.merchandises.Merchandise;
import model.postgresqlModel.Users;
import model.postgresqlModel.tables.Classes;
import model.postgresqlModel.tables.Deals;
import model.postgresqlModel.tables.Merchandises;
import model.postgresqlModel.tables.News;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Repository
public class PostgresModel implements SpringModel {
    private final static Logger logger = LoggerFactory.getLogger(PostgresModel.class);
    private SessionFactory sessionFactory;
    private ResourceBundle errCodes = ResourceBundle.getBundle("errcodes");
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public PostgresModel() {

    }

    @Override
    public String getUserByToken(String token) {
        Session session = sessionFactory.getCurrentSession();
        try {
            Users users = (Users) session.createQuery("from Users where token=:token")
                    .setParameter("token", token).getSingleResult();
            return gson.toJson(users);
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public String getNewsById(int id) {
        Session session = sessionFactory.getCurrentSession();
        try {
            News news = (News) session.createQuery("from News where id=:id")
                    .setParameter("id", id).getSingleResult();
            return gson.toJson(news);
        } catch (NoResultException e) {
            return null;
        }
    }


    @Override
    public void addMerchandise(Merchandise merch, String user, String token, int price) {
        throw new UnsupportedOperationException("lol. That shit isn't working");
    }

    @Override
    public void removeMerchandise(Merchandise merch, String user, String token) {
        throw new UnsupportedOperationException("lol. That shit isn't working");
    }

    @Override
    public void removeMerchandise(int id, String user, String token) throws MerchandiseAlreadyBought {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.createStoredProcedureQuery("removeMerchandise")
                    .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(4, Boolean.class, ParameterMode.OUT)
                    .setParameter(1, id)
                    .setParameter(2, user)
                    .setParameter(3, token).getOutputParameterValue(4);
            logger.info("removed Merchandise" + id + " by User " + user);
        } catch (JDBCException e) {
            throw new MerchandiseRemoveException(getMessageByCode(e));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<String> searchMerchandise(String query) throws WrongQueryException {
        Session session = sessionFactory.getCurrentSession();
        Query querySet = session.createNativeQuery("SELECT * FROM searchMerchandise(:query)")
                .addEntity(Merchandises.class).setParameter("query", query);
        JsonParser parser = new JsonParser();
        return ((List<Merchandises>) querySet.getResultList())
                .stream().map(o -> {
                    Deals deals = (Deals) session.createNativeQuery("SELECT * FROM getLastDeal(:id)")
                            .setParameter("id", o.getId()).addEntity(Deals.class).getSingleResult();
                    JsonObject object = parser.parse(o.getAllInfo()).getAsJsonObject();
                    object.add("state", new JsonPrimitive(deals.getState()));
                    Users users = (Users) session.createQuery("from Users where id=:id")
                            .setParameter("id", deals.getUserId()).getSingleResult();
                    object.add("user", new JsonPrimitive(users.getUsername()));
                    object.add("price", new JsonPrimitive(deals.getPrice()));
                    return gson.toJson(object);
                }).collect(toList());
    }

    @Override
    public String getMerchantById(int id) throws MerchandiseNotFoundException {
        try {
            Session session = sessionFactory.getCurrentSession();
            Merchandises merch = (Merchandises) session.createQuery("from Merchandises where id = :id")
                    .setParameter("id", id).getSingleResult();
            JsonParser parser = new JsonParser();
            return gson.toJson(parser.parse(merch.getAllInfo()));
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException {
        Session session = sessionFactory.getCurrentSession();

        try {
            StoredProcedureQuery buyMerch = session.createStoredProcedureQuery("buyMerchandise")
                    .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(4, String.class, ParameterMode.OUT)
                    .setParameter(1, id)
                    .setParameter(2, user)
                    .setParameter(3, token);
            String merch = buyMerch.getOutputParameterValue(4).toString();
            logger.info("Merchandise with id=" + id + " was bought by " + user);
            JsonParser parser = new JsonParser();
            return gson.toJson(parser.parse(merch));
        } catch (JDBCException e) {
            logger.error("User " + user + " can't buy merchandise with id=" + id + " because " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("found exception while buying merchandise(id=" + id + ") by " + user);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void setValuesToMerchandise(int id, String params, String user, String token) {
        Session session = sessionFactory.getCurrentSession();

        String kvs = Arrays.stream(params.trim().split(" "))
                .collect(Collectors.joining(", "));
        StoredProcedureQuery updateMerch = session.createStoredProcedureQuery("canUpdateMerch")
                .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Boolean.class, ParameterMode.OUT).setParameter(1, id)
                .setParameter(2, user)
                .setParameter(3, token);
        try {
            updateMerch.getOutputParameterValue(4);
            Merchandises merch = (Merchandises) session.createQuery("from Merchandises where id=:id")
                    .setParameter("id", id).getSingleResult();
            session.createNativeQuery("UPDATE " + merch.getClassName() + " SET " + kvs + " WHERE id=:id")
                    .setParameter("id", id).executeUpdate();
            logger.info("updated Merchandise" + id + " by User " + user);
        } catch (JDBCException e) {
            logger.error(getMessageByCode(e) + " while updating values merchandise " + id + " by user " + user);
            throw new MerchandiseUpdateException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("found exception while updating merchandise(id=" + id + ") by user " + user);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<String> getAvailableClasses() {
        Session session = sessionFactory.getCurrentSession();

        List<Classes> classes = session.createQuery("from Classes ").getResultList();
        return classes.stream().map(Classes::getClassname).collect(toList());
    }

    @Override
    public List<String> getMandatoryFields(String className) {
        Session session = sessionFactory.getCurrentSession();

        try {
            Classes classes = (Classes) session.createQuery("from Classes where classname like :name")
                    .setParameter("name", className).getSingleResult();
            return Arrays.asList(classes.getFields());
        } catch (Throwable e) {
            logger.error("Can't get fields for class " + className + ", because exception: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public String getMandatoryFieldsWithTypes(String className) {
        Session session = sessionFactory.getCurrentSession();

        try {
            Classes classes = (Classes) session.createQuery("from Classes where classname like :name")
                    .setParameter("name", className).getSingleResult();
            String[] fields = classes.getFields();
            String[] types = classes.getTypes();
            JsonObject root = new JsonObject();
            root.add("$schema", new JsonPrimitive("http://json-schema.org/draft-03/schema#"));
            root.add("type", new JsonPrimitive("object"));
            JsonObject properties = new JsonObject();
            IntStream.range(0, fields.length).forEach(i -> {
                JsonObject type = new JsonObject();
                type.add("title", new JsonPrimitive(fields[i]));
                if (types[i].equals("text")) {
                    type.add("type", new JsonPrimitive("string"));
                    type.add("format", new JsonPrimitive("text"));
                } else {
                    type.add("type", new JsonPrimitive(types[i]));
                }
                type.add("required", new JsonPrimitive(true));
                type.add("description", new JsonPrimitive("merchandise " + fields[i]));
                properties.add(fields[i], type);
            });
            JsonObject price = new JsonObject();
            price.add("type", new JsonPrimitive("integer"));
            price.add("title", new JsonPrimitive("price"));
            price.add("minimum", new JsonPrimitive(1));
            price.add("description", new JsonPrimitive("The amount of money you will receive for the sale of this product"));
            price.add("required", new JsonPrimitive(true));
            properties.add("price", price);

            root.add("properties", properties);
            return root.toString();
        } catch (Throwable e) {
            logger.error("Can't get fields for class " + className + ", because exception: " + e.getMessage());
            return "";
        }
    }

    @Override
    public void addMerchandiseByMap(String className,
                                    Map<String, String> kvs,
                                    String user,
                                    String token,
                                    int price)
            throws CreateMerchandiseException {
        StringBuilder keys = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        for (String key : kvs.keySet()) {
            keys.append(key).append(", ");
            vals.append("?").append(", ");
        }
        Session session = sessionFactory.getCurrentSession();

        NativeQuery query = session.createNativeQuery("INSERT INTO "
                + className +
                "(" + keys.substring(0, keys.length() - 2) + ") " +
                "VALUES (" + vals.substring(0, vals.length() - 2) + ") RETURNING id");
        int i = 1;
        for (String key : kvs.keySet()) {
            try {
                double param = Double.parseDouble(kvs.get(key));
                query.setParameter(i, param);
            } catch (Exception e) {
                query.setParameter(i, kvs.get(key));
            }
            i++;
        }
        try {
            int id = (int) query.getSingleResult();
//            Integer lastId = (Integer) session.createQuery("SELECT LAST_INSERT_ID()").uniqueResult();

            Users gUser = (Users) session.createQuery("from Users where username like :un and token= :tkn")
                    .setParameter("un", user)
                    .setParameter("tkn", token).getSingleResult();

            Deals deals = new Deals();
            deals.setMerchId(id);
            deals.setPrice(price);
            deals.setState(DealState.FOR_SALE.toString());
            deals.setUserId(gUser.getId());
            session.save(deals);
            logger.info("Added new merchandise(id=" + id + ") by user " + user);
        } catch (Throwable e) {
            logger.error("Can't add merchandise, because exception: " + e.getMessage());
            throw new JDBCException(e.getMessage(), new SQLException(e.getCause()), query.getQueryString());
        }
    }

    @Override
    public String login(String username, String password) {
        Session session = sessionFactory.getCurrentSession();

        try {
            StoredProcedureQuery login = session.createStoredProcedureQuery("login")
                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT);
            login.setParameter(1, username);
            login.setParameter(2, password);
            String token = login.getOutputParameterValue(3).toString();
            logger.info("User " + username + " successfully logged in");
            return token;
        } catch (JDBCException e) {
            logger.error("User " + username + " can't login because: " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("Found exception while logging in by user " + username + " because exception: " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public boolean register(String username, String pass) {
        Session session = sessionFactory.getCurrentSession();

        try {
            Users user = new Users();
            user.setUsername(username);
            user.setPassword(pass);
            user.setRole("user");
            session.save(user);
            logger.info("User " + username + " successfully registered");
            return true;
        } catch (JDBCException e) {
            logger.error("Can't register " + username + " because error " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("Found exception while registration " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String getMessageByCode(JDBCException e) {
        return errCodes.getString(e.getSQLException().getSQLState());
    }

    @Override
    public void disconnect(String username, String token) {
        Session session = sessionFactory.getCurrentSession();

        try {
            StoredProcedureQuery logout = session.createStoredProcedureQuery("logout")
                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(3, Boolean.class, ParameterMode.OUT);
            logout.setParameter(1, username).setParameter(2, token);
            logout.getOutputParameterValue(3);
            logger.info("User " + username + " disconnected");
        } catch (JDBCException e) {
            logger.error("Can't disconnect user because " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("Can't disconnect user because exception: " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<String> getDealsByUser(String username, String token) {
        Session session = sessionFactory.getCurrentSession();
        try {
            List<Deals> dealsList = session.createQuery("from deals where userId=(select id from Users where username like :un AND token like :tkn) order by id desc")
                    .setParameter("un", username)
                    .setParameter("tkn", token).getResultList();
            return formatDeals(dealsList);
        } catch (Exception e) {
            logger.error("Found exception while taking deals for user " + username + "Exception message: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<String> formatDeals(List<Deals> dealsList) {
        return dealsList.stream().map(gson::toJson).collect(toList());
    }

    @Override
    public String getDealsByUser(String username, String token, int offset, int limit) {
        Session session = sessionFactory.getCurrentSession();
        try {
            List<Deals> dealsList = session.createNativeQuery("SELECT * FROM deals WHERE " +
                    "userId=(SELECT id FROM Users WHERE username LIKE :un AND token LIKE :tkn) ORDER BY id DESC OFFSET :ofs LIMIT :lim")
                    .setParameter("un", username)
                    .setParameter("tkn", token)
                    .setParameter("ofs", offset)
                    .setParameter("lim", limit)
                    .addEntity(Deals.class)
                    .getResultList();
            long counter = ((long) session.createQuery("select count(id) from deals where userId=(SELECT id FROM Users WHERE username LIKE :un AND token LIKE :tkn) group by userId")
                    .setParameter("un", username)
                    .setParameter("tkn", token).getSingleResult());
            JsonObject root = new JsonObject();
            root.add("count", new JsonPrimitive(counter));
            JsonArray dealsArray = new JsonArray();
            JsonParser parser = new JsonParser();
            dealsList.forEach(deal -> dealsArray.add(parser.parse(gson.toJson(deal)).getAsJsonObject()));
            root.add("deals", dealsArray);
            return root.toString();
//            return formatDeals(dealsList);
        } catch (Exception e) {
            logger.error("Found exception while taking deals for user " + username + "Exception message: " + e.getMessage());
            return "";
        }
    }

    @Override
    public boolean changeLogin(String username, String newLogin, String token) {
        Session session = sessionFactory.getCurrentSession();

        StoredProcedureQuery updateUsername = session.createStoredProcedureCall("updateUsername")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Boolean.class, ParameterMode.OUT)
                .setParameter(1, username)
                .setParameter(2, newLogin)
                .setParameter(3, token);
        try {
            boolean updated = (Boolean) updateUsername.getOutputParameterValue(4);
            logger.info("User " + username + " changed his username to " + newLogin);
            return updated;
        } catch (JDBCException e) {
            logger.error("User " + username + "can't change his username to " + newLogin + " because " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("Found exception while changing login: " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void changePassword(String username, String newPassword, String token) {
        Session session = sessionFactory.getCurrentSession();

        StoredProcedureQuery updatePassword = session.createStoredProcedureCall("updatePassword")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Boolean.class, ParameterMode.OUT)
                .setParameter(1, username)
                .setParameter(2, newPassword)
                .setParameter(3, token);
        try {
            updatePassword.getOutputParameterValue(4);
            logger.info("User " + username + " changed his password to " + newPassword);
        } catch (JDBCException e) {
            logger.error("User " + username + "can't change his password to " + newPassword + " because " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("Found exception while changing password: " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public String getDealById(int id) {
        Session session = sessionFactory.getCurrentSession();
        try {
            Deals deal = (Deals) session.createQuery("from deals where id = :id")
                    .setParameter("id", id).getSingleResult();
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(gson.toJson(deal)).getAsJsonObject();
            object.add("merchandise", parser.parse(getMerchantById(deal.getMerchId())));
            return gson.toJson(object);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean exportAllData(String fileName) {
        Session session = sessionFactory.getCurrentSession();

        News news = new News();
        news.setHeader("News now available");
        news.setDescription("If you want to be aware of all the events of this site, then this is for you.");
        news.setText("A new section was created in order to be always up to date on the latest news on this site. For many of you this will seem uninteresting and you will prove to be right. Because the news on this site is only needed to fill the void on the main page and in the databases. I believe that on all self-respecting sites there should be a news line. or RSS-distribution. Here we now have. Watch the news, gentlemen.\n");
//        news.setText("This site was created to show you what I can. And the entire system was created in order to prove that I'm not an empty place in programming. It was hard, but our team coped. Now it seems that there is nothing with which our team could not cope.\n" +
//                "Our team consists of one person who was terribly tired while writing all this. Really. Why did I even create this news? The next news will be just about them.\n");
//        news.setText("Every day people look through the web pages to find something interesting and where they could take the money. Our site is just like that. You can not only look at the pages of our site, but also earn a little. The system is simple. You must add the product to the system of one of the proposed categories and, after its purchase by someone from the visitors, you will be supplemented with balances.");
//        news.setHeader("New slaves");
//        news.setDescription("Many types of new slaves is coming out soon");
//        news.setText("Our system is growing up. So we need to add new types of slaves." +
//                "\nWe already have aliens, slaves, poisons and foods. But as you know we need to grow to be a real shopping platform." +
//                " So you can support us by sending , as you think, new needed type on win10@list.ru\n" +
//                "With love your SlaveMarket administration.");
        news.setSlider(false);
        try {
            BufferedImage image = ImageIO.read(new File(fileName));
            Image scaled = image.getScaledInstance(1366, 768, Image.SCALE_SMOOTH);
            BufferedImage result = new BufferedImage(1366, 768, image.getType());
            Graphics2D g = result.createGraphics();
            g.drawImage(scaled, 0, 0, null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(result, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            news.setImage(imageInByte);

            try {
                session.save(news);
            } catch (HibernateException e) {
                e.printStackTrace();
            } finally {
                session.close();
            }
            return true;
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public boolean importAllData(String filename) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getNews() {
        Session session = sessionFactory.getCurrentSession();

        List<News> newsList = session.createQuery("from News ").getResultList();
        return newsList.stream().map(news -> gson.toJson(news)).collect(toList());
    }

    @Override
    public String newsById(int id) {
        Session session = sessionFactory.getCurrentSession();

        News news = (News) session.createQuery("from News where id=:id")
                .setParameter("id", id).getSingleResult();
        return gson.toJson(news);
    }

    @Override
    public void addNews(String username, String token, String header, String description, String text, byte[] image, boolean slider) {
        Session session = sessionFactory.getCurrentSession();

        Users user = (Users) session.createQuery("from Users where username=:username AND token=:token")
                .setParameter("username", username)
                .setParameter("token", token).getSingleResult();
        if (user.getRole().equals("admin")) {
            News news = new News();
            news.setHeader(header);
            news.setText(text);
            news.setDescription(description);
            news.setImage(image);
            news.setSlider(slider);
            news.setAuthor(user.getId());
            try {
                session.save(news);
            } catch (JDBCException e) {
                logger.error(getMessageByCode(e) + " while adding news");
            }
        } else {
            logger.error("User" + username + " tried to add news, but have no privileges to do this");
            throw new PermissionDeniedException();
        }

    }

    @Override
    public void setRole(String username, String token, int id, String role) {
        Session session = sessionFactory.getCurrentSession();
        Users admin = (Users) session.createQuery("from Users where username=:uname and token=:tkn")
                .setParameter("uname", username)
                .setParameter("tkn", token).getSingleResult();
        if (admin.getRole().equals("admin")) {
            if (!getAvailableRoles().contains(role)) {
                throw new IllegalArgumentException("Unknown role");
            } else {
                Users users = (Users) session.createQuery("from Users where id=:id")
                        .setParameter("id", id).getSingleResult();
                users.setRole(role);
                session.update(users);
            }
        } else {
            throw new PermissionDeniedException();
        }
    }

    @Override
    public List<String> availableRoles() {
        return null;
    }

    @Override
    public List<String> getAllUsers() {
        Session session = sessionFactory.getCurrentSession();
        List<Users> users = session.createQuery("from Users ").getResultList();
        return users.stream().map(user -> user.getUsername() + "#" + user.getId()).collect(toList());
    }

    @Override
    public String updateToken(String username, String password) {
        Session session = sessionFactory.getCurrentSession();
        Users user = (Users) session.createQuery("from Users where username=:uname and password LIKE ENCODE(CONVERT_TO(:psw, 'UTF-8'), 'base64')")
                .setParameter("uname", username)
                .setParameter("psw", password).getSingleResult();
        user.setToken(null);
        session.update(user);
        session.flush();
        String token = login(username, password);
        return token;
    }

    @Override
    public List<String> getAvailableRoles() {
        Session session = sessionFactory.getCurrentSession();
        List<String> roles = session.createNativeQuery("SELECT role FROM roles").getResultList();
        return roles;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

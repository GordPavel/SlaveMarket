package model;


import com.google.gson.*;
import exceptions.*;
import model.database.DealState;
import model.merchandises.Merchandise;
import model.postgresqlModel.Users;
import model.postgresqlModel.tables.Classes;
import model.postgresqlModel.tables.Deals;
import model.postgresqlModel.tables.Merchandises;
import model.postgresqlModel.tables.News;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class PostgresModel implements Model {
    private final static Logger logger = LoggerFactory.getLogger(PostgresModel.class);
    private Session session;
    private ResourceBundle errCodes = ResourceBundle.getBundle("errcodes");
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructor with default credentials.
     * username: s3rius
     * password: 19216211
     */
    public PostgresModel() {
        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        logger.info("session created");
    }

    /**
     * Constructor with custom credentials.
     *
     * @param username user's username.
     * @param password user's password.
     */
    public PostgresModel(String username, String password) {
        try {

            Configuration hibConfiguration = new Configuration();
            hibConfiguration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            hibConfiguration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost/slaveMarket");
            hibConfiguration.setProperty("hibernate.connection.username", username);
            hibConfiguration.setProperty("hibernate.connection.password", password);
            hibConfiguration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
            hibConfiguration.setProperty("hibernate.current_session_context_class",
                    "org.hibernate.context.internal.ThreadLocalSessionContext");

            SessionFactory factory = hibConfiguration.buildSessionFactory();
            session = factory.openSession();
            logger.info("session created");
        } catch (Throwable thr) {
            logger.error("Can't establish connection to postgresql");
            throw new ExceptionInInitializerError(thr);
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
        Transaction transaction = session.beginTransaction();
        try {
            session.createStoredProcedureQuery("removeMerchandise")
                    .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(4, Boolean.class, ParameterMode.OUT)
                    .setParameter(1, id)
                    .setParameter(2, user)
                    .setParameter(3, token).getOutputParameterValue(4);
            transaction.commit();
            logger.info("removed Merchandise" + id + " by User " + user);
        } catch (JDBCException e) {
            transaction.rollback();
            throw new MerchandiseRemoveException(getMessageByCode(e));
        } catch (Exception e) {
            transaction.rollback();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<String> searchMerchandise(String query) throws WrongQueryException {
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
        Transaction transaction = session.beginTransaction();
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
            transaction.commit();
            logger.info("Merchandise with id=" + id + " was bought by " + user);
            JsonParser parser = new JsonParser();
            return gson.toJson(parser.parse(merch));
        } catch (JDBCException e) {
            transaction.rollback();
            logger.error("User " + user + " can't buy merchandise with id=" + id + " because " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("found exception while buying merchandise(id=" + id + ") by " + user);
            transaction.rollback();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void setValuesToMerchandise(int id, String params, String user, String token) {
        String kvs = Arrays.stream(params.trim().split(" "))
                .collect(Collectors.joining(", "));
        StoredProcedureQuery updateMerch = session.createStoredProcedureQuery("canUpdateMerch")
                .registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Boolean.class, ParameterMode.OUT).setParameter(1, id)
                .setParameter(2, user)
                .setParameter(3, token);
        session.beginTransaction();
        try {
            updateMerch.getOutputParameterValue(4);
            Merchandises merch = (Merchandises) session.createQuery("from Merchandises where id=:id")
                    .setParameter("id", id).getSingleResult();
            session.createNativeQuery("UPDATE " + merch.getClassName() + " SET " + kvs + " WHERE id=:id")
                    .setParameter("id", id).executeUpdate();
            session.getTransaction().commit();
            logger.info("updated Merchandise" + id + " by User " + user);
        } catch (JDBCException e) {
            session.getTransaction().rollback();
            logger.error(getMessageByCode(e) + " while updating values merchandise " + id + " by user " + user);
            throw new MerchandiseUpdateException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("found exception while updating merchandise(id=" + id + ") by user " + user);
            session.getTransaction().rollback();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<String> getAvailableClasses() {
        List<Classes> classes = session.createQuery("from Classes ").getResultList();
        return classes.stream().map(Classes::getClassname).collect(toList());
    }

    @Override
    public List<String> getMandatoryFields(String className) {
        try {
            Classes classes = (Classes) session.createQuery("from Classes where classname like :name")
                    .setParameter("name", className).getSingleResult();
            return Arrays.asList(classes.getFields());
        } catch (Throwable e) {
            return new ArrayList<>();
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
        Transaction transaction = session.beginTransaction();
        try {
            int id = (int) query.getSingleResult();
//            Integer lastId = (Integer) session.createQuery("SELECT LAST_INSERT_ID()").uniqueResult();
            transaction.commit();

            Users gUser = (Users) session.createQuery("from Users where username like :un and token= :tkn")
                    .setParameter("un", user)
                    .setParameter("tkn", token).getSingleResult();

            Deals deals = new Deals();
            deals.setMerchId(id);
            deals.setPrice(price);
            deals.setState(DealState.FOR_SALE.toString());
            deals.setUserId(gUser.getId());
            transaction = session.beginTransaction();
            session.save(deals);
            transaction.commit();
            logger.info("Added new merchandise(id=" + id + ") by user " + user);
        } catch (Throwable e) {
            transaction.rollback();
            logger.error("Can't add merchandise, because exception: " + e.getMessage());
            throw new JDBCException(e.getMessage(), new SQLException(e.getCause()), query.getQueryString());
        }
    }

    @Override
    public String login(String username, String password) {
        Transaction transaction = session.beginTransaction();
        try {
            StoredProcedureQuery login = session.createStoredProcedureQuery("login")
                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT);
            login.setParameter(1, username);
            login.setParameter(2, password);
            String token = login.getOutputParameterValue(3).toString();
            transaction.commit();
            logger.info("User " + username + " successfully logged in");
            return token;
        } catch (JDBCException e) {
            transaction.rollback();
            logger.error("User " + username + " can't login because: " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("Found exception while logging in by user " + username + " because exception: " + e.getMessage());
            transaction.rollback();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public boolean register(String username, String pass) {
        Transaction transaction = session.beginTransaction();
        try {
            Users user = new Users();
            user.setUsername(username);
            user.setPassword(pass);
            session.save(user);
            transaction.commit();
            logger.info("User " + username + " successfully registered");
            return true;
        } catch (JDBCException e) {
            transaction.rollback();
            logger.error("Can't register " + username + " because error " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            logger.error("Found exception while registration " + e.getMessage());
            transaction.rollback();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String getMessageByCode(JDBCException e) {
        return errCodes.getString(e.getSQLException().getSQLState());
    }

    @Override
    public void disconnect(String username, String token) {
        Transaction transaction = session.beginTransaction();
        try {
            StoredProcedureQuery logout = session.createStoredProcedureQuery("logout")
                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter(3, Boolean.class, ParameterMode.OUT);
            logout.setParameter(1, username).setParameter(2, token);
            logout.getOutputParameterValue(3);
            transaction.commit();
            logger.info("User " + username + " disconnected");
        } catch (JDBCException e) {
            transaction.rollback();
            logger.error("Can't disconnect user because " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Can't disconnect user because exception: " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<String> getDealsByUser(String username, String token) {
        List<Deals> dealsList = session.createQuery("from deals where userId=(select id from Users where username like :un AND token like :tkn)")
                .setParameter("un", username)
                .setParameter("tkn", token).getResultList();
        JsonParser parser = new JsonParser();
        return dealsList.stream().map(deal -> {
            JsonObject mDeal = new JsonObject();
            mDeal.add("date",
                    new JsonPrimitive(deal.getTime().toString()));
            mDeal.add("id", new JsonPrimitive(deal.getId()));
            mDeal.add("userId", new JsonPrimitive(deal.getUserId()));
            mDeal.add("state", new JsonPrimitive(deal.getState()));
            mDeal.add("price", new JsonPrimitive(deal.getPrice()));
            mDeal.add("merchandise", parser.parse(getMerchantById(deal.getMerchId())));
            return gson.toJson(mDeal);
        }).collect(toList());
    }

    @Override
    public boolean changeLogin(String username, String newLogin, String token) {
        StoredProcedureQuery updateUsername = session.createStoredProcedureCall("updateUsername")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Boolean.class, ParameterMode.OUT)
                .setParameter(1, username)
                .setParameter(2, newLogin)
                .setParameter(3, token);
        Transaction transaction = session.beginTransaction();
        try {
            boolean updated = (Boolean) updateUsername.getOutputParameterValue(4);
            transaction.commit();
            logger.info("User " + username + " changed his username to " + newLogin);
            return updated;
        } catch (JDBCException e) {
            transaction.rollback();
            logger.error("User " + username + "can't change his username to " + newLogin + " because " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Found exception while changing login: " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void changePassword(String username, String newPassword, String token) {
        StoredProcedureQuery updatePassword = session.createStoredProcedureCall("updatePassword")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, Boolean.class, ParameterMode.OUT)
                .setParameter(1, username)
                .setParameter(2, newPassword)
                .setParameter(3, token);
        Transaction transaction = session.beginTransaction();
        try {
            updatePassword.getOutputParameterValue(4);
            transaction.commit();
            logger.info("User " + username + " changed his password to " + newPassword);
        } catch (JDBCException e) {
            transaction.rollback();
            logger.error("User " + username + "can't change his password to " + newPassword + " because " + getMessageByCode(e));
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            transaction.rollback();
            logger.error("Found exception while changing password: " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public String getDealById(int id) {
        try {
            Deals deal = (Deals) session.createQuery("from deals where id = :id")
                    .setParameter("id", id).getSingleResult();
            JsonParser parser = new JsonParser();
            JsonObject mDeal = new JsonObject();
            mDeal.add("date",
                    new JsonPrimitive(deal.getTime().toString()));
            mDeal.add("id", new JsonPrimitive(deal.getId()));
            mDeal.add("state", new JsonPrimitive(deal.getState()));
            mDeal.add("price", new JsonPrimitive(deal.getPrice()));
            mDeal.add("userId", new JsonPrimitive(deal.getUserId()));
            mDeal.add("merchandise", parser.parse(getMerchantById(deal.getMerchId())));
            return mDeal.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean exportAllData(String fileName) {
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
        byte[] img = new byte[0];
        try {
            img = Files.readAllBytes(Paths.get("/home/s3rius/Development/Projects/slaveMarket/server/web/resources/images/news.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        news.setImage(img);
        Transaction transaction = session.beginTransaction();
        try {
            session.save(news);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean importAllData(String filename) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getNews() {
        List<News> newsList = session.createQuery("from News ").getResultList();
        return newsList.stream().map(news -> gson.toJson(news)).collect(toList());
    }

    @Override
    public String newsById(int id) {
        News news = (News) session.createQuery("from News where id=:id")
                .setParameter("id", id).getSingleResult();
        return gson.toJson(news);
    }

    @Override
    public void addNews(String username, String token, String header, String description, String text, byte[] image, boolean slider) {
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
            Transaction transaction = session.beginTransaction();
            try {
                session.save(news);
                transaction.commit();
            } catch (JDBCException e) {
                transaction.rollback();
                logger.error(getMessageByCode(e) + " while adding news");
            }
        } else {
            logger.error("User" + username + " tried to add news, but have no privileges to do this");
            throw new PermissionDeniedException();
        }

    }

    @Override
    public void setRole(String username, String token, int id, String role) {

    }

    @Override
    public List<String> availableRoles() {
        return null;
    }

    @Override
    public List<String> getAllUsers() {
        List<Users> users = session.createQuery("from Users ").getResultList();
        return users.stream().map(user -> user.getUsername() + "#" + user.getId()).collect(toList());
    }


    private void setMerchId(List<Deals> deals, int oldId, int newId) {
        if (oldId != newId) {
            deals.forEach(deal -> {
                if (deal.getMerchId() == oldId) {
                    deal.setMerchId(newId);
                }
            });
        }
    }

    private void setNewUserId(List<Deals> deals, int oldId, int newId) {
        if (oldId != newId) {
            deals.forEach(deal -> {
                if (deal.getUserId() == oldId) {
                    deal.setUserId(newId);
                }
            });
        }
    }
}

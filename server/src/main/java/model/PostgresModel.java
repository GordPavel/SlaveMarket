package model;


import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import exceptions.*;
import model.database.DealState;
import model.merchandises.Merchandise;
import model.postgresqlModel.JsonExportObject;
import model.postgresqlModel.Users;
import model.postgresqlModel.tables.Classes;
import model.postgresqlModel.tables.Deals;
import model.postgresqlModel.tables.Merchandises;
import model.postgresqlModel.tables.merchandises.Aliens;
import model.postgresqlModel.tables.merchandises.Foods;
import model.postgresqlModel.tables.merchandises.Poisons;
import model.postgresqlModel.tables.merchandises.Slaves;
import org.apache.log4j.BasicConfigurator;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class PostgresModel implements Model {
    private Session session;
    private ResourceBundle errCodes = ResourceBundle.getBundle("errcodes");

    /**
     * Constructor with default credentials.
     * username: s3rius
     * password: 19216211
     */
    public PostgresModel() {
        BasicConfigurator.configure();
        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
    }

    /**
     * Constructor with custom credentials.
     *
     * @param username user's username.
     * @param password user's password.
     */
    public PostgresModel(String username, String password) {
        try {
            BasicConfigurator.configure();
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
        } catch (Throwable thr) {
            System.err.println("Can't establish connection to postgresql");
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
                    return object.toString();
                }).collect(toList());
    }

    @Override
    public String getMerchantById(int id) throws MerchandiseNotFoundException {
        try {
            Merchandises merch = (Merchandises) session.createQuery("from Merchandises where id = :id")
                    .setParameter("id", id).getSingleResult();
            return merch.getAllInfo();
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
            return merch;
        } catch (JDBCException e) {
            transaction.rollback();
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
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
        } catch (JDBCException e) {
            session.getTransaction().rollback();
            throw new MerchandiseUpdateException(getMessageByCode(e));
        } catch (Exception e) {
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
        } catch (Throwable e) {
            transaction.rollback();
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
            return token;
        } catch (JDBCException e) {
            transaction.rollback();
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
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
            return true;
        } catch (JDBCException e) {
            transaction.rollback();
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
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
        } catch (JDBCException e) {
            transaction.rollback();
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            transaction.rollback();
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
            return mDeal.toString();
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
            return updated;
        } catch (JDBCException e) {
            transaction.rollback();
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            transaction.rollback();
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
        } catch (JDBCException e) {
            transaction.rollback();
            throw new UserException(getMessageByCode(e));
        } catch (Exception e) {
            transaction.rollback();
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

        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }
        JsonExportObject root = new JsonExportObject();
        try {
            List<Users> users = session.createQuery("from Users ").getResultList();
            List<Deals> deals = session.createQuery("from deals order by id asc ").getResultList();

            List<Slaves> slaves = session.createQuery("from Slaves order by id  asc ").getResultList();
            List<Aliens> aliens = session.createQuery("from Aliens order by id  asc ").getResultList();
            List<Poisons> poisons = session.createQuery("from Poisons order by id  asc ").getResultList();
            List<Foods> foods = session.createQuery("from Foods order by id  asc ").getResultList();

            root.setUsers(users);
            root.setDeals(deals);

            root.setSlaves(slaves);
            root.setAliens(aliens);
            root.setPoisons(poisons);
            root.setFoods(foods);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(fileName)) {
                gson.toJson(root, writer);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean importAllData(String filename) {
        if (!filename.endsWith(".json")) {
            filename += ".json";
        }
        try (JsonReader reader = new JsonReader(new FileReader(filename))) {
            Gson gson = new Gson();
            JsonExportObject root = gson.fromJson(reader, JsonExportObject.class);
            root.getUsers().forEach(users -> {
                int oldId = users.getId();
                System.out.println(users.getUsername());
                String pass = users.getPassword();
                session.beginTransaction();
                try {
                    users.setPassword((String) session.createNativeQuery(
                            "SELECT CONVERT_FROM(DECODE(:pass, 'BASE64'), 'UTF-8')"
                    ).setParameter("pass", users.getPassword()).getSingleResult());
                    session.save(users);
                    session.getTransaction().commit();
                    setNewUserId(root.getDeals(), oldId, users.getId());
                } catch (JDBCException e) {
                    session.getTransaction().rollback();
                    // If user already exists
                    // Error code U0001 -> user already in database
                    if (e.getSQLException().getSQLState().equals("U0001")) {
                        Users exUsers = (Users) session.createQuery("from Users where username=:un and password=:pss")
                                .setParameter("un", users.getUsername())
                                .setParameter("pss", pass)
                                .getSingleResult();
                        setNewUserId(root.getDeals(), users.getId(), exUsers.getId());
                    }
                    System.out.println(getMessageByCode(e));
                }
            });
            List<Aliens> aliens = root.getAliens();

            aliens.stream().sorted(Comparator.comparingInt(Aliens::getId).reversed()).forEach(alien -> {
                int id = alien.getId();
                session.save(alien);
                setMerchId(root.getDeals(), id, alien.getId());
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
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

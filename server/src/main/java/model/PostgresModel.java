package model;


import exceptions.*;
import model.database.DealState;
import model.merchandises.Merchandise;
import model.postgresqlModel.Users;
import model.postgresqlModel.tables.Classes;
import model.postgresqlModel.tables.Deals;
import model.postgresqlModel.tables.Merchandises;
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
import java.sql.SQLException;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class PostgresModel implements Model {
    private Session session;
    private ResourceBundle errCodes = ResourceBundle.getBundle("errcodes");
    private StoredProcedureQuery login;
    private StoredProcedureQuery logout;
    private StoredProcedureQuery addMerch;

    /**
     * Constructor with default credentials.
     * username: s3rius
     * password: 19216211
     */
    public PostgresModel() {
        BasicConfigurator.configure();
        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
        initProcedures();
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
            initProcedures();
        } catch (Throwable thr) {
            System.err.println("Can't establish connection to postgresql");
            throw new ExceptionInInitializerError(thr);
        }
    }

    private void initProcedures() {
        login = session.createStoredProcedureQuery("login")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT);
        logout = session.createStoredProcedureQuery("logout")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, Boolean.class, ParameterMode.OUT);
        addMerch = session.createStoredProcedureQuery("addByMap")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN);
    }


    @Override
    public void addMerchandise(Merchandise merch, String user, String token, int price) {

    }

    @Override
    public void removeMerchandise(Merchandise merch, String user, String token) {

    }

    @Override
    public void removeMerchandise(int id, String user, String token) throws MerchandiseAlreadyBought {

    }

    @Override
    public List<String> searchMerchandise(String query) throws WrongQueryException {
        Query querySet = session.createNativeQuery("SELECT * FROM searchMerchandise(:query)")
                .addEntity(Merchandises.class).setParameter("query", query);
        return ((List<Merchandises>) querySet.getResultList())
                .stream().map(Merchandises::toString).collect(toList());
    }

    @Override
    public String getMerchantById(int id) throws MerchandiseNotFoundException {
        try {
            Merchandises merch = (Merchandises) session.createQuery("from Merchandises where id = :id")
                    .setParameter("id", id).getSingleResult();
            return merch.toString();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException {
        return null;
    }

    @Override
    public void setValuesToMerchandise(int id, String params, String user, String token) {

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
        System.out.println(className);
        System.out.println(keys.substring(0, keys.length() - 2));
        System.out.println(vals.substring(0, vals.length() - 2));
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
        try {
            login.setParameter(1, username);
            login.setParameter(2, password);
            Transaction transaction = session.beginTransaction();
            String token = login.getOutputParameterValue(3).toString();
            transaction.commit();
            return token;
        } catch (JDBCException e) {
            throw new UserException(getMessageByCode(e));
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
        }
    }

    private String getMessageByCode(JDBCException e) {
        return errCodes.getString(e.getSQLException().getSQLState());
    }

    @Override
    public void disconnect(String username, String token) {
        try {
            logout.setParameter(1, username).setParameter(2, token);
            Transaction transaction = session.beginTransaction();
            logout.getOutputParameterValue(3);
            transaction.commit();
        } catch (JDBCException e) {
            throw new UserException(getMessageByCode(e));
        }
    }

    @Override
    public List<String> getDealsByUser(String username, String token) {
        List<Deals> dealsList = session.createQuery("from deals where userId=(select id from Users where username like :un AND token like :tkn)")
                .setParameter("un", username)
                .setParameter("tkn", token).getResultList();
        dealsList.stream().map(deal -> {
            return "";
        }).collect(toList());
        return null;
    }

    @Override
    public boolean changeLogin(String username, String newLogin, String token) {
        return false;
    }

    @Override
    public void changePassword(String username, String newPassword, String token) {

    }

    @Override
    public String getDealById(int id) {
        Deals deals = (Deals) session.createQuery("from deals where id = :id")
                .setParameter("id", id).getSingleResult();
        System.out.println(deals);
        return null;
    }

    @Override
    public boolean exportAllData(String fileName) {
        return false;
    }

    @Override
    public boolean importAllData(String filename) {
        return false;
    }
}

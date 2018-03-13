package model;


import exceptions.CreateMerchandiseException;
import exceptions.MerchandiseAlreadyBought;
import exceptions.MerchandiseNotFoundException;
import exceptions.WrongQueryException;
import model.merchandises.Merchandise;
import model.postgresqlModel.Users;
import org.apache.log4j.BasicConfigurator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.Map;

public class PostgresModel implements Model {
    private Session session;
    private StoredProcedureQuery login;
    private StoredProcedureQuery logout;

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
        BasicConfigurator.configure();
        Configuration hibConfiguration = new Configuration();
        hibConfiguration.setProperty("connection.driver_class", "org.postgresql.Driver");
        hibConfiguration.setProperty("connection.url", "jdbc:postgresql://localhost/slaveMarket");
        hibConfiguration.setProperty("connection.username", username);
        hibConfiguration.setProperty("connection.password", password);
        hibConfiguration.setProperty("dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
        hibConfiguration.setProperty("current_session_context_class",
                "org.hibernate.context.internal.ThreadLocalSessionContext");
//        hibConfiguration.addClass(Users.class);
        session = hibConfiguration.buildSessionFactory().openSession();
        initProcedures();
    }

    private void initProcedures() {
//        login = session.createStoredProcedureQuery("login")
//                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
//                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
//                .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT);
//        logout = session.createNamedStoredProcedureQuery("logout")
//                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
//                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
//                .registerStoredProcedureParameter(3, Boolean.class, ParameterMode.OUT);
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
        return null;
    }

    @Override
    public String getMerchantById(int id) throws MerchandiseNotFoundException {
        return null;
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
        return null;
    }

    @Override
    public List<String> getMandatoryFields(String className) {
        return null;
    }

    @Override
    public void addMerchandiseByMap(String className, Map<String, String> kvs, String user, String token, int price) throws CreateMerchandiseException {

    }

    @Override
    public String login(String username, String password) {
//        StoredProcedureQuery query = login;
//        Transaction transaction = session.beginTransaction();
//        query.setParameter(1, username)
//                .setParameter(2, password);
//        transaction.commit();
        StoredProcedureQuery query = session.createStoredProcedureQuery("login", Users.class);
        query.setParameter(1, username);
        query.setParameter(2, password);
        Transaction transaction = session.beginTransaction();
        String token = query.getOutputParameterValue("token").toString();
        transaction.commit();
        return token;
    }

    @Override
    public boolean register(String username, String pass) {
        return false;
    }

    @Override
    public void disconnect(String username, String token) {

    }

    @Override
    public List<String> getDealsByUser(String username, String token) {
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

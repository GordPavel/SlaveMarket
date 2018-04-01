package controllers;

import exceptions.*;
import model.merchandises.Merchandise;

import java.util.List;
import java.util.Map;

/**
 * Class to provide model functions to user.
 */
public interface Controller {

  /**
   * Add slave in model.
   *
   * @param merch Slave to add
   * @param user  user who performed action
   */
  void addMerchant(Merchandise merch, String user, String token, int price);

  /**
   * tells to model for remove the slave.
   *
   * @param merch slave to remove
   * @param user  user who performed action
   */
  void removeMerchant(Merchandise merch, String user, String token);

  /**
   * tells to model for remove the slave by id.
   *
   * @param id   slaves's id to remove the slave by it.
   * @param user user who performed action
   */
  void removeMerchant(int id, String user, String token) throws MerchandiseAlreadyBought;

  /**
   * tells to model for search  the slave by the query.
   *
   * @param query query for search
   * @return list of founded slaves
   */
  List<String> searchMerchant(String query) throws WrongQueryException;

  /**
   * Returns merchandise by id or exception.
   *
   * @param id id of Merchandise
   * @return Founded merchandise or Exception
   */
  String getMerchantById(int id) throws MerchandiseNotFoundException;

  /**
   * Marks merchandise as bought.
   *
   * @param id   unique merchandise identity
   * @param user user who performed action
   * @return bought merchandise
   * @throws MerchandiseNotFoundException throws if merchandise can not be found
   */
  String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException;

  /**
   * Set new values  to merchandise.
   *
   * @param id     id of merchandise to be changed
   * @param params String of parameters with values to change
   * @param user   user who performed action
   */
  void setValuesToMerchandise(int id, String params, String user, String token);


  /**
   * Method for getting available types of merchandises.
   *
   * @return list of available types for creation
   */
  List<String> getAvailableClasses();

  /**
   * Returns minimum of required fields for create an object of chosen class.
   * To see all available tables call {@link #getAvailableClasses()} Method
   *
   * @return field names
   */
  List<String> getMandatoryFields(String className);

  /**
   * Method to add new Merchandise item.
   *
   * @param className merchandise's type
   * @param kvs       map with params.
   *                  To get valid keys call {@link #getMandatoryFields(String className)}
   * @param user      current user
   * @param token     current user's token
   * @throws CreateMerchandiseException throws if can't create merchandise by map.
   */
  void addMerchandiseByMap(
          String className,
          Map<String, String> kvs,
          String user, String token,
          int price)
          throws CreateMerchandiseException;

  /**
   * Method to connect to shop.
   *
   * @param username username
   * @param password password
   * @return new created token
   */
  String login(String username, String password);

  /**
   * Method to create and add new user.
   *
   * @param username new profile username
   * @param pass     new password
   * @return true if registration complete, false if registration failed.
   */
  boolean register(String username, String pass);

  /**
   * Method to disconnect user and reset token.
   *
   * @param username user's login
   * @param token    user's current token
   */
  void disconnect(String username, String token);

  /**
   * Start point of controller.
   * Initialize all important resources.
   */
  void start();

  /**
   * Method to see all of user's deals.
   *
   * @param username user, that make deals.
   * @return list of deals strings.
   * @throws InvalidToken if user token is invalid.
   */
  List<String> getDealsByUser(String username, String token);


  /**
   * Method to change user's username.
   *
   * @param username current username
   * @param newLogin new username
   * @param token    current token
   * @return true if login successfully changed
   */
  boolean changeLogin(String username, String newLogin, String token);

  /**
   * Method to change user's password.
   *
   * @param username    user who want to change password.
   * @param newPassword new user's password.
   * @param token       current user's token.
   */
  void changePassword(String username, String newPassword, String token);

  /**
   * Method to get deal specified by id.
   *
   * @param id deal's id.
   * @return deal in json format string
   */
  String getDealById(int id);


  /**
   * Method to export all database into xml file.
   *
   * @param fileName file to export
   * @return true if export successful
   */
  boolean exportAllData(String fileName);

  /**
   * Method to import data from xml file.
   *
   * @param filename file to import
   * @return true if import successful
   */
  boolean importAllData(String filename);

  /**
   * Method to check server availability.
   *
   * @param address server's address.
   * @param port    server's port.
   * @return true if server available.
   */
  boolean ping(String address, int port);

    /**
     * Method to set role to user
     *
     * @param username admin's username
     * @param token    admin's current token
     * @param id       user admin want to change
     * @param role     new user's role
     */
    void setRole(String username, String token, int id, String role);

    /**
     * Method to get list of available permission roles
     *
     * @return list of all roles
     */
    List<String> availableRoles();

    /**
     * Method to get list of users
     *
     * @return list of json objects
     */
    List<String> getAllUsers();
}

package ru.cracker.model;

import ru.cracker.exceptions.CreateMerchandiseException;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongClassCallException;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.Merchandise;

import java.util.List;
import java.util.Map;

/**
 * Provides interface to manage slaves.
 */
public interface Model {


  /**
   * Adding slave in database or something like that.
   *
   * @param merch Slave to add
   * @param user  user who performed action
   * @param price merchandise's price
   */
  void addMerchandise(Merchandise merch, String user, String token, int price);

  /**
   * removes slave out our collection.
   *
   * @param user user who performed action
   */
  void removeMerchandise(Merchandise merch, String user, String token);

  /**
   * removes slave out our collection using only unique id.
   *
   * @param user user who performed action
   */
  void removeMerchandise(int id, String user, String token);

  /**
   * Search slave by the string  query like "height>150 productivity>40 weight<90 age=22".
   *
   * @param query query string
   * @return list of founed slaves
   */
  List<String> searchMerchandise(String query) throws WrongQueryException;

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
   * To see all available classes call {@link #getAvailableClasses()} Method
   *
   * @return field names
   */
  List<String> getMandatoryFields(String className) throws WrongClassCallException;

  /**
   * Method to add new Merchandise item
   *
   * @param className merchandise's type
   * @param kvs       map with params. To get valid keys call {@link #getMandatoryFields(String className)}
   * @param user      current user
   * @param token     current user's token
   * @param price     merchandise's price
   * @throws CreateMerchandiseException
   */
  void addMerchandiseByMap(String className, Map<String, String> kvs, String user, String token, int price)
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
   * Method to see all of user's deals
   *
   * @param username user, that make deals.
   * @return list of deals strings.
   */
  List<String> getDealsByUser(String username, String token);
}
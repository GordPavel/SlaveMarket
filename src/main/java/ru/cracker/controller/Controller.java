package ru.cracker.controller;

import ru.cracker.exceptions.CreateMerchandiseException;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongClassCallException;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.Merchandise;

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
  void removeMerchant(int id, String user, String token);

  /**
   * tells to model for search  the slave by the query.
   *
   * @param querry querry for search
   * @return list of founded slaves
   */
  List<String> searchMerchant(String querry) throws WrongQueryException;

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
   * To see all available classes call {@link #getAvailableClasses()} Method
   *
   * @return field names
   */
  List<String> getMandatoryFields(String className) throws WrongClassCallException;

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

  void start();

  /**
   * Method to see all of user's deals
   *
   * @param username user, that make deals.
   * @return list of deals strings.
   * @throws ru.cracker.exceptions.InvalidToken if user token is invalid.
   */
  List<String> getDealsByUser(String username, String token);
}

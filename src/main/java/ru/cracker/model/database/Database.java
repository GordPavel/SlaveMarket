package ru.cracker.model.database;

import ru.cracker.exceptions.CreateMerchandiseException;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongClassCallException;
import ru.cracker.model.merchandises.Merchandise;

import java.util.List;
import java.util.Map;

/**
 * Database interface to manage the data adn provide it to view through controllers.
 */
public interface Database {


  /**
   * Puts merch into the vault.
   *
   * @param merch Merch to put in vault
   * @param user  user who performed action
   */
  void addMerchandise(Merchandise merch, String user, String token, int price);

  /**
   * Removes merchandise from database.
   *
   * @param merch Removes merchandise from vault
   * @param user  user who performed action
   */
  void removeMerchandise(Merchandise merch, String user, String token);

  /**
   * remove merchandise from vault by id.
   *
   * @param user user who performed action
   */
  void removeMerchandise(int id, String user, String token);

  /**
   * Method to find specified Merchandises.
   *
   * @param querry querry to filter results
   * @return List of Merchandises specified by query
   */
  List<String> searchMerchandise(String querry);

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
   * @param user   user who performed action
   * @param params String of parameters with values to change
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
   * @param price     merchandise's price
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
   * @throws ru.cracker.exceptions.InvalidToken if user token is invalid.
   */
  void disconnect(String username, String token);

  /**
   * Method to see all of user's deals.
   *
   * @param username user, that make deals.
   * @return list of deals strings.
   * @throws ru.cracker.exceptions.InvalidToken if user token is invalid.
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
}
package ru.cracker.model.database;

import java.util.List;
import java.util.Map;
import ru.cracker.exceptions.CreateMerchandiseException;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongClassCallException;
import ru.cracker.model.merchandises.Merchandise;

/**
 * Database interface to manage the data adn provide it to view through controller.
 */
public interface Database {


  /**
   * Puts merch into the vault.
   *
   * @param merch Merch to put in vault
   * @param user user who performed action
   */
  void addMerchandise(Merchandise merch, String user);

  /**
   * Removes merchandise from database.
   *
   * @param merch Removes merchandise from vault
   * @param user user who performed action
   */
  void removeMerchandise(Merchandise merch, String user);

  /**
   * remove merchandise from vault by id.
   *
   * @param user user who performed action
   */
  void removeMerchandise(int id, String user);

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
   * @param id unique merchandise identity
   * @param user user who performed action
   * @return bought merchandise
   * @throws MerchandiseNotFoundException throws if merchandise can not be found
   */
  String buyMerchandise(int id, String user) throws MerchandiseNotFoundException;

  /**
   * Set new values  to merchandise.
   *
   * @param id id of merchandise to be changed
   * @param user user who performed action
   * @param params String of parameters with values to change
   */
  void setValuesToMerchandise(int id, String params, String user);

  /**
   * Method for getting available types of merchandises.
   *
   * @return list of available types for creation
   */
  List<String> getAvailableClasses();

  /**
   * Returns minimum of required fields for create an object of chosen class.
   *
   * @return field names
   */
  List<String> getMandatoryFields(String className) throws WrongClassCallException;

  void addMerchandiseByMap(String className, Map<String, String> kvs, String user)
      throws CreateMerchandiseException;
}
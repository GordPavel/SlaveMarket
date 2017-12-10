package ru.cracker.model;

import java.util.List;
import java.util.Map;
import ru.cracker.exceptions.CreateMerchandiseException;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongClassCallException;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.Merchandise;

/**
 * Provides interface to manage slaves.
 */
public interface Model {


  /**
   * Adding slave in database or something like that.
   *
   * @param merch Slave to add
   * @param user user who performed action
   */
  void addMerchandise(Merchandise merch, String user);

  /**
   * removes slave out our collection.
   *
   * @param user user who performed action
   */
  void removeMerchandise(Merchandise merch, String user);

  /**
   * removes slave out our collection using only unique id.
   *
   * @param user user who performed action
   */
  void removeMerchandise(int id, String user);

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
   * @param id unique merchandise identity
   * @param user user who performed action
   * @return bought merchandise
   */
  String buyMerchandise(int id, String user) throws MerchandiseNotFoundException;

  /**
   * Set new values  to merchandise.
   *
   * @param id id of merchandise to be changed
   * @param params String of parameters with values to change
   * @param user user who performed action
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
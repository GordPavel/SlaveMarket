package ru.cracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ru.cracker.exceptions.CreateMerchandiseException;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongClassCallException;
import ru.cracker.model.database.Database;
import ru.cracker.model.database.MerchDb;
import ru.cracker.model.merchandises.Merchandise;
import ru.cracker.view.Observer;

/**
 * Realization of Model and Observable.
 */
public class SlaveMarketModel implements Observable, Model {

  private Database database;
  private List<Observer> observers;

  /**
   * Default constructor.
   */
  public SlaveMarketModel() {
    database = new MerchDb(true);
    observers = new ArrayList<Observer>();
  }

  /**
   * Subscribes observer to notifications.
   *
   * @param observer observer to add.
   */
  public void addObserver(Observer observer) {
    observers.add(observer);
  }

  /**
   * Triggers all available observers to do something.
   */
  public void notifyAllObservers() {
    observers.parallelStream().forEach(Observer::update);
  }

  /**
   * Notifies observers that data with that id has changed.
   *
   * @param id id of changed data
   */
  public void changed(int id) {
    observers.parallelStream().forEach(i -> i.changed(id));

  }

  /**
   * Notifies observer s that data with that id has deleted.
   *
   * @param id id of deleted data
   */
  public void deleted(int id) {
    observers.parallelStream().forEach(i -> i.deleted(id));
  }

  /**
   * unsubscribes observer from notifies.
   *
   * @param observer observer to delete
   */
  public void deleteObserver(Observer observer) {
    observers.remove(observer);
  }

  /**
   * Adding slave in database or something like that.
   *
   * @param merch Slave to add
   * @param user user who performed action
   */
  public void addMerchandise(Merchandise merch, String user) {
    database.addMerchandise(merch, user);
    notifyAllObservers();
  }

  /**
   * removes slave out our collection.
   *
   * @param merch merch to remove
   * @param user user who performed action
   */
  public void removeMerchandise(Merchandise merch, String user) {
    int id = observers.indexOf(merch);
    observers.remove(id);
    deleted(id);
  }

  /**
   * removes slave out our collection using only unique id.
   *
   * @param id id of merch to remove
   * @param user user who performed action
   */
  public void removeMerchandise(int id, String user) {
    database.removeMerchandise(id, user);
    deleted(id);
  }

  /**
   * Search slave by the string  query like "height>=150 and productivity=40 and weight<90 and
   * age=22".
   *
   * @param query query string
   * @return list of founed slaves
   */
  public List<String> searchMerchandise(String query) {
    return database.searchMerchandise(query);
  }

  /**
   * Returns merchandise by id or exception.
   *
   * @param id id of Merchandise
   * @return Founded merchandise or Exception
   */
  @Override
  public String getMerchantById(int id) throws MerchandiseNotFoundException {
    return database.getMerchantById(id);
  }

  /**
   * Marks merchandise as bought.
   *
   * @param id unique merchandise identity
   * @param user user who performed action
   * @return bought merchandise
   */
  @Override
  public String buyMerchandise(int id, String user) throws MerchandiseNotFoundException {
    return database.buyMerchandise(id, user);
  }

  /**
   * Set new values  to merchandise.
   *
   * @param id id of merchandise to be changed
   * @param user user who performed action
   * @param params String of parameters with values to change
   */
  public void setValuesToMerchandise(int id, String params, String user) {
    database.setValuesToMerchandise(id, params, user);
    changed(id);
  }

  /**
   * Method for getting available types of merchandises.
   *
   * @return list of available types for creation
   */
  @Override
  public List<String> getAvailableClasses() {
    return database.getAvailableClasses();
  }

  @Override
  public List<String> getMandatoryFields(String className) throws WrongClassCallException {
    return database.getMandatoryFields(className);
  }

  @Override
  public void addMerchandiseByMap(String className, Map<String, String> kvs, String user)
      throws CreateMerchandiseException {
    database.addMerchandiseByMap(className, kvs, user);
  }
}

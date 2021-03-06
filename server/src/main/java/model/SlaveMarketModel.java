package model;

import exceptions.CreateMerchandiseException;
import exceptions.MerchandiseAlreadyBought;
import exceptions.MerchandiseNotFoundException;
import model.database.Database;
import model.database.MerchDb;
import model.merchandises.Merchandise;
import view.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Realization of Model and Observable.
 */
public class SlaveMarketModel implements Observable, Model {

  private final Database database;
  private final List<Observer> observers;

  /**
   * Default constructor.
   */
  public SlaveMarketModel() {
    database = new MerchDb(true);
    observers = new ArrayList<>();
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
   * @param user  user who performed action
   * @param price merchandise's price
   */
  public void addMerchandise(Merchandise merch, String user, String token, int price) {
    database.addMerchandise(merch, user, token, price);
    notifyAllObservers();
  }

  /**
   * removes slave out our collection.
   *
   * @param merch merch to remove
   * @param user  user who performed action
   */
  public void removeMerchandise(Merchandise merch, String user, String token) {
    database.removeMerchandise(merch, user, token);
    deleted(merch.getId());
  }

  /**
   * removes slave out our collection using only unique id.
   *
   * @param id   id of merch to remove
   * @param user user who performed action
   */
  public void removeMerchandise(int id, String user, String token) throws MerchandiseAlreadyBought {
    database.removeMerchandise(id, user, token);
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
   * @param id   unique merchandise identity
   * @param user user who performed action
   * @return bought merchandise
   */
  @Override
  public String buyMerchandise(int id, String user, String token) throws MerchandiseNotFoundException {
    return database.buyMerchandise(id, user, token);
  }

  /**
   * Set new values  to merchandise.
   *
   * @param id     id of merchandise to be changed
   * @param user   user who performed action
   * @param params String of parameters with values to change
   */
  public void setValuesToMerchandise(int id, String params, String user, String token) {
    database.setValuesToMerchandise(id, params, user, token);
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
  public List<String> getMandatoryFields(String className) {
    return database.getMandatoryFields(className);
  }

  @Override
  public void addMerchandiseByMap(String className, Map<String, String> kvs, String user, String token, int price)
          throws CreateMerchandiseException {
    database.addMerchandiseByMap(className, kvs, user, token, price);
  }

  @Override
  public String login(String username, String password) {
    return database.login(username, password);
  }

  @Override
  public boolean register(String username, String pass) {
    return database.register(username, pass);
  }

  @Override
  public void disconnect(String username, String token) {
    database.disconnect(username, token);
  }

  @Override
  public List<String> getDealsByUser(String username, String token) {
    return database.getDealsByUser(username, token);
  }

  @Override
  public boolean changeLogin(String username, String newLogin, String token) {
    return database.changeLogin(username, newLogin, token);
  }

  @Override
  public void changePassword(String username, String newPassword, String token) {
    database.changePassword(username, newPassword, token);
  }

  @Override
  public String getDealById(int id) {
    return database.getDealById(id);
  }

  @Override
  public boolean exportAllData(String fileName) {
    return database.exportAllData(fileName);
  }

  @Override
  public boolean importAllData(String filename) {
    return database.importAllData(filename);
  }
}

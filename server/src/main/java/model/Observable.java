package model;

import view.Observer;

/**
 * Class to send notifications to Observers.
 */
public interface Observable {

  /**
   * Subscribes observer to notifications.
   *
   * @param observer observer to add.
   */
  void addObserver(Observer observer);

  /**
   * Triggers all available observers to do something.
   */
  void notifyAllObservers();

  /**
   * Notifies observers that data with that id has changed.
   *
   * @param id id of changed data
   */
  void changed(int id);

  /**
   * Notifies observer s that data with that id has deleted.
   *
   * @param id id of deleted data
   */
  void deleted(int id);

  /**
   * unsubscribes observer from notifies.
   *
   * @param observer observer to delete
   */
  void deleteObserver(Observer observer);

}
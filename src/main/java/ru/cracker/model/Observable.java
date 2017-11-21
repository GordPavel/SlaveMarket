package ru.cracker.model;


import ru.cracker.view.Observer;

/**
 *
 */
public interface Observable {

    /**
     * Subscribes observer to notifications
     *
     * @param observer observer to add.
     */
    public void addObserver(Observer observer);

    /**
     * Triggers all available observers to do something
     */
    public void notifyAllObservers();

    /**
     * Notifies observers that data with that id has changed
     *
     * @param id id of changed data
     */
    public void changed(int id);

    /**
     * Notifies observer s that data with that id has deleted
     *
     * @param id id of deleted data
     */
    public void deleted(int id);

    /**
     * unsubscribes observer from notifies.
     *
     * @param observer observer to delete
     */
    public void deleteObserver(Observer observer);

}
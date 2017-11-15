package ru.cracker.Model;

import ru.cracker.Model.database.Database;
import ru.cracker.Model.database.MerchDb;
import ru.cracker.Model.merchandises.Merchandise;
import ru.cracker.exceptions.AlreadyBoughtException;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.view.Observer;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class SlaveMarketModel implements Observable, Model {

    private Database database;
    private List<Observer> observers;

    /**
     * Default constructor
     */
    public SlaveMarketModel() {
        database = new MerchDb();
        observers = new ArrayList<>();
    }

    /**
     * Subscribes observer to notifications
     *
     * @param observer observer to add.
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Triggers all available observers to do something
     */
    public void notifyAllObservers() {
        observers.parallelStream().forEach(Observer::update);
    }

    /**
     * Notifies observers that data with that id has changed
     *
     * @param id id of changed data
     */
    public void changed(int id) {
        observers.parallelStream().forEach(i -> i.changed(id));

    }

    /**
     * Notifies observer s that data with that id has deleted
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
     * Adding slave in database or something like that
     *
     * @param merch Slave to add
     */
    public void addMerchandise(Merchandise merch) {
        database.addMerchandise(merch);
        notifyAllObservers();
    }

    /**
     * removes slave out our collection
     *
     * @param merch merch to remove
     */
    public void removeMerchandise(Merchandise merch) {
        int id = observers.indexOf(merch);
        observers.remove(id);
        deleted(id);
    }

    /**
     * removes slave out our collection using only unique id
     *
     * @param id id of merch to remove
     */
    public void removeMerchandise(int id) {
        database.removeMerchandise(id);
        deleted(id);
    }

    /**
     * Search slave by the string  query
     * like "height>=150 and productivity=40 and weight<90 and age=22"
     *
     * @param query query string
     * @return list of founed slaves
     */
    public List<Merchandise> searchMerchandise(String query) {
        return database.searchMerchandise(query);
    }

    /**
     * Returns merchandise by id or exception
     *
     * @param id id of Merchandise
     * @return Founded merchandise or Exception
     */
    @Override
    public Merchandise getMerchantById(int id) throws MerchandiseNotFoundException {
        return database.getMerchantById(id);
    }

    /**
     * Marks merchandise as bought
     *
     * @param id unique merchandise identity
     * @return bought merchandise
     * @throws MerchandiseNotFoundException
     */
    @Override
    public Merchandise buyMerchandise(int id) throws MerchandiseNotFoundException, AlreadyBoughtException {
        Merchandise merchandise = database.buyMerchandise(id);
        changed(id);
        return merchandise;
    }

}
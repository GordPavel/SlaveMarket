package ru.cracker.model;

import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.Merchandise;

import java.util.List;

/**
 * Provides interface to manage slaves
 */
public interface Model {


    /**
     * Adding slave in database or something like that
     *
     * @param merch Slave to add
     * @param user  user who performed action
     * @return
     */
    public void addMerchandise(Merchandise merch, String user);

    /**
     * removes slave out our collection
     *
     * @param merch
     * @param user  user who performed action
     * @return
     */
    public void removeMerchandise(Merchandise merch, String user);

    /**
     * removes slave out our collection using only unique id
     *
     * @param id
     * @param user user who performed action
     * @return
     */
    public void removeMerchandise(int id, String user);

    /**
     * Search slave by the string  query
     * like "height>150 productivity>40 weight<90 age=22"
     *
     * @param query query string
     * @return list of founed slaves
     */
    public List<Merchandise> searchMerchandise(String query) throws WrongQueryException;

    /**
     * Returns merchandise by id or exception
     *
     * @param id id of Merchandise
     * @return Founded merchandise or Exception
     */
    public Merchandise getMerchantById(int id) throws MerchandiseNotFoundException;

    /**
     * Marks merchandise as bought
     *
     * @param id   unique merchandise identity
     * @param user user who performed action
     * @return bought merchandise
     * @throws MerchandiseNotFoundException
     */
    public Merchandise buyMerchandise(int id, String user) throws MerchandiseNotFoundException;

    /**
     * Set new values  to merchandise.
     *
     * @param id     id of merchandise to be changed
     * @param params String of parameters with values to change
     * @param user   user who performed action
     */
    public void setValuesToMerchandise(int id, String params, String user);
}
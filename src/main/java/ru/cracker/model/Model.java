package ru.cracker.model;

import ru.cracker.model.merchandises.Merchandise;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongQueryException;

import java.util.List;

/**
 * Provides interface to manage slaves
 */
public interface Model {


    /**
     * Adding slave in database or something like that
     *
     * @param merch Slave to add
     * @return
     */
    public void addMerchandise(Merchandise merch);

    /**
     * removes slave out our collection
     *
     * @param merch
     * @return
     */
    public void removeMerchandise(Merchandise merch);

    /**
     * removes slave out our collection using only unique id
     *
     * @param id
     * @return
     */
    public void removeMerchandise(int id);

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
     * @param id unique merchandise identity
     * @return bought merchandise
     * @throws MerchandiseNotFoundException
     */
    public Merchandise buyMerchandise(int id) throws MerchandiseNotFoundException;
}
package ru.cracker.controller;


import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.Merchandise;

import java.util.List;

/**
 *
 */
public interface Controller {

    /**
     * Add slave in model
     *
     * @param merch Slave to add
     * @param user  user who performed action
     * @return
     */
    public void addMerchant(Merchandise merch, String user);

    /**
     * tells to model for remove the slave
     *
     * @param merch slave to remove
     * @param user  user who performed action
     * @return
     */
    public void removeMerchant(Merchandise merch, String user);

    /**
     * tells to model for remove the slave by id
     *
     * @param id   slaves's id to remove the slave by it.
     * @param user user who performed action
     * @return
     */
    public void removeMerchant(int id, String user);

    /**
     * tells to model for search  the slave by the query
     *
     * @param querry querry for search
     * @return list of founded slaves
     */
    public List<Merchandise> searchMerchant(String querry) throws WrongQueryException;

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

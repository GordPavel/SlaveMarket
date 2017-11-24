package ru.cracker.model.database;

import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.model.merchandises.Merchandise;

import java.util.List;

/**
 *
 */
public interface Database {


    /**
     * Puts merch into the vault
     *
     * @param merch Merch to put in vault
     * @param user  user who performed action
     * @return void
     */
    public void addMerchandise(Merchandise merch, String user);

    /**
     * @param merch Removes merchandise from vault
     * @param user  user who performed action
     * @return void
     */
    public void removeMerchandise(Merchandise merch, String user);

    /**
     * remove merchandise from vault by id
     *
     * @param id
     * @param user user who performed action
     * @return
     */
    public void removeMerchandise(int id, String user);

    /**
     * Method to find specified Merchandises
     *
     * @param querry querry to filter results
     * @return List of Merchandises specified by query
     */
    public List<Merchandise> searchMerchandise(String querry);

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
     * @param user   user who performed action
     * @param params String of parameters with values to change
     */
    public void setValuesToMerchandise(int id, String params, String user);

}
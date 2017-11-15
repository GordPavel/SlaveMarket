package ru.cracker.Model.database;

import ru.cracker.Model.merchandises.Merchandise;
import ru.cracker.exceptions.AlreadyBoughtException;
import ru.cracker.exceptions.MerchandiseNotFoundException;

import java.util.List;

/**
 *
 */
public interface Database {


    /**
     * Puts merch into the vault
     *
     * @param merch Merch to put in vault
     * @return void
     */
    public void addMerchandise(Merchandise merch);

    /**
     * @param merch Removes merchandise from vault
     * @return void
     */
    public void removeMerchandise(Merchandise merch);

    /**
     * remove merchandise from vault by id
     *
     * @param id
     * @return
     */
    public void removeMerchandise(int id);

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
     * @param id unique merchandise identity
     * @return bought merchandise
     * @throws MerchandiseNotFoundException
     */
    public Merchandise buyMerchandise(int id) throws MerchandiseNotFoundException, AlreadyBoughtException;

}
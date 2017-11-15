package ru.cracker.Controller;

import ru.cracker.Model.merchandises.Merchandise;
import ru.cracker.exceptions.AlreadyBoughtException;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongQueryException;

import java.util.List;

/**
 *
 */
public interface Controller {

    /**
     * Add slave in model
     *
     * @param merch Slave to add
     * @return
     */
    public void addMerchant(Merchandise merch);

    /**
     * tells to model for remove the slave
     *
     * @param merch slave to remove
     * @return
     */
    public void removeMerchant(Merchandise merch);

    /**
     * tells to model for remove the slave by id
     *
     * @param id slaves's id to remove the slave by it.
     * @return
     */
    public void removeMerchant(int id);

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
     * @param id unique merchandise identity
     * @return bought merchandise
     * @throws MerchandiseNotFoundException
     */
    public Merchandise buyMerchandise(int id) throws MerchandiseNotFoundException, AlreadyBoughtException;

}
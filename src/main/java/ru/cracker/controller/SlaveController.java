package ru.cracker.controller;

import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongClassCallException;
import ru.cracker.model.Model;
import ru.cracker.model.merchandises.Merchandise;
import ru.cracker.view.View;
import ru.cracker.view.cli.CLView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SlaveController implements Controller {

    /**
     *
     */
    private Model model;

    /**
     *
     */
    private View view;

    /**
     * Initial constructor with model
     *
     * @param model model to manage.
     */
    public SlaveController(Model model) {
        this.model = model;
        view = new CLView(model, this);
        view.launch();
    }

    /**
     * Add slave in model
     *
     * @param merch Slave to add
     * @param user  user who performed action
     * @return
     */
    public void addMerchant(Merchandise merch, String user) {
        model.addMerchandise(merch, user);
    }

    /**
     * tells to model for remove the slave
     *
     * @param merch slave to remove
     * @param user  user who performed action
     * @return
     */
    public void removeMerchant(Merchandise merch, String user) {
        model.removeMerchandise(merch, user);
    }

    /**
     * tells to model for search  the slave by the query
     *
     * @param querry querry for search
     * @return list of founded slaves
     */
    public List<String> searchMerchant(String querry) {
        return model.searchMerchandise(querry);
    }

    /**
     * tells to model for remove the slave by id
     *
     * @param id   slaves's id to remove the slave by it.
     * @param user user who performed action
     * @return
     */
    public void removeMerchant(int id, String user) {
        model.removeMerchandise(id, user);
    }

    /**
     * Returns merchandise by id or exception
     *
     * @param id id of Merchandise
     * @return Founded merchandise or Exception
     */
    @Override
    public String  getMerchantById(int id) throws MerchandiseNotFoundException {
        return model.getMerchantById(id);
    }

    /**
     * Marks merchandise as bought
     *
     * @param id   unique merchandise identity
     * @param user user who performed action
     * @return bought merchandise
     * @throws MerchandiseNotFoundException throws if merchandise with that id is not found
     */
    @Override
    public String buyMerchandise(int id, String user) throws MerchandiseNotFoundException {
        return model.buyMerchandise(id, user);
    }

    /**
     * Set new values  to merchandise.
     *
     * @param id     id of merchandise to be changed
     * @param params String of parameters with values to change
     * @param user   user who performed action
     */
    public void setValuesToMerchandise(int id, String params, String user) {
        model.setValuesToMerchandise(id, params, user);
    }

    public List<String> getAvailableClasses(){
        return model.getAvailableClasses();
    }

    @Override
    public List<String> getMandatoryFields(String className) throws WrongClassCallException {
        return model.getMandatoryFields(className);
    }

    @Override
    public void addMerchantByMap(String className, Map<String, String> kvs, String user) {
        model.addMerchandiseByMap(className, kvs, user);
    }
}

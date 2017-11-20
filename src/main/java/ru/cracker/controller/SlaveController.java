package ru.cracker.controller;

import ru.cracker.model.Model;
import ru.cracker.model.merchandises.Merchandise;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.view.View;
import ru.cracker.view.cli.CLView;

import java.util.List;

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
     * @return
     */
    public void addMerchant(Merchandise merch) {
        model.addMerchandise(merch);
    }

    /**
     * tells to model for remove the slave
     *
     * @param merch slave to remove
     * @return
     */
    public void removeMerchant(Merchandise merch) {
        // TODO implement here
    }

    /**
     * tells to model for search  the slave by the query
     *
     * @param querry querry for search
     * @return list of founded slaves
     */
    public List<Merchandise> searchMerchant(String querry) {
        return model.searchMerchandise(querry);
    }

    /**
     * tells to model for remove the slave by id
     *
     * @param id slaves's id to remove the slave by it.
     * @return
     */
    public void removeMerchant(int id) {
        model.removeMerchandise(id);
    }

    /**
     * Returns merchandise by id or exception
     *
     * @param id id of Merchandise
     * @return Founded merchandise or Exception
     */
    @Override
    public Merchandise getMerchantById(int id) throws MerchandiseNotFoundException {
        return model.getMerchantById(id);
    }

    /**
     * Marks merchandise as bought
     *
     * @param id unique merchandise identity
     * @return bought merchandise
     * @throws MerchandiseNotFoundException
     */
    @Override
    public Merchandise buyMerchandise(int id) throws MerchandiseNotFoundException {
        return model.buyMerchandise(id);
    }

}

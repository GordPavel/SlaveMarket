package ru.cracker.view.cli;

import ru.cracker.Controller.Controller;
import ru.cracker.Model.Model;
import ru.cracker.Model.Observable;
import ru.cracker.Model.merchandises.Slave;
import ru.cracker.exceptions.AlreadyBoughtException;
import ru.cracker.view.Observer;
import ru.cracker.view.View;

import java.util.ArrayList;


/**
 *
 */
public class CLView implements Observer, View {


    private Controller controller;

    /**
     * Constructor to subscribe new view as observer . And link controller.
     *
     * @param model
     * @param controller
     */
    public CLView(Model model, Controller controller) {
        ((Observable) model).addObserver(this);
        this.controller = controller;

    }

    /**
     * Apply action performed after the trigger
     */
    public void update() {
        System.out.println("some updates");
    }

    /**
     * Update information of Merchandise with id.
     *
     * @param id id of changed element
     */
    public void deleted(int id) {
        System.out.println("deleted " + id);
    }

    /**
     * Update information of Merchandise with id.
     *
     * @param id id of changed element
     */
    public void changed(int id) {
        System.out.println("changed " + id);
    }

    /**
     * Launch the view or CLI
     */
    public void launch() {
        System.out.println("hello");
        ArrayList<Slave> slaves = new ArrayList<>();
        slaves.add(new Slave(140, 35, 12, "male", 0, "Pete", 100));
        slaves.add(new Slave(174, 44, 16, "female", 1, "Diana", 200));
        slaves.add(new Slave(185, 85, 20, "male", 2, "Luise", 300));
        slaves.forEach(controller::addMerchant);
        try {
            controller.buyMerchandise(1);
        } catch (AlreadyBoughtException e) {
            System.out.println("Already bought");
        }
        System.out.println(controller.searchMerchant("id>=0 and id!=2"));
    }


}
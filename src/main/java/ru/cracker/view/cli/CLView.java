package ru.cracker.view.cli;

import ru.cracker.Controller.Controller;
import ru.cracker.Model.Model;
import ru.cracker.Model.Observable;
import ru.cracker.view.Observer;
import ru.cracker.view.View;


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
        System.out.println("Program works good");
        System.out
                .println("That what i can find with query \"id<=10 and id!=4 and name=Julia and gender!=female\" in random generated data");
        System.out.println(controller.searchMerchant("id<=20 and id!=4 and name=Julia and gender!=female"));
    }


}
package ru.cracker.view.cli;

import ru.cracker.controller.Controller;
import ru.cracker.model.Model;
import ru.cracker.model.Observable;
import ru.cracker.model.merchandises.Slave;
import ru.cracker.view.Observer;
import ru.cracker.view.View;

import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;


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
        System.out.println("Updated merchandise");
    }

    /**
     * Update information of Merchandise with id.
     *
     * @param id id of changed element
     */
    public void deleted(int id) {
        System.out.println("deleted merchandise " + id);
    }

    /**
     * Update information of Merchandise with id.
     *
     * @param id id of changed element
     */
    public void changed(int id) {
        System.out.println("changed merchandise " + id);
    }

    /**
     * Launch the view or CLI
     */
    public void launch() {

    }


}
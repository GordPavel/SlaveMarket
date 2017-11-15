package ru.cracker.view.cli;

import ru.cracker.Controller.Controller;
import ru.cracker.Model.Model;
import ru.cracker.Model.Observable;
import ru.cracker.Model.merchandises.Slave;
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
        System.out.println("Program starts good");
        Scanner scanner = new Scanner(System.in);
        System.out.println("let's generate some slave input int how many slave you want to see in database");
        int slaveNumber = scanner.nextInt();
        scanner.nextLine();
        Random random = new Random();
        IntStream.range(0, slaveNumber).forEach(i ->
                controller.addMerchant(new Slave(random.nextInt(40) + 160, random.nextInt(60) + 50, random
                        .nextInt(40) + 23, random
                        .nextBoolean() ? "male" : "female", i, random.nextBoolean() ? "Brian" : random
                        .nextBoolean() ? "Julia" : random.nextBoolean() ? "Mark" : "Mary", random.nextInt(600) + 200)));
        System.out
                .println("That what i can find with query \"id<=10 and id!=4 and name=Julia and gender!=female\" in random generated data");
        System.out.println(controller.searchMerchant("id<=20 and id!=4 and name=Julia and gender!=female"));
        System.out
                .println("Try by yourself... Enter the query it can contains \"> or >= or < or <= or = or !=\" without spaces like \"id>23\" and delimiter is \" and \"");
        String query = scanner.nextLine();
        System.out.println(controller.searchMerchant(query));
        System.out.println("Cool and good");

    }


}
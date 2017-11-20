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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
      Scanner scanner = new Scanner(System.in);
    Pattern exit = Pattern.compile("(\\bEXIT\\b)([ ]*)([\\w]*)");
    Pattern search = Pattern
            .compile("^(\\bSEARCH\\b)( (([a-zA-Z]*[a-zA-Z0-9]*)(>=|<=|>|<|!=|=)([a-zA-Z0-9]+[.\\w]*)+)((\\b and \\b)(([a-zA-Z]*[a-zA-Z0-9]*)(>=|<=|>|<|!=|=)([a-zA-Z0-9]+[.\\w]*)))*| )");
    Pattern slaveMenu = Pattern.compile("(\\bSLAVE \\b)(\\d*)");
    Matcher exitMatcher;
    Matcher searchMatcher;
    Matcher slaveMenuMatcher;
    while (scanner.hasNext()) {
        String line = scanner.nextLine().toUpperCase();
        exitMatcher = exit.matcher(line);
        searchMatcher = search.matcher(line);
        slaveMenuMatcher = slaveMenu.matcher(line);
        if (exitMatcher.lookingAt()) {
            System.out.println("bye");
            System.exit(0);
        } else if (searchMatcher.lookingAt()) {
            if (searchMatcher.group(2).equals(" ")) {
                //todo controller.searchMerchandise(searchMatcher.group(2));
                System.out.println("search of all db performed");
            } else
                //todo controller.searchMerchandise(searchMatcher.group(2).trim());
                System.out.println("search of \"" + searchMatcher.group(2).trim() + "\" performed");
        } else if (slaveMenuMatcher.lookingAt()) {
            System.out.println("opened slave menu by id " + slaveMenuMatcher.group(2));
            openSlaveMenu(Integer.parseInt(slaveMenuMatcher.group(2)), scanner);
            System.out.println("backed into main menu");
        } else {
            System.out.println("Wrong command");
        }
    }
    }

    private static void openSlaveMenu(int slaveId, Scanner scanner) {
          System.out.println("You can do this that that and that with slave with id " + slaveId);
          Pattern delete = Pattern.compile("\\bDELETE\\b");
          Pattern set = Pattern.compile("(\\bSET \\b)([a-zA-Z]*=[a-z0-9A-Z]+)");
          Pattern exit = Pattern.compile("(\\bEXIT\\b)");
          Pattern help = Pattern.compile("(\\bHELP\\b)");
          Matcher deleteMatcher;
          Matcher setMatcher;
          Matcher exitMatcher;
          Matcher helpMatcher;
          while (scanner.hasNext()) {
              String action = scanner.nextLine().toUpperCase();
              deleteMatcher = delete.matcher(action);
              setMatcher = set.matcher(action);
              exitMatcher = exit.matcher(action);
              helpMatcher = help.matcher(action);
              if (deleteMatcher.lookingAt()) {
                  System.out.println("whoop whoop slave deleted");
                  return;
              } else if (exitMatcher.lookingAt()) {
                  return;
              } else if (setMatcher.lookingAt()) {
                  System.out.println("you try to set " + setMatcher.group(2) + " to slave with id=" + slaveId);
              } else if (helpMatcher.lookingAt()) {

              }
          }
}
}

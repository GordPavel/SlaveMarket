package ru.cracker.view.cli;

import ru.cracker.controller.Controller;
import ru.cracker.model.Model;
import ru.cracker.model.Observable;
import ru.cracker.view.Observer;
import ru.cracker.view.View;

import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.Merchandise;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import ru.cracker.model.merchandises.Merchandise;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 *
 */
public class CLView implements Observer, View {

  private ResourceBundle resources = ResourceBundle.getBundle("app");
  private Controller controller;

  /**
   * Constructor to subscribe new view as observer . And link controller.
   *
   * @param model      to register itself
   * @param controller to manage inputs
   */
  public CLView(Model model, Controller controller) {
    ((Observable) model).addObserver(this);
    this.controller = controller;

  }

  private void openSlaveMenu(int slaveId, Scanner scanner) {
    System.out.println("Opened slave\'s menu with id " + slaveId + "\nType \"help\" to learn basics");
    System.out.print(">");
    Pattern delete = Pattern.compile("\\bDELETE\\b");
    Pattern set = Pattern.compile("(\\bSET \\b)([a-zA-Z]+=[a-z0-9A-Z]+)");
    Pattern exit = Pattern.compile("(\\bEXIT\\b)");
    Pattern help = Pattern.compile("(\\bHELP\\b)");
    Pattern info = Pattern.compile("(\\bINFO\\b)");
    Pattern buy = Pattern.compile("(\\bBUY\\b)");
    Matcher deleteMatcher;
    Matcher setMatcher;
    Matcher exitMatcher;
    Matcher helpMatcher;
    Matcher infoMatcher;
    Matcher buyMatcher;
    while (scanner.hasNext()) {
      String action = scanner.nextLine().toUpperCase();
      deleteMatcher = delete.matcher(action);
      setMatcher = set.matcher(action);
      exitMatcher = exit.matcher(action);
      helpMatcher = help.matcher(action);
      infoMatcher = info.matcher(action);
      buyMatcher = buy.matcher(action);
      if (deleteMatcher.lookingAt()) {
        System.out.println("You try to delete slave but that operation is ");
        try {
          controller.removeMerchant(slaveId);
        } catch (UnsupportedOperationException e) {
          System.out.println(e.getMessage());
        }
        return;
      } else if (exitMatcher.lookingAt()) {
        return;
      } else if (setMatcher.lookingAt()) {
        System.out.println("you try to set " + setMatcher.group(2) + " to slave with id=" + slaveId);
        System.out.println("But that operation is not supported yet");
      } else if (helpMatcher.lookingAt()) {
        System.out.println(resources.getString("slaveMenuHelp"));
      } else if (infoMatcher.lookingAt()) {
        System.out.println("Slave's info :");
        try {
          System.out.println(controller.getMerchantById(slaveId).getAllInfo());
        } catch (UnsupportedOperationException e) {
          System.out.println(e.getMessage());
        }
      } else if (buyMatcher.lookingAt()) {
        try {
          controller.buyMerchandise(slaveId);
        } catch (UnsupportedOperationException e) {
          System.out.println(e.getMessage());
        }
      } else {
        System.out.println("Unknown command");
      }
      System.out.print(">");
    }
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
    System.out.println(resources.getString("welcomeBasics"));
    System.out.print(">");
    Scanner scanner = new Scanner(System.in);
    Pattern exit = Pattern.compile("(\\bEXIT\\b)([ ]*)([\\w]*)");
    Pattern search = Pattern.compile(
        "^(\\bSEARCH \\b)((([a-zA-Z]*[a-zA-Z0-9]*)(>=|<=|>|<|!=|=)([a-zA-Z0-9]+[.\\w]*)+)((\\b and \\b)(([a-zA-Z]*[a-zA-Z0-9]*)(>=|<=|>|<|!=|=)([a-zA-Z0-9]+[.\\w]*)))*)");
    Pattern slaveMenu = Pattern.compile("(\\bSLAVE \\b)(\\d*)");
    Pattern addMerchandise = Pattern.compile("(\\bADD \\b)([A-Z]+)(( (([a-zA-Z]*[a-zA-Z0-9]*)=([a-zA-Z0-9]+[.\\w]*)))+)");
    Pattern help = Pattern.compile("\\bHELP\\b");
    Matcher exitMatcher;
    Matcher searchMatcher;
    Matcher slaveMenuMatcher;
    Matcher helpMatcher;
    Matcher addMatcher;
    while (scanner.hasNext()) {
      String line = scanner.nextLine().toUpperCase();
      exitMatcher = exit.matcher(line);
      addMatcher = addMerchandise.matcher(line);
      searchMatcher = search.matcher(line);
      slaveMenuMatcher = slaveMenu.matcher(line);
      helpMatcher = help.matcher(line);
      if (exitMatcher.lookingAt()) {
        System.out.println("bye");
        System.exit(0);
      } else if (searchMatcher.lookingAt()) {
        if (searchMatcher.group(2).equals(" ")) {
          System.out.println("search of all db performed");
          try {
            controller.searchMerchant(searchMatcher.group(2));
          } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
          }
        } else {
          try {
            System.out.println("search of \"" + searchMatcher.group(2).trim() + "\" performed");
            controller.searchMerchant(searchMatcher.group(2).trim());
          } catch (UnsupportedOperationException | WrongQueryException e) {
            System.out.println(e.getMessage());
          }
        }
      } else if (slaveMenuMatcher.lookingAt()) {
        openSlaveMenu(Integer.parseInt(slaveMenuMatcher.group(2)), scanner);
        System.out.println("backed into main menu");
      } else if (helpMatcher.lookingAt()) {
        System.out.println(resources.getString("help"));
      } else if (addMatcher.lookingAt()) {
        String className = addMatcher.group(2);
        className = className.toLowerCase();
        className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
        try {
          Class merchandise = Class.forName("ru.cracker.model.merchandises.classes." + className);
          Map<String, String> kvs = Arrays.stream(addMatcher.group(3).trim().split(" ")).map(elem -> elem.split("="))
              .collect(Collectors.toMap(e -> e[0], e -> e[1]));
          Merchandise merch = (Merchandise) merchandise.getMethod("buildFromMap", kvs.getClass()).invoke(null, kvs);
          System.out.println(merch);
        } catch (ClassNotFoundException e) {
          System.out.println("Can not find that Type of merchandise");
        } catch (IllegalAccessException |  NoSuchMethodException e) {
          System.out.println("Error while building");
        }catch (WrongQueryException e){
          System.out.println(e.getMessage());
        }catch (InvocationTargetException e){
          System.out.println(e.getCause().getMessage());
        }
      } else {
        System.out.println("Unknown command");
      }
      System.out.print(">");
    }
  }
}

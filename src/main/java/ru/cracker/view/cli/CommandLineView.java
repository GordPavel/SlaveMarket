package ru.cracker.view.cli;

import ru.cracker.controller.Controller;
import ru.cracker.exceptions.*;
import ru.cracker.model.Model;
import ru.cracker.model.Observable;
import ru.cracker.view.Observer;
import ru.cracker.view.View;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


/**
 * Realization of View with Observer.
 */
public class CommandLineView implements Observer, View {

  private ResourceBundle resources = ResourceBundle.getBundle("app");
  private Controller controller;
  private String user = "s3rius";
  private String token = "";

  /**
   * Constructor to subscribe new view as observer . And link controller.
   *
   * @param model      to register itself
   * @param controller to manage inputs
   */
  public CommandLineView(Model model, Controller controller) {
    ((Observable) model).addObserver(this);
    this.controller = controller;

  }

  /**
   * Method called when opened one merchandise by id.
   *
   * @param slaveId id of merchandise.
   * @param scanner scanner to read current user input.
   */
  private void openSlaveMenu(int slaveId, Scanner scanner) {
    System.out.println(
            "Opened merchandise\'s menu with id " + slaveId + "\nType \"help\" to learn basics");
    System.out.print(">");
    Pattern delete = Pattern.compile("\\bDELETE\\b", Pattern.CASE_INSENSITIVE);
    Pattern set = Pattern
            .compile("(\\bSET\\b)(( ([a-zA-Z]*[a-zA-Z0-9]*)(=)([a-zA-Z0-9]+[.\\w]*)+)+)",
                    Pattern.CASE_INSENSITIVE);
    Pattern exit = Pattern.compile("(\\bEXIT\\b)", Pattern.CASE_INSENSITIVE);
    Pattern help = Pattern.compile("(\\bHELP\\b)( [\\w]*)?", Pattern.CASE_INSENSITIVE);
    Pattern info = Pattern.compile("(\\bINFO\\b)", Pattern.CASE_INSENSITIVE);
    Pattern buy = Pattern.compile("(\\bBUY\\b)", Pattern.CASE_INSENSITIVE);
    Matcher deleteMatcher;
    Matcher setMatcher;
    Matcher exitMatcher;
    Matcher helpMatcher;
    Matcher infoMatcher;
    Matcher buyMatcher;
    while (scanner.hasNext()) {
      String action = scanner.nextLine();
      deleteMatcher = delete.matcher(action);
      setMatcher = set.matcher(action);
      exitMatcher = exit.matcher(action);
      helpMatcher = help.matcher(action);
      infoMatcher = info.matcher(action);
      buyMatcher = buy.matcher(action);
      if (deleteMatcher.lookingAt()) {
        try {
          controller.removeMerchant(slaveId, user, token);
        } catch (UnsupportedOperationException e) {
          System.out.println("You try to delete slave but that operation is ");
          System.out.println(e.getMessage());
        }
        return;
      } else if (exitMatcher.lookingAt()) {
        return;
      } else if (setMatcher.lookingAt()) {
        try {
          controller.setValuesToMerchandise(slaveId, setMatcher.group(2), user, token);
        } catch (MerchandiseAlreadyBought e) {
          System.out.println("Can not change merchandise. It's already bought.");
        } catch (WrongQueryException e) {
          System.out.println(e.getMessage());
        }
      } else if (helpMatcher.lookingAt()) {
        if (helpMatcher.group(2) == null) {
          System.out.println(resources.getString("slaveMenuHelp"));
        } else {
          try {
            System.out
                    .println(resources.getString("HELPMERCH"
                            + helpMatcher.group(2)
                            .trim()
                            .toUpperCase()
                    ));
          } catch (MissingResourceException e) {
            System.out.println("Unknown option \"" + helpMatcher.group(2).trim() + "\"");
          }
        }
      } else if (infoMatcher.lookingAt()) {
        System.out.println("Slave's info :");
        try {
          System.out.println(controller.getMerchantById(slaveId));
        } catch (UnsupportedOperationException e) {
          System.out.println(e.getMessage());
        }
      } else if (buyMatcher.lookingAt()) {
        try {
          controller.buyMerchandise(slaveId, user, token);
        } catch (MerchandiseAlreadyBought e) {
          System.out.println(e.getMessage());
        }
      } else {
        System.out.println("Unknown command");
      }
      System.out.print(">");
    }
  }

  /**
   * Apply action performed after the trigger.
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
   * Launch the view or CLI.
   */
  public void launch() {
    startLogin();
  }

  private void startLogin() {
    System.out.println(resources.getString("login"));
    Scanner scanner = new Scanner(System.in);
    Pattern login = Pattern.compile("\\bLOGIN\\b", Pattern.CASE_INSENSITIVE);
    Pattern register = Pattern.compile("\\bREGISTER\\b", Pattern.CASE_INSENSITIVE);
    Pattern help = Pattern.compile("(\\bHELP\\b)( [\\w]*)?", Pattern.CASE_INSENSITIVE);
    Pattern exit = Pattern.compile("\\bEXIT\\b", Pattern.CASE_INSENSITIVE);
    Matcher helpMatcher;
    while (true) {
      String command = scanner.nextLine();
      helpMatcher = help.matcher(command);
      if (exit.matcher(command).lookingAt()) {
        System.out.println("bye.");
        System.exit(0);
      } else if (login.matcher(command).lookingAt()) {
        System.out.println("Enter your login");
        String username = scanner.nextLine();
        System.out.println("Enter your password");
        String pass = scanner.nextLine();
        try {
          token = controller.login(username, pass);
        } catch (UserException e) {
          System.out.println(e.getMessage());
        }
        if (!token.equals("-1") && !token.equals("")) {
          startMenu(scanner);
        } else if (!token.equals("")) {
          token = "";
          System.out.println("Wrong password or user already in");
        }
      } else if (register.matcher(command).lookingAt()) {
        System.out.println("Enter your login");
        String username = scanner.nextLine().trim();
        System.out.println("Enter your password");
        String pass = scanner.nextLine();

        boolean state = controller.register(username, pass);
        if (state) {
          System.out.println("Successfully registered");
        } else {
          System.out.println("That login already taken");
        }
      } else if (helpMatcher.lookingAt()) {
        if (helpMatcher.group(2) == null) {
          System.out.println(resources.getString("HELPLOGIN"));
        } else {
          try {
            System.out.println(
                    resources.getString("HELPLOGIN"
                            + help.matcher(command)
                            .group(2)
                            .trim()
                            .toUpperCase()
                    )
            );
          } catch (MissingResourceException e) {
            System.out.println("Unknown option \"" + help.matcher(command).group(2).trim() + "\"");
          }
        }
      } else {
        System.out.println("Unknown command");
      }
    }
  }

  /**
   * Method to enter main menu of program.
   */
  private void startMenu(Scanner scanner) {
    System.out.println(resources.getString("welcomeBasics"));
    System.out.print(">");
    String namePattern = "([a-zA-Z]*[a-zA-Z0-9]*)";
    String valuePattern = "([a-zA-Z0-9]+[.\\w]*)";
    Pattern exit = Pattern.compile("(\\bEXIT\\b)([ ]*)([\\w]*)", Pattern.CASE_INSENSITIVE);
    Pattern search = Pattern.compile(
            "^(\\bSEARCH \\b)((" + namePattern + "(>=|<=|>|<|!=|=)" + valuePattern
                    + "+)((\\b AND \\b)("
                    + namePattern + "(>=|<=|>|<|!=|=)" + valuePattern + "))*|ALL)",
            Pattern.CASE_INSENSITIVE);
    Pattern slaveMenu = Pattern.compile("(\\bMERCH \\b)(\\d*)", Pattern.CASE_INSENSITIVE);
    Pattern addMerchandise = Pattern
            .compile("(\\bADD\\b)", Pattern.CASE_INSENSITIVE);
    Pattern help = Pattern.compile("(\\bHELP\\b)( [\\w]*)?", Pattern.CASE_INSENSITIVE);
    Pattern profile = Pattern.compile("\\bprofile\\b", Pattern.CASE_INSENSITIVE);
    Matcher exitMatcher;
    Matcher searchMatcher;
    Matcher slaveMenuMatcher;
    Matcher helpMatcher;
    Matcher addMatcher;
    Matcher profileMatcher;
    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      exitMatcher = exit.matcher(line);
      addMatcher = addMerchandise.matcher(line);
      searchMatcher = search.matcher(line);
      slaveMenuMatcher = slaveMenu.matcher(line);
      helpMatcher = help.matcher(line);
      profileMatcher = profile.matcher(line);
      if (exitMatcher.lookingAt()) {
        controller.disconnect(user, token);
        System.out.println("bye");
        System.exit(0);
      } else if (searchMatcher.lookingAt()) {
        try {
          List<String> found = controller.searchMerchant(searchMatcher.group(2).trim());
          if (found.size() == 0) {
            System.out.println("there's no data found");
          } else {
            found.forEach(System.out::println);
          }
        } catch (UnsupportedOperationException | WrongQueryException e) {
          System.out.println(e.getMessage());
        }
      } else if (slaveMenuMatcher.lookingAt()) {
        try {
          controller.getMerchantById(Integer.parseInt(slaveMenuMatcher.group(2)));
          openSlaveMenu(Integer.parseInt(slaveMenuMatcher.group(2)), scanner);
          System.out.println("backed into main menu");
        } catch (MerchandiseNotFoundException e) {
          System.out.println("Merchandise with that id doesn't exist");
        }
      } else if (helpMatcher.lookingAt()) {
        if (null == helpMatcher.group(2)) {
          System.out.println(resources.getString("help"));
        } else {
          try {
            System.out.println(
                    resources.getString("HELPMAIN" + helpMatcher.group(2).trim().toUpperCase()));
          } catch (MissingResourceException e) {
            System.out.println("Unknown option \"" + helpMatcher.group(2).trim() + "\"");
          }
        }
      } else if (addMatcher.lookingAt()) {
        List<String> classes = controller.getAvailableClasses();
        System.out.println("You can create these types of merchandise:");
        IntStream.range(0, classes.size()).forEach(i -> {
          System.out.println(" " + i + 1 + " | " + classes.get(i));
        });
        System.out.println("please enter your choice's index or name");
        System.out.print(">");
        Pattern number = Pattern.compile("[0-9]+");
        String className = scanner.nextLine();
        try {
          if (number.matcher(className).lookingAt()) {
            int index = Integer.parseInt(className) - 1;
            if (index > classes.size() - 1) {
              System.out.println("wrong index");
            } else {
              className = classes.get(index);
            }
          }
          List<String> fields = controller.getMandatoryFields(className);
          className = className.toLowerCase();
          className = Character.toUpperCase(className.charAt(0)) + className.substring(1);

          Map<String, String> kvs = new HashMap<>();
          for (String field : fields) {
            System.out.println("Please enter value of " + field);
            System.out.print(">");
            kvs.put(field.toUpperCase(), scanner.nextLine());
          }
          System.out.println("Enter deal's price");
          System.out.print(">");
          try {
            int price = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Do you want to add merchandise with this params? "
                    + kvs
                    + " with price = " + price
                    + "\nY/N");
            String ans = scanner.nextLine();
            if (ans.trim().toUpperCase().equals("Y")) {
              controller.addMerchandiseByMap(className, kvs, user, token, price);
            }
          } catch (NumberFormatException e) {
            System.out.println("Wrong input " + e.getMessage());
          }
        } catch (WrongClassCallException | CreateMerchandiseException e) {
          System.out.println(e.getMessage());
        }
        System.out.println("backed in the main menu");
      } else if (profileMatcher.lookingAt()) {
        userMenu(scanner);
      } else {
        System.out.println("Unknown command");
      }
      System.out.print(">");
    }
  }

  private void userMenu(Scanner scanner) {
    System.out.println(resources.getString("USERMENU"));
    Pattern help = Pattern.compile("(\\bHELP\\b)( [\\w]*)?", Pattern.CASE_INSENSITIVE);
    Pattern exit = Pattern.compile("(\\bEXIT\\b)([ ]*)([\\w]*)", Pattern.CASE_INSENSITIVE);
    Pattern deals = Pattern.compile("\\bdeals\\b", Pattern.CASE_INSENSITIVE);
    Matcher dealsMatcher;
    Matcher exitMatcher;
    Matcher helpMatcher;
    System.out.print(">");
    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      exitMatcher = exit.matcher(line);
      helpMatcher = help.matcher(line);
      dealsMatcher = deals.matcher(line);
      if (exitMatcher.lookingAt()) {
        System.out.println("backed in main menu");
        break;
      } else if (helpMatcher.lookingAt()) {
        if (null == helpMatcher.group(2)) {
          System.out.println(resources.getString("HELPPROFILE"));
        } else {
          try {
            System.out.println(
                    resources.getString("HELPPROFILE" + helpMatcher.group(2).trim().toUpperCase()));
          } catch (MissingResourceException e) {
            System.out.println("Unknown option \"" + helpMatcher.group(2).trim() + "\"");
          }
        }
      } else if (dealsMatcher.lookingAt()) {
        controller.getDealsByUser(user, token).forEach(System.out::println);
      } else {
        System.out.println("Unknown command");
      }
      System.out.print(">");
    }
  }
}
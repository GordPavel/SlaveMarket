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
 *
 */
public class CLView implements Observer, View {

    private ResourceBundle resources = ResourceBundle.getBundle("app");
    private Controller controller;
    private String user = "s3rius";

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
        System.out.println("Opened merchandise\'s menu with id " + slaveId + "\nType \"help\" to learn basics");
        System.out.print(">");
        Pattern delete = Pattern.compile("\\bDELETE\\b", Pattern.CASE_INSENSITIVE);
        Pattern set = Pattern
                .compile("(\\bSET\\b)(( ([a-zA-Z]*[a-zA-Z0-9]*)(=)([a-zA-Z0-9]+[.\\w]*)+)+)", Pattern.CASE_INSENSITIVE);
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
                    controller.removeMerchant(slaveId, user);
                } catch (UnsupportedOperationException e) {
                    System.out.println("You try to delete slave but that operation is ");
                    System.out.println(e.getMessage());
                }
                return;
            } else if (exitMatcher.lookingAt()) {
                return;
            } else if (setMatcher.lookingAt()) {
                try {
                    controller.setValuesToMerchandise(slaveId, setMatcher.group(2), user);
                } catch (MerchandiseAlreadyBought e) {
                    System.out.println("Can not change merchandise. It's already bought.");
                } catch (WrongQueryException e) {
                    System.out.println(e.getMessage());
                }
            } else if (helpMatcher.lookingAt()) {
                if (helpMatcher.group(2) == null)
                    System.out.println(resources.getString("slaveMenuHelp"));
                else
                    try {
                        System.out
                                .println(resources.getString("HELPMERCH" + helpMatcher.group(2).trim().toUpperCase()));
                    } catch (MissingResourceException e) {
                        System.out.println("Unknown option \"" + helpMatcher.group(2).trim() + "\"");
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
                    controller.buyMerchandise(slaveId, user);
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
        String namePattern = "([a-zA-Z]*[a-zA-Z0-9]*)";
        String valuePattern = "([a-zA-Z0-9]+[.\\w]*)";
        Pattern exit = Pattern.compile("(\\bEXIT\\b)([ ]*)([\\w]*)", Pattern.CASE_INSENSITIVE);
        Pattern search = Pattern.compile(
                "^(\\bSEARCH \\b)((" + namePattern + "(>=|<=|>|<|!=|=)" + valuePattern + "+)((\\b AND \\b)(" + namePattern + "(>=|<=|>|<|!=|=)" + valuePattern + "))*|ALL)", Pattern.CASE_INSENSITIVE);
        Pattern slaveMenu = Pattern.compile("(\\bMERCH \\b)(\\d*)", Pattern.CASE_INSENSITIVE);
        Pattern addMerchandise = Pattern
                .compile("(\\bADD\\b)", Pattern.CASE_INSENSITIVE);
        Pattern help = Pattern.compile("(\\bHELP\\b)( [\\w]*)?", Pattern.CASE_INSENSITIVE);
        Matcher exitMatcher;
        Matcher searchMatcher;
        Matcher slaveMenuMatcher;
        Matcher helpMatcher;
        Matcher addMatcher;
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            exitMatcher = exit.matcher(line);
            addMatcher = addMerchandise.matcher(line);
            searchMatcher = search.matcher(line);
            slaveMenuMatcher = slaveMenu.matcher(line);
            helpMatcher = help.matcher(line);
            if (exitMatcher.lookingAt()) {
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
                if (helpMatcher.group(2) == null) {
                    System.out.println(resources.getString("help"));
                } else {
                    try {
                        System.out.println(resources.getString("HELPMAIN" + helpMatcher.group(2).trim().toUpperCase()));
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
                    System.out.println("Do you want to add merchandise with this params? " + kvs + "\nY/N");
                    String ans = scanner.nextLine();
                    if (ans.trim().toUpperCase().equals("Y")) {
                        controller.addMerchantByMap(className, kvs, user);
                    }
                } catch (WrongClassCallException | CreateMerchandiseException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("backed in the main menu");
            } else {
                System.out.println("Unknown command");
            }
            System.out.print(">");
        }
    }
}

package ru.cracker.view.cli;

import ru.cracker.controller.Controller;
import ru.cracker.exceptions.MerchandiseAlreadyBought;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.Model;
import ru.cracker.model.Observable;
import ru.cracker.model.merchandises.Merchandise;
import ru.cracker.view.Observer;
import ru.cracker.view.View;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


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
        Pattern set = Pattern.compile("(\\bSET\\b)(( ([a-zA-Z]*[a-zA-Z0-9]*)(=)([a-zA-Z0-9]+[.\\w]*)+)+)", Pattern.CASE_INSENSITIVE);
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
                        System.out.println(resources.getString("HELPMERCH" + helpMatcher.group(2).trim().toUpperCase()));
                    } catch (MissingResourceException e) {
                        System.out.println("Unknown option \"" + helpMatcher.group(2).trim() + "\"");
                    }
            } else if (infoMatcher.lookingAt()) {
                System.out.println("Slave's info :");
                try {
                    System.out.println(controller.getMerchantById(slaveId).getAllInfo());
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
                .compile("(\\bADD \\b)([A-Z]+)(( (" + namePattern + "=" + valuePattern + "))+)", Pattern.CASE_INSENSITIVE);
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
                if (searchMatcher.group(2).equals(" ")) {
                    System.out.println("search of all db performed");
                    try {
                        controller.searchMerchant(searchMatcher.group(2));
                    } catch (UnsupportedOperationException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    try {
//            System.out.println("search of \"" + searchMatcher.group(2).trim() + "\" performed");
                        controller.searchMerchant(searchMatcher.group(2).trim()).forEach(System.out::println);
                    } catch (UnsupportedOperationException | WrongQueryException e) {
                        System.out.println(e.getMessage());
                    }
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
                String className = addMatcher.group(2);
                className = className.toLowerCase();
                className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
                try {
                    Class merchandise = Class.forName("ru.cracker.model.merchandises.classes." + className);
                    Map<String, String> kvs = Arrays.stream(addMatcher.group(3).trim().split(" "))
                            .map(elem -> elem.split("="))
                            .collect(Collectors.toMap(e -> e[0].toUpperCase(), e -> e[1]));
                    Merchandise merch = (Merchandise) merchandise.getMethod("buildFromMap", kvs.getClass())
                            .invoke(null, kvs);
                    controller.addMerchant(merch, user);
                } catch (ClassNotFoundException e) {
                    System.out.println("Can not find that Type of merchandise");
                } catch (IllegalAccessException | NoSuchMethodException e) {
                    System.out.println("Error while building");
                } catch (WrongQueryException e) {
                    System.out.println(e.getMessage());
                } catch (InvocationTargetException e) {
                    System.out.println(e.getCause().getMessage());
                }
            } else {
                System.out.println("Unknown command");
            }
            System.out.print(">");
        }
    }
}

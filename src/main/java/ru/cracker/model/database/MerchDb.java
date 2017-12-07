package ru.cracker.model.database;

import org.reflections.Reflections;
import ru.cracker.exceptions.MerchandiseAlreadyBought;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongClassCallException;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.Merchandise;
import ru.cracker.model.merchandises.SlaveInterface;
import ru.cracker.model.merchandises.classes.Niger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@FunctionalInterface
interface QueryComparator<A, B> {
    Boolean apply(A a, B b);
}

/**
 *
 */
public class MerchDb implements Database {

    /**
     * List of merchandise.
     */
    private List<Merchandise> merchants;

    private ObjectOutputStream objectOutputStream;

    /**
     * Logger object to log all operations into file.
     */
    private Logger logger = new Logger();

    /**
     * Default constructor
     */
    public MerchDb() {
        merchants = new ArrayList<Merchandise>();

//        generateData(400);
        loadFromFile();
    }

    private void loadFromFile() {
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream("data.dat"))) {
            merchants = (List<Merchandise>) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            try {
                boolean newFile = new File("data.dat").createNewFile();
                if (newFile) {
                    merchants.clear();
                } else {
                    System.out.println("Serious error happen. We're sorry.");
                    System.exit(-1);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Generate random data for database.
     *
     * @param quantity quantity of objects to generate.
     */
    private void generateData(int quantity) {
        Random random = new Random();
        IntStream.range(0, quantity).forEach(i ->
                addMerchandise(Niger.newBuilder().addName(random.nextBoolean() ? "Brian" : random
                        .nextBoolean() ? "Julia" : random.nextBoolean() ? "Mark" : "Mary")
                        .addAge(random.nextInt(60) + 23)
                        .addGender(random.nextBoolean() ? "male" : "female")
                        .addPrice(random.nextInt(3000) + 1000)
                        .addHeight(random.nextInt(55) + 140)
                        .addWeight(random.nextInt(60) + 50)
                        .build(), "admin"));

    }

    /**
     * Saves all data into file.
     */
    private void saveData() {
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream("data.dat"));
        } catch (IOException e) {
            logger.logError("\u001B[31mCan't open/create data file create data.dat file in project directory\u001B[0m");
        }
        try {
            objectOutputStream.writeObject(merchants);
        } catch (IOException e) {
            logger.logError("\u001B[31mCan't save changes\u001B[0m");
            logger.log("DataBase", "\u001B[31mSave failed\u001B[0m", "");
        }

    }

    /**
     * Puts merch into the vault
     *
     * @param merch Merch to put in vault
     * @param user  user who performed action
     */
    public void addMerchandise(Merchandise merch, String user) {
        merch.setId(merchants.size());
        merchants.add(merch);
        logger.log(user, "add merchandise", merchants.get(merchants.size() - 1).getAllInfo());
        saveData();
    }

    /**
     * @param merch Removes merchandise from vault
     * @param user  user who performed action
     */
    public void removeMerchandise(Merchandise merch, String user) {
        int id = merchants.indexOf(merch);
        if (id != -1) {
            merchants.stream().filter(i -> i.getId() >= id)
                    .forEach(merchandise -> merchandise.setId(merchandise.getId() - 1));
            merchants.remove(id);
            logger.log(user, "removed merchandise", merch.getAllInfo());
            saveData();
        } else
            throw new MerchandiseNotFoundException(merch);
    }

    /**
     * remove merchandise from vault by id
     *
     * @param id   merchandise unique identification
     * @param user user who performed action
     */
    public void removeMerchandise(int id, String user) {
        if (id >= merchants.size() || id < 0) {
            throw new MerchandiseNotFoundException(id);
        }
        if (merchants.get(id).isBought()) {
            throw new MerchandiseAlreadyBought(id);
        }
        logger.log(user, "removed merchandise", findMerh(id).getAllInfo());
        merchants.remove(id);
        merchants.stream().filter(i -> i.getId() >= id)
                .forEach(merchandise -> merchandise.setId(merchandise.getId() - 1));
        saveData();
    }

    /**
     * Method to find specified Merchandises
     *
     * @param querry querry to filter results
     * @return List of Merchandises specified by query
     */
    //add niger name=nikolai age=41 gender=male height=180 weight=80 price=1000
    public List<String> searchMerchandise(String querry) {
        Stream<Merchandise> merchandises = merchants.stream();
        /**
         //	Dear maintainer:
         //
         // Once you are done trying to 'optimize' this routine,
         // and have realized what a terrible mistake that was,
         // please increment the following counter as a warning
         // to the next guy:
         //
         // total_hours_wasted_here = 42
         */
        if (!Pattern.compile("all", Pattern.CASE_INSENSITIVE).matcher(querry.trim()).lookingAt()) {
            String namePattern = "([a-zA-z]+[[0-9]*[a-zA-z]]*)";
            Pattern pattern = Pattern.compile("\\sAND\\s");
            Pattern notEqQuerySplitter = Pattern.compile(namePattern + "(>|>=|<|<=)([\\d]+[.\\d]*)");
            Pattern eqQuerySplitter = Pattern.compile(namePattern + "(=|!=)([\\w]+[.\\w]*)");
            String[] strings = pattern.split(querry);
            for (String subQuery : strings) {
                Matcher notEqualsMatcher = notEqQuerySplitter.matcher(subQuery);
                Matcher equalsMatcher = eqQuerySplitter.matcher(subQuery);
                Matcher finalMatcher;
                if (notEqualsMatcher.lookingAt()) {
                    finalMatcher = notEqualsMatcher;
                } else if (equalsMatcher.lookingAt()) {
                    finalMatcher = equalsMatcher;
                } else {
                    throw new WrongQueryException(subQuery);
                }
                String field = finalMatcher.group(1).toUpperCase();
                String value = finalMatcher.group(3);
                QueryComparator<String, String> finalComparator = createComparator(finalMatcher.group(2));
                merchandises = merchandises.filter(merchandise -> {
//                    Field[] fields = merchandise.getClass().getDeclaredFields();
//                    for (Field fieldName : fields) {
//                        fieldName.setAccessible(true);
//                        try {
//                            if (fieldName.getName().toUpperCase().equals(field) && !merchandise.isBought()
//                                    && finalComparator.apply(String.valueOf(fieldName.get(merchandise)), value)) {
//                                return true;
//                            }
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    return false;
                    Method[] methods = merchandise.getClass().getMethods();
                    for (Method method : methods) {
                        if (!method.getName().startsWith("get")) continue;
                        if (method.getParameterTypes().length != 0) continue;
                        if (void.class.equals(method.getReturnType())) continue;
                        try {
                            if (method.getName().substring(3).toUpperCase().equals(field)
                                    && !merchandise.isBought()
                                    && finalComparator.apply(String.valueOf(method.invoke(merchandise)), value))
                                return true;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            System.out.println("Something bad happens sorry.");
                        }
                    }
                    return false;
                });
            }
            return merchandises.map(Merchandise::getAllInfo).collect(Collectors.toList());
        }
        return merchandises.map(Merchandise::getAllInfo).collect(Collectors.toList());
    }

    /**
     * Function to create comparator for subQuery
     *
     * @param sign sign of subQuery between key an value
     * @return lambda comparator that compare to values and return boolean.
     */
    private QueryComparator<String, String> createComparator(String sign) {
        QueryComparator<String, String> comparator = null;
        if (sign.equals(">"))
            comparator = (left, right) -> Double.parseDouble(left) > Double.parseDouble(right);
        if (sign.equals("<"))
            comparator = (left, right) -> Double.parseDouble(left) < Double.parseDouble(right);
        if (sign.equals("<="))
            comparator = (left, right) -> Double.parseDouble(left) <= Double.parseDouble(right);
        if (sign.equals(">="))
            comparator = (left, right) -> Double.parseDouble(left) >= Double.parseDouble(right);
        if (sign.equals("="))
            comparator = String::equals;
        if (sign.equals("!="))
            comparator = (left, right) -> !left.equals(right);
        return comparator;
    }

    /**
     * Returns merchandise by id or exception
     *
     * @param id id of Merchandise
     * @return Founded merchandise or Exception
     */
    @Override
    public String getMerchantById(int id) throws MerchandiseNotFoundException {
        return merchants.stream().filter(i -> Integer.compare(i.getId(), id) == 0)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    if (list.size() != 1) {
                        throw new MerchandiseNotFoundException(id);
                    }
                    return list.get(0).getAllInfo();
                }));
    }

    private Merchandise findMerh(int id) {
        return merchants.stream().filter(i -> Integer.compare(i.getId(), id) == 0)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    if (list.size() != 1) {
                        throw new MerchandiseNotFoundException(id);
                    }
                    return list.get(0);
                }));
    }

    /**
     * Marks merchandise as bought
     *
     * @param id   unique merchandise identity
     * @param user user who performed action
     * @return bought merchandise
     * @throws MerchandiseNotFoundException throws if merchandise can not be found
     */
    @Override
    public String buyMerchandise(int id, String user) throws MerchandiseNotFoundException {
        if (id >= merchants.size() || id < 0) {
            throw new MerchandiseNotFoundException(id);
        } else {
            if (findMerh(id).buy(user)) {
                logger.log(user, "bought merchandise", getMerchantById(id));
                saveData();
                return getMerchantById(id);
            } else {
                throw new MerchandiseAlreadyBought(id);
            }
        }
    }

    /**
     * Set new values  to merchandise.
     *
     * @param id     id of merchandise to be changed
     * @param params String of parameters with values to change
     * @param user   user who performed action
     */
    public void setValuesToMerchandise(int id, String params, String user) {
        Map<String, String> kvs = Arrays.stream(params.trim().split(" "))
                .map(elem -> elem.split("="))
                .collect(Collectors.toMap(e -> e[0].toUpperCase(), e -> e[1]));
        String merchIfo = "{Before: " + findMerh(id).getAllInfo() + "},";
        findMerh(id).setParamsByMap(kvs);
        logger.log(user, "Changed merchandise parameters", merchIfo + " {changed Values:" + kvs + "}");
        saveData();
    }

    /**
     * Method for getting available types of merchandises
     *
     * @return list of available types for creation
     */
    @Override
    public List<String> getAvailableClasses() {
        Reflections reflection = new Reflections("ru.cracker.model.merchandises.classes");
        return reflection.getSubTypesOf(SlaveInterface.class).stream().map(Class::getSimpleName).collect(Collectors.toList());
    }

    @Override
    public List<String> getMandatoryFields(String className) throws WrongClassCallException {
        return Merchandise.getMandatoryFields(className);
    }

    @Override
    public void addMerchandiseByMap(String className, Map<String, String> kvs, String user) {
        try {
            Class merchandise = Class.forName("ru.cracker.model.merchandises.classes." + className);
            Merchandise merch = (Merchandise) merchandise.getMethod("buildFromMap", kvs.getClass()).invoke(null, kvs);
            addMerchandise(merch, user);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.logError(e.getMessage());
            e.printStackTrace();
//            throw new IllegalArgumentException("Error while adding. We're sorry");
        }
    }

}

package ru.cracker.model.database;

import ru.cracker.exceptions.MerchandiseAlreadyBought;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.Merchandise;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
     *
     */
    private List<Merchandise> merchants;

    private Logger logger = new Logger();

    /**
     * Default constructor
     */
    public MerchDb() {
        merchants = new ArrayList<Merchandise>();
        //        generateData(1300);
    }

    private void generateData(int quantity) {
        Random random = new Random();
        //        IntStream.range(0, quantity).forEach(i ->
        //                merchants.add(new Slave(random.nextInt(40) + 160, random.nextInt(60) + 50, random
        //                        .nextInt(40) + 23, random
        //                        .nextBoolean() ? "male" : "female", i, random.nextBoolean() ? "Brian" : random
        //                        .nextBoolean() ? "Julia" : random.nextBoolean() ? "Mark" : "Mary", random.nextInt(600) + 200)));
    }

    /**
     * Puts merch into the vault
     *
     * @param merch Merch to put in vault
     */
    public void addMerchandise(Merchandise merch) {
        merch.setId(merchants.size());
        merchants.add(merch);
        logger.log("admin", "add merchandise with id:" + merch.getId());
    }

    /**
     * @param merch Removes merchandise from vault
     */
    public void removeMerchandise(Merchandise merch) {
        int id = merchants.indexOf(merch);
        if (id != -1) {
            merchants.stream().filter(i -> i.getId() >= id)
                    .forEach(merchandise -> merchandise.setId(merchandise.getId() - 1));
            merchants.remove(id);
//        add niger name=igor gender=helicopter price=10000 height=1414 weight=1300 age=11
            logger.log("admin", "removed merchandise with id:" + id);
        } else
            throw new MerchandiseNotFoundException(merch);
    }

    /**
     * remove merchandise from vault by id
     *
     * @param id merchandise unique identification
     */
    public void removeMerchandise(int id) {
        if (id >= merchants.size() || id < 0) {
            throw new MerchandiseNotFoundException(id);
        }
        merchants.remove(id);
        merchants.stream().filter(i -> i.getId() >= id)
                .forEach(merchandise -> merchandise.setId(merchandise.getId() - 1));
        logger.log("admin", "removed merchandise with id:" + id);

    }

    /**
     * Method to find specified Merchandises
     *
     * @param querry querry to filter results
     * @return List of Merchandises specified by query
     */
    //add niger name=nikolai age=41 gender=male height=180 weight=80 price=1000
    public List<Merchandise> searchMerchandise(String querry) {
        Stream<Merchandise> merchandises = merchants.stream();
        if (!querry.trim().equals("")) {
            Pattern pattern = Pattern.compile("\\sAND\\s");
            Pattern notEqQuerySplitter = Pattern.compile("([a-zA-z]+[[0-9]*[a-zA-z]]*)(>|>=|<|<=)([\\d]+[.\\d]*)");
            Pattern eqQuerySplitter = Pattern.compile("([a-zA-z]+[[0-9]*[a-zA-z]]*)(=|!=)([\\w]+[.\\w]*)");
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
                    Field[] fields = merchandise.getClass().getDeclaredFields();
                    for (Field fieldName : fields) {
                        fieldName.setAccessible(true);
                        try {
                            if (fieldName.getName().toUpperCase().equals(field) && !merchandise.isBought()
                                    && finalComparator.apply(String.valueOf(fieldName.get(merchandise)), value)) {
                                return true;
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                });
            }
            return merchandises.collect(Collectors.toList());
        }
        return merchandises.collect(Collectors.toList());
    }

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
    public Merchandise getMerchantById(int id) throws MerchandiseNotFoundException {
        return merchants.stream().filter(i -> Integer.compare(i.getId(), id) == 0)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    if (list.size() != 1) {
                        throw new MerchandiseNotFoundException(id);
                    }
                    return list.get(0);
                }));
    }

    @Override
    public Merchandise buyMerchandise(int id) throws MerchandiseNotFoundException {
        if (id >= merchants.size() || id < 0) {
            throw new MerchandiseNotFoundException(id);
        } else {
            if (getMerchantById(id).buy()) {
                logger.log("admin", "bought merchandise with id:" + id);
                return getMerchantById(id);
            } else {
                throw new MerchandiseAlreadyBought(id);
            }
        }
    }
}

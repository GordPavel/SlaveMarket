package ru.cracker.Model.database;

import ru.cracker.Model.merchandises.Merchandise;
import ru.cracker.Model.merchandises.Slave;
import ru.cracker.exceptions.MerchandiseNotFoundException;
import ru.cracker.exceptions.WrongQueryException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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

    /**
     * Default constructor
     */
    public MerchDb() {
        merchants = new ArrayList<Merchandise>();
//        generateData(1300);
    }

    private void generateData(int quantity) {
        Random random = new Random();
        IntStream.range(0, quantity).forEach(i ->
                merchants.add(new Slave(random.nextInt(40) + 160, random.nextInt(60) + 50, random
                        .nextInt(40) + 23, random
                        .nextBoolean() ? "male" : "female", i, random.nextBoolean() ? "Brian" : random
                        .nextBoolean() ? "Julia" : random.nextBoolean() ? "Mark" : "Mary", random.nextInt(600) + 200)));
    }

    /**
     * Puts merch into the vault
     *
     * @param merch Merch to put in vault
     */
    public void addMerchandise(Merchandise merch) {
        if (merch.getId() == -1) {
            merch.setId(merchants.size() - 1);
            merchants.add(merch);
        } else {
            merchants.add(merch);
        }
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
        } else throw new MerchandiseNotFoundException(merch);
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
    }

    /**
     * Method to find specified Merchandises
     *
     * @param querry querry to filter results
     * @return List of Merchandises specified by query
     */
    public List<Merchandise> searchMerchandise(String querry) {
//        Stream<Merchandise> merchandises = merchants.stream();
//        QueryComparator<String, String> comparator = null;
//        if (!querry.equals("")) {
//            for (String subQuery : querry.split(" and ")) {
//                String[] reg;
//                reg = subQuery.split(">=");
//                String value;
//                String which;
//                if (reg.length == 1) {
//                    reg = subQuery.split("<=");
//                    if (reg.length == 1) {
//                        reg = subQuery.split("=");
//                        if (reg.length == 1) {
//                            reg = subQuery.split(">");
//                            if (reg.length == 1) {
//                                reg = subQuery.split("<");
//                                try {
//                                    Double.parseDouble(reg[1]);
//                                    which = "<";
//                                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
//                                    throw new WrongQueryException(subQuery);
//                                }
//                            } else {
//                                try {
//                                    Double.parseDouble(reg[1]);
//                                    which = ">";
//                                } catch (NumberFormatException e) {
//                                    throw new WrongQueryException(subQuery);
//                                }
//                            }
//                        } else {
//                            reg = subQuery.split("=");
//                            if (reg.length == 1) {
//                                which = "?";
//                            } else {
//                                which = "=";
//                            }
//                        }
//                    } else {
//                        try {
//                            Double.parseDouble(reg[1]);
//                            which = "<=";
//                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
//                            throw new WrongQueryException(subQuery);
//                        }
//                    }
//                } else {
//                    try {
//                        Double.parseDouble(reg[1]);
//                        which = ">=";
//                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
//                        throw new WrongQueryException(subQuery);
//                    }
//                }
//                switch (which) {
//                    case "<=":
//                        comparator = (a, b) -> Double.parseDouble(a) <= Double.parseDouble(b);
//                        break;
//                    case ">=":
//                        comparator = (a, b) -> Double.parseDouble(a) >= Double.parseDouble(b);
//                        break;
//                    case "<":
//                        comparator = (a, b) -> Double.parseDouble(a) < Double.parseDouble(b);
//                        break;
//                    case ">":
//                        comparator = (a, b) -> Double.parseDouble(a) > Double.parseDouble(b);
//                        break;
//                    case "=":
//                        comparator = String::equals;
//                        break;
//                    case "?":
//                        throw new WrongQueryException(subQuery);
//                }
//                String key = reg[0].toUpperCase();
//                value = reg[1];
//                QueryComparator<String, String> comparator1 = comparator;
//                merchandises = merchandises.filter(merchandise -> {
//                    Field[] fields = merchandise.getClass().getDeclaredFields();
//                    for (Field field : fields) {
//                        field.setAccessible(true);
//                        try {
//                            if (field.getName().toUpperCase().equals(key) &&
//                                    !merchandise.isBought() &&
//                                    comparator1.apply(String.valueOf(field.get(merchandise)), value)) {
//                                return true;
//                            }
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    return false;
//                });
//            }
//            return merchandises.collect(toList());
//        } else
//            return merchandises.collect(toList());

        Stream<Merchandise> merchandises = merchants.stream();
        if (!querry.trim().equals("")) {
            Pattern pattern = Pattern.compile("\\sand\\s");
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
                            if (fieldName.getName().toUpperCase().equals(field) &&
                                    !merchandise.isBought() &&
                                    finalComparator.apply(String.valueOf(fieldName.get(merchandise)), value)) {
                                return true;
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                });
            }
            return merchandises.collect(toList());
        }
        return merchandises.collect(toList());
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
//                if (equals.matcher(subQuery).matches()) {
//                    comparator = String::equals;
//                    field = subQuery.split("=")[0].toUpperCase();
//                    value = subQuery.split("=")[1];
//                    System.out.println(field + " is equals to " + value);
//                } else if (greater.matcher(subQuery).matches()) {
//                    comparator = (left, right) -> Double.parseDouble(left) > Double.parseDouble(right);
//                    field = subQuery.split(">")[0].toUpperCase();
//                    value = subQuery.split(">")[1];
//                    System.out.println(field + " is greater than " + value);
//                } else if (less.matcher(subQuery).matches()) {
//                    comparator = (left, right) -> Double.parseDouble(left) < Double.parseDouble(right);
//                    field = subQuery.split("<")[0].toUpperCase();
//                    value = subQuery.split("<")[1];
//                    System.out.println(field + " is less than " + value);
//                } else if (lessAndEquals.matcher(subQuery).matches()) {
//                    comparator = (left, right) -> Double.parseDouble(left) <= Double.parseDouble(right);
//                    field = subQuery.split("<=")[0].toUpperCase();
//                    value = subQuery.split("<=")[1];
//                    System.out.println(field + " is less or equals " + value);
//                } else if (greaterAndEquals.matcher(subQuery).matches()) {
//                    comparator = (left, right) -> Double.parseDouble(left) >= Double.parseDouble(right);
//                    field = subQuery.split(">=")[0].toUpperCase();
//                    value = subQuery.split(">=")[1];
//                    System.out.println(field + " is greater or equals " + value);
//                } else {
//                    throw new WrongQueryException(subQuery);
//                }
//                if (value.toUpperCase().equals("ALL")) {
//                    comparator = (a, b) -> true;
//                    System.out.println(field + " is always true");
//                }
//                QueryComparator<String, String> finalComparator = comparator;
//                String finalValue = value;
//                String finalField = field;
//                merchandises = merchandises.filter(merchandise -> {
//                    Field[] fields = merchandise.getClass().getDeclaredFields();
//                    for (Field fieldName : fields) {
//                        fieldName.setAccessible(true);
//                        try {
//                            if (fieldName.getName().toUpperCase().equals(finalField) &&
//                                    !merchandise.isBought() &&
//                                    finalComparator.apply(String.valueOf(fieldName.get(merchandise)), finalValue)) {
//                                return true;
//                            }
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    return false;
//                });
//            }
//            return merchandises.collect(toList());
//        } else
//            return merchandises.
//                    filter(merchandise -> !merchandise.isBought()).
//                    collect(toList());

    /**
     * Returns merchandise by id or exception
     *
     * @param id id of Merchandise
     * @return Founded merchandise or Exception
     */
    @Override
    public Merchandise getMerchantById(int id) throws MerchandiseNotFoundException {
        return merchants.stream().filter(i -> Integer.compare(i.getId(), id) == 0)
                .collect(Collectors.collectingAndThen(toList(),
                        list -> {
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
            getMerchantById(id).buy();
            return getMerchantById(id);
        }
    }
}
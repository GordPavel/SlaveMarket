package model.database;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import exceptions.*;
import gigadot.rebound.Rebound;
import model.merchandises.Merchandise;
import model.merchandises.classes.Alien;
import model.merchandises.classes.Food;
import model.merchandises.classes.Poison;
import model.merchandises.classes.Slave;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


/**
 * Realization of Database interface.
 */
@XmlRootElement(name = "database")
@XmlAccessorType(XmlAccessType.NONE)
public class MerchDb implements Database {

    /**
     * Logger object to log all operations into file.
     */
    private final Logger logger = new Logger();
    /**
     * File to write all deals.
     */
    private final String dealFile = "deals.dl";
    /**
     * File to write user's list.
     */
    private final String userFile = "users.us";
    private final String classesPackage = "model.merchandises.classes";
    /**
     * List of merchandise.
     */
    private List<Merchandise> merchants;
    /**
     * Deal manager.
     */
    @XmlElement(name = "dealList")
    private DealList deals;
    /**
     * List of users.
     */
    @XmlElementWrapper
    @XmlAnyElement(lax = true)
    private List<User> users;


    /**
     * Classes, that used in import
     */
    private Class[] importClasses = new Class[]{
            MerchDb.class,
            DealList.class,
            Deal.class,
            User.class,
            Slave.class,
            Food.class,
            Alien.class,
            Poison.class
    };

    /**
     * Default constructor, for xml import.
     */
    public MerchDb() {

    }

    /**
     * Constructor to create/open database.
     *
     * @param load boolean to load data from file. If true data file
     */
    public MerchDb(boolean load) {
        merchants = new ArrayList<Merchandise>();
        users = new ArrayList<User>();
        deals = new DealList();

        if (load) {
            loadFromFile();
            unblockAll();
        }
//    generateData(20, "White man", encrypt("White man", "password"));
    }

    private void unblockAll() {
        users.forEach(user -> user.setToken(""));
    }


    /**
     * Method to load data from file.
     */
    private void loadFromFile() {
        deals = FileManager.readDeals(dealFile);
        users = FileManager.readUsers(userFile);
        merchants = deals.getDeals()
                .stream()
                .map(Deal::getMerchandise)
                .distinct()
                .sorted(Comparator.comparingInt(Merchandise::getId))
                .collect(toList());
    }

//  /**
//   * Generate random data for database.
//   *
//   * @param quantity quantity of objects to generate.
//   */
//  private void generateData(int quantity, String username, String password) {
//    Random random = new Random();
//    register(username, password);
//    String token = login(username, password);
//    for (int i = 0; i < quantity; i++) {
//      deals.addDeal(new Deal(getUser(username, token),
//              Slave.newBuilder().addName(random.nextBoolean() ? "Brian" : random
//                      .nextBoolean() ? "Julia" : random.nextBoolean() ? "Mark" : "Mary")
//                      .addId(i)
//                      .addAge(random.nextInt(60) + 23)
//                      .addGender(random.nextBoolean() ? "male" : "female")
//                      .addHeight(random.nextInt(55) + 140)
//                      .addWeight(random.nextInt(60) + 50)
//                      .build(),
//              random.nextInt(3000) + 1000, DealState.FOR_SALE, deals.getDeals().size()));
//    }
//    disconnect(username, token);
//    saveData();
//  }
//
//  private String encrypt(String username, String pass) {
//    try {
//      String key = username;
//      if (username.length() < 16) {
//        char[] chars = new char[16 - username.length()];
//        Arrays.fill(chars, '1');
//        key = username + new String(chars);
//      }
//      Cipher cipher = Cipher.getInstance("AES");
//      Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
//      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
//      Base64.Encoder encoder = Base64.getEncoder();
//      pass = encoder.encodeToString(cipher.doFinal(pass.getBytes()));
//    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
//      logger.logError(e.getMessage());
//    }
//    return pass;
//  }

    /**
     * Saves all data into file.
     */
    private synchronized void saveData() {
        FileManager.writeSerialaizableToFile(deals, dealFile);
        FileManager.writeSerialaizableToFile(users, userFile);
    }

    /**
     * Puts merch into the vault.
     *
     * @param merch Merchandise to put in vault
     * @param user  user who performed action
     */
    public synchronized void addMerchandise(Merchandise merch, String user, String token, int price) {
        merch.setId(merchants.size());
        merchants.add(merch);
        deals.addDeal(
                new Deal(getUser(user, token),
                        merch,
                        price,
                        DealState.FOR_SALE,
                        deals.getDeals().size()
                ));
        logger.log(user, "add merchandise", getMerchandise(merchants.size() - 1).getAllInfo());
        saveData();
    }

    /**
     * Removes merchandise from database.
     *
     * @param merch Removes merchandise from vault
     * @param user  user who performed action
     */
    public synchronized void removeMerchandise(Merchandise merch, String user, String token) {
        int id = merchants.indexOf(merch);
//    merchants.remove(merch);
        if (id != -1) {
//      merchants.stream().filter(i -> i.getId() >= id)
//              .forEach(merchandise -> merchandise.setId(merchandise.getId() - 1));
            deals.addDeal(
                    new Deal(
                            getUser(user, token),
                            merch, lastDeal(merch).getPrice(),
                            DealState.REMOVED,
                            deals.getDeals().size()
                    ));
            logger.log(user, "removed merchandise", merch.getAllInfo());
            saveData();
        } else {
            throw new MerchandiseNotFoundException(merch);
        }
    }

    /**
     * remove merchandise from vault by id.
     *
     * @param id   merchandise unique identification
     * @param user user who performed action
     */
    public synchronized void removeMerchandise(int id, String user, String token) throws MerchandiseAlreadyBought {
        if (id >= merchants.size() || id < 0) {
            throw new MerchandiseNotFoundException(id);
        }
        if (!lastDeal(getMerchandise(id)).getUser().getToken().equals(token)) {
            throw new MerchandiseAlreadyBought("That merchandise not yours.");
        }
        if (lastDeal(getMerchandise(id)).getState().equals(DealState.REMOVED)) {
            throw new MerchandiseAlreadyBought("Merchandise already removed.");
        }
        logger.log(user, "removed merchandise", findMerh(id).getAllInfo());
        deals.addDeal(new Deal(getUser(user, token),
                getMerchandise(id),
                lastDeal(getMerchandise(id)).getPrice(),
                DealState.REMOVED, deals.getDeals().size())
        );
//    merchants.remove(id);
//    merchants.stream().filter(i -> i.getId() >= id)
//            .forEach(merchandise -> merchandise.setId(merchandise.getId() - 1));
        saveData();
    }


    /**
     * Method to find specified Merchandises.
     *
     * @param query query to filter results
     * @return List of Merchandises specified by query
     */
    //add niger name=nikolai age=41 gender=male height=180 weight=80 price=1000
    public List<String> searchMerchandise(String query) {
        Stream<Merchandise> merchandises = merchants.stream();
    /*
  //Dear maintainer:
  //
  // Once you are done trying to 'optimize' this routine,
  // and have realized what a terrible mistake that was,
  // please increment the following counter as a warning
  // to the next guy:
  //
  // total_hours_wasted_here = 42
  */
        if (!Pattern.compile("all", Pattern.CASE_INSENSITIVE).matcher(query.trim()).lookingAt()) {
            String namePattern = "([a-zA-z]+[[0-9]*[a-zA-z]]*)";
            Pattern pattern = Pattern.compile("\\sAND\\s", Pattern.CASE_INSENSITIVE);
            Pattern notEqQuerySplitter = Pattern.compile(namePattern + "(>|>=|<|< =)([\\d]+[.\\d]*)");
            Pattern eqQuerySplitter = Pattern.compile(namePattern + "(=|!=)([\\w]+[.\\w]*)");
            String[] strings = pattern.split(query);
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
                QueryComparator<String, String> finalComparator = createComparator(finalMatcher.group(2),
                        subQuery);
                merchandises = merchandises.filter(merchandise -> {
                    Method[] methods = merchandise.getClass().getMethods();
                    for (Method method : methods) {
                        if (!method.getName().startsWith("get")) {
                            continue;
                        }
                        if (method.getParameterTypes().length != 0) {
                            continue;
                        }
                        if (void.class.equals(method.getReturnType())) {
                            continue;
                        }
                        try {
                            if (method.getName().substring(3).toUpperCase().equals(field)
                                    && null != lastDeal(merchandise)
                                    && lastDeal(merchandise).getState().equals(DealState.FOR_SALE)
                                    && finalComparator.apply(String.valueOf(method.invoke(merchandise)), value)) {
                                return true;
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            System.out.println("Something bad happens sorry.");
                        }
                    }
                    return false;
                });
            }
            return merchandises
                    .sorted(Comparator
                            .comparingInt(Merchandise::getId)
                            .reversed())
                    .map(this::toJson).collect(toList());
        }
        return merchandises.sorted(Comparator
                .comparingInt(Merchandise::getId)
                .reversed())
                .filter(merchandise -> lastDeal(merchandise).getState().equals(DealState.FOR_SALE))
                .map(this::toJson).collect(toList());
    }

    private String toJson(Merchandise merchandise) {
        JsonObject object = new JsonParser()
                .parse(merchandise.getAllInfo())
                .getAsJsonObject();
        Deal deal = lastDeal(merchandise);
        object.add("state",
                new JsonPrimitive(deal.getState().toString()));
        object.add("user",
                new JsonPrimitive(deal.getUser().getUsername()));
        object.add("price",
                new JsonPrimitive(deal.getPrice()));
        return object.toString();
    }

    /**
     * Function to create comparator for subQuery.
     *
     * @param sign sign of subQuery between key an value
     * @return lambda comparator that compare to values and return boolean.
     */
    private QueryComparator<String, String> createComparator(String sign, String query) {
        QueryComparator<String, String> comparator = null;
        if (">".equals(sign)) {
            comparator = (left, right) -> {
                try {
                    return Double.parseDouble(left) > Double.parseDouble(right);
                } catch (NumberFormatException e) {
                    throw new WrongQueryException(query);
                }
            };
        }
        if ("<".equals(sign)) {
            comparator = (left, right) -> {
                try {
                    return Double.parseDouble(left) < Double.parseDouble(right);
                } catch (NumberFormatException e) {
                    throw new WrongQueryException(query);
                }
            };
        }
        if ("<=".equals(sign)) {
            comparator = (left, right) -> {
                try {
                    return Double.parseDouble(left) <= Double.parseDouble(right);
                } catch (NumberFormatException e) {
                    throw new WrongQueryException(query);
                }
            };
        }
        if (">=".equals(sign)) {
            comparator = (left, right) -> {
                try {
                    return Double.parseDouble(left) >= Double.parseDouble(right);
                } catch (NumberFormatException e) {
                    throw new WrongQueryException(query);
                }
            };
        }
        if ("=".equals(sign)) {
            comparator = String::equals;
        }
        if ("!=".equals(sign)) {
            comparator = (left, right) -> !left.equals(right);
        }
        return comparator;
    }

    /**
     * Returns merchandise by id or exception.
     *
     * @param id id of Merchandise
     * @return Founded merchandise or Exception
     */
    @Override
    public synchronized String getMerchantById(int id) throws MerchandiseNotFoundException {
        return merchants.stream().filter(i -> Integer.compare(i.getId(), id) == 0)
                .collect(Collectors.collectingAndThen(toList(), list -> {
                    if (list.size() != 1) {
                        throw new MerchandiseNotFoundException(id);
                    }
                    return list.get(0).getAllInfo();
                }));
    }

    /**
     * Method to find merchandise by id.
     *
     * @param id merchandise's id
     * @return Merchandise
     */
    private Merchandise findMerh(int id) {
        return merchants.stream().filter(i -> Integer.compare(i.getId(), id) == 0)
                .collect(Collectors.collectingAndThen(toList(), list -> {
                    if (list.size() != 1) {
                        throw new MerchandiseNotFoundException(id);
                    }
                    return list.get(0);
                }));
    }

//  private Deal lastDeal(Merchandise merchandise, Expression expression) {
//    deals.getDeals().stream()
//            .filter(deal -> deal.getMerchandise().equals(merchandise)
//                    && expression.execute();
//            .collect(toList());
//  }


    /**
     * Marks merchandise as bought.
     *
     * @param id   unique merchandise identity
     * @param user user who performed action
     * @return bought merchandise
     * @throws MerchandiseNotFoundException throws if merchandise can not be found
     */
    @Override
    public synchronized String buyMerchandise(int id, String user, String token)
            throws MerchandiseNotFoundException {
        if (id >= merchants.size() || id < 0) {
            throw new MerchandiseNotFoundException(id);
        } else {
            Deal deal = lastDeal(findMerh(id));
            if (deal.getState().equals(DealState.FOR_SALE)) {
                deals.addDeal(
                        new Deal(getUser(user, token),
                                deal.getMerchandise(),
                                deal.getPrice(),
                                DealState.BOUGHT,
                                deals.getDeals().size()));
                deals.addDeal(new Deal(
                        deal.getUser(),
                        deal.getMerchandise(),
                        deal.getPrice(),
                        DealState.SOLD,
                        deals.getDeals().size()));
                logger.log(user, "bought merchandise", getMerchantById(id));
                saveData();
                return getMerchantById(id);
            } else {
                Deal lastOne = lastDeal(findMerh(id));
                if (lastOne.getState().equals(DealState.REMOVED)) {
                    throw new MerchandiseAlreadyBought("Merchandise was removed");
                }
                if (lastOne.getState().equals(DealState.BOUGHT)) {
                    throw new MerchandiseAlreadyBought("Merchandise already bought by you");
                }
                throw new MerchandiseAlreadyBought("Before you bought it");
            }
        }

    }

    /**
     * Method to find last deal with {@link Merchandise}.
     *
     * @param merh {@link Merchandise} to find deals with
     * @return last {@link Deal} with merchandise
     */
    private Deal lastDeal(Merchandise merh) {
        List<Deal> deals = this.deals.getDeals();
        for (int i = deals.size() - 1; i >= 0; i--) {
            if (deals.get(i).getMerchandise().equals(merh)
                    && !deals.get(i).getState().equals(DealState.SOLD)) {
                return deals.get(i);
            }
        }
        return null;
    }

    /**
     * Set new values  to merchandise.
     *
     * @param id     id of merchandise to be changed
     * @param params String of parameters with values to change
     * @param user   user who performed action
     */
    public synchronized void setValuesToMerchandise(int id, String params, String user, String token) {
        Map<String, String> kvs = Arrays.stream(params.trim().split(" "))
                .map(elem -> elem.split("="))
                .collect(Collectors.toMap(e -> e[0].toUpperCase(), e -> e[1]));
        String merchInfo = "{Before: " + findMerh(id).getAllInfo() + "},";
        if (!lastDeal(getMerchandise(id)).getUser().getToken().equals(token)) {
            throw new MerchandiseAlreadyBought("That merchandise not yours.");
        }
        if (lastDeal(getMerchandise(id)).getState().equals(DealState.REMOVED)) {
            throw new MerchandiseAlreadyBought("Merchandise was removed.");
        }
        findMerh(id).setParamsByMap(kvs);
        deals.getDeals().forEach(deal -> {
            if (deal.getMerchandise().getId() == id) {
                deal.getMerchandise().setParamsByMap(kvs);
            }
        });
        logger.log(user, "Changed merchandise parameters", merchInfo + " {changed Values:" + kvs + "}");
        saveData();
    }

    private Merchandise getMerchandise(int id) {
        return merchants.stream()
                .filter(merchandise -> merchandise.getId() == id)
                .collect(Collectors.collectingAndThen(toList(), list -> {
                    if (list.size() != 1) {
                        throw new MerchandiseNotFoundException(id);
                    }
                    return list.get(0);
                }));
    }

    /**
     * Method for getting available types of merchandises.
     *
     * @return list of available types for creation
     */
    @Override
    public List<String> getAvailableClasses() {

        Rebound rebound = new Rebound(classesPackage);
        return rebound.getSubClassesOf(Merchandise.class).stream().map(Class::getSimpleName)
                .collect(toList());
    }

    @Override
    public List<String> getMandatoryFields(String className) {
        return Merchandise.getMandatoryFields(className);
    }

    @Override
    public synchronized void addMerchandiseByMap(
            String className,
            Map<String, String> kvs,
            String user, String token,
            int price)
            throws CreateMerchandiseException {
        try {
            Class merchandise = Class.forName(classesPackage + "." + className);
            Merchandise merch = (Merchandise) merchandise.getMethod("buildFromMap", kvs.getClass())
                    .invoke(null, kvs);
            addMerchandise(merch, user, token, price);
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            throw new CreateMerchandiseException(e.getCause().getMessage());
        }
    }

    @Override
    public synchronized String login(String username, String password) {
        return users.stream().filter(user -> user.verifyUser(username, password)).limit(1).map(user -> {
            if (user.verifyUser(username, password) && user.getToken().equals("")) {
                String token = user.generateToken();
                deals.getDeals().forEach(deal -> {
                    if (deal.getUser().verifyUser(username, password)) {
                        deal.getUser().setToken(token);
                    }
                });
                return token;
            } else {
                return "-1";
            }
        }).collect(Collectors.collectingAndThen(toList(), list -> {
            if (list.size() == 1) {
                return list.get(0);
            } else {
                throw new UserException("Wrong login");
            }
        }));
    }

    @Override
    public synchronized boolean register(String username, String pass) {
        List<User> users1 = users.stream()
                .filter(
                        user -> user.verifyUser(username, pass)
                ).collect(toList());
        if (users1.size() != 0) {
            return false;
        }
        users.add(new User(username, pass));
        saveData();
        return true;
    }

    @Override
    public synchronized void disconnect(String username, String token) {
        User user = getUser(username, token);
        if (user.getToken().equals(token)) {
            user.setToken("");
            deals.getDeals().forEach(deal -> {
                if (deal.getUser().getToken().equals(token)) {
                    deal.getUser().setToken("");
                }
            });
        } else
            throw new InvalidToken();
        logger.log(username, "disconnected", "");
    }

    /**
     * Get user by username.
     *
     * @param username username.
     * @return {@link User} with specified username.
     * @throws UserException if user can not be founded.
     */
    private synchronized User getUser(String username) {
        return users.stream().filter(user -> username.equals(user.getUsername())
        ).collect(Collectors.collectingAndThen(toList(), list -> {
            if (list.size() != 1) {
                throw new UserException("Can't find user");
            }
            return list.get(0);
        }));
    }


    /**
     * Get user by username.
     *
     * @param username username.
     * @param token    unique user temporary token
     * @return {@link User} with specified username.
     * @throws UserException if user can not be founded.
     */
    private synchronized User getUser(String username, String token) {
        return users.stream().filter(
                user -> username.equals(user.getUsername())
                        && token.equals(user.getToken())
        ).collect(Collectors.collectingAndThen(toList(), list -> {
            if (list.size() != 1) {
                throw new UserException("Can't find user");
            }
            return list.get(0);
        }));
    }

    /**
     * Method to get all user's deals.
     *
     * @param username user, that make deals.
     * @param token    user's token
     * @return {@link List} of {@link Deal}s
     */
    public synchronized List<String> getDealsByUser(String username, String token) {
        if (getUser(username, token).getToken().equals(token)) {
            return deals.getDeals().stream()
                    .sorted(Collections.reverseOrder())
                    .filter(deal -> deal.getUser().getUsername().equals(username)
                            && deal.getUser().getToken().equals(token))
                    .map(Deal::toString).collect(toList());
        } else {
            throw new InvalidToken();
        }
    }

    @Override
    public synchronized boolean changeLogin(String username, String newLogin, String token) {
        User user = getUser(username, token);
        if (user.getToken().equals(token)) {
            try {
                getUser(newLogin);
                return false;
            } catch (UserException e) {
                user.setUsername(newLogin);
                deals.getDeals().forEach(deal -> {
                    if (deal.getUser().getUsername().equals(username)) {
                        deal.getUser().setUsername(newLogin);
                    }
                });
                saveData();
            }
            return true;
        }
        throw new InvalidToken();
    }

    @Override
    public synchronized void changePassword(String username, String newPassword, String token) {
        User user = getUser(username, token);
        if (user.getToken().equals(token)) {
            user.setPassword(newPassword);
            saveData();
        } else {
            throw new InvalidToken();
        }
    }

    @Override
    public synchronized String getDealById(int id) {
        return deals.getDeals()
                .stream()
                .filter(deal -> deal.getId() == id)
                .collect(Collectors.collectingAndThen(
                        toList(), list -> {
                            if (list.size() != 1) {
                                throw new IllegalArgumentException("can't find deal with id " + id);
                            }
                            return list.get(0).toString();
                        }));
    }

    @Override
    public synchronized boolean exportAllData(String fileName) {
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File(getClass().getResource("/format.xsd").getFile()));
            JAXBContext context = JAXBContext.newInstance(importClasses);
            Marshaller m = context.createMarshaller();
            m.setSchema(schema);
            m.setEventHandler(getHandler());
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(this, new File(fileName));
        } catch (JAXBException | SAXException e) {
            logger.logError(e.getMessage());
            return false;
        }
        return true;
    }


    private ValidationEventHandler getHandler() {
        return event -> {
            System.out.println("\nEVENT");
            System.out.println("SEVERITY:  " + event.getSeverity());
            System.out.println("MESSAGE:  " + event.getMessage());
            System.out.println("LINKED EXCEPTION:  " + event.getLinkedException());
            System.out.println("LOCATOR");
            System.out.println("    LINE NUMBER:  " + event.getLocator().getLineNumber());
            System.out.println("    COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
            System.out.println("    OFFSET:  " + event.getLocator().getOffset());
            System.out.println("    OBJECT:  " + event.getLocator().getObject());
            System.out.println("    NODE:  " + event.getLocator().getNode());
            System.out.println("    URL:  " + event.getLocator().getURL());
            return true;
        };
    }


    @Override
    public synchronized boolean importAllData(String filename) {
        try {

            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File(getClass().getResource("/format.xsd").getFile()));
            JAXBContext context = JAXBContext.newInstance(importClasses);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(getHandler());
            MerchDb db = (MerchDb) unmarshaller.unmarshal(new File(filename));
            int lastMerch = merchants.size();
            int lastDeal = deals.getDeals().size() == 0 ? 0 : deals.getDeals().get(deals.getDeals().size() - 1).getId();
            for (Deal dbDeal : db.deals.getDeals()) {
                Deal newDeal = new Deal(dbDeal);
                try {
                    int oldId = dbDeal.getMerchandise().getId();
                    getMerchandise(oldId);
                    lastMerch++;
                    for (Deal deal : db.deals.getDeals()) {
                        if (deal.getMerchandise().getId() == oldId) {
                            deal.getMerchandise().setId(lastMerch);
                        }
                    }
                    if (dbDeal.getId() <= lastDeal) {
                        lastDeal++;
                        newDeal.setId(lastDeal);
                    }
                } catch (MerchandiseNotFoundException e) {
                    if (dbDeal.getId() <= lastDeal) {
                        lastDeal++;
                        newDeal.setId(lastDeal);
                    }
                }
                deals.addDeal(newDeal);
            }
            db.users.forEach(user -> {
                user.setToken("");
                users.add(user);
            });
        } catch (JAXBException | SAXException e) {
            return false;
        }
        saveData();
        merchants = deals.getDeals()
                .stream()
                .map(Deal::getMerchandise)
                .distinct()
                .sorted(Comparator.comparingInt(Merchandise::getId))
                .collect(toList());
        return true;
    }

}

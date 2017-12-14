package ru.cracker.model.database;

import gigadot.rebound.Rebound;
import ru.cracker.exceptions.*;
import ru.cracker.model.merchandises.Merchandise;

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
public class MerchDb implements Database {

  /**
   * List of merchandise.
   */
  private List<Merchandise> merchants;

  /**
   * Deal manager.
   */
  private DealList deals;

  /**
   * Logger object to log all operations into file.
   */
  private Logger logger = new Logger();

  /**
   * File to write all merchandises.
   */
  private String merchFile = "data.sb";

  /**
   * File to write all deals.
   */
  private String dealFile = "deals.dl";

  /**
   * List of users.
   */
  private List<User> users;

  /**
   * File to write user's list.
   */
  private String userFile = "users.us";

  /**
   * Constructor to create/open database.
   *
   * @param load boolean to load data from file. If true data file
   */
  public MerchDb(boolean load) {
    merchants = new ArrayList<Merchandise>();
    users = new ArrayList<User>();
    deals = new DealList();
    users.add(new User("s3rius", "GYrcN+xQe/XMRDn5sz2whg=="));

    //    generateData(400);
    if (load) {
      loadFromFile();
      unblockAll();
    } else {
      merchFile = "newData.sb";
    }
  }

  private void unblockAll() {
    users.forEach(user -> user.setToken(""));
  }

  /**
   * Method to load data from file.
   */
  private void loadFromFile() {
    merchants = FileManager.readMerchandises(merchFile);
    deals = FileManager.readDeals(dealFile);
    users = FileManager.readUsers(userFile);
  }

  //    /**
  //     * Generate random data for database.
  //     *
  //     * @param quantity quantity of objects to generate.
  //     */
  //    private void generateData(int quantity) {
  //        Random random = new Random();
  //        IntStream.range(0, quantity).forEach(i ->
  //              addMerchandise(Slave.newBuilder().addName(random.nextBoolean() ? "Brian" : random
  //                        .nextBoolean() ? "Julia" : random.nextBoolean() ? "Mark" : "Mary")
  //                        .addAge(random.nextInt(60) + 23)
  //                        .addGender(random.nextBoolean() ? "male" : "female")
  //                        .addPrice(random.nextInt(3000) + 1000)
  //                        .addHeight(random.nextInt(55) + 140)
  //                        .addWeight(random.nextInt(60) + 50)
  //                        .build(), "admin"));
  //
  //    }

  /**
   * Saves all data into file.
   */
  private void saveData() {
    FileManager.writeSerialaizableToFile(merchants, merchFile);
    FileManager.writeSerialaizableToFile(deals, dealFile);
    FileManager.writeSerialaizableToFile(users, userFile);
  }

  /**
   * Puts merch into the vault.
   *
   * @param merch Merch to put in vault
   * @param user  user who performed action
   */
  public void addMerchandise(Merchandise merch, String user, String token, int price) {
    merch.setId(merchants.size());
    merchants.add(merch);
    deals.addDeal(
            new Deal(getUser(user),
                    merch,
                    price,
                    DealState.FOR_SALE,
                    deals.getDeals().size()
            ));
    logger.log(user, "add merchandise", merchants.get(merchants.size() - 1).getAllInfo());
    saveData();
  }

  /**
   * Removes merchandise from database.
   *
   * @param merch Removes merchandise from vault
   * @param user  user who performed action
   */
  public void removeMerchandise(Merchandise merch, String user, String token) {
    int id = merchants.indexOf(merch);
    if (id != -1) {
      merchants.stream().filter(i -> i.getId() >= id)
              .forEach(merchandise -> merchandise.setId(merchandise.getId() - 1));
      deals.addDeal(
              new Deal(
                      getUser(user),
                      merch, lastDeal(merch).getPrice(),
                      DealState.Bought,
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
  public void removeMerchandise(int id, String user, String token) {
    if (id >= merchants.size() || id < 0) {
      throw new MerchandiseNotFoundException(id);
    }
    if (lastDeal(merchants.get(id)).getState().equals(DealState.Bought)) {
      throw new MerchandiseAlreadyBought(id);
    }
    logger.log(user, "removed merchandise", findMerh(id).getAllInfo());
    deals.addDeal(
            new Deal(getUser(user),
                    merchants.get(id),
                    lastDeal(merchants.get(id)).getPrice(),
                    DealState.Bought, deals.getDeals().size())
    );
    merchants.remove(id);
    merchants.stream().filter(i -> i.getId() >= id)
            .forEach(merchandise -> merchandise.setId(merchandise.getId() - 1));
    saveData();
  }


  /**
   * Method to find specified Merchandises.
   *
   * @param querry querry to filter results
   * @return List of Merchandises specified by query
   */
  //add niger name=nikolai age=41 gender=male height=180 weight=80 price=1000
  public List<String> searchMerchandise(String querry) {
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
    if (!Pattern.compile("all", Pattern.CASE_INSENSITIVE).matcher(querry.trim()).lookingAt()) {
      String namePattern = "([a-zA-z]+[[0-9]*[a-zA-z]]*)";
      Pattern pattern = Pattern.compile("\\sAND\\s", Pattern.CASE_INSENSITIVE);
      Pattern notEqQuerySplitter = Pattern.compile(namePattern + "(>|>=|<|< =)([\\d]+[.\\d]*)");
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
                      && null == lastDeal(merchandise)
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
      return merchandises.map(Merchandise::getAllInfo).collect(toList());
    }
    return merchandises.map(Merchandise::getAllInfo).collect(toList());
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
  public String getMerchantById(int id) throws MerchandiseNotFoundException {
    return merchants.stream().filter(i -> Integer.compare(i.getId(), id) == 0)
            .collect(Collectors.collectingAndThen(toList(), list -> {
              if (list.size() != 1) {
                throw new MerchandiseNotFoundException(id);
              }
              Deal deal = lastDeal(list.get(0));
              return list.get(0).getAllInfo() + deal.getState() + deal.getUser().getUsername();
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
  public String buyMerchandise(int id, String user, String token)
          throws MerchandiseNotFoundException {
    if (id >= merchants.size() || id < 0) {
      throw new MerchandiseNotFoundException(id);
    } else {
      Deal deal = lastDeal(findMerh(id));
      if (deal.getState().equals(DealState.FOR_SALE)) {
        deals.addDeal(
                new Deal(getUser(user),
                        deal.getMerchandise(),
                        deal.getPrice(),
                        DealState.Bought,
                        deals.getDeals().size()));
        logger.log(user, "bought merchandise", getMerchantById(id));
        saveData();
        return getMerchantById(id);
      } else {
        throw new MerchandiseAlreadyBought(id);
      }
    }
  }

  private Deal lastDeal(Merchandise merh) {
    List<Deal> deals = this.deals.getDeals();
    for (int i = deals.size() - 1; i >= 0; i--) {
      if (deals.get(i).getMerchandise().equals(merh)) {
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
  public void setValuesToMerchandise(int id, String params, String user, String token) {
    Map<String, String> kvs = Arrays.stream(params.trim().split(" "))
            .map(elem -> elem.split("="))
            .collect(Collectors.toMap(e -> e[0].toUpperCase(), e -> e[1]));
    String merchIfo = "{Before: " + findMerh(id).getAllInfo() + "},";
    findMerh(id).setParamsByMap(kvs);
    logger.log(user, "Changed merchandise parameters", merchIfo + " {changed Values:" + kvs + "}");
    saveData();
  }

  /**
   * Method for getting available types of merchandises.
   *
   * @return list of available types for creation
   */
  @Override
  public List<String> getAvailableClasses() {

    Rebound rebound = new Rebound("ru.cracker.model.merchandises.classes");
    return rebound.getSubClassesOf(Merchandise.class).stream().map(Class::getSimpleName)
            .collect(toList());
  }

  @Override
  public List<String> getMandatoryFields(String className)
          throws WrongClassCallException {
    return Merchandise.getMandatoryFields(className);
  }

  @Override
  public void addMerchandiseByMap(
          String className,
          Map<String, String> kvs,
          String user, String token,
          int price)
          throws CreateMerchandiseException {
    try {
      Class merchandise = Class.forName("ru.cracker.model.merchandises.classes." + className);
      Merchandise merch = (Merchandise) merchandise.getMethod("buildFromMap", kvs.getClass())
              .invoke(null, kvs);
      addMerchandise(merch, user, token, price);
    } catch (ClassNotFoundException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException e) {
      throw new CreateMerchandiseException("Can't create merchandise. Wrong values");
      // throw new IllegalArgumentException("Error while adding. We're sorry");
    }
  }

  @Override
  public String login(String username, String password) {
    return users.stream().filter(user -> user.getUsername().equals(username)).limit(1).map(user -> {
      if (user.verifyPassword(password) && user.getToken().equals("")) {
        return user.generateToken();
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
  public boolean register(String username, String pass) {
    List<User> users1 = users.stream().filter(user -> {
      if (username.equals(user.getUsername())) {
        return true;
      }
      return false;
    }).collect(toList());
    if (users1.size() != 0) {
      return false;
    }
    users.add(new User(username, pass));
    return true;
  }

  @Override
  public void disconnect(String username, String token) {
    User user = getUser(username);
    if (user.getToken().equals(token)) {
      user.setToken("");
    } else
      throw new InvalidToken();
    logger.log(username, "disconnected", "");
  }

  private User getUser(String username) {
    return users.stream().filter(user -> {
      if (username.equals(user.getUsername())) {
        return true;
      }
      return false;
    }).collect(Collectors.collectingAndThen(toList(), list -> {
      if (list.size() != 1) {
        throw new UserException("Can't find user");
      }
      return list.get(0);
    }));
  }

  public List<String> getDealsByUser(String username, String token) {
    if (getUser(username).getToken().equals(token))
      return deals.getDeals().stream()
              .sorted(Collections.reverseOrder())
              .filter(deal -> deal.getUser().getUsername().equals(username))
              .map(Deal::toString).collect(toList());
    else throw new InvalidToken();
  }
}

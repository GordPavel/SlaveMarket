import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import exceptions.CreateMerchandiseException;
import model.Model;
import model.Observable;
import model.SlaveMarketModel;
import model.database.MerchDb;
import model.merchandises.Merchandise;
import model.merchandises.classes.Slave;
import org.junit.Assert;
import org.junit.Test;
import view.Observer;

import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class Tests {
  MerchDb db = new MerchDb(false);
  ArrayList<Slave> slaves = new ArrayList<>();
  String user = "test";
  String token;

  {
    //        Slave slave = Slave.newBuilder().addId(0).addAge(12).addGender("male").addHeight(140).addWeight(35).build();
    //        Slave slave = Slave.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140).addWeight(35).addPrice(100).build();
    //                new Slave(140, 35, 12, "male", 0, "Pete", 100);
    db.register("test", "GYrcN+xQe/XMRDn5sz2whg==");
    token = db.login("test", "GYrcN+xQe/XMRDn5sz2whg==");
    db.addMerchandise(Slave.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140).addWeight(35)
            .addGender("male").build(), user, token, 100);
    db.addMerchandise(Slave.newBuilder().addId(1).addName("Diana").addAge(16).addHeight(174).addWeight(44)
            .addGender("female").build(), user, token, 100);
    db.addMerchandise(Slave.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85)
            .addGender("male").build(), user, token, 100);
    //        db.addMerchandise(new Slave(174, 44, 16, "female", 1, "Diana", 200));
    //        db.addMerchandise(new Slave(185, 85, 20, "male", 2, "Luise", 300));

    //        slaves.add(new Slave(140, 35, 12, "male", 0, "Pete", 100));
    //        slaves.add(new Slave(174, 44, 16, "female", 1, "Diana", 200));
    //        slaves.add(new Slave(185, 85, 20, "male", 2, "Luise", 300));

    slaves.add(Slave.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140).addWeight(35).addGender("male")
            .build());
    slaves.add(Slave.newBuilder().addId(1).addName("Diana").addAge(16).addHeight(174).addWeight(44)
            .addGender("female")
            .build());
    slaves.add(Slave.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85)
            .addGender("male")
            .build());
  }

  @Test
  public void searchMerchandiseTest() {
    Assert.assertEquals(slaves.stream().map(merchandise -> {
      JsonObject object = new JsonParser()
              .parse(merchandise.getAllInfo())
              .getAsJsonObject();
      object.add("state",
              new JsonPrimitive(" on sale "));
      object.add("user",
              new JsonPrimitive("test"));
      object.add("price",
              new JsonPrimitive(100));
      return object.toString();
    })
            .collect(toList()).get(2), db.searchMerchandise("GENDER=male AND NAME=Luise").get(0));
  }

  @Test
  public void searchMerchandiseEuqalsAndGreaterTest() {
    slaves.remove(1);
    slaves.remove(1);
    Assert.assertEquals(slaves.stream().map(merchandise -> {
      JsonObject object = new JsonParser()
              .parse(merchandise.getAllInfo())
              .getAsJsonObject();
      object.add("state",
              new JsonPrimitive(" on sale "));
      object.add("user",
              new JsonPrimitive("test"));
      object.add("price",
              new JsonPrimitive(100));
      return object.toString();
    })
            .collect(toList()), db.searchMerchandise("id=0"));
  }

  @Test
  public void removeMerchandiseTest() {
    db.removeMerchandise(Slave.newBuilder().addId(1).addName("Diana").addAge(16).addHeight(174).addWeight(44)
            .addGender("female").build(), user, token);
    slaves.remove(1);
    ArrayList<Slave> list = new ArrayList<>();
    list.add(Slave.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140).addWeight(35).addGender("male")
            .build());
    list.add(Slave.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85).addGender("male")
            .build());
    Assert.assertEquals(list.stream().sorted(Comparator
            .comparingInt(Merchandise::getId)
            .reversed()).map(merch -> {
      JsonObject object = new JsonParser()
              .parse(merch.getAllInfo())
              .getAsJsonObject();
      object.add("state",
              new JsonPrimitive(" on sale "));
      object.add("user",
              new JsonPrimitive("test"));
      object.add("price",
              new JsonPrimitive(100));
      return object.toString();
    }).collect(toList()), db.searchMerchandise("ALL"));
  }

  @Test
  public void getMerchantByIdTest() {
    JsonObject object = new JsonParser()
            .parse(Slave.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85)
                    .addGender("male").build().getAllInfo())
            .getAsJsonObject();
    Assert.assertEquals(object.toString(), db.getMerchantById(2));
  }

  @Test
  public void removeMerchandiseByIdTest() {
    db.removeMerchandise(1, user, token);
    db.removeMerchandise(0, user, token);
    ArrayList<Slave> list = new ArrayList<>();
    //        list.add(new Slave(185, 85, 20, "male", 1, "Luise", 300));
    list.add(Slave.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85).addGender("male")
            .build());
    Assert.assertEquals(list.stream().sorted(Comparator
            .comparingInt(Merchandise::getId)
            .reversed()).map(merch -> {
      JsonObject object = new JsonParser()
              .parse(merch.getAllInfo())
              .getAsJsonObject();
      object.add("state",
              new JsonPrimitive(" on sale "));
      object.add("user",
              new JsonPrimitive("test"));
      object.add("price",
              new JsonPrimitive(100));
      return object.toString();
    }).collect(toList()), db.searchMerchandise("ALL"));
  }

  @Test
  public void slaveEqualsTest() {
    Assert.assertEquals(true, slaves.get(0)
            .equals(Slave.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140)
                    .addWeight(35).addGender("male").build()));
  }

  @Test
//    @Ignore
  public void addSlaveTest() {
    //        Slave slave = new Slave(1, 2, 3, "male", "David", 320);
    Slave slave = Slave.newBuilder().addName("niger").addWeight(3).addHeight(3).addGender("male")
            .addAge(3).build();
    db.addMerchandise(slave, user, token, 100);
    slaves.add(Slave.newBuilder().addName("niger").addWeight(3).addHeight(3).addGender("male")
            .addId(3).addAge(3).build());
    Assert.assertEquals(slaves.stream().sorted(Comparator
            .comparingInt(Merchandise::getId)
            .reversed()).map(merch -> {
      JsonObject object = new JsonParser()
              .parse(merch.getAllInfo())
              .getAsJsonObject();
      object.add("state",
              new JsonPrimitive(" on sale "));
      object.add("user",
              new JsonPrimitive("test"));
      object.add("price",
              new JsonPrimitive(100));
      return object.toString();
    }).collect(toList()), db.searchMerchandise("ALL"));
  }

  @Test
  public void observerTests() {
    System.out.println("\u001B[32mObserver mechanism\u001B[0m testBlock started{");
    Model model = new SlaveMarketModel();
    model.register("test", "GYrcN+xQe/XMRDn5sz2whg==");
    String token2 = model.login("test", "GYrcN+xQe/XMRDn5sz2whg==");
    Observer observer = new Observer() {

      int assertId = 1;

      {
        ((Observable) model).addObserver(this);
      }

      @Override
      public void update() {
        System.out.println("some data was updated");
      }

      @Override
      public void deleted(int id) {
        System.out.println("item with id=" + id + " was deleted");
        Assert.assertEquals(assertId, id);
      }

      @Override
      public void changed(int id) {
        System.out.println("item with id=" + id + " was changed");
        Assert.assertEquals(assertId, id);
      }
    };
    slaves.forEach(i -> model.addMerchandise(i, user, token2, 100));
    model.removeMerchandise(1, user, token2);
    System.out.println("\u001B[32m}\u001B[0m");
  }

  @Test
  public void queryTest() {
    System.out.println("\u001B[32mQuery test\u001B[0m testBlock started{");
    System.out.println(db.searchMerchandise("id>=0 and id!=1"));
    //        String query = "id<=213";
    Pattern pattern = Pattern.compile("(\\bnot \\b)?[b]*");
    //        Pattern querySplitter = Pattern.compile("([a-zA-z]+[[0-9]*[a-zA-z]]*)(=)([\\w]+[.\\w]*)");
    //        Matcher matcher = pattern.matcher(query);
    //        System.out.println(matcher.lookingAt());
    //        System.out.println(matcher.groupCount());
    ////        System.out.println(matcher.group(2).toUpperCase());
    ////        System.out.println(matcher.group(3).toUpperCase());
    System.out.println("\u001B[32m}\u001B[0m");
  }

  @Test
  public void changeNameTest() {
    db.changeLogin("test", "test2", token);
  }

  @Test
  public void exportTest() {
    db.exportAllData("testExport.xml");
    MerchDb db1 = new MerchDb(false);
    db1.importAllData("testExport.xml");
  }


  @Test
  public void addFood() {
    List<String> fields = db.getMandatoryFields("Food");
    Map<String, String> map = new HashMap<>();
    fields.forEach(field -> map.put(field.toUpperCase(), String.valueOf(new Random().nextFloat())));
    try {
      db.addMerchandiseByMap("Food", map, user, token, 1000);
    } catch (CreateMerchandiseException e) {
      e.printStackTrace();
    }
    db.searchMerchandise("All").forEach(System.out::println);
  }
}

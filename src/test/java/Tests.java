import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import ru.cracker.model.Model;
import ru.cracker.model.Observable;
import ru.cracker.model.SlaveMarketModel;
import ru.cracker.model.database.MerchDb;
import ru.cracker.model.merchandises.Niger;
import ru.cracker.view.Observer;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Tests {
  MerchDb db = new MerchDb();
  ArrayList<Niger> slaves = new ArrayList<>();

  {
    //        Slave slave = Slave.newBuilder().addId(0).addAge(12).addGender("male").addHeight(140).addWeight(35).build();
    //        Slave slave = Slave.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140).addWeight(35).addPrice(100).build();
    //                new Slave(140, 35, 12, "male", 0, "Pete", 100);
    db.addMerchandise(Niger.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140).addWeight(35)
        .addGender("male").addPrice(100).build());
    db.addMerchandise(Niger.newBuilder().addId(1).addName("Diana").addAge(16).addHeight(174).addWeight(44)
        .addGender("female").addPrice(200).build());
    db.addMerchandise(Niger.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85)
        .addGender("male").addPrice(300).build());
    //        db.addMerchandise(new Slave(174, 44, 16, "female", 1, "Diana", 200));
    //        db.addMerchandise(new Slave(185, 85, 20, "male", 2, "Luise", 300));

    //        slaves.add(new Slave(140, 35, 12, "male", 0, "Pete", 100));
    //        slaves.add(new Slave(174, 44, 16, "female", 1, "Diana", 200));
    //        slaves.add(new Slave(185, 85, 20, "male", 2, "Luise", 300));

    slaves.add(Niger.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140).addWeight(35).addGender("male")
        .addPrice(100).build());
    slaves.add(Niger.newBuilder().addId(1).addName("Diana").addAge(16).addHeight(174).addWeight(44).addGender("female")
        .addPrice(200).build());
    slaves.add(Niger.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85).addGender("male")
        .addPrice(300).build());
  }

  @Test
  public void searchMerchandiseTest() {
    Assert.assertEquals(slaves.get(2), db.searchMerchandise("gender=male and name=Luise").get(0));
  }

  @Test
  public void searchMerchandiseEuqalsAndGreaterTest() {
    slaves.remove(1);
    slaves.remove(1);
    Assert.assertEquals(slaves, db.searchMerchandise("id<=0"));
  }

  @Test
  public void removeMerchandiseTest() {
    db.removeMerchandise(Niger.newBuilder().addId(1).addName("Diana").addAge(16).addHeight(174).addWeight(44)
        .addGender("female").addPrice(200).build());
    slaves.remove(1);
    ArrayList<Niger> list = new ArrayList<>();
    list.add(Niger.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140).addWeight(35).addGender("male")
        .addPrice(100).build());
    list.add(Niger.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85).addGender("male")
        .addPrice(300).build());
    Assert.assertEquals(list, db.searchMerchandise(""));
  }

  @Test
  public void getMerchantByIdTest() {
    Assert.assertEquals(Niger.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85)
        .addGender("male").addPrice(300).build(), db.getMerchantById(2));
  }

  @Test
  public void removeMerchandiseByIdTest() {
    db.removeMerchandise(1);
    db.removeMerchandise(0);
    ArrayList<Niger> list = new ArrayList<>();
    //        list.add(new Slave(185, 85, 20, "male", 1, "Luise", 300));
    list.add(Niger.newBuilder().addId(2).addName("Luise").addAge(20).addHeight(185).addWeight(85).addGender("male")
        .addPrice(300).build());
    Assert.assertEquals(list, db.searchMerchandise(""));
  }

  @Test
  public void slaveEqualsTest() {
    Assert.assertEquals(true, slaves.get(0).equals(Niger.newBuilder().addId(0).addName("Pete").addAge(12).addHeight(140)
        .addWeight(35).addGender("male").addPrice(100).build()));
  }

  @Test
  @Ignore
  public void addSlaveTest() {
    //        Slave slave = new Slave(1, 2, 3, "male", "David", 320);
    Niger slave = Niger.newBuilder().addWeight(3).addHeight(3).addGender("male").addPrice(320).addAge(3).build();
    db.addMerchandise(slave);
    slaves.add(Niger.newBuilder().addWeight(3).addHeight(3).addGender("male").addPrice(320).addAge(3).build());
    Assert.assertEquals(slaves, db.searchMerchandise(""));
  }

  @Test
  public void observerTests() {
    System.out.println("\u001B[32mObserver mechanism\u001B[0m testBlock started{");
    Model model = new SlaveMarketModel();
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
    slaves.forEach(model::addMerchandise);
    model.removeMerchandise(1);
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
}

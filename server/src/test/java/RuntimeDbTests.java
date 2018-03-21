import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import exceptions.CreateMerchandiseException;
import exceptions.UserException;
import model.Model;
import model.Observable;
import model.PostgresModel;
import model.SlaveMarketModel;
import model.database.MerchDb;
import model.merchandises.Merchandise;
import model.merchandises.classes.Slave;
import org.apache.log4j.BasicConfigurator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import view.Observer;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RuntimeDbTests {
    private MerchDb db = new MerchDb(false);
    private ArrayList<Slave> slaves = new ArrayList<>();
    private String user = "test";
    private String token;
    private boolean sqlConnection;

    {
        sqlConnection = true;
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
        Assert.assertEquals(slaves.stream().map(this::toJsonObj)
                .collect(toList()).get(2), db.searchMerchandise("GENDER=male AND NAME=Luise").get(0));
    }

    @Test
    public void searchMerchandiseEuqalsAndGreaterTest() {
        slaves.remove(1);
        slaves.remove(1);
        Assert.assertEquals(slaves.stream().map(this::toJsonObj)
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
        toJson(list.stream());
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
        toJson(list.stream());
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
        toJson(slaves.stream());
    }

    private void toJson(Stream<Slave> stream) {
        Assert.assertEquals(stream.sorted(Comparator
                .comparingInt(Merchandise::getId)
                .reversed()).map(this::toJsonObj).collect(toList()), db.searchMerchandise("ALL"));
    }

    private String toJsonObj(Slave merch) {
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
//        Pattern pattern = Pattern.compile("(\\bnot \\b)?[b]*");
        System.out.println("\u001B[32m}\u001B[0m");
    }

    @Test
    public void changeNameTest() {
        db.changeLogin("test", "test2", token);
    }

    @Test
    public void exportTest() {
        testBlock("exportTest{");
        for (int i = 0; i < 123; i++) {
            addMerchandise("Alien");
            addMerchandise("Food");
            addMerchandise("Poison");
            addMerchandise("Slave");
        }
        db.exportAllData("testExport.xml");
        MerchDb db1 = new MerchDb(false);
        db1.importAllData("testExport.xml");
        new File("testExport.xml").delete();
        System.out.println("Test passed successfully");
        testBlock("}");
    }


    @Test
    public void addFood() {
//        List<String> fields = db.getMandatoryFields("Food");
//        Map<String, String> map = new HashMap<>();
//        fields.forEach(field -> map.put(field.toUpperCase(), String.valueOf(new Random().nextFloat())));
//        try {
//            db.addMerchandiseByMap("Food", map, user, token, 1000);
//        } catch (CreateMerchandiseException e) {
//            e.printStackTrace();
//        }
//        db.searchMerchandise("All").forEach(System.out::println);
        addMerchandise("Food");
    }

    @Test
    public void addAlien() {
        addMerchandise("Alien");
    }

    @Test
    public void addPoison() {
        addMerchandise("Poison");
    }

    private void addMerchandise(String merchClass) {
        testBlock("Adding Merchandise{");
        List<String> fields = db.getMandatoryFields(merchClass);
        Map<String, String> map = new HashMap<>();
        fields.forEach(field -> map.put(field.toUpperCase(), String.valueOf(new Random().nextFloat() * 10)));
        try {
            db.addMerchandiseByMap(merchClass, map, user, token, 1000);
        } catch (CreateMerchandiseException e) {
            e.printStackTrace();
        }
        System.out.println(merchClass + " with values:");
        System.out.println(map);
        System.out.println("Added successfully");
        testBlock("}");
    }

    @Test
    public void importTest() {
        testBlock("Import test{");
        MerchDb db = new MerchDb(false);
        List<String> fields = db.getMandatoryFields("Food");
        String user3 = "Christ";
        String pass3 = "12334";
        db.register(user3, pass3);
        String token3 = db.login(user3, pass3);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            fields.forEach(field -> map.put(field.toUpperCase(), String.valueOf(new Random().nextFloat())));
            try {
                db.addMerchandiseByMap("Food", map, user3, token3, 1000);
            } catch (CreateMerchandiseException e) {
                e.printStackTrace();
            }
        }
        db.buyMerchandise(0, user3, token3);
        db.exportAllData("importTest.xml");
        MerchDb db2 = new MerchDb(false);
        fields = db2.getMandatoryFields("Poison");
        String user2 = "Buddha";
        String pass2 = "12334";
        db2.register(user2, pass2);
        String token2 = db2.login(user2, pass2);
        fields.forEach(field -> map.put(field.toUpperCase(), String.valueOf(new Random().nextFloat())));
        try {
            db2.addMerchandiseByMap("Poison", map, user2, token2, 2000);
        } catch (CreateMerchandiseException e) {
            e.printStackTrace();
        }
        db2.importAllData("importTest.xml");
        new File("importTest.xml").delete();
        System.out.println("Test passed successfully");
        testBlock("}");
    }


    /**
     * Method to print colored text.
     *
     * @param text string for color
     */
    private void testBlock(String text) {
        System.out.println("\u001B[32m" + text + "\u001B[0m");
    }


}

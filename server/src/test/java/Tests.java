import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import exceptions.CreateMerchandiseException;
import model.Model;
import model.Observable;
import model.PostgresModel;
import model.SlaveMarketModel;
import model.database.MerchDb;
import model.merchandises.Merchandise;
import model.merchandises.classes.Slave;
import org.junit.Assert;
import org.junit.Test;
import view.Observer;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
            return toJsonObj(merchandise);
        })
                .collect(toList()).get(2), db.searchMerchandise("GENDER=male AND NAME=Luise").get(0));
    }

    @Test
    public void searchMerchandiseEuqalsAndGreaterTest() {
        slaves.remove(1);
        slaves.remove(1);
        Assert.assertEquals(slaves.stream().map(merchandise -> {
            return toJsonObj(merchandise);
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
        for (int i = 0; i < 123; i++) {
            addMerchandise("Alien");
            addMerchandise("Food");
            addMerchandise("Poison");
            addMerchandise("Slave");
        }
        db.exportAllData("testExport.xml");
        MerchDb db1 = new MerchDb(false);
        db1.importAllData("testExport.xml");
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
        List<String> fields = db.getMandatoryFields(merchClass);
        Map<String, String> map = new HashMap<>();
        fields.forEach(field -> map.put(field.toUpperCase(), String.valueOf(new Random().nextFloat() * 10)));
        try {
            db.addMerchandiseByMap(merchClass, map, user, token, 1000);
        } catch (CreateMerchandiseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void importTest() {
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
    }

    @Test
    public void postgresTest() {
//        BasicConfigurator.configure();
//        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
//        Session session = sessionFactory.openSession();
//        Users users = session.get(Users.class, 1);
//        if (null == users) {
//            System.out.println("succ");
//        } else {
//            System.out.println(users.getUsername());
//            System.out.println("succ ess");
//        }
//        StoredProcedureQuery login = session.createStoredProcedureQuery("login")
//                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
//                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
//                .registerStoredProcedureParameter(3, String.class, ParameterMode.OUT)
//                .setParameter(1, "s3rius")
//                .setParameter(2, "19216211");
//        try {
//            Transaction transaction = session.beginTransaction();
//            login.execute();
//            Object result = ((ProcedureCallImpl) login).getOutputs().getOutputParameterValue(3);
//            transaction.commit();
//            System.out.println(result);
//            System.out.println(result.getClass());
//            StoredProcedureQuery logout = session.createNamedStoredProcedureQuery("logout")
//                    .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
//                    .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
//                    .registerStoredProcedureParameter(3, Boolean.class, ParameterMode.OUT)
//                    .setParameter(1, "s3rius")
//                    .setParameter(2, result.toString());
//            transaction = session.beginTransaction();
//            Object body = ((ProcedureCallImpl) logout).getOutputs().getOutputParameterValue(3);
//            transaction.commit();
//            System.out.println(body);
//            System.out.println(body.getClass());
//        } catch (JDBCException e) {
//            SQLException cause = (SQLException) e.getCause();
//            System.out.println(cause.getSQLState());
//            System.out.println("---");
//            System.out.println(cause.getMessage());
//            System.out.println("---");
//            System.out.println(cause.getErrorCode());
//            System.out.println("---");
//            System.out.println(cause.getLocalizedMessage());
//        }
        PostgresModel model = new PostgresModel();
        System.out.println(model.login("s3rius", "19216211"));
    }

}

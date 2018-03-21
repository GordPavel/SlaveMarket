import exceptions.CreateMerchandiseException;
import exceptions.UserException;
import model.PostgresModel;
import org.apache.log4j.BasicConfigurator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PostgresqlTests {
    private static boolean connect = true;
    private PostgresModel model;
    private String user = "s2rius";
    private String pasword = "19216211";

    {
        try {
            model = new PostgresModel();
        } catch (Throwable e) {
            connect = false;
        }
    }

    /**
     * Method to print colored text.
     *
     * @param text string for color
     */
    private void testBlock(String text) {
        System.out.println("\u001B[32m" + text + "\u001B[0m");
    }

    /**
     * Testing connection
     */
    @Test
    public void postgresConnectionTest() {
        testBlock("Postgresql connection test{");
        BasicConfigurator.configure();
        try {
            SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            Session session = sessionFactory.openSession();
            System.out.println("Connection established");

        } catch (Throwable e) {
            System.out.print("Can't connect database ");
            testBlock("Skipping...");
            connect = false;
        }
        testBlock("}");

    }

    /**
     * Test of model to connect postgresql.
     */
    @Test
    public void postgresqlModelTest() {
        testBlock("PostgresqlModel test{");
        if (connect) {
            String token = model.login(user, pasword);
            System.out.println(token);
            model.disconnect(user, token);
            try {
                model.register("s3rius", "19216211");
            } catch (UserException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.print("Can't connect database ");
            testBlock("Skipping...");
        }
        testBlock("}");
    }

    @Test
    public void selectTest() {
        testBlock("selectTest{");
        if (connect) {
            System.out.println(model.getMerchantById(1));
        } else {
            System.out.print("Can't connect database ");
            testBlock("Skipping...");
        }
        testBlock("}");
    }

    @Test
    public void searchTest() {
        testBlock("searchTest{");
        if (connect) {
            model.searchMerchandise("li").forEach(System.out::println);
        } else {
            System.out.print("Can't connect database ");
            testBlock("Skipping...");
        }
        testBlock("}");
    }

    @Test
    public void fieldsTest() {
        testBlock("fieldTest{");
        if (connect) {
            String token = model.login(user, pasword);
            try {
                List<String> classes = model.getAvailableClasses();
                System.out.println(classes);
                for (int i = 0; i < 1; i++) {
                    classes.forEach(o -> addMerchandise(o, token));
                }
//                addMerchandise("slaves", token);
            } catch (Exception e) {
                e.printStackTrace();
            }
            model.disconnect(user, token);
        } else {
            System.out.print("Can't connect database ");
            testBlock("Skipping...");
        }
        testBlock("}");
    }

    private void addMerchandise(String merchClass, String token) {
        testBlock("Adding Merchandise{");
        List<String> fields = model.getMandatoryFields(merchClass);
        String[] names = new String[]{
                "Josef", "Julia", "Mark", "Siina",
                "Roman", "Carl", "Akito", "Liara",
                "Greg", "John", "Anabel", "Violet",
                "Misato", "Tom", "Linda", "Garry",
                "Paul", "Andrew", "Noise", "kris"
        };
        String[] planets = new String[]{
                "Mercury", "Venus", "Earth", "Moon",
                "Mars", "Phobos", "Deimos", "Eros",
                "Gaspra", "Jupiter", "Ganymede", "Callisto",
                "Amalthea", "Himalia", "Pasiphae", "Dia",
                "Herse", "Saturn", "Phoebe", "Uranus",
                "Cordelia", "Neptune", "Thalassa", "Despina",
                "Galatea", "Pluto"
        };
        String[] colors = new String[]{
                "Red", "Orange", "Yellow", "Green",
                "Cyan", "Blue", "Indigo", "Violet"
        };
        String[] genders = new String[]{
                "Male", "Female", "Abimegender", "Adamasgender",
                "Aerogender", "Agender", "Transmission", "Trebuchet", "Apache",
                "Exgender", "Femfluid", "Genderblank", "Genderflow", "Neutrois"
        };
        String[] races = new String[]{
                "Asari", "Drell", "Elcor",
                "Hanar", "Keeper", "Salarian",
                "Turian", "Volu", "Batarian",
                "Collector", "Geth", "Quarian",
                "Krogan", "Quarian", "Leviathan",
                "Reaper", "Yahg", "Vorcha"
        };
        Map<String, String> map = new HashMap<>();
        fields.forEach(field -> {
            if (field.equals("name")) {
                map.put(field.toUpperCase(), names[abs(new Random().nextInt()) % names.length]);
            } else if (field.equals("planet")) {
                map.put(field.toUpperCase(), planets[abs(new Random().nextInt()) % planets.length]);
            } else if (field.equals("color")) {
                map.put(field.toUpperCase(), colors[abs(new Random().nextInt()) % colors.length]);
            } else if (field.equals("gender")) {
                map.put(field.toUpperCase(), genders[abs(new Random().nextInt()) % genders.length]);
            } else if (field.equals("race")) {
                map.put(field.toUpperCase(), races[abs(new Random().nextInt()) % races.length]);
            } else if (field.equals("height")) {
                map.put(field.toUpperCase(), String.valueOf(160 + abs(new Random().nextInt()) % 40));
            } else if (field.equals("weight")) {
                map.put(field.toUpperCase(), String.valueOf(60 + abs(new Random().nextInt()) % 35));
            } else {
                map.put(field.toUpperCase(), String.valueOf((new Random().nextFloat() + 2) * 10));
            }
        });
        try {
            model.addMerchandiseByMap(merchClass, map, user, token, (abs(new Random().nextInt())));
        } catch (CreateMerchandiseException e) {
            e.printStackTrace();
        }
        testBlock("}");
    }

    @Test
    public void getDealTest() {
        testBlock("get deal by id Test block{");
        if (connect) {
            model.getDealById(1);
        } else {
            System.out.print("Can't connect database ");
            testBlock("Skipping...");
        }
        testBlock("}");
    }

    @Test
    public void getDealsTest() {
        testBlock("get deal by id Test block{");
        if (connect) {
            String token = model.login(user, pasword);
            model.getDealsByUser(user, token);
            model.disconnect(user, token);
        } else {
            System.out.print("Can't connect database ");
            testBlock("Skipping...");
        }
        testBlock("}");
    }
}

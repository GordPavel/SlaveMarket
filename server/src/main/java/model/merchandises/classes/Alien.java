package model.merchandises.classes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import exceptions.WrongQueryException;
import model.merchandises.SlaveInterface;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "alien")
@XmlAccessorType(XmlAccessType.FIELD)
public class Alien implements SlaveInterface {
    private String planet;
    private Float weight;
    private Float height;
    private String color;
    private String name;
    private int age;
    private String race;
    private int id;
    private String className = "alien";

    /**
     * returns new instance of Slave created by map.
     *
     * @param map map for creating. Where key - field Name, value - field value.
     * @return new Instance of class made by map.
     */
    public static Alien buildFromMap(HashMap<String, String> map) {
        // This is crap code but it’s 3 a.m. and I need to get this working
        Alien slave = new Alien();
        List<String> requiredFields = mandatoryFields();
        for (String s : requiredFields) {
            if (!map.containsKey(s.toUpperCase())) {
                throw new WrongQueryException("Missed key \"" + s + "\"");
            }
        }
        try {
            slave.planet = map.get("PLANET");
            slave.race = map.get("RACE");
            slave.age = parseAndCheck("AGE", map.get("AGE")).intValue();
            slave.weight = parseAndCheck("WEIGHT", map.get("WEIGHT"));
            slave.height = parseAndCheck("HEIGHT", map.get("HEIGHT"));
            slave.color = map.get("COLOR");
            slave.name = map.get("NAME");
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return slave;
    }

    public static List<String> mandatoryFields() {
        return Arrays.asList("Name", "Age", "Race", "Planet", "Height", "Weight", "Color");
    }

    /**
     * Method to check and parse values that comes out from map.
     *
     * @param value value with integer or float.
     * @return value from string in float if it's >=1
     */
    public static Float parseAndCheck(String key, String value) {
        Float dValue = Food.getaFloat(key, value);
        return dValue;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getGender() {
        return "undefined";
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public float getBenefit() {
        return ((height * weight) / age) % 100;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns all information about merchandise.
     *
     * @return Complicated string of merchandise info. Formatted like "ClassName Param1:Value1 etc.
     */
    public String getAllInfo() {
        JsonObject info = new JsonObject();
        info.add("class", new JsonPrimitive("Alien"));
        info.add("id", new JsonPrimitive(id));
        info.add("name", new JsonPrimitive(name));
        info.add("planet", new JsonPrimitive(planet));
        info.add("color", new JsonPrimitive(color));
        info.add("race", new JsonPrimitive(race));
        info.add("age", new JsonPrimitive(age));
        info.add("height", new JsonPrimitive(height));
        info.add("weight", new JsonPrimitive(weight));
        info.add("benefit", new JsonPrimitive(getBenefit()));
        return info.toString();
    }

    /**
     * Set up object params with map values.
     *
     * @param map of params. Where key is field name and value is field value.
     */
    public void setParamsByMap(Map<String, String> map) {
        List<String> fields = mandatoryFields();
        map.forEach((key, value) -> {
            boolean contains = false;
            for (String field : fields) {
                if (field.toUpperCase().equals(key.toUpperCase())) {
                    contains = true;
                }
            }
            if (!contains) {
                throw new WrongQueryException(key + "=" + value);
            }
            switch (key) {
                case "PLANET":
                    this.planet = value;
                    break;
                case "AGE":
                    this.age = parseAndCheck(key, value).intValue();
                    break;
                case "WEIGHT":
                    this.weight = parseAndCheck(key, value);
                    break;
                case "HEIGHT":
                    this.height = parseAndCheck(key, value);
                    break;
                case "NAME":
                    this.name = value;
                    break;
                case "COLOR":
                    this.color = value;
                    break;
                case "RACE":
                    this.race = value;
                default:
                    break;
            }
        });
    }

    @Override
    public int hashCode() {
        return id
                + name.hashCode()
                + race.hashCode()
                + planet.hashCode()
                + color.hashCode()
                + age
                + Float.floatToIntBits(weight)
                + Float.floatToIntBits(height);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().getName().equals(this.getClass().getName())
                && ((Alien) obj).getAllInfo().equals(getAllInfo());
    }

    public String getClassName() {
        return className;
    }
}
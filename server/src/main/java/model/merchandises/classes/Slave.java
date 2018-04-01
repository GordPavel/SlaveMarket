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

/**
 * Realization of SlaveInterface. Model of Slave. Slave is a person who is the property of and
 * wholly subject to another; a bond servant.
 */
@XmlRootElement(name = "slave")
@XmlAccessorType(XmlAccessType.FIELD)
public class Slave implements SlaveInterface {

    /**
     * Human height or stature is the distance from the bottom of the feet to the top of the head in a
     * human body, standing erect.
     */
    private float height;

    /**
     * The term human body weight is used colloquially and in the biological and medical sciences to
     * refer to a person's mass or weight.
     */
    private float weight;
    /**
     * The measure to calculate human ageing. Where the Ageing or aging (see spelling differences) is
     * the process of becoming older.
     */
    private int age;
    /**
     * Gender is the range of characteristics pertaining to, and differentiating between, masculinity
     * and femininity. if isMale is true true  slave is a male, or else she's a female.
     */
    private String gender;
    /**
     * Names can identify a class or category of things, or a single thing, either uniquely, or within
     * a given context.
     */
    private String name;
    /**
     * An identifier is a name that identifies (that is, labels the identity of) either a unique
     * slave.
     */
    private int id;

    private String className = "Slave";

    /**
     * Default constructor.
     */
    private Slave() {
    }

    public static Builder newBuilder() {
        return new Slave().new Builder();
    }

    /**
     * returns new instance of Slave created by map.
     *
     * @param map map for creating. Where key - field Name, value - field value.
     * @return new Instance of class made by map.
     */
    public static Slave buildFromMap(HashMap<String, String> map) {
        // This is crap code but it’s 3 a.m. and I need to get this working
        Slave slave = new Slave();
        List<String> requiredFields = mandatoryFields();
        for (String s : requiredFields) {
            if (!map.containsKey(s.toUpperCase())) {
                throw new WrongQueryException("Missed key \"" + s + "\"");
            }
        }
        try {
            slave.age = parseAndCheck("AGE", map.get("AGE")).intValue();
            slave.weight = parseAndCheck("WEIGHT", map.get("WEIGHT"));
            slave.height = parseAndCheck("HEIGHT", map.get("HEIGHT"));
            slave.gender = map.get("GENDER");
            slave.name = map.get("NAME");
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return slave;
    }

    /**
     * Method to list all of params required to create object. And that fields can be changed.
     *
     * @return list of strings with minimum required fields for creating instance of class
     */
    public static List<String> mandatoryFields() {
        return Arrays.asList("Name", "Gender", "Height", "Weight", "Age");
    }

    /**
     * Method to check and parse values that comes out from map.
     *
     * @param value value with integer or float.
     * @return value from string in float if it's >=1
     */
    private static Float parseAndCheck(String key, String value) {
        Float dValue;
        try {
            dValue = Float.parseFloat(value);
            if (dValue <= 0) {
                JsonObject mistake = new JsonObject();
                mistake.add("errorType", new JsonPrimitive("Must be grater than 0"));
                mistake.add("errorKey", new JsonPrimitive(key));
                throw new IllegalArgumentException(mistake.toString());
            }
        } catch (Exception e) {
            JsonObject mistake = new JsonObject();
            mistake.add("errorType", new JsonPrimitive("Can't parse"));
            mistake.add("errorKey", new JsonPrimitive(key));
            throw new IllegalArgumentException(mistake.toString());
        }
        return dValue;
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
                case "AGE":
                    this.age = parseAndCheck(key, value).intValue();
                    break;
                case "WEIGHT":
                    this.weight = parseAndCheck(key, value);
                    break;
                case "HEIGHT":
                    this.height = parseAndCheck(key, value);
                    break;
                case "GENDER":
                    this.gender = value;
                    break;
                case "NAME":
                    this.name = value;
                    break;
                default:
                    break;
            }
        });
    }


    /**
     * Returns merchandise quality in percentage.
     */
    public float getBenefit() {
        return (height * weight / age) % 100;
    }

    /**
     * Returns all information about merchandise.
     *
     * @return Complicated string of merchandise info. Formatted like "ClassName Param1:Value1 etc.
     */
    public String getAllInfo() {
        JsonObject info = new JsonObject();
        info.add("class", new JsonPrimitive("Slave"));
        info.add("id", new JsonPrimitive(id));
        info.add("name", new JsonPrimitive(name));
        info.add("gender", new JsonPrimitive(gender));
        info.add("age", new JsonPrimitive(age));
        info.add("height", new JsonPrimitive(height));
        info.add("weight", new JsonPrimitive(weight));
        info.add("benefit", new JsonPrimitive(getBenefit()));
        return info.toString();
    }

    @Override
    public String toString() {
        return "Slave Slave id:" + id + " height:" + height + " weight:" + weight + " age:" + age
                + " gender:" + gender
                + " name:" + name + " benefit:" + getBenefit();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().getName().equals(this.getClass().getName())
                && ((Slave) obj).getAllInfo().equals(getAllInfo());
    }

    @Override
    public int hashCode() {
        return id
                + name.hashCode()
                + gender.hashCode()
                + age
                + Float.floatToIntBits(weight)
                + Float.floatToIntBits(height);
    }

    /**
     * get Merchandise's id.
     *
     * @return id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * method to set new id to merchandise.
     *
     * @param id new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * returns slave's height.
     *
     * @return slave's height
     */
    public float getHeight() {
        return height;
    }

    /**
     * returns slave's weight.
     *
     * @return slave's weight
     */
    public float getWeight() {
        return weight;
    }

    /**
     * returns slave's age.
     *
     * @return slave's age
     */
    public int getAge() {
        return age;
    }

    /**
     * returns slave's gender.
     *
     * @return returns slave's gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * returns slave's name.
     *
     * @return slave's name
     */
    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }


    public class Builder {

        private Builder() {
        }

        /**
         * Sets up height to new Object.
         *
         * @param height new Object's height
         */
        public Builder addHeight(int height) {
            Slave.this.height = height;
            return this;
        }

        /**
         * Sets up weight to new Object.
         *
         * @param weight new Object's weight
         */
        public Builder addWeight(int weight) {
            Slave.this.weight = weight;
            return this;
        }

        /**
         * Sets up age to new Object.
         *
         * @param age new Object's age
         */
        public Builder addAge(int age) {
            Slave.this.age = age;
            return this;
        }

        /**
         * Setup gender in new Object.
         *
         * @param gender new Object's gender
         */
        public Builder addGender(String gender) {
            Slave.this.gender = gender;
            return this;
        }

        /**
         * Sets up age to new Object.
         *
         * @param id new Object's id
         */
        public Builder addId(int id) {
            Slave.this.id = id;
            return this;
        }

        /**
         * Build slave with already specified parameters.
         *
         * @return Instance of new object
         */
        public Slave build() {
            Slave slave = new Slave();
            slave.name = name;
            slave.id = id;
            slave.gender = gender;
            slave.age = age;
            slave.weight = weight;
            slave.height = height;
            return slave;
        }

        /**
         * Method to build instance of Slave with some default values.
         *
         * @return new Instance of Slave
         */
        public Slave buildDefault() {
            Slave slave = new Slave();
            slave.name = "";
            slave.id = -1;
            slave.gender = "";
            slave.age = -1;
            slave.weight = -1;
            slave.height = -1;
            return slave;
        }

        /**
         * Sets up name to new Object.
         *
         * @param name new Object's name
         */
        public Builder addName(String name) {
            Slave.this.name = name;
            return this;
        }

    }

}
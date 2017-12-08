package ru.cracker.model.merchandises.classes;

import ru.cracker.exceptions.MerchandiseAlreadyBought;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.SlaveInterface;

import java.util.*;

/**
 *
 */
public class Slave implements SlaveInterface {

    /**
     * Human height or stature is the distance from the bottom of the feet to the top of the head in a human body, standing erect.
     */
    private float height;
    /**
     * A parameter indicating whether there are goods in stock.
     */
    private boolean bought;
    /**
     * The term human body weight is used colloquially and in the biological and medical sciences to refer to a person's mass or weight.
     */
    private float weight;
    /**
     * The mesuare to calculate human ageing.
     * Where the Ageing or aging (see spelling differences) is the process of becoming older.
     */
    private int age;
    /**
     * Gender is the range of characteristics pertaining to, and differentiating between, masculinity and femininity. if isMale is true true  slave is a male, or else she's a female.
     */
    private String gender;
    /**
     *
     */
    private String name;
    /**
     * An identifier is a name that identifies (that is, labels the identity of) either a unique slave.
     */
    private int id;
    /**
     * In modern economies, prices are generally expressed in units of some form of currency.
     * Although prices could be quoted as quantities of other goods or services this sort
     * of barter exchange is rarely seen.
     */
    private int price;
    private String boughtBy = "";

    /**
     * Default constructor
     */
    private Slave() {
    }

    /**
     * Returns new Builder instance for adding parameters in new slave
     *
     * @return new builder with method for creating new instance
     */
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
            if (!map.containsKey(s.toUpperCase()))
                throw new WrongQueryException("Missed key \"" + s + "\"");
        }
        try {
            slave.age = Integer.parseInt(map.get("AGE"));
            slave.weight = Float.parseFloat(map.get("WEIGHT"));
            slave.height = Float.parseFloat(map.get("HEIGHT"));
            slave.price = Integer.parseInt(map.get("PRICE"));
            slave.gender = map.get("GENDER");
            slave.name = map.get("NAME");
            slave.bought = false;
        } catch (Exception e) {
            throw new WrongQueryException(e.getMessage());
        }
        return slave;
    }

    /**
     * Method to list all of params required to create object. And that fields can be changed.
     *
     * @return list of strings with minimum required fields for creating instance of class
     */
    public static List<String> mandatoryFields() {
        return Arrays.asList("Name", "Gender", "Height", "Weight", "Age", "price");
    }

    /**
     * Set up object params with map values
     *
     * @param map of params. Where key is field name and value is field value.
     */
    public void setParamsByMap(Map<String, String> map) {
        if (!bought) {
            List<String> fields = mandatoryFields();
            map.forEach((key, value) -> {
                boolean contains = false;
                for (String field : fields) {
                    if (field.equals(key.toUpperCase())) {
                        contains = true;
                    }
                }
                if (!contains)
                    throw new WrongQueryException(key + "=" + value);
                switch (key) {
                    case "AGE":
                        this.age = Integer.parseInt(value);
                        break;
                    case "WEIGHT":
                        this.weight = Float.parseFloat(value);
                        break;
                    case "HEIGHT":
                        this.height = Float.parseFloat(value);
                        break;
                    case "PRICE":
                        this.price = Integer.parseInt(value);
                        break;
                    case "GENDER":
                        this.gender = value;
                        break;
                    case "NAME":
                        this.name = value;
                        break;
                }
            });
        } else throw new MerchandiseAlreadyBought(id);
    }

    /**
     * Method mark merchandise as bought
     *
     * @return is this bought success?
     */
    public boolean buy(String user) {
        if (bought) {
            throw new MerchandiseAlreadyBought(id);
        }
        bought = true;
        boughtBy = user;
        return true;
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
     * @return Complicated string of merchandise info.
     * Formatted like "ClassName Param1:Value1 Param2:Value2 ..."
     */
    public String getAllInfo() {
        return "Slave Slave id:" + id + " height:" + height + " weight:" + weight + " age:" + age + " gender:" + gender
                + " name:" + name + " benefit:" + getBenefit() + " price:" + price;
    }

    @Override
    public String toString() {
        return "Slave Slave id:" + id + " height:" + height + " weight:" + weight + " age:" + age + " gender:" + gender
                + " name:" + name + " benefit:" + getBenefit() + " price:" + price;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().getName().equals(this.getClass().getName())) {
            if (((Slave) obj).name.equals(name) && ((Slave) obj).gender.equals(gender)
                    && Objects.equals(((Slave) obj).getBenefit(), getBenefit()) && ((Slave) obj).age == age
                    && ((Slave) obj).weight == weight && ((Slave) obj).bought == bought && ((Slave) obj).price == price
                    && ((Slave) obj).boughtBy.equals(boughtBy)) {
                return true;
            }
        }
        return false;
    }

    /**
     * get Merchandise's id
     */
    public int getId() {
        return id;
    }

    /**
     * method to set new id to mercandise
     *
     * @param id new id
     * @return
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * returns slave's height
     *
     * @return slave's height
     */
    public float getHeight() {
        return height;
    }

    /**
     * returns slave's weight
     *
     * @return slave's weight
     */
    public float getWeight() {
        return weight;
    }

    /**
     * returns slave's age
     *
     * @return slave's age
     */
    public int getAge() {
        return age;
    }

    /**
     * returns slave's gender
     *
     * @return returns slave's gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * returns slave's name
     *
     * @return slave's name
     */
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    /**
     * Returns merchandise boughtFlag.
     *
     * @return true if merchandise already bought.
     */
    public boolean isBought() {
        return bought;
    }


    /**
     * Builder class for realiztion of builder pattern.
     */
    public class Builder {

        /**
         * Default constructor
         */
        private Builder() {
        }

        /**
         * Sets up height to new Object
         *
         * @param height new Object's height
         * @return
         */
        public Builder addHeight(int height) {
            Slave.this.height = height;
            return this;
        }

        /**
         * Sets up weight to new Object
         *
         * @param weight new Object's weight
         * @return
         */
        public Builder addWeight(int weight) {
            Slave.this.weight = weight;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param age new Object's age
         * @return
         */
        public Builder addAge(int age) {
            Slave.this.age = age;
            return this;
        }

        /**
         * Setup gender in new Object
         *
         * @param gender new Object's gender
         * @return
         */
        public Builder addGender(String gender) {
            Slave.this.gender = gender;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param price new Object's price
         * @return
         */
        public Builder addPrice(int price) {
            Slave.this.price = price;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param id new Object's id
         * @return
         */
        public Builder addId(int id) {
            Slave.this.id = id;
            return this;
        }

        /**
         * Build slave with already specified parametres.
         *
         * @return Instance of new object
         */
        public Slave build() {
            Slave slave = new Slave();
            slave.name = Slave.this.name;
            slave.id = Slave.this.id;
            slave.gender = Slave.this.gender;
            slave.price = Slave.this.price;
            slave.age = Slave.this.age;
            slave.weight = Slave.this.weight;
            slave.height = Slave.this.height;
            return slave;
        }

        public Slave buildDefault() {
            Slave slave = new Slave();
            slave.name = "";
            slave.id = -1;
            slave.gender = "";
            slave.price = 0;
            slave.age = -1;
            slave.weight = -1;
            slave.height = -1;
            return slave;
        }

        /**
         * Sets up name to new Object
         *
         * @param name new Object's name
         * @return
         */
        public Builder addName(String name) {
            Slave.this.name = name;
            return this;
        }

    }

}

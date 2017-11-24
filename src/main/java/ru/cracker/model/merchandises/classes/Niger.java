package ru.cracker.model.merchandises.classes;

import ru.cracker.exceptions.MerchandiseAlreadyBought;
import ru.cracker.exceptions.WrongQueryException;
import ru.cracker.model.merchandises.SlaveInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class Niger implements SlaveInterface {

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
    private Niger() {
    }

    /**
     * Returns new Builder instance for adding parameters in new slave
     *
     * @return new builder with method for creating new instance
     */
    public static Builder newBuilder() {
        return new Niger().new Builder();
    }

    /**
     * returns new instance of Slave created by map.
     *
     * @param map map for creating. Where key - field Name, value - field value.
     * @return new Instance of class made by map.
     */
    public static Niger buildFromMap(HashMap<String, String> map) {
        Niger niger = new Niger();
        String[] requiredFields = new String[]{"AGE", "WEIGHT", "HEIGHT", "PRICE", "GENDER", "NAME"};
        for (String s : requiredFields) {
            if (!map.containsKey(s))
                throw new WrongQueryException("Missed key \"" + s + "\"");
        }
        try {
            niger.age = Integer.parseInt(map.get("AGE"));
            niger.weight = Float.parseFloat(map.get("WEIGHT"));
            niger.height = Float.parseFloat(map.get("HEIGHT"));
            niger.price = Integer.parseInt(map.get("PRICE"));
            niger.gender = map.get("GENDER");
            niger.name = map.get("NAME");
            niger.bought = false;
        } catch (Exception e) {
            throw new WrongQueryException(e.getMessage());
        }
        return niger;
    }

    /**
     * Set up object params with map values
     *
     * @param map of params. Where key is field name and value is field value.
     */
    public void setParamsByMap(Map<String, String> map) {
        if (!bought) {
            String[] fields = new String[]{"AGE", "WEIGHT", "HEIGHT", "PRICE", "GENDER", "NAME"};
            map.forEach((key, value) -> {
                boolean contains = false;
                for (String field : fields) {
                    if (field.equals(key)) {
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
        return "Slave Niger id:" + id + " height:" + height + " weight:" + weight + " age:" + age + " gender:" + gender
                + " name:" + name + " benefit:" + getBenefit();
    }

    @Override
    public String toString() {
        return "Slave Niger id:" + id + " height:" + height + " weight:" + weight + " age:" + age + " gender:" + gender
                + " name:" + name + " benefit:" + getBenefit() + " price:" + price;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().getName().equals(this.getClass().getName())) {
            if (((Niger) obj).name.equals(name) && ((Niger) obj).gender.equals(gender)
                    && Objects.equals(((Niger) obj).getBenefit(), getBenefit()) && ((Niger) obj).age == age
                    && ((Niger) obj).weight == weight && ((Niger) obj).bought == bought && ((Niger) obj).price == price
                    && ((Niger) obj).boughtBy.equals(boughtBy)) {
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
            Niger.this.height = height;
            return this;
        }

        /**
         * Sets up weight to new Object
         *
         * @param weight new Object's weight
         * @return
         */
        public Builder addWeight(int weight) {
            Niger.this.weight = weight;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param age new Object's age
         * @return
         */
        public Builder addAge(int age) {
            Niger.this.age = age;
            return this;
        }

        /**
         * Setup gender in new Object
         *
         * @param gender new Object's gender
         * @return
         */
        public Builder addGender(String gender) {
            Niger.this.gender = gender;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param price new Object's price
         * @return
         */
        public Builder addPrice(int price) {
            Niger.this.price = price;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param id new Object's id
         * @return
         */
        public Builder addId(int id) {
            Niger.this.id = id;
            return this;
        }

        /**
         * Build slave with already specified parametres.
         *
         * @return Instance of new object
         */
        public Niger build() {
            Niger niger = new Niger();
            niger.name = Niger.this.name;
            niger.id = Niger.this.id;
            niger.gender = Niger.this.gender;
            niger.price = Niger.this.price;
            niger.age = Niger.this.age;
            niger.weight = Niger.this.weight;
            niger.height = Niger.this.height;
            return niger;
        }

        public Niger buildDefault() {
            Niger niger = new Niger();
            niger.name = "";
            niger.id = -1;
            niger.gender = "";
            niger.price = 0;
            niger.age = -1;
            niger.weight = -1;
            niger.height = -1;
            return niger;
        }

        /**
         * Sets up name to new Object
         *
         * @param name new Object's name
         * @return
         */
        public Builder addName(String name) {
            Niger.this.name = name;
            return this;
        }

    }

}

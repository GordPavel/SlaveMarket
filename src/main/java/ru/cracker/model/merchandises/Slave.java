package ru.cracker.model.merchandises;

import java.util.Objects;

/**
 * A slave is unable to withdraw unilaterally from such an arrangement and works without remuneration.
 * Many scholars now use the term chattel slavery to refer to this specific sense of legalised, de jure slavery.
 */
public class Slave implements Merchandise {

    /**
     * Human height or stature is the distance from the bottom of the feet to the top of the head in a human body,
     * standing erect.
     */
    private int height;
    /**
     * The term human body weight is used colloquially and in the biological and medical
     * sciences to refer to a person's mass or weight.
     */
    private int weight;
    /**
     * The measure to calculate human ageing.
     * Where the Ageing or aging (see spelling differences) is the process of becoming older.
     */
    private int age;
    /**
     * An identifier is a name that identifies (that is, labels the identity of) either a unique slave.
     */
    private int id;
    /**
     * Gender is the range of characteristics pertaining to, and differentiating between,
     * masculinity and femininity. if isMale is true true  slave is a male, or else she's a female.
     */
    private String gender;
    /**
     * Productivity describes various measures of the efficiency of production.
     * A productivity measure is expressed as the ratio of output to inputs used in a production process.
     */

    private Double benefit;
    /**
     * A parameter indicating whether there are goods in stock.
     */

    private boolean bought;
    /**
     * A name is a term used for identification.
     * Names can identify a class or category of things,
     * or a single thing, either uniquely, or within a given context.
     */
    private String name = "unnamed";

    /**
     * In modern economies, prices are generally expressed in units of some form of currency.
     * Although prices could be quoted as quantities of other goods or services this sort
     * of barter exchange is rarely seen.
     */
    private int price;


    /**
     * Initial constructor for Slave.
     *
     * @param height  height of a new slave
     * @param weight  weight of a new slave
     * @param age     age of a new slave
     * @param gender  Gender of a new slave.
     * @param id      slave's unique identification
     * @param benefit How effective he can work in percentage
     * @param price   slave's cost
     */
    private Slave(int height, int weight, int age, String gender, int id, Double benefit, String name, int price) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.id = id;
        this.name = name;
        this.benefit = benefit;
        this.price = price;
    }

    /**
     * Constructor of a new Slave with autocalculate productivity.
     *
     * @param height height of a new slave
     * @param weight weight of a new slave
     * @param age    age of a new slave
     * @param gender Gender of a new slave.
     * @param id     slave's unique identification
     */
    private Slave(int height, int weight, int age, String gender, int id, String name, int price) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.id = id;
        this.name = name;
        this.price = price;
        calculateBenefit();
    }

    /**
     * Constructor of a new Slave with autoclaculate productivity.
     *
     * @param height height of a new slave
     * @param weight weight of a new slave
     * @param age    age of a new slave
     * @param gender Gender of a new slave.
     */
    private Slave(int height, int weight, int age, String gender, String name, int price) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.id = -1;
        this.name = name;
        this.price = price;
        calculateBenefit();
    }


    /**
     * Initial constructor for Slave.
     *
     * @param height  height of a new slave
     * @param weight  weight of a new slave
     * @param age     age of a new slave
     * @param gender  Gender of a new slave.
     * @param benefit How effective he can work in percentage
     * @param price   slave's cost
     */
    private Slave(int height, int weight, int age, String gender, Double benefit, String name, int price) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.price = price;
        this.name = name;
        this.benefit = benefit;
        this.price = price;
    }

    private Slave() {
        id = -1;
        price = 0;
        gender = "";
        benefit = 0.0;
    }

    /**
     * Returns new Builder instance
     *
     * @return new Builder instance for that slave
     */
    public static Builder newBuilder() {
        return new Slave().new Builder();
    }

    /**
     * @return slave's height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return slave's weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @return slave's age
     */
    public int getAge() {
        return age;
    }

    /**
     * @return slave's masculinity
     */
    public String getIsMale() {
        return gender;
    }

    /**
     * Calculates productivity with
     */
    private void calculateBenefit() {
        // TODO need to do calcuations

    }

    /**
     * get Merchandise's id
     */
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     */
    public int getPrice() {
        // TODO implement here
        return price;
    }

    /**
     * Returns merchandise quality in percentage.
     */
    public Double getBenefit() {
        return benefit;
    }

    /**
     * @param benefit slave's benefit
     */
    public void setBenefit(Double benefit) {
        this.benefit = benefit;
    }

    /**
     * Method mark merchandise as bought
     *
     * @return is this bought success?
     */
    public boolean buy() {
        if (bought) {
            return false;
        }
        bought = true;
        return true;
    }

    /**
     * Get merchant's name
     *
     * @return Merchant's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns all information about merchandise.
     *
     * @return Complicated string of merchandise info.
     * Formatted like "ClassName Param1:Value1 Param2:Value2 ..."
     */
    public String getAllInfo() {
        return "Slave id:" + id +
                " height:" + height +
                " weight:" + weight +
                " age:" + age +
                " gender:" + gender +
                " name:" + name +
                " benefit:" + benefit;
    }

    /**
     * Returns merchandise boughtFlag.
     *
     * @return true if merchandise already bought.
     */
    public boolean isBought() {
        return bought;
    }

    @Override
    public String toString() {
        return "Slave id:" + id +
                " height:" + height +
                " weight:" + weight +
                " age:" + age +
                " gender:" + gender +
                " name:" + name +
                " benefit:" + benefit +
                " price:" + price;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().getName().equals(this.getClass().getName())) {
            if (((Slave) obj).name.equals(name) &&
                    ((Slave) obj).gender.equals(gender) &&
                    Objects.equals(((Slave) obj).benefit, benefit) &&
                    ((Slave) obj).age == age &&
                    ((Slave) obj).weight == weight &&
                    ((Slave) obj).bought == bought &&
                    ((Slave) obj).price == price) {
                return true;
            }
        }
        return false;
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
         */
        public Builder addHeight(int height) {
            Slave.this.height = height;
            return this;
        }

        /**
         * Sets up weight to new Object
         *
         * @param weight new Object's weight
         */
        public Builder addWeight(int weight) {
            Slave.this.weight = weight;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param age new Object's age
         */
        public Builder addAge(int age) {
            Slave.this.age = age;
            return this;
        }

        /**
         * Setup gender in new Object
         *
         * @param gender new Object's gender
         */
        public Builder addGender(String gender) {
            Slave.this.gender = gender;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param benefit new Object's benefit
         */
        public Builder addBenefit(double benefit) {
            Slave.this.benefit = benefit;
            return this;
        }

        /**
         * calculates benefit with setted up parameters
         */
        public Builder autoCalculateBenefit() {
            // TODO implement here
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param price new Object's price
         */
        public Builder addPrice(int price) {
            Slave.this.price = price;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param id new Object's id
         */
        public Builder addId(int id) {
            Slave.this.id = id;
            return this;
        }

        /**
         * Sets up age to new Object
         *
         * @param name new Object's id
         */
        public Builder addName(String name) {
            Slave.this.name = name;
            return this;
        }

        /**
         * Build slave with already specified parametres.
         *
         * @return Instance of new object
         */
        public Slave build() {
            Slave slave = new Slave();
            slave.benefit = Slave.this.benefit;
            slave.name = Slave.this.name;
            slave.id = Slave.this.id;
            slave.gender = Slave.this.gender;
            slave.price = Slave.this.price;
            slave.age = Slave.this.age;
            slave.weight = Slave.this.weight;
            slave.height = Slave.this.height;
            return slave;
        }

    }

}

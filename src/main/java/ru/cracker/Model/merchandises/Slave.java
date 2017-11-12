package ru.cracker.Model.merchandises;

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
     * Default constructor
     */
    public Slave() {
    }

    /**
     * Initial constructor for Slave.
     *
     * @param height  height of a new slave
     * @param weight  weight of a new slave
     * @param age     age of a new slave
     * @param gender  Gender of a new slave.
     * @param id      How effective he can work in percentage
     * @param benefit
     */
    public Slave(int height, int weight, int age, String gender, int id, Double benefit, String name) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.id = id;
        this.name = name;
        this.benefit = benefit;
    }

    /**
     * Constructor of a new Slave with autoclaculate productivity.
     *
     * @param height height of a new slave
     * @param weight weight of a new slave
     * @param age    age of a new slave
     * @param gender Gender of a new slave.
     * @param id
     * @return void
     */
    public Slave(int height, int weight, int age, String gender, int id, String name) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.id = id;
        this.name = name;
        calculateBenefit();
    }

    /**
     * Constructor of a new Slave with autoclaculate productivity.
     *
     * @param height height of a new slave
     * @param weight weight of a new slave
     * @param age    age of a new slave
     * @param gender Gender of a new slave.
     * @return void
     */
    public Slave(int height, int weight, int age, String gender, String name) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.id = -1;
        this.name = name;
        calculateBenefit();
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
        // TODO do calcuations

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
     *
     * @return
     */
    public Double getBenefit() {
        return benefit;
    }

    /**
     * @param benefit
     */
    public void setBenefit(Double benefit) {
        this.benefit = benefit;
    }

    /**
     * Method mark merchandise as bought
     *
     * @return
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
        // TODO: 12.11.17 set Info
        return "info ";
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
                " benefit:" + benefit;
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
}
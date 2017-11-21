package ru.cracker.model.merchandises;


/**
 *
 */
public class Niger implements SlaveInterface {

    /**
     * Default constructor
     */
    private Niger() {
    }

    /**
     * Human height or stature is the distance from the bottom of the feet to the top of the head in a human body, standing erect.
     */
    private float height;

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

    private int price;


    /**
     * Returns new Builder instance for adding parametres in new slave
     * @return new builder wtih methord for crating new instance
     */
    public static Builder newBuilder() {
        return new Niger().new Builder();
    }

    /**
     * returns slave's height
     * @return slave's height
     */
    public float getHeight() {
        return height;
    }

    /**
     * returns slave's weight
     * @return slave's weight
     */
    public float getWeight() {
        return weight;
    }

    /**
     * returns slave's age
     * @return slave's age
     */
    public int getAge() {
        return age;
    }

    /**
     * returns slave's gender
     * @return returns slave's gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * returns slave's name
     * @return slave's name
     */
    public String getName() {
        return name;
    }


    public int getPrice()
    {
        return price;
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
         * @param height new Object's height
         * @return
         */
        public Builder addHeight(int height) {
            Niger.this.height = height;
            return this;
        }

        /**
         * Sets up weight to new Object
         * @param weight new Object's weight
         * @return
         */
        public Builder addWeight(int weight) {
            Niger.this.weight = weight;
            return this;
        }

        /**
         * Sets up age to new Object
         * @param age new Object's age
         * @return
         */
        public Builder addAge(int age) {
            Niger.this.age = age;
            return this;
        }

        /**
         * Setup gender in new Object
         * @param gender new Object's gender
         * @return
         */
        public Builder addGender(String gender) {
            Niger.this.gender = gender;
            return this;
        }

        /**
         * Sets up age to new Object
         * @param price new Object's price
         * @return
         */
        public Builder addPrice(int price) {
            Niger.this.price = price;
            return this;
        }

        /**
         * Sets up age to new Object
         * @param id new Object's id
         * @return
         */
        public Builder addId(int id) {
            Niger.this.id = id;
            return this;
        }

        /**
         * Build slave with already specified parametres.
         * @return Instance of new object
         */
        public Niger build() {
            Niger niger = new Niger();
            niger.benefit = Niger.this.benefit;
            niger.name = Niger.this.name;
            niger.id = Niger.this.id;
            niger.gender = Niger.this.gender;
            niger.price = Niger.this.price;
            niger.age = Niger.this.age;
            niger.weight = Niger.this.weight;
            niger.height = Niger.this.height;
            return niger;
        }

        /**
         * Sets up name to new Object
         * @param name new Object's name
         * @return
         */
        public Builder addName(String name) {
            Niger.this.name = name;
            return this;
        }

    }

}

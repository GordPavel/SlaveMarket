package ru.cracker.model.merchandises;


/**
 *
 */
public class Niger implements SlaveInterface {

    /**
     * Default constructor
     */
    public Niger() {
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


    /**
     * Returns new Builder instance for adding parametres in new slave
     * @return new builder wtih methord for crating new instance
     */
    public static Builder newBuilder() {
        // TODO implement here
        return null;
    }

    /**
     * returns slave's height
     * @return slave's height
     */
    public float getHeight() {
        // TODO implement here
        return 0.0f;
    }

    /**
     * returns slave's weight
     * @return slave's weight
     */
    public float getWeight() {
        // TODO implement here
        return 0.0f;
    }

    /**
     * returns slave's age
     * @return slave's age
     */
    public int getAge() {
        // TODO implement here
        return 0;
    }

    /**
     * returns slave's gender
     * @return returns slave's gender
     */
    public String getGender() {
        // TODO implement here
        return "";
    }

    /**
     * returns slave's name
     * @return slave's name
     */
    public String getName() {
        // TODO implement here
        return "";
    }

    /**
     * Builder class for realiztion of builder pattern.
     */
    public class Builder {

        /**
         * Default constructor
         */
        public Builder() {
        }


        /**
         * Sets up height to new Object
         * @param height new Object's height
         * @return
         */
        public Builder addHeight(int height) {
            // TODO implement here
            return null;
        }

        /**
         * Sets up weight to new Object
         * @param weight new Object's weight
         * @return
         */
        public Builder addWeight(int weight) {
            // TODO implement here
            return null;
        }

        /**
         * Sets up age to new Object
         * @param age new Object's age
         * @return
         */
        public Builder addAge(int age) {
            // TODO implement here
            return null;
        }

        /**
         * Setup gender in new Object
         * @param gender new Object's gender
         * @return
         */
        public Builder addGender(String gender) {
            // TODO implement here
            return null;
        }

        /**
         * Sets up age to new Object
         * @param benefit new Object's benefit
         * @return
         */
        public Builder addBenefit(double benefit) {
            // TODO implement here
            return null;
        }

        /**
         * calculates benefit with setted up parameters
         * @return calculates benefit with setted up parameters
         */
        public Builder autoCalculateBenefit() {
            // TODO implement here
            return null;
        }

        /**
         * Sets up age to new Object
         * @param price new Object's price
         * @return
         */
        public Builder addPrice(int price) {
            // TODO implement here
            return null;
        }

        /**
         * Sets up age to new Object
         * @param id new Object's id
         * @return
         */
        public Builder addId(int id) {
            // TODO implement here
            return null;
        }

        /**
         * Build slave with already specified parametres.
         * @return Instance of new object
         */
        public Niger build() {
            // TODO implement here
            return null;
        }

        /**
         * Sets up name to new Object
         * @param name new Object's name
         * @return
         */
        public Builder addName(String name) {
            // TODO implement here
            return null;
        }

    }

}

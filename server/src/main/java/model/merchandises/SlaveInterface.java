package model.merchandises;

/**
 * Model of slave.
 */
public interface SlaveInterface extends Merchandise {

    /**
     * returns slave's height.
     *
     * @return slave's height
     */
    float getHeight();

    /**
     * returns slave's weight.
     *
     * @return slave's weight
     */
    float getWeight();

    /**
     * returns slave's age.
     *
     * @return slave's age
     */
    int getAge();

    /**
     * returns slave's gender.
     *
     * @return returns slave's gender
     */
    String getGender();

    /**
     * returns slave's name.
     *
     * @return slave's name
     */
    String getName();


}

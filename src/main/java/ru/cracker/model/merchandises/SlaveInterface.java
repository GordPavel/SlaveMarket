package ru.cracker.model.merchandises;

/**
 *
 */
public interface SlaveInterface extends Merchandise {

  /**
   * returns slave's height
   * @return slave's height
   */
  public float getHeight();

  /**
   * returns slave's weight
   * @return slave's weight
   */
  public float getWeight();

  /**
   * returns slave's age
   * @return slave's age
   */
  public int getAge();

  /**
   * returns slave's gender
   * @return returns slave's gender
   */
  public String getGender();

  /**
   * returns slave's name
   * @return slave's name
   */
  public String getName();

}

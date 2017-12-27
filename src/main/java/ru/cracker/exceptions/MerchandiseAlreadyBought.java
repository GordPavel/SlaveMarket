package ru.cracker.exceptions;

/**
 * Throws if trying to buy already bought Merchandise.
 */
public class MerchandiseAlreadyBought extends IllegalArgumentException {

  /**
   * Constructor.
   *
   * @param id id of bought merchandise
   */
  public MerchandiseAlreadyBought(int id) {
    super("You can't change that merchandise before you bought it");
  }
}

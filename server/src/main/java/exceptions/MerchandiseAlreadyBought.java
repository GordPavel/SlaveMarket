package exceptions;

/**
 * Throws if trying to buy already bought Merchandise.
 */
public class MerchandiseAlreadyBought extends IllegalArgumentException {

  /**
   * Constructor.
   * "You can't change that merchandise.\n"
   *
   * @param cause cause why user can't buy merchandise
   */
  public MerchandiseAlreadyBought(String cause) {
    super(cause);
  }
}

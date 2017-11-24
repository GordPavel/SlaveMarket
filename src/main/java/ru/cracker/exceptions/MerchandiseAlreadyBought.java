package ru.cracker.exceptions;

/**
 * Throws if trying to buy already bought Merchandise.
 */
public class MerchandiseAlreadyBought extends IllegalArgumentException {
    /**
     * Constructor
     *
     * @param id id of bought merchandise
     */
    public MerchandiseAlreadyBought(int id) {
        super("Merchandise with id " + id + " already bought");
    }
}

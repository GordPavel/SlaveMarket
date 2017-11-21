package ru.cracker.exceptions;

import ru.cracker.model.merchandises.Merchandise;

/**
 * Throws when specified wrong id of merchandise
 */
public class MerchandiseNotFoundException extends IllegalArgumentException {
    /**
     * Constructor to generate new exception
     *
     * @param id id of missed merchandise
     */
    public MerchandiseNotFoundException(int id) {
        super("Merchandise with " + id + " cannot be founded in database");
    }

    public MerchandiseNotFoundException(Merchandise merchandise) {
        super("Merchandise \"" + merchandise.getAllInfo() + "\" cannot be founded in database");
    }
}

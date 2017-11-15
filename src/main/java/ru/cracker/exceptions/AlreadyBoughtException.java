package ru.cracker.exceptions;

/**
 * Throws when requested item already bought.
 */
public class AlreadyBoughtException extends Throwable {
    public AlreadyBoughtException(int id) {
        super("Merchandise with id " + id + " already bought by someone");
    }
}

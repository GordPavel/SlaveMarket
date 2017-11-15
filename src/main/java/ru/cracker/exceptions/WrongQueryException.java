package ru.cracker.exceptions;

/**
 * Throws when search query is syntax wrong
 */
public class WrongQueryException extends IllegalArgumentException {
    public WrongQueryException(String query) {
        super("Illegal query \"" + query + "\"");
    }

}

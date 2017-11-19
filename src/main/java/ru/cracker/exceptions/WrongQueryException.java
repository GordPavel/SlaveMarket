package ru.cracker.exceptions;

public class WrongQueryException extends IllegalArgumentException {
    public WrongQueryException(String query) {
        super("Illegal query \"" + query + "\"");
    }

}

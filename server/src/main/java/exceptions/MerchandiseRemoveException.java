/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package exceptions;

public class MerchandiseRemoveException extends IllegalArgumentException {
    private String message;

    /**
     * Constructor to generate new exception.
     */
    public MerchandiseRemoveException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        return "Founded error while updating merchandise: " + message;
    }
}

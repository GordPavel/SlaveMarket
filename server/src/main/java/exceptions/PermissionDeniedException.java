/*
 * Copyright (c) 2018.
 * You may use, distribute and modify this code
 * under the terms of the TOT (take on trust) public license which unfortunately won't be
 * written for another century. So you can modify and copy this files.
 *
 */

package exceptions;

public class PermissionDeniedException extends IllegalArgumentException {
    public PermissionDeniedException() {
        super("Permission denied");
    }
}

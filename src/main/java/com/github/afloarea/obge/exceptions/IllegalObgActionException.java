package com.github.afloarea.obge.exceptions;

/**
 * Generic interface thrown on an illegal action.
 */
public class IllegalObgActionException extends RuntimeException {

    public IllegalObgActionException(String message) {
        super(message);
    }
}

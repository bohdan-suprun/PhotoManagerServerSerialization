package edu.nure.exceptions;

/**
 * Created by bod on 25.11.15.
 */
public class InsertException extends DBException {

    public InsertException() {
    }

    public InsertException(String message) {
        super(message);
    }

    public InsertException(Throwable cause) {
        super(cause);
    }
}

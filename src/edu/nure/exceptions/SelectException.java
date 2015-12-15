package edu.nure.exceptions;

/**
 * Created by bod on 25.11.15.
 */
public class SelectException extends DBException {

    public SelectException() {
    }

    public SelectException(String message) {
        super(message);
    }

    public SelectException(Throwable cause) {
        super(cause);
    }
}

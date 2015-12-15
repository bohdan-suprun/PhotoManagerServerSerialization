package edu.nure.exceptions;

/**
 * Created by bod on 11.11.15.
 */
public class DBException extends Exception {

    public DBException() {
    }

    public DBException(String message) {
        super(message);
    }

    public DBException(Throwable another) {
        super(another.getMessage());
    }
}

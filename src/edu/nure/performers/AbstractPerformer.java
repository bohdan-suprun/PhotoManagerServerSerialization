package edu.nure.performers;

import edu.nure.exceptions.DBException;
import edu.nure.exceptions.PerformException;
import edu.nure.util.ResponseBuilder;

import java.io.IOException;

/**
 * Created by bod on 18.09.15.
 * Abstract class. Used for manipulation on entities
 */
abstract public class AbstractPerformer {
    protected ResponseBuilder builder;

    public AbstractPerformer(ResponseBuilder builder) throws DBException {
        setBuilder(builder);

    }

    protected ResponseBuilder getBuilder() {
        return builder;
    }

    protected void setBuilder(ResponseBuilder builder) {
        this.builder = builder;
    }

    abstract public void perform() throws PerformException, IOException, DBException;

    protected void doGet() throws PerformException, IOException {
        // returns requested values
        //TODO
    }

    protected void doInsert() throws PerformException, IOException {
        // inserts and returns value of last inserted item
        //TODO
    }

    protected void doUpdate() throws PerformException, IOException {
        // updates value
        //TODO
    }

    protected void doDelete() throws PerformException, IOException {
        // just deletes value if it is not restricted
        //TODO
    }
}

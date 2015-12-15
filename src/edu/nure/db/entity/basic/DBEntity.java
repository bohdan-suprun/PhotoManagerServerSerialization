package edu.nure.db.entity.basic;

import edu.nure.bl.constraints.ValidationException;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.DBException;

import java.sql.ResultSet;

/**
 * Created by bod on 11.11.15.
 */
public interface DBEntity {
    String[] getFields();

    Object[] getValues();

    void parseResultSet(ResultSet rs) throws DBException, ValidationException;

    String entityName();

    PrimaryKey getPrimaryKey();
}

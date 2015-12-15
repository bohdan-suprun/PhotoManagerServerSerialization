package edu.nure.db.primarykey;

/**
 * Created by bod on 11.11.15.
 */
public class StringPrimaryKey extends PrimaryKey<String> {

    public StringPrimaryKey(String name, String value) {
        super(name, "'" + value + "'");
    }
}

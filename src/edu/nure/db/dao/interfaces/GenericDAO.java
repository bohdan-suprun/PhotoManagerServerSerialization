package edu.nure.db.dao.interfaces;

import edu.nure.db.entity.basic.DBEntity;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.SelectException;

import java.util.List;

/**
 * Created by bod on 11.11.15.
 */
public interface GenericDAO<T extends DBEntity> {

    T insert(T ent) throws DBException;

    boolean update(T ent) throws DBException;

    boolean update(T ent, PrimaryKey pk) throws DBException;

    /**
     * Update row in the DB and return previous value of the row
     *
     * @param ent
     * @return previous value of row
     * @throws DBException
     */
    T updatePrevious(T ent) throws DBException;

    boolean delete(T ent) throws DBException;

    boolean delete(String entityName, PrimaryKey key) throws DBException;

    T select(PrimaryKey key) throws SelectException;

    List<T> selectAll() throws SelectException;

}

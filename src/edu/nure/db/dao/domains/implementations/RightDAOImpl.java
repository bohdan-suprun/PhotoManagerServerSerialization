package edu.nure.db.dao.domains.implementations;

import edu.nure.bl.Right;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.SelectException;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

public class RightDAOImpl extends GenericDAOImpl<Right> {

    public RightDAOImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Right select(PrimaryKey key) throws SelectException {
        Iterator<Right> it = getAll(Right.class, "WHERE `" + key.getName() + "` = " + key.getValue()).iterator();
        if (it.hasNext()) {
            return it.next();
        } else {
            throw new SelectException("No such element");
        }
    }

    @Override
    public List<Right> selectAll() throws SelectException {
        return getAll(Right.class, null);
    }
}

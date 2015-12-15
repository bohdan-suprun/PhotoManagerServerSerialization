package edu.nure.db.dao.domains.implementations;

import edu.nure.bl.Urgency;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.SelectException;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bod on 11.11.15.
 */
public class UrgencyDAOImpl extends GenericDAOImpl<Urgency> {

    public UrgencyDAOImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Urgency select(PrimaryKey key) throws SelectException {
        Iterator<Urgency> it = getAll(Urgency.class, "WHERE `" + key.getName() + "` = " + key.getValue()).iterator();
        if (it.hasNext()) {
            return it.next();
        } else {
            throw new SelectException("No such element");
        }
    }

    @Override
    public List<Urgency> selectAll() throws SelectException {
        return getAll(Urgency.class, null);
    }

    @Override
    protected Urgency getLastInserted(Urgency tClass, Statement s) throws SelectException {
        return tClass;
    }
}

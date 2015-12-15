package edu.nure.db.dao.domains.implementations;

import edu.nure.bl.Format;
import edu.nure.db.dao.interfaces.FormatDAO;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.SelectException;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bod on 11.11.15.
 */
public class FormatDAOImpl extends GenericDAOImpl<Format> implements FormatDAO {

    public FormatDAOImpl(Connection connection) {
        super(connection);
    }

    @Override
    public List<Format> getLikeName(String like) throws SelectException {
        return getAll(Format.class, "WHERE `Name` LIKE '" + like + "%'");
    }

    @Override
    public Format select(PrimaryKey key) throws SelectException {
        Iterator<Format> it = getAll(Format.class,
                "WHERE `" + key.getName() + "`=\'" + key.getValue() + "\'").iterator();
        if (it.hasNext()) {
            return it.next();
        } else {
            throw new SelectException("No such element");
        }
    }

    @Override
    public List<Format> selectAll() throws SelectException {
        return getAll(Format.class, null);
    }

    @Override
    protected Format getLastInserted(Format tClass, Statement s) throws SelectException {
        return tClass;
    }
}

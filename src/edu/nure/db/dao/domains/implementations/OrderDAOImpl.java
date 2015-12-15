package edu.nure.db.dao.domains.implementations;

import edu.nure.bl.Order;
import edu.nure.db.dao.interfaces.OrderDAO;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.SelectException;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bod on 11.11.15.
 */
public class OrderDAOImpl extends GenericDAOImpl<Order> implements OrderDAO {

    public OrderDAOImpl(Connection connection) {
        super(connection);
    }

    @Override
    public List<Order> getByResponsible(int respId) throws SelectException {
        return getAll(Order.class,
                "WHERE `Responsible` = " + respId + " ORDER BY `Urg`"
        );
    }

    @Override
    public List<Order> getByCustomer(int customerId) throws SelectException {
        return getAll(Order.class,
                "WHERE `Customer` = " + customerId + " ORDER BY `Urg`"
        );
    }

    @Override
    public Order select(PrimaryKey key) throws SelectException {
        Iterator<Order> it = getAll(Order.class,
                "WHERE `" + key.getName() + "` = " + key.getValue() + " ORDER BY `Urg`"
        ).iterator();
        if (it.hasNext()) {
            return it.next();
        } else {
            throw new SelectException("No such element");
        }
    }

    @Override
    public List<Order> getActiveByResponsible(int respId) throws SelectException {
        return getAll(Order.class,
                "WHERE `Responsible` = " + respId + " AND `Status` = 1 ORDER BY `Urg`"
        );
    }

    @Override
    public List<Order> getActiveByCustomer(int customerId) throws SelectException {
        return getAll(Order.class,
                "WHERE `Customer` = " + customerId + " AND `Status` = 1 ORDER BY `Urg`"
        );
    }

    @Override
    public List<Order> getActiveById(int id) throws SelectException {
        return getAll(Order.class,
                "WHERE `Id` = " + id + " AND `Status` = 1 ORDER BY `Urg`"
        );
    }
}

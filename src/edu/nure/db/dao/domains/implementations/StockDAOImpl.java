package edu.nure.db.dao.domains.implementations;

import edu.nure.bl.Stock;
import edu.nure.db.dao.interfaces.StockDAO;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.SelectException;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bod on 11.11.15.
 */
public class StockDAOImpl extends GenericDAOImpl<Stock> implements StockDAO {

    public StockDAOImpl(Connection connection) {
        super(connection);
    }

    @Override
    public List<Stock> getStock(int orderId) throws SelectException {
        return getAll(Stock.class,
                "WHERE `Id_order` = " + orderId
        );
    }

    @Override
    public Stock select(PrimaryKey key) throws SelectException {
        Iterator<Stock> it = getAll(Stock.class, "WHERE `" + key.getName() + "` = " + key.getValue()).iterator();
        if (it.hasNext()) {
            return it.next();
        } else {
            throw new SelectException("No such element");
        }
    }
}

package edu.nure.db.dao.interfaces;

import edu.nure.bl.Stock;
import edu.nure.exceptions.SelectException;

import java.util.List;

/**
 * Created by bod on 11.11.15.
 */
public interface StockDAO extends GenericDAO<Stock> {

    /**
     * @param orderId the order id
     * @return all stocks related with Order id = orderId
     */

    List<Stock> getStock(int orderId) throws SelectException;
}

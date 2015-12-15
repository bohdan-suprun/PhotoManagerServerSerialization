package edu.nure.db.dao;

import edu.nure.bl.Right;
import edu.nure.bl.Urgency;
import edu.nure.db.dao.interfaces.*;
import edu.nure.exceptions.DBException;

import java.sql.Connection;

/**
 * Created by bod on 11.11.15.
 */
public interface DAOFactory {

    Connection getConnection() throws DBException;

    AlbumDAO getAlbumDAO() throws DBException;

    FormatDAO getFormatDAO() throws DBException;

    ImageDAO getImageDAO() throws DBException;

    OrderDAO getOrderDAO() throws DBException;

    GenericDAO<Right> getRightDAO() throws DBException;

    StockDAO getStockDAO() throws DBException;

    GenericDAO<Urgency> getUrgencyDAO() throws DBException;

    UserDAO getUserDAO() throws DBException;

}

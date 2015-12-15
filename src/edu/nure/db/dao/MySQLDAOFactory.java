package edu.nure.db.dao;

import edu.nure.bl.Right;
import edu.nure.bl.Urgency;
import edu.nure.db.dao.domains.implementations.*;
import edu.nure.db.dao.interfaces.*;
import edu.nure.exceptions.DBException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by bod on 11.11.15.
 */
public class MySQLDAOFactory implements DAOFactory {

    private DataSource dataSource;

    public MySQLDAOFactory() throws DBException {
        try {
            Context init = new InitialContext();
            dataSource = (DataSource) init.lookup("java:/comp/env/jdbc/pdb");
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    @Override
    public AlbumDAO getAlbumDAO() throws DBException {
        return new AlbumDAOImpl(getConnection());
    }

    @Override
    public FormatDAO getFormatDAO() throws DBException {
        return new FormatDAOImpl(getConnection());
    }

    @Override
    public ImageDAO getImageDAO() throws DBException {
        return new ImageDAOImpl(getConnection());
    }

    @Override
    public OrderDAO getOrderDAO() throws DBException {
        return new OrderDAOImpl(getConnection());
    }

    @Override
    public GenericDAO<Right> getRightDAO() throws DBException {
        return new RightDAOImpl(getConnection());
    }

    @Override
    public StockDAO getStockDAO() throws DBException {
        return new StockDAOImpl(getConnection());
    }

    @Override
    public GenericDAO<Urgency> getUrgencyDAO() throws DBException {
        return new UrgencyDAOImpl(getConnection());
    }

    @Override
    public UserDAO getUserDAO() throws DBException {
        return new UserDAOImpl(getConnection());
    }

    @Override
    public Connection getConnection() throws DBException {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }
}

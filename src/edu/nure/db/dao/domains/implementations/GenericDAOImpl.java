package edu.nure.db.dao.domains.implementations;

import edu.nure.bl.constraints.ValidationException;
import edu.nure.db.RequestPreparing;
import edu.nure.db.dao.interfaces.GenericDAO;
import edu.nure.db.entity.basic.DBEntity;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.InsertException;
import edu.nure.exceptions.SelectException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

abstract public class GenericDAOImpl<T extends DBEntity> implements GenericDAO<T> {

    protected Connection connection;

    public GenericDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public T insert(T ent) throws DBException {
        try (Statement s = connection.createStatement()) {
            String sql = RequestPreparing.insert(ent.entityName(), ent.getFields(), ent.getValues());
            connection.setAutoCommit(false);
            int n = s.executeUpdate(sql);
            if (n != 1) {
                throw new InsertException("Произошла ошибка во время добавления данных: ничего не добавлено");
            }
            T last = getLastInserted(ent, s);
            connection.commit();
            return last;
        } catch (SQLException ex) {
            throw new DBException(ex);
        } finally {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException ex) {
                throw new DBException(ex);
            }
        }
    }

    protected T getLastInserted(T tClass, Statement s) throws SelectException {
        try {
            T last = (T) tClass.getClass().newInstance();
            PrimaryKey pk = last.getPrimaryKey();
            String sql = RequestPreparing.select(last.entityName(), new String[]{"*"},
                    "WHERE `" + pk.getName() + "`= (Select Max(" + pk.getName() + ") From `" + RequestPreparing.DB_NAME + "`.`"
                            + last.entityName() + "`)"
            );
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                last.parseResultSet(rs);
            }
            return last;
        } catch (Exception ex) {
            throw new SelectException(ex);
        }
    }

    @Override
    public boolean update(T ent, PrimaryKey key) throws DBException {
        try (Connection c = connection; Statement s = c.createStatement()) {
            String sql = RequestPreparing.update(ent.entityName(), ent.getFields(), ent.getValues(),
                    key.getName() + " = " + key.getValue());
            int n = s.executeUpdate(sql);
            return n > 0;
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    @Override
    public boolean update(T ent) throws DBException {
        return update(ent, ent.getPrimaryKey());
    }

    @Override
    public T updatePrevious(T ent) throws DBException {
        try (Statement s = connection.createStatement()) {
            PrimaryKey key = ent.getPrimaryKey();
            String sql = RequestPreparing.select(ent.entityName(), new String[]{"*"}, key);
            connection.setAutoCommit(false);
            ResultSet rs = s.executeQuery(sql);
            T res = (T) ent.getClass().newInstance();
            if (rs.next()) {
                res.parseResultSet(rs);
            } else {
                throw new SelectException("Нет записи с таким ключем");
            }
            sql = RequestPreparing.update(ent.entityName(), ent.getFields(), ent.getValues(),
                    key.getName() + " = " + key.getValue());
            int n = s.executeUpdate(sql);
            if (n < 1) {
                throw new InsertException("Ошибка во время изменения записи");
            }
            connection.commit();
            return res;
        } catch (SQLException | ValidationException ex) {
            throw new DBException(ex);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new InsertException("Во время выполнения произошла внутренняя ошибка сервера");
        } finally {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException ex) {
                throw new DBException(ex);
            }
        }
    }

    @Override
    public boolean delete(T ent) throws DBException {
        return delete(ent.entityName(), ent.getPrimaryKey());
    }

    @Override
    public boolean delete(String entityName, PrimaryKey key) throws DBException {
        try (Connection c = connection; Statement s = c.createStatement()) {
            if (entityName == null || entityName.isEmpty() || entityName.contains("'")) {
                throw new SQLException("Unreachable entity name");
            }
            String sql = "DELETE FROM `" + RequestPreparing.DB_NAME + "`.`" + entityName + "` WHERE " + key.getName() + "=" + key.getValue();
            int n = s.executeUpdate(sql);
            return n > 0;
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    @Override
    public List<T> selectAll() throws SelectException {
        throw new UnsupportedOperationException();
    }

    protected List<T> getAll(Class<T> tClass, String cond) throws SelectException {
        List<T> list = new ArrayList<>();
        try (Connection c = connection; Statement s = c.createStatement()) {
            T inst = tClass.newInstance();
            String sql = RequestPreparing.select(inst.entityName(), new String[]{"*"}, cond);
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                inst = tClass.newInstance();
                inst.parseResultSet(rs);
                list.add(inst);
            }
            rs.close();
            return list;
        } catch (Exception ex) {
            throw new SelectException(ex);
        }
    }
}

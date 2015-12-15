package edu.nure.db.dao.domains.implementations;

import edu.nure.bl.InsertedUser;
import edu.nure.bl.User;
import edu.nure.db.RequestPreparing;
import edu.nure.db.dao.interfaces.UserDAO;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.InsertException;
import edu.nure.exceptions.SelectException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class UserDAOImpl extends GenericDAOImpl<User> implements UserDAO {

    public UserDAOImpl(Connection connection) {
        super(connection);
    }

    @Override
    public User login(String login, String pass) throws SelectException {
        List<User> li = getAll(User.class, "WHERE `Phone`='" + login + "' and `Password`='"
                + pass + "' AND `Password` IS NOT NULL");

        Iterator<User> it = li.iterator();
        if (it.hasNext()) {
            return it.next();
        } else {
            throw new SelectException("No such user");
        }
    }

    @Override
    public List<User> getByName(String likeName) throws SelectException {
        return getByName(likeName, false);
    }

    @Override
    public List<User> getByName(String likeName, boolean withHiRights) throws SelectException {
        if (withHiRights) {
            return getAll(User.class, "WHERE `Name` LIKE '%" + likeName
                            + "%' AND `Right` = 'фотограф' ORDER BY `Name` LIMIT 15"
            );
        } else {
            return getAll(User.class,
                    "WHERE Name LIKE '%" + likeName + "%' Group BY `Name` ORDER BY Name LIMIT 15"
            );
        }
    }

    @Override
    public List<User> getAllNames(String likeName) throws SelectException {
        return getAll(User.class,
                "WHERE Name LIKE '%" + likeName + "%' ORDER BY Name LIMIT 15"
        );
    }

    @Override
    public List<User> getByPhone(String likePhone) throws SelectException {
        return getAll(User.class, "Where replace(replace(replace(replace(replace(`Phone`,'+','')"
                + ",'-',''),'(',''),')',''),' ','') "
                + "Like '%" + likePhone + "%' ORDER BY Phone LIMIT 15");
    }

    @Override
    public boolean setPassword(int id, String pass) throws DBException {
        try (Connection c = connection; Statement s = c.createStatement()) {
            String sql = RequestPreparing.update("USER", new String[]{"Password"}, new Object[]{pass},
                    "`Id`=" + id + " AND `Password` is NULL"
            );
            int n = s.executeUpdate(sql);
            return n == 1;
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    @Override
    public User authenticate(String code) throws DBException {

        try (Connection c = connection; Statement s = c.createStatement()) {
            String sql = "Select * FROM `AUT` INNER JOIN `USER` USING(Id) WHERE `Code` = '" + code
                    + "' AND `Password` is null";

            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                User user = new User();
                user.parseResultSet(rs);
                // removing the auth code if the user exists
                s.executeUpdate(
                        "DELETE FROM `AUT` WHERE `Id` = '" + user.getId() + "'"
                );
                return user;
            } else {
                throw new SelectException("Can't authenticate, user not found");
            }
        } catch (Exception ex) {
            throw new DBException(ex);
        }
    }

    @Override
    public User select(PrimaryKey key) throws SelectException {
        Iterator<User> it = getAll(User.class, "WHERE `" + key.getName() + "` = " + key.getValue()).iterator();
        if (it.hasNext()) {
            return it.next();
        } else {
            throw new SelectException("No such user");
        }
    }

    private User newUser(User ent, Statement s) throws DBException {
        try {
            String sql = RequestPreparing.insert(ent.entityName(), ent.getFields(), ent.getValues());
            int n = s.executeUpdate(sql);
            if (n != 1) {
                throw new InsertException("Произошла ошибка во время добавления нового пользователя");
            }
            return getLastInserted(ent, s);
        } catch (SQLException ex) {
            throw new DBException(ex);
        }
    }

    @Override
    public InsertedUser insertCode(User user) throws DBException {
        try (Statement s = connection.createStatement()) {
            connection.setAutoCommit(false);
            user = newUser(user, s);
            String autCode = getCode(user);
            String sql = RequestPreparing.insert("aut", new String[]{"Id", "Code"},
                    new Object[]{user.getId(), autCode});
            int n = s.executeUpdate(sql);
            if (n != 1) {
                throw new InsertException("Ошибка во время добавления кода авторизации для пользователя");
            }
            connection.commit();
            return new InsertedUser(user, autCode);
        } catch (SQLException ex) {
            throw new DBException(ex);
        } finally {
            try {
                connection.setAutoCommit(false);
                connection.close();
            } catch (SQLException ex) {
                throw new DBException(ex);
            }
        }
    }

    private String getCode(User user) {
        try {
            return new BigInteger(MessageDigest.getInstance("MD5").digest((user.getName() + new Date().getTime()
                    + user.getId() + user.getPhone() + (1000000 + new Random()
                    .nextLong())).getBytes())).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return new BigInteger(user.getName() + new Date().getTime() + user.getPhone() + user.getId() + (1000000 +
                    new Random().nextLong())).toString(16);
        }
    }

    @Override
    public boolean delete(String entityName, PrimaryKey key) throws DBException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(User ent) throws DBException {
        throw new UnsupportedOperationException();
    }
}

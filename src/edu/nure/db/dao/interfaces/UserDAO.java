package edu.nure.db.dao.interfaces;

import edu.nure.bl.InsertedUser;
import edu.nure.bl.User;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.SelectException;

import java.util.List;

/**
 * Created by bod on 11.11.15.
 */
public interface UserDAO extends GenericDAO<User> {

    User login(String login, String pass) throws SelectException;

    List<User> getByName(String likeName) throws SelectException;

    List<User> getAllNames(String likeName) throws SelectException;

    List<User> getByName(String likeName, boolean withHiRights) throws SelectException;

    List<User> getByPhone(String likePhone) throws SelectException;

    boolean setPassword(int id, String pass) throws DBException;

    User authenticate(String code) throws DBException;

    public InsertedUser insertCode(User ent) throws DBException;

}

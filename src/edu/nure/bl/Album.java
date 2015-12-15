package edu.nure.bl;

import edu.nure.db.entity.basic.AbstractEntity;
import edu.nure.db.primarykey.IntegerPrimaryKey;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.DBException;
import edu.nure.util.ResponseBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by bod on 07.10.15.
 */
public class Album extends AbstractEntity {

    private static final long serialVersionUID = 5728929805742000494L;
    private String name;
    private int id;
    private int userId;

    public Album(String name, int id, int userId) {
        this.name = name.replace("'", "\"");
        this.id = id;
        this.userId = userId;
    }

    public Album() {

    }

    @Override
    public void parseResultSet(ResultSet rs) throws DBException {
        try {
            this.name = rs.getString("Name");
            this.id = rs.getInt("Id");
            this.userId = rs.getInt("UserId");
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

    public Album(ResponseBuilder rs) {
        this.name = rs.getParameter("name");
        this.id = rs.getIntParameter("id");
        this.userId = rs.getIntParameter("userId");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

//    @Override
//    public String toXML() {
//        return "<album name=\"" + name.replace("\"", "'") + "\" id = \"" + id + "\" userId=\"" + userId + "\"/>";
//    }

    @Override
    public String toQuery() {
        return "name=" + name.replace("\"", "'") + "&id=" + id + "&userId=" + userId;
    }

    public String[] getFields() {
        return new String[]{"Name", "UserId"};
    }

    @Override
    public Object[] getValues() {
        return new Object[]{getName(), getUserId()};
    }

    @Override
    public String entityName() {
        return "ALBUM";
    }

    @Override
    public PrimaryKey getPrimaryKey() {
        return new IntegerPrimaryKey(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        return id == album.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

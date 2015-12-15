package edu.nure.bl;

import edu.nure.db.entity.basic.AbstractEntity;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.db.primarykey.StringPrimaryKey;
import edu.nure.exceptions.DBException;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by bod on 17.09.15.
 */
public class Right extends AbstractEntity {

    private static final long serialVersionUID = -9113235862677337231L;
    private String type, desc;

    public Right(String type, String desc) {
        setType(type);
        setDesc(desc);
    }

    public Right() {

    }

    public void parseResultSet(ResultSet rs) throws DBException {
        try {
            setType(rs.getString("Type"));
            setDesc(rs.getString("Desc"));
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }

    }

    public Right(HttpServletRequest req) {
        setType(req.getParameter("type"));
        setDesc(req.getParameter("desc"));
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = Objects.requireNonNull(type);
    }

    public String getDesc() {
        if (desc != null)
            desc = desc.replace('"', '\'');
        return desc;
    }

    private void setDesc(String desc) {
        if (desc != null)
            desc = desc.replace('\'', '"');
        this.desc = desc;
    }

//    @Override
//    public String toXML() {
//        return "<right type=\"" + type + "\"" + ((desc == null) ? "" : " desc=\"" + desc + "\"") + "/>";
//    }

    @Override
    public String toQuery() {
        return "type=" + type + "&desc=" + desc;
    }

    public String[] getFields() {
        return new String[]{"Type", "Desc"};
    }

    @Override
    public Object[] getValues() {
        return new Object[]{getType(), getDesc()};
    }

    @Override
    public String entityName() {
        return "USER_RIGHT";
    }

    @Override
    public PrimaryKey getPrimaryKey() {
        return new StringPrimaryKey("Type", getType());
    }
}

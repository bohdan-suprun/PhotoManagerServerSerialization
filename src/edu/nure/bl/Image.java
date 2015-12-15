package edu.nure.bl;

import edu.nure.bl.constraints.ValidationException;
import edu.nure.db.entity.basic.AbstractEntity;
import edu.nure.db.primarykey.IntegerPrimaryKey;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.DBException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bod on 17.09.15.
 */
public class Image extends AbstractEntity {
    private static final long serialVersionUID = -88424382708126923L;
    private int id = ID_NOT_SET;
    transient private String hash;
    transient private byte[] image;
    private int album;
    private Date createdIn;

    public Image() {

    }

    public Image(String hash, int id, byte[] image, int album, Date createdIn) {
        setHash(hash);
        setId(id);
        setImage(image);
        setAlbum(album);
        setCreatedIn(createdIn);
    }

    @Override
    public void parseResultSet(ResultSet rs) throws DBException, ValidationException {
        try {
            setHash(rs.getString("Hash"));
            setId(rs.getInt("Id"));
            setImage(rs.getBytes("Image"));
            setAlbum(rs.getInt("Album"));
            setCreatedIn(rs.getDate("CreatedIn"));
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

    public String getCreatedIn() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(createdIn);
    }

    public void setCreatedIn(Date createdIn) {
        this.createdIn = createdIn;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    @Override
//    public String toXML() {
//        return "<image id=\"" + id + "\" hash=\"" + hash + "\" album=\""
//                + album + "\" createdIn=\"" + getCreatedIn() + "\"/>";
//    }

    @Override
    public String toQuery() {
        return "id=" + id +
                "&hash=" + hash +
                "&album=" + album +
                "&createdIn=" + getCreatedIn();
    }

    public String[] getFields() {
        return new String[]{"Hash", "Album", "CreatedIn", "Image"};
    }

    @Override
    public Object[] getValues() {
        return new Object[]{
                getHash(),
                getAlbum(),
                getCreatedIn(),
                getImage()
        };
    }

    @Override
    public String entityName() {
        return "IMAGE";
    }

    @Override
    public PrimaryKey getPrimaryKey() {
        return new IntegerPrimaryKey(getId());
    }
}

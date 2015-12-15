package edu.nure.db.dao.domains.implementations;

import edu.nure.bl.Album;
import edu.nure.bl.Image;
import edu.nure.db.dao.interfaces.AlbumDAO;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.SelectException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * Created by bod on 11.11.15.
 */
public class AlbumDAOImpl extends GenericDAOImpl<Album> implements AlbumDAO {

    public AlbumDAOImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Album select(PrimaryKey key) throws SelectException {
        Iterator<Album> a = getAll(Album.class, "WHERE `" + key.getName() + "` = "
                + key.getValue() + " LIMIT 1").iterator();
        if (a.hasNext()) {
            return a.next();
        } else {
            throw new SelectException("No such element");
        }

    }

    @Override
    public List<Album> selectAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Album> getUserAlbum(int userId) throws SelectException {
        return getAll(Album.class, "WHERE UserId = " + userId + " Order by Name");
    }

    /**
     * @param userId
     * @return users albums and images in the album
     */
    @Override
    public Map<Album, List<Image>> getUserAlbums(int userId) throws SelectException {
        Map<Album, List<Image>> result = new HashMap<Album, List<Image>>();
        try (Connection c = connection; Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(
                    "Select UserId, `Name`, a.Id as aId, i.Id, `Hash`, `Image`, `Album`, `CreatedIn` From `ALBUM` as a " +
                            "INNER JOIN `IMAGE` as i on i.Album = a.Id" +
                            " Where `UserId` = " + userId +
                            " Group by `Name`, i.`Id`, `Album`"
            );
            while (rs.next()) {
                Album album = new Album(
                        rs.getString("Name"),
                        rs.getInt("aId"),
                        rs.getInt("UserId")
                );
                Image image = new Image();
                image.parseResultSet(rs);
                List<Image> albumImages = result.get(album);
                if (albumImages == null) {
                    albumImages = new ArrayList<Image>();
                }
                albumImages.add(image);
                result.put(album, albumImages);
            }
            rs.close();
            return result;
        } catch (Exception ex) {
            throw new SelectException(ex);
        }
    }
}

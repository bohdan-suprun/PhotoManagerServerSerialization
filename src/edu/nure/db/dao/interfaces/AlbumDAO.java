package edu.nure.db.dao.interfaces;

import edu.nure.bl.Album;
import edu.nure.bl.Image;
import edu.nure.exceptions.SelectException;

import java.util.List;
import java.util.Map;

/**
 * Created by bod on 11.11.15.
 */
public interface AlbumDAO extends GenericDAO<Album> {

    List<Album> getUserAlbum(int userId) throws SelectException;

    Map<Album, List<Image>> getUserAlbums(int userId) throws SelectException;

}

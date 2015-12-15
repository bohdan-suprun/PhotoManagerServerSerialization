package edu.nure.db.dao.interfaces;

import edu.nure.bl.Image;
import edu.nure.exceptions.SelectException;

import java.util.List;

/**
 * Created by bod on 11.11.15.
 */
public interface ImageDAO extends GenericDAO<Image> {

    List<Image> getLike(String hash, int limit) throws SelectException;

    List<Image> getInAlbum(int albumId) throws SelectException;

}

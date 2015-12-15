package edu.nure.db.dao.interfaces;

import edu.nure.bl.Format;
import edu.nure.exceptions.SelectException;

import java.util.List;

/**
 * Created by bod on 11.11.15.
 */
public interface FormatDAO extends GenericDAO<Format> {

    List<Format> getLikeName(String like) throws SelectException;


}

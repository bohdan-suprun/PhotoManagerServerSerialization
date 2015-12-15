package edu.nure.performers;

import edu.nure.bl.Right;
import edu.nure.db.dao.AbstractDAOFactory;
import edu.nure.db.dao.interfaces.GenericDAO;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.PerformException;
import edu.nure.util.ResponseBuilder;

import java.io.IOException;

/**
 * Created by bod on 21.09.15.
 */
public class RightPerformer extends AbstractPerformer {

    private GenericDAO<Right> dao;

    public RightPerformer(ResponseBuilder b) throws DBException {
        super(b);
        dao = AbstractDAOFactory.getDAO(AbstractDAOFactory.MYSQL).getRightDAO();
    }

    @Override
    public void perform() throws PerformException, IOException, DBException {

        for (Right right : dao.selectAll()) {
            builder.add(right);
        }

        builder.setStatus(ResponseBuilder.STATUS_OK);
    }
}

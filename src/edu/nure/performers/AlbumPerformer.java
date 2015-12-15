package edu.nure.performers;

import edu.nure.Manager;
import edu.nure.bl.Album;
import edu.nure.db.dao.AbstractDAOFactory;
import edu.nure.db.dao.interfaces.AlbumDAO;
import edu.nure.db.primarykey.IntegerPrimaryKey;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.InsertException;
import edu.nure.exceptions.PerformException;
import edu.nure.exceptions.SelectException;
import edu.nure.util.Action;
import edu.nure.util.ResponseBuilder;

import java.io.IOException;

public class AlbumPerformer extends AbstractPerformer {
    private AlbumDAO dao;

    public AlbumPerformer(ResponseBuilder builder) throws DBException {
        super(builder);
        dao = AbstractDAOFactory.getDAO(AbstractDAOFactory.MYSQL).getAlbumDAO();
    }

    @Override
    public void perform() throws PerformException, IOException, DBException {
        int action = builder.getAction();
        switch (action) {
            case Action.GET_ALBUM:
                doGet();
                break;
            case Action.INSERT_ALBUM:
                doInsert();
                break;
            case Action.DELETE_ALBUM:
                doDelete();
        }
    }

    @Override
    protected void doGet() throws PerformException, IOException {
        try {
            int userId = builder.getIntParameter("id");
            if (builder.getParameter("albumId") != null) {
                Album album = dao.select(new IntegerPrimaryKey(Integer.valueOf(builder.getParameter("albumId"))));
                builder.add(album);
            } else {
                for (Album album : dao.getUserAlbum(userId)) {
                    builder.add(album);
                }
            }
            builder.setStatus(ResponseBuilder.STATUS_OK);
        } catch (NumberFormatException ex) {
            Manager.setLog(ex);
            throw new PerformException("Недостаточно параметров");
        } catch (SelectException ex) {
            Manager.setLog(ex);
            throw new PerformException("Ошибка во время работы с базой данных");
        }

    }

    @Override
    protected void doInsert() throws PerformException, IOException {
        try {
            Album a = new Album(builder);

            a = dao.insert(a);
            if (a != null) {
                builder.add(a);
                builder.setStatus(ResponseBuilder.STATUS_OK);
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Ошибка создания нового альбома");
            }
        } catch (InsertException ex) {
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
            builder.setText(ex.getMessage());
        } catch (NumberFormatException ex) {
            Manager.setLog(ex);
            throw new PerformException("Недостаточно параметров");
        } catch (DBException ex) {
            Manager.setLog(ex);
            if (ex.getMessage().toLowerCase().contains("duplicate")) {
                throw new PerformException("Такой альбом уже существует");
            }
            throw new PerformException("Ошибка обработки запроса");
        }
    }

    @Override
    protected void doDelete() throws PerformException, IOException {
        try {
            Album a = new Album(builder);
            if (!dao.delete(a)) {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Ошибка удаления альбома");
            }
        } catch (NumberFormatException ex) {
            throw new PerformException("Недостаточно параметров");
        } catch (DBException ex) {
            Manager.setLog(ex);
            throw new PerformException("Ошибка обработки запроса: альбом содержит фотографии");
        }
    }
}

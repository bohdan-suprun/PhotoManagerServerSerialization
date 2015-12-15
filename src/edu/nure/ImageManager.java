package edu.nure;

import edu.nure.exceptions.DBException;
import edu.nure.exceptions.PerformException;
import edu.nure.performers.ImagePerformer;
import edu.nure.util.Action;
import edu.nure.util.ResponseBuilder;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

public class ImageManager extends HttpServlet {
    DiskFileItemFactory factory;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int action;

        if (ServletFileUpload.isMultipartContent(req)) {
            action = Action.INSERT_IMAGE;
        } else action = Integer.valueOf(req.getParameter("action"));

        ResponseBuilder builder = new ResponseBuilder(req, action);

        try {
            if (action == Action.GET_IMAGE)
                if (!Manager.checkLowRight(req.getSession())) throw new AccessDeniedException("Ошибка прав доступа");
            if (action == Action.DELETE_IMAGE || action == Action.INSERT_IMAGE)
                if (!Manager.checkHiRight(req.getSession())) throw new AccessDeniedException("Ошибка прав доступа");

            ImagePerformer p = new ImagePerformer(builder, factory);
            p.perform();
        } catch (PerformException | AccessDeniedException e) {
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
            builder.setText(e.getMessage());
        } catch (DBException ex) {
            String message = ex.getMessage().toLowerCase();
            if (message.contains("conn")) {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Сервер Баз Данных не отвечает");
            } else if (message.contains("syntax")) {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Ошибка синтаксиса запроса к базе данных");
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неизвестная ошибка при обращении к базе данных");
            }
        } finally {
            resp.setContentType(builder.getContentType());
            builder.writeTo(resp.getOutputStream());
        }
    }

    @Override
    public void init() throws ServletException {
        factory = new DiskFileItemFactory();
    }
}

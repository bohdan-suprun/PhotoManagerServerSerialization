package edu.nure;


import edu.nure.exceptions.DBException;
import edu.nure.performers.*;
import edu.nure.util.Action;
import edu.nure.util.ResponseBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;
import java.util.logging.Logger;


/**
 * Created by bod on 15.09.15.
 */
public class Manager extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(edu.nure.Manager.class.getName());

    public static void setLog(Throwable ex) {
        LOG.warning(ex.getMessage());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseBuilder builder = new ResponseBuilder(null, -1);
        AbstractPerformer p = null;
        try {
            resp.setCharacterEncoding("UTF-8");
            req.setCharacterEncoding("UTF-8");

            int action = Integer.valueOf(Objects.requireNonNull(req.getParameter("action")));
            builder = new ResponseBuilder(req, action);
            builder.setContentType(ResponseBuilder.XML_TYPE);
            if (action == Action.LOGIN) {
                p = new LoginPerformer(builder);
                p.perform();
            } else {
                if (!checkHiRight(req.getSession())) throw new AccessDeniedException("Ошибка прав доступа");
                switch (action) {
                    case Action.REGISTRY:
                        p = new UserPerformer(builder);
                        p.perform();
                        break;
                    case Action.GET_USER:
                        p = new UserPerformer(builder);
                        p.perform();
                        break;
                    case Action.UPDATE_USER:
                        p = new UserPerformer(builder);
                        p.perform();
                        break;
                    case Action.KILL_SESSION:
                        req.getSession().invalidate();
                        builder.setStatus(ResponseBuilder.STATUS_OK);
                        break;

                    //Format
                    case Action.INSERT_FORMAT:
                        p = new FormatPerformer(builder);
                        p.perform();
                        break;
                    case Action.GET_FORMAT:
                        p = new FormatPerformer(builder);
                        p.perform();
                        break;
                    case Action.UPDATE_FORMAT:
                        p = new FormatPerformer(builder);
                        p.perform();
                        break;
                    case Action.DELETE_FORMAT:
                        p = new FormatPerformer(builder);
                        p.perform();
                        break;
                    //Format END

                    case Action.INSERT_ALBUM:
                        p = new AlbumPerformer(builder);
                        p.perform();
                        break;
                    case Action.GET_ALBUM:
                        p = new AlbumPerformer(builder);
                        p.perform();
                        break;
                    case Action.DELETE_ALBUM:
                        p = new AlbumPerformer(builder);
                        p.perform();
                        break;

                    //Right
                    case Action.GET_RIGHT:
                        p = new RightPerformer(builder);
                        p.perform();
                        break;
                    //Urgency
                    case Action.INSERT_URGENCY:
                        new UrgencyPerformer(builder).perform();
                        break;
                    case Action.GET_URGENCY:
                        p = new UrgencyPerformer(builder);
                        p.perform();
                        break;
                    case Action.UPDATE_URGENCY:
                        p = new UrgencyPerformer(builder);
                        p.perform();
                        break;
                    case Action.DELETE_URGENCY:
                        p = new UrgencyPerformer(builder);
                        p.perform();
                        break;
                    //Urgency END

                    //Order
                    case Action.INSERT_ORDER:
                        p = new OrderPerformer(builder);
                        p.perform();
                        break;
                    case Action.GET_ORDER:
                        p = new OrderPerformer(builder);
                        p.perform();
                        break;
                    case Action.UPDATE_ORDER:
                        p = new OrderPerformer(builder);
                        p.perform();
                        break;
                    case Action.DELETE_ORDER:
                        p = new OrderPerformer(builder);
                        p.perform();
                        break;
                    //Order END

                    //Stock
                    case Action.INSERT_STOCK:
                        p = new StockPerformer(builder);
                        p.perform();
                        break;
                    case Action.GET_STOCK:
                        p = new StockPerformer(builder);
                        p.perform();
                        break;
                    case Action.UPDATE_STOCK:
                        p = new StockPerformer(builder);
                        p.perform();
                        break;
                    case Action.DELETE_STOCK:
                        p = new StockPerformer(builder);
                        p.perform();
                        break;
                    //Stock END
                    default:
                        builder.setStatus(ResponseBuilder.STATUS_PARAM_ERROR);

                }
            }
        } catch (DBException ex) {
            String message = ex.getMessage().toLowerCase();
            if (message.contains("comm")) {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Сервер Баз Данных не отвечает");
            } else if (message.contains("syntax")) {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Ошибка синтаксиса запроса к базе данных");
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неизвестная ошибка при обращении к базе данных");
            }
        } catch (Exception ex) {
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
            builder.setText(ex.getMessage());
        } finally {
            resp.setContentType(builder.getContentType());
            builder.writeTo(resp.getOutputStream());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public static boolean checkHiRight(HttpSession session) {
        if (session.getAttribute("id") != null) {
            if (session.getAttribute("right").equals("фотограф") ||
                    session.getAttribute("right").equals("su"))
                return true;
        }
        return false;
    }

    public static boolean checkLowRight(HttpSession session) {
        return session.getAttribute("id") != null;
    }
}

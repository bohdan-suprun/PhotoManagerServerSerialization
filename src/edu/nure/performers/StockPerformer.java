package edu.nure.performers;

import edu.nure.Manager;
import edu.nure.bl.Stock;
import edu.nure.bl.constraints.ValidationException;
import edu.nure.db.dao.AbstractDAOFactory;
import edu.nure.db.dao.interfaces.StockDAO;
import edu.nure.db.primarykey.IntegerPrimaryKey;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.InsertException;
import edu.nure.exceptions.PerformException;
import edu.nure.exceptions.SelectException;
import edu.nure.util.Action;
import edu.nure.util.ResponseBuilder;

import java.io.IOException;
import java.util.Objects;

public class StockPerformer extends AbstractPerformer {

    private StockDAO dao;

    public StockPerformer(ResponseBuilder b) throws DBException {
        super(b);
        dao = AbstractDAOFactory.getDAO(AbstractDAOFactory.MYSQL).getStockDAO();
    }

    @Override
    public void perform() throws PerformException, IOException, DBException {
        int action = builder.getIntParameter("action");
        switch (action) {
            case Action.GET_STOCK:
                doGet();
                break;
            case Action.INSERT_STOCK:
                doInsert();
                break;
            case Action.UPDATE_STOCK:
                doUpdate();
                break;
            case Action.DELETE_STOCK:
                doDelete();
                break;
            default:
                builder.setStatus(ResponseBuilder.STATUS_PARAM_ERROR);
        }
    }

    @Override
    protected void doGet() throws PerformException, IOException {
        try {
            String order = builder.getParameter("order");
            String id = builder.getParameter("id");
            if (order != null) {
                for (Stock stock : dao.getStock(Integer.valueOf(order))) {
                    builder.add(stock);
                }
                builder.setStatus(ResponseBuilder.STATUS_OK);
            }
            if (id != null) {
                Stock stock = Objects.requireNonNull(dao.select(new IntegerPrimaryKey(Integer.valueOf(id))));
                builder.add(stock);
                builder.setStatus(ResponseBuilder.STATUS_OK);
            }
        } catch (NumberFormatException ex) {
            throw new PerformException("Неверный формат данных");
        } catch (NullPointerException ex) {
            throw new PerformException("Заказа с таким номером не существует");
        } catch (SelectException ex) {
            Manager.setLog(ex);
            throw new PerformException("Ошибка во время работы с базой данных");
        }
    }

    @Override
    protected void doInsert() throws PerformException, IOException {
        try {
            Stock stock = new Stock(builder);
            stock = dao.insert(stock);
            if (stock != null) {
                builder.add(stock);
                builder.setStatus(ResponseBuilder.STATUS_OK);
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неудалось добавить задание");
            }
        } catch (InsertException ex) {
            Manager.setLog(ex);
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
            builder.setText(ex.getMessage());
        } catch (DBException e) {
            Manager.setLog(e);
            String mes = e.getMessage().toLowerCase();
            if (mes.contains("foreign key")) {
                throw new PerformException("Поля Заказ, Изображение и Формат должны быть выбраны с поля");
            } else if (mes.contains("inactive")) {
                throw new PerformException("Невозможно добавить задание для неактивного заказа");
            } else {
                throw new PerformException("Ошибка обработки запроса");
            }
        } catch (ValidationException e) {
            throw new PerformException("Ошибка формата данных");
        }
    }

    @Override
    protected void doUpdate() throws PerformException, IOException {
        try {
            Stock stock = new Stock(builder);
            if (dao.update(stock)) {
                builder.setStatus(ResponseBuilder.STATUS_OK);
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неудалось изменить задание");
            }
        } catch (InsertException ex) {
            Manager.setLog(ex);
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
            builder.setText(ex.getMessage());
        } catch (DBException e) {
            Manager.setLog(e);
            String mes = e.getMessage().toLowerCase();
            if (mes.contains("foreign key")) {
                throw new PerformException("Поля Заказ, Изображение и Формат должны быть выбраны с поля");
            } else if (mes.contains("inactive")) {
                throw new PerformException("Невозможно изменить задание для неактивного заказа");
            } else {
                throw new PerformException("Ошибка обработки запроса");
            }
        } catch (ValidationException e) {
            throw new PerformException("Ошибка формата данных");
        }
    }

    @Override
    protected void doDelete() throws PerformException, IOException {
        try {
            Stock stock = new Stock(builder);
            if (dao.delete(stock)) {
                builder.setStatus(ResponseBuilder.STATUS_OK);
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неудалось удалить задание");
            }
        } catch (DBException e) {
            Manager.setLog(e);
            String mes = e.getMessage().toLowerCase();
            if (mes.contains("inactive")) {
                throw new PerformException("Невозможно удалить задание для неактивного заказа");
            } else {
                throw new PerformException("Ошибка обработки запроса");
            }
        } catch (ValidationException e) {
            throw new PerformException("Ошибка формата данных");
        }

    }
}

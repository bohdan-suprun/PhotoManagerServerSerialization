package edu.nure.performers;

import edu.nure.Manager;
import edu.nure.bl.Order;
import edu.nure.bl.User;
import edu.nure.bl.constraints.ValidationException;
import edu.nure.db.dao.AbstractDAOFactory;
import edu.nure.db.dao.interfaces.OrderDAO;
import edu.nure.db.dao.interfaces.UserDAO;
import edu.nure.db.primarykey.IntegerPrimaryKey;
import edu.nure.email.EmailSender;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.InsertException;
import edu.nure.exceptions.PerformException;
import edu.nure.exceptions.SelectException;
import edu.nure.util.Action;
import edu.nure.util.ResponseBuilder;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;

public class OrderPerformer extends AbstractPerformer {
    private OrderDAO dao;

    private static final String MESSAGE_TEXT = "Здравствуйте!\nСпешу сообщить Вам, что Ваш заказ уже готов. Сумма к оплате %.2f"
            + " грн. Спасибо за то, что воспользовались нашими услугами! Хорошего дня!";

    public OrderPerformer(ResponseBuilder b) throws DBException {
        super(b);
        dao = AbstractDAOFactory.getDAO(AbstractDAOFactory.MYSQL).getOrderDAO();
    }

    @Override
    public void perform() throws PerformException, IOException, DBException {
        int action = Integer.valueOf(Objects.requireNonNull(builder.getParameter("action")));
        switch (action) {
            case Action.GET_ORDER:
                doGet();
                break;
            case Action.INSERT_ORDER:
                doInsert();
                break;
            case Action.UPDATE_ORDER:
                doUpdate();
                break;
            case Action.DELETE_ORDER:
                doDelete();
                break;
            default:
                builder.setStatus(ResponseBuilder.STATUS_PARAM_ERROR);
        }
    }

    private void putResult(List<Order> orders) {
        for (Order order : orders) {
            putResult(order);
        }
    }

    private void putResult(Order order) {
        builder.add(order);
        builder.setStatus(ResponseBuilder.STATUS_OK);
    }

    @Override
    protected void doGet() throws PerformException, IOException {
        try {
            String customer = builder.getParameter("customer");
            String responsible = builder.getParameter("responsible");
            boolean isActive = builder.getParameter("active") != null;
            String id = builder.getParameter("id");
            if (customer != null) {
                int customerId = Integer.valueOf(customer);
                if (isActive) {
                    putResult(dao.getActiveByCustomer(customerId));
                } else {
                    putResult(dao.getByCustomer(customerId));
                }
            }
            if (responsible != null) {
                int responsibleId = Integer.valueOf(responsible);
                if (isActive) {
                    putResult(dao.getActiveByResponsible(responsibleId));
                } else {
                    putResult(dao.getByResponsible(responsibleId));
                }
            }
            if (id != null) {
                int orderId = Integer.valueOf(id);
                if (isActive) {
                    putResult(dao.getActiveById(orderId));
                } else {
                    putResult(dao.select(new IntegerPrimaryKey(orderId)));
                }
            }
        } catch (NumberFormatException ex) {
            throw new PerformException("Неверный формат данных");
        } catch (SelectException ex) {
            Manager.setLog(ex);
            throw new PerformException("Ошибка во время работы с базой данных");
        }
    }

    @Override
    protected void doInsert() throws PerformException, IOException {
        try {
            Order order = new Order(builder);
            order = dao.insert(order);
            if (order != null) {
                builder.add(order);
                builder.setStatus(ResponseBuilder.STATUS_OK);
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неудалось добавить заказ");
            }
        } catch (InsertException ex) {
            Manager.setLog(ex);
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
            builder.setText(ex.getMessage());
        } catch (DBException e) {
            Manager.setLog(e);
            String mes = e.getMessage();
            if (mes.contains("foreign key")) {
                throw new PerformException("Поля Покупатель, Ответственный и Срочность должны быть выбраны с поля");
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
            Order order = new Order(builder);
            Order previousOrder = dao.updatePrevious(order);
            builder.setStatus(ResponseBuilder.STATUS_OK);
            // if inactivating of the order, send email to customer
            if (order.getStatus() == 0 && previousOrder.getStatus() == 1) {
                dao = AbstractDAOFactory.getDAO(AbstractDAOFactory.MYSQL).getOrderDAO();
                order = dao.select(order.getPrimaryKey());

                UserDAO userDAO = AbstractDAOFactory.getDAO(AbstractDAOFactory.MYSQL).getUserDAO();
                User user = userDAO.select(new IntegerPrimaryKey(order.getCustomer()));
                if (user.getEmail() != null) {
                    EmailSender sender = new EmailSender();
                    Formatter f = new Formatter();
                    String message = f.format(MESSAGE_TEXT, order.getForPay()).toString();
                    sender.send("PManager - Ваш заказ", message, user.getEmail());
                }
            }

        } catch (InsertException ex) {
            Manager.setLog(ex);
            builder.setText(ex.getMessage());
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
        } catch (SelectException ex) {
            Manager.setLog(ex);
            builder.setText(ex.getMessage());
            builder.setStatus(ResponseBuilder.STATUS_ERROR_READ);
        } catch (DBException e) {
            Manager.setLog(e);
            String mes = e.getMessage();
            builder.setText("exception");
            if (mes.contains("foreign key"))
                throw new PerformException("Поля Покупатель, Ответственный и Срок должны быть выбраны с поля");
            if (mes.contains("deactivated"))
                throw new PerformException("Невозможно обновить неактивный заказ");
            throw new PerformException("Ошибка обработки запроса");
        } catch (ValidationException e) {
            Manager.setLog(e);
            throw new PerformException("Ошибка формата данных");
        }
    }

    @Override
    protected void doDelete() throws PerformException, IOException {
        try {
            Order order = new Order(builder);
            if (dao.delete(order)) {
                builder.setStatus(ResponseBuilder.STATUS_OK);
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неудалось удалить заказ");
            }
        } catch (DBException e) {
            Manager.setLog(e);
            String msg = e.getMessage();
            if (msg.contains("deactivated"))
                throw new PerformException("Невозможно удалить неактивный заказ");
            throw new PerformException("Ошибка обработки запроса");
        } catch (ValidationException e) {
            throw new PerformException("Ошибка формата данных");
        } catch (NullPointerException e) {
            throw new PerformException("Не найдены нужные параметры");
        }

    }
}

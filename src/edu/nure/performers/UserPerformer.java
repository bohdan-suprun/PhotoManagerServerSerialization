package edu.nure.performers;

import edu.nure.Manager;
import edu.nure.bl.InsertedUser;
import edu.nure.bl.User;
import edu.nure.bl.constraints.ValidationException;
import edu.nure.db.dao.AbstractDAOFactory;
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
import java.util.List;

public class UserPerformer extends AbstractPerformer {

    private UserDAO dao;

    public UserPerformer(ResponseBuilder b) throws DBException {
        super(b);
        dao = AbstractDAOFactory.getDAO(AbstractDAOFactory.MYSQL).getUserDAO();
    }

    @Override
    public void perform() throws PerformException, IOException, DBException {
        int action = builder.getAction();
        switch (action) {
            case Action.GET_USER:
                doGet();
                break;
            case Action.REGISTRY:
                doInsert();
                break;
            case Action.UPDATE_USER:
                doUpdate();
                break;
        }
    }

    @Override
    protected void doGet() throws PerformException, IOException {
        try {
            String name = builder.getParameter("name");
            String id = builder.getParameter("id");
            String phone = builder.getParameter("phone");
            boolean hiRight = builder.getParameter("hiRight") != null;
            boolean ajax = builder.getParameter("ajax") != null;
            if (name != null) {
                List<User> userList;
                if (ajax) {
                    userList = dao.getByName(name, hiRight);

                } else {
                    userList = dao.getAllNames(name);
                }
                for (User user : userList) {
                    builder.add(user);
                }
                builder.setStatus(ResponseBuilder.STATUS_OK);
            }
            if (phone != null) {
                for (User user : dao.getByPhone(phone)) {
                    builder.add(user);
                }
                builder.setStatus(ResponseBuilder.STATUS_OK);
            }
            if (id != null) {
                User user = dao.select(new IntegerPrimaryKey(Integer.valueOf(id)));
                if (user != null) {
                    builder.add(user);
                }
                builder.setStatus(ResponseBuilder.STATUS_OK);
            }
        } catch (NullPointerException ex) {
            throw new PerformException("Не указан нужный параметер");
        } catch (SelectException ex) {
            Manager.setLog(ex);
            throw new PerformException("Ошибка во время работы с базой данных");
        }
    }

    @Override
    protected void doInsert() throws PerformException, IOException {
        try {
            User user = new User(builder);
            InsertedUser insertedUser = dao.insertCode(user);
            String autCode = insertedUser.getCode();
            user = insertedUser.getUser();

            if (user.getId() != User.ID_NOT_SET && autCode != null) {
                if (user.getEmail() != null) {
                    EmailSender sender = new EmailSender();
                    sender.send("Photo Studio Registration", "Уважаемый(-ая) " + user.getName() + "!\n" +
                                    "Вы воспользовались услугами нашей студии." +
                                    " Рекомендуем пройти по ссылке https://" + builder.getRequest().getServerName() + "/user?aut=" +
                                    autCode +
                                    " для регистрации.\nХорошего дня, и " +
                                    "благодарим за доверие!",
                            user.getEmail());
                }
                builder.add(user);
                builder.setStatus(ResponseBuilder.STATUS_OK);
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неудалось добавить пользователя");
            }
        } catch (InsertException ex) {
            Manager.setLog(ex);
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
            builder.setText(ex.getMessage());
        } catch (DBException e) {
            Manager.setLog(e);
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("unique")) throw new PerformException("Пользователь с таким адрессом почты или телефоном" +
                    " уже зарегестрирован");
            else {
                if (msg.contains("foreign key"))
                    throw new PerformException("Права должны иметь значение Фотограф или Покупатель");

                else throw new PerformException("Ошибка обработки запроса");
            }
        } catch (ValidationException ex) {
            Manager.setLog(ex);
            throw new PerformException("Ошибка формата данных");
        }

    }

    @Override
    protected void doUpdate() throws PerformException, IOException {
        try {
            User user = new User(builder);

            if (dao.update(user)) {
                builder.setStatus(ResponseBuilder.STATUS_OK);
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неудалось изменить пользователя");
            }
        } catch (InsertException ex) {
            Manager.setLog(ex);
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
            builder.setText(ex.getMessage());
        } catch (DBException e) {
            Manager.setLog(e);
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("unique")) throw new PerformException("Пользователь с таким адрессом почты или телефоном" +
                    " уже зарегестрирован");
            else {
                if (msg.contains("foreign key")) {
                    throw new PerformException("Права должны иметь значения Фотограф или Покупатель" + e.getMessage());
                } else {
                    throw new PerformException("Ошибка обработки запроса");
                }
            }
        } catch (ValidationException e) {
            throw new PerformException("Ошибка формата данных");
        }
    }


}

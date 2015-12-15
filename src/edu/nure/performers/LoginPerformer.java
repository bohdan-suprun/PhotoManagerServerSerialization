package edu.nure.performers;

import edu.nure.bl.User;
import edu.nure.bl.constraints.ValidationException;
import edu.nure.bl.constraints.Validator;
import edu.nure.db.dao.AbstractDAOFactory;
import edu.nure.db.dao.interfaces.UserDAO;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.PerformException;
import edu.nure.util.ResponseBuilder;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by bod on 18.09.15.
 */
public class LoginPerformer extends AbstractPerformer {
    private UserDAO dao;

    public LoginPerformer(ResponseBuilder b) throws DBException {
        super(b);
        dao = AbstractDAOFactory.getDAO(AbstractDAOFactory.MYSQL).getUserDAO();
    }

    @Override
    public void perform() throws PerformException, IOException, DBException {

        try {
            String phone = Validator.validate(Objects.requireNonNull(builder.getParameter("phone")), Validator.PHONE_VALIDATOR);
            String pass = Objects.requireNonNull(builder.getParameter("password"));
            User user = dao.login(phone, pass);
            if (user != null) {
                if ((user.getRight().getType().equals("фотограф") ||
                        user.getRight().getType().equals("su"))) {

                    builder.add(user);
                    builder.getRequest().getSession().setAttribute("id", user.getId());
                    builder.getRequest().getSession().setAttribute("right", user.getRight().getType());
                    builder.setStatus(ResponseBuilder.STATUS_OK);
                } else {
                    throw new PerformException("Ошибка прав доступа.");
                }
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_READ);
                builder.setText("Пользователя с таким номером телефона не существует, или не верен пароль");
            }
        } catch (NullPointerException ex) {
            throw new PerformException("Parameter count doesn't match");
        } catch (ValidationException e) {
            throw new PerformException("Validation error");
        }
    }
}

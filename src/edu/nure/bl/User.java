package edu.nure.bl;

import edu.nure.bl.constraints.ValidationException;
import edu.nure.bl.constraints.Validator;
import edu.nure.db.entity.basic.AbstractEntity;
import edu.nure.db.primarykey.IntegerPrimaryKey;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.DBException;
import edu.nure.util.ResponseBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by bod on 17.09.15.
 */
public class User extends AbstractEntity {

    private static final long serialVersionUID = 5222501006936056025L;
    private int id = ID_NOT_SET;
    private String phone;
    private String name;
    private String password;
    private String email;
    private Right right;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ValidationException {
        try {
            if (email != null) {
                this.email = Validator.validate(email, Validator.EMAIL_VALIDATOR);
            }
        } catch (ValidationException ex) {
            throw new ValidationException("Адресс электронной почты указан неверно");
        }

    }

    public User() {

    }

    public User(int id, String name, String phone, String email, String password, Right right) throws ValidationException {
        this.id = id;
        setPhone(phone);
        setName(name);
        setEmail(email);
        this.password = password;
        this.right = right;

    }

    @Override
    public void parseResultSet(ResultSet result) throws DBException, ValidationException {
        try {
            setId(result.getInt("Id"));
            setName(result.getString("Name"));
            setPhone(result.getString("Phone"));
            setEmail(result.getString("Email"));
            setPassword(result.getString("Password"));
            setRight(new Right(result.getString("Right"), null));
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

    public User(ResponseBuilder req) throws ValidationException {
        String id = req.getParameter("id");
        if (id != null)
            setId(Integer.valueOf(id));
        else setId(ID_NOT_SET);
        setName(req.getParameter("name"));
        setPhone(req.getParameter("phone"));
        setEmail(req.getParameter("email"));
        setPassword(req.getParameter("password"));
        setRight(new Right(req.getParameter("right"), null));
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    private void setPhone(String phone) throws ValidationException {
        try {
            this.phone = Objects.requireNonNull(Validator.validate(phone, Validator.PHONE_VALIDATOR));
        } catch (NullPointerException ex) {
            throw new ValidationException("Вы не казали номер телефона");
        } catch (ValidationException ex) {
            throw new ValidationException("Номер телефона в неверном формате. Пример: +38(xxx) yyy-yy-yy");
        }
    }

    public String getName() {
        return name;
    }

    private void setName(String name) throws ValidationException {
        try {
            this.name = Objects.requireNonNull(Validator.validate(name.replace('"', '\''), Validator.NAME_VALIDATOR))
                    .replace("\'", "\"");
        } catch (NullPointerException ex) {
            throw new ValidationException("Вы забыли указать имя");
        } catch (ValidationException ex) {
            throw new ValidationException("Имя должно состоять только из букв, может содержать символ '");
        }
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public Right getRight() {
        return right;
    }

    private void setRight(Right right) {
        this.right = right;
    }

//    @Override
//    public String toXML() {
//        return "<user " + "id=\"" + id + "\" name=\"" + name.replace('"', '\'') + "\""
//                + " phone=\"" + phone + "\""
//                + ((email == null) ? "" : " email=\"" + email.replace('"', '\'') + "\"")
//                + " right=\"" + right.getType() + "\"/>";
//    }

    @Override
    public String toQuery() {
        return "id=" + id +
                "&phone=" + phone +
                "&name=" + name +
                "&email=" + email +
                "&right=" + right.getType();
    }

    public String[] getFields() {
        return new String[]{"Name", "Phone", "Email", "Right"};
    }

    @Override
    public Object[] getValues() {
        return new Object[]{
                getName(), getPhone(), getEmail(), getRight().getType()
        };
    }

    @Override
    public String entityName() {
        return "USER";
    }

    @Override
    public PrimaryKey getPrimaryKey() {
        return new IntegerPrimaryKey(getId());
    }
}

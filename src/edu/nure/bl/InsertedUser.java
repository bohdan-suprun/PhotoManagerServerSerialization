package edu.nure.bl;

/**
 * Created by bod on 02.12.15.
 */
public class InsertedUser {

    private User user;
    private String code;

    public InsertedUser(User user, String code) {
        this.user = user;
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public String getCode() {
        return code;
    }
}

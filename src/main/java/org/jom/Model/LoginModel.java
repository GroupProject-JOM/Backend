package org.jom.Model;

import org.jom.Dao.UserDAO;

public class LoginModel {
    private String username;
    private String password;

    public LoginModel(String username) {
        this.username = username;
    }

    public LoginModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserModel getUser(){
        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserByEmail(this.username);
        return  user;
    }
}

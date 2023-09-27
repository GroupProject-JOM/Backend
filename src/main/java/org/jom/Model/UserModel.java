package org.jom.Model;

import org.jom.Dao.UserDAO;

public class UserModel {
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String phone;
    private String add_line_1;
    private String add_line_2;
    private String add_line_3;

    public UserModel(String first_name, String last_name, String email, String password, String phone, String add_line_1, String add_line_2, String add_line_3) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.add_line_1 = add_line_1;
        this.add_line_2 = add_line_2;
        this.add_line_3 = add_line_3;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAdd_line_1() {
        return add_line_1;
    }

    public String getAdd_line_2() {
        return add_line_2;
    }

    public String getAdd_line_3() {
        return add_line_3;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean Register(){
        UserDAO userDAO = new UserDAO();
        boolean status = userDAO.register(this);
        return  status;
    }

    public boolean EmailExists(){
        UserDAO userDAO = new UserDAO();
        boolean status = userDAO.emailExists(this.email);
        return  status;
    }
}

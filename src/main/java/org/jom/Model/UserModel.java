package org.jom.Model;

import org.jom.Dao.Supplier.EstateDAO;
import org.jom.Dao.UserDAO;

public class UserModel {
    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String phone;
    private String add_line_1;
    private String add_line_2;
    private String add_line_3;
    private String role;
    private int validity;

    public UserModel() {
    }

    public UserModel(int id) {
        this.id = id;
    }

    public UserModel(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public UserModel(int id, String first_name, String last_name, String phone) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
    }

    public UserModel(String first_name, String last_name, String phone, String add_line_1, String add_line_2, String add_line_3, String role) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.add_line_1 = add_line_1;
        this.add_line_2 = add_line_2;
        this.add_line_3 = add_line_3;
        this.role = role;
    }

    public UserModel(int id, String first_name, String phone, String add_line_3, String role) {
        this.id = id;
        this.first_name = first_name;
        this.phone = phone;
        this.add_line_3 = add_line_3;
        this.role = role;
    }

    public UserModel(String first_name, String last_name, String email, String password, String phone, String add_line_1, String add_line_2, String add_line_3, String role) {
        this.id = 0;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.add_line_1 = add_line_1;
        this.add_line_2 = add_line_2;
        this.add_line_3 = add_line_3;
        this.role = role;
        this.validity = 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAdd_line_1(String add_line_1) {
        this.add_line_1 = add_line_1;
    }

    public void setAdd_line_2(String add_line_2) {
        this.add_line_2 = add_line_2;
    }

    public void setAdd_line_3(String add_line_3) {
        this.add_line_3 = add_line_3;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public int getId() {
        return id;
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

    public String getRole() {
        return role;
    }

    public int getValidity() {
        return validity;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void Register(){
        UserDAO userDAO = new UserDAO();
        this.id = userDAO.register(this);
        EmployeeRegister();
    }

    public boolean updateUser(){
        UserDAO userDAO = new UserDAO();
        updateEmployee();
        return userDAO.updateUser(this);
    }

    public void EmployeeRegister() {
    }

    public boolean updateEmployee(){
        return true;}

    public boolean EmailExists(){
        UserDAO userDAO = new UserDAO();
        boolean status = userDAO.emailExists(this.email);
        return  status;
    }

    public void updateValidity(int validity){
        UserDAO userDAO = new UserDAO();
        userDAO.updateValidity(this.id);
        this.setValidity(1);
    }
    public boolean updateEmail(){
        UserDAO userDAO = new UserDAO();
        return userDAO.updateEmail(this.email,this.id);
    }

}

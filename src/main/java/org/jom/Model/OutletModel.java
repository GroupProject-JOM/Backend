package org.jom.Model;

import org.jom.Dao.OutletDAO;
import org.jom.Dao.Supplier.AccountDAO;

public class OutletModel {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address1;
    private String street;
    private String city;
    private int emp_id;

    public OutletModel() {
    }

    public OutletModel(int id, String name, String email, String phone, String address1, String street, String city) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address1 = address1;
        this.street = street;
        this.city = city;
    }

    public OutletModel(String name, String email, String phone, String address1, String street, String city, int emp_id) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address1 = address1;
        this.street = street;
        this.city = city;
        this.emp_id = emp_id;
    }

    public OutletModel(int id, String name, String email, String phone, String address1, String street, String city, int emp_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address1 = address1;
        this.street = street;
        this.city = city;
        this.emp_id = emp_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(int emp_id) {
        this.emp_id = emp_id;
    }

    public void addOutlet(){
        OutletDAO outletDAO = new OutletDAO();
        this.id = outletDAO.addOutlet(this);
    }
    public boolean updateOutlet(){
        OutletDAO outletDAO = new OutletDAO();
        return outletDAO.updateOutlet(this);
    }
}

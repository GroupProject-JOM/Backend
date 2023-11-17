package org.jom.Model.Collection;

import java.sql.Time;
import java.util.Date;

public class SupplyModel {
    private int id;
    private String date;
    private String time;
    private int amount;
    private int status;
    private int final_amount;
    private int value;
    private String name;
    private String method;
    private String last_name;
    private String phone;
    private String payment_method;
    private String location;
    private String area;

    public SupplyModel() {
    }

    public SupplyModel(int id, String date, String time, int amount, int status, int final_amount, int value) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.status = status;
        this.final_amount = final_amount;
        this.value = value;
    }

    public SupplyModel(int id, String date, int amount, String name, String method, int status) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.name = name;
        this.method = method;
        this.status = status;
    }

    public SupplyModel(int id, String date, String time, int amount, String name, String last_name, String phone, String location, String area) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.name = name;
        this.last_name = last_name;
        this.phone = phone;
        this.location = location;
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

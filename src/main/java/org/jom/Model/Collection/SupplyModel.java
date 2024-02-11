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
    private String address;
    private String h_name;
    private String account;
    private String bank;
    private String c_fName;
    private String c_lName;
    private String c_phone;
    private String collected_date;
    private String reason;
    private int supplier_id;
    private String ename;
    private int estate_id;
    private int account_id;

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

    public SupplyModel(int id, int status, int value, String name, String last_name, String payment_method) {
        this.id = id;
        this.status = status;
        this.value = value;
        this.name = name;
        this.last_name = last_name;
        this.payment_method = payment_method;
    }

    public SupplyModel(int id, int amount, int status, String name, String last_name, String phone, String area) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.name = name;
        this.last_name = last_name;
        this.phone = phone;
        this.area = area;
    }

    public SupplyModel(int id, int amount, String name, String last_name, String c_fName, String c_lName, String time) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.last_name = last_name;
        this.c_fName = c_fName;
        this.c_lName = c_lName;
        this.time = time;
    }

    // for past supply table supplier dashboard
    public SupplyModel(int id, String date, int status, int final_amount, int value, String method) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.final_amount = final_amount;
        this.value = value;
        this.method = method;
    }

    public SupplyModel(int id, int amount, String name, String method, String last_name, String c_fName, String c_lName, String time) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.method = method;
        this.last_name = last_name;
        this.c_fName = c_fName;
        this.c_lName = c_lName;
        this.time = time;
    }

    // get all delete collections
    public SupplyModel(int id, String date, int amount, String name, String method, String last_name) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.name = name;
        this.method = method;
        this.last_name = last_name;
    }

    public SupplyModel(int id, String date, int amount, String name, String method, int status) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.name = name;
        this.method = method;
        this.status = status;
    }

    public SupplyModel(int id, String date, String time, int amount, String name, String last_name, String phone, String location, String area, String payment_method) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.name = name;
        this.last_name = last_name;
        this.phone = phone;
        this.location = location;
        this.area = area;
        this.payment_method = payment_method;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getH_name() {
        return h_name;
    }

    public void setH_name(String h_name) {
        this.h_name = h_name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setFinal_amount(int final_amount) {
        this.final_amount = final_amount;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getAmount() {
        return amount;
    }

    public int getStatus() {
        return status;
    }

    public int getFinal_amount() {
        return final_amount;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getLocation() {
        return location;
    }

    public String getArea() {
        return area;
    }

    public String getC_fName() {
        return c_fName;
    }

    public void setC_fName(String c_fName) {
        this.c_fName = c_fName;
    }

    public String getC_lName() {
        return c_lName;
    }

    public void setC_lName(String c_lName) {
        this.c_lName = c_lName;
    }

    public String getC_phone() {
        return c_phone;
    }

    public void setC_phone(String c_phone) {
        this.c_phone = c_phone;
    }

    public String getCollected_date() {
        return collected_date;
    }

    public void setCollected_date(String collected_date) {
        this.collected_date = collected_date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public int getEstate_id() {
        return estate_id;
    }

    public void setEstate_id(int estate_id) {
        this.estate_id = estate_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
}

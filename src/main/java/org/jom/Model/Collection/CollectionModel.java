package org.jom.Model.Collection;

import org.jom.Dao.Supplier.Collection.CollectionDAO;

public class CollectionModel {
    private int id;
    private int supplier_id;
    private int initial_amount;
    private int final_amount;
    private int value;
    private String payment_method;
    private String supply_method;
    private int status;
    // 0 - Request not completed
    // 1 - pending approval
    // 2 - accepted
    // 3 - Ready to pick-up
    //   - waiting for verification
    // 4 - reject
    // 5 - pending payment
    // 6 - paid
    private int account;
    private int estate;
    private String date;
    private String time;

    public CollectionModel(int initial_amount, String payment_method, String supply_method) {
        this.initial_amount = initial_amount;
        this.payment_method = payment_method;
        this.supply_method = supply_method;
    }

    public CollectionModel(int id, int initial_amount, String payment_method, String supply_method, int account, int estate, String date, String time) {
        this.id = id;
        this.initial_amount = initial_amount;
        this.payment_method = payment_method;
        this.supply_method = supply_method;
        this.account = account;
        this.estate = estate;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public int getInitial_amount() {
        return initial_amount;
    }

    public int getFinal_amount() {
        return final_amount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getSupply_method() {
        return supply_method;
    }

    public int getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public void setInitial_amount(int initial_amount) {
        this.initial_amount = initial_amount;
    }

    public void setFinal_amount(int final_amount) {
        this.final_amount = final_amount;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public void setSupply_method(String supply_method) {
        this.supply_method = supply_method;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getEstate() {
        return estate;
    }

    public void setEstate(int estate) {
        this.estate = estate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void addCollection() {
        CollectionDAO collectionDAO = new CollectionDAO();
        this.id = collectionDAO.addCollection(this);
    }
}

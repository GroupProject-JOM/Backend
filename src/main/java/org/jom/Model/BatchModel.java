package org.jom.Model;

import org.jom.Dao.BatchDAO;

public class BatchModel {
    private int id;
    private int amount;
    private String amount_by;
    private String requests;
    private String products;
    private String create_date;
    private int status;
    private String days;
    private String products_count;

    public BatchModel() {
    }

    public BatchModel(int amount, String amount_by, String requests, String products, String days) {
        this.amount = amount;
        this.amount_by = amount_by;
        this.requests = requests;
        this.products = products;
        this.days = days;
    }

    public BatchModel(int id, int amount, String products, String create_date, int status) {
        this.id = id;
        this.amount = amount;
        this.products = products;
        this.create_date = create_date;
        this.status = status;
    }

    public BatchModel(int id, int amount, int status, String amount_by, String requests, String products) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.amount_by = amount_by;
        this.requests = requests;
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getAmount_by() {
        return amount_by;
    }

    public void setAmount_by(String amount_by) {
        this.amount_by = amount_by;
    }

    public String getRequests() {
        return requests;
    }

    public void setRequests(String requests) {
        this.requests = requests;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int createBatch() {
        BatchDAO batchDAO = new BatchDAO();
        return batchDAO.createProductionBatch(this);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProducts_count() {
        return products_count;
    }

    public void setProducts_count(String products_count) {
        this.products_count = products_count;
    }
}

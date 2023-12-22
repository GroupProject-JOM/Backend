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

    public BatchModel(int amount, String amount_by, String requests, String products) {
        this.amount = amount;
        this.amount_by = amount_by;
        this.requests = requests;
        this.products = products;
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
}

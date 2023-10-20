package org.jom.Model.Collection;

import org.jom.Dao.Supplier.Collection.PickupDAO;
import org.jom.Dao.Supplier.Collection.YardDAO;

public class YardModel {
    private int id;
    private String date;
    private String time;
    private int supplier_id;
    private int collection_id;
    private int account_id;

    public YardModel(String date, String time, int supplier_id, int collection_id, int account_id) {
        this.date = date;
        this.time = time;
        this.supplier_id = supplier_id;
        this.collection_id = collection_id;
        this.account_id = account_id;
    }

    public YardModel(String date, String time, int supplier_id, int collection_id) {
        this.date = date;
        this.time = time;
        this.supplier_id = supplier_id;
        this.collection_id = collection_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public int getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(int collection_id) {
        this.collection_id = collection_id;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public void addYard(){
        YardDAO yardDAO = new YardDAO();
        this.id = yardDAO.addYard(this);
    }
}

package org.jom.Model;

public class CocoModel {
    private int id;
    private int user;
    private String date;
    private String price;

    public CocoModel() {
    }

    public CocoModel(String date, String price) {
        this.date = date;
        this.price = price;
    }

    public CocoModel(int id, String date, String price) {
        this.id = id;
        this.date = date;
        this.price = price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }

    public int getUser() {
        return user;
    }
}

package org.jom.Model;

public class YardModel {
    private int id;
    private int count;
    private int days;
    private String date;

    public YardModel() {
    }

    public YardModel(int count, String date) {
        this.count = count;
        this.date = date;
    }

    public YardModel(int id, int count, int days) {
        this.id = id;
        this.count = count;
        this.days = days;
    }

    public YardModel(int id, int days, int count, String date) {
        this.id = id;
        this.count = count;
        this.days = days;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

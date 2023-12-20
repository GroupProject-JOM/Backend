package org.jom.Model;

public class ProductionModel {
    private int id;
    private int yard;
    private int block;
    private int amount;
    private int status;
    // 1 requested/pending
    // 2 accepted
    // 3 rejected
    // 4 completed
    private String date;
    private int user;
    private String reason;
    private int actual;

    public ProductionModel() {
    }

    public ProductionModel(int id, int yard, int block, int amount, int user) {
        this.id = id;
        this.yard = yard;
        this.block = block;
        this.amount = amount;
        this.user = user;
    }

    public ProductionModel(int yard, int block, int amount, int user) {
        this.yard = yard;
        this.block = block;
        this.amount = amount;
        this.user = user;
    }

    public ProductionModel(int id, int yard, int block, int amount, int status, String date) {
        this.id = id;
        this.yard = yard;
        this.block = block;
        this.amount = amount;
        this.status = status;
        this.date = date;
    }

    public ProductionModel(int id, int yard, int block, int amount, int status, String date, int actual) {
        this.id = id;
        this.yard = yard;
        this.block = block;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.actual = actual;
    }

    public int getId() {
        return id;
    }

    public int getYard() {
        return yard;
    }

    public int getBlock() {
        return block;
    }

    public int getAmount() {
        return amount;
    }

    public int getUser() {
        return user;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setYard(int yard) {
        this.yard = yard;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

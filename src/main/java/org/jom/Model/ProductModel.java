package org.jom.Model;

public class ProductModel {
    private int id;
    private String type;
    private String category;
    private String price;
    private int status;

    public ProductModel() {
    }

    public ProductModel(int id, String type, String category, String price, int status) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.price = price;
        this.status = status;
    }

    public ProductModel(String type, String category) {
        this.type = type;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

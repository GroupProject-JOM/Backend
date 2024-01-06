package org.jom.Model;

public class DistributionModel {
    private int id;
    private String first_name;
    private String last_name;
    private int remaining;
    private String type;
    private String category;
    private int product;
    private String price;

    public DistributionModel(int id, String first_name, String last_name, int remaining) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.remaining = remaining;
    }

    public DistributionModel(int id, int remaining, String type, String category, int product) {
        this.id = id;
        this.remaining = remaining;
        this.type = type;
        this.category = category;
        this.product = product;
    }

    public DistributionModel(int remaining, String type, String category, int product, String price) {
        this.remaining = remaining;
        this.type = type;
        this.category = category;
        this.product = product;
        this.price = price;
    }
}

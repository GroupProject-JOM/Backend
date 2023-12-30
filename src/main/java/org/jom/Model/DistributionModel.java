package org.jom.Model;

public class DistributionModel {
    private int id;
    private String first_name;
    private String last_name;
    private int remaining;

    public DistributionModel(int id, String first_name, String last_name, int remaining) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.remaining = remaining;
    }
}

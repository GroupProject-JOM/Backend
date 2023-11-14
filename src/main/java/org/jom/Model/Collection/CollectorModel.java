package org.jom.Model.Collection;

public class CollectorModel {
    private int employee_id;
    private String name;
    private int row_count;

    public CollectorModel(int employee_id, String name, int row_count) {
        this.employee_id = employee_id;
        this.name = name;
        this.row_count = row_count;
    }
}

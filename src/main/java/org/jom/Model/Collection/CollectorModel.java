package org.jom.Model.Collection;

public class CollectorModel {
    private int employee_id;
    private int user_id;
    private String name;
    private String last_name;
    private String phone;
    private int collection_count;
    private int today_total;
    private int row_count;

    public CollectorModel(int employee_id, String name, int row_count, String last_name) {
        this.employee_id = employee_id;
        this.name = name;
        this.row_count = row_count;
        this.last_name = last_name;
    }

    public CollectorModel(int user_id, String name, String last_name, String phone, int collection_count, int today_total) {
        this.user_id = user_id;
        this.name = name;
        this.last_name = last_name;
        this.phone = phone;
        this.collection_count = collection_count;
        this.today_total = today_total;
    }
}

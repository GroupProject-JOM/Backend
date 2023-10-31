package org.jom.Model.Collection;

import java.sql.Time;
import java.util.Date;

public class SupplyModel {
    private int id;
    private String date;
    private String time;
    private int amount;
    private int status;
    private int final_amount;
    private int value;

    public SupplyModel(int id, String date, String time, int amount, int status,int final_amount,int value) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.status = status;
        this.final_amount = final_amount;
        this.value = value;
    }
}

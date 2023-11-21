package org.jom.Model.Collection;

public class CollectionSingleViewModel {
    private int collection_id;
    private String sMethod;
    private String pMethod;
    private String date;
    private String time;
    private int init_amount;
    private int final_amount;
    private int status;
    private int value;
    private int account;
    private int estate;

    public CollectionSingleViewModel() {
    }

    public int getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(int collection_id) {
        this.collection_id = collection_id;
    }

    public String getsMethod() {
        return sMethod;
    }

    public void setsMethod(String sMethod) {
        this.sMethod = sMethod;
    }

    public String getpMethod() {
        return pMethod;
    }

    public void setpMethod(String pMethod) {
        this.pMethod = pMethod;
    }

    public int getInit_amount() {
        return init_amount;
    }

    public void setInit_amount(int init_amount) {
        this.init_amount = init_amount;
    }

    public int getFinal_amount() {
        return final_amount;
    }

    public void setFinal_amount(int final_amount) {
        this.final_amount = final_amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getEstate() {
        return estate;
    }

    public void setEstate(int estate) {
        this.estate = estate;
    }

}

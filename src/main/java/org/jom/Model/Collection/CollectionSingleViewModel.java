package org.jom.Model.Collection;

public class CollectionSingleViewModel {
    private int collection_id;
    private String sMethod;
    private String pMethod;
    private int init_amount;
    private int final_amount;
    private String name;
    private String phone;
    private String date;
    private String time;
    private int status;

    public CollectionSingleViewModel() {
    }

    public CollectionSingleViewModel(int collection_id, String sMethod, String pMethod, int init_amount, int final_amount, String name, String phone, int status) {
        this.collection_id = collection_id;
        this.sMethod = sMethod;
        this.pMethod = pMethod;
        this.init_amount = init_amount;
        this.final_amount = final_amount;
        this.name = name;
        this.phone = phone;
        this.status = status;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

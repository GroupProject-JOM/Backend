package org.jom.Model;

import org.jom.Dao.Supplier.EstateDAO;

public class EstateModel {
    private int id;
    private int supplier_id;
    private String estate_name;
    private String estate_location;
    private  String area;

    public EstateModel(int supplier_id, String estate_name, String estate_location, String area) {
        this.id = 0;
        this.supplier_id = supplier_id;
        this.estate_name = estate_name;
        this.estate_location = estate_location;
        this.area = area;
    }

    public EstateModel(int id, String estate_name, String estate_location, String area, int supplier_id) {
        this.id = id;
        this.supplier_id = supplier_id;
        this.estate_name = estate_name;
        this.estate_location = estate_location;
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getEstate_name() {
        return estate_name;
    }

    public void setEstate_name(String estate_name) {
        this.estate_name = estate_name;
    }

    public String getEstate_location() {
        return estate_location;
    }

    public void setEstate_location(String estate_location) {
        this.estate_location = estate_location;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void addEstate(){
        EstateDAO estateDAO = new EstateDAO();
        this.id = estateDAO.addEstate(this);
    }
}

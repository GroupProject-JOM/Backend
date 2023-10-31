package org.jom.Model;

import org.jom.Dao.Supplier.SupplierDAO;

public class SupplierModel {
    private int id;
    private int user_id;

    public SupplierModel(int user_id) {
        this.id = 0;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void createSupplier(){
        SupplierDAO supplierDAO = new SupplierDAO();
        this.id = supplierDAO.createSupplier(this.user_id);
    }

    public void getSupplier(){
        SupplierDAO supplierDAO = new SupplierDAO();
        this.id = supplierDAO.getSupplier(this.user_id);
    }
}

package org.jom.Model;

import org.jom.Dao.Supplier.AccountDAO;

public class AccountModel {
    private int id;
    private int supplier_id;
    private String account_number;
    private String bank;
    private  String name;

    public AccountModel() {
    }
    public AccountModel(int id, int supplier_id, String account_number, String bank, String name) {
        this.id = id;
        this.supplier_id = supplier_id;
        this.account_number = account_number;
        this.bank = bank;
        this.name = name;
    }

    public AccountModel(int supplier_id, String account_number, String bank, String name) {
        this.id = 0;
        this.supplier_id = supplier_id;
        this.account_number = account_number;
        this.bank = bank;
        this.name = name;
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

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAccount(){
        AccountDAO accountDAO = new AccountDAO();
        this.id = accountDAO.addAccount(this);
    }
}

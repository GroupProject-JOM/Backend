package org.jom.Model;

import org.jom.Dao.Supplier.AccountDAO;
import org.jom.Dao.Supplier.EstateDAO;

public class AccountModel {
    private int id;
    private int supplier_id;
    private String account_number;
    private String bank;
    private String name;
    private String nickName;

    public AccountModel() {
    }

    public AccountModel(int id, String account_number, String bank, String name, String nickName) {
        this.id = id;
        this.account_number = account_number;
        this.bank = bank;
        this.name = name;
        this.nickName = nickName;
    }

    public AccountModel(String account_number, String bank, String name, String nickName) {
        this.account_number = account_number;
        this.bank = bank;
        this.name = name;
        this.nickName = nickName;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void addAccount() {
        AccountDAO accountDAO = new AccountDAO();
        this.id = accountDAO.addAccount(this);
    }

    public boolean updateAccount() {
        AccountDAO accountDAO = new AccountDAO();
        return accountDAO.updateAccount(this);
    }
}

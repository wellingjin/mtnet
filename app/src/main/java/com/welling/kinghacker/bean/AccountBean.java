package com.welling.kinghacker.bean;


import android.content.ContentValues;
import android.content.Context;

import com.welling.kinghacker.database.DatabaseManager;
import com.welling.kinghacker.database.TableItem;

import java.util.ArrayList;

/**
 * Created by KingHacker on 4/30/2016.
 * 账户bean
 */
public class AccountBean extends MTBean{

    private String account;
    private String password;
    public AccountBean(Context context){
        super(context);
    }
    public AccountBean(Context context,String account,String password){
        super(context);
        this.account = account;
        this.password = password;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    static public String getAccountName(){
        return "account";
    }
    static public String getPasswordName(){
        return "password";
    }

    public void update(){
        if (account == null || password == null) return;
        DatabaseManager manager = new DatabaseManager(context);
        manager.deleteByFieldEqual(getClass().getName(), getAccountName(), account);
        ContentValues cv = new ContentValues();
        cv.put(getAccountName(),account);
        cv.put(getPasswordName(),password);
        manager.insert(getClass().getName(),cv);
    }

    @Override
    protected void createTable() {
        TableItem item1 = new TableItem(getAccountName(),TableItem.M_VARCHAR,30);
        TableItem item2 = new TableItem(getPasswordName(),TableItem.M_VARCHAR,30);
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        manager.createTable(getClass().getName(), items);
    }
}

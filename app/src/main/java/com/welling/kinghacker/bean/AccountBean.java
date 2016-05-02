package com.welling.kinghacker.bean;


import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.welling.kinghacker.database.DatabaseManager;
import com.welling.kinghacker.database.TableItem;

import java.util.ArrayList;

/**
 * Created by KingHacker on 4/30/2016.
 * 账户bean
 */
public class AccountBean extends MTBean{
    static public String TABLENAME = "AccountBean";
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
    @Override
    public void insert(){
        if (account == null || password == null) return;
        manager.deleteByFieldEqual(TABLENAME, getAccountName(), account);
        ContentValues cv = new ContentValues();
        cv.put(getAccountName(),account);
        cv.put(getPasswordName(),password);
        manager.insert(TABLENAME,cv);
    }

    @Override
    protected void createTable() {
        TableItem item1 = new TableItem(getAccountName(),TableItem.M_VARCHAR,30);
        TableItem item2 = new TableItem(getPasswordName(),TableItem.M_VARCHAR,30);
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Log.i("data",item1.toString());
        manager.createTable(TABLENAME, items);
    }
}

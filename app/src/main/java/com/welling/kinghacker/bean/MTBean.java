package com.welling.kinghacker.bean;

import android.content.Context;

import com.welling.kinghacker.database.DatabaseManager;

/**
 * Created by KingHacker on 4/30/2016.
 * bean类
 */
public abstract class MTBean {
    protected Context context;
    protected DatabaseManager manager;
    protected MTBean(Context context){
        manager = new DatabaseManager(context);
        createTable();
    }
    abstract public void insert();
    abstract protected void createTable();
}

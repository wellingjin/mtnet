package com.welling.kinghacker.oxygenbean;

import android.content.Context;

import com.welling.kinghacker.database.DatabaseManager;

/**
 * Created by zsw on 2016/5/7.
 */
public abstract class OxygenMTBean {
    protected Context context;
    protected DatabaseManager manager;
    protected OxygenMTBean(Context context){
        manager = new DatabaseManager(context);
        createTable();
    }
    abstract public void insert();
    abstract protected void createTable();
    abstract public int getRecentlyOneData();
    abstract public int[] getRecentlySevenData();
}

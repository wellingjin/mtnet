package com.welling.kinghacker.tools;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by KingHacker on 4/1/2016.
 **/
public class MTDataBaseManager {
    private static MTDataBaseManager dataBaseManager = null;
    private SQLiteDatabase db;

    private MTDataBaseManager(){
        init();
    }
    static public MTDataBaseManager getDataBaseManager(){
        if (dataBaseManager == null){
            dataBaseManager = new MTDataBaseManager();
        }
        return dataBaseManager;
    }

    private void init(){
//        db =
    }
}

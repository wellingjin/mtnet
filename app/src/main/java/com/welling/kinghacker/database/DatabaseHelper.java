package com.welling.kinghacker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by KingHacker on 4/29/2016.
 * 数据库助手类
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "netmt.db"; //数据库名称
    private static final int version = 1; //数据库版本


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
        // TODO Auto-generated constructor stub

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
/*        String sql = "create table user(username varchar(20) not null , password varchar(60) not null );";
        db.execSQL(sql);*/
        Log.i("database","create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}

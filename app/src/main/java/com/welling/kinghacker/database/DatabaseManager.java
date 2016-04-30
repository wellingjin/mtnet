package com.welling.kinghacker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KingHacker on 4/29/2016.
 * 数据库管理类
 */
public class DatabaseManager {
    private DatabaseHelper helper;
    final String Tag = "database";
    public DatabaseManager(Context context){
//        db = context.openOrCreateDatabase(DatabaseHelper.DB_NAME, Context.MODE_PRIVATE, null);
        helper = new DatabaseHelper(context);
    }
    public void createTable(String tableName,ArrayList<TableItem> fields){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlCmd = "create table if not exists " + tableName + " (";
        boolean isFirstField = true;
        for (TableItem item:fields){
            if (isFirstField){
                isFirstField = false;
            }else {
                sqlCmd += ",";
            }
            sqlCmd += item.fieldName;
            sqlCmd += (" " + item.fieldType);
            if (item.length > 0){
                sqlCmd += ("(" + item.length + ")");
            }
            if (item.attribute != null){
                sqlCmd += (" " + item.attribute);
            }
        }
        sqlCmd += ");";
        Log.i(Tag, sqlCmd);
        try {
            db.execSQL(sqlCmd);
        }catch (SQLException e){
            Log.i(Tag,e.toString());
        }

    }
    public boolean updateByFieldEqual(String table,String field,String value,ContentValues cv){
        SQLiteDatabase db = helper.getWritableDatabase();
        if (cv != null){
            String whereClause = field+"=?";//删除的条件
            String[] whereArgs = {value};//删除的条件参数
            return db.update(table, cv, whereClause, whereArgs) > 0;
        }
        return false;
    }
    public boolean insert(String table,ContentValues cv) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return cv != null && db.insert(table, null, cv) > 0;
    }
    public boolean deleteByFieldEqual(String table,String field,String value){
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = field+"=?";//删除的条件
        String[] whereArgs = {value};//删除的条件参数
        return db.delete(table,whereClause,whereArgs) > 0;//执行删除

    }
    //查询单行数据
    public JSONObject getOneRawByFieldEqual(String table,String field,String value){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ table +" where "+ field+" =?", new String[]{value});
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("size",cursor.getColumnCount());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (cursor.moveToNext()){
            for(int i = 0;i < cursor.getColumnCount();i++){
                try {
                    jsonObject.put(cursor.getColumnName(i),cursor.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        return jsonObject;
    }
    //查询多行数据，如果start 或 end 为null，查询所有field数据
    //如果field 为null，查询所有数据
    public JSONObject getMultiRaw(String table,String field,String start,String end){
        SQLiteDatabase db = helper.getReadableDatabase();
        JSONObject jsonObject = new JSONObject();
        int index = 0;
        Cursor cursor;
        if (field == null){
            cursor = db.rawQuery("select * from "+ table , null);
        }else if (start == null && end == null){
            cursor = db.rawQuery("select "+ field +" from "+ table , null);
        }else if (end == null){
            cursor = db.rawQuery("select * from "+ table +" where " + field+" >=? ", new String[]{start});
        }else if (start == null){
            cursor = db.rawQuery("select * from "+ table +" where " + field+" <=? ", new String[]{end});
        }else {
            cursor = db.rawQuery("select * from "+ table +" where " + field+" >=? and "+field+" <=?", new String[]{start,end});
        }

        try {
            jsonObject.put("size",cursor.getColumnCount());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        while (cursor.moveToNext()){
            JSONObject item = new JSONObject();
            for (int i=0;i<cursor.getColumnCount();i++){
                try {
                    item.put(cursor.getColumnName(i),cursor.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                jsonObject.put(index+"",item);
                index++;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        try {
            jsonObject.put("row",index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cursor.close();
        return jsonObject;
    }

    public boolean execSQL(String sql){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.execSQL(sql);
            return true;
        }catch (SQLException e){
            return false;
        }

    }
    public void close(){
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.close();
        }
    }


}

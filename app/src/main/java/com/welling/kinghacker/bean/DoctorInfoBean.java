package com.welling.kinghacker.bean;


import android.content.ContentValues;
import android.content.Context;

import com.welling.kinghacker.database.TableItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KingHacker on 5/2/2016.
 * doctor information
 */
public class DoctorInfoBean extends MTBean {
    static public String
            TABLENAME = "DoctorInfo",
            USERNAME = "username",
            HOSPITAL = "hospital",
            DOCTORID = "ID",
            SEX = "sex",
            AGE = "age",
            ADDRESS = "address",
            NATION = "nation",
            INFO = "info",
            PHONE = "phone",
            EMAIL = "email",
            PERFESSION = "persion";

    public String name;
    public String hospital;
    public int doctorID;
    public String perfession;
    public int sex = 1;
    public int age = 30;
    public String address;
    public String nation;
    public String info ;//简介
    public String telPhone;
    public String email;
    public DoctorInfoBean(Context context){
        super(context);
    }
    public DoctorInfoBean(Context context,int doctorID){
        this(context);
        this.doctorID = doctorID;
        JSONObject JsonBean = manager.getOneRawByFieldEqual(TABLENAME, DOCTORID, doctorID+"");
        try {
            int count = JsonBean.getInt("count");
            if (count > 0) {
                name = JsonBean.getString(USERNAME);
                hospital = JsonBean.getString(HOSPITAL);
                perfession = JsonBean.getString(PERFESSION);
                address = JsonBean.getString(ADDRESS);
                nation = JsonBean.getString(NATION);
                info = JsonBean.getString(INFO);
                telPhone = JsonBean.getString(PHONE);
                email = JsonBean.getString(EMAIL);
                age = Integer.valueOf(JsonBean.getString(AGE));
                sex = Integer.valueOf(JsonBean.getString(SEX));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void update(){
        ContentValues cv = new ContentValues();
        cv.put(INFO,info);
        cv.put(USERNAME,name);
        cv.put(PHONE,telPhone);
        cv.put(SEX,sex);
        cv.put(AGE,age);
        cv.put(ADDRESS,address);
        cv.put(HOSPITAL,hospital);
        cv.put(PERFESSION,perfession);
        cv.put(NATION, nation);
        cv.put(EMAIL,email);
        manager.updateByFieldEqual(TABLENAME,DOCTORID,doctorID+"",cv);
    }

    public boolean isExict(){
        return true;
    }

    @Override
    public void insert() {
        if (doctorID <= 0) return;
        manager.deleteByFieldEqual(TABLENAME, DOCTORID, doctorID + "");
        ContentValues cv = new ContentValues();
        cv.put(DOCTORID,doctorID);
        cv.put(INFO,info);
        cv.put(USERNAME,name);
        cv.put(PHONE,telPhone);
        cv.put(SEX,sex);
        cv.put(AGE,age);
        cv.put(ADDRESS,address);
        cv.put(HOSPITAL,hospital);
        cv.put(PERFESSION,perfession);
        cv.put(NATION,nation);
        cv.put(EMAIL,email);
        manager.insert(TABLENAME,cv);
    }

    @Override
    protected void createTable() {
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(new TableItem(INFO));
        items.add(new TableItem(USERNAME,TableItem.M_VARCHAR,30));
        items.add(new TableItem(PHONE,TableItem.M_CHAR,11));
        items.add(new TableItem(SEX,TableItem.M_INTEGER,1));
        items.add(new TableItem(AGE,TableItem.M_INTEGER));
        items.add(new TableItem(HOSPITAL, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(ADDRESS));
        items.add(new TableItem(NATION, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(EMAIL, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(DOCTORID,TableItem.M_INTEGER));
        items.add(new TableItem(PERFESSION));
        manager.createTable(TABLENAME, items);
    }
}

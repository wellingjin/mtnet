package com.welling.kinghacker.oxygenbean;

import android.content.ContentValues;
import android.content.Context;
import com.welling.kinghacker.database.TableItem;

import java.util.ArrayList;

/**
 * Created by li on 2016/5/7.
 */
public class OxygenDoctorInfoBean extends OxygenMTBean {
    static public String
            TABLENAME = "OxygenDoctorInfo",
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
    public String sex;
    public int age;
    public String address;
    public String nation;
    public String info ;
    public String telPhone;
    public String email;
    public OxygenDoctorInfoBean(Context context){
        super(context);
    }
    @Override
    public void insert() {
        if (doctorID <= 0) return;
        manager.deleteByFieldEqual(TABLENAME, DOCTORID, doctorID + "");
        ContentValues cv = new ContentValues();
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
        items.add(new TableItem(USERNAME, TableItem.M_VARCHAR, 30));
        items.add(new TableItem(PHONE, TableItem.M_CHAR, 11));
        items.add(new TableItem(SEX, TableItem.M_INTEGER, 1));
        items.add(new TableItem(AGE, TableItem.M_INTEGER));
        items.add(new TableItem(HOSPITAL, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(ADDRESS));
        items.add(new TableItem(NATION, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(EMAIL, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(DOCTORID, TableItem.M_INTEGER));
        items.add(new TableItem(PERFESSION));
        manager.createTable(TABLENAME, items);
    }
    @Override
    public int getRecentlyOneData(){
        return 0;
    }
    @Override
    public int[] getRecentlySevenData(){
        int[] data={};
        return data;
    }
}

package com.welling.kinghacker.bean;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.welling.kinghacker.database.TableItem;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KingHacker on 4/30/2016.
 * 用户信息
 */
public class PersonalInfo extends MTBean{
    private String account;
    private String userName;
    private String phone;
    private String IDNum;
    private Integer sex = 1;//0,1
    private String borthDay;
    private int age = 30;
    static String ACCOUNT = "account",
                    USERNAME = "username",
                    PHONE = "phone",
                    IDNUM = "IDnum",
                    SEX = "sex",
                    BORTHDAY = "borthday",
                    TABLENAME = "personalinfo",
                    AGE = "age";

    public PersonalInfo(Context context) {
        super(context);
    }

    public PersonalInfo(Context context,String  account){
        this(context);
        this.account = account;
        JSONObject JsonBean = manager.getOneRawByFieldEqual(TABLENAME, ACCOUNT, account);
        try {
            int count = JsonBean.getInt("count");
            if (count > 0) {
                userName = JsonBean.getString(USERNAME);
                phone = JsonBean.getString(PHONE);
                IDNum = JsonBean.getString(IDNUM);
                borthDay = JsonBean.getString(BORTHDAY);
                sex = Integer.valueOf(JsonBean.getString(SEX));
                age = Integer.valueOf(JsonBean.getString(AGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setBorthDay(String borthDay) {
        this.borthDay = borthDay;
    }

    public String getBorthDay() {
        return borthDay;
    }

    public void setIDNum(String IDNum) {
        this.IDNum = IDNum;
    }

    public String getIDNum() {
        return IDNum;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getSex() {
        return sex;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    @Override
    public void insert(){
        if (account == null) return;
        manager.deleteByFieldEqual(TABLENAME, ACCOUNT, account);
        ContentValues cv = new ContentValues();
        cv.put(ACCOUNT,account);
        cv.put(USERNAME,userName);
        cv.put(PHONE,phone);
        cv.put(SEX,sex);
        cv.put(IDNUM,IDNum);
        cv.put(BORTHDAY,borthDay);
        manager.insert(TABLENAME,cv);
    }
    public void updateInfo(){
        account = SystemTool.getSystem(context).getStringValue(PublicRes.ACCOUNT);
        ContentValues cv = new ContentValues();
        cv.put(USERNAME,userName);
        cv.put(PHONE,phone);
        cv.put(SEX,sex);
        cv.put(IDNUM,IDNum);
        cv.put(BORTHDAY,borthDay);
        manager.updateByFieldEqual(TABLENAME,ACCOUNT,account,cv);
    }

    @Override
    protected void createTable() {

        ArrayList<TableItem> items = new ArrayList<>();
        items.add(new TableItem(ACCOUNT,TableItem.M_VARCHAR,30));
        items.add(new TableItem(USERNAME,TableItem.M_VARCHAR,30));
        items.add(new TableItem(PHONE,TableItem.M_CHAR,11));
        items.add(new TableItem(SEX,TableItem.M_INTEGER,1));
        items.add(new TableItem(IDNUM,TableItem.M_VARCHAR,20));
        items.add(new TableItem(BORTHDAY, TableItem.M_VARCHAR, 11));
        items.add(new TableItem(AGE, TableItem.M_INTEGER,3));
        manager.createTable(TABLENAME, items);

    }
}

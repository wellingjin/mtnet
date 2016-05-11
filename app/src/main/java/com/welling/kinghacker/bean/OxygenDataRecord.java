package com.welling.kinghacker.bean;

import android.content.ContentValues;
import android.content.Context;

import com.welling.kinghacker.bean.MTBean;
import com.welling.kinghacker.database.TableItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by li on 2016/5/7.
 *
 */
public class OxygenDataRecord extends MTBean {
    static public String
            TABLENAME = "OxygenDataRecord",
            UPDATETIME ="UpdateTime",
            OXYGENVALUE = "OxygenValue",
            BMPVALUE = "bmpValue";

    public String updatetime;
    public int oxygenvalue;
    public int bmpvalue;
    public OxygenDataRecord(Context context){
        super(context);
    }
    public OxygenDataRecord(Context context,int oxygenvalue,int bmpvalue){
        super(context);
        this.oxygenvalue = oxygenvalue;
        this.bmpvalue = bmpvalue;
        SimpleDateFormat formatter = new  SimpleDateFormat  ("yyyy年MM月dd日HH:mm:ss");
        Date curDate =new  Date(System.currentTimeMillis());
        this.updatetime = formatter.format(curDate);
        //System.out.print("当前时间:" + this.updatetime + " 血氧:" + this.oxygenvalue);
    }
    @Override
    public void insert() {
       // manager.deleteByFieldEqual(TABLENAME, DOCTORID, doctorID + "");
        ContentValues cv = new ContentValues();
        cv.put(UPDATETIME,updatetime);
        cv.put(OXYGENVALUE,oxygenvalue);
        cv.put(BMPVALUE, bmpvalue);
        manager.insert(TABLENAME, cv);
       // Log.i("123", "数据插入成功");
    }

    @Override
    public void createTable() {
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(new TableItem(UPDATETIME, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(OXYGENVALUE, TableItem.M_INTEGER));
        items.add(new TableItem(BMPVALUE, TableItem.M_INTEGER));
        manager.createTable(TABLENAME, items);
       // Log.i("123", "表创建成功");
    }
    //得到最近一次测量的血氧值

    public int getRecentlyOneData(){
        //首先得到所有数据
        int data= 0;
        JSONObject jsonObject =  manager.getMultiRaw(TABLENAME, null, null, null);
        try{
            int row = (int)jsonObject.get("count");
            JSONObject item = jsonObject.getJSONObject((row -1)+"");
            data = item.getInt(OXYGENVALUE);
        }catch(JSONException j){
            j.printStackTrace();
        }
        return data;
    }

    public int[] getRecentlySevenData(){
        int data[]= new int[7];
        JSONObject jsonObject =  manager.getMultiRaw(TABLENAME, null, null, null);
        try{
            int row = (int)jsonObject.get("count");
            for(int index = 1;index<=7;index++ ) {
                JSONObject item = jsonObject.getJSONObject((row - index) + "");
                data[index-1] = item.getInt(OXYGENVALUE);
            }
        }catch(JSONException j){
            j.printStackTrace();
        }
        return data;
    }

}

package com.welling.kinghacker.bean;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.welling.kinghacker.bean.MTBean;
import com.welling.kinghacker.database.TableItem;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by li on 2016/5/7.
 *
 */
public class OxygenDataRecord extends MTBean {
    static public String
            UPDATETIME ="time",
            OXYGENVALUE = "OxygenValue",
            ISUPDATE = "isupdate",
            BMPVALUE = "bmpValue";

    public String tableName;
    public String updatetime;
    public int oxygenvalue;
    public int bmpvalue ;
    public int isUpdate = NO;
    public int numberOfDate;
    public String time[] = null;
    public OxygenDataRecord(Context context){
        super(context);
        init();
    }

    @Override
    public void init() {
        this.tableName = "BloodOxygenData"+SystemTool.getSystem(context).getStringValue(PublicRes.ACCOUNT);
    }

    public OxygenDataRecord(Context context,int number){
        super(context);
        init();
        this.numberOfDate = number;
    }
    public OxygenDataRecord(Context context,int oxygenvalue,int bmpvalue,String time){
        super(context);
        init();
        this.oxygenvalue = oxygenvalue;
        this.bmpvalue = bmpvalue;
        this.updatetime = time;
        System.out.print("当前时间:" + this.updatetime + " 血氧:" + this.oxygenvalue);
    }
    public void update(){
        ContentValues cv = new ContentValues();
        isUpdate = YES;
        cv.put(ISUPDATE,isUpdate);
        manager.updateByFieldEqual(tableName, UPDATETIME, updatetime, cv);
    }
    @Override
    public void insert() {
        manager.deleteByFieldEqual(tableName, UPDATETIME, updatetime);
        ContentValues cv = new ContentValues();
        cv.put(UPDATETIME,updatetime);
        cv.put(OXYGENVALUE,oxygenvalue);
        cv.put(BMPVALUE, bmpvalue);
        cv.put(ISUPDATE,isUpdate);
        manager.insert(tableName, cv);
       // Log.i("123", "数据插入成功");
    }

    @Override
    public void createTable() {
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(new TableItem(UPDATETIME, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(OXYGENVALUE, TableItem.M_INTEGER));
        items.add(new TableItem(BMPVALUE, TableItem.M_INTEGER));
        items.add(new TableItem(ISUPDATE, TableItem.M_INTEGER));
        manager.createTable(tableName, items);
       // Log.i("123", "表创建成功");
    }
    //得到最近一次测量的血氧值

    public String getRecentlyOneData(){
        //首先得到所有数据
        int data= 0;
        String value =null;
        String date =null;
        String time =null;
        JSONObject jsonObject =  manager.getMultiRaw(tableName, null, null, null);
        try{
            int row = (int)jsonObject.get("count");
            if(row>0) {
                JSONObject item = jsonObject.getJSONObject((row - 1) + "");
                data = item.getInt(OXYGENVALUE);
                SimpleDateFormat formatter = new  SimpleDateFormat  ("yyyy年MM月dd日HH:mm:ss");
                try {
                    long st = formatter.parse(item.getString(UPDATETIME)).getTime();
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = formatter.format(st).split(" ")[0];
                    time = formatter.format(st).split(" ")[1];
                }catch (ParseException e){
                    e.printStackTrace();
                }
                value =data+","+date+","+time;
            }
        }catch(JSONException j){
            j.printStackTrace();
        }
        return value;
    }
    public int[] getRecentlyMoreChooseData(String endTime){
        int data[]=null;
        boolean start = false;
        JSONObject jsonObject =  manager.getMultiRaw(tableName, null, null, null);
        try{
            int row = (int)jsonObject.get("count");
            data= new int[this.numberOfDate];
            time = new String[this.numberOfDate];
            for(int index = 1,k=0;index<=row;index++ ) {
                JSONObject item = jsonObject.getJSONObject((row - index) + "");
                Log.i("日期",item.getString(UPDATETIME).split("日")[0]+"        "+ endTime);
                if(!start)
                    if(item.getString(UPDATETIME).split("日")[0].equals(endTime)){
                        start = true;
                    }
                if(start) {
                    if(k>=this.numberOfDate) break;
                    data[k] = item.getInt(OXYGENVALUE);
                    time[k] = item.getString(UPDATETIME);
                    k++;
                }
            }
        }catch(JSONException j){
            j.printStackTrace();
        }
        return data;
    }
    public int[] getRecentlyMoreData(){
        int data[]=null;

        JSONObject jsonObject =  manager.getMultiRaw(tableName, null, null, null);
        try{
            int row = (int)jsonObject.get("count");
            data= new int[this.numberOfDate];
            time = new String[this.numberOfDate];
            if(row>=this.numberOfDate){
                for(int index = 1;index<=this.numberOfDate;index++ ) {
                    JSONObject item = jsonObject.getJSONObject((row - index) + "");
                    data[index-1] = item.getInt(OXYGENVALUE);
                    time[index-1] = item.getString(UPDATETIME);
                }
            }else{
                for(int index = 1;index<=this.numberOfDate;index++ ) {
                    if(index<=row) {
                        JSONObject item = jsonObject.getJSONObject((row - index) + "");
                        data[index - 1] = item.getInt(OXYGENVALUE);
                        time[index-1] = item.getString(UPDATETIME);
                    }else{
                        data[index - 1] = 0;
                        time[index-1] = null;
                    }
                }
            }
        }catch(JSONException j){
            j.printStackTrace();
        }
        return data;
    }
    public String[] getRecentlyMoreTime(){
        if(time!=null){
            return  time;
        }
        return  null;
    }
}

package com.welling.kinghacker.bean;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.welling.kinghacker.database.TableItem;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by 13wlli on 2016/5/13.
 */
public class SugerBean extends MTBean {
    static public String
            UPDATETIME = "time",
            SUGERVALUE = "SugerValue",
            ISUPDATE = "isupdate";

    public String tableName;
    public String updatetime;
    public float sugervalue;
    public int isUpdate = NO;
    public int numberOfDate;
    public String time[] = null;

    public SugerBean(Context context) {
        super(context);
        init();
    }

    public void init() {
        this.tableName = "BloodSugerData"+ SystemTool.getSystem(context).getStringValue(PublicRes.ACCOUNT);
    }

    public SugerBean(Context context, int number){
        super(context);
        init();
        this.numberOfDate = number;
    }

    public SugerBean(Context context, float sugervalue, String time){
        super(context);
        init();
        this.sugervalue = sugervalue;
        this.updatetime = time;
        System.out.print("当前时间:" + this.updatetime + " 血糖:" + this.sugervalue);
    }

    public void update() {
        ContentValues cv = new ContentValues();
        isUpdate = YES;
        cv.put(ISUPDATE, isUpdate);
        manager.updateByFieldEqual(tableName, UPDATETIME, updatetime, cv);
    }

    @Override
    public void insert() {
        manager.deleteByFieldEqual(tableName, UPDATETIME, updatetime);
        ContentValues cv = new ContentValues();
        cv.put(UPDATETIME, updatetime);
        cv.put(SUGERVALUE, sugervalue);
        cv.put(ISUPDATE, isUpdate);
        manager.insert(tableName, cv);
        // Log.i("123", "数据插入成功");
    }

    @Override
    public void createTable() {
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(new TableItem(UPDATETIME, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(SUGERVALUE, TableItem.M_BLOB));
        items.add(new TableItem(ISUPDATE, TableItem.M_VARCHAR));
        manager.createTable(tableName, items);
        Log.i("123", "表创建成功");
    }

    //得到最近一次测量的血糖值
    public String getRecentlyOneData() {
        //首先得到所有数据
        float data = 0f;
        String value =null;
        String date =null;
        String time =null;
        JSONObject jsonObject = manager.getMultiRaw(tableName, null, null, null);
        try {
            int row = (int) jsonObject.get("count");
            if(row>0) {
                JSONObject item = jsonObject.getJSONObject((row - 1) + "");
                double bef = item.getDouble(SUGERVALUE);
                data = (float) bef;
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
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return value;
    }

    public float[] getRecentlyMoreChooseData(String endTime){
        float data[]=null;
        boolean start = false;
        JSONObject jsonObject =  manager.getMultiRaw(tableName, null, null, null);
        try{
            int row = (int)jsonObject.get("count");
            data= new float[this.numberOfDate];
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
                    data[k] =(float) item.getDouble(SUGERVALUE);
                    time[k] = item.getString(UPDATETIME);
                    k++;
                }
            }
        }catch(JSONException j){
            j.printStackTrace();
        }
        return data;
    }
    public float[] getRecentlyMoreData(){
        float data[]=null;

        JSONObject jsonObject =  manager.getMultiRaw(tableName, null, null, null);
        try{
            int row = (int)jsonObject.get("count");
            data= new float[this.numberOfDate];
            time = new String[this.numberOfDate];
            if(row>=this.numberOfDate){
                for(int index = 1;index<=this.numberOfDate;index++ ) {
                    JSONObject item = jsonObject.getJSONObject((row - index) + "");
                    data[index-1] =(float) item.getDouble(SUGERVALUE);
                    time[index-1] = item.getString(UPDATETIME);
                }
            }else{
                for(int index = 1;index<=this.numberOfDate;index++ ) {
                    if(index<=row) {
                        JSONObject item = jsonObject.getJSONObject((row - index) + "");
                        data[index-1] =(float) item.getDouble(SUGERVALUE);
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

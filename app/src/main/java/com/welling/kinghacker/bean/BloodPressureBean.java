package com.welling.kinghacker.bean;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.welling.kinghacker.database.TableItem;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zsw on 2016/5/13.
 *
 */
public class BloodPressureBean extends MTBean {
    private int highblood,lowblood,heartrate,heartproblem,isupdate;
    public String UpdateTime;
    public static String[] blood_status=new String[]{"低血压","理想血压","正常血压",
            "正常偏高值血压","轻度高血压","中度高血压","重度高血压"};
    public static int statu=0;
    public static String
            TABLENAME = "BloodpressureDataRecord",
            UPDATETIME ="UpdateTime",
            HIGHBLOOD = "highblood",
            LOWBLOOD = "lowblood",
            HEARTRATE = "heartrate",
            HEARTPROBLEM = "heartproblem",
            ISUPDATE="isupdate";
    public BloodPressureBean(Context context){
        super(context);
    }

    @Override
    public void init() {

    }

    public void setData(int highblood,int lowblood,int heartrate,int heartproblem){
        this.highblood = highblood;
        this.lowblood = lowblood;
        this.heartrate=heartrate;
        this.heartproblem=heartproblem;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        this.UpdateTime = formatter.format(curDate);
        this.isupdate=0;
    }
    @Override
    public void insert() {
        ContentValues cv = new ContentValues();
        cv.put(UPDATETIME, UpdateTime);
        cv.put(HIGHBLOOD, highblood);
        cv.put(LOWBLOOD, lowblood);
        cv.put(HEARTRATE, heartrate);
        cv.put(HEARTPROBLEM, heartproblem);
        cv.put(ISUPDATE,isupdate);
        manager.insert(TABLENAME, cv);
    }

    @Override
    protected void createTable() {
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(new TableItem(UPDATETIME, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(HIGHBLOOD, TableItem.M_INTEGER));
        items.add(new TableItem(LOWBLOOD, TableItem.M_INTEGER));
        items.add(new TableItem(HEARTRATE, TableItem.M_INTEGER));
        items.add(new TableItem(HEARTPROBLEM, TableItem.M_INTEGER));
        items.add(new TableItem(ISUPDATE, TableItem.M_INTEGER));
        manager.createTable(TABLENAME, items);
    }

    public void setLatestRecordFromlocal(){
        String[] columns=new String[]{UPDATETIME, HIGHBLOOD, LOWBLOOD, HEARTRATE, HEARTPROBLEM,ISUPDATE};
        String orderby=UPDATETIME+" desc";
        String limit="1";
        JSONObject jsonObject=manager.query(TABLENAME, columns, null, null, null, null, orderby, limit);
        try{
            if((int)jsonObject.get("count")==0){
                this.highblood=100;
                this.lowblood=90;
                this.heartrate=80;
                this.heartproblem=1;
                this.UpdateTime="2016.01.01-00:00:00";
            }else{
                jsonObject=(JSONObject)jsonObject.get("0");
                this.highblood=Integer.parseInt((String)jsonObject.get("highblood"));
                this.lowblood=Integer.parseInt((String)jsonObject.get("lowblood"));
                this.heartrate=Integer.parseInt((String)jsonObject.get("heartrate"));
                this.heartproblem=Integer.parseInt((String)jsonObject.get("heartproblem"));
                this.UpdateTime=(String)jsonObject.get("UpdateTime");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(highblood<=99)statu=0;
        else if(highblood>=100&&highblood<=119)statu=1;
        else if(highblood>=120&&highblood<=129)statu=2;
        else if(highblood>=130&&highblood<=139)statu=3;
        else if(highblood>=140&&highblood<=159)statu=4;
        else if(highblood>=160&&highblood<=179)statu=5;
        else if(highblood>=180)statu=6;
    }
    public JSONObject setWeekRecordFromlocal(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        setLatestRecordFromlocal();
        String endTime=this.getUpdatetime(),startTime=null;
        try{
            long st=formatter.parse(endTime).getTime()-6*86400000;
            startTime=formatter.format(st).split("-")[0]+"-00:00:00";
        }catch(ParseException e){e.printStackTrace();}
        String[] columns=new String[]{UPDATETIME, HIGHBLOOD, LOWBLOOD, HEARTRATE, HEARTPROBLEM,ISUPDATE};
        String selection=UPDATETIME+" >= ?"+
                " AND "+UPDATETIME+" <= ?";
        String[] args=new String[]{startTime,endTime};
        JSONObject jsonObject=manager.query(TABLENAME, columns, selection, args, null, null, null, null);
        Log.i("database","执行完query后");
        return jsonObject;
    }
    public JSONObject getNotUptoServer(){
        String[] columns=new String[]{UPDATETIME, HIGHBLOOD, LOWBLOOD, HEARTRATE, HEARTPROBLEM,ISUPDATE};
        String selection=ISUPDATE+" == ?";
        String[] args=new String[]{"0"};
        JSONObject jsonObject=manager.query(TABLENAME, columns, selection, args, null, null, null, null);
        return jsonObject;
    }
    public int getHighblood() {
        return highblood;
    }

    public void update_data(String sql){
        manager.execSQL(sql);
    }
    public int getLowblood() {
        return lowblood;
    }

    public int getHeartrate() {
        return heartrate;
    }

    public int getHeartproblem() {
        return heartproblem;
    }

    public String getUpdatetime() {
        return UpdateTime;
    }
}

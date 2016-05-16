package com.welling.kinghacker.bean;

import android.content.ContentValues;
import android.content.Context;

import com.welling.kinghacker.database.TableItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 13wlli on 2016/5/13.
 */
public class SugerBean extends MTBean {
    static public String
            TABLENAME = "Suger",
            UPDATETIME = "UpdateTime",
            SUGERVALUE = "SugerValue",
            ISUPDATE = "isupdate";

    public String updatetime;
    public float sugervalue;
    public int isUpdate = NO;

    public SugerBean(Context context) {
        super(context);
    }

    public SugerBean(Context context, float sugervalue) {
        super(context);
        this.sugervalue = sugervalue;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        this.updatetime = formatter.format(curDate);
        //System.out.print("当前时间:" + this.updatetime + " 血糖:" + this.sugervalue);
    }

    public void update() {
        ContentValues cv = new ContentValues();
        isUpdate = YES;
        cv.put(ISUPDATE, isUpdate);
        manager.updateByFieldEqual(TABLENAME, UPDATETIME, updatetime, cv);
    }

    @Override
    public void insert() {
        manager.deleteByFieldEqual(TABLENAME, UPDATETIME, updatetime + "");
        ContentValues cv = new ContentValues();
        cv.put(UPDATETIME, updatetime);
        cv.put(SUGERVALUE, sugervalue);
        cv.put(ISUPDATE, isUpdate);
        manager.insert(TABLENAME, cv);
        // Log.i("123", "数据插入成功");
    }

    @Override
    public void createTable() {
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(new TableItem(UPDATETIME, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(SUGERVALUE, TableItem.M_BLOB));
        items.add(new TableItem(ISUPDATE, TableItem.M_VARCHAR, 3));
        manager.createTable(TABLENAME, items);
        // Log.i("123", "表创建成功");
    }

    //得到最近一次测量的血糖值
    public float getRecentlyOneData() {
        //首先得到所有数据
        float data = 0f;
        JSONObject jsonObject = manager.getMultiRaw(TABLENAME, null, null, null);

        try {
            int row = (int) jsonObject.get("count");
            JSONObject item = jsonObject.getJSONObject((row-1) + "");
//            Log.i("333",item.getDouble(SUGERVALUE)+"");
            double bef = item.getDouble(SUGERVALUE);
            data = (float) bef;
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return data;
    }
}

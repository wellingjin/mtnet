package com.welling.kinghacker.bean;

import android.content.ContentValues;
import android.content.Context;

import com.welling.kinghacker.database.TableItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KingHacker on 5/2/2016.
 * 药品信息
 */
public class MedicineBean extends MTBean {
    static public String
            TABLENAME = "medicinebean",
            MEDICINEID = "medicineID",
            MEDICINENAME = "medicineName",
            WAY = "way",
            NUMBER = "number",
            COUNT = "dayCount",
            TIME = "time",
            UNIT = "unit",
            CONSTITUENT = "constituent",
            ADAPTATUIO = "adaptation",
            ADVERSE_REACTION = "reaction",
            TABOO = "taboo",
            DIRECTION = "direction",
            CREATETIME = "createtime",
            ATTENTION = "attention";


    public int medicineID;
    public String medicineName ;//药名
    public String way;   //服用方式
    public float number = 1;//用量
    public String unit;//单位
    public String count;//次数
    public String time;//服用时间
    public String constituent; //成分
    public String adaptation_disease; //适应症
    public String adverse_reaction; //不良反应
    public String taboo; //用药禁忌
    public String attentions; //注意事项
    public String direction; //药品说明
    public String createTime ; //药方时间

    public MedicineBean(Context context){
        super(context);
    }
    public MedicineBean(Context context,int medicineID){
        this(context);
        this.medicineID = medicineID;
        JSONObject jsonObject = manager.getOneRawByFieldEqual(TABLENAME, MEDICINEID, medicineID + "");
        try {
            int c = jsonObject.getInt("count");
            if (c > 0) {
                medicineName = jsonObject.getString(MEDICINENAME);
                unit = jsonObject.getString(UNIT);
                count = jsonObject.getString(COUNT);
                way = jsonObject.getString(WAY);
                number = Float.valueOf(jsonObject.getString(NUMBER));
                time = jsonObject.getString(TIME);
                createTime = jsonObject.getString(CREATETIME);
                constituent = jsonObject.getString(CONSTITUENT);
                attentions = jsonObject.getString(ATTENTION);
                taboo = jsonObject.getString(TABOO);
                direction = jsonObject.getString(DIRECTION);
                adverse_reaction = jsonObject.getString(ADVERSE_REACTION);
                adaptation_disease = jsonObject.getString(ADAPTATUIO);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void update(){
        ContentValues cv = new ContentValues();
        cv.put(MEDICINENAME,medicineName);
        cv.put(UNIT,unit);
        cv.put(COUNT,count);
        cv.put(WAY,way);
        cv.put(NUMBER,number);
        cv.put(TIME,time);
        cv.put(CONSTITUENT,constituent);
        cv.put(CREATETIME,createTime);
        cv.put(ADAPTATUIO,adaptation_disease);
        cv.put(ADVERSE_REACTION,adverse_reaction);
        cv.put(TABOO,taboo);
        cv.put(ATTENTION,attentions);
        cv.put(DIRECTION,direction);
        manager.updateByFieldEqual(TABLENAME,MEDICINEID,medicineID+"",cv);
    }
    @Override
    public void insert() {
        if (medicineID <= 0) return;
        manager.deleteByFieldEqual(TABLENAME, MEDICINEID, medicineID + "");
        ContentValues cv = new ContentValues();
        cv.put(MEDICINEID,medicineID);
        cv.put(MEDICINENAME,medicineName);
        cv.put(UNIT,unit);
        cv.put(COUNT,count);
        cv.put(WAY,way);
        cv.put(NUMBER,number);
        cv.put(TIME,time);
        cv.put(CONSTITUENT,constituent);
        cv.put(CREATETIME,createTime);
        cv.put(ADAPTATUIO,adaptation_disease);
        cv.put(ADVERSE_REACTION,adverse_reaction);
        cv.put(TABOO,taboo);
        cv.put(ATTENTION,attentions);
        cv.put(DIRECTION,direction);
        manager.insert(TABLENAME, cv);
    }

    @Override
    protected void createTable() {
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(new TableItem(MEDICINEID,TableItem.M_INTEGER));
        items.add(new TableItem(MEDICINENAME,TableItem.M_VARCHAR,50));
        items.add(new TableItem(UNIT,TableItem.M_VARCHAR,5));
        items.add(new TableItem(COUNT,TableItem.M_VARCHAR,3));
        items.add(new TableItem(WAY,TableItem.M_VARCHAR,20));
        items.add(new TableItem(NUMBER, TableItem.M_VARCHAR, 3));
        items.add(new TableItem(TIME,TableItem.M_VARCHAR,5));
        items.add(new TableItem(CONSTITUENT));
        items.add(new TableItem(CREATETIME, TableItem.M_VARCHAR, 50));
        items.add(new TableItem(ADAPTATUIO));
        items.add(new TableItem(ADVERSE_REACTION));
        items.add(new TableItem(TABOO));
        items.add(new TableItem(ATTENTION));
        items.add(new TableItem(DIRECTION));

        manager.createTable(TABLENAME, items);

    }
}

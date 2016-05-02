package com.welling.kinghacker.bean;

import android.content.Context;

/**
 * Created by KingHacker on 5/2/2016.
 * 药品信息
 */
public class MedicineBean extends MTBean {

    public String medicineName ;//药名
    public String way;   //服用方式
    public float number;
    public String unit;
    public String count;
    public String time;
    public String constituent; //成分
    public String adaptation_disease; //适应症
    public String adverse_reaction; //不良反应
    public String taboo; //用药禁忌
    public String attentions; //注意事项
    public String direction; //药品说明

    public MedicineBean(Context context){
        super(context);
    }
    @Override
    public void insert() {

    }

    @Override
    protected void createTable() {

    }
}

package com.welling.kinghacker.oxygenbean;

import android.content.Context;

/**
 * Created by li on 2016/5/7.
 */
public class OxygenMedicineBean extends OxygenMTBean {
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

    public OxygenMedicineBean(Context context){
        super(context);
    }
    @Override
    public void insert() {

    }

    @Override
    protected void createTable() {

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

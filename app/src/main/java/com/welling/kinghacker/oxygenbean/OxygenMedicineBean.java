package com.welling.kinghacker.oxygenbean;

import android.content.Context;

/**
 * Created by li on 2016/5/7.
 */
public class OxygenMedicineBean extends OxygenMTBean {
    public String medicineName ;
    public String way;
    public float number;
    public String unit;
    public String count;
    public String time;
    public String constituent;
    public String adaptation_disease;
    public String adverse_reaction;
    public String taboo;
    public String attentions;
    public String direction;

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

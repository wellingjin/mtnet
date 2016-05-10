package com.welling.kinghacker.mtdata;

import com.welling.kinghacker.bean.MedicineBean;


/**
 * Created by KingHacker on 5/1/2016.
 *
 */
public class MedicineAdapterStruct {
    public String medicineName,way,eatTime,count;
    public boolean isSeccsion = false;
    public int medicineId;
    public MedicineBean bean;

    public MedicineAdapterStruct(String time){
        this.isSeccsion = true;
        this.eatTime = time;
    }
    public MedicineAdapterStruct(MedicineBean bean){
        this.bean = bean;
        this.isSeccsion = false;
        this.eatTime = bean.createTime;
        medicineName = bean.medicineName;
        medicineId = bean.medicineID;
        way = bean.way;
        count = String.format("1天%s次，1次%.1f%s",bean.count,bean.number,bean.unit);

    }


}

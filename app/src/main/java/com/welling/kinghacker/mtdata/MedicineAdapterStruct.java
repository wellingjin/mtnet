package com.welling.kinghacker.mtdata;

import com.welling.kinghacker.customView.MedicineAdapter;

/**
 * Created by KingHacker on 5/1/2016.
 *
 */
public class MedicineAdapterStruct {
    public String medicineName,way,eatTime,count;
    public boolean isSeccsion = false;
    public int medicineId;
    public MedicineAdapterStruct(boolean isSeccsion,String time){
        this.isSeccsion = isSeccsion;
        this.eatTime = time;
    }
    public MedicineAdapterStruct(boolean isSeccsion,String medicineName,String way,String eatTime,String count,int id){
        this.isSeccsion = isSeccsion;
        this.medicineName =medicineName;
        this.way = way;
        this.eatTime = eatTime;
        this.count = count;
        this.medicineId = id;
    }

}

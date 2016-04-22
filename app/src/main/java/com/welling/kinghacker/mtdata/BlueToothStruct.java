package com.welling.kinghacker.mtdata;

/**
 * Created by KingHacker on 3/25/2016.
 */
public class BlueToothStruct {
    public String blueToothName;
    public String address;
    static private int id = 0;
    public BlueToothStruct(String name,String address){
        blueToothName = name;
        this.address = address;
        id++;
    }

    public int getId() {
        return id;
    }
}

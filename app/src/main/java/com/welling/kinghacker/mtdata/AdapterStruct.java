package com.welling.kinghacker.mtdata;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.bean.DoctorInfoBean;

/**
 * Created by KingHacker on 3/22/2016.
 *
 */
public class AdapterStruct {
    //    数据结构

    public int profile,doctorID;
    public String style,name,description;
    public AdapterStruct(int id,int profile,String style,String name,String description){
        doctorID = id;
        this.profile = profile;
        this.style = style;
        this.name = name;
        this.description = description;
    }
    public AdapterStruct(DoctorInfoBean bean){
        doctorID = bean.doctorID;
        profile = R.mipmap.lady;
        style = bean.name;
        name = bean.hospital;
        description = bean.perfession;
    }

}

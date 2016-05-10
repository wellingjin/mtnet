package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.welling.kinghacker.customView.DoctorListView;
import com.welling.kinghacker.mtdata.AdapterStruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2016/5/7.
 */
public class OxygenMainDoctorActivity extends  MTActivity{
    @Override
    protected void onCreate(Bundle instance){
        super.onCreate(instance);
        setActionBarTitle("主治医生");

        DoctorListView doctorInfo = new DoctorListView(this);
        List<AdapterStruct> data = new ArrayList<>();
        for (int i = 0;i<10;i++) {
            data.add(new AdapterStruct(R.mipmap.gentleman, "血氧", "陈医生", "陈医生毕业于汕头大学医学院"));
        }
        doctorInfo.setData(data);
        setContentView(doctorInfo);
        doctorInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoActivity(OxygenDoctorDetailActivity.class);
            }
        });
    }
}

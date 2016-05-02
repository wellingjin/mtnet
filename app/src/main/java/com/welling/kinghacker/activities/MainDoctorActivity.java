package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.welling.kinghacker.customView.DoctorListView;
import com.welling.kinghacker.mtdata.AdapterStruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 4/30/2016.
 * 主治医生
 */
public class MainDoctorActivity extends MTActivity{
    @Override
    protected void onCreate(Bundle instance){
        super.onCreate(instance);
        setActionBarTitle("主治医生");

        DoctorListView doctorInfo = new DoctorListView(this);
        List<AdapterStruct> data = new ArrayList<>();
        for (int i = 0;i<10;i++) {
            data.add(new AdapterStruct(R.mipmap.lady, "血糖", "张医生", "张医生毕业于汕头大学医学院，在2002年曾经"));
        }
        doctorInfo.setData(data);
        setContentView(doctorInfo);
        doctorInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoActivity(DoctorDetailActivity.class);
            }
        });
    }
}

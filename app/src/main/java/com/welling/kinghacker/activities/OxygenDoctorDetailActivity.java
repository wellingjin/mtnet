package com.welling.kinghacker.activities;

import android.os.Bundle;

/**
 * Created by li on 2016/5/7.
 */
public class OxygenDoctorDetailActivity extends MTActivity{
    @Override
    protected void onCreate(Bundle instance) {
        super.onCreate(instance);
        setActionBarTitle("详细信息");
        setContentView(R.layout.layout_oxygen_doctor_detail);
    }
}

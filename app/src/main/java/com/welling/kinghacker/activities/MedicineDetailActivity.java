package com.welling.kinghacker.activities;

import android.os.Bundle;

/**
 * Created by KingHacker on 5/1/2016.
 * 药品详细
 */
public class MedicineDetailActivity extends MTActivity{
    @Override
    protected void onCreate(Bundle instance) {
        super.onCreate(instance);
        setActionBarTitle("详细信息");
        setContentView(R.layout.layout_medicine_detail);
    }
}

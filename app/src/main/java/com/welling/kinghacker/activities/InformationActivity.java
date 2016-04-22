package com.welling.kinghacker.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.welling.kinghacker.customView.ListAdapter;
import com.welling.kinghacker.mtdata.AdapterStruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/19/2016.
 **/
public class InformationActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView(){
        TextView textView = new TextView(this);
        textView.setText("sdfafd");
        textView.setTextColor(Color.RED);
        setLeftView(textView);

        ListView doctorInfo = new ListView(this);
        List<AdapterStruct> data = new ArrayList<>();
        for (int i = 0;i<10;i++) {
            data.add(new AdapterStruct(R.mipmap.lady, "血糖", "张医生", "张医生毕业于汕头大学医学院，在2002年曾经"));
        }
        ListAdapter docAdaptor = new ListAdapter(this,data);
        doctorInfo.setAdapter(docAdaptor);
        setRightView(doctorInfo);
    }

}

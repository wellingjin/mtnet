package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.welling.kinghacker.customView.ListAdapter;
import com.welling.kinghacker.mtdata.AdapterStruct;
import com.welling.kinghacker.tools.SystemTool;

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
        View personInfoView = SystemTool.getSystem(this).getView(R.layout.layout_personal_info);
        setLeftView(personInfoView);

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

package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/19/2016.
 **/
public class AboutActivity extends MTActivity {
    private List<String> buttonData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.about));
        setRightButtonEnable(false);
        setIsBackEnable(true);
        setContentView(R.layout.about_activity_layout);
        initDate();
        ListView buttonList = (ListView)findViewById(R.id.aboutList);
        buttonList.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_textview_layout,buttonData));
        TextView version = (TextView)findViewById(R.id.versionName);
        String versionName = getResources().getString(R.string.app_name);
        versionName += SystemTool.getSystem(this).getVersionName();
        version.setText(versionName);
    }
    private void initDate(){
        buttonData.add(getString(R.string.checkNewVersion));
        buttonData.add(getString(R.string.sugession));
    }
}

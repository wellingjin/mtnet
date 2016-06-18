package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/19/2016.
 **/
public class SettingActivity extends MTActivity {
    ListView settingList;
    List<String> listData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.setting));
        setRightButtonEnable(false);
        setIsBackEnable(true);
        settingList = new ListView(this);
        initData();
        settingList.setAdapter(new ArrayAdapter<>(this, R.layout.list_item_textview_layout, listData));
        setContentView(settingList);
        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(position);
            }
        });
    }
    private void initData(){
        listData = new ArrayList<>();
        listData.add(getString(R.string.alertSetting));
        listData.add(getString(R.string.accountChange));
        listData.add(getString(R.string.exitAccount));
    }
    private void onItemClicked(int position){
        switch (position){
            case 0:
                break;
            case 1:
                SystemTool.getSystem(this).saveBooleanKV(PublicRes.AUTOLOGIN, false);
                gotoActivity(LoginActivity.class);
                break;
            case 2:
                SystemTool.getSystem(this).saveBooleanKV(PublicRes.AUTOLOGIN, false);
                gotoActivity(LoginActivity.class);
                finish();
                break;
        }
    }
}

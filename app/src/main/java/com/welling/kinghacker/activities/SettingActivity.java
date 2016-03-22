package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    }
    private void initData(){
        listData = new ArrayList<>();
        listData.add(getString(R.string.alertSetting));
        listData.add(getString(R.string.accountChange));
        listData.add(getString(R.string.exitAccount));
    }
}

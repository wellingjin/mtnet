package com.welling.kinghacker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONObject;

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
        buttonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(position);
            }
        });
        TextView version = (TextView)findViewById(R.id.versionName);
        String versionName = getResources().getString(R.string.app_name);
        versionName += SystemTool.getSystem(this).getVersionName();
        version.setText(versionName);
    }
    private void initDate(){
        buttonData.add(getString(R.string.checkNewVersion));
        buttonData.add(getString(R.string.sugession));
    }
    private void onItemClicked(int which){
        switch (which){
            case 0:
                gotoActivity("http://www.baidu.com");
                break;
            case 1:
                gotoActivity("http://www.baidu.com");
                break;
        }
    }
    void checkUpdate(){
        MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {

            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });
        manager.post(manager.getRequestID(),"");
    }
   protected void gotoActivity(String url){
        Intent intent = new Intent(this, MTWebViewActivity.class);
        Bundle bundle = new Bundle();
        //传递name参数为tinyphp
        bundle.putString("webUrl",url);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.welling.kinghacker.customView.MedicineAdapter;
import com.welling.kinghacker.mtdata.MedicineAdapterStruct;
import com.welling.kinghacker.tools.MTHttpManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 4/30/2016.
 * 用药查询
 */
public class MedicionActicity extends  MTActivity{
    @Override
    protected void onCreate(Bundle instance){
        super.onCreate(instance);
        setActionBarTitle("用药查询");
        ListView medicineListView = new ListView(this);
        final List<MedicineAdapterStruct> listData = new ArrayList<>();
        MedicineAdapter adapter = new MedicineAdapter(this,listData);
        for (int i=0;i<40;i++) {
            if (i%8==0) {
                listData.add(new MedicineAdapterStruct(true,"1992-2-"+i));
            }else {
                listData.add(new MedicineAdapterStruct(false,"999","koufu","1990","1day 1 night",90));
            }
        }
        medicineListView.setAdapter(adapter);
        medicineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!listData.get(position).isSeccsion) {
                    gotoActivity(MedicineDetailActivity.class);
                }
            }
        });
        setContentView(medicineListView);
        getMedicineList();
    }
    private void getMedicineList(){
        MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {

            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });
        manager.post();
    }
}

package com.welling.kinghacker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.bean.DoctorInfoBean;
import com.welling.kinghacker.bean.MedicineBean;
import com.welling.kinghacker.customView.MedicineAdapter;
import com.welling.kinghacker.database.DatabaseManager;
import com.welling.kinghacker.mtdata.AdapterStruct;
import com.welling.kinghacker.mtdata.MedicineAdapterStruct;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 4/30/2016.
 * 用药查询
 */
public class MedicionActicity extends  MTActivity{

    List<MedicineAdapterStruct> listData = new ArrayList<>();
    String session = "";
    ListView medicineListView;
    MedicineAdapter adapter;
    @Override
    protected void onCreate(final Bundle instance){
        super.onCreate(instance);
        setActionBarTitle("用药查询");
        medicineListView = new ListView(this);
        adapter = new MedicineAdapter(this,listData);

        medicineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!listData.get(position).isSeccsion) {

                    Intent intent = new Intent(MedicionActicity.this, MedicineDetailActivity.class);
                    MedicineBean bean = listData.get(position).bean;

                    JSONObject medicine = new JSONObject();
                    try {
                        medicine.put("medicineID", bean.medicineID);
                        medicine.put("way", bean.way);
                        medicine.put("unit", bean.unit);
                        medicine.put("number", bean.number + "");
                        medicine.put("time", bean.time);
                        medicine.put("count", bean.count);
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                    intent.putExtra("medicine", medicine.toString());
                    startActivity(intent);
                }
            }
        });
        medicineListView.setAdapter(adapter);
        setContentView(medicineListView);
        getMedicineList();
    }
    private void getMedicineList(){
        getLocalMedicineList();
        MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                setMedicineList(JSONResponse);
            }

            @Override
            public void onFailure(int requestId, int errorCode) {
                makeToast("错误码：" + errorCode);
            }
        });
        RequestParams params = new RequestParams();
        params.put("username", SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
        manager.post(params, manager.getRequestID(), "getPrescription.do");
    }

    private void getLocalMedicineList() {
        DatabaseManager manager = new DatabaseManager(this);
        JSONObject jsonMedicineList = manager.getMultiRaw(MedicineBean.TABLENAME, null, null, null);

        listData.clear();
        try {
            int count = jsonMedicineList.getInt("count");
            for (int i=0;i<count;i++){
                JSONObject object = jsonMedicineList.getJSONObject(""+i);
                MedicineBean medicineBean = new MedicineBean(this);
                medicineBean.medicineID = Integer.valueOf(object.getString(MedicineBean.MEDICINEID));
                medicineBean.medicineName = object.getString(MedicineBean.MEDICINENAME);
                if (object.has(MedicineBean.NUMBER)) {
                    medicineBean.number = Float.valueOf(object.getString(MedicineBean.NUMBER));
                }
                if (object.has(MedicineBean.TIME)) {
                    medicineBean.time = object.getString(MedicineBean.TIME);
                }
                if (object.has(MedicineBean.UNIT)) {
                    medicineBean.unit = object.getString(MedicineBean.UNIT);
                }
                if (object.has(MedicineBean.WAY)) {
                    medicineBean.way = object.getString(MedicineBean.WAY);
                }
                if (object.has(MedicineBean.COUNT)) {
                    medicineBean.count = object.getString(MedicineBean.COUNT);
                }
                if (object.has(MedicineBean.CREATETIME)) {
                    medicineBean.createTime = object.getString(MedicineBean.CREATETIME);
                }
                if (medicineBean.createTime !=null && !session.equals(medicineBean.createTime)) {
                    session = medicineBean.createTime;
                    listData.add(new MedicineAdapterStruct(session));
                }
                listData.add(new MedicineAdapterStruct(medicineBean));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Log.i("medicine","exception");
            e.printStackTrace();
        }

    }

    private void setMedicineList(JSONObject jsonObject){
        int state = 0;
        String excption;
        try {
            listData.clear();
            state = jsonObject.getInt(PublicRes.STATE);
            excption = jsonObject.getString(PublicRes.EXCEPTION);
            JSONArray jsonArray = jsonObject.getJSONArray("medicineInfos");
            for (int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                MedicineBean medicineBean = new MedicineBean(this);
                medicineBean.medicineID = object.getInt("id");
                medicineBean.medicineName = object.getString("medicineName");
                medicineBean.number = (float)object.getDouble("number");
                medicineBean.time = object.getString("time");
                medicineBean.unit = object.getString("unit");
                medicineBean.way = object.getString("way");
                medicineBean.count = object.getString("count");
                medicineBean.createTime = object.getString("createTime");
                if (!session.equals(medicineBean.createTime)) {
                    session = medicineBean.createTime;
                    listData.add(new MedicineAdapterStruct(session));

                }

                listData.add(new MedicineAdapterStruct(medicineBean));
            }

        } catch (JSONException e) {
            excption = "格式解析错误";
            e.printStackTrace();
        }
        Log.i("Medicine",listData.size()+" size");
        if (state == 1){
            adapter.notifyDataSetChanged();
        }else {
            makeToast(excption);
        }

    }
}

package com.welling.kinghacker.activities;


import android.os.Bundle;

import com.welling.kinghacker.bean.DoctorInfoBean;
import com.welling.kinghacker.customView.DoctorListView;
import com.welling.kinghacker.mtdata.AdapterStruct;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by KingHacker on 4/30/2016.
 * 主治医生
 */
public class MainDoctorActivity extends MTActivity{

    DoctorListView doctorInfo;

    @Override
    protected void onCreate(Bundle instance){
        super.onCreate(instance);
        setActionBarTitle("主治医生");

        doctorInfo = new DoctorListView(this);

        setContentView(doctorInfo);
        getInfo();

    }
    private void getInfo(){
        final MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                setDocList(JSONResponse);

            }

            @Override
            public void onFailure(int requestId, int errorCode) {
                makeToast("error:" + errorCode);
            }
        });
        HashMap<String,String> params = new HashMap<>();
        params.put("username", SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
        manager.post(params, manager.getRequestID(), "getDoctorList.do");
    }
    private void setDocList(JSONObject jsonObject){
        int state = 0;
        String excption;

        try {
            state = jsonObject.getInt(PublicRes.STATE);
            excption = jsonObject.getString(PublicRes.EXCEPTION);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            doctorInfo.data.clear();
            for (int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                DoctorInfoBean doctorInfoBean = new DoctorInfoBean(this);
                doctorInfoBean.doctorID = object.getInt("doctorID");
                doctorInfoBean.name = object.getString("doctorName");
                doctorInfoBean.hospital = object.getString("hospital");
                doctorInfoBean.perfession = object.getString("perfession");
                doctorInfo.data.add(new AdapterStruct(doctorInfoBean));
            }


        } catch (JSONException e) {
            excption = "格式解析错误";
            e.printStackTrace();
        }
        if (state != 1){
            makeToast(excption);
        }else {
            doctorInfo.update();
        }

    }
}

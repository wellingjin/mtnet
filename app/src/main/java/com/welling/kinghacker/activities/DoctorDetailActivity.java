package com.welling.kinghacker.activities;

import android.os.Bundle;

import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONObject;

/**
 * Created by KingHacker on 5/1/2016
 * 医生详细信息.
 */
public class DoctorDetailActivity  extends MTActivity{
    @Override
    protected void onCreate(Bundle instance) {
        super.onCreate(instance);
        setActionBarTitle("详细信息");
        setContentView(R.layout.layout_doctor_detail);
        if (instance!=null){
            getDetail(instance.getInt("doctorID", 0));
        }
    }
    private void getDetail(int id){
        if (id <= 0) {
            makeToast("id错误");
            return;
        }
        MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {

            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });
        RequestParams params = new RequestParams();
        params.put("username", SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
        params.put("doctorID",id);
        manager.post(params,manager.getRequestID(),"getDoctorDetail.do");
    }
}

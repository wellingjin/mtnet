package com.welling.kinghacker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.bean.DoctorInfoBean;
import com.welling.kinghacker.mtdata.AdapterStruct;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 5/1/2016
 * 医生详细信息.
 */
public class DoctorDetailActivity  extends MTActivity{
    TextView doctorNmae,doctorPerfession,doctorHospital
            ,doctorAge,sex,nation,address,tel,email,info;
    int doctorID;
    final private String Tag = "doctorDetail";

    @Override
    protected void onCreate(Bundle instance) {
        super.onCreate(instance);
        setActionBarTitle("详细信息");
        setContentView(R.layout.layout_doctor_detail);
        setParentView(findViewById(R.id.doctorDetail));
        initView();
        Intent intent = getIntent();
        doctorID = intent.getIntExtra("doctorID", 0);
        Log.i(Tag, doctorID + " id");
        getDetail(doctorID);


    }
    @Override
    protected void setOverFlowView(){

        super.setOverFlowView();
        List<OverFlowItem> list = new ArrayList<>();
        list.add(new OverFlowItem(android.R.drawable.stat_sys_phone_call, "联系医生"));
        list.add(new OverFlowItem(android.R.drawable.stat_notify_sync_noanim, "刷新"));
        setOverFlowViewItems(list);
    }
    @Override
    protected void selectItem(String text){
        if (text.equals("刷新")){
            getDetail(doctorID);
        }else if (text.equals("联系医生")){
            call();
        }
    }
    void call(){

    }
    void initView(){
        doctorNmae = (TextView)findViewById(R.id.doctorName);
        doctorPerfession = (TextView)findViewById(R.id.perfession);
        doctorAge = (TextView)findViewById(R.id.doctorAge);
        address = (TextView)findViewById(R.id.doctorAddress);
        sex = (TextView)findViewById(R.id.doctorSex);
        doctorHospital = (TextView)findViewById(R.id.hospital);
        nation = (TextView)findViewById(R.id.nation);
        tel = (TextView)findViewById(R.id.doctorTel);
        email = (TextView)findViewById(R.id.doctorEmail);
        info = (TextView)findViewById(R.id.doctorInfo);
    }
    void setDetailText(DoctorInfoBean bean){
        doctorNmae.setText(bean.name);
        doctorPerfession.setText(bean.perfession);
        doctorAge.setText(bean.age+"");
        address.setText(bean.address);
        sex.setText(bean.sex == 1?"男":"女");
        doctorHospital.setText(bean.hospital);
        nation.setText(bean.nation);
        tel.setText(bean.telPhone);
        email.setText(bean.email);
        info.setText(bean.info);
    }
    private void getDetail(int id){
        if (id <= 0) {
            makeToast("id错误");
            return;
        }
        getLocalDetail();
        MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                setDoctorDetail(JSONResponse);
            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });
        RequestParams params = new RequestParams();
        //params.put("username", SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
        params.put("doctor_id", id);
        manager.post(params, manager.getRequestID(), "getDoctorInfo.do");
    }

    private void getLocalDetail() {
        DoctorInfoBean bean = new DoctorInfoBean(this,doctorID);
        setDetailText(bean);
    }

    private void setDoctorDetail(JSONObject object){
        int state = 0;
        String excption;
        DoctorInfoBean doctorInfoBean = new DoctorInfoBean(this,doctorID);
        try {
            state = object.getInt(PublicRes.STATE);
            excption = object.getString(PublicRes.EXCEPTION);
            JSONObject doctor = object.getJSONObject("doctor");
            doctorInfoBean.name = doctor.getString("doctorName");
            doctorInfoBean.hospital = doctor.getString("hospital");
            doctorInfoBean.perfession = doctor.getString("perfession");
            doctorInfoBean.info = doctor.getString("info");
            doctorInfoBean.age = doctor.getInt("age");
            doctorInfoBean.address = doctor.getString("address");
            doctorInfoBean.email = doctor.getString("email");
            doctorInfoBean.nation = doctor.getString("nation");
            doctorInfoBean.sex = doctor.getInt("sex");
            doctorInfoBean.telPhone = doctor.getString("telePhone");


        } catch (JSONException e) {
            excption = "格式解析错误";
            e.printStackTrace();
        }
        if (state == 0){
            makeToast(excption);
        }
        doctorInfoBean.update();
        setDetailText(doctorInfoBean);
    }
}

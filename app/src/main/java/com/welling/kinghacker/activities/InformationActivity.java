package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.view.View;

import com.welling.kinghacker.bean.DoctorInfoBean;
import com.welling.kinghacker.bean.PersonalInfo;
import com.welling.kinghacker.customView.DoctorListView;
import com.welling.kinghacker.customView.MyInformation;

import com.welling.kinghacker.mtdata.AdapterStruct;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by KingHacker on 3/19/2016.
 **/
public class InformationActivity extends TabActivity {
    MyInformation myInformation;
    private final int myInfoRequestID = 0x123,doctorRequestID = 0x321;

    DoctorListView doctorInfo ;
    private PersonalInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = new PersonalInfo(this,SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
        initView();
        getInfo();
    }
    private void getInfo(){
        setDetailText(info);
        final MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                switch (requestId) {
                    case myInfoRequestID:
                        setMyInfo(JSONResponse);
                        break;
                    case doctorRequestID:
                        setDocList(JSONResponse);
                        break;
                }
            }

            @Override
            public void onFailure(int requestId, int errorCode) {
                makeToast("error:" + errorCode);
                switch (requestId) {
                    case myInfoRequestID:

                        break;
                    case doctorRequestID:

                        break;
                }
            }
        });
        HashMap<String,String> params = new HashMap<>();
        params.put("username", SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
        manager.post(params, myInfoRequestID, "getPatientInfo.do");
        manager.post(params, doctorRequestID, "getDoctorList.do");
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
        if (state == 0){
            makeToast(excption);
        }else {
            doctorInfo.update();
        }

    }

    private void setMyInfo(JSONObject JSONResponse){
        int state = 0;
        String excption;


        try {
            state = JSONResponse.getInt(PublicRes.STATE);
            excption = JSONResponse.getString(PublicRes.EXCEPTION);
            JSONObject json = JSONResponse.getJSONObject("simplePatient");
            info.setUserName(json.getString("name"));
            info.setPhone(json.getString("phoneNum"));
            info.setIDNum(json.getString("idNumber"));
            info.setSex(json.getInt("sex"));
            info.setAge(json.getInt("age"));
        } catch (JSONException e) {
            e.printStackTrace();
            excption = "格式错误";
        }
        if (state == 0){
            makeToast(excption);
        }else {
            setDetailText(info);
            info.updateInfo();
        }
    }
    void setDetailText(PersonalInfo info){
        myInformation.setNameText(info.getUserName());
        myInformation.setPhoneText(info.getPhone());
        myInformation.setIDNumText(info.getIDNum());
        myInformation.setSexText(info.getSex());
        myInformation.setAgeText(info.getBorthDay());
    }
    private void initView(){

        myInformation = new MyInformation(this);
        View personInfoView = myInformation.getRootView();
        setLeftView(personInfoView);

        doctorInfo = new DoctorListView(this);
        setRightView(doctorInfo);

    }
    @Override
    protected void itemSelected(String text){
        switch (text){
            case "编辑个人信息":
                myInformation.setEditEnable(true);
                break;

        }
    }
    @Override
    protected void initRightButton(){
        super.initRightButton();
        setRightButton(android.R.drawable.ic_menu_edit,"编辑个人信息");

    }


}

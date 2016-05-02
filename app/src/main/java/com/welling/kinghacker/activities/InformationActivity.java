package com.welling.kinghacker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.welling.kinghacker.bean.DoctorInfoBean;
import com.welling.kinghacker.customView.DoctorListAdapter;
import com.welling.kinghacker.customView.DoctorListView;
import com.welling.kinghacker.customView.MyInformation;
import com.welling.kinghacker.mtdata.AdapterStruct;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KingHacker on 3/19/2016.
 **/
public class InformationActivity extends TabActivity {
    MyInformation myInformation;
    private final int myInfoRequestID = 0x123,doctorRequestID = 0x321;
    List<DoctorInfoBean> doctorList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getInfo();
    }
    private void getInfo(){
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
            }
        });
        HashMap<String,String> params = new HashMap<>();
        params.put("username", SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
        manager.post(params, myInfoRequestID, "getPatientInfo.do");
        manager.post(params, doctorRequestID, "getDoctorInfo.do");
    }
    private void setDocList(JSONObject jsonObject){
        int state = 0;
        String excption;

        try {
            state = jsonObject.getInt(PublicRes.STATE);
            excption = jsonObject.getString(PublicRes.EXCEPTION);
            JSONArray jsonArray = jsonObject.getJSONArray("doctorList");
            for (int i=0;i<jsonArray.length();i++){
                JSONObject object = new JSONObject((String)jsonArray.get(i));
                DoctorInfoBean doctorInfoBean = new DoctorInfoBean(this);
                doctorInfoBean.doctorID = object.getInt("id");
                doctorList.add(doctorInfoBean);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void setMyInfo(JSONObject JSONResponse){
        int state = 0;
        String excption;
        String name = "";
        String phone = "";
        String IdNumber = "";
        String sex = "",birthday = "";
        try {
            state = JSONResponse.getInt(PublicRes.STATE);
            excption = JSONResponse.getString(PublicRes.EXCEPTION);
            JSONObject json = JSONResponse.getJSONObject("simplePatient");
            name = json.getString("name");
            phone = json.getString("phone");
            IdNumber = json.getString("IdNumber");
            sex = json.getString("sex");
            birthday = json.getString("birthday");
        } catch (JSONException e) {
            e.printStackTrace();
            excption = "格式错误";
        }
        if (state == 1){
            myInformation.setNameText(name);
            myInformation.setPhoneText(phone);
            myInformation.setIDNumText(IdNumber);
            myInformation.setSexText(sex);
            myInformation.setBirthdayText(birthday);
        }else {
            makeToast(excption);
        }
    }
    private void initView(){

        myInformation = new MyInformation(this);
        View personInfoView = myInformation.getRootView();
        setLeftView(personInfoView);

        DoctorListView doctorInfo = new DoctorListView(this);
        List<AdapterStruct> data = new ArrayList<>();
        for (int i = 0;i<10;i++) {
            data.add(new AdapterStruct(R.mipmap.lady, "血糖", "张医生", "张医生毕业于汕头大学医学院，在2002年曾经"));
        }
        doctorInfo.setData(data);
        doctorInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("doctorID", doctorList.get(position).doctorID);
                Intent intent = new Intent(InformationActivity.this,DoctorDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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

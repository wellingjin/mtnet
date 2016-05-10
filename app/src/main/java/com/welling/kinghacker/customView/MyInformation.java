package com.welling.kinghacker.customView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.bean.PersonalInfo;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONObject;

/**
 * Created by KingHacker on 4/30/2016.
 * 我的信息
 */
public class MyInformation {
    private EditText editName,editPhone,editID;
    private TextView textSex,textAge;
    private View rootView ;
    private Button saveButton;
    private PersonalInfo info;
    private Context context;
    public MyInformation(Context context){
        info = new PersonalInfo(context);
        this.context = context;
        info.setAccount(SystemTool.getSystem(context).getStringValue(PublicRes.ACCOUNT));
        rootView = SystemTool.getSystem(context).getView(R.layout.layout_personal_info);
        editName = (EditText)rootView.findViewById(R.id.personName);
        editPhone = (EditText)rootView.findViewById(R.id.personPhone);
        editID = (EditText)rootView.findViewById(R.id.personID);
        textAge = (TextView)rootView.findViewById(R.id.personBirdthday);
        textSex = (TextView)rootView.findViewById(R.id.personSex);
        saveButton = (Button)rootView.findViewById(R.id.save);
        textSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSexDialog();
            }
        });
        textAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });
        setEditEnable(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setEditEnable(false);
                saveInfo();
            }
        });
    }
    private void showSexDialog(){
        Log.i("information","sex");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("");
        //    指定下拉列表的显示数据

        final String[] cities ={"女","男"};



        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               setSexText(which);
            }
        });
        builder.show();
    }
    private void showTimeDialog(){
        Log.i("information","data");
        DatePickerDialog pickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                setAgeText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        },2016,2,3);
        pickerDialog.show();
    }
    private void saveInfo(){
        MTHttpManager manager = new MTHttpManager();
        setData();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                info.updateInfo();
            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });
        RequestParams params = new RequestParams();
        params.put("username",info.getAccount());
        params.put("name",info.getUserName());
        params.put("sex",info.getSex());
        params.put("birthday",info.getBorthDay());
        params.put("idNumber",info.getIDNum());
        params.put("phone",info.getPhone());
        manager.post(params,manager.getRequestID(),"/updatePatientInfo");
    }
    //public


    public void setNameText(String text) {
        this.editName.setText(text);
    }
    public void setPhoneText(String text){
        editPhone.setText(text);
    }
    public void setIDNumText(String text){
        editID.setText(text);
    }
    public void setSexText(int sex){
        String text = "男";
        if (sex == 0){
            text = "女";
        }
        textSex.setText(text);
    }
    public void setAgeText(String text){
        textAge.setText(text);
    }

    public View getRootView() {
        return rootView;
    }
    public void setEditEnable(boolean editEnable){
        editPhone.setEnabled(editEnable);
        editName.setEnabled(editEnable);
        editID.setEnabled(editEnable);
        textSex.setClickable(editEnable);
        textAge.setClickable(editEnable);
        if (editEnable){
            saveButton.setVisibility(View.VISIBLE);
        }else {
            saveButton.setVisibility(View.GONE);
        }
    }


    public void setData(){
        info.setIDNum(editID.getText().toString());
        info.setPhone(editPhone.getText().toString());
        info.setUserName(editName.getText().toString());
        info.setSex(textSex.getText().toString().equals("男") ? 1 : 0);
        info.setBorthDay(textAge.getText().toString());
    }
}

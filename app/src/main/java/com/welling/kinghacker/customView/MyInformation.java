package com.welling.kinghacker.customView;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.bean.PersonalInfo;
import com.welling.kinghacker.tools.SystemTool;

/**
 * Created by KingHacker on 4/30/2016.
 * 我的信息
 */
public class MyInformation {
    private EditText editName,editPhone,editID;
    private TextView textSex,textAge;
    private View rootView ;
    private Context context;
    public MyInformation(Context context){
        this.context = context;
        rootView = SystemTool.getSystem(context).getView(R.layout.layout_personal_info);
        editName = (EditText)rootView.findViewById(R.id.personName);
        editPhone = (EditText)rootView.findViewById(R.id.personPhone);
        editID = (EditText)rootView.findViewById(R.id.personID);
        textAge = (TextView)rootView.findViewById(R.id.personBirdthday);
        textSex = (TextView)rootView.findViewById(R.id.personSex);
        textSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("person","clicksex");
            }
        });
        textAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("person","clickday");
            }
        });
        setEditEnable(false);
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
    }


    public void saveData(){
        PersonalInfo info = new PersonalInfo(context);
        info.setIDNum(editID.getText().toString());
        info.setPhone(editPhone.getText().toString());
        info.setUserName(editName.getText().toString());
        info.setSex(textSex.getText().toString().equals("男")?1:0);
        info.setBorthDay(textAge.getText().toString());
        info.updateInfo();

    }
}

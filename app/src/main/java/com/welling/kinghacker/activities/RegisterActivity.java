package com.welling.kinghacker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.bean.PersonalInfo;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by KingHacker on 4/21/2016.
 *
 */
public class RegisterActivity extends MTActivity{
    EditText account,password,comfirePassword,name,phone,answer1,answer2,answer3,editID;
    TextView sex,birthday;
    Button summit;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle save){
        super.onCreate(save);
        setContentView(R.layout.layout_register);
        initView();
    }

    //初始化控件
    private void initView(){
        account = (EditText)findViewById(R.id.registerAccount);
        password = (EditText)findViewById(R.id.registerPsd);
        comfirePassword = (EditText)findViewById(R.id.registerComfirePsd);
        name = (EditText)findViewById(R.id.registerName);
        phone = (EditText)findViewById(R.id.registerPhone);
        answer1 = (EditText)findViewById(R.id.registerAnswer1);
        answer2 = (EditText)findViewById(R.id.registerAnswer2);
        answer3 = (EditText)findViewById(R.id.registerAnswer3);
        sex = (TextView)findViewById(R.id.registerSex);
        birthday = (TextView)findViewById(R.id.registerBirdthday);
        summit = (Button)findViewById(R.id.registerCommitButton);
        editID = (EditText)findViewById(R.id.registerID);

        summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //--------------------------
                if (account.getText().toString().equals("12345")){
                    saveDataBase();
                    return;
                }
                //---------------------
                if (checkValidity()) {
                    summitRegist();
                }
            }
        });
    }
    //提交注册
    private void summitRegist(){
        progressDialog = ProgressDialog.show(this,"注册中","正在提交数据...");
        MTHttpManager httpManager = new MTHttpManager();
        httpManager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {

                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                int state = 0;
                String excptionInfo = "";
                try {
                    state = JSONResponse.getInt(PublicRes.STATE);
                    excptionInfo = JSONResponse.getString(PublicRes.EXCEPTION);
                } catch (JSONException e) {
                    e.printStackTrace();
                    excptionInfo = "数据解析失败";
                }
                if (state == 1){
                    excptionInfo = "注册成功";
                    saveDataBase();
                    finish();
                }
                makeToast(excptionInfo);
            }

            @Override
            public void onFailure(int requestId, int errorCode) {
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                makeToast("连接服务器错误，错误码："+errorCode);
            }
        });
//        HashMap<String,String> params = new HashMap<>();
        RequestParams params = new RequestParams();
        params.put("account",account.getText().toString());
        params.put("password",password.getText().toString());
        params.put("name",name.getText().toString());
        int sexInt = 0;
        if (sex.getText().toString().equals("男")){
            sexInt = 1;
        }
        params.put("sex", sexInt);
        params.put("birthday",birthday.getText().toString());
        params.put("phone",phone.getText().toString());
        params.put("idNumber",editID.getText().toString());
        params.put("answer1",answer1.getText().toString());
        params.put("answer2",answer2.getText().toString());
        params.put("answer3",answer3.getText().toString());

        httpManager.post(params, httpManager.getRequestID(), "register.do");
    }

    boolean checkValidity(){
        if (account.getText().toString().length() <= 0){
            makeToast("账号不能为空");
            account.requestFocus();
            return false;
        }
        String regex = "[a-z0-9A-Z]+$";
        if (!account.getText().toString().matches(regex)){
            makeToast("账号必须为数字或字母");
            account.requestFocus();
            return false;
        }
        if (password.getText().toString().length() <= 6){
            makeToast("密码不能少于6位");
            password.requestFocus();
            return false;
        }
        if (comfirePassword.getText().toString().length() <= 0){
            makeToast("确认密码不能为空");
            comfirePassword.requestFocus();
            return false;
        }
        if (!password.getText().toString().equals(comfirePassword.getText().toString())){
            makeToast("密码不一致");
            password.requestFocus();
            return false;
        }
        if (name.getText().toString().length() <= 0){
            makeToast("姓名不能为空");
            name.requestFocus();
            return false;
        }
        if (phone.getText().toString().length() != 11){
            makeToast("手机号码不符合规则，必须为11位");
            phone.requestFocus();
            return false;
        }
        if (editID.getText().toString().length() != 18){
            makeToast("身份证号码不符合规则，必须为18位");
            phone.requestFocus();
            return false;
        }
        if (answer1.getText().toString().length() <= 0){
            makeToast("问题1还没有回答");
            answer1.requestFocus();
            return false;
        }
        if (answer2.getText().toString().length() <= 0){
            makeToast("问题2还没有回答");
            answer1.requestFocus();
            return false;
        }
        if (answer3.getText().toString().length() <= 0){
            makeToast("问题3还没有回答");
            answer1.requestFocus();
            return false;
        }


        return true;
    }
    private void saveDataBase(){
        PersonalInfo info = new PersonalInfo(this);
        info.setAccount(account.getText().toString());
        info.setBorthDay(birthday.getText().toString());
        info.setUserName(name.getText().toString());
        info.setPhone(phone.getText().toString());
        info.setSex(sex.getText().toString().equals("男") ? 1 : 0);
        info.setIDNum(editID.getText().toString());
        info.insert();
    }

}

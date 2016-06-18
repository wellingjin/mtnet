package com.welling.kinghacker.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by KingHacker on 3/18/2016.
 **/
public class ChangePasswordActivity extends MTActivity{
    EditText account,oldPassword,answer1,answer2,answer3,password,comfirePassword;
    Button summit;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_layout);
        initView();
        setIsBackEnable(true);
        setRightButtonEnable(false);
        setActionBarTitle("修改密码");
    }
    void initView(){
        account = (EditText)findViewById(R.id.changeAccount);
        oldPassword = (EditText)findViewById(R.id.oldPassword);
        answer1 = (EditText)findViewById(R.id.changeAnswer1);
        answer2 = (EditText)findViewById(R.id.changeAnswer2);
        answer3 = (EditText)findViewById(R.id.changeAnswer3);
        password = (EditText)findViewById(R.id.changePassword);
        comfirePassword = (EditText)findViewById(R.id.changeComfirePassword);
        summit = (Button)findViewById(R.id.changeSummit);
        summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    changePassword();
                }
            }
        });
    }
    private boolean check(){
        if (password.getText().toString().length() <= 7){
            makeToast("密码长度不能少于8位");
            return false;
        }
        if (!password.getText().toString().equals(comfirePassword.getText().toString())){
            makeToast("密码不一致");
            return false;
        }
        return true;
    }
    private void changePassword(){
        progressDialog = ProgressDialog.show(this,"请稍候","正在验证登录信息...");
        MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                int state = 0;
                String error;
                try {
                    state = JSONResponse.getInt(PublicRes.STATE);
                    error = JSONResponse.getString(PublicRes.EXCEPTION);
                } catch (JSONException e) {
                    error = "格式解析错误";
                }
                if (state == 1){
                    makeToast("密码修改成功");
                    finish();
                }else {
                    makeToast(error);
                }
                dismissProgress();

            }

            @Override
            public void onFailure(int requestId, int errorCode) {
                dismissProgress();
                makeToast("修改失败，错误码："+errorCode);
            }
        });
        RequestParams params = new RequestParams();
        params.put("username",account.getText().toString());
        params.put("oldPassword",oldPassword.getText().toString());
        params.put("answer1",answer1.getText().toString());
        params.put("answer2", answer2.getText().toString());
        params.put("answer3",answer3.getText().toString());
        params.put("newPassword",password.getText().toString());
        manager.post(params,manager.getRequestID(),"changePassword.do");
    }
    private void dismissProgress(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}

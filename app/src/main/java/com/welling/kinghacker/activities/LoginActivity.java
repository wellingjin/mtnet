package com.welling.kinghacker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.welling.kinghacker.customView.RippleView;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private EditText account,password;
    final private int passwordLength = 8;
    private MTHttpManager httpManager;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemTool.getSystem(this).saveBooleanKV(PublicRes.AUTOLOGIN, false);
        String accountStr = SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT);
        String passwordStr = SystemTool.getSystem(this).getStringValue(PublicRes.PASSWORD, "");
        Boolean isAutoLogin = SystemTool.getSystem(this).getBooleanValue(PublicRes.AUTOLOGIN, false);
        if (isAutoLogin){
            login(accountStr,passwordStr);
        }

        setContentView(R.layout.activity_login);
        account = (EditText)findViewById(R.id.account);
        password = (EditText)findViewById(R.id.password);
        account.setText(accountStr);
        password.setText(passwordStr);

        RippleView rippleViewLogin = (RippleView)findViewById(R.id.rippleViewOfLogin);
        rippleViewLogin.setFrameRate(50);
        rippleViewLogin.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                String accountStr = account.getText().toString();
                String passwordStr = password.getText().toString();
                login(accountStr,passwordStr);
            }
        });

        TextView forgerPassword = (TextView)findViewById(R.id.forgetpassword);
        TextView regist = (TextView)findViewById(R.id.register);
        forgerPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpActivity(ChangePasswordActivity.class);
            }
        });
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               jumpActivity(RegisterActivity.class);
            }
        });
    }

    private void login(String accountStr,String passwordStr){

        if (checkRegulation(accountStr,passwordStr)){
            progressDialog = ProgressDialog.show(this,"请稍候","正在验证登录信息...");
            httpManager = new MTHttpManager();
            HashMap<String,String> params = new HashMap<>();
            params.put("name",accountStr);
            params.put("password", passwordStr);
            httpManager.post(params, httpManager.getRequestID(), "user/login.do");
            httpManager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
                @Override
                public void onSuccess(int requestId, JSONObject JSONResponse) {
                    int state = PublicRes.ERROR;
                    String cookie;
                    String exceptionInfo;
                    try {
                        state = JSONResponse.getInt(PublicRes.STATE);
                        cookie = JSONResponse.getString(PublicRes.COOKIE);
                        exceptionInfo = JSONResponse.getString(PublicRes.EXCEPTION);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        cookie = null;
                        exceptionInfo = "返回格式错误";
                    }
                    dimissProgress();
                    if (state == PublicRes.OK) {
                        makeToast("登录成功");
                        SystemTool.getSystem(LoginActivity.this).saveBooleanKV(PublicRes.AUTOLOGIN, true);
                        SystemTool.getSystem(LoginActivity.this).saveStringKV(PublicRes.COOKIE, cookie);
                        jumpActivity(HomeActivity.class);
                    }else{
                        makeToast(exceptionInfo);
                    }
                }

                @Override
                public void onFailure(int requestId, int errorCode) {
                    dimissProgress();
                    makeToast("连接服务器失败，错误码："+errorCode);
                }
            });

        }

    }
    private void dimissProgress(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    private boolean checkRegulation(String name,String psd){

//        **********--Super Account--************************
        if (name.equals("12345") && psd.equals("welling")) {
            makeToast("超级用户登录成功");

            SystemTool.getSystem(this).saveBooleanKV(PublicRes.AUTOLOGIN, true);

            saveAccountPsd(name,psd);
            jumpActivity(HomeActivity.class);
            return false;
        }
//        ************************************

        if (name.length() <= 0){
            makeToast("账号不能为空");
            return false;
        }
        String regex = "[a-z0-9A-Z]+$";
        if (!name.matches(regex)){
            makeToast("账号必须为数字或字母");
            return false;
        }
        if (psd.length() <= 0){
            makeToast("密码不能为空");
            return false;
        }
        if (psd.length() < passwordLength){
            makeToast("密码长度不能少于" + passwordLength +"位");
            return false;
        }
        saveAccountPsd(name,psd);

        return true;
    }
    private void saveAccountPsd(String name,String psd){
        Map<String,String> map = new HashMap<>();
        map.put(PublicRes.ACCOUNT, name);
        map.put(PublicRes.PASSWORD, psd);
        SystemTool.getSystem(this).saveStringKV(map);
    }
    public void jumpActivity(Class cls){
        Intent intent = new Intent(this,cls);
        startActivity(intent);
    }
    private void makeToast(String content){
        Toast.makeText(this,content,Toast.LENGTH_LONG).show();
    }
}

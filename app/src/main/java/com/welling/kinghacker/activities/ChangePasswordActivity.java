package com.welling.kinghacker.activities;

import android.os.Bundle;


/**
 * Created by KingHacker on 3/18/2016.
 **/
public class ChangePasswordActivity extends MTActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_layout);

        setIsBackEnable(true);
        setRightButtonEnable(false);
        setActionBarTitle("修改密码");


    }
}

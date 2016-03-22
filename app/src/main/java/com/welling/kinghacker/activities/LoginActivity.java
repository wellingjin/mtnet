package com.welling.kinghacker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.welling.kinghacker.customView.RippleView;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RippleView rippleView = (RippleView)findViewById(R.id.rippleViewOfLogin);
        rippleView.setFrameRate(50);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                jumpActivity(HomeActivity.class);
            }
        });

        TextView forgerPassword = (TextView)findViewById(R.id.forgetpassword);
        forgerPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpActivity(ChangePasswordActivity.class);
            }
        });
    }
    public void jumpActivity(Class cls){
        Intent intent = new Intent(this,cls);
        startActivity(intent);
    }
}

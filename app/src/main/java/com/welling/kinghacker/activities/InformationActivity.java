package com.welling.kinghacker.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by KingHacker on 3/19/2016.
 **/
public class InformationActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView(){
        TextView textView = new TextView(this);
        textView.setText("sdfafd");
        textView.setTextColor(Color.RED);
        setLeftView(textView);
        TextView textView1 = new TextView(this);
        textView1.setText("sasfafdfafd");
        textView1.setTextColor(Color.RED);
        setRightView(textView1);
    }

}

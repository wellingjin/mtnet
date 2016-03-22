package com.welling.kinghacker.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.welling.kinghacker.customView.PagerView;

/**
 * Created by KingHacker on 3/19/2016.
 **/
public class TabActivity extends AppCompatActivity{
    private boolean isBackEnable;
    private TextView leftTab,rightTab;
    private int selectColor,unSelectColor;
    private FrameLayout leftView,rightView;
    private PagerView pagerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initActionBar();

    }
    private void initActionBar(){
        isBackEnable = true;
        selectColor = R.color.selectColor;
        unSelectColor = R.color.unSelectTabColor;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.tab_view_layout,null);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(rootView, layoutParams);
//        left button
        ImageView leftButton = (ImageView)findViewById(R.id.backButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBackEnable) {
                    onBackKey();
                } else {
                    onLeftButtonClick();
                }
            }
        });
        leftTab = (TextView)findViewById(R.id.leftTab);
        rightTab = (TextView)findViewById(R.id.rightTab);
        setTab(true);
        leftTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTab(true);
                pagerView.setCurrentPage(0);
            }
        });
        rightTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTab(false);
                pagerView.setCurrentPage(1);
            }
        });
    }

    private void setTab(boolean isLeftTab){
        if (isLeftTab){
            leftTab.setBackgroundColor(getResources().getColor(selectColor));
            rightTab.setBackgroundColor(getResources().getColor(unSelectColor));


        }else {
            leftTab.setBackgroundColor(getResources().getColor(unSelectColor));
            rightTab.setBackgroundColor(getResources().getColor(selectColor));

        }
    }

    private void initView(){
        pagerView = new PagerView(this);
        setContentView(pagerView);
        pagerView.setIsCreateDot(false);
        FrameLayout.LayoutParams matchParent = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        leftView = new FrameLayout(this);
        rightView = new FrameLayout(this);
        leftView.setLayoutParams(matchParent);
        rightView.setLayoutParams(matchParent);

        pagerView.addItem(leftView);
        pagerView.addItem(rightView);
        pagerView.commit();
        pagerView.setOnPagerChangedListener(new PagerView.OnPagerChangedListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    setTab(true);
                }else {
                    setTab(false);
                }
            }
        });
    }
   protected void setLeftView(View view){
       // TODO: have to override this method
       leftView.addView(view);
   }
    protected void setRightView(View view){
        // TODO: have to override this method
        rightView.addView(view);
    }

    protected void onLeftButtonClick(){
    //TODO:  if you need to custom the left button ,override this method
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return onBackKey();
        }
        return super.onKeyDown(keyCode, event);
    }
//    allow subclass to override this method
    protected boolean onBackKey(){
        finish();
        return true;
    }
}

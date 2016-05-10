package com.welling.kinghacker.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.welling.kinghacker.customView.OverFlowView;
import com.welling.kinghacker.customView.PagerView;
import com.welling.kinghacker.customView.RippleView;
import com.welling.kinghacker.tools.SystemTool;

/**
 * Created by KingHacker on 3/19/2016.
 **/
public class TabActivity extends AppCompatActivity{
    private boolean isBackEnable;
    private TextView leftTab,rightTab;
    private int selectColor,unSelectColor;
    private FrameLayout leftView,rightView;
    private PagerView pagerView;
    private OverFlowView overFlowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        View rootView = SystemTool.getSystem(this).getView(R.layout.tab_view_layout);


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
        RippleView rightButton = (RippleView)rootView.findViewById(R.id.tabMore);
        initRightButton();
        rightButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                onRightClick();
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

    protected void onRightClick() {
        setOverFlowPosition();
        overFlowView.showOverFlow(pagerView);
    }

    protected void initRightButton(){
        if (overFlowView == null){
            overFlowView = new OverFlowView(this);
        }
        overFlowView.setOnRowClickListener(new OverFlowView.OnRowClickListener() {
            @Override
            public void onClick(String text) {
                itemSelected(text);
            }
        });
    }
    protected void itemSelected(String text){

    }
    protected void setRightButton(int id,String text){
        overFlowView.addItem(id, text);
    }
    //设置overflow的位置
    private void setOverFlowPosition(){
        int offsetX,offsetY;
        offsetX = 50;

        offsetY = SystemTool.getSystem(this).getStatusBarHeight() + getSupportActionBar().getHeight();
        overFlowView.setOffset(offsetX, offsetY);
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
                if (position == 0) {
                    setTab(true);
                } else {
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
    protected void gotoActivity(Class activity){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
    }
    protected void makeToast(String content){
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
    private  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}

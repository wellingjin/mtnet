package com.welling.kinghacker.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.welling.kinghacker.customView.ActionBarView;
import com.welling.kinghacker.customView.OverFlowView;
import com.welling.kinghacker.tools.SystemTool;

import java.util.List;

/**
 * Created by KingHacker on 3/13/2016.
* */
public class MTActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private ActionBarView actionBarView;
    private OverFlowView overFlowView;
    private View parentView;
    private boolean isOverFlow = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initActionBar();
    }
    //初始化actionbar
    private void initActionBar(){
        actionBarView = new ActionBarView(this);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.LayoutParams wrapContent = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT
        );
        actionBar.setCustomView(actionBarView.getRootView(), wrapContent);
        if (actionBarView.isRightButtonEnable()||actionBarView.isLeftButtonEnable()) {
            setOverFlowView();
            actionBarView.setOnclickListener(new ActionBarView.OnclickListener() {
                @Override
                public void leftButtonOnClick(boolean isBack) {
                    if (isBack) {
                        onBackKey();
                    } else {
                        onLeftButtonClick();
                    }
                }

                @Override
                public void rightButtonOnClick() {
                    if (isOverFlow) {
                        Log.i("MTActivity","rightButton");
                        setOverFlowPosition();
                        overFlowView.showOverFlow(parentView);
                    }
                }
            });
            setIsBackEnable(true);
        }

    }
    //    设置overflow,其子类必须在这里面实现增加item
    protected void setOverFlowView(){
        overFlowView = new OverFlowView(this);
        overFlowView.setOnRowClickListener(new OverFlowView.OnRowClickListener() {

            @Override
            public void onClick(String text) {
                selectItem(text);
            }
        });
    }
//   子类 必须调用这个方法
    protected void setParentView(View parentView){
        isOverFlow = true;
        this.parentView = parentView;
    }
//设置overflow的位置
    private void setOverFlowPosition(){
        int offsetX,offsetY;
        offsetX = 50;

        offsetY = SystemTool.getSystem(this).getStatusBarHeight() + actionBar.getHeight();
        overFlowView.setOffset(offsetX, offsetY);
    }

    protected ActionBar getMTActionBar(){
        return actionBar;
    }
//    提供子类增加item的方法
    protected void setOverFlowViewItems(List<OverFlowItem> items){
        for(int i= 0;i < items.size(); ++i){
            overFlowView.addItem(items.get(i).imageID,items.get(i).text);
        }
    }
//    设置actionbar的title
    protected void setActionBarTitle(String title){
        actionBarView.setTitle(title);
    }
//    item被选中的响应事件，重写这个方法
    protected void selectItem(String text){
    }

    protected void setIsBackEnable(boolean enable){
        actionBarView.setIsBackEnable(enable);
    }
    protected void setCustonLsftButton(Drawable drawable){
        actionBarView.setCustomLeftButton(drawable);
    }

    protected void setLeftButtonEnable(boolean enable){
        actionBarView.setLeftButtonEnable(enable);
    }
    protected void setRightButtonEnable(boolean enable){
        actionBarView.setRightButtonEnable(enable);
    }

    protected void onLeftButtonClick(){

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return onBackKey();
        }
        return super.onKeyDown(keyCode, event);
    }

    //    返回键和左上角的响应事件，子类可以重写来自定义
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
    private   boolean isShouldHideInput(View v, MotionEvent event) {
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


//overflowitem的结构体
    class OverFlowItem{
        int imageID;
        String text;
        OverFlowItem(int imageID,String text){
            this.imageID = imageID;
            this.text = text;
        }
    }
}

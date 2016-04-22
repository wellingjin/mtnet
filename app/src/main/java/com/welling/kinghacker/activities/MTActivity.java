package com.welling.kinghacker.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

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
        initActionBar();
    }
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

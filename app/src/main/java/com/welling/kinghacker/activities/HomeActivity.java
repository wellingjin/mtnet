package com.welling.kinghacker.activities;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.welling.kinghacker.customView.BloodOxygenView;
import com.welling.kinghacker.customView.BloodPressureView;
import com.welling.kinghacker.customView.BloodSugerView;
import com.welling.kinghacker.customView.MTToast;
import com.welling.kinghacker.customView.PagerView;
import com.welling.kinghacker.customView.RippleView;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 2/28/2016.
 **/
public class HomeActivity extends MTActivity {
    //全局变量定义
    enum cuteItem {ED,BS,BP,BO,BF}//分别表示，心电，血糖，血压，血氧，血脂
    int screenWidth;
    PagerView pagerView;
    String [] titleText = new String[5];
    int[] color = new int[5];
    private BloodSugerView bloodSugerView;
    float bloodSugerValue = 8f;
    private BloodPressureView bloodPressureView;
    private boolean isExit = false;
    private BloodOxygenView bloodOxygenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        初始化
        init();
//        正文布局
        setContentView(R.layout.home_layout);
        setParentView(findViewById(R.id.homeRootView));
        initBloodSuger();
        initBloodPressure();
        initBloodOxygen();
        initBloodFat();
        setPagerView();
    }


    @Override
    protected void selectItem(String text){
        super.selectItem(text);
        if (text.equals(getString(R.string.about))){
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
        }else if (text.equals(getString(R.string.setting))){
            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);

        }


    }

    private void init(){
        screenWidth = (int)SystemTool.getSystem(this).getScreenWidth();

        titleText[cuteItem.ED.ordinal()] = PublicRes.getInstance().electrocarDiogram;
        titleText[cuteItem.BF.ordinal()] = PublicRes.getInstance().bloodFat;
        titleText[cuteItem.BO.ordinal()] = PublicRes.getInstance().bloodOxygen;
        titleText[cuteItem.BP.ordinal()] = PublicRes.getInstance().bloodPresure;
        titleText[cuteItem.BS.ordinal()] = PublicRes.getInstance().bloodSuger;

        color[cuteItem.ED.ordinal()] = getResources().getColor(R.color.electrocarDiogramBGColor);
        color[cuteItem.BF.ordinal()] = getResources().getColor(R.color.bloodFatBgColor);
        color[cuteItem.BO.ordinal()] = getResources().getColor(R.color.bloodOxygenBgColor);
        color[cuteItem.BP.ordinal()] = getResources().getColor(R.color.bloodPressionBgColor);
        color[cuteItem.BS.ordinal()] = getResources().getColor(R.color.bloodSugerBGColor);

        setActionBarTitle(titleText[0]);

        setCustonLsftButton(getResources().getDrawable(R.mipmap.gentleman_low));

    }
    @Override
    protected void setOverFlowView(){
        super.setOverFlowView();
        List<OverFlowItem> items = new ArrayList<>();
        items.add(new OverFlowItem(android.R.drawable.ic_menu_preferences, getResources().getString(R.string.setting)));
        items.add(new OverFlowItem(android.R.drawable.ic_menu_help, getResources().getString(R.string.about)));
        setOverFlowViewItems(items);
    }

    @Override
    protected void onLeftButtonClick(){
        Intent intent = new Intent(this,InformationActivity.class);
        startActivity(intent);
    }

    private void initBloodSuger(){
        bloodSugerView = new BloodSugerView(this);
//        设置血糖值和容器高度
        bloodSugerView.setBloodSugerValue(bloodSugerValue);

    }
    private void initBloodPressure(){
        bloodPressureView = new BloodPressureView(this);
        bloodPressureView.setValues(100, 120, 140);
        RippleView bloodPressureButton = (RippleView)findViewById(R.id.bloodPressureButton);
        bloodPressureButton.setRippleDuration(bloodPressureButton.getRippleDuration() / 2);
        bloodPressureButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(HomeActivity.this, BloodPressureActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initBloodOxygen(){
        bloodOxygenView = new BloodOxygenView(this);
        bloodOxygenView.setBloodOxygenValue(56f);
        RippleView bloodOxygenButton = (RippleView)findViewById(R.id.bloodOxygenButton);
        bloodOxygenButton.setRippleDuration(bloodOxygenButton.getRippleDuration()/2);
        bloodOxygenButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(HomeActivity.this, BloodOxygenActivity.class);
                startActivity(intent);
            }
        });

    }
    private void initBloodFat(){

    }


    private void setPagerView(){
        pagerView = (PagerView) findViewById(R.id.flipperView);
        LayoutInflater flater = getLayoutInflater();
        View electrocarDiogram = flater.inflate(R.layout.electrocar_diogram, null);
        View bloodSuger = bloodSugerView.getBloodSugerView();

        ImageView item2;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

        final View bloodPressure = bloodPressureView.getBloodPressureView();


        item2 = new ImageView(this);

        item2.setBackgroundColor(getResources().getColor(R.color.bloodFatBgColor));
        item2.setLayoutParams(layoutParams);

        pagerView.addItem(electrocarDiogram);
        pagerView.addItem(bloodSuger);
        pagerView.addItem(bloodPressure);
        pagerView.addItem(bloodOxygenView.getBloodOxygenView());
        pagerView.addItem(item2);
        pagerView.commit();
        pagerView.setOnPagerChangedListener(new PagerView.OnPagerChangedListener() {

            @Override
            public void onPageSelected(int position) {
                setActionBarTitle(titleText[position]);
                if (position == cuteItem.BS.ordinal()) {
                    bloodSugerView.startAnimation();
                }else if (position == cuteItem.BP.ordinal()){
                   bloodPressureView.startAnimation();
                }else if (position == cuteItem.BO.ordinal()){
                    bloodOxygenView.startAnimation();
                }

            }
        });
    }
    public void jumpActivity(View view){
        Intent intent = new Intent(this,BloodSugerActivity.class);
        startActivity(intent);
    }


    @Override
    protected boolean onBackKey(){
        if (isExit){
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        } else {
            MTToast toast = new MTToast(this);
            toast.makeText("再按一次退出程序", MTToast.LONGTIME);
            toast.showAtView(findViewById(R.id.homeRootView));
            toast.setToastStaticListener(new MTToast.ToastStaticListener() {
                @Override
                public void toastDispeared() {
                    isExit = false;
                }
            });
            isExit = true;
        }
        return true;
    }
}

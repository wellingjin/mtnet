package com.welling.kinghacker.activities;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;


import com.example.bluetooth.le.DeviceScanActivity;
import com.welling.kinghacker.customView.BloodPressureView;
import com.welling.kinghacker.customView.FilterView;
import com.welling.kinghacker.customView.LineBlood;
import com.welling.kinghacker.customView.OverFlowView;
import com.welling.kinghacker.tools.PublicRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsw on 2016/3/19.
 **/
public class BloodPressureActivity extends MTActivity {

    BloodPressureView singleBloodPressureView;
    FrameLayout rootView;
    private enum ViewType{single,multiple,all}
    private ViewType viewType;
    private Animation rightInAnimation;
    private LineBlood lineBlood;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_moudle_layout);
        init();
    }
    private void init(){
        setParentView(findViewById(R.id.universalMoudleRootView));
        singleBloodPressureView = new BloodPressureView(this);
        rootView = (FrameLayout)findViewById(R.id.universalUpView);
        rootView.addView(singleBloodPressureView.getBloodPressureView());

        viewType = ViewType.single;
        singleBloodPressureView.setValues(120, 100, 78);
        singleBloodPressureView.startAnimation();

        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Button synInfoButton = (Button)findViewById(R.id.synButton);
        synInfoButton.setText("同步血压仪数据");
        synInfoButton.setBackgroundColor(getResources().getColor(R.color.bloodSugerBGColor));
        Button medicineButton = (Button)findViewById(R.id.medicineQueryButton);
        medicineButton.setBackgroundColor(getResources().getColor(R.color.bloodSugerBGColor));
        Button doctorInfoButton = (Button)findViewById(R.id.doctorButton);
        doctorInfoButton.setBackgroundColor(getResources().getColor(R.color.bloodSugerBGColor));
        synInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoActivity(DeviceScanActivity.class);
            }
        });
    }


    @Override
    protected void onPostCreate(Bundle saveBundle){
        super.onPostCreate(saveBundle);
        initActionBar();
    }

    private void initActionBar(){
        setActionBarTitle(getString(R.string.blood_pressu));
        setIsBackEnable(true);
    }

    @Override
    protected void setOverFlowView(){
        super.setOverFlowView();
        List<OverFlowItem> items = new ArrayList<>();
        items.add(new OverFlowItem(OverFlowView.NONE, PublicRes.getInstance().bloodSugerItem1));
        items.add(new OverFlowItem(OverFlowView.NONE, "历史查询"));
        items.add(new OverFlowItem(OverFlowView.NONE, PublicRes.getInstance().bloodSugerItem3));
        setOverFlowViewItems(items);
    }

    @Override
    protected void selectItem(String text){
        super.selectItem(text);
        if (text.contentEquals(PublicRes.getInstance().bloodSugerItem1)){
            if (viewType != ViewType.single){
                rootView.removeAllViews();

                rootView.startAnimation(rightInAnimation);
                singleBloodPressureView.startAnimation();
                rootView.addView(singleBloodPressureView.getBloodPressureView());

                viewType = ViewType.single;
            }
        }else if (text.contentEquals("历史查询")){
            if (viewType != ViewType.all){
                rootView.removeAllViews();
                if (lineBlood == null){
                    lineBlood=new LineBlood(this);
                }

                rootView.startAnimation(rightInAnimation);
                rootView.addView(lineBlood);

                viewType = ViewType.all;
            }
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem3)){
            FilterView filterView = new FilterView(this);
            filterView.showFilter(findViewById(R.id.universalMoudleRootView));
        }
    }
}

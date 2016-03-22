package com.welling.kinghacker.activities;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.welling.kinghacker.customView.BloodOxygenChartView;
import com.welling.kinghacker.customView.BloodOxygenView;

import com.welling.kinghacker.customView.FilterView;
import com.welling.kinghacker.customView.OverFlowView;

import com.welling.kinghacker.tools.PublicRes;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/10/2016.
 **/
public class BloodOxygenActivity extends MTActivity {

    private FrameLayout rootView;
    private enum ViewType{single,multiple,all}
    private ViewType viewType;
    private BloodOxygenView singleBloodOxygenView;
    private Animation rightInAnimation;
    private BloodOxygenChartView bloodOxygenChartView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_moudle_layout);
        init();
    }

    private void init() {
        setParentView(findViewById(R.id.universalMoudleRootView));
        singleBloodOxygenView = new BloodOxygenView(this);
        rootView = (FrameLayout)findViewById(R.id.universalUpView);
        rootView.addView(singleBloodOxygenView.getBloodOxygenView());

        viewType = ViewType.single;
        singleBloodOxygenView.setBloodOxygenValue(85f);
        singleBloodOxygenView.startAnimation();

        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        Button synInfoButton = (Button)findViewById(R.id.synButton);
        synInfoButton.setBackgroundColor(getResources().getColor(R.color.bloodOxygenBgColor));
        Button medicineButton = (Button)findViewById(R.id.medicineQueryButton);
        medicineButton.setBackgroundColor(getResources().getColor(R.color.bloodOxygenBgColor));
        Button doctorInfoButton = (Button)findViewById(R.id.doctorButton);
        doctorInfoButton.setBackgroundColor(getResources().getColor(R.color.bloodOxygenBgColor));

    }
    @Override
    protected void onPostCreate(Bundle saveBundle){
        super.onPostCreate(saveBundle);
        initActionBar();
    }
    //    ������ͼ
    private void drawLineChart(){
        bloodOxygenChartView = new BloodOxygenChartView(this);
    }

    private void initActionBar(){
        setIsBackEnable(true);
        setActionBarTitle(PublicRes.getInstance().bloodOxygen);
    }
    @Override
    protected void setOverFlowView(){
        super.setOverFlowView();
        List<OverFlowItem> items = new ArrayList<>();
        items.add(new OverFlowItem(OverFlowView.NONE, PublicRes.getInstance().bloodSugerItem1));
        items.add(new OverFlowItem(OverFlowView.NONE, PublicRes.getInstance().bloodSugerItem2));
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
                singleBloodOxygenView.startAnimation();
                rootView.addView(singleBloodOxygenView.getBloodOxygenView());

                viewType = ViewType.single;
            }
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem2)){
            if (viewType != ViewType.all){
                rootView.removeAllViews();
                if (bloodOxygenChartView == null){
                    drawLineChart();
                }

                rootView.startAnimation(rightInAnimation);
                rootView.addView(bloodOxygenChartView);

                viewType = ViewType.all;
            }
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem3)){
            FilterView filterView = new FilterView(this);
            filterView.showFilter(findViewById(R.id.universalMoudleRootView));
        }
    }
}

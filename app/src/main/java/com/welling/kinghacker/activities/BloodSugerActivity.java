package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.welling.kinghacker.customView.BloodSugerView;
import com.welling.kinghacker.customView.ChartView;
import com.welling.kinghacker.customView.FilterView;
import com.welling.kinghacker.customView.OverFlowView;
import com.welling.kinghacker.tools.FontTool;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/10/2016.
 **/
public class BloodSugerActivity extends MTActivity {

    private FrameLayout rootView;
    private enum ViewType{single,multiple,all}
    private ViewType viewType;
    private BloodSugerView singleBloodSugerView;
    private ChartView multipleView;
    private Animation rightInAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_moudle_layout);
        init();
    }

    private void init() {
        setParentView(findViewById(R.id.universalMoudleRootView));
        singleBloodSugerView = new BloodSugerView(this);
        rootView = (FrameLayout)findViewById(R.id.universalUpView);
        rootView.addView(singleBloodSugerView.getBloodSugerView());

        viewType = ViewType.single;
        singleBloodSugerView.setBloodSugerValue(5.2f);
        singleBloodSugerView.startAnimation();

        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        Button synInfoButton = (Button)findViewById(R.id.synButton);
        synInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));
        Button medicineButton = (Button)findViewById(R.id.medicineQueryButton);
        medicineButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));
        Button doctorInfoButton = (Button)findViewById(R.id.doctorButton);
        doctorInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));

    }
    @Override
    protected void onPostCreate(Bundle saveBundle){
        super.onPostCreate(saveBundle);
        initActionBar();
    }
//    画折线图
    private void drawLineChart(){
        FontTool fontTool = new FontTool(this);
        multipleView = new ChartView(this);

        int viewHeight = fontTool.getViewHeight(rootView);
        float originPointX = getResources().getDimension(R.dimen.originalX),
                originPointY = viewHeight - getResources().getDimension(R.dimen.originalY);

        multipleView.setOriginPoint(originPointX, originPointY);
        multipleView.setYLength((int)(originPointY - getResources().getDimension(R.dimen.originalY)/2));
        List<String> xaxis = new ArrayList<>();
        int count = 50;
        for (int i= 0;i<count;i++){
            xaxis.add("3/"+i + " 13:00");
        }
        multipleView.setXaxis(xaxis);
        List<Float> yaxis = new ArrayList<>();
        for (int i= 0;i<count;i++){
            Float num = new Float(Math.random() * 30);
            yaxis.add(num);
        }
        multipleView.setYaxis(yaxis);
        multipleView.setDescTextY("血糖值");
        multipleView.setDescTextX("时间");
    }

    private void initActionBar(){
        setIsBackEnable(true);
        setActionBarTitle(getString(R.string.blood_sugar));
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
                singleBloodSugerView.startAnimation();
                rootView.addView(singleBloodSugerView.getBloodSugerView());

                viewType = ViewType.single;
            }
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem2)){
            if (viewType != ViewType.all){
                rootView.removeAllViews();
                if (multipleView == null){
                    drawLineChart();
                }

                rootView.startAnimation(rightInAnimation);
                rootView.addView(multipleView.getRootView());

                viewType = ViewType.all;
            }
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem3)){
                FilterView filterView = new FilterView(this);
                filterView.showFilter(findViewById(R.id.universalMoudleRootView));
        }
    }
}

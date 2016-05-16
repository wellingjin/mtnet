package com.welling.kinghacker.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.welling.kinghacker.bean.SugerBean;
import com.welling.kinghacker.customView.BloodSugerView;
import com.welling.kinghacker.customView.ChartView;
import com.welling.kinghacker.customView.FilterView;
import com.welling.kinghacker.customView.OverFlowView;
import com.welling.kinghacker.customView.OxygenMTDialog;
import com.welling.kinghacker.tools.FontTool;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/10/2016.
 * update by 13wlli on 4/30/2016
 **/
public class BloodSugerActivity extends MTActivity {

    private FrameLayout rootView;
    private enum ViewType{single,multiple,all}
    private ViewType viewType;
    private static BloodSugerView singleBloodSugerView;
    private ChartView multipleView;
    private Animation rightInAnimation;
    OxygenMTDialog oxygenMTDialog;
    boolean isFinish = false;
    public SugerBean sugerBean = null;
    public static float currentSugerValue = 0;
    public static Handler myHandler=new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    BloodSugerActivity.singleBloodSugerView.setBloodSugerValue(currentSugerValue);
                    BloodSugerActivity.singleBloodSugerView.startAnimation();

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_moudle_layout1);
        init();
    }

    private void init() {
        //获取最近一次测量记录的血糖值赋值给currentOxygenValue
        SugerBean sugerbean = new SugerBean(this);
        currentSugerValue = sugerbean.getRecentlyOneData();

        setParentView(findViewById(R.id.universalMoudleRootView));
        singleBloodSugerView = new BloodSugerView(this);
        rootView = (FrameLayout)findViewById(R.id.universalUpView);
        rootView.addView(singleBloodSugerView.getBloodSugerView());

        viewType = ViewType.single;
        singleBloodSugerView.setBloodSugerValue(currentSugerValue);
//        singleBloodSugerView.setBloodSugerDate("0年0月0日");
        singleBloodSugerView.startAnimation();

        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        Button synInfoButton = (Button)findViewById(R.id.synButton);
        synInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));
        Button sendInfoButton = (Button)findViewById(R.id.sendButton);
        sendInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));
        Button medicineButton = (Button)findViewById(R.id.medicineQueryButton);
        medicineButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));
        Button doctorInfoButton = (Button)findViewById(R.id.doctorButton);
        doctorInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));

        synInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               gotoActivity(IGateActivity.class);
//               SugerBean sugerBean =new SugerBean(BloodSugerActivity.this);
//                Log.i("123",sugerBean.getRecentlyOneData()+"");
            }
        });

        sendInfoButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //在此跳出手动输入的弹窗
                showMTAlertDialog();

//                SugerBean sugerBean =new SugerBean(BloodSugerActivity.this,9.5f);
//                sugerBean.createTable();
//                sugerBean.insert();
            }
        });
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
        for (int i= 1;i<count;i++){
            xaxis.add("3/"+i + " 13:00");
        }
        multipleView.setXaxis(xaxis);
        List<Float> yaxis = new ArrayList<>();
        for (int i= 1;i<count;i++){
            Float num = new Float(Math.random() * 30);
            float num1 = num*10;
            int num2 =(int)num1;
            double num3=num2*0.1;
            float num4 = (float)num3;
//            float num4 = Math.round((num*100)/100);
            yaxis.add(num4);
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

    //设置手动输入弹窗
    void showMTAlertDialog(){
        if (oxygenMTDialog == null){
            oxygenMTDialog = new OxygenMTDialog(this);
            oxygenMTDialog.setdialogText();
            oxygenMTDialog.setOnButtonClickListener(new OxygenMTDialog.OnButtonClickListener() {
                @Override
                public void onButtonClick(int which) {
                    oxygenMTDialog.dismiss();
                    switch (which){
                        case 0://取消
                            oxygenMTDialog = null;
                            break;
                        case 1://确定
                            if(oxygenMTDialog.getText()== null || oxygenMTDialog.getText()=="") {
                                AlertDialog.Builder builder  = new AlertDialog.Builder(BloodSugerActivity.this);
                                builder.setTitle("提示" ) ;
                                builder.setMessage("你还没有输入" ) ;
                                builder.setPositiveButton("确定" ,  null );
                                builder.show();
                            }
                            else {
                                float data = Float.parseFloat(oxygenMTDialog.getText());
                                //将测量结果保存
                                sugerBean = new SugerBean(BloodSugerActivity.this,data);
                                //创建表  当然有分析 如果表存在就不创建
                                sugerBean.createTable();
                                //将信息插入
                                sugerBean.insert();
                                currentSugerValue = data;
                                myHandler.sendEmptyMessage(1);
                            }
                            oxygenMTDialog.dismiss();
                            oxygenMTDialog = null;
                            break;
                    }
                }
            });
        }
    }
}

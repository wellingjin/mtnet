package com.welling.kinghacker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.creative.filemanage.ECGFile;
import com.welling.kinghacker.bean.BloodPressureBean;
import com.welling.kinghacker.bean.SugerBean;
import com.welling.kinghacker.customView.BloodOxygenView;
import com.welling.kinghacker.customView.BloodPressureView;
import com.welling.kinghacker.customView.BloodSugerView;
import com.welling.kinghacker.customView.ElectrocarDiogram;
import com.welling.kinghacker.customView.MTToast;
import com.welling.kinghacker.customView.PagerView;
import com.welling.kinghacker.customView.RippleView;
import com.welling.kinghacker.mtdata.ECGFilesUtils;
import com.welling.kinghacker.bean.OxygenDataRecord;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 2/28/2016.
 **/
public class HomeActivity extends MTActivity {
    private ElectrocarDiogram diogram;
    private TextView ELCTime,ELCattr,ELCDate;

    private String stime,sattr,sdate;
    private String time,attr,date;
    //全局变量定义
    enum cuteItem {ED,BS,BP,BO}//分别表示，心电，血糖，血压，血氧
    int screenWidth;
    PagerView pagerView;
    String [] titleText = new String[5];
    int[] color = new int[5];
    private BloodSugerView bloodSugerView;
    public  Float currentSugerValue = 0f;
    float bloodSugerValue = 8f;
    private BloodPressureView bloodPressureView;
    private boolean isExit = false;
    public BloodOxygenView bloodOxygenView;
    public  int currentOxygenValue = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        初始化
        init();
//        正文布局
        setContentView(R.layout.home_layout);
        setParentView(findViewById(R.id.homeRootView));
        initBloodOxygen();
        initBloodPressure();
        initBloodSuger();
        initElectocarDiagram();
        setPagerView();
    }


    @Override
    protected void selectItem(String text){
        super.selectItem(text);
        if (text.equals(getString(R.string.about))){
            gotoActivity(AboutActivity.class);
        }else if (text.equals(getString(R.string.setting))){
            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);

        }


    }

    private void init(){
        screenWidth = (int)SystemTool.getSystem(this).getScreenWidth();

        titleText[cuteItem.ED.ordinal()] = PublicRes.getInstance().electrocarDiogram;
        titleText[cuteItem.BO.ordinal()] = PublicRes.getInstance().bloodOxygen;
        titleText[cuteItem.BP.ordinal()] = PublicRes.getInstance().bloodPresure;
        titleText[cuteItem.BS.ordinal()] = PublicRes.getInstance().bloodSuger;

        color[cuteItem.ED.ordinal()] = SystemTool.getSystem(this).getXMLColor(R.color.electrocarDiogramBGColor);
        color[cuteItem.BO.ordinal()] = SystemTool.getSystem(this).getXMLColor(R.color.bloodOxygenBgColor);
        color[cuteItem.BP.ordinal()] = SystemTool.getSystem(this).getXMLColor(R.color.bloodPressionBgColor);
        color[cuteItem.BS.ordinal()] = SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor);

        setActionBarTitle(titleText[0]);

        setCustonLsftButton(SystemTool.getSystem(this).getXMLDrawable(R.mipmap.gentleman_low));

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
//        bloodSugerView = new BloodSugerView(this);
//        设置血糖值和容器高度
//        bloodSugerView.setBloodSugerValue(bloodSugerValue);
        float high = 16f;
        float low = 3.9f;
        SugerBean suger = new SugerBean(this);
        String value =  suger.getRecentlyOneData();
        if(value!=null) {
            String[] dateTime = new String[3];
            dateTime = value.split(",");
            currentSugerValue = Float.parseFloat(dateTime[0]);
            sdate = dateTime[1];
            stime = dateTime[2];
            sattr = "正常";
            if (currentSugerValue >= high) {
                sattr = "高血糖";
            }
            else if (currentSugerValue <= low) {
                sattr = "低血糖";
            }
        }
        if (bloodSugerView == null)
            bloodSugerView = new BloodSugerView(this);
        bloodSugerView.setBloodSugerValue(currentSugerValue);
        if(value!=null) {
            bloodSugerView.setBloodSugerDate(sdate);
            bloodSugerView.setBloodSugerTime(stime);
            bloodSugerView.setBloodSugerAttr(sattr);
        }
        RippleView bloodOxygenButton = (RippleView)findViewById(R.id.bloodSugerButton);
        bloodOxygenButton.setRippleDuration(bloodOxygenButton.getRippleDuration()/2);
        bloodOxygenButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(HomeActivity.this, BloodSugerActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initBloodPressure(){
        if(bloodPressureView==null)bloodPressureView = new BloodPressureView(this);
        BloodPressureBean bpbean=new BloodPressureBean(this);
        bpbean.setLatestRecordFromlocal();
        bloodPressureView.setValues(bpbean.getHighblood(), bpbean.getLowblood()
                , bpbean.getHeartrate(),bpbean.getUpdatetime(),BloodPressureBean.blood_status[BloodPressureBean.statu]);
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
        //获取最近一次测量记录的血氧值赋值给currentOxygenValue
        OxygenDataRecord oxygenDataRecord = new OxygenDataRecord(this);
        String value = oxygenDataRecord.getRecentlyOneData();
        if(value!=null) {
            String[] dateTime = new String[3];
            dateTime = value.split(",");
            currentOxygenValue = Integer.parseInt(dateTime[0]);
            date = dateTime[1];
            time = dateTime[2];
            attr = "异常";
            if (currentOxygenValue >= 90) {
                attr = "正常";
            }
        }
        if (bloodOxygenView == null)
            bloodOxygenView = new BloodOxygenView(this);
        bloodOxygenView.setBloodOxygenValue(currentOxygenValue);
        if(value!=null) {
            bloodOxygenView.setBloodOxygenDate(date);
            bloodOxygenView.setBloodOxygenTime(time);
            bloodOxygenView.setBloodOxygenAttr(attr);
        }
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

    private void initElectocarDiagram(){
        RippleView heardButton = (RippleView)findViewById(R.id.electrocarDiogramButton);
        heardButton.setRippleDuration(heardButton.getRippleDuration()/3);
        heardButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                gotoActivity(ElectorDragramActivity.class);
            }
        });
    }

    void showELC(ECGFile file){
        if (file != null) {
            diogram.stopDram();
            setDateTime(file);
            diogram.setPoint(file.ecgData);
            diogram.startDram();
        }
    }
    //设置对应的时间，属性
    void setDateTime(ECGFile file){
        String []dateTime = file.time.split(" ");
        ELCDate.setText(dateTime[0]);
        ELCTime.setText(dateTime[1]);
        String attr = "异常";
        if (file.nAnalysis == 0){
            attr = "正常";
        }
        ELCattr.setText(attr);
    }
    private void setPagerView(){
        pagerView = (PagerView) findViewById(R.id.flipperView);

        View electrocarDiogram = SystemTool.getSystem(this).getView(R.layout.electrocar_diogram);
        diogram = (ElectrocarDiogram)electrocarDiogram.findViewById(R.id.heartDram);

        ELCDate = (TextView)electrocarDiogram.findViewById(R.id.elcDate);
        ELCTime = (TextView)electrocarDiogram.findViewById(R.id.elcTime);
        ELCattr = (TextView)electrocarDiogram.findViewById(R.id.elcAttr);
        ECGFile file = ECGFilesUtils.getLastECGFile();
        showELC(file);

        View bloodSuger = bloodSugerView.getBloodSugerView();


        final View bloodPressure = bloodPressureView.getBloodPressureView();



        pagerView.addItem(electrocarDiogram);
        pagerView.addItem(bloodSuger);
        pagerView.addItem(bloodPressure);
        pagerView.addItem(bloodOxygenView.getBloodOxygenView());
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
        gotoActivity(BloodSugerActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
        initBloodOxygen();
        initBloodPressure();
        initBloodSuger();
    }
}

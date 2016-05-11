package com.welling.kinghacker.activities;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import com.creative.base.BLUReader;
import com.creative.base.BLUSender;
import com.creative.base.BaseDate;
import com.creative.FingerOximeter.IFingerOximeterCallBack;
import com.welling.kinghacker.customView.BloodOxygenChartView;
import com.welling.kinghacker.customView.BloodOxygenView;

import com.welling.kinghacker.customView.FilterView;
import com.welling.kinghacker.customView.MTDialog;
import com.welling.kinghacker.customView.MTToast;
import com.welling.kinghacker.customView.OverFlowView;

import com.welling.kinghacker.customView.OxygenMTDialog;
import com.welling.kinghacker.bean.OxygenDataRecord;
import com.welling.kinghacker.tools.BlueToothManager;
import com.welling.kinghacker.tools.BluetoothConnectUtils;
import com.welling.kinghacker.tools.FingerOximeterOxygen;
import com.welling.kinghacker.tools.PublicRes;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class BloodOxygenActivity extends MTActivity {

    private static FrameLayout rootView;
    private enum ViewType{single,multiple,all}
    private ViewType viewType;
    public static BloodOxygenView singleBloodOxygenView;
    private Animation rightInAnimation;
    public static BloodOxygenChartView bloodOxygenChartView;
    BlueToothManager blueToothManager;
    MTToast mtToast;
    List<String> items;
    MTDialog mtDialog;
    OxygenMTDialog oxygenMTDialog;
    Intent intent;
    BluetoothConnectUtils connectUtils;
    FingerOximeterOxygen fo;
    public static int currentOxygenValue = 0;
    boolean isFinish = false;
    public OxygenDataRecord oxygenDataRecord = null;
    public static Handler myHandler=new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                   // rootView.removeView(singleBloodOxygenView.getBloodOxygenView());
                    BloodOxygenActivity.singleBloodOxygenView.setBloodOxygenValue(currentOxygenValue);
                   // rootView.addView(singleBloodOxygenView.getBloodOxygenView());
                    BloodOxygenActivity.singleBloodOxygenView.startAnimation();
                    //更新最近7次数据
                    for(int i= bloodOxygenChartView.bloodOxyData.length-1;i>=1;i--){
                        bloodOxygenChartView.bloodOxyData[i] = bloodOxygenChartView.bloodOxyData[i-1];
                    }
                    //最新的值
                    bloodOxygenChartView.bloodOxyData[0] = currentOxygenValue;
                    bloodOxygenChartView.invalidate();
                    rootView.invalidate();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_moudle_layout);
        init();
    }
    private void init() {
        //获取最近一次测量记录的血氧值赋值给currentOxygenValue
        OxygenDataRecord oxygenDataRecord = new OxygenDataRecord(this);
        currentOxygenValue = oxygenDataRecord.getRecentlyOneData();

        setParentView(findViewById(R.id.universalMoudleRootView));
        singleBloodOxygenView = new BloodOxygenView(this);
        rootView = (FrameLayout)findViewById(R.id.universalUpView);
        rootView.addView(singleBloodOxygenView.getBloodOxygenView());
        viewType = ViewType.single;
        singleBloodOxygenView.setBloodOxygenValue(currentOxygenValue);
        singleBloodOxygenView.startAnimation();
        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        Button synInfoButton = (Button)findViewById(R.id.synButton);
        synInfoButton.setBackgroundColor(getResources().getColor(R.color.bloodOxygenBgColor));
        Button medicineButton = (Button)findViewById(R.id.medicineQueryButton);
        medicineButton.setBackgroundColor(getResources().getColor(R.color.bloodOxygenBgColor));
        Button doctorInfoButton = (Button)findViewById(R.id.doctorButton);
        doctorInfoButton.setBackgroundColor(getResources().getColor(R.color.bloodOxygenBgColor));
        synInfoButton.setText("同步血氧仪数据");
        synInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                discoverBlueTooth();

            }
        });
        medicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(MedicionActicity.class);
            }
        });
        doctorInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoActivity(MainDoctorActivity.class);
            }
        });

        mtToast = new MTToast(this);
        items = new ArrayList<>();
        drawLineChart();
    }
    private void discoverBlueTooth(){
        isFinish = false;
        blueToothManager = new BlueToothManager(this);
        if (!blueToothManager.isHaveBlueTooth()){
            mtToast.makeText("本机没有找到蓝牙硬件或驱动！", MTToast.LONGTIME);
            mtToast.showAtView(rootView);
            return;
        }

        // 如果本地蓝牙没有开启，则开启
        if (!blueToothManager.isBlueToothEnable()) {
            // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
            // 那么将会收到RESULT_OK的结果，
            // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 1);
            // 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
            // mBluetoothAdapter.enable();
            // mBluetoothAdapter.disable();//关闭蓝牙
        }else {
            if (connectUtils!=null) {
                connectUtils.closeSocket();
                connectUtils = null;
                mtDialog = null;
            }
            startSearchBlueTooth();
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                startSearchBlueTooth();
            }
        }

    }
    private void startSearchBlueTooth(){
        //   bluetooth
        connectUtils = new BluetoothConnectUtils(this);
        showAlertDialog("正在查找...",false,true);
        connectUtils.setOnBluetoothConnectedListener(new BluetoothConnectUtils.OnBluetoothConnectedListener() {
            @Override
            public void setOnBluetoothConnected(BluetoothSocket bluetoothSocket) {
                try {
                    InputStream is = bluetoothSocket.getInputStream();
                    OutputStream os = bluetoothSocket.getOutputStream();
                    fo = new FingerOximeterOxygen(new BLUReader(is), new BLUSender(os), new FingerCallBack());
                    fo.Start();
                    fo.SetParamAction(true);
                    fo.SetWaveAction(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void bluetoothConnectState(int state) {
                switch (state){
                    case BluetoothConnectUtils.CONNECTING:
                        showAlertDialog("正在连接...",false,true);
                        break;
                    case BluetoothConnectUtils.CONNECT_FAILED:
                        showAlertDialog("连接失败...",true,true);
                        mtDialog.dismiss();
                        //在此跳出手动输入的弹窗
                        showMTAlertDialog();
                        break;
                    case BluetoothConnectUtils.CONNECTED:
                        showAlertDialog("连接成功",false,true);
                        //跳到展示的页面
                        mtDialog.dismiss();
                        intent= new Intent(BloodOxygenActivity.this, BloodOxygenUploadActivity.class);
                        startActivity(intent);
                        break;
                    case BluetoothConnectUtils.SEARCH_COMPLETE:
                        showAlertDialog("搜索结束...",true,true);
                        break;
                    case BluetoothConnectUtils.SEARCH_FAILED:
                        showAlertDialog("搜索失败...",true,true);
                        mtDialog.dismiss();
                        showMTAlertDialog();
                        break;

                }
            }
        });

    }
    private static class FingerCallBack implements IFingerOximeterCallBack
    {
        @Override
        public void OnConnectLose() {
            System.out.println("Break the connection with device.");
        }
        @Override
        public void OnGetDeviceVer(int nHWMajor, int nHWMinor,
                                   int nSWMajor,int nSWMinor) {
            //System.out.println("Have got device version information.");
        }
        @Override
        public void OnGetSpO2Param(int nSpO2, int nPR, float nPI,
                                   boolean nStatus, int nMode, float nPower) {
            //System.out.println("Have got SpO2 parameter data.");
            if(!nStatus){
                //探头脱落  测量结束
                System.out.println("Probe off.");
            }
            if(BloodOxygenUploadActivity.getProgressBar()<100) {
                BloodOxygenUploadActivity.nSpO2 = nSpO2;
                BloodOxygenUploadActivity.nPR = nPR;
                BloodOxygenUploadActivity.nPI = nPI;
                BloodOxygenUploadActivity.nStatus = nStatus;
                BloodOxygenUploadActivity.mHandler.sendEmptyMessage(1);
            }
            //System.out.println(" " + nSpO2 + " " + nPR + " " + nPI + " " + nStatus + " " + nMode + " " + nPower);

        }
        @Override
        public void OnGetSpO2Wave(List<BaseDate.Wave> wave) {
            if(BloodOxygenUploadActivity.getProgressBar()<100) {
                BloodOxygenUploadActivity.wave = wave.get(wave.size() - 1);
                BloodOxygenUploadActivity.mHandler.sendEmptyMessage(2);
            }
            //System.out.println("Have got SpO2 waveform data." + BloodOxygenUploadActivity.wave.toString());
        }
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
    void showAlertDialog(String str,boolean prgHide,boolean barHide){
        if (mtDialog == null){
            mtDialog = new MTDialog(this);
        }
        mtDialog.setStateText(str);
        mtDialog.setRecBarHiden(barHide);
        mtDialog.setProgressBarHiden(prgHide);
        mtDialog.setCancleButtonEnable(false,"取消");
        mtDialog.setComfireButtonEnable(false,"上传");
    }
    //设置手动输入弹窗
    void showMTAlertDialog(){
        if (oxygenMTDialog == null){
            oxygenMTDialog = new OxygenMTDialog(this);
            oxygenMTDialog.setOnButtonClickListener(new OxygenMTDialog.OnButtonClickListener() {
                @Override
                public void onButtonClick(int which) {
                    oxygenMTDialog.dismiss();
                    switch (which){
                        case 0://取消
                            oxygenMTDialog = null;
                            break;
                        case 1://确定
                            int data = Integer.parseInt(oxygenMTDialog.getText());
                            if(data<=100&&data>85) {//数据合法
                                //将测量结果保存
                                oxygenDataRecord = new OxygenDataRecord(BloodOxygenActivity.this,data,60);
                                //创建表  当然有分析 如果表存在就不创建
                                oxygenDataRecord.createTable();
                                //将信息插入
                                oxygenDataRecord.insert();
                                currentOxygenValue = data;
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

package com.welling.kinghacker.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.creative.base.BLUReader;
import com.creative.base.BLUSender;
import com.creative.base.BaseDate;
import com.creative.ecg.ECG;
import com.creative.ecg.IECGCallBack;
import com.creative.filemanage.ECGFile;
import com.creative.filemanage.FileOperation;
import com.welling.kinghacker.customView.ElectrocarDiogram;
import com.welling.kinghacker.customView.MTDialog;
import com.welling.kinghacker.customView.MTToast;
import com.welling.kinghacker.mtdata.ECGFilesUtils;
import com.welling.kinghacker.tools.BlueToothManager;
import com.welling.kinghacker.tools.BluetoothConnectUtils;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by KingHacker on 3/23/2016.
 **/
public class ElectorDragramActivity extends MTActivity {

    private static final String TAG = "ELC";
    BlueToothManager blueToothManager;
    View rootView;
    MTToast mtToast;
    List<String> items;
    int sumSize = 0;
    MTDialog mtDialog;

    BluetoothConnectUtils connectUtils;
    ECG ecg;
    final int
            MTOUTOFTIME = 0x012,//获取超时
            MTFINISH = 0x011,//完成
            MTRECFILE = 0x010,//接收
            MTRESULT = 0x009,//显示结果
            MTTOAST = 0x008,//显示toast
            MTHR = 0x007,  //实现HR
            MTTRANS = 0x006;//实时传输

    Handler handler;
    boolean isFinish = false;

    @Override
    protected void onCreate(Bundle saveBundle){
        super.onCreate(saveBundle);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MTTRANS:
                        break;
                    case MTHR:
                        break;
                    case MTTOAST:
                        Toast.makeText(ElectorDragramActivity.this, msg.obj + "", Toast.LENGTH_SHORT).show();
                        break;
                    case MTRESULT:
                        break;
                    case MTRECFILE:
                        if (isFinish) break;
                        int size = msg.getData().getInt("fileSize",0);
                        sumSize += size;
                        showAlertDialog("正在接收数据...", false, false);
                        if (sumSize >= mtDialog.getMAX()){
                            mtDialog.setMax(sumSize + size);
                        }
                        mtDialog.setProgress(sumSize);
                        break;
                    case MTFINISH:
                        showAlertDialog("数据接收完成", true, false);
                        mtDialog.setMax(sumSize);
                        mtDialog.setProgress(sumSize);
                        break;
                    case MTOUTOFTIME:
                        showAlertDialog("文件接收超时", true, true);
                        break;

                }
            }

        };
        init();
    }
    private void init(){
//        actionbar
        setIsBackEnable(true);
        setActionBarTitle(getResources().getString(R.string.electrocar_diogram));

//
        setContentView(R.layout.universal_moudle_layout);
        FrameLayout upView = (FrameLayout)findViewById(R.id.universalUpView);
        View electrocarDiogram = SystemTool.getSystem(this).getView(R.layout.electrocar_diogram);
        upView.addView(electrocarDiogram);
        ElectrocarDiogram diogram = (ElectrocarDiogram)electrocarDiogram.findViewById(R.id.heartDram);
        ECGFile file = ECGFilesUtils.getLastECGFile();
        if (file != null) {
            diogram.setPoint(file.ecgData);
            diogram.startDram();
        }
        rootView = upView.getRootView();
        setParentView(rootView);
//        get the buttons
        Button synInfoButton = (Button)findViewById(R.id.synButton);
        synInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.electrocarDiogramBGColor));
        Button medicineButton = (Button)findViewById(R.id.medicineQueryButton);
        medicineButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.electrocarDiogramBGColor));
        Button doctorInfoButton = (Button)findViewById(R.id.doctorButton);
        doctorInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.electrocarDiogramBGColor));

        synInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                discoverBlueTooth();
            }
        });
        medicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        doctorInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoActivity(HeartAllActivity.class);
            }
        });

        mtToast = new MTToast(this);
        items = new ArrayList<>();
    }
    @Override
    protected void setOverFlowView(){
        super.setOverFlowView();
        List<OverFlowItem> items = new ArrayList<>();
        items.add(new OverFlowItem(android.R.drawable.stat_notify_sync, PublicRes.Syn_Clound));
        setOverFlowViewItems(items);
    }
    @Override
    protected void selectItem(String text){
        switch (text){
            case PublicRes.Syn_Clound:
                break;
        }
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
                    final InputStream iss = is;
                    ecg = new ECG(new BLUReader(is), new BLUSender(os), new HeartAllECGCallBack());
                    ecg.Start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isRun = true;
                            boolean isStart = false;
                            int count = 0;
                            while (isRun) {
                                byte[] buff = new byte[128];
                                int state;
                                try {
                                    if (iss == null){
                                        isRun = false;
                                        break;
                                    }
                                    if (isStart) {
                                        int countA = 0, len;
                                        do {
                                            len = iss.available();
                                            countA++;
                                            if (countA > 80560) {
                                                isRun = false;
                                                break;
                                            }
                                        } while (len == 0);
                                    }
                                    if (!isRun){
                                        break;
                                    }
                                    state = iss.read(buff);
                                    if (state == -1){
                                        isRun = false;
                                    }else{
                                        Message msg = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("fileSize", state);
                                        msg.setData(bundle);
                                        msg.what = MTRECFILE;
                                        handler.sendMessage(msg);
                                        isStart = true;
                                    }
                                } catch (Exception e) {
                                    count++;
                                    if (count >10) {
                                        isRun =false;
                                    }
                                }

                            }
                            handler.sendEmptyMessage(MTFINISH);
                        }
                    }).start();

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
                        break;
                    case BluetoothConnectUtils.CONNECTED:
                        showAlertDialog("连接成功，等待接收文件...",false,true);

                        break;
                    case BluetoothConnectUtils.SEARCH_COMPLETE:
                        showAlertDialog("搜索结束...",true,true);
                        break;
                    case BluetoothConnectUtils.SEARCH_FAILED:
                        showAlertDialog("搜索失败...",true,true);
                        break;

                }
            }
        });

    }

    void showAlertDialog(String str,boolean prgHide,boolean barHiden){
        if (mtDialog == null){
            mtDialog = new MTDialog(this);
        }
        mtDialog.setStateText(str);
        mtDialog.setRecBarHiden(barHiden);
        mtDialog.setProgressBarHiden(prgHide);

    }

    class HeartAllECGCallBack implements IECGCallBack {

        @Override
        public void OnGetDeviceVer(int i, int i1, int i2, int i3, int i4, int i5) {
            Log.i(TAG, "OnGetDeviceVer");
        }

        @Override
        public void OnGetRequest(String sDeviceId, String sProductId, int mSmoothingMode, int nTransMode) {
            Log.i(TAG,"接受请求");
            Message msg = handler.obtainMessage(MTTRANS);
            switch (nTransMode) {
                case 0:
                    //    连续测量
                    msg.obj = "连续测量";
                    break;
                case 1:
                    //    文件传输
                    msg.obj = "文件传输";
                    break;
                case 2:
                    //    快速测量
                    msg.obj = "快速测量";
                    break;
            }
            handler.sendMessage(msg);
        }

        @Override
        public void OnGetFileTransmit(int bFinish, Vector<Integer> srcByte) {
            Log.i(TAG, "文件接收..."+bFinish);
/*            if (bFinish == 3){
                handler.sendEmptyMessage(MTFINISH);
            }*/
            isFinish = false;
            //    当bfinish值为1代表传输完成
            if (bFinish == 1 ){
                Log.i(TAG,"完成");
                isFinish = true;
            }
            try {
                if(srcByte.size()>0) {
                    Log.i(TAG, " " + bFinish);

                    ECGFile ecgFile = FileOperation.AnalyseSCPFile(srcByte);
                    //  打包ecg文件到本地
                    ECGFilesUtils.packECGFile(ecgFile);
//                    setHR(ecgFile.nAverageHR);
//                    setAnalysisResult(ecgFile.nAnalysis-1);

                }else {
                    Log.i(TAG, "空"+bFinish);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void OnGetRealTimePrepare(boolean bLeadOff, BaseDate.ECGData ecgData, int nGain) {
            Log.i(TAG, "实时测量准备");

        }

        @Override
        public void OnGetRealTimeMeasure(boolean bLeadOff, BaseDate.ECGData ecgData, int nTransMode, int nHR, int nPower, int nGain) {
            Log.i(TAG,"实时测量数据接收");

            if (nTransMode == 0){
                setHR(nHR);
            }
        }

        @Override
        public void OnGetRealTimeResult(String sTime, int nTransMode, int nResult, int i2) {
            Log.i(TAG, "实时测量结束");

            setAnalysisResult(nResult);
            makeToast("实时测量结束");
        }

        @Override
        public void OnGetPower(int nPower) {
            Log.i(TAG, "OnGetPower");
        }

        @Override
        public void OnReceiveTimeOut() {
            Log.i(TAG, "文件接收超时");
            handler.sendEmptyMessage(MTOUTOFTIME);
        }

        @Override
        public void OnConnectLose() {
            Log.i(TAG, "断开连接");
            makeToast("断开连接");
            showAlertDialog("断开连接", true, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectUtils.closeSocket();
    }

    /**
     * 显示toast消息
     * @param text toast显示的内容
     */
    public void makeToast(String text){
        Message msg = handler.obtainMessage(MTTOAST);
        msg.obj = text;
        handler.sendMessage(msg);
    }

    /**
     * 显示测量结果
     * @param index 选择的结果下标
     */
    public void setAnalysisResult(int index){
        Message msg = handler.obtainMessage(MTRESULT);
        handler.sendMessage(msg);
    }

    /**
     * 显示HR
     * @param hr 心率
     */
    public void setHR(int hr){
        Message msg = handler.obtainMessage(MTHR);
        msg.arg1 = hr;
        handler.sendMessage(msg);
    }

}

package com.welling.kinghacker.activities;

import android.app.AlertDialog;
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
import android.widget.ListAdapter;
import android.widget.Toast;

import com.creative.base.BLUReader;
import com.creative.base.BLUSender;
import com.creative.base.BaseDate;
import com.creative.ecg.ECG;
import com.creative.ecg.IECGCallBack;
import com.creative.filemanage.ECGFile;
import com.creative.filemanage.FileOperation;
import com.welling.kinghacker.customView.ElectrocarDiogram;
import com.welling.kinghacker.customView.MTToast;
import com.welling.kinghacker.mtdata.ECGFilesUtils;
import com.welling.kinghacker.tools.BlueToothManager;
import com.welling.kinghacker.tools.BluetoothConnectUtils;
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
    AlertDialog.Builder builder;
    ListAdapter listAdapter;
    BluetoothConnectUtils connectUtils;
    ECG ecg;
    final int
            MTRESULT = 0x009,//显示结果
            MTTOAST = 0x008,//显示toast
            MTHR = 0x007,  //实现HR
            MTTRANS = 0x006;//实时传输

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MTTRANS:
//                    tv_transMode.setText(msg.obj.toString());
                    break;
                case MTHR:
//                    tv_HR.setText(msg.arg1 + "");
                    break;
                case MTTOAST:
                    Toast.makeText(ElectorDragramActivity.this, msg.obj + "", Toast.LENGTH_SHORT).show();
                    break;
                case MTRESULT:
//                    tv_result.setText(msg.obj+"");
                    break;

            }
        }

    };

    @Override
    protected void onCreate(Bundle saveBundle){
        super.onCreate(saveBundle);
        init();
    }
    private void init(){
//        actionbar
        setIsBackEnable(true);
        setActionBarTitle(getResources().getString(R.string.electrocar_diogram));
        setRightButtonEnable(false);


//
        setContentView(R.layout.universal_moudle_layout);
        FrameLayout upView = (FrameLayout)findViewById(R.id.universalUpView);
        View electrocarDiogram = SystemTool.getSystem(this).getView(R.layout.electrocar_diogram);
        upView.addView(electrocarDiogram);
        ElectrocarDiogram diogram = (ElectrocarDiogram)electrocarDiogram.findViewById(R.id.heartDram);
        ECGFile file = ECGFilesUtils.getLastECGFile();
        diogram.setPoint(file.ecgData);
        diogram.startDram();

        rootView = upView.getRootView();
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

    private void discoverBlueTooth(){
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
        connectUtils.setOnBluetoothConnectedListener(new BluetoothConnectUtils.OnBluetoothConnectedListener() {
            @Override
            public void setOnBluetoothConnected(BluetoothSocket bluetoothSocket) {
                try {
                    InputStream is = bluetoothSocket.getInputStream();
                    OutputStream os = bluetoothSocket.getOutputStream();

                    ecg = new ECG(new BLUReader(is), new BLUSender(os), new HeartAllECGCallBack());
                    ecg.Start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void bluetoothConnectState(int state) {
                switch (state){
                    case BluetoothConnectUtils.CONNECTING:
                        showAlertDialog("正在连接...");
                        break;
                    case BluetoothConnectUtils.CONNECT_FAILED:
                        showAlertDialog("连接失败...");
                        break;
                    case BluetoothConnectUtils.CONNECTED:
                        showAlertDialog("连接成功，等待接收文件...");
                        break;
                    case BluetoothConnectUtils.SEARCH_COMPLETE:
                        showAlertDialog("搜索结束...");
                        break;
                    case BluetoothConnectUtils.SEARCH_FAILED:
                        showAlertDialog("搜索失败...");
                        break;

                }
            }
        });
        /*if (blueToothManager.startSearch()) {
            List<BlueToothStruct> bondList = blueToothManager.getBoundList();

            if (bondList.size() > 0) {
                for (BlueToothStruct struct : bondList) {
                    items.add(struct.blueToothName + "/" + struct.address);
                }
            }
            showAlertDialog();
            blueToothManager.setBlueToothDiscoverStaic(new BlueToothDiscover.SearchBlueToothState() {

                @Override
                public void actionFound(BlueToothStruct device) {
                    items.add(device.blueToothName + "/" + device.address);
                    listAdapter.notify();
//                    builder.notify();
                }

                @Override
                public void searchFinish() {

                }
            });
        }*/
    }

    void showAlertDialog(String str){
        if (builder == null) {
            builder = new AlertDialog.Builder(this);
        }
        builder.setIcon(android.R.drawable.ic_popup_sync);
        builder.setTitle(str);

        /*listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,items);

        builder.setAdapter(listAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String macAddress = items.get(which).split("/")[1];
                blueToothManager.connet(macAddress);
            }
        });*/
        builder.show();
//        builder.setTitle("搜索结束");
    }

    class HeartAllECGCallBack implements IECGCallBack {

        @Override
        public void OnGetDeviceVer(int i, int i1, int i2, int i3, int i4, int i5) {
            Log.i(TAG, "OnGetDeviceVer");
        }

        @Override
        public void OnGetRequest(String sDeviceId, String sProductId, int mSmoothingMode, int nTransMode) {
            Log.i(TAG,"接受请求");
            Message msg = handler.obtainMessage(0x006);
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
            Log.i("tag", "文件接收...");
            List<Integer> data = new ArrayList<>();
            //    当bfinish值为1代表传输完成
            if (bFinish == 1 ){
//                makeToast("done");
                Log.i("tag","完成时间");
            }
            try {
                if(srcByte.size()>0) {
                    ECGFile ecgFile = FileOperation.AnalyseSCPFile(srcByte);
                    List<Integer> ecgData = ecgFile.ecgData;
                    Log.i("tag", "收到数据:" + ecgData.size());

                    for (int w : ecgData) {
                        data.add((w-1950)/10);
                    }

                    setHR(ecgFile.nAverageHR);
                    setAnalysisResult(ecgFile.nAnalysis-1);
                    //  打包ecg文件到本地
                    ECGFilesUtils.packECGFile(ecgFile);
                }else{
//                    Toast.makeText(ElectorDragramActivity.this,"接收到的数据为空,接收失败",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void OnGetRealTimePrepare(boolean bLeadOff, BaseDate.ECGData ecgData, int nGain) {
            Log.i(TAG,"实时测量准备");

        }

        @Override
        public void OnGetRealTimeMeasure(boolean bLeadOff, BaseDate.ECGData ecgData, int nTransMode, int nHR, int nPower, int nGain) {
            Log.i(TAG,"实时测量数据接收");
            List<BaseDate.Wave> list = ecgData.data;
            List<Integer> data = new ArrayList<>();
            for (BaseDate.Wave w : list) {
                data.add(w.data-1950);
            }
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
            Log.i(TAG,"OnGetPower");
        }

        @Override
        public void OnReceiveTimeOut() {
            Log.i(TAG, "文件接收超时");
            makeToast("文件接收超时");
        }

        @Override
        public void OnConnectLose() {
            Log.i(TAG,"断开连接");
            makeToast("断开连接");
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
//        String[] results = getResources().getStringArray(R.array.ECG_results);
        msg.obj = "result: 整除";
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

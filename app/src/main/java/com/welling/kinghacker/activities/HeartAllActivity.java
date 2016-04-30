package com.welling.kinghacker.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.base.BLUReader;
import com.creative.base.BLUSender;
import com.creative.base.BaseDate;
import com.creative.ecg.ECG;
import com.creative.ecg.IECGCallBack;
import com.creative.filemanage.ECGFile;
import com.creative.filemanage.FileOperation;
import com.welling.kinghacker.mtdata.ECGFilesUtils;
import com.welling.kinghacker.tools.BluetoothConnectUtils;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by user on 2015/12/1.
 *
 */
public class HeartAllActivity extends AppCompatActivity{
    private static String TAG = HeartAllActivity.class.getName();

    TextView tv_ha_device,tv_HR,tv_transMode,tv_result;
    BluetoothConnectUtils utils;
    ECG ecg;
    private static final int POSITION=3;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x006:
                    tv_transMode.setText(msg.obj.toString());
                    break;
                case 0x007:
                    tv_HR.setText(msg.arg1 + "");
                    break;
                case 0x008:
                    Toast.makeText(HeartAllActivity.this, msg.obj + "", Toast.LENGTH_SHORT).show();
                    break;
                case 0x009:
                    tv_result.setText(msg.obj+"");
                    break;

            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt_test_layout);
        setupActionBar();

        tv_ha_device = (TextView) findViewById(R.id.text1);
        tv_HR = (TextView) findViewById(R.id.text2);
        tv_transMode = (TextView) findViewById(R.id.text3);
        tv_result = (TextView) findViewById(R.id.text4);


        utils = new BluetoothConnectUtils(this);
        utils.setOnBluetoothConnectedListener(new BluetoothConnectUtils.OnBluetoothConnectedListener() {
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

            }
        });

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    class HeartAllECGCallBack implements IECGCallBack{

        @Override
        public void OnGetDeviceVer(int i, int i1, int i2, int i3, int i4, int i5) {
            Log.i(TAG,"OnGetDeviceVer");
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
//                ha_sectionView.clearData();
//                ha_sectionView.setPulseView(ha_pulseView);
//                makeToast(getString(R.string.transfer_completed));
                Log.i("tag","完成时间");
            }
            try {
                if(srcByte!=null && srcByte.size()>0) {
                    ECGFile ecgFile = FileOperation.AnalyseSCPFile(srcByte);
                    List<Integer> ecgData = ecgFile.ecgData;
                    Log.i("tag", "收到数据:" + ecgData.toString());

                    for (int w : ecgData) {
                        data.add((w-1950)/10);
                    }

//                    ha_sectionView.postData(data);

                    setHR(ecgFile.nAverageHR);
                    Log.i("tag",ecgFile.nAverageHR+"");

                    Log.i("tag",ecgFile.toString()+"");
                    setAnalysisResult(ecgFile.nAnalysis-1);
                    //  打包ecg文件到本地
                    ECGFilesUtils.packECGFile(ecgFile);
                }else{
//                    Toast.makeText(HeartAllActivity.this,"接收到的数据为空,接收失败",Toast.LENGTH_SHORT).show();
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
//            ha_pulseView.setData(data);
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
        utils.closeSocket();
//        HomeActivity.adapter.disable();
    }

    /**
     * 显示toast消息
     * @param text toast显示的内容
     */
    public void makeToast(String text){
        Message msg = handler.obtainMessage(0x008);
        msg.obj = text;
        handler.sendMessage(msg);
    }

    /**
     * 显示测量结果
     * @param index 选择的结果下标
     */
    public void setAnalysisResult(int index){
        Message msg = handler.obtainMessage(0x009);
//        String[] results = getResources().getStringArray(R.array.ECG_results);
        msg.obj = "result: 整除";
        handler.sendMessage(msg);
    }

    /**
     * 显示HR
     * @param hr 心率
     */
    public void setHR(int hr){
        Message msg = handler.obtainMessage(0x007);
        msg.arg1 = hr;
        handler.sendMessage(msg);
    }
}

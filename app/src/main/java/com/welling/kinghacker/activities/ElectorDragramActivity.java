package com.welling.kinghacker.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creative.base.BLUReader;
import com.creative.base.BLUSender;
import com.creative.base.BaseDate;
import com.creative.ecg.ECG;
import com.creative.ecg.IECGCallBack;
import com.creative.filemanage.ECGFile;
import com.creative.filemanage.FileOperation;
import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.bean.ELCBean;
import com.welling.kinghacker.customView.ElectrocarDiogram;
import com.welling.kinghacker.customView.MTDialog;
import com.welling.kinghacker.customView.MTToast;
import com.welling.kinghacker.database.DatabaseManager;
import com.welling.kinghacker.mtdata.ECGFilesUtils;
import com.welling.kinghacker.mtdata.MTECGFile;
import com.welling.kinghacker.tools.BlueToothManager;
import com.welling.kinghacker.tools.BluetoothConnectUtils;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by KingHacker on 3/23/2016.
 **/
public class ElectorDragramActivity extends MTActivity {
    List<ELCBean> timeList = new ArrayList<>();
    private static final String TAG = "ELC";
    BlueToothManager blueToothManager;
    View rootView;
    MTToast mtToast;
    List<String> items;
    int sumSize = 0;
    MTDialog mtDialog;
    TextView ELCDate,ELCTime,ELCattr;

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
    ElectrocarDiogram diogram;
    @Override
    protected void onCreate(Bundle saveBundle){
        super.onCreate(saveBundle);
        Log.i("LIFE","oncreate");
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MTTRANS:
                        break;
                    case MTHR:
                        break;
                    case MTTOAST:
                        Toast.makeText(ElectorDragramActivity.this,msg.obj+"" , Toast.LENGTH_SHORT).show();
                        break;
                    case MTRESULT:
                        break;
                    case MTRECFILE:
                        if (isFinish) break;
                        int size = msg.getData().getInt("fileSize",0);
                        sumSize += size;
                        showAlertDialog("正在接收数据...", false, false,false,false);
                        if (sumSize >= mtDialog.getMAX()){
                            mtDialog.setMax(sumSize + size);
                        }
                        mtDialog.setProgress(sumSize);
                        break;
                    case MTFINISH:
                        showAlertDialog("数据接收完成", true, false,true,true);
                        mtDialog.setMax(sumSize);
                        mtDialog.setProgress(sumSize);
                        break;
                    case MTOUTOFTIME:
                       // showAlertDialog("文件接收超时", true, true,true,false);
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
        LinearLayout timeChoose = (LinearLayout)electrocarDiogram.findViewById(R.id.timeChoose);
        timeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeChoose();
            }
        });
        diogram = (ElectrocarDiogram)electrocarDiogram.findViewById(R.id.heartDram);

        ELCDate = (TextView)electrocarDiogram.findViewById(R.id.elcDate);
        ELCTime = (TextView)electrocarDiogram.findViewById(R.id.elcTime);
        ELCattr = (TextView)electrocarDiogram.findViewById(R.id.elcAttr);

        ECGFile file = ECGFilesUtils.getLastECGFile();
        showELC(file);

        rootView = upView.getRootView();
        setParentView(rootView);
//        get the buttons
        Button synInfoButton = (Button)findViewById(R.id.synButton);
        synInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.electrocarDiogramBGColor));
        synInfoButton.setText("同步心电仪数据");

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
        getAllTimeList();
    }

    void showELC(ECGFile file){
        if (file != null) {
            diogram.stopDram();
            setDateTime(file);
            diogram.setPoint(file.ecgData);
            diogram.startDram();
            Log.i(TAG,"startD");
        }else {
            Log.i(TAG, "file is null");
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
        Log.i(TAG, PublicRes.ELCresult[file.nAnalysis]);
        ELCattr.setText(attr);
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
                updateToCloud();

                break;
        }
    }


    private void discoverBlueTooth(){
        isFinish = false;
        if (blueToothManager == null) {
            blueToothManager = new BlueToothManager(this);
        }
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
        if (connectUtils != null) connectUtils = null;
        showAlertDialog("正在查找...", false, true, false, false);
        connectUtils = new BluetoothConnectUtils(this);

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
                            int count = 0;
                            while (isRun) {
                                byte[] buff = new byte[128];
                                int state;
                                try {
                                    if (iss == null) {
                                        isRun = false;
                                        break;
                                    }

                                    state = iss.read(buff);
                                    if (state == -1) {
                                        isRun = false;
                                    } else {
                                        Message msg = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("fileSize", state);
                                        msg.setData(bundle);
                                        msg.what = MTRECFILE;
                                        handler.sendMessage(msg);
                                    }
                                } catch (Exception e) {
                                    count++;
                                    if (count > 10) {
                                        isRun = false;
                                    }
                                }

                            }
                            // handler.sendEmptyMessage(MTFINISH);
                        }
                    }).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void bluetoothConnectState(int state) {
                switch (state) {
                    case BluetoothConnectUtils.CONNECTING:
                        showAlertDialog("正在连接...", false, true, false, false);
                        break;
                    case BluetoothConnectUtils.CONNECT_FAILED:
                        showAlertDialog("连接失败...", true, true, true, false);
                        break;
                    case BluetoothConnectUtils.CONNECTED:
                        showAlertDialog("连接成功，等待接收文件...", false, true, true, false);
                        break;
                    case BluetoothConnectUtils.SEARCH_COMPLETE:
                        showAlertDialog("搜索结束...", true, true, false, false);
                        break;
                    case BluetoothConnectUtils.SEARCH_FAILED:
                        showAlertDialog("搜索失败...", true, true, true, false);
                        break;

                }
            }
        });

    }

    void showAlertDialog(String str,boolean prgHide,boolean barHiden,boolean caEnable,boolean coEnable){
        if (mtDialog == null){
            mtDialog = new MTDialog(this);
            mtDialog.setOnButtonClickListener(new MTDialog.OnButtonClickListener() {
                @Override
                public void onButtonClick(int which) {
                    mtDialog.dismiss();
                    switch (which){

                        case 0://取消

                            break;
                        case 1://确定
                            updateToCloud();
                            break;
                    }
                    mtDialog = null;
                }
            });
        }
        mtDialog.setStateText(str);
        mtDialog.setRecBarHiden(barHiden);
        mtDialog.setProgressBarHiden(prgHide);
        mtDialog.setCancleButtonEnable(caEnable, "取消");
        mtDialog.setComfireButtonEnable(coEnable, "上传");
    }

    private void updateToCloud(){
        MTHttpManager manager = new MTHttpManager();

        final List<ELCBean> beans = new ArrayList<>();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                if (requestId < beans.size()) {
                    beans.get(requestId).update();
                }
            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });

        DatabaseManager dbManager = new DatabaseManager(this);
        JSONObject object = dbManager.getMultiRaw(ELCBean.TABLENAME, ELCBean.ISUPDATE,null, "0");

        try {
            int count = object.getInt("count");
            Log.i(TAG,"count:"+count);
            for(int i=0;i<count;i++){
                ELCBean bean = new ELCBean(this);
                bean.fileName = object.getJSONObject(""+i).getString(ELCBean.FILENAME);
                beans.add(bean);
                Log.i(TAG, "filename"+bean.fileName);
                String file = ECGFilesUtils.getFileByName(bean.fileName);
                manager.updateToCloud(this, file, MTHttpManager.ECG, i);
            }
        } catch (JSONException e) {
            Log.i(TAG,"updateExc");
            e.printStackTrace();
        }



    }
    void getLocalTimeList(){
        timeList.clear();
        DatabaseManager dbManager = new DatabaseManager(this);
        JSONObject object = dbManager.getMultiRaw(ELCBean.TABLENAME, ELCBean.CREATETIME, null, null);

        try {
            int count = object.getInt("count");
            for(int i=0;i<count;i++){
                ELCBean bean = new ELCBean(this);
                bean.createTime = object.getJSONObject(""+i).getString(ELCBean.CREATETIME);
                timeList.add(bean);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void getAllTimeList(){
        getLocalTimeList();
        MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                int state = 0;
                String error;
                timeList.clear();
                try {
                    state = JSONResponse.getInt(PublicRes.STATE);
                    error = JSONResponse.getString(PublicRes.EXCEPTION);
                    JSONArray  jsonArray = JSONResponse.getJSONArray("list");
                    for (int i=0;i< jsonArray.length();i++){
                        String timestamp = jsonArray.getString(i);
                        ELCBean bean = new ELCBean(ElectorDragramActivity.this);
                        bean.createTime = timestamp;
                        timeList.add(bean);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                if (state == 0){
                    makeToast("获取历史列表失败");
                }
            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });
        RequestParams params = new RequestParams();
        params.put("username", SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
        manager.post(params, manager.getRequestID(), "getAllHdRecordTime.do");
    }
    private void showTimeChoose(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.shape_circle);
        builder.setTitle("选择一个时间的心电数据");
        //    指定下拉列表的显示数据

        final String[] cities ;

        cities = new String[timeList.size()];
        for (int i=0;i<timeList.size();i++){
            cities[i] = timeList.get(i).createTime;
        }
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getELCByTime(which);
            }
        });
        builder.show();
    }
    boolean getLocalELC(String chooseTime){
        Log.i(TAG,"getLocal");
        DatabaseManager manager = new DatabaseManager(this);
        JSONObject object = manager.getOneRawByFieldEqual(ELCBean.TABLENAME, ELCBean.CREATETIME, chooseTime);
        try {
            int count = object.getInt("count");
            Log.i(TAG,"local count:"+count);
            if (count > 0){
                String fileName = object.getString(ELCBean.FILENAME);
                ECGFile file = ECGFilesUtils.getECGFileByName(fileName);
                if (file == null){
                    return false;
                }
                showELC(file);
                return true;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }
    void dealWithELC(JSONObject object){
        int state = 0;
        String error;
        Log.i(TAG,object.toString());
        try {
            state = object.getInt(PublicRes.STATE);
            error = object.getString(PublicRes.EXCEPTION);
            JSONArray array = object.getJSONArray("list");
            if (array.length() >0){
                Log.i(TAG,"0");
                JSONObject fileObject=array.getJSONObject(0);
                ECGFile file = new ECGFile();
                Log.i(TAG,"1");
                file.time = fileObject.getString("measureTime");
                Log.i(TAG,"2");
                file.nAnalysis = fileObject.getInt("analysis");
                Log.i(TAG,"3");
                String ecgDataArray = fileObject.getString("ecg");
                Log.i(TAG,"4");
                List<Integer> ecgData = new ArrayList<>();
                ecgDataArray = ecgDataArray.substring(1,ecgDataArray.length()-1);
                Log.i(TAG, ecgDataArray);
                String dates[] = ecgDataArray.split(",");

                for (String data:dates){
                    ecgData.add(Integer.valueOf(data));
                }
                file.ecgData = ecgData;
                file.nAverageHR = fileObject.getInt("heartRate");
                showELC(file);
            }

        } catch (JSONException e) {
            Log.i(TAG,"excshow");
            e.printStackTrace();
        }
    }
    private void getELCByTime(int which){
        Log.i(TAG,"which:"+which);
        if (getLocalELC(timeList.get(which).createTime)){
            Log.i(TAG,"local OK");
//            return;
        }
        Log.i(TAG,"local false");
        MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                Log.i(TAG,"success");
                dealWithELC(JSONResponse);
            }

            @Override
            public void onFailure(int requestId, int errorCode) {


            }
        });
        RequestParams params = new RequestParams();
        params.put("username",SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
        params.put("startTime",timeList.get(which).createTime);
        params.put("endTime", timeList.get(which).createTime);
        manager.post(params, manager.getRequestID(), "getHdPatientRecords.do");
    }
    void saveFileToLocal(ELCBean bean){
        Log.i(TAG,"fileName:"+bean.fileName);
        bean.insert();
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

            isFinish = false;
            //    当bfinish值为1代表传输完成
            if (bFinish == 1 ){
                Log.i(TAG,"完成");
                isFinish = true;
            }
            try {
                if(srcByte.size()>0) {
                    Log.i(TAG, " size:" + bFinish);

                    ECGFile ecgFile = FileOperation.AnalyseSCPFile(srcByte);

                    ELCBean bean = new ELCBean(ElectorDragramActivity.this);
                    bean.createTime = ecgFile.time;
                    //  打包ecg文件到本地
                    bean.fileName = ECGFilesUtils.packECGFile(ecgFile);
                    saveFileToLocal(bean);
//                    handler.sendEmptyMessage(MTFINISH);
//                    setHR(ecgFile.nAverageHR);
//                    setAnalysisResult(ecgFile.nAnalysis-1);

                }else {
                    Log.i(TAG, "空"+bFinish);
                }

            } catch (Exception e) {
                Log.i(TAG, "exc:"+bFinish);
                if (bFinish == 3){
                    handler.sendEmptyMessage(MTFINISH);
                }
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

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectUtils!=null) {
            connectUtils.closeSocket();
            connectUtils = null;
        }
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

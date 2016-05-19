package com.welling.kinghacker.activities;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.creative.base.BLUReader;
import com.creative.base.BLUSender;
import com.creative.base.BaseDate;
import com.creative.FingerOximeter.IFingerOximeterCallBack;
import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.customView.BloodOxygenChartView;
import com.welling.kinghacker.customView.BloodOxygenView;

import com.welling.kinghacker.customView.FilterView;
import com.welling.kinghacker.customView.MTDialog;
import com.welling.kinghacker.customView.MTToast;
import com.welling.kinghacker.customView.OverFlowView;

import com.welling.kinghacker.customView.OxygenChooseDialog;
import com.welling.kinghacker.customView.OxygenMTDialog;
import com.welling.kinghacker.bean.OxygenDataRecord;
import com.welling.kinghacker.database.DatabaseManager;
import com.welling.kinghacker.tools.BlueToothManager;
import com.welling.kinghacker.tools.BluetoothConnectUtils;
import com.welling.kinghacker.tools.FingerOximeterOxygen;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BloodOxygenActivity extends MTActivity {
    List<OxygenDataRecord> timeList = new ArrayList<>();
    private  FrameLayout rootView;
    private enum ViewType{single,multiple,all,choose}
    private ViewType viewType;
    public  BloodOxygenView singleBloodOxygenView;
    private Animation rightInAnimation;
    public  BloodOxygenChartView bloodOxygenChartView;
    BlueToothManager blueToothManager;
    MTToast mtToast;
    List<String> items;
    MTDialog mtDialog;
    OxygenMTDialog oxygenMTDialog;
    OxygenChooseDialog oxygenChooseDialog;
    Intent intent;
    BluetoothConnectUtils connectUtils;
    FingerOximeterOxygen fo;
    public  int currentOxygenValue = 0;
    boolean isFinish = false;
    public OxygenDataRecord oxygenDataRecord = null;
    public  String time,attr,date;
    public static boolean update = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_moudle_layout);
        init();
    }
    private void init() {
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
        setParentView(findViewById(R.id.universalMoudleRootView));
        rootView = (FrameLayout)findViewById(R.id.universalUpView);
        if(singleBloodOxygenView==null)
             singleBloodOxygenView = new BloodOxygenView(this);
        else rootView.removeView(singleBloodOxygenView.getBloodOxygenView());

        rootView.addView(singleBloodOxygenView.getBloodOxygenView());
        LinearLayout timeChoose = (LinearLayout)singleBloodOxygenView.getRootView().findViewById(R.id.OxygenTimeChoose);
        timeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeChoose();
            }
        });
        viewType = ViewType.single;
        singleBloodOxygenView.setBloodOxygenValue(currentOxygenValue);
        if(value!=null) {
            singleBloodOxygenView.setBloodOxygenDate(date);
            singleBloodOxygenView.setBloodOxygenTime(time);
            singleBloodOxygenView.setBloodOxygenAttr(attr);
        }
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
                showChooseDialog();
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
        if(bloodOxygenChartView==null)
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
        showAlertDialog("正在查找...", false, true);
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
                switch (state) {
                    case BluetoothConnectUtils.CONNECTING:
                        showAlertDialog("正在连接...", false, true);
                        break;
                    case BluetoothConnectUtils.CONNECT_FAILED:
                        showAlertDialog("连接失败...", true, true);
                        mtDialog.dismiss();
                        //在此跳出手动输入的弹窗
                        showMTAlertDialog();
                        break;
                    case BluetoothConnectUtils.CONNECTED:
                        showAlertDialog("连接成功", false, true);
                        //跳到展示的页面
                        mtDialog.dismiss();
                        intent = new Intent(BloodOxygenActivity.this, BloodOxygenUploadActivity.class);
                        startActivity(intent);
                        break;
                    case BluetoothConnectUtils.SEARCH_COMPLETE:
                        showAlertDialog("搜索结束...", true, true);
                        break;
                    case BluetoothConnectUtils.SEARCH_FAILED:
                        showAlertDialog("搜索失败...", true, true);
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
        items.add(new OverFlowItem(OverFlowView.NONE, PublicRes.getInstance().bloodSugerItem4));
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
                }else{
                    //默认展示最近10个数据
                    BloodOxygenChartView.numberOfData = 10;
                    bloodOxygenChartView.initDate();
                    rootView.removeView(bloodOxygenChartView);
                }
                rootView.startAnimation(rightInAnimation);
                rootView.addView(bloodOxygenChartView);
                viewType = ViewType.all;
            }
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem3)){
            final FilterView filterView = new FilterView(this);
            filterView.showFilter(findViewById(R.id.universalMoudleRootView));
            filterView.setOnButtonClickListener(new FilterView.OnButtonClickListener(){
                @Override
                public void onButtonClick(int which) {
                    getAllTimeList();
                    //得到当前时间
                    SimpleDateFormat formatter = new  SimpleDateFormat  ("yyyy年MM月dd日HH:mm:ss");
                    Date curDate =new  Date(System.currentTimeMillis());
                    String time1 = formatter.format(curDate).split("日")[0];//存储今天的日期

                    Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
                    ca.setTime(curDate); //设置时间为当前时间
                    ca.add(Calendar.DAY_OF_MONTH, -1); //天数减1
                    Date lastOneDay = ca.getTime(); //结果
                    String time2 = formatter.format(lastOneDay).split("日")[0];//存储昨天的日期
                    ca.add(Calendar.DAY_OF_MONTH, -1); //天数减1
                    Date lastTwoDay = ca.getTime(); //结果
                    String time3 = formatter.format(lastTwoDay).split("日")[0];//存储前天的日期
                    int number= 0;
                    switch (which){
                        case 0://查询最近一天
                            for(OxygenDataRecord oxygenDataRecord:timeList){
                                if(oxygenDataRecord.updatetime.split("日")[0].equals(time1)){//查询今天测量的次数
                                    number++;
                                }
                            }
                            BloodOxygenChartView.numberOfData = number;//改变最大容量
                            if(bloodOxygenChartView!=null){
                                rootView.removeAllViews();
                                bloodOxygenChartView.initDate();
                                rootView.removeView(bloodOxygenChartView);
                                rootView.startAnimation(rightInAnimation);
                                rootView.addView(bloodOxygenChartView);
                                viewType = ViewType.choose;
                            }
                            filterView.dismiss();
                            break;
                        case 1://查询最近两天
                            for(OxygenDataRecord oxygenDataRecord:timeList){
                                if(oxygenDataRecord.updatetime.split("日")[0].equals(time1)){//查询今天测量的次数
                                    number++;
                                }else{
                                    if(oxygenDataRecord.updatetime.split("日")[0].equals(time2)){//查询昨天测量的次数
                                        number++;
                                    }
                                }
                            }
                            BloodOxygenChartView.numberOfData = number;//改变最大容量
                            if(bloodOxygenChartView!=null){
                                rootView.removeAllViews();
                                bloodOxygenChartView.initDate();
                                rootView.removeView(bloodOxygenChartView);
                                rootView.startAnimation(rightInAnimation);
                                rootView.addView(bloodOxygenChartView);
                                viewType = ViewType.choose;
                            }
                            filterView.dismiss();
                            break;
                        case 2://查询最近三天
                            for(OxygenDataRecord oxygenDataRecord:timeList){
                                if(oxygenDataRecord.updatetime.split("日")[0].equals(time1)){//查询今天测量的次数
                                    number++;
                                }else{
                                    if(oxygenDataRecord.updatetime.split("日")[0].equals(time2)){//查询昨天测量的次数
                                        number++;
                                    }else{
                                        if(oxygenDataRecord.updatetime.split("日")[0].equals(time3)){//查询前天测量的次数
                                            number++;
                                        }
                                    }
                                }
                            }
                            BloodOxygenChartView.numberOfData = number;//改变最大容量
                            if(bloodOxygenChartView!=null){
                                rootView.removeAllViews();
                                bloodOxygenChartView.initDate();
                                rootView.removeView(bloodOxygenChartView);
                                rootView.startAnimation(rightInAnimation);
                                rootView.addView(bloodOxygenChartView);
                                viewType = ViewType.choose;
                            }
                            filterView.dismiss();
                            break;
                        case 3://确定
                            //获取开始时间与结束时间
                            String startTime = filterView.getStartTime();
                            String endTime = filterView.getEndTime();
                            int startYear = Integer.parseInt(startTime.split("-")[0]);
                            int startMonth = Integer.parseInt(startTime.split("-")[1]);
                            int startDay = Integer.parseInt(startTime.split("-")[2]);
                            int endYear = Integer.parseInt(endTime.split("-")[0]);
                            int endMonth = Integer.parseInt(endTime.split("-")[1]);
                            int endDay = Integer.parseInt(endTime.split("-")[2]);
                            Log.i("日期",startYear+"    "+startMonth+"   "+startDay);
                            Log.i("日期", endYear + "    " + endMonth + "   " + endDay);
                            StringBuffer endTimeBuffer = new StringBuffer(endTime);
                            endTimeBuffer.deleteCharAt(4);
                            endTimeBuffer.deleteCharAt(5);
                            endTimeBuffer.insert(4, "年");
                            if(endMonth<10){
                                endTimeBuffer.insert(5, '0');
                                endTimeBuffer.insert(7, "月");
                            }
                            if(endDay<10){
                                endTimeBuffer.insert(8,'0');
                            }
                            endTime = endTimeBuffer.toString();
                            Log.i("日期","    "+endTime);
                            for(OxygenDataRecord oxygenDataRecord:timeList){
                                boolean ok = true;
                                int cuYear = Integer.parseInt(oxygenDataRecord.updatetime.split("年")[0]);
                                String temp = oxygenDataRecord.updatetime.split("年")[1];
                                int cuMonth = Integer.parseInt(temp.split("月")[0]);
                                temp = temp.split("月")[1];
                                int cuDay = Integer.parseInt(temp.split("日")[0]);
                                if(cuYear<startYear||cuYear>endYear) {
                                    ok = false;
                                    continue;
                                }
                                if(cuYear>startYear&&cuYear==endYear){
                                    if(cuMonth>endMonth){ ok = false;continue;}
                                    if(cuMonth==endMonth){
                                        if(cuDay>endDay){
                                            ok = false;
                                            continue;
                                        }

                                    }
                                }
                                if(cuYear==startYear&&cuYear<endYear){
                                    if(cuMonth<startMonth){ ok = false;continue;}
                                    if(cuMonth==startMonth){
                                        if(cuDay<startDay){
                                            ok = false;
                                            continue;
                                        }

                                    }
                                }
                                if(cuYear==startYear&&cuYear==endYear){
                                    if(cuMonth<startMonth){ ok = false;continue;}
                                    if(cuMonth>endMonth){ ok = false;continue;}
                                    if(cuMonth==startMonth&&cuMonth<endMonth){
                                        if(cuDay<startDay){
                                            ok = false;
                                            continue;
                                        }

                                    }
                                    if(cuMonth>startMonth&&cuMonth==endMonth){
                                        if(cuDay>endDay){
                                            ok = false;
                                            continue;
                                        }
                                    }
                                    if(cuMonth==startMonth&&cuMonth==endMonth){
                                        if(cuDay>endDay){
                                            ok = false;
                                            continue;
                                        }
                                        if(cuDay<startDay){
                                            ok = false;
                                            continue;
                                        }
                                    }
                                }
                                if(ok){
                                    number++;
                                }
                            }
                            Log.i("日期",number+"");
                            BloodOxygenChartView.numberOfData = number;//改变最大容量
                            if(bloodOxygenChartView!=null){
                                rootView.removeAllViews();
                                bloodOxygenChartView.initDateAnother(endTime);
                                rootView.removeView(bloodOxygenChartView);
                                rootView.startAnimation(rightInAnimation);
                                rootView.addView(bloodOxygenChartView);
                                viewType = ViewType.choose;
                            }
                            filterView.dismiss();
                            break;
                    }
                }
            });
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem4)){
            //同步到云端  当没有网络时数据可能没法自动上传  所以可以手动同步
            updateToCloud();
        }
    }
    private void updateToCloud(){
        MTHttpManager manager = new MTHttpManager();

        final List<OxygenDataRecord> beans = new ArrayList<>();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                if (requestId < beans.size()) {
                    beans.get(requestId).update();
                    if(requestId==beans.size()-1)
                        makeToast("上传云端成功");
                }
            }

            @Override
            public void onFailure(int requestId, int errorCode) {
                //Toast.makeText(BloodOxygenActivity.this, "上传云端失败", Toast.LENGTH_LONG).show();
                if(requestId==beans.size()-1)
                    makeToast("上传云端失败");
            }
        });

        DatabaseManager dbManager = new DatabaseManager(this);
        JSONObject object = dbManager.getMultiRaw(new OxygenDataRecord(this).tableName, OxygenDataRecord.ISUPDATE,null, "0");

        try {
            int count = object.getInt("count");
            Log.i("123", "count:" + count);
            for(int i=0;i<count;i++){
                OxygenDataRecord bean = new OxygenDataRecord(this);
                bean.updatetime = object.getJSONObject(""+i).getString(OxygenDataRecord.UPDATETIME);
                beans.add(bean);
                String time =null;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("bmpValue",object.getJSONObject(""+i).getInt(OxygenDataRecord.BMPVALUE));
                jsonObject.put("OxygenValue",object.getJSONObject(""+i).getInt(OxygenDataRecord.OXYGENVALUE));
                SimpleDateFormat formatter = new  SimpleDateFormat  ("yyyy年MM月dd日HH:mm:ss");
                try {
                    long st = formatter.parse(object.getJSONObject("" + i).getString(OxygenDataRecord.UPDATETIME)).getTime();
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    time =formatter.format(st);
                }catch (ParseException e){
                    e.printStackTrace();
                }

                jsonObject.put("time",time);
                manager.updateToCloud(this, jsonObject.toString(), MTHttpManager.BO, i);
                Log.i("123", jsonObject.toString());
            }
        } catch (JSONException e) {
            Log.i("123","updateExc");
            e.printStackTrace();
        }
    }
    void getLocalTimeList(){
        timeList.clear();
        DatabaseManager dbManager = new DatabaseManager(this);
        JSONObject object = dbManager.getMultiRaw(new OxygenDataRecord(this).tableName, OxygenDataRecord.UPDATETIME, null, null);

        try {
            int count = object.getInt("count");
            for(int i=0;i<count;i++){
                OxygenDataRecord bean = new OxygenDataRecord(this);
                bean.updatetime = object.getJSONObject(""+i).getString(OxygenDataRecord.UPDATETIME);
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
                    JSONArray jsonArray = JSONResponse.getJSONArray("list");
                    for (int i=0;i< jsonArray.length();i++){
                        String timestamp = jsonArray.getString(i);
                        OxygenDataRecord bean = new OxygenDataRecord(BloodOxygenActivity.this);
                        bean.updatetime = timestamp;
                        timeList.add(bean);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                if (state == 0){
                    //makeToast("获取历史列表失败");
                    Toast.makeText(BloodOxygenActivity.this,"获取列表失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });
        RequestParams params = new RequestParams();
        params.put("username", SystemTool.getSystem(BloodOxygenActivity.this).getStringValue(PublicRes.ACCOUNT));
        manager.post(params, manager.getRequestID(), "getAllHdRecordTime.do");
    }
    private void showTimeChoose(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.shape_circle);
        builder.setTitle("选择一个时间的血氧数据");
        //    指定下拉列表的显示数据

        final String[] cities ;

        cities = new String[timeList.size()];
        for (int i=0;i<timeList.size();i++){
            cities[i] = timeList.get(i).updatetime;
        }
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getOxygenByTime(which);
            }
        });
        builder.show();
    }
    boolean getLocalOxygen(String chooseTime){
        Log.i("123", "getLocal");
        DatabaseManager manager = new DatabaseManager(this);
        JSONObject object = manager.getOneRawByFieldEqual(new OxygenDataRecord(this).tableName, OxygenDataRecord.UPDATETIME, chooseTime);
        try {
            int count = object.getInt("count");
            Log.i("123","local count:"+count);
            if (count > 0){
                currentOxygenValue = object.getInt(OxygenDataRecord.OXYGENVALUE);
                singleBloodOxygenView.setBloodOxygenValue(currentOxygenValue);
                date = chooseTime.split("日")[0];
                singleBloodOxygenView.setBloodOxygenDate(date);
                time = chooseTime.split("日")[1];
                singleBloodOxygenView.setBloodOxygenTime(time);
                attr = "异常";
                if (currentOxygenValue >= 90) {
                    attr = "正常";
                }
                singleBloodOxygenView.setBloodOxygenAttr(attr);
                singleBloodOxygenView.startAnimation();
                rootView.invalidate();
                return true;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }
    private void getOxygenByTime(int which){
        Log.i("123", "which:" + which);
        if (getLocalOxygen(timeList.get(which).updatetime)){
            Log.i("123","local OK");
//            return;
        }else {//本地没有数据  从云端获取
            Log.i("123", "local false");
            MTHttpManager manager = new MTHttpManager();
            manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
                @Override
                public void onSuccess(int requestId, JSONObject JSONResponse) {
                    Log.i("123", "success");
                    dealWithOxygen(JSONResponse);
                }

                @Override
                public void onFailure(int requestId, int errorCode) {


                }
            });
            RequestParams params = new RequestParams();
            params.put("username", SystemTool.getSystem(this).getStringValue(PublicRes.ACCOUNT));
            params.put("startTime", timeList.get(which).updatetime);
            params.put("endTime", timeList.get(which).updatetime);
            manager.post(params, manager.getRequestID(), "getPatientRecords.do");
        }
    }
    void dealWithOxygen(JSONObject object){
        int state = 0;
        String error;
        Log.i("123", object.toString());
        try {
            state = object.getInt(PublicRes.STATE);
            error = object.getString(PublicRes.EXCEPTION);
            Log.i("123",object.toString());
           // JSONArray array = object.getJSONArray("list");
//            if (array.length() >0){
//                Log.i("123","0");
//                JSONObject fileObject=array.getJSONObject(0);
//                //处理完数据要展示
//                Log.i("123待处理数据",fileObject.toString());
//            }

        } catch (JSONException e) {
            Log.i("123","excshow");
            e.printStackTrace();
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
    void showChooseDialog(){
        if (oxygenChooseDialog == null){
            oxygenChooseDialog = new OxygenChooseDialog(this);
            oxygenChooseDialog.setOnButtonClickListener(new OxygenChooseDialog.OnButtonClickListener() {
                @Override
                public void onButtonClick(int which) {
                    oxygenChooseDialog.dismiss();
                    switch (which){
                        case 0://自动上传
                            discoverBlueTooth();
                            break;
                        case 1://手动上传
                            showMTAlertDialog();
                            break;
                    }
                    oxygenChooseDialog = null;
                }
            });
        }
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
                                SimpleDateFormat formatter = new  SimpleDateFormat  ("yyyy年MM月dd日HH:mm:ss");
                                Date curDate =new  Date(System.currentTimeMillis());
                                String time1 = formatter.format(curDate);
                                oxygenDataRecord = new OxygenDataRecord(BloodOxygenActivity.this,data,60,time1);
                                //创建表  当然有分析 如果表存在就不创建
                                oxygenDataRecord.createTable();
                                //将信息插入
                                oxygenDataRecord.insert();
                                init();
                                if(bloodOxygenChartView!=null){
                                    BloodOxygenChartView.numberOfData = 10;
                                    bloodOxygenChartView.initDate();
                                }
                                updateToCloud();//自动上传云端
                            }else{
                                Toast.makeText(BloodOxygenActivity.this,"数据不合法",Toast.LENGTH_LONG).show();
                            }
                            oxygenMTDialog.dismiss();
                            oxygenMTDialog = null;
                            break;
                    }
                }
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        init();
        if(bloodOxygenChartView!=null){
            BloodOxygenChartView.numberOfData = 10;
            bloodOxygenChartView.initDate();
        }
        if(update){
            updateToCloud();
            update = false;
        }
    }

}

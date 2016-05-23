package com.welling.kinghacker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.bean.SugerBean;
import com.welling.kinghacker.customView.BloodOxygenChartView;
import com.welling.kinghacker.customView.BloodSugerView;
import com.welling.kinghacker.customView.ChartView;
import com.welling.kinghacker.customView.FilterView;
import com.welling.kinghacker.customView.MTDialog;
import com.welling.kinghacker.customView.MTToast;
import com.welling.kinghacker.customView.OverFlowView;

import com.welling.kinghacker.customView.OxygenMTDialog;
import com.welling.kinghacker.database.DatabaseManager;
import com.welling.kinghacker.tools.FontTool;
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

/**
 * Created by KingHacker on 3/10/2016.
 * update by 13wlli on 4/30/2016
 **/
public class BloodSugerActivity extends MTActivity {

    private String time,attr,date;
    private FrameLayout rootView;
    List<SugerBean> timeList = new ArrayList<>();
    private enum ViewType{single,multiple,all,choose}
    private ViewType viewType;
    public BloodSugerView singleBloodSugerView;
    private ChartView multipleView;
    private Animation rightInAnimation;
    MTToast mtToast;
    List<String> items;
    MTDialog mtDialog;
    OxygenMTDialog oxygenMTDialog;
    public  ChartView ChartView;
    public static boolean update = false;
    public SugerBean sugerBean = null;
    public float currentSugerValue = 0;
    float high = 16f;
    float low = 3.9f;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_moudle_layout1);
        init();
    }

    public void init() {
        //获取最近一次测量记录的血糖值赋值给currentOxygenValue
        SugerBean sugerbean = new SugerBean(this);
        String value =  sugerbean.getRecentlyOneData();
        if(value!=null) {
            String[] dateTime = new String[3];
            dateTime = value.split(",");
            currentSugerValue = Float.parseFloat(dateTime[0]);
            date = dateTime[1];
            time = dateTime[2];
            attr = "正常";
            if (currentSugerValue >= high) {
                attr = "高血糖";
            }
            else if (currentSugerValue <= low) {
                attr = "低血糖";
            }
        }

        setParentView(findViewById(R.id.universalMoudleRootView1));
        rootView = (FrameLayout)findViewById(R.id.universalUpView1);
        if(singleBloodSugerView==null)
            singleBloodSugerView = new BloodSugerView(this);
        else rootView.removeView(singleBloodSugerView.getBloodSugerView());
        rootView.addView(singleBloodSugerView.getBloodSugerView());
        if(singleBloodSugerView==null)
            singleBloodSugerView = new BloodSugerView(this);
        else rootView.removeView(singleBloodSugerView.getBloodSugerView());

        rootView.addView(singleBloodSugerView.getBloodSugerView());
        LinearLayout timeChoose = (LinearLayout)singleBloodSugerView.getRootView().findViewById(R.id.SugerTimeChoose);
        timeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeChoose();
            }
        });
        viewType = ViewType.single;

        singleBloodSugerView.setBloodSugerValue(currentSugerValue);
        if(value!=null) {
            singleBloodSugerView.setBloodSugerDate(date);
            singleBloodSugerView.setBloodSugerTime(time);
            singleBloodSugerView.setBloodSugerAttr(attr);
        }

        singleBloodSugerView.startAnimation();

        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        Button synInfoButton = (Button)findViewById(R.id.synButton1);
        synInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));
        Button sendInfoButton = (Button)findViewById(R.id.sendButton);
        sendInfoButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));
        Button medicineButton = (Button)findViewById(R.id.medicineQueryButton1);
        medicineButton.setBackgroundColor(SystemTool.getSystem(this).getXMLColor(R.color.bloodSugerBGColor));
        Button doctorInfoButton = (Button)findViewById(R.id.doctorButton1);
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
        if(ChartView==null)
            drawLineChart();
    }
    @Override
    protected void onPostCreate(Bundle saveBundle){
        super.onPostCreate(saveBundle);
        initActionBar();
    }
//    画折线图
    private void drawLineChart(){
        float bloodSugerData1[] = null;
        String bloodUpdateTime1[] = null;
        int numberOfData = 10;
        int leftI,rightI;

        SugerBean sugerBean2 = new SugerBean(BloodSugerActivity.this,numberOfData);
        bloodSugerData1 = new float[numberOfData];
        bloodUpdateTime1 =new String [numberOfData];
        for(int i=0;i<numberOfData;i++) bloodSugerData1[i] = 100-i;
        bloodSugerData1 = sugerBean2.getRecentlyMoreData();
        bloodUpdateTime1 = sugerBean2.getRecentlyMoreTime();

        FontTool fontTool = new FontTool(this);
        multipleView = new ChartView(this);

        int viewHeight = fontTool.getViewHeight(rootView);
        float originPointX = getResources().getDimension(R.dimen.originalX),
                originPointY = viewHeight - getResources().getDimension(R.dimen.originalY);

        multipleView.setOriginPoint(originPointX, originPointY);
        multipleView.setYLength((int)(originPointY - getResources().getDimension(R.dimen.originalY)/2));
        List<String> xaxis = new ArrayList<>();
        for (int i= 1;i<numberOfData;i++){
            xaxis.add(bloodUpdateTime1[i]);
        }
        multipleView.setXaxis(xaxis);
        List<Float> yaxis = new ArrayList<>();
        for (int i= 1;i<numberOfData;i++){
            float num = bloodSugerData1[i]*10;
            int num1 = (int) num;
            double num3=num1*0.1;
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

 /*               else{
                    //默认展示最近10个数据
                    ChartView.numberOfData = 10;
                    ChartView.initDate();
                    rootView.removeView(ChartView);
                }
                rootView.startAnimation(rightInAnimation);
                rootView.addView(ChartView);
                viewType = ViewType.all;   */
            }
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem3)){
            FilterView filterView = new FilterView(this);
            filterView.showFilter(findViewById(R.id.universalMoudleRootView));

  /*          final FilterView filterView = new FilterView(this);
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
                            for(SugerBean sugerBean:timeList){
                                if(sugerBean.updatetime.split("日")[0].equals(time1)){//查询今天测量的次数
                                    number++;
                                }
                            }
                            ChartView.numberOfData = number;//改变最大容量
                            if(ChartView!=null){
                                rootView.removeAllViews();
                                ChartView.initDate();
                                rootView.removeView(ChartView);
                                rootView.startAnimation(rightInAnimation);
                                rootView.addView(ChartView);
                                viewType = ViewType.choose;
                            }
                            filterView.dismiss();
                            break;
                        case 1://查询最近两天
                            for(SugerBean sugerBean:timeList){
                                if(sugerBean.updatetime.split("日")[0].equals(time1)){//查询今天测量的次数
                                    number++;
                                }else{
                                    if(sugerBean.updatetime.split("日")[0].equals(time2)){//查询昨天测量的次数
                                        number++;
                                    }
                                }
                            }
                            ChartView.numberOfData = number;//改变最大容量
                            if(ChartView!=null){
                                rootView.removeAllViews();
                                ChartView.initDate();
                                rootView.removeView(ChartView);
                                rootView.startAnimation(rightInAnimation);
                                rootView.addView(ChartView);
                                viewType = ViewType.choose;
                            }
                            filterView.dismiss();
                            break;
                        case 2://查询最近三天
                            for(SugerBean sugerBean:timeList){
                                if(sugerBean.updatetime.split("日")[0].equals(time1)){//查询今天测量的次数
                                    number++;
                                }else{
                                    if(sugerBean.updatetime.split("日")[0].equals(time2)){//查询昨天测量的次数
                                        number++;
                                    }else{
                                        if(sugerBean.updatetime.split("日")[0].equals(time3)){//查询前天测量的次数
                                            number++;
                                        }
                                    }
                                }
                            }
                            ChartView.numberOfData = number;//改变最大容量
                            if(ChartView!=null){
                                rootView.removeAllViews();
                                ChartView.initDate();
                                rootView.removeView(ChartView);
                                rootView.startAnimation(rightInAnimation);
                                rootView.addView(ChartView);
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
                            for(SugerBean sugerBean:timeList){
                                boolean ok = true;
                                int cuYear = Integer.parseInt(sugerBean.updatetime.split("年")[0]);
                                String temp = sugerBean.updatetime.split("年")[1];
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
                            ChartView.numberOfData = number;//改变最大容量
                            if(ChartView!=null){
                                rootView.removeAllViews();
                                ChartView.initDateAnother(endTime);
                                rootView.removeView(ChartView);
                                rootView.startAnimation(rightInAnimation);
                                rootView.addView(ChartView);
                                viewType = ViewType.choose;
                            }
                            filterView.dismiss();
                            break;
                    }
                }
            });  */
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem4)){
            //同步到云端  当没有网络时数据可能没法自动上传  所以可以手动同步
            updateToCloud();
        }
    }

    public void updateToCloud(){
        MTHttpManager manager = new MTHttpManager();

        final List<SugerBean> beans = new ArrayList<>();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                if (requestId < beans.size()) {
                    beans.get(requestId).update();
                    if (requestId == beans.size() - 1)
                        makeToast("上传云端成功");
//                    Toast.makeText(BloodSugerActivity.this, "上传云端成功", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int requestId, int errorCode) {
//                Toast.makeText(BloodSugerActivity.this, "上传云端失败", Toast.LENGTH_LONG).show();
                if (requestId == beans.size() - 1)
                    makeToast("上传云端失败");
            }
        });

        DatabaseManager dbManager = new DatabaseManager(this);
        JSONObject object = dbManager.getMultiRaw(new SugerBean(this).tableName, SugerBean.ISUPDATE,null, "0");

        try {
            int count = object.getInt("count");
            Log.i("123", "count:" + count);
            if(count==0) makeToast("没有可以上传的数据");
            else
            for(int i=0;i<count;i++){
                SugerBean bean = new SugerBean(this);
                bean.updatetime = object.getJSONObject(""+i).getString(SugerBean.UPDATETIME);
                beans.add(bean);
                String time =null;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("SugerValue",object.getJSONObject(""+i).getInt(SugerBean.SUGERVALUE));
                SimpleDateFormat formatter = new  SimpleDateFormat  ("yyyy年MM月dd日HH:mm:ss");
                try {
                    long st = formatter.parse(object.getJSONObject("" + i).getString(SugerBean.UPDATETIME)).getTime();
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    time =formatter.format(st);
                }catch (ParseException e){
                    e.printStackTrace();
                }

                jsonObject.put("time",time);
                manager.updateToCloud(this, jsonObject.toString(), MTHttpManager.BS, i);
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
        JSONObject object = dbManager.getMultiRaw(new SugerBean(this).tableName, SugerBean.ISUPDATE,null, "0");

        try {
            int count = object.getInt("count");
            for(int i=0;i<count;i++){
                SugerBean bean = new SugerBean(this);
                bean.updatetime = object.getJSONObject(""+i).getString(SugerBean.UPDATETIME);
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
                        SugerBean bean = new SugerBean(BloodSugerActivity.this);
                        bean.updatetime = timestamp;
                        timeList.add(bean);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                if (state == 0){
                    //makeToast("获取历史列表失败");
                    Toast.makeText(BloodSugerActivity.this,"获取列表失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });
        RequestParams params = new RequestParams();
        params.put("username", SystemTool.getSystem(BloodSugerActivity.this).getStringValue(PublicRes.ACCOUNT));
        manager.post(params, manager.getRequestID(), "getAllHdRecordTime.do");
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
                            String s = oxygenMTDialog.getText();
                            float data = 0;
                            try{
                                data = Float.parseFloat(s);
                            }catch (NumberFormatException e){
                                e.printStackTrace();
                            }
                            if (data<=30&&data>0) {
                                //将测量结果保存
                                SimpleDateFormat formatter = new  SimpleDateFormat  ("yyyy年MM月dd日HH:mm:ss");
                                Date curDate =new  Date(System.currentTimeMillis());
                                String time1 = formatter.format(curDate);
                                sugerBean = new SugerBean(BloodSugerActivity.this,data,time1);
                                //创建表  当然有分析 如果表存在就不创建
                                sugerBean.createTable();
                                //将信息插入
                                sugerBean.insert();
                                init();
                                if(ChartView!=null){
                                    ChartView.numberOfData = 10;
                                    ChartView.initDate();
                                }
                                updateToCloud();
                            }else{
                                Toast.makeText(BloodSugerActivity.this,"数据不合法",Toast.LENGTH_LONG).show();
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
        if(ChartView!=null){
            BloodOxygenChartView.numberOfData = 10;
            ChartView.initDate();
        }
        if(update){
            updateToCloud();
            update = false;
        }
    }

    private void showTimeChoose(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.shape_circle);
        builder.setTitle("选择一个时间的血糖数据");
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
                getSugerByTime(which);
            }
        });
        builder.show();
    }

    private void getSugerByTime(int which){
        Log.i("123", "which:" + which);
        if (getLocalSuger(timeList.get(which).updatetime)){
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
            manager.post(params, manager.getRequestID(), "getHdPatientRecords.do");
        }
    }

    void dealWithOxygen(JSONObject object){
        int state = 0;
        String error;
        Log.i("123", object.toString());
        try {
            state = object.getInt(PublicRes.STATE);
            error = object.getString(PublicRes.EXCEPTION);
            JSONArray array = object.getJSONArray("list");
            if (array.length() >0){
                Log.i("123","0");
                JSONObject fileObject=array.getJSONObject(0);
                //处理完数据要展示
                Log.i("123待处理数据",fileObject.toString());
            }

        } catch (JSONException e) {
            Log.i("123","excshow");
            e.printStackTrace();
        }
    }

    boolean getLocalSuger(String chooseTime){
        Log.i("123", "getLocal");
        DatabaseManager manager = new DatabaseManager(this);
        JSONObject object = manager.getOneRawByFieldEqual(new SugerBean(this).tableName, SugerBean.UPDATETIME, chooseTime);
        try {
            int count = object.getInt("count");
            Log.i("123","local count:"+count);
            if (count > 0){
                double bef = object.getDouble(SugerBean.SUGERVALUE);
                currentSugerValue = (float) bef;
                singleBloodSugerView.setBloodSugerValue(currentSugerValue);
                SimpleDateFormat formatter = new  SimpleDateFormat  ("yyyy年MM月dd日HH:mm:ss");
                try {
                    long st = formatter.parse(chooseTime).getTime();
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    chooseTime =formatter.format(st);
                }catch (ParseException e){
                    e.printStackTrace();
                }
                date = chooseTime.split(" ")[0];
                singleBloodSugerView.setBloodSugerDate(date);
                time = chooseTime.split(" ")[1];
                singleBloodSugerView.setBloodSugerTime(time);
                attr = "正常";
                if (currentSugerValue >= high) {
                    attr = "高血糖";
                }
                else if (currentSugerValue <= low) {
                    attr = "低血糖";
                }
                singleBloodSugerView.setBloodSugerAttr(attr);
                singleBloodSugerView.startAnimation();
                rootView.invalidate();
                return true;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }
}

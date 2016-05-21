package com.welling.kinghacker.activities;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.example.bluetooth.le.DeviceScanActivity;
import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.bean.BloodPressureBean;
import com.welling.kinghacker.customView.BloodPressureView;
import com.welling.kinghacker.customView.FilterView;
import com.welling.kinghacker.customView.LineBlood;
import com.welling.kinghacker.customView.OverFlowView;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zsw on 2016/3/19.
 **/
public class BloodPressureActivity extends MTActivity implements FilterView.OnButtonClickListener{

    BloodPressureView singleBloodPressureView;
    FrameLayout rootView;
    private enum ViewType{single,multiple,all}
    private ViewType viewType;
    private Animation rightInAnimation;
    private LineBlood lineBlood;
    private BloodPressureBean bpbean;
    private Button previous_page,next_page,show_info;
    private int currpage=0;
    private FilterView filterView;
    private ListView show_data;
    private ProgressBar upload_data_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_universal_moudle_layout);
        previous_page=(Button)findViewById(R.id.previous_page);
        next_page=(Button)findViewById(R.id.next_page);
        show_info=(Button)findViewById(R.id.show_info);
        upload_data_progress=(ProgressBar)findViewById(R.id.upload_data_progress);
        previous_page.setVisibility(View.GONE);
        next_page.setVisibility(View.GONE);
        show_info.setVisibility(View.GONE);
        init();
    }
    private void init(){
        setParentView(findViewById(R.id.universalMoudleRootView));
        singleBloodPressureView = new BloodPressureView(this);
        rootView = (FrameLayout)findViewById(R.id.universalUpView);
        rootView.addView(singleBloodPressureView.getBloodPressureView());
        LinearLayout timeChoose = (LinearLayout)singleBloodPressureView.getRootView().findViewById(R.id.bloodPressure_pick);
        timeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("database_click", "you click");
                upload_data_progress.setVisibility(View.VISIBLE);
                MTHttpManager manager=new MTHttpManager();
                manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
                    @Override
                    public void onSuccess(int requestId, JSONObject JSONResponse) {
                        Log.i("database_getdata_s", requestId + " " + JSONResponse.toString());
                        dealwith_bpdata(JSONResponse);
                        upload_data_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int requestId, int errorCode) {
                        Log.i("database_getdata_f",requestId+" "+errorCode);
                        upload_data_progress.setVisibility(View.GONE);
                        Toast.makeText(BloodPressureActivity.this,"连接服务器失败 errorCode="+errorCode,Toast.LENGTH_SHORT).show();
                    }
                });
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endTime= formatter.format(System.currentTimeMillis());
                String startTime=null;
                try {
                    long st = formatter.parse(endTime).getTime() - (long)100 * 86400000;
                    startTime = formatter.format(st).split(" ")[0] + " 00:00:00";
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                RequestParams params=new RequestParams();
                params.put("username", SystemTool.getSystem(BloodPressureActivity.this).getStringValue(PublicRes.ACCOUNT));
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                manager.post(params, manager.getRequestID(), "getHtnPatientRecords.do");
            }
        });
        viewType = ViewType.single;

        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Button synInfoButton = (Button)findViewById(R.id.synButton);
        synInfoButton.setText("同步血压仪数据");
        synInfoButton.setBackgroundColor(getResources().getColor(R.color.bloodSugerBGColor));
        Button medicineButton = (Button)findViewById(R.id.medicineQueryButton);
        medicineButton.setBackgroundColor(getResources().getColor(R.color.bloodSugerBGColor));
        Button doctorInfoButton = (Button)findViewById(R.id.doctorButton);
        doctorInfoButton.setBackgroundColor(getResources().getColor(R.color.bloodSugerBGColor));
        synInfoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoActivity(DeviceScanActivity.class);
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
    }
    private List<Map<String,Object>> getData(JSONObject jsonObject){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try{
            JSONArray jsonArray=jsonObject.getJSONArray("list");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject object=jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<String, Object>();
                String string=object.get("measureTime").toString();
                string=string.substring(0,string.length()-2);
                map.put("time",string);
                map.put("blood_info","高压 "+object.get("systolicPressure").toString()+
                                " 低压 "+object.get("diastolicPressure").toString()+
                                " 心率 "+object.get("heartRate").toString());
                if(object.getInt("heartRateState")==1)
                    map.put("blood_state","心率不齐");
                else map.put("blood_state","");
                list.add(map);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
    private void dealwith_bpdata(final JSONObject jsonObject){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BloodPressureActivity.this);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.bloodpress_data,null);
        alertDialog.setView(view);
        show_data=(ListView)view.findViewById(R.id.show_data);
        SimpleAdapter adapter=new SimpleAdapter(this,getData(jsonObject),R.layout.bloodpress_vlist,
                new String[]{"time","blood_info","blood_state"},
                new int[]{R.id.vlist_time,R.id.vlist_bloodinfo,R.id.vlist_bloodstate});
        show_data.setAdapter(adapter);
        alertDialog.setTitle("血压测量记录");
        alertDialog.setPositiveButton("更新到本地", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject object = new JSONObject();
                JSONObject item;
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    object.put("count", jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        item = new JSONObject();
                        JSONObject temp = jsonArray.getJSONObject(i);
                        item.put("time", temp.get("measureTime").toString());
                        item.put("highblood", temp.get("systolicPressure").toString());
                        item.put("lowblood", temp.get("diastolicPressure").toString());
                        item.put("heartrate", temp.get("heartRate").toString());
                        item.put("heartproblem", temp.get("heartRateState").toString());
                        item.put("isupdate", "1");
                        object.put(i + "", item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BloodPressureBean bpbean=new BloodPressureBean(BloodPressureActivity.this);
                bpbean.putDataintoLocal(object);
                setLatestRecord();
            }
        });
        alertDialog.show();
    }
    @Override
    protected void onPostCreate(Bundle saveBundle){
        super.onPostCreate(saveBundle);
        initActionBar();
    }

    private void initActionBar(){
        setActionBarTitle(getString(R.string.blood_pressu));
        setIsBackEnable(true);
    }

    @Override
    protected void setOverFlowView(){
        super.setOverFlowView();
        List<OverFlowItem> items = new ArrayList<>();
        items.add(new OverFlowItem(OverFlowView.NONE, PublicRes.getInstance().bloodSugerItem1));
        items.add(new OverFlowItem(OverFlowView.NONE, "历史查询"));
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
                singleBloodPressureView.startAnimation();
                rootView.addView(singleBloodPressureView.getBloodPressureView());
                viewType = ViewType.single;
            }
            currpage=0;
            previous_page.setVisibility(View.GONE);
            next_page.setVisibility(View.GONE);
            show_info.setVisibility(View.GONE);
        }else if (text.contentEquals("历史查询")){
            rootView.removeAllViews();
            lineBlood=null;
            lineBlood=new LineBlood(this);
            rootView.startAnimation(rightInAnimation);
            rootView.addView(lineBlood);
            viewType = ViewType.all;
            previous_page.setVisibility(View.VISIBLE);
            previous_page.setText("<");
            next_page.setVisibility(View.VISIBLE);
            show_info.setVisibility(View.VISIBLE);
            if(lineBlood!=null)
                show_info.setText(lineBlood.startTime+"-"+lineBlood.endTime+" 有效记录: "+LineBlood.count);
            currpage=1;
        }else if (text.contentEquals(PublicRes.getInstance().bloodSugerItem3)){
            filterView = new FilterView(this);
            filterView.showFilter(findViewById(R.id.universalMoudleRootView));
            filterView.setOnButtonClickListener(this);
            previous_page.setVisibility(View.GONE);
            next_page.setVisibility(View.GONE);
            show_info.setVisibility(View.GONE);
            currpage=2;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLatestRecord();
    }

    public void setLatestRecord(){
        bpbean=new BloodPressureBean(this);
        bpbean.setLatestRecordFromlocal();
        if(currpage==0) {
            singleBloodPressureView.setValues(bpbean.getHighblood(), bpbean.getLowblood(),
                    bpbean.getHeartrate(), bpbean.getUpdatetime(), BloodPressureBean.blood_status[BloodPressureBean.statu]);
            singleBloodPressureView.startAnimation();
        }else if(currpage==1){
            if (lineBlood != null){
                lineBlood.initdata(this);
                lineBlood.invalidate();
            }
        }
        Log.i("qwert","bpa_onResume");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("qwert","bpa_onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("qwert", "bpa_onPause");
    }

    public void onPrevious_page(View v){
        LineBlood.previous_page();
        lineBlood.invalidate();
    }
    public void onNext_page(View v){
        LineBlood.next_page();
        lineBlood.invalidate();
    }

    @Override
    public void onButtonClick(int which) {
        switch (which){
            case 0:
                setnumofday(null,null,1);
                break;
            case 1:
                setnumofday(null,null,2);
                break;
            case 2:
                setnumofday(null,null,3);
                break;
            case 3:
                Log.i("database_day",filterView.getStartTime()+" "+filterView.getEndTime());
                setnumofday(filterView.getStartTime(), filterView.getEndTime(), -1);
                break;
        }
    }
    public void setnumofday(String sTime,String eTime,int num){
        Log.i("database_day_sure",sTime+" "+eTime);
        rootView.removeAllViews();
        lineBlood=null;
        lineBlood=new LineBlood(this,sTime,eTime,num);
        rootView.startAnimation(rightInAnimation);
        rootView.addView(lineBlood);
        viewType = ViewType.all;
        previous_page.setVisibility(View.VISIBLE);
        previous_page.setText("<");
        next_page.setVisibility(View.VISIBLE);
        show_info.setVisibility(View.VISIBLE);
        if(lineBlood!=null){
            Log.i("database_day_sureof",lineBlood.startTime+" "+lineBlood.endTime);
            show_info.setText(lineBlood.startTime + "|" + lineBlood.endTime + " 有效记录: " + LineBlood.count);
        }
        currpage=1;
    }
}

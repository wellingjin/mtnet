package com.welling.kinghacker.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.creative.base.BaseDate;
import com.welling.kinghacker.customView.BloodOxygenUpload;
import com.welling.kinghacker.bean.OxygenDataRecord;

/**
 * Created by 李双双 on 2016/5/1.
 */
public class BloodOxygenUploadActivity extends MTActivity{
    private  static TextView textViewBloodOxygen,textViewBmp,textViewPi,progressExpalin;
    private  static ProgressBar progressBar;
    private  static BloodOxygenUpload bloodOxygenUpload;//图
    public static int nSpO2 = 0, nPR = 0;
    public static float nPI = 0;
    public static boolean nStatus =true;//代表探头没有脱落
    public static BaseDate.Wave wave = null;
    public static int averageSpo2 = 0,averagePR = 0;//取oxygenDataRecord平均值

    public OxygenDataRecord oxygenDataRecord = null;
    public static Handler mHandler=new Handler()
    {

        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case 1:
                    if(nSpO2!=0) {
                        setTextViewBloodOxygen(nSpO2);
                        setTextViewBmp(nPR);
                        setTextViewPi(nPI);
                        setProgressBar();
                        if(averageSpo2 ==0){
                            averageSpo2 = nSpO2;
                            averagePR = nPR;
                        }else{
                            averageSpo2=(averageSpo2+nSpO2)/2;
                            averagePR=(averagePR+nPR)/2;
                        }
                    }
                    break;
                case 2:
                    if(wave!=null){
                        bloodOxygenUpload.addItem(wave);
                        bloodOxygenUpload.dataOfPi = wave.data;
                        bloodOxygenUpload.invalidate();
                    }
                    break;
                default:
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_oxygen_upload);
        textViewBloodOxygen = (TextView)findViewById(R.id.blood_oxygen_value);
        textViewBmp = (TextView)findViewById(R.id.bmp_value);
        textViewPi = (TextView)findViewById(R.id.pi_value);
        bloodOxygenUpload = (BloodOxygenUpload)findViewById(R.id.bloodOxygenGraphics);
        progressBar = (ProgressBar)findViewById(R.id.upload_progress);
        progressExpalin = (TextView)findViewById(R.id.progressExpalin);
        progressBar.setVisibility(View.VISIBLE);
        this.setActionBarTitle("血氧");
        new Thread(new Runnable(){

            @Override
            public void run() {
                while(true){
                    //测量结束
                    if(getProgressBar()==100){
                        //System.out.println("测量结果：  血氧：" + averageSpo2 + "  心率：" + averagePR);
                        //将测量结果保存
                        oxygenDataRecord = new OxygenDataRecord(BloodOxygenUploadActivity.this,averageSpo2,averagePR);
                        //创建表  当然有分析 如果表存在就不创建
                        oxygenDataRecord.createTable();
                        //将信息插入
                        oxygenDataRecord.insert();
                        //更新展示的血氧值
                        BloodOxygenActivity.currentOxygenValue = averageSpo2;
                        BloodOxygenActivity.myHandler.sendEmptyMessage(1);//更新数据
                        //finish();
                        break;
                    }
                    if(!nStatus){
                        //探头脱落
                        break;
                    }
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public  static void setTextViewBloodOxygen(int data){
        textViewBloodOxygen.setText(data+"");
        bloodOxygenUpload.invalidate();
    }
    public static void setTextViewBmp(int data){
        textViewBmp.setText(data+"");
        bloodOxygenUpload.invalidate();
    }
    public  static void setTextViewPi(float data){
        textViewPi.setText(data+"");
        bloodOxygenUpload.invalidate();
    }
    public  static void setProgressBar(){
        if(progressBar.getProgress()<100)
            progressBar.setProgress(progressBar.getProgress() + 5);
        else{
            progressExpalin.setText(R.string.upload_data_finished);
        }
    }
    public static int getProgressBar(){
        return  progressBar.getProgress();
    }
}

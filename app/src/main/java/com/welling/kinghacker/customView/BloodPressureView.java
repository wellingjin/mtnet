package com.welling.kinghacker.customView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;


/**
 * Created by zsw on 2016/3/19.
 */
public class BloodPressureView {
    private TextView highPressure,lowPressure,heartBeat,highPressurelong;
    private TextView highPressurelongtwo,lowPressurelongtwo,heartBeatlongtwo;
    private FrameLayout bloodPressureView;
    private LinearLayout linearlong;
    private int highPressureValue,lowPressureValue,heartBeatValue,trueLong;
    private Animation sani,tani;
    public BloodPressureView(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView=inflater.inflate(R.layout.blood_pressure_layout,null);
        highPressure=(TextView)rootView.findViewById(R.id.highPressure);
        lowPressure=(TextView)rootView.findViewById(R.id.lowPressure);
        heartBeat=(TextView)rootView.findViewById(R.id.heartBeat);
        bloodPressureView=(FrameLayout)rootView.findViewById(R.id.bloodPressure);
        linearlong=(LinearLayout)rootView.findViewById(R.id.linearlong);
        highPressurelong=(TextView)rootView.findViewById(R.id.highPressurelong);
        highPressurelongtwo=(TextView)rootView.findViewById(R.id.highPressurelongtwo);
        lowPressurelongtwo=(TextView)rootView.findViewById(R.id.lowPressurelongtwo);
        heartBeatlongtwo=(TextView)rootView.findViewById(R.id.heartBeatlongtwo);
        measuresize();
    }
    private void measuresize(){
        int w=View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h=View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        bloodPressureView.measure(w,h);
        linearlong.measure(w,h);
        highPressurelong.measure(w,h);
        highPressurelongtwo.measure(w,h);
    }
    public FrameLayout getBloodPressureView(){return bloodPressureView;}

    public void setValues(int highPressureValue,int lowPressureValue,int heartBeatValue){
        trueLong=bloodPressureView.getMeasuredWidth()-highPressurelong.getMeasuredWidth()-highPressurelongtwo.getMeasuredWidth();
        highPressurelongtwo.setText(" "+highPressureValue+"(mmHg)");
        lowPressurelongtwo.setText(" " + lowPressureValue + "(mmHg)");
        heartBeatlongtwo.setText(" " + heartBeatValue + "(bpm)");
        Log.i("valuesof", bloodPressureView.getMeasuredWidth() + " <0");
        Log.i("valuesof", linearlong.getMeasuredWidth() + " <1");
        Log.i("valuesof",highPressurelong.getMeasuredWidth()+" <2");
        Log.i("valuesof",highPressurelongtwo.getMeasuredWidth()+" <3");
        this.highPressureValue=(int)(highPressureValue/200.0*trueLong);
        this.lowPressureValue=(int)(lowPressureValue/200.0*trueLong);
        this.heartBeatValue=(int)(heartBeatValue/200.0*trueLong);
        highPressure.getLayoutParams().width=this.highPressureValue;
        lowPressure.getLayoutParams().width=this.lowPressureValue;
        heartBeat.getLayoutParams().width=this.heartBeatValue;
        sani = new ScaleAnimation(0,1,1,1, Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,1.F);
        sani.setDuration(600);
        Log.i("valuesof--1", highPressure.getLayoutParams().width+" "+(float)highPressurelongtwo.getMeasuredWidth());
        Log.i("valuesof->", -highPressure.getLayoutParams().width / ((float) highPressurelongtwo.getMeasuredWidth()) + "");

    }
    public void startAnimation(){
        Log.i("valuesof",trueLong+" |");
        Log.i("valuesof",highPressureValue+" = "+highPressure.getMeasuredWidth());
        Log.i("valuesof", lowPressureValue + " - " + lowPressure.getMeasuredWidth());
        Log.i("valuesof",heartBeatValue+" > "+heartBeat.getMeasuredWidth());
        highPressure.startAnimation(sani);
        lowPressure.startAnimation(sani);
        heartBeat.startAnimation(sani);
        tani=new TranslateAnimation(Animation.RELATIVE_TO_SELF,-highPressure.getLayoutParams().width/((float)highPressurelongtwo.getMeasuredWidth()),
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0);
        tani.setDuration(600);
        highPressurelongtwo.startAnimation(tani);
        tani=new TranslateAnimation(Animation.RELATIVE_TO_SELF,-lowPressure.getLayoutParams().width/((float)lowPressurelongtwo.getMeasuredWidth()),
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0);
        tani.setDuration(600);
        lowPressurelongtwo.startAnimation(tani);
        tani=new TranslateAnimation(Animation.RELATIVE_TO_SELF,-heartBeat.getLayoutParams().width/((float)heartBeatlongtwo.getMeasuredWidth()),
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0);
        tani.setDuration(600);
        heartBeatlongtwo.startAnimation(tani);
    }
}

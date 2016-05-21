package com.welling.kinghacker.customView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;


/**
 * Created by KingHacker on 3/10/2016.
 **/
public class BloodSugerView {
    private TextView shadeLayout,valueLayout,valueView,dateView;
    private FrameLayout bloodSugerView;
    private float bloodSugerValue = 0;
    private TextView textViewDate,textViewTime,textViewAttr;
    private ScaleAnimation down2UpAnimation;
    private View rootView;
    private float defaultBloodSugerWhiteHeight;
    public BloodSugerView(Context context){

        defaultBloodSugerWhiteHeight = context.getResources().getDimension(R.dimen.blood_suger_Height);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         rootView = inflater.inflate(R.layout.blood_suger_layout,null);
        shadeLayout = (TextView)rootView.findViewById(R.id.bloodSugerWhite);
        valueLayout = (TextView)rootView.findViewById(R.id.bloodSugerGreen);
        valueView = (TextView)rootView.findViewById(R.id.bloodSugerValue);

        textViewDate = (TextView)rootView.findViewById(R.id.SugerDate);
        textViewTime = (TextView)rootView.findViewById(R.id.SugerTime);
        textViewAttr = (TextView)rootView.findViewById(R.id.SugerAttr);
        bloodSugerView = (FrameLayout)rootView.findViewById(R.id.bloodSuger);

        shadeLayout.getLayoutParams().height = (int)defaultBloodSugerWhiteHeight;
        valueLayout.getLayoutParams().height = (int)defaultBloodSugerWhiteHeight;
        valueView.getLayoutParams().height = (int)defaultBloodSugerWhiteHeight;
        setBloodSugerValue(0);
    }

    public FrameLayout getBloodSugerView(){
        return bloodSugerView;
    }

    public View getRootView(){
        return rootView;
    }

    public void setBloodSugerValue(float value){
        bloodSugerValue = value;
        valueView.setText(value + "");
        float bloodSugerWhiteHeight;
        if (bloodSugerValue < 20) {
            bloodSugerWhiteHeight = defaultBloodSugerWhiteHeight * (1 - bloodSugerValue / 20);
        }else{
            bloodSugerWhiteHeight = defaultBloodSugerWhiteHeight * (1 - 19f / 20);
        }
        setShadeLayoutHeight((int)bloodSugerWhiteHeight);
        setAnimation();
    }

    public void setBloodSugerTime(String time){
        if (time == null)
        System.out.println("1dd23");
        else{
            System.out.println("12ddd3");
            textViewTime.setText(time);
        }
    }
    public void setBloodSugerDate(String date){
        textViewDate.setText(date);
    }
    public void setBloodSugerAttr(String attr){
        textViewAttr.setText(attr);
    }

    private void setShadeLayoutHeight(int height){
        shadeLayout.getLayoutParams().height = height;
    }
    private void setAnimation(){
        float scale = 10;
        if (bloodSugerValue < 10) {
            scale = 10 / (10 - bloodSugerValue);
        }else{
            scale = 10 / (10 - 9f);
        }
        down2UpAnimation = new ScaleAnimation(1,1,scale,0, Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0);
        down2UpAnimation.setDuration(1000);

        final Animation up2DownAnimation = new ScaleAnimation(1,1,0,1, Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0);
        up2DownAnimation.setDuration(1000);


        down2UpAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                shadeLayout.startAnimation(up2DownAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    public void startAnimation(){
        shadeLayout.startAnimation(down2UpAnimation);
    }

}

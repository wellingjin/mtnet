package com.welling.kinghacker.customView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.tools.SystemTool;


/**
 * Created by KingHacker on 3/10/2016.
 **/
public class BloodSugerView {
    private TextView shadeLayout,valueLayout,valueView;
    private FrameLayout bloodSugerView;
    private float bloodSugerValue = 0;
    private ScaleAnimation down2UpAnimation;
    private float defaultBloodSugerWhiteHeight;
    public BloodSugerView(Context context){

        defaultBloodSugerWhiteHeight = context.getResources().getDimension(R.dimen.blood_suger_Height);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.blood_suger_layout,null);
        shadeLayout = (TextView)rootView.findViewById(R.id.bloodSugerWhite);
        valueLayout = (TextView)rootView.findViewById(R.id.bloodSugerGreen);
        valueView = (TextView)rootView.findViewById(R.id.bloodSugerValue);
        bloodSugerView = (FrameLayout)rootView.findViewById(R.id.bloodSuger);

        shadeLayout.getLayoutParams().height = (int)defaultBloodSugerWhiteHeight;
        valueLayout.getLayoutParams().height = (int)defaultBloodSugerWhiteHeight;
        valueView.getLayoutParams().height = (int)defaultBloodSugerWhiteHeight;
        setBloodSugerValue(0);
    }

    public FrameLayout getBloodSugerView(){
        return bloodSugerView;
    }
    public void setBloodSugerValue(float value){
        bloodSugerValue = value;
        valueView.setText(value + "");
        float bloodSugerWhiteHeight;
        if (bloodSugerValue < 10) {
            bloodSugerWhiteHeight = defaultBloodSugerWhiteHeight * (1 - bloodSugerValue / 10);
        }else{
            bloodSugerWhiteHeight = defaultBloodSugerWhiteHeight * (1 - 9f / 10);
        }
        setShadeLayoutHeight((int)bloodSugerWhiteHeight);
        setAnimation();
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

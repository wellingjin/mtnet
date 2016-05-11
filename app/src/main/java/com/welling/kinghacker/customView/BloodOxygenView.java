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
public class BloodOxygenView{
    private TextView shadeLayout,valueLayout,valueView;
    private FrameLayout bloodOxygenView;
    private float bloodOxygenValue = 0;
    private ScaleAnimation down2UpAnimation;
    private float defaultBloodSugerWhiteHeight;
    public BloodOxygenView(Context context){
        defaultBloodSugerWhiteHeight = context.getResources().getDimension(R.dimen.blood_suger_Height);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.blood_oxygen_layout,null);
        shadeLayout = (TextView)rootView.findViewById(R.id.bloodOxygenWhite);
        valueLayout = (TextView)rootView.findViewById(R.id.bloodOxygenGreen);
        valueView = (TextView)rootView.findViewById(R.id.bloodOxygenValue);
        bloodOxygenView = (FrameLayout)rootView.findViewById(R.id.bloodOxygen);

        shadeLayout.getLayoutParams().height = (int)defaultBloodSugerWhiteHeight;
        valueLayout.getLayoutParams().height = (int)defaultBloodSugerWhiteHeight;
        valueView.getLayoutParams().height = (int)defaultBloodSugerWhiteHeight;
        setBloodOxygenValue(0);
    }

    public FrameLayout getBloodOxygenView(){
        return bloodOxygenView;
    }
    public void setBloodOxygenValue(float value){
        bloodOxygenValue = value;
        valueView.setText(value + "");
        float bloodOxygenWhiteHeight;
        if (bloodOxygenValue < 100) {
            bloodOxygenWhiteHeight = defaultBloodSugerWhiteHeight * (1 - bloodOxygenValue / 100);
        }else{
            bloodOxygenWhiteHeight = defaultBloodSugerWhiteHeight * (1 - 90f / 100);
        }
        setShadeLayoutHeight((int)bloodOxygenWhiteHeight);
        setAnimation();
    }
    private void setShadeLayoutHeight(int height){
        shadeLayout.getLayoutParams().height = height;
    }
    private void setAnimation(){
        float scale = 100;
        if (bloodOxygenValue < 100) {
            scale = 100 / (100 - bloodOxygenValue);
        }else{
            scale = 100 / (100 - 90f);
        }
        down2UpAnimation = new ScaleAnimation(10,10,scale,0, Animation.RELATIVE_TO_SELF,0.5F,Animation.RELATIVE_TO_SELF,0);
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

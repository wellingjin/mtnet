package com.welling.kinghacker.customView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.tools.SystemTool;

/**
 * Created by KingHacker on 3/10/2016.
 **/
public class ActionBarView {
    private ImageView leftButton;
    private ImageView rightButton;
    private TextView titleView;
    private boolean isClick = false;

    private View rootView;
    private OnclickListener onclickListener;
    private boolean isBackEnable = false;
    public ActionBarView(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.actionbar_layout,null);

        leftButton = (ImageView)rootView.findViewById(R.id.button);


        RippleView rippleViewOfPersonInfo =(RippleView)rootView.findViewById(R.id.rippleViewOfPersonInfo);

        int frameRate = 30;
        rippleViewOfPersonInfo.setFrameRate(frameRate);


        rippleViewOfPersonInfo.setOnClickListener(new RippleView.OnClickListener() {

            @Override
            public void onClick(View v) {
                isClick = true;
            }
        });
        rippleViewOfPersonInfo.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                if (isClick) {
                    isClick = false;
                    onclickListener.leftButtonOnClick(isBackEnable);
                }
            }
        });
        titleView = (TextView)rootView.findViewById(R.id.titleOfActionbar);

        rightButton = (ImageView)rootView.findViewById(R.id.buttonMore);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickListener.rightButtonOnClick();
            }
        });

    }
    private void setLeftButtonImage(Drawable image){
        leftButton.setBackground(image);
    }

    public void setTitle(String title){
        titleView.setText(title);
    }

    public View getRootView(){
        return rootView;
    }

    public void setOnclickListener(OnclickListener onclickListener){
        this.onclickListener = onclickListener;
    }
    public interface OnclickListener{
        void leftButtonOnClick(boolean isBack);
        void rightButtonOnClick();
    }
    public void setLeftButtonEnable(boolean enable){
        if (enable){
            leftButton.setVisibility(View.VISIBLE);
        }else {
            leftButton.setVisibility(View.GONE);
        }
        leftButton.setEnabled(enable);
    }
    public boolean isLeftButtonEnable(){
        return leftButton.isEnabled();
    }
    public void setRightButtonEnable(boolean enable){
        if (enable){
            rightButton.setBackground(rootView.getResources().getDrawable(R.drawable.abc_ic_menu_overflow_material));
        }else {
            rightButton.setBackgroundColor(Color.TRANSPARENT);
        }
        rightButton.setEnabled(enable);
    }
    public boolean isRightButtonEnable(){
        return rightButton.isEnabled();
    }

    public void setIsBackEnable(boolean isBackEnable) {
        this.isBackEnable = isBackEnable;
        setLeftButtonImage(rootView.getResources().getDrawable(R.drawable.abc_ic_ab_back_material));
    }
    public void setCustomLeftButton(Drawable backgrand){
        isBackEnable = false;
        setLeftButtonImage(backgrand);
    }
}

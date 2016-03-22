package com.welling.kinghacker.customView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.tools.SystemTool;


/**
 * Created by KingHacker on 3/21/2016.
 */
public class MTToast {
    private Activity context;
    private TextView textView;
    static public int SHORTTIME = 1000,LONGTIME = 2000;
    private int timeStyle;
    private PopupWindow popWind;
    private final Handler handler;
    private ToastStaticListener toastStaticListener;

    public MTToast(Context context){
        this.context = (Activity)context;
        textView = new TextView(context);
        textView.setTextSize(context.getResources().getDimension(R.dimen.normalTextSize));
        textView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        timeStyle = SHORTTIME;
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (popWind.isShowing()) {
                            popWind.dismiss();
                        }
                        toastStaticListener.toastDispeared();
                        break;
                }
                super.handleMessage(msg);
            }

        };
    }
    public void makeText(String text,int timeStyle){
        textView.setText(text);
        this.timeStyle = timeStyle;
    }
    public void showAtView(View view){
        if (popWind == null){
            popWind = new PopupWindow(textView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
            popWind.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
            popWind.setOutsideTouchable(true);
            popWind.setFocusable(false);
            popWind.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画
        }
        popWind.showAtLocation(view, Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, (int)(SystemTool.getSystem(context).getScreenHeight()*2/3));

        startTime();
    }
    private void startTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("TAGGGG","run");
                Message message = new Message( );
                message.what = 1;
                handler.sendMessageDelayed(message,timeStyle);
            }
        }).start();
    }

    public void setToastStaticListener(ToastStaticListener toastStaticListener) {
        this.toastStaticListener = toastStaticListener;
    }

    public interface ToastStaticListener{
        void toastDispeared();
    }
}

package com.welling.kinghacker.customView;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.tools.SystemTool;

/**
 * Created by KingHacker on 4/25/2016.
 * 蓝牙数据获取状态显示
 */
public class MTDialog {
    private int MAX = 102400;
    private AlertDialog alertDialog;
    private TextView stateText,percentText,recText;
    private ProgressBar recBar,progressBar;

    public MTDialog(Context context){
        alertDialog = new AlertDialog.Builder(context).create();
        stateText = new TextView(context);
        show();
        alertDialog.setCanceledOnTouchOutside(false);
        Window rootView = alertDialog.getWindow();
        rootView.setContentView(R.layout.layout_mtdialog);

        stateText = (TextView)rootView.findViewById(R.id.dialogText);
        percentText = (TextView)rootView.findViewById(R.id.pecent);
        recText = (TextView)rootView.findViewById(R.id.recSize);
        recBar = (ProgressBar)rootView.findViewById(R.id.dialogRec);
        progressBar = (ProgressBar)rootView.findViewById(R.id.dialogProgress);
        recBar.setMax(MAX);
//        alertDialog.setOnDismissListener(new                                                                                  );
    }
//    内部方法
    protected void setPercentText(int progress){
        percentText.setText(String.format("%d%%",progress));
    }
    protected void setRecSize(int progress,int max){
        if (max <= 0) max = 1024;
        recText.setText(String.format("%d kb/%d kb",progress,max));

        setPercentText(progress * 100 / max);
    }
    private void show(){
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

//    对外接口
    public void setStateText(String text) {
        stateText.setText(text);
    }
//    设置右边旋转bar的显示
    public void setProgressBarHiden(boolean isHide){
        if (isHide){
            progressBar.setVisibility(View.GONE);
        }else {
            if (progressBar.getVisibility() != View.VISIBLE){
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }
    public void setRecBarHiden(boolean isHide){
        if (isHide){
            recBar.setVisibility(View.GONE);
            percentText.setVisibility(View.GONE);
            recText.setVisibility(View.GONE);
        }else {
            if (recBar.getVisibility() != View.VISIBLE){
                recBar.setVisibility(View.VISIBLE);
                percentText.setVisibility(View.VISIBLE);
                recText.setVisibility(View.VISIBLE);
            }
        }
    }
    public void setProgress(int value){
        if (recBar.getVisibility() == View.VISIBLE){
            recBar.setProgress(value);
            setRecSize(value, MAX);
        }
    }
    public void setMax(int max){
        this.MAX = max;
        recBar.setMax(max);
    }


    public int getMAX() {
        return MAX;
    }
}

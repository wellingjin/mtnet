package com.welling.kinghacker.customView;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.tools.SystemTool;

/**
 * Created by KingHacker on 4/25/2016.
 * 蓝牙数据获取状态显示
 */
public class MTDialog {
    private int MAX = 82400;
    private AlertDialog alertDialog;
    private TextView stateText,percentText,recText;
    private ProgressBar recBar,progressBar;
    private Button cancleButton,comfireButton;
    private boolean isListening = false;
    private OnButtonClickListener onButtonClickListener;

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
        cancleButton = (Button)rootView.findViewById(R.id.cancelUpdate);
        comfireButton = (Button)rootView.findViewById(R.id.updateCloud);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListening){
                    onButtonClickListener.onButtonClick(0);
                }
            }
        });
        comfireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListening){
                    onButtonClickListener.onButtonClick(1);
                }
            }
        });

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
    public void setCancleButtonEnable(boolean enable,String text){
        if (enable) {
            cancleButton.setVisibility(View.VISIBLE);
            cancleButton.setText(text);
        }else {
            cancleButton.setVisibility(View.GONE);
        }
    }
    public void setComfireButtonEnable(boolean enable,String text){
        if (enable) {
            comfireButton.setVisibility(View.VISIBLE);
            comfireButton.setText(text);
        }else {
            comfireButton.setVisibility(View.GONE);
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

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        isListening = true;
        this.onButtonClickListener = onButtonClickListener;
    }
    public void dismiss(){
        if (alertDialog!=null && alertDialog.isShowing())
            alertDialog.dismiss();

    }

    public interface OnButtonClickListener{
        void onButtonClick(int which);
    }
}

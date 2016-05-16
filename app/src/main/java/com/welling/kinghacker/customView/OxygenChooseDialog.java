package com.welling.kinghacker.customView;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;

/**
 * Created by li on 2016/5/14.
 */
public class OxygenChooseDialog {
    private AlertDialog alertDialog;
    private Button AutomeButton,HandButton;
    private boolean isListening = false;
    private OnButtonClickListener onButtonClickListener;
    public OxygenChooseDialog(Context context){
        alertDialog = new AlertDialog.Builder(context).create();
        show();
        alertDialog.setCanceledOnTouchOutside(false);
        Window rootView = alertDialog.getWindow();
        rootView.setContentView(R.layout.layout_blood_oxygen_choosedialog);

        AutomeButton = (Button)rootView.findViewById(R.id.updatebyautome);
        HandButton = (Button)rootView.findViewById(R.id.updatebyhand);
        AutomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListening){
                    onButtonClickListener.onButtonClick(0);
                }
            }
        });
        HandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListening){
                    onButtonClickListener.onButtonClick(1);
                }
            }
        });
//        alertDialog.setOnDismissListener(new                                                                                  );
    }
    private void show(){
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
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

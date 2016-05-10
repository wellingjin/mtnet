package com.welling.kinghacker.customView;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;

/**
 * Created by li on 2016/5/7.
 */
public class OxygenMTDialog {
    private AlertDialog alertDialog;
    private TextView titleView,bloodOxygen;
    private EditText editText;
    private Button cancleButton,comfireButton;
    private boolean isListening = false;
    private OnButtonClickListener onButtonClickListener;
    public OxygenMTDialog(Context context){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setView(new EditText(context));
        show();
        alertDialog.setCanceledOnTouchOutside(false);
        Window rootView = alertDialog.getWindow();
        rootView.setContentView(R.layout.layout_blood_oxygen_mtdialog);

        bloodOxygen = (TextView)rootView.findViewById(R.id.dialogText);
        titleView = (TextView)rootView.findViewById(R.id.titleTextView);
        editText = (EditText)rootView.findViewById(R.id.editText);
        editText.setEnabled(true);

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
    public String getText(){
        return  editText.getText().toString();
    }
}

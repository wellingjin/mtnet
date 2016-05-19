package com.welling.kinghacker.customView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by KingHacker on 3/15/2016.
**/
public class FilterView {
    private int yOffset = 0,xOffset = 0;
    private PopupWindow popWind;
    private View rootView;
    private Context context;
    private TextView endDate,startDate;
    private boolean isListening = false;
    private Button recentOneWeekButton,recentOneMonthButton,recentThreeMonthButton,sureButton;
    private OnButtonClickListener onButtonClickListener;
    public FilterView(final Context context){
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.filter_layout,null);
        endDate = (TextView)rootView.findViewById(R.id.endDate);
        recentOneWeekButton = (Button)rootView.findViewById(R.id.recentOneWeek);

        recentOneMonthButton = (Button)rootView.findViewById(R.id.recentOneMonth);

        recentThreeMonthButton = (Button)rootView.findViewById(R.id.recentThreeMonth);

        sureButton = (Button)rootView.findViewById(R.id.sure);
        recentOneWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isListening){
                    onButtonClickListener.onButtonClick(0);
                }
            }
        });
        recentOneMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isListening){
                    onButtonClickListener.onButtonClick(1);
                }
            }
        });
        recentThreeMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isListening){
                    onButtonClickListener.onButtonClick(2);
                }
            }
        });
        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isListening){
                    onButtonClickListener.onButtonClick(3);
                }
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog pickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDate.setText(year + "-" + (monthOfYear+1) +"-" + dayOfMonth);
                    }
                },2016,4,14);
                pickerDialog.show();
                ;
            }
        });
        startDate = (TextView)rootView.findViewById(R.id.startDate);
        startDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDate.setText(year + "-" + (monthOfYear+1) +"-" + dayOfMonth);
                    }
                },2016,4,13).show();
            }
        });
    }
    public void changeToBOValue(){
        recentOneWeekButton.setText("最近一天");
        recentOneMonthButton.setText("最近两天");
        recentThreeMonthButton.setText("最近三天");
    }
    public void showFilter(View parentView) {
        if (popWind == null) {
            popWind = new PopupWindow(rootView,
                    TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, true);
            popWind.setBackgroundDrawable(context.getResources().getDrawable(R.color.colorAccent));
            popWind.setOutsideTouchable(true);
            popWind.setAnimationStyle(android.R.style.Animation_Translucent);    //设置一个动画
        }
        popWind.showAtLocation(parentView, Gravity.CENTER, xOffset, yOffset);
    }
    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        isListening = true;
        this.onButtonClickListener = onButtonClickListener;
    }
    public void dismiss(){
        if(popWind!=null){
            popWind.dismiss();
            popWind = null;
        }
    }
    public String getStartTime(){
        return startDate.getText().toString();
    }
    public String getEndTime(){
        return endDate.getText().toString();
    }
    public interface OnButtonClickListener{
        void onButtonClick(int which);
    }

}

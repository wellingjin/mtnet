package com.welling.kinghacker.customView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
    public FilterView(final Context context){
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.filter_layout,null);
        endDate = (TextView)rootView.findViewById(R.id.endDate);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog pickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDate.setText(year + "-" + (monthOfYear+1) +"-" + dayOfMonth);
                    }
                },2016,2,3);
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
                },2016,2,3).show();
            }
        });
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


}

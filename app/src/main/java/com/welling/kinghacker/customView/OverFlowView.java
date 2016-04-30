package com.welling.kinghacker.customView;

import android.content.Context;

import android.view.Gravity;
import android.view.View;

import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.tools.FontTool;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;


/**
 * Created by KingHacker on 3/4/2016.
 *
 */
public class OverFlowView extends TableLayout{
    final static public int NONE = 0;
    Context context;
    int yOffset = 0,xOffset = 0;
    private PopupWindow popWind;
    int rowNum = 0;
    private  OnRowClickListener onRowClickListener;
    public OverFlowView(Context context){
        super(context);
        this.context = context;
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);

    }
//    新增一项
    public void addItem(int iconId,String text){

        if (rowNum > 0){
            TableRow view = new TableRow(context);
            view.setMinimumHeight(1);
            view.setBackgroundColor(SystemTool.getSystem(context).getXMLColor(R.color.colorHint));
            addView(view);
        }
        rowNum++;
        TableRow row = new TableRow(context);
        if (iconId != NONE) {
            ImageView icon = new ImageView(context);
            icon.setImageResource(iconId);
            row.addView(icon);
        }
        TextView textView = new TextView(context) ;
        textView.setTag("text");
        textView.setText(text);
        textView.setTextSize(SystemTool.getSystem(context).PxToDp(context.getResources().getDimension(R.dimen.item_size)));
        textView.setTextColor(SystemTool.getSystem(context).getXMLColor(R.color.colorWhite));
        int textWidth = (int) SystemTool.getSystem(context).adaptation1080((new FontTool(context).getTextWidth(textView)) + PublicRes.getInstance().overFlowItemOffset);
        textView.setWidth(textWidth);

        row.setBackground(SystemTool.getSystem(context).getXMLDrawable(R.drawable.bt_selector));
        row.addView(textView);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams lp = new LayoutParams();
        int marginLeft = (int)SystemTool.getSystem(context).adaptation1080(PublicRes.getInstance().marginLeft);
        int marginRight = (int)SystemTool.getSystem(context).adaptation1080(PublicRes.getInstance().marginRight);
        int marginTop = (int)SystemTool.getSystem(context).adaptation1080(PublicRes.getInstance().marginTop);
        int marginBottom = (int)SystemTool.getSystem(context).adaptation1080(PublicRes.getInstance().marginBottom);
        lp.setMargins(marginLeft,marginTop,marginRight,marginBottom);
        row.setLayoutParams(lp);
        addView(row);
        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onRowClickListener.onClick(((TextView) v.findViewWithTag("text")).getText().toString());
                if (popWind != null && popWind.isShowing()){
                    popWind.dismiss();
                }
            }
        });
    }
    public void showOverFlow(View parentView){
        if (popWind == null){
        popWind = new PopupWindow(this,
                         LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popWind.setBackgroundDrawable(SystemTool.getSystem(context).getXMLDrawable(R.color.colorPrimary));
        popWind.setOutsideTouchable(true);
        popWind.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画
        }
        if (parentView != null) {
            popWind.showAtLocation(parentView, Gravity.RIGHT | Gravity.TOP, xOffset, yOffset);
        }
    }
    public void setOffset(int x,int y){
        xOffset = x;
        yOffset = y;
    }
    public  void setOnRowClickListener(OnRowClickListener onRowClickListener){
        this.onRowClickListener = onRowClickListener;
    }
    public interface OnRowClickListener{
         void onClick(String text);
    }


}

package com.welling.kinghacker.tools;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

/**
 * Created by KingHacker on 3/3/2016.
 */
public class FontTool {
    Context context;
    public FontTool(Context context){
        this.context = context;
    }
//    获取字符串宽度
    public float getTextWidth(String text, float textSize){
//        TextPaint paint = new TextPaint();
//        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
//        paint.setTextSize(scaledDensity * textSize);
//        return paint.measureText(text);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(textSize);
        return getTextWidth(textView);
    }
//获取textView内容宽度
    public float getTextWidth(TextView textView){
        if (textView == null) return 0;
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(spec, spec);
        TextPaint textPaint = textView.getPaint();
        float textWidth = textPaint.measureText(textView.getText().toString());
        if (textWidth <= 0) return getTextWidth(textView.getText().toString(),textView.getTextSize());
        return textWidth;
    }
//    获取字符高度
    public float getTextHeight(float fontSize){
            Paint paint = new Paint();
            paint.setTextSize(fontSize);
            Paint.FontMetrics fm = paint.getFontMetrics();
            return (int)Math.ceil(fm.descent - fm.top) + 2;
    }
    public int getViewHeight(View view){
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(h, h);
        int viewHeight = view.getMeasuredHeight();
        if (viewHeight == 0) return view.getHeight();
        return viewHeight;
    }
    public int getViewWidth(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, w);
        return view.getMeasuredWidth();
    }
}

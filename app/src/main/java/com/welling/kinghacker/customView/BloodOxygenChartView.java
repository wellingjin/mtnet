package com.welling.kinghacker.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.welling.kinghacker.bean.OxygenDataRecord;

public class BloodOxygenChartView extends View{
    public int bloodOxyData[] = null;
    private Context context;
    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private float xratio,yratio;
    public BloodOxygenChartView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        //将最近几次的数据显示出来更新数据
        bloodOxyData = new int[7];
        for(int i=0;i<7;i++) bloodOxyData[i] = 90;
        OxygenDataRecord oxygenDataRecord = new OxygenDataRecord(context);
        bloodOxyData = oxygenDataRecord.getRecentlySevenData();
    }
    public BloodOxygenChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        xratio=MeasureSpec.getSize(widthMeasureSpec)/1080f;
        yratio=MeasureSpec.getSize(heightMeasureSpec)/1590f;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(!mPath.isEmpty()){
            mPath.rewind();
        }
        mPaint.setAntiAlias(true);

        float perHeight=1390/15*yratio;
        float perWidth=880/8*xratio;
        float startX=100*xratio,startY=1540*yratio;
        mPaint.setTextSize(30 * xratio);

        //设置背景
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(Color.DKGRAY);
        mPaint.setAlpha(128);
        canvas.drawRect(0, 0, 1080 * xratio, 1590 * yratio, mPaint);
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setAlpha(255);
        Path path=new Path();
        canvas.drawLine(startX, startY-perHeight*16, startX, startY-perHeight, mPaint);
        startY-=perHeight;
        for(int i=85;i<100;i++){
            canvas.drawLine(startX, startY, startX+20*xratio, startY, mPaint);
            mPaint.setTextSize(30*xratio);
            canvas.drawText(i+"", startX-60*xratio, startY, mPaint);
            startY-=perHeight;
        }
        path.moveTo(startX, startY);
        path.lineTo(startX-10*xratio, startY+perHeight/2);
        path.lineTo(startX + 10 * xratio, startY + perHeight / 2);
        path.close();
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(Color.BLACK);
        canvas.drawPath(path, mPaint);
        canvas.drawText("(SpO2)", startX+20*xratio, startY+10*yratio, mPaint);
        startX=100*xratio;
        startY=1540*yratio-perHeight;

        canvas.drawLine(startX, startY, startX+perWidth*8, startY, mPaint);
        startX+=perWidth;
        for(int i=0;i<7;i++){
            canvas.drawLine(startX, startY, startX, startY-30*yratio, mPaint);
            mPaint.setTextSize(30*xratio);
            canvas.drawText("前"+i+"次", startX-15*xratio, startY+60*yratio, mPaint);
            startX+=perWidth;
        }
        path.moveTo(startX, startY);
        path.lineTo(startX-perHeight/2, startY-10*xratio);
        path.lineTo(startX - perHeight / 2, startY + 10 * xratio);
        path.close();
        canvas.drawPath(path, mPaint);
        canvas.drawText("(次数)", startX-40*xratio, startY+60*yratio, mPaint);

        startX=100*xratio;
        startY=1540*yratio-perHeight;
        mPath.moveTo(startX+perWidth,startY-(bloodOxyData[0]-85)*perHeight);
        canvas.drawCircle(startX+perWidth, startY-(bloodOxyData[0]-85)*perHeight, 10*xratio, mPaint);
        for(int i=1;i<bloodOxyData.length;i++){
            mPath.cubicTo(startX+perWidth*i+20,startY-(bloodOxyData[i-1]-85)*perHeight
                    , startX+perWidth*(i+1)-20,startY-(bloodOxyData[i]-85)*perHeight
                    , startX+perWidth*(i+1),startY-(bloodOxyData[i]-85)*perHeight);
            canvas.drawCircle(startX+perWidth*(i+1), startY-(bloodOxyData[i]-85)*perHeight,10*xratio, mPaint);
        }
        mPaint.setStyle(Style.STROKE);
        canvas.drawPath(mPath, mPaint);
    }

}

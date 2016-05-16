package com.welling.kinghacker.customView;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.welling.kinghacker.bean.OxygenDataRecord;

import org.json.JSONObject;

public class BloodOxygenChartView extends View implements View.OnTouchListener,GestureDetector.OnGestureListener{
    public int bloodOxyData[] = null,bloodOxyData1[] = null;
    public String bloodUpdateTime[] = null,bloodUpdateTime1[] = null;
    private Context context;
    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private float xratio,yratio;
    public static int numberOfData = 10;
    public int leftI,rightI;
    public GestureDetector mGestureDetector =null;
    public BloodOxygenChartView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.setClickable(true);
        this.setOnTouchListener(this);
        //将最近几次的数据显示出来更新数据
        mGestureDetector = new GestureDetector(this.context,this);
        initDate();
    }
    public void initDate(){
        //存储数据
        bloodOxyData1 = new int[numberOfData];
        bloodUpdateTime1 =new String [numberOfData];
        for(int i=0;i<numberOfData;i++) bloodOxyData1[i] = 100-i;
        OxygenDataRecord oxygenDataRecord = new OxygenDataRecord(context,numberOfData);
        bloodOxyData1 = oxygenDataRecord.getRecentlyMoreData();
        bloodUpdateTime1 = oxygenDataRecord.getRecentlyMoreTime();
        //展示的数据
        leftI = 0;
        rightI = 6;
        setData(leftI, rightI);
    }
    public void initDateAnother(String endTime){
        //存储数据
        bloodOxyData1 = new int[numberOfData];
        bloodUpdateTime1 =new String [numberOfData];
        for(int i=0;i<numberOfData;i++) bloodOxyData1[i] = 100-i;
        OxygenDataRecord oxygenDataRecord = new OxygenDataRecord(context,numberOfData);
        bloodOxyData1 = oxygenDataRecord.getRecentlyMoreChooseData(endTime);
        bloodUpdateTime1 = oxygenDataRecord.getRecentlyMoreTime();
        //展示的数据
        leftI = 0;
        rightI = 6;
        setData(leftI,rightI);
    }
    public void setData(int leftI,int rightI){
        bloodOxyData = new int[7];
        bloodUpdateTime = new String [7];
        for(int i=leftI,j=0;i<rightI+1;i++,j++) {
            if(i<numberOfData) {
                bloodOxyData[j] = bloodOxyData1[i];
                bloodUpdateTime[j] = bloodUpdateTime1[i];
            }
            else{
                bloodOxyData[j] = 0;
                bloodUpdateTime[j]=null;
            }
        }
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
            canvas.drawLine(startX, startY, startX, startY - 30 * yratio, mPaint);
            mPaint.setTextSize(25 * xratio);
            if(bloodUpdateTime[i]!=null) {
                canvas.drawText(bloodUpdateTime[i].split("日")[1], startX - 50 * xratio, startY + 60 * yratio, mPaint);
                canvas.drawText((bloodUpdateTime[i].split("日")[0]).split("年")[1], startX - 50 * xratio, startY + 120 * yratio, mPaint);
            }
            startX+=perWidth;
        }
        path.moveTo(startX, startY);
        path.lineTo(startX-perHeight/2, startY-10*xratio);
        path.lineTo(startX - perHeight / 2, startY + 10 * xratio);
        path.close();
        canvas.drawPath(path, mPaint);
        canvas.drawText("(时间)", startX-40*xratio, startY+60*yratio, mPaint);

        startX=100*xratio;
        startY=1540*yratio-perHeight;
        mPath.moveTo(startX+perWidth,startY-(bloodOxyData[0]-85)*perHeight);
        canvas.drawCircle(startX+perWidth, startY-(bloodOxyData[0]-85)*perHeight, 10*xratio, mPaint);
        for(int i=1;i<bloodOxyData.length;i++){
            if(bloodOxyData[i]!=0) {
                mPath.cubicTo(startX + perWidth * i + 20, startY - (bloodOxyData[i - 1] - 85) * perHeight
                        , startX + perWidth * (i + 1) - 20, startY - (bloodOxyData[i] - 85) * perHeight
                        , startX + perWidth * (i + 1), startY - (bloodOxyData[i] - 85) * perHeight);
                canvas.drawCircle(startX + perWidth * (i + 1), startY - (bloodOxyData[i] - 85) * perHeight, 10 * xratio, mPaint);
            }
        }
        mPaint.setStyle(Style.STROKE);
        canvas.drawPath(mPath, mPaint);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX()-e2.getX() > 20){//向左滑动
            if(rightI<numberOfData-1) {//右边还有数据
                rightI += 1;
                leftI += 1;
                setData(leftI,rightI);
                this.invalidate();
            }
        }
        if(e2.getX()-e1.getX() > 20){//向右滑动
            if(leftI>=1) {//左边还有数据
                rightI -= 1;
                leftI -= 1;
                setData(leftI,rightI);
                this.invalidate();
            }
        }
        return false;
    }
}

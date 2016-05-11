package com.welling.kinghacker.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.creative.base.BaseDate;

import java.util.Vector;

public class BloodOxygenUpload extends View{
    private Paint paint = null;
    private float xratio,yratio;
    public float dataOfPi=60;
   // public Vector<Point> vectorOfPoint = null;
    public Vector<BaseDate.Wave> waves = null;
    public BloodOxygenUpload(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

    }
    public BloodOxygenUpload(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        waves = new Vector<>();
    }@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        xratio=MeasureSpec.getSize(widthMeasureSpec)/760f;
        yratio=MeasureSpec.getSize(heightMeasureSpec)/420f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        paint.setStyle(Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setAlpha(100);
        canvas.drawRect(10*xratio,0,670*xratio,360*yratio,paint);
        paint.setColor(Color.BLACK);
        paint.setAlpha(255);
        for(int i=0;i<7;i++)
            canvas.drawLine(10*xratio,i*60*yratio,670*xratio,i*60*yratio,paint);
        for(int i=0;i<12;i++)
            canvas.drawLine(i*60*xratio+10*xratio,0,i*60*xratio+10*xratio,360*yratio,paint);
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE);
        canvas.drawRect(680 * xratio, 60 * yratio, 740 * xratio, 360 * yratio, paint);
        paint.setStyle(Style.FILL);
        paint.setColor(Color.GREEN);
        canvas.drawRect(683 * xratio,  (300-dataOfPi*2+60)*yratio , 737 * xratio, 360 * yratio, paint);
        paint.setColor(Color.RED);
        if(waves.size()>2) {
            float lastX = (waves.size()*8) * xratio + 10 * xratio;
            float lastY = (360 - waves.get(0).data * 3/2) * yratio;
            for (int i = 1; i < waves.size(); i++) {
                BaseDate.Wave wave = waves.get(i);
                float currentX = (waves.size() - i)*8 * xratio + 10 * xratio;
                float currentY = (360 - wave.data*3/2 ) * yratio;
                paint.setColor(Color.RED);
                if(lastX<670*xratio)
                    canvas.drawLine(lastX,lastY,currentX,currentY,paint);
                lastX = currentX;
                lastY = currentY;
            }
        }
    }
    public void addItem(BaseDate.Wave wave){
        waves.add(wave);
    }
}

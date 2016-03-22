package com.welling.kinghacker.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zsw on 2015/12/15.
 */
public class LineBlood extends View {
    Paint paint;
    float xratio=1,yratio=1;
    int xstart,xshow=0;//x轴开始的位置
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x=(int)event.getX();
        xshow=0;
        if(x<xstart+60*xratio)xshow=0;
        else if(x<xstart+180*xratio)xshow=1;
        else if(x<xstart+300*xratio)xshow=2;
        else if(x<xstart+420*xratio)xshow=3;
        else if(x<xstart+540*xratio)xshow=4;
        else if(x<xstart+660*xratio)xshow=5;
        else if(x<xstart+780*xratio)xshow=6;
        else xshow=7;
        this.invalidate();
        Log.i("valuesofx",x+"");
        return false;

    }

    public LineBlood(Context context) {
        super(context);

    }

    public LineBlood(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        xratio=MeasureSpec.getSize(widthMeasureSpec)/1080f;//是以1080来画的
        yratio=MeasureSpec.getSize(heightMeasureSpec)/1590f;
        Log.i("widgt's position4",xratio+" "+yratio);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        RectF rect=new RectF(0,0,300,300);
//        canvas.drawArc(rect,0,90,false,paint);
//        canvas.drawText("hello world",0,300,paint);
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF000000);
        canvas.drawColor(0xffefefef);
        Path path=new Path();
        float x[]=new float[]{230*xratio,0,0,0,0,0,0};//星期几的分隔
        float y[]=new float[]{98,108,129,112,138,143,105};//高血压
        float y1[]=new float[]{82,77,90,88,75,92,80};//低血压
        float y2[]=new float[]{58,62,68,72,63,66,69};//心率
        for(int i=1;i<x.length;i++)x[i]=x[i-1]+120*xratio;

        paint.setStrokeWidth(5*xratio);
        paint.setStyle(Paint.Style.FILL);
        path.moveTo(110*xratio, 140*yratio);//y轴上的箭头，此处是最上面的点
        path.lineTo(100*xratio, 150*yratio);
        path.lineTo(120*xratio,150*yratio);
        path.lineTo(110*xratio, 140*yratio);
        canvas.drawPath(path, paint);

        paint.setTextSize(30*xratio);
        canvas.drawText("血压(mmHg)", 150*xratio, 170 * yratio, paint);
        canvas.drawText("心率(次/min)", 150*xratio, 230*yratio, paint);
        int yvalue=(int)(200*yratio),ytext=200;
        for(int i=0;i<11;i++){
            if(i<10)canvas.drawLine(110*xratio,yvalue+50*yratio,130*xratio,yvalue+50*yratio,paint);
            canvas.drawText(ytext+"",35*xratio,yvalue+65*yratio,paint);
            yvalue+=110*yratio;ytext-=20;
        }//从此处可以得出：yvalue-60*yratio则为0点，间隔为110*yratio
        canvas.drawLine(110*xratio,150*yratio,110*xratio,yvalue-50*yratio,paint);//y轴
        int xvalue=(int)(110*xratio);
        xstart=xvalue;
        Rect rect = new Rect();
        paint.getTextBounds("星期一", 0, "星期一".length(), rect);

        for(int i=1;i<=8;i++){
            canvas.drawLine(xvalue, yvalue - 65 * yratio, xvalue, yvalue - 55 * yratio, paint);
            xvalue+=(120*xratio);
            if(i==1)canvas.drawText("星期一",xvalue-rect.width()/2,yvalue+20*yratio,paint);
            else if(i==3)canvas.drawText("星期三",xvalue-rect.width()/2,yvalue+20*yratio,paint);
            else if(i==5)canvas.drawText("星期五",xvalue-rect.width()/2,yvalue+20*yratio,paint);
            else if(i==7)canvas.drawText("星期日",xvalue-rect.width()/2,yvalue+20*yratio,paint);
            if(xshow==i){
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2 * xratio);
                canvas.drawLine(xvalue, yvalue - 65 * yratio, xvalue, yvalue - 1100 * yratio, paint);
                PathEffect effect = new DashPathEffect(new float[] {3*xratio,5*xratio},1);
                paint.setPathEffect(effect);
                path=new Path();
                path.moveTo(xstart, yvalue - 60 * yratio - 110 * yratio / 20 * y[i - 1]);
                path.lineTo(xvalue, yvalue - 60 * yratio - 110 * yratio / 20 * y[i - 1]);
                canvas.drawPath(path, paint);
                path.close();
                path.moveTo(xstart, yvalue - 60 * yratio - 110 * yratio / 20 * y1[i - 1]);
                path.lineTo(xvalue, yvalue - 60 * yratio - 110 * yratio / 20 * y1[i - 1]);
                canvas.drawPath(path, paint);
                path.close();
                path.moveTo(xstart, yvalue - 60 * yratio - 110 * yratio / 20 * y2[i - 1]);
                path.lineTo(xvalue, yvalue - 60 * yratio - 110 * yratio / 20 * y2[i - 1]);
                canvas.drawPath(path, paint);
                paint.setStyle(Paint.Style.FILL);
            }
        }
        paint.setPathEffect(null);
        paint.setStrokeWidth(5*xratio);
        if(xshow!=0){
            canvas.drawText((int)(y[xshow-1])+"", xvalue - 600 * xratio, yvalue + 100 * yratio, paint);
            canvas.drawText((int)(y1[xshow-1])+"",xvalue - 350*xratio,yvalue + 100*yratio,paint);
            canvas.drawText((int)(y2[xshow-1])+"",xvalue - 125*xratio,yvalue + 100*yratio,paint);
        }
        canvas.drawLine(110 * xratio, yvalue - 55 * yratio, xvalue - 40 * xratio, yvalue - 55 * yratio, paint);//x轴
        canvas.drawText("(时间)", xvalue - 100 * xratio, yvalue - 100 * yratio, paint);
        canvas.drawText("高血压:", xvalue - 700 * xratio, yvalue + 100 * yratio, paint);
        canvas.drawText("低血压:", xvalue - 450 * xratio, yvalue + 100 * yratio, paint);
        canvas.drawText("心率:", xvalue - 200 * xratio, yvalue + 100 * yratio, paint);
        path.moveTo(xvalue - 30 * xratio, yvalue - 55 * yratio);//x轴上的箭头
        path.lineTo(xvalue - 40 * xratio, yvalue - 65 * yratio);
        path.lineTo(xvalue - 40 * xratio, yvalue - 45 * yratio);
        path.lineTo(xvalue - 30 * xratio, yvalue - 55 * yratio);
        canvas.drawPath(path, paint);

        path=new Path();
        paint.setColor(0xff066E46);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(30);
        for(int i=0;i<x.length;i++) {//将各部分数据转换成在此画布下的坐标
            y[i] = yvalue - 60 * yratio - 110 * yratio / 20 * y[i];
            y1[i] = yvalue - 60 * yratio - 110 * yratio / 20 * y1[i];
            y2[i] = yvalue - 60 * yratio - 110 * yratio / 20 * y2[i];
        }
        path.moveTo(x[0], y[0]);
        for(int i=1;i<x.length;i++){
            path.cubicTo(x[i - 1] + (float)(0.4*(x[i]-x[i-1])), y[i - 1], x[i] - (float)(0.4*(x[i]-x[i-1])), y[i], x[i], y[i]);
        }
        canvas.drawPath(path, paint);
        //canvas.drawLine(xvalue - 820*xratio, yvalue + 100*yratio, xvalue - 700*xratio, yvalue + 100*yratio, paint);

        path=new Path();
        paint.setColor(0xffFA9D29);
        paint.setTextSize(30);
        path.moveTo(x[0], y1[0]);
        for(int i=1;i<x.length;i++){
            path.cubicTo(x[i - 1] + (float)(0.4*(x[i]-x[i-1])), y1[i - 1], x[i] - (float)(0.4*(x[i]-x[i-1])), y1[i], x[i], y1[i]);
        }
        canvas.drawPath(path, paint);
        //canvas.drawLine(xvalue - 570*xratio, yvalue + 100*yratio, xvalue - 450*xratio, yvalue + 100*yratio, paint);

        path=new Path();
        paint.setColor(0xffFF3146);
        path.moveTo(x[0], y2[0]);
        for(int i=1;i<x.length;i++){
            path.cubicTo(x[i - 1] + (float)(0.4*(x[i]-x[i-1])), y2[i - 1], x[i] - (float)(0.4*(x[i]-x[i-1])), y2[i], x[i], y2[i]);
        }
        canvas.drawPath(path, paint);
        //canvas.drawLine(xvalue - 320*xratio, yvalue + 100*yratio, xvalue - 200*xratio, yvalue + 100*yratio, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff457DD7);
        for(int i=0;i<x.length; i++) {
            canvas.drawCircle(x[i],y[i],10*xratio,paint);
        }

        paint.setColor(0xff00ff00);
        for(int i=0;i<x.length; i++) {
            canvas.drawCircle(x[i],y1[i],10*xratio,paint);
        }

        paint.setColor(0xff0393BC);
        for(int i=0;i<x.length; i++) {
            canvas.drawCircle(x[i],y2[i],10*xratio,paint);
        }

    }

}

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

import com.welling.kinghacker.bean.BloodPressureBean;

import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by zsw on 2015/12/15.
 */
public class LineBlood extends View {
    Paint paint;
    float xratio=1,yratio=1;
    int xstart,xshow=1;//x轴开始的位置
    private static float y[]=new float[7],y1[]=new float[7],y2[]=new float[7];
    private static float y_t[]=new float[7],y1_t[]=new float[7],y2_t[]=new float[7];
    private static String dates[]=new String[]{"","","","","","",""};
    private static int heart_pro[]=new int[]{0,0,0,0,0,0,0};
    private static int isupdate[]=new int[]{0,0,0,0,0,0,0};
    private static JSONObject jsonObject=null;
    private static int scale=1;
    public String startTime,endTime;
    public static int count=0;
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
        Log.i("valuesofx", x + "");
        return false;
    }

    public LineBlood(Context context) {
        super(context);
        initdata(context);
    }

    public LineBlood(Context context, AttributeSet attrs) {
        super(context, attrs);
        initdata(context);
    }
    public LineBlood(Context context,String sTime,String eTime,int numofday){
        super(context);
        BloodPressureBean bpbean=new BloodPressureBean(context);
        jsonObject=bpbean.pickDayRecordFromlocal(sTime,eTime,numofday);
        startTime=bpbean.startTime.split(" ")[0];
        endTime=bpbean.endTime.split(" ")[0];
        setdatafromjson();
    }

    public void initdata(Context context){
        BloodPressureBean bpbean=new BloodPressureBean(context);
        jsonObject=bpbean.setWeekRecordFromlocal();
        startTime=bpbean.startTime.split(" ")[0];
        endTime=bpbean.endTime.split(" ")[0];
        setdatafromjson();
        Log.i("database", "one");
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        xratio=MeasureSpec.getSize(widthMeasureSpec)/1080f;//是以1080来画的
        yratio=MeasureSpec.getSize(heightMeasureSpec)/1590f;
        Log.i("widgt's position4",xratio+" "+yratio);
    }

    private static void setdatafromjson(){
        int i=0;
        try{
            count=(int)jsonObject.get("count");
            Log.i("database","LineBlood_count="+count);
            JSONObject temp=null;
            int start=0,end=0;
            if(count==0){
                y[0]=0;y1[0]=0;y2[0]=0;
            }
            else{
                if(count<=7*scale){
                    start=7*(scale-1);end=count;
                }else{
                    start=7*(scale-1);end=7*scale;
                }
                Log.i("database","start="+start+" end="+end);
                for(i=start;i<end;i++){
                    Log.i("database","what_happen_0");
                    temp=(JSONObject)jsonObject.get(i+"");
                    Log.i("database","temp="+temp.toString());
                    y[i%7]=Float.parseFloat((String) temp.get("highblood"));
                    y1[i%7]=Float.parseFloat((String) temp.get("lowblood"));
                    y2[i%7]=Float.parseFloat((String) temp.get("heartrate"));
                    dates[i%7]=(String)temp.get("time");
                    heart_pro[i%7]=Integer.parseInt((String)temp.get("heartproblem"));
                    isupdate[i%7]=Integer.parseInt((String)temp.get("isupdate"));
                }
                Log.i("database","exit");
            }
            for(int j=i;j<7*scale;j++){
                y[j%7]=0;y1[j%7]=0;y2[j%7]=0;
                dates[j%7]="";
                Log.i("database","y="+y[j%7]+" y1="+y1[j%7]+" y2="+y2[j%7]);
            }
        }catch (Exception e)
        {
            Log.i("database","what happen_1");
            e.printStackTrace();
        }
    }
    public static void previous_page(){
        if(scale>1){
            scale-=1;
            setdatafromjson();
        }
    }
    public static void next_page(){
        try{
            int count=(int)jsonObject.get("count");
            if(count/7+1>scale){
                scale+=1;
                setdatafromjson();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF000000);//设置画笔为黑色
        canvas.drawColor(0xffefefef);//背景色
        Path path=new Path();
        float x[]=new float[]{230*xratio,0,0,0,0,0,0};//星期几的分隔

        for(int i=1;i<x.length;i++)x[i]=x[i-1]+120*xratio;

        paint.setStrokeWidth(5*xratio);
        paint.setStyle(Paint.Style.FILL);
        path.moveTo(110*xratio, 190*yratio);//y轴上的箭头，此处是最上面的点
        path.lineTo(100*xratio, 200*yratio);
        path.lineTo(120*xratio,200*yratio);
        path.lineTo(110*xratio, 190*yratio);
        canvas.drawPath(path, paint);

        paint.setTextSize(30*xratio);
        canvas.drawText("血压(mmHg)", 150*xratio, 220 * yratio, paint);
        canvas.drawText("心率(次/min)", 150*xratio, 280*yratio, paint);
        int yvalue=(int)(250*yratio),ytext=200;//这里修改200的值
        for(int i=0;i<11;i++){
            if(i<10)canvas.drawLine(110*xratio,yvalue+50*yratio,130*xratio,yvalue+50*yratio,paint);
            canvas.drawText(ytext+"",35*xratio,yvalue+65*yratio,paint);
            yvalue+=110*yratio;ytext-=20;
        }//从此处可以得出：yvalue-60*yratio则为0点，间隔为110*yratio
        canvas.drawLine(110*xratio,200*yratio,110*xratio,yvalue-50*yratio,paint);//y轴
        int xvalue=(int)(110*xratio);
        xstart=xvalue;
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, "1".length(), rect);

        for(int i=1;i<=8;i++){
            canvas.drawLine(xvalue, yvalue - 65 * yratio, xvalue, yvalue - 55 * yratio, paint);
            xvalue+=(120*xratio);
            if(i<8)canvas.drawText(""+(7*(scale-1)+i),xvalue-rect.width()/2,yvalue+20*yratio,paint);
            if(xshow==i){
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2 * xratio);
                canvas.drawLine(xvalue, yvalue - 65 * yratio, xvalue, yvalue - 1150 * yratio, paint);
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
        paint.setStrokeWidth(5 * xratio);
        if(xshow!=0){
            canvas.drawText((int) (y[xshow - 1]) + "", xvalue - 600 * xratio, yvalue + 100 * yratio, paint);
            canvas.drawText((int) (y1[xshow - 1]) + "", xvalue - 350 * xratio, yvalue + 100 * yratio, paint);
            paint.setColor(0xff000000);if(heart_pro[xshow-1]==1)paint.setColor(0xffDA413E);
            canvas.drawText((int) (y2[xshow - 1]) + "", xvalue - 125 * xratio, yvalue + 100 * yratio, paint);
            paint.setColor(0xff000000);if(isupdate[xshow-1]==0)paint.setColor(0xffCF5B56);
            canvas.drawText(dates[xshow - 1], xvalue - 300 * xratio, 220 * yratio, paint);
            paint.setColor(0xff000000);
            if(y[xshow-1]!=0){
                String text=BloodPressureBean.blood_status[BloodPressureBean.getBloodStatu((int)y[xshow-1])];
                canvas.drawText(text, xvalue - 300*xratio, 280 * yratio, paint);
            }
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
            y_t[i] = yvalue - 60 * yratio - 110 * yratio / 20 * y[i];
            y1_t[i] = yvalue - 60 * yratio - 110 * yratio / 20 * y1[i];
            y2_t[i] = yvalue - 60 * yratio - 110 * yratio / 20 * y2[i];
        }
        path.moveTo(x[0], y_t[0]);
        for(int i=1;i<x.length;i++){
            path.cubicTo(x[i - 1] + (float)(0.4*(x[i]-x[i-1])), y_t[i - 1], x[i] - (float)(0.4*(x[i]-x[i-1])), y_t[i], x[i], y_t[i]);
        }
        canvas.drawPath(path, paint);
        //canvas.drawLine(xvalue - 820*xratio, yvalue + 100*yratio, xvalue - 700*xratio, yvalue + 100*yratio, paint);

        path=new Path();
        paint.setColor(0xffFA9D29);
        paint.setTextSize(30);
        path.moveTo(x[0], y1_t[0]);
        for(int i=1;i<x.length;i++){
            path.cubicTo(x[i - 1] + (float)(0.4*(x[i]-x[i-1])), y1_t[i - 1], x[i] - (float)(0.4*(x[i]-x[i-1])), y1_t[i], x[i], y1_t[i]);
        }
        canvas.drawPath(path, paint);
        //canvas.drawLine(xvalue - 570*xratio, yvalue + 100*yratio, xvalue - 450*xratio, yvalue + 100*yratio, paint);

        path=new Path();
        paint.setColor(0xffFF3146);
        path.moveTo(x[0], y2_t[0]);
        for(int i=1;i<x.length;i++){
            path.cubicTo(x[i - 1] + (float)(0.4*(x[i]-x[i-1])), y2_t[i - 1], x[i] - (float)(0.4*(x[i]-x[i-1])), y2_t[i], x[i], y2_t[i]);
        }
        canvas.drawPath(path, paint);
        //canvas.drawLine(xvalue - 320*xratio, yvalue + 100*yratio, xvalue - 200*xratio, yvalue + 100*yratio, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff457DD7);
        for(int i=0;i<x.length; i++) {
            canvas.drawCircle(x[i],y_t[i],10*xratio,paint);
        }

        paint.setColor(0xff00ff00);
        for(int i=0;i<x.length; i++) {
            canvas.drawCircle(x[i],y1_t[i],10*xratio,paint);
        }

        paint.setColor(0xff0393BC);
        for(int i=0;i<x.length; i++) {
            canvas.drawCircle(x[i],y2_t[i],10*xratio,paint);
        }

    }

}

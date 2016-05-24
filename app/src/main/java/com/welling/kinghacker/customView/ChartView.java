package com.welling.kinghacker.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.bean.SugerBean;
import com.welling.kinghacker.tools.FontTool;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/12/2016.
 **/
public class ChartView extends View {

    public float bloodSugerData[] = null,bloodSugerData1[] = null;
    public String bloodUpdateTime[] = null,bloodUpdateTime1[] = null;
    private Context context;
    private LinearLayout rootView;

    public static int numberOfData = 10;
    public int leftI,rightI;
    private List<String> Xaxis;
    private List<Float> Yaxis;
    private int diagramWidth,diagramHeight;
    private int chartMarginHorizontal = 50;
    private YasixView yasixView;
    private Point originPoint;
    private  int intervalY = 50,intervalX = 150;

    private int arrowLength = 15;
    private int descTextSize,textTextSize;
    private int asixPaintWidth;
    private float markLength;
    private float pointRadius;

    private String descTextY,descTextX;

//    Paint
    private Paint linePaint,linePaintH,linePaintL,asixPaint,pointPaint,textPaint,arrowPaint,desPaint;


    public ChartView(Context context) {
        super(context);
        init();
    }
    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        intervalX = (int)getResources().getDimension(R.dimen.intervalX);

        markLength = getResources().getDimension(R.dimen.markLength);
        asixPaintWidth = (int)getResources().getDimension(R.dimen.asixPaintWidth);
        textTextSize = (int)getResources().getDimension(R.dimen.asixTextSize);
        descTextSize = (int)getResources().getDimension(R.dimen.asixTextSize);
        pointRadius = getResources().getDimension(R.dimen.pointRadius);

        initPaint();
        rootView = new LinearLayout(getContext());
        rootView.setOrientation(LinearLayout.HORIZONTAL);
        originPoint = new Point(100,300);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
        yasixView = new YasixView(getContext());
        rootView.addView(yasixView);
        rootView.addView(scrollView);
        rootView.setBackgroundColor(getContext().getResources().getColor(R.color.bloodSugerBGColor));
        scrollView.addView(this);

        yasixView.setYLength(100);
        scrollView.setHorizontalScrollBarEnabled(false);

    }

    private void initPaint() {
//        折线paint
        linePaint =  new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(asixPaintWidth/2);

        linePaintH =  new Paint();
        linePaintH.setColor(Color.RED);
        linePaintH.setStrokeWidth(asixPaintWidth/2);

        linePaintL =  new Paint();
        linePaintL.setColor(Color.GRAY);
        linePaintL.setStrokeWidth(asixPaintWidth/2);
//        坐标paint
        asixPaint = new Paint();
        asixPaint.setColor(Color.BLACK);
        asixPaint.setStrokeWidth(asixPaintWidth);
//        textpaint
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textTextSize);
//      pointPaint
        pointPaint = new Paint();
        pointPaint.setColor(Color.BLUE);
//        箭头paint
        arrowPaint = new Paint();
        arrowPaint.setColor(Color.BLACK);
        arrowPaint.setStrokeWidth(asixPaintWidth);
//        坐标描述paint
        desPaint = new Paint();
        desPaint.setColor(Color.BLUE);
        desPaint.setTextSize(descTextSize);
    }

    public void setYLength(int length){
        yasixView.setYLength(length);
    }
    public void setOriginPoint(float x,float y) {
        originPoint.x = x;
        originPoint.y = y;
        yasixView.setLayoutParams(new LinearLayout.LayoutParams((int) x + 2, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    public void setXaxis(List<String> xaxis) {
        Xaxis = xaxis;
    }
    public void setYaxis(List<Float> yaxis) {
        Yaxis = yaxis;
    }
    public void setDescTextY(String text){
        descTextY = text;
    }

    public void setDescTextX(String descTextX) {
        this.descTextX = descTextX;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //当总数据已经传入，即不为空时，根据总数据中数据个数设定view的总宽
        if (Xaxis != null) {
            diagramWidth = Xaxis.size() * intervalX + chartMarginHorizontal * 2;
        }
        diagramHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(diagramWidth + 150, diagramHeight);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        List<Point> points = new ArrayList<>();
        canvas.drawLine(0, originPoint.y, diagramWidth, originPoint.y, asixPaint);
//        绘制X轴箭头
        canvas.drawLine(diagramWidth,originPoint.y,diagramWidth-arrowLength,originPoint.y-arrowLength,arrowPaint);
        canvas.drawLine(diagramWidth,originPoint.y,diagramWidth-arrowLength,originPoint.y+arrowLength,arrowPaint);
//
        if(descTextX != null){
            int textHeight = (int)new FontTool(getContext()).getTextHeight(descTextSize);
            canvas.drawText(descTextX, diagramWidth, originPoint.y + textHeight,desPaint);
        }
        float x = intervalX,y;
        FontTool fontTool = new FontTool(getContext());
        for (int i = 0;i < Yaxis.size();++i){
//            坐标轴刻度
            canvas.drawLine(x, originPoint.y, x, originPoint.y - markLength, asixPaint);

//            x轴数据
            float textHeight = fontTool.getTextHeight(textTextSize);
            if (Xaxis.get(i).contains(" ")){
              String data[] =  Xaxis.get(i).split(" ");
                for (int index = 0;index < data.length;index++){
                    float textSize = fontTool.getTextWidth(data[index], textTextSize);
                    canvas.drawText(data[index],x - textSize/4, originPoint.y + textHeight,textPaint);
                    textHeight +=textHeight;
                }
            }else {
                float textSize = fontTool.getTextWidth(Xaxis.get(i), textTextSize);
                canvas.drawText(Xaxis.get(i),x - textSize / 2, originPoint.y + textHeight,textPaint);
            }

//            画点
            y = getY(intervalY * Yaxis.get(i));
            canvas.drawCircle(x,y,pointRadius,pointPaint);
//            点的数据
            canvas.drawText(Yaxis.get(i)+"",x - 10,y - 2*pointRadius,textPaint);
            points.add(new Point(x,y));
            x += intervalX;
        }
        drawLine(points, canvas);
    }
//    因为y轴和我们平时的坐标是反过来的，所以需要转换
    private float getY(float y){
        return originPoint.y - y;
    }

//连线
    private void drawLine(List<Point> points,Canvas canvas){
        int pointSize = points.size();
        if (pointSize < 2) return;
        for (int i = 0;i < pointSize-1;i++){
            double xm,ym,xn,yn;
            float  y1 = getY(intervalY * Yaxis.get(i)),y2= getY(intervalY * Yaxis.get(i+1));
            double x=150;  //数据到折线图的X轴偏移为150
            ym=3.9;    //低血糖上限
            yn=16;     //高血糖下限

            //低于低血糖上限画灰线
             if(Yaxis.get(i)<ym&&Yaxis.get(i+1)<ym) canvas.drawLine(points.get(i).x,points.get(i).y,points.get(i+1).x,points.get(i+1).y,linePaintL);

            //高出高血糖下限画红线
             else if(Yaxis.get(i)>yn&&Yaxis.get(i+1)>yn) canvas.drawLine(points.get(i).x,points.get(i).y,points.get(i+1).x,points.get(i+1).y,linePaintH);

             else if(Yaxis.get(i)<ym&&Yaxis.get(i+1)<yn&&Yaxis.get(i+1)>ym){ //跨低血糖区画双色线（升线）
                //低于低血糖上限画灰线
                xm=points.get(i).x+(points.get(i+1).x-points.get(i).x)*(ym-Yaxis.get(i))/(Yaxis.get(i+1)-Yaxis.get(i));
                float xm1=(float)xm;
                double ym2 = y2-(Yaxis.get(i+1)-ym)*(y2-y1)/(Yaxis.get(i+1)-Yaxis.get(i));
                 float ym1=(float)ym2;
                canvas.drawLine(points.get(i).x,points.get(i).y,xm1,ym1,linePaintL);

                //处于正常血糖区画绿线
                canvas.drawLine(xm1,ym1,points.get(i+1).x,points.get(i+1).y,linePaint);
            }
            else if(Yaxis.get(i)<ym&&Yaxis.get(i+1)>yn){ //跨高低血糖区画三色线（升线）
                //低于低血糖上限画灰线
                xm=points.get(i).x+(points.get(i+1).x-points.get(i).x)*(ym-Yaxis.get(i))/(Yaxis.get(i+1)-Yaxis.get(i));
                float xm1=(float)xm;
                 double ym2 = y2-(Yaxis.get(i+1)-ym)*(y2-y1)/(Yaxis.get(i+1)-Yaxis.get(i));
                 float ym1=(float)ym2;
                canvas.drawLine(points.get(i).x,points.get(i).y,xm1,ym1,linePaintL);

                //高出高血糖下限画红线
                xn=points.get(i).x+(points.get(i+1).x-points.get(i).x)*(yn-Yaxis.get(i))/(Yaxis.get(i+1)-Yaxis.get(i));
                float xn1=(float)xn;
                 double yn2 = y2-(Yaxis.get(i+1)-yn)*(y2-y1)/(Yaxis.get(i+1)-Yaxis.get(i));
                 float yn1=(float)yn2;
                canvas.drawLine(xn1,yn1,points.get(i+1).x,points.get(i+1).y,linePaintH);

                //处于正常血糖区画绿线
                canvas.drawLine(xm1,ym1,xn1,yn1,linePaint);
            }
            else if(Yaxis.get(i)>ym&&Yaxis.get(i)<yn&&Yaxis.get(i+1)>yn){ //跨高血糖区画双色线（升线）
                xn=points.get(i).x+(points.get(i+1).x-points.get(i).x)*(yn-Yaxis.get(i))/(Yaxis.get(i+1)-Yaxis.get(i));
                float xn1=(float)xn;
                 double yn2 = y2-(Yaxis.get(i+1)-yn)*(y2-y1)/(Yaxis.get(i+1)-Yaxis.get(i));
                 float yn1=(float)yn2;
                canvas.drawLine(points.get(i).x,points.get(i).y,xn1,yn1,linePaint);   //处于正常血糖区画绿线
                canvas.drawLine(xn1,yn1,points.get(i+1).x,points.get(i+1).y,linePaintH);   //高出高血糖下限画红线
            }
            else if(Yaxis.get(i)>yn&&Yaxis.get(i+1)<yn&&Yaxis.get(i+1)>ym){ //跨高血糖区画双色线（降线）
                xn=points.get(i+1).x-(points.get(i+1).x-points.get(i).x)*(yn-Yaxis.get(i+1))/(Yaxis.get(i)-Yaxis.get(i+1));
                float xn1=(float)xn;
                 double yn2 = y1-(Yaxis.get(i)-yn)*(y1-y2)/(Yaxis.get(i)-Yaxis.get(i+1));
                 float yn1=(float)yn2;
                canvas.drawLine(points.get(i).x,points.get(i).y,xn1,yn1,linePaintH);    //高出高血糖下限画红线
                canvas.drawLine(xn1,yn1,points.get(i+1).x,points.get(i+1).y,linePaint);   //处于正常血糖区画绿线
            }
            else if(Yaxis.get(i)>yn&&Yaxis.get(i+1)<ym){ //跨高低血糖区画三色线（降线）
                //高出高血糖下限画红线
                xn=points.get(i+1).x-(points.get(i+1).x-points.get(i).x)*(yn-Yaxis.get(i+1))/(Yaxis.get(i)-Yaxis.get(i+1));
                float xn1=(float)xn;
                 double yn2 = y1-(Yaxis.get(i)-yn)*(y1-y2)/(Yaxis.get(i)-Yaxis.get(i+1));
                 float yn1=(float)yn2;
                canvas.drawLine(points.get(i).x,points.get(i).y,xn1,yn1,linePaintH);

                //低于低血糖上限画灰线
                xm=points.get(i+1).x-(points.get(i+1).x-points.get(i).x)*(ym-Yaxis.get(i+1))/(Yaxis.get(i)-Yaxis.get(i+1));
                float xm1=(float)xm;
                 double ym2 = y1-(Yaxis.get(i)-ym)*(y1-y2)/(Yaxis.get(i)-Yaxis.get(i+1));
                 float ym1=(float)ym2;
                canvas.drawLine(xm1,ym1,points.get(i+1).x,points.get(i+1).y,linePaintL);

                //处于正常血糖区画绿线
                canvas.drawLine(xn1,yn1,xm1,ym1,linePaint);
            }
            else if(Yaxis.get(i)>ym&&Yaxis.get(i)<yn&&Yaxis.get(i+1)<ym){ //跨低血糖区画双色线（降线）
                xm=points.get(i+1).x-(points.get(i+1).x-points.get(i).x)*(ym-Yaxis.get(i+1))/(Yaxis.get(i)-Yaxis.get(i+1));
                float xm1=(float)xm;
                 double ym2 = y1-(Yaxis.get(i)-ym)*(y1-y2)/(Yaxis.get(i)-Yaxis.get(i+1));
                 float ym1=(float)ym2;
                canvas.drawLine(points.get(i).x,points.get(i).y,xm1,ym1,linePaint);   //处于正常血糖区画绿线
                canvas.drawLine(xm1,ym1,points.get(i+1).x,points.get(i+1).y,linePaintL);    //低于低血糖上限画灰线
            }

            //处于正常血糖区画绿线
            else/* if(points.get(i).y>ym&&points.get(i+1).y<yn) */
                canvas.drawLine(points.get(i).x,points.get(i).y,points.get(i+1).x,points.get(i+1).y,linePaint);
        }
    }
    public LinearLayout getRootView(){
        return rootView;
    }
//    用来存放点坐标
     class Point{
        float x,y;
        Point(float x,float y){
            this.x = x;
            this.y = y;
        }
    }
//    内部类，实现y轴不可滑动
   private class YasixView extends View{
        private float YLength;
        public YasixView(Context context){
            super(context);
        }




        public void setYLength(int length) {
            this.YLength = length;
        }

        @Override
        protected void onDraw(Canvas canvas){
           super.onDraw(canvas);
            float YLengthPlus = YLength + 20;
            canvas.drawLine(originPoint.x, originPoint.y, originPoint.x, originPoint.y - YLengthPlus, asixPaint);
            canvas.drawLine(originPoint.x, originPoint.y - YLengthPlus, originPoint.x - arrowLength, originPoint.y - YLengthPlus + arrowLength, arrowPaint);//画y轴上面的箭头

            float YtextOffset = getResources().getDimension(R.dimen.asixTextMark);
            Path path = new Path(); //定义一条路径
            float textHeigth = new FontTool(getContext()).getTextHeight(descTextSize) + YtextOffset;
            float textX = originPoint.x - textHeigth;
            if (textX <= 0) textX = SystemTool.getSystem(getContext()).adaptation1080(10);
            path.moveTo(textX, originPoint.y - YLengthPlus); //移动到 坐标10,10
            path.lineTo(textX, originPoint.y - YLengthPlus + textHeigth * descTextY.length());

            canvas.drawTextOnPath(descTextY, path, 5, 5, desPaint);
//画y轴坐标点
            float y = originPoint.y;
            float maxY = getMaxY();
            intervalY = (int)(YLength / maxY)+1;
            int count =(int) maxY / 10;

            for (int i = 0;i <= maxY ;i++){
                if (maxY <= 10 || i%count == 0) {
                    canvas.drawLine(originPoint.x, y, originPoint.x - markLength, y, asixPaint);
                    canvas.drawText(i + "", originPoint.x - YtextOffset, y + 5, textPaint);
                }
                y -= intervalY;
            }

       }

//    获取Y轴最大的数
    private float getMaxY(){
        float max = 0;
        for (int i = 0;i < Yaxis.size();i++){
            if (Yaxis.get(i) > max)
                max = Yaxis.get(i);
        }
        return max;
    }
    }

    public void initDate(){
        //存储数据
        bloodSugerData1 = new float[numberOfData];
        bloodUpdateTime1 =new String [numberOfData];
        for(int i=0;i<numberOfData;i++) bloodSugerData1[i] = 100-i;
        SugerBean sugerbean = new SugerBean(context,numberOfData);
        bloodSugerData1 = sugerbean.getRecentlyMoreData();
        bloodUpdateTime1 = sugerbean.getRecentlyMoreTime();
        //展示的数据
        leftI = 0;
        rightI = 6;
        setData(leftI, rightI);
    }
    public void initDateAnother(String endTime){
        //存储数据
        bloodSugerData1 = new float[numberOfData];
        bloodUpdateTime1 =new String [numberOfData];
        for(int i=0;i<numberOfData;i++) bloodSugerData1[i] = 100-i;
        SugerBean sugerbean = new SugerBean(context,numberOfData);
        bloodSugerData1 = (float[])sugerbean.getRecentlyMoreChooseData(endTime);
        bloodUpdateTime1 = sugerbean.getRecentlyMoreTime();
        //展示的数据
        leftI = 0;
        rightI = 6;
        setData(leftI,rightI);
    }
    public void setData(int leftI,int rightI){
        bloodSugerData = new float[7];
        bloodUpdateTime = new String [7];
        for(int i=leftI,j=0;i<rightI+1;i++,j++) {
            if(i<numberOfData) {
                bloodSugerData[j] = bloodSugerData1[i];
                bloodUpdateTime[j] = bloodUpdateTime1[i];
            }
            else{
                bloodSugerData[j] = 0;
                bloodUpdateTime[j]=null;
            }
        }
    }

}

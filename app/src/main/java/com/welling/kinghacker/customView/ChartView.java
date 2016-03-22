package com.welling.kinghacker.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.tools.FontTool;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/12/2016.
 **/
public class ChartView extends View {

    private LinearLayout rootView;

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
    private Paint linePaint,asixPaint,pointPaint,textPaint,arrowPaint,desPaint;


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
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(asixPaintWidth/2);
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

}

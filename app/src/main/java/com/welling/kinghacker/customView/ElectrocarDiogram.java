package com.welling.kinghacker.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/16/2016.
**/
public class ElectrocarDiogram extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private boolean isRun = false;
    private List<Float> point;
    private float baseLine ,range ,amplitude;
    private Thread drawThread;
    private int timeInterval = 50;
    private float maxNum = 0;
    private float startX;

    public ElectrocarDiogram(Context context) {
        super(context);
        init();
    }
    public ElectrocarDiogram(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ElectrocarDiogram(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        baseLine = getResources().getDimension(R.dimen.baseLine);
        amplitude = getResources().getDimension(R.dimen.amplitude);
        range = getResources().getDimension(R.dimen.range);
        startX = getResources().getDimension(R.dimen.startX);;
        point = new ArrayList<>();
        int pointCount = 10000;
        for (int i = 0; i<pointCount;i++){
            float num =(float) Math.random() * 10 - 5;
            point.add(new Float(num));
            if (Math.abs(num) > maxNum) {
                maxNum = Math.abs(num);
            }
        }
        holder = getHolder();
        holder.addCallback(this);

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        isRun = true;
        if (drawThread == null || !drawThread.isAlive()) {
            dramThread();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRun = false;
    }

    synchronized private void dramThread(){
       drawThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (isRun){
                        SurfaceHolder surfaceHolder = holder;
                        Canvas  canvas = surfaceHolder.lockCanvas();
                        canvas.drawColor(Color.BLACK);
                        Paint paint = new Paint();
                        paint.setColor(Color.GREEN);
                        paint.setStrokeWidth(getResources().getDimension(R.dimen.paintWidth));
                        if (canvas != null  && isRun) {
                            surfaceHolder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。

                        }

                        List<Float> drawPoint = new ArrayList<Float>(point);
                        float lastX = -startX, lastY = baseLine;
                        for (int i = 0; i < drawPoint.size(); i++) {
                            if (!isRun) break;
                            Rect rect = new Rect((int)(lastX+0.5) ,(int)(baseLine - maxNum * amplitude ),(int)(lastX + 2*range+5 ),(int)(baseLine + maxNum * amplitude* 2));

                            clearCanvas(rect, surfaceHolder);
                            Canvas c = surfaceHolder.lockCanvas(rect);
                            if (c != null) {
                                c.drawLine(lastX, lastY, lastX + range, baseLine + drawPoint.get(i) * amplitude, paint);
                                surfaceHolder.unlockCanvasAndPost(c);
                            }
                            lastX += range;
                            lastY = baseLine + drawPoint.get(i) * amplitude;

                            if (lastX >= SystemTool.getSystem(getContext()).getScreenWidth() - range){
                                lastX = -startX;

                            }

                            try {
                                Thread.sleep(timeInterval);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        isRun = false;
                    }
                }

        });
        drawThread.start();
    }
   private void clearCanvas(Rect rect,SurfaceHolder holder){
       Canvas canvas = holder.lockCanvas(rect);
//       canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
       if (canvas != null) {
           canvas.drawColor(Color.BLACK);
           holder.unlockCanvasAndPost(canvas);
       }
   }
    private void clearAll(Rect rect,SurfaceHolder holder){
        Canvas canvas = holder.lockCanvas(rect);

        if (canvas != null) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            holder.unlockCanvasAndPost(canvas);
        }
    }


}

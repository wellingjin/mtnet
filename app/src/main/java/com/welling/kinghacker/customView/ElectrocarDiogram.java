package com.welling.kinghacker.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.creative.filemanage.ECGFile;
import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.mtdata.ECGFilesUtils;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/16/2016.
**/
public class ElectrocarDiogram extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private boolean isRun = false;
    private List<Integer> point;
    private float baseLine ,range ,amplitude;
    private Thread drawThread;
    private int timeInterval = 10;
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

        baseLine = SystemTool.getSystem(getContext()).getXMLDimension(R.dimen.baseLine);
        amplitude = SystemTool.getSystem(getContext()).getXMLDimension(R.dimen.amplitude);
        range = SystemTool.getSystem(getContext()).getXMLDimension(R.dimen.range);
        startX = SystemTool.getSystem(getContext()).getXMLDimension(R.dimen.startX);

        ECGFile file = ECGFilesUtils.getLastECGFile();
        if (file != null) {
            point = new ArrayList<>(file.ecgData);
        }else {
            point = new ArrayList<>();
        }
        /*int pointCount = 10000;
        for (int i = 0; i<pointCount;i++){
            Float num =Float.valueOf((float) (Math.random() * 10 - 5));
            point.add(num);
            if (Math.abs(num) > maxNum) {
                maxNum = Math.abs(num);
            }
        }*/
        holder = getHolder();
        holder.addCallback(this);

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDram();
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
                    Log.i("Dram","start");

                        Paint paint = new Paint();
                        paint.setColor(Color.GREEN);
                        paint.setStrokeWidth(getResources().getDimension(R.dimen.paintWidth));


                        List<Integer> drawPoint = new ArrayList<>();
                    if (point.size() <=0){
                        isRun = false;
                        return;
                    }
                    for (int w : point) {
                        int pointNum = (w-1950)/10;
                        if (Math.abs(pointNum) > maxNum){
                            maxNum = pointNum;
                        }
                        drawPoint.add(-pointNum);
                    }
                        float lastX = -startX, lastY = baseLine;
                        for (int i = 0; i < drawPoint.size(); i++) {
                            if (!isRun) break;
                            Rect rect = new Rect((int)(lastX+0.5) ,(int)(baseLine - maxNum * amplitude ),(int)(lastX + 2*range+5 ),(int)(baseLine + maxNum * amplitude* 2));
                            Log.i("Dram","dram"+drawPoint.get(i));
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

                            /*try {
                                Thread.sleep(timeInterval);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                        }

                        isRun = false;
                    }
                }

        });
        drawThread.start();
    }
    public void startDram(){
        isRun = true;
        if (drawThread == null || !drawThread.isAlive()) {
            clearCanvas(null,holder);
            dramThread();
        }
    }

    private void clearCanvas(Rect rect,SurfaceHolder holder){
        Canvas canvas;
        if (rect == null){
            canvas = holder.lockCanvas();
        }else {
            canvas = holder.lockCanvas(rect);
        }

//       canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
       if (canvas != null) {
           canvas.drawColor(Color.BLACK);
           holder.unlockCanvasAndPost(canvas);
       }
   }
    /*private void clearAll(Rect rect,SurfaceHolder holder){
        Canvas canvas = holder.lockCanvas(rect);

        if (canvas != null) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            holder.unlockCanvasAndPost(canvas);
        }
    }*/

    public void setPoint(List<Integer> point) {
        this.point = point;
    }

}

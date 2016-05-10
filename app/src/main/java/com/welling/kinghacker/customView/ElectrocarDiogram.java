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
import com.welling.kinghacker.mtdata.MTThread;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 3/16/2016.
**/
public class ElectrocarDiogram extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private List<Integer> point;
    private float baseLine ,range ,amplitude;

    private int timeInterval = 10;
    private float maxNum = 0;
    private float startX;
    static private List<MTThread> threads = new ArrayList<>();

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

        holder = getHolder();
        holder.addCallback(this);

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDram();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i("MySurface",width+"change:"+format);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        stopDram();
    }

    synchronized private void dramThread(final MTThread thread){

        thread.setMtRun(new MTThread.MTRun() {
            @Override
            public void run() {
                SurfaceHolder surfaceHolder = holder;
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(getResources().getDimension(R.dimen.paintWidth));


                List<Integer> drawPoint = new ArrayList<>();
                if (point.size() <= 0) {
                    thread.isRun = false;
                    return;
                }
                for (int w : point) {
                    int pointNum = (w - 1950) / 10;
                    if (Math.abs(pointNum) > maxNum) {
                        maxNum = pointNum;
                    }
                    drawPoint.add(-pointNum);
                }
                float lastX = -startX, lastY = baseLine;
                for (int i = 0; i < drawPoint.size() && thread.isRun; i++) {
                    Rect rect = new Rect((int) (lastX + 0.5), (int) (baseLine - maxNum * amplitude), (int) (lastX + 2 * range + 5), (int) (baseLine + maxNum * amplitude * 2));

                    clearCanvas(thread, rect, surfaceHolder);
                    if (!thread.isRun) {
                        break;
                    }
                    Canvas c = surfaceHolder.lockCanvas(rect);
                    if (c != null) {
                        c.drawLine(lastX, lastY, lastX + range, baseLine + drawPoint.get(i) * amplitude, paint);
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                    lastX += range;
                    lastY = baseLine + drawPoint.get(i) * amplitude;
                    if (lastX >= SystemTool.getSystem(getContext()).getScreenWidth() - range) {
                        lastX = -startX;
                    }

                }
            }

        });
        thread.start();
    }
    public void startDram(){
        if (threads!=null&&threads.size() > 0){
            stopDram();
        }

        MTThread thread = new MTThread();
        thread.isRun = true;
        threads.add(thread);
        clearCanvas(thread, null, holder);
        dramThread(thread);
        Log.i("thread", "thread size:" + threads.size());
    }
    public void stopDram(){
        for (MTThread thread:threads){
            thread.isRun = false;
            if (thread.isAlive()){
                thread.interrupt();
            }
        }

        threads.clear();
    }

    synchronized private void clearCanvas(MTThread thread,Rect rect,SurfaceHolder holder){
        Canvas canvas;
        if (rect == null){
            canvas = holder.lockCanvas();
        }else {
            canvas = holder.lockCanvas(rect);
        }

//       canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
       if (canvas != null) {
           canvas.drawColor(Color.BLACK);
           try {
               holder.unlockCanvasAndPost(canvas);
           }catch (Exception e){
               Log.i("ELC","ex");
               thread.isRun = false;
           }
       }
   }


    synchronized public void setPoint(List<Integer> point) {
        this.point = point;
    }

}

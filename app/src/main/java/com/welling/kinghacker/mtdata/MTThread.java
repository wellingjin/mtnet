package com.welling.kinghacker.mtdata;

/**
 * Created by KingHacker on 5/6/2016.
 *
 */
public class MTThread extends Thread implements Runnable{
    public boolean isRun = false;
    private MTRun mtRun;
    @Override
    public void run(){
        if (mtRun!=null){
            mtRun.run();
        }
    }

    public void setMtRun(MTRun mtRun) {
        this.mtRun = mtRun;
    }

    public interface MTRun{
        void run();
    }
}

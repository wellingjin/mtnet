package com.welling.kinghacker.tools;

import com.creative.base.BLUReader;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by KingHacker on 5/17/2016.
 */
public class MTBLUReader extends BLUReader {
    private InputStream is;
    private ReadResultListener resultListener;
    public MTBLUReader(InputStream is){
        super(is);
        this.is = is;
    }
    @Override
    public int read(byte[] buffer) throws IOException {
        int size = this.is != null?this.is.read(buffer):0;
        if(resultListener!=null){
            resultListener.readSize(size);
        }
        return size;
    }

    public void setResultListener(ReadResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public interface ReadResultListener{
        void readSize(int size);
    }
}

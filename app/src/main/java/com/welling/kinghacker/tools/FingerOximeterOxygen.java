package com.welling.kinghacker.tools;

import com.creative.FingerOximeter.FingerOximeter;
import com.creative.FingerOximeter.IFingerOximeterCallBack;
import com.creative.base.Ireader;
import com.creative.base.Isender;

/**
 * Created by li on 2016/5/7.
 */
public class FingerOximeterOxygen extends FingerOximeter {
    public FingerOximeterOxygen(Ireader is, Isender sender, IFingerOximeterCallBack _callBack){
        super(is,sender,_callBack);
    }
    public void SetParamAction(boolean bFlag) {
        super.SetParamAction(bFlag);
    }
}

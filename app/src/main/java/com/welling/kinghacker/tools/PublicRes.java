package com.welling.kinghacker.tools;

import android.app.Activity;
import android.content.Context;

import com.welling.kinghacker.activities.R;

/**
 * Created by KingHacker on 3/11/2016.
 */
public class PublicRes{
    private static PublicRes publicRes = null;
//    int overflow参数
    public int
                overFlowItemOffset = 100,//item偏移量
                marginLeft = 10,marginTop = 30,marginRight = 10,marginBottom = 30;
//    折线图
    public float originPointX = 150f,
                   originPointY = 100f;
    public int YLengthOffset = 100,
                YTextOffset = 80;

//String
    public String electrocarDiogram,
                    bloodSuger,
                    bloodFat,
                    bloodPresure,
                    bloodOxygen,
                    bloodSugerItem1,
                    bloodSugerItem2,
                    bloodSugerItem3;


    private PublicRes(){
        electrocarDiogram = "心电";
        bloodSuger = "血糖";
        bloodFat = "血脂";
        bloodOxygen = "血氧";
        bloodPresure = "血压";
        bloodSugerItem1 = "单次";
        bloodSugerItem2 = "全部";
        bloodSugerItem3 = "筛选";


    }
    static public PublicRes getInstance(){
        if (publicRes == null){
            publicRes = new PublicRes();
        }
        return publicRes;
    }


}

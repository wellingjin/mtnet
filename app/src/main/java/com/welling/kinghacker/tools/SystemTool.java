package com.welling.kinghacker.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.Field;


/**
 * Created by KingHacker on 3/3/2016.
 */
public class SystemTool {
    private DisplayMetrics dm;
    private Context context;
    static private SystemTool systemTool = null;

    private SystemTool(){}

    static public SystemTool getSystem(Context context){
        if (systemTool == null){
            systemTool = new SystemTool();
        }
        systemTool.context = context;
        Resources resources = context.getResources();
        systemTool.dm = resources.getDisplayMetrics();
        return systemTool;
    }
//    获取屏幕宽度
    public float getScreenWidth(){
        return dm.widthPixels;
    }
//    获取屏幕高度
    public float getScreenHeight(){
        return dm.heightPixels;
    }
//单位转换
    public int DpToPx(float dp) {
        final float scale = dm.density;
        return (int) (dp * scale + 0.5f);
    }
    public int PxToDp(float dx){
        final float scale = dm.density;
        return (int) (dx / scale );
    }
//    获取状态栏高度
    public int getStatusBarHeight(){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            Log.e("error","getStatusBarHeight faile");
            e1.printStackTrace();
            return 75;
        }
    }
//
    public float adaptation540(float size){
        float scale = getScreenWidth() / 540f;
        return scale * size;
    }
    public float adaptation1080(float size){
        float scale = getScreenWidth() / 1080f;
        if (scale < 1) scale = (float)Math.sqrt(scale);
        return scale * size;
    }
    public float adaptation1920Y(float size){
        float scale = getScreenHeight() / 1920f;
        if (scale < 1) scale = (float)Math.sqrt(scale);
        return scale * size;
    }
    public boolean isLowPhone(){
        return getScreenWidth() < 850;
    }
    public String getVersionName()
    {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}

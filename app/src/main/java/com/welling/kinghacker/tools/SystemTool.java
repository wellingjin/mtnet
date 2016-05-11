package com.welling.kinghacker.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by KingHacker on 3/3/2016.
 *
 */
public class SystemTool {
    private DisplayMetrics dm;
    private Context context;
    static private SystemTool systemTool = null;
    private SharedPreferences sharedPreferences;

    private SystemTool(Context context){
        this.context = context;
        Resources resources = context.getResources();
        dm = resources.getDisplayMetrics();
        sharedPreferences = context.getSharedPreferences(PublicRes.preferencesName, context.MODE_PRIVATE);
    }

    static public SystemTool getSystem(Context context){
        if (systemTool == null){
            systemTool = new SystemTool(context);
        }

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
    public View getView(int layout){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(layout,null);
    }
    public float getXMLTextSize(int dimension){
        float size = context.getResources().getDimension(dimension);
        return PxToDp(size);
    }
    public int getXMLColor(int color){
        return context.getResources().getColor(color);
    }
    public Drawable getXMLDrawable(int drawable){
        return context.getResources().getDrawable(drawable);
    }
    public float getXMLDimension(int dimension){
        return context.getResources().getDimension(dimension);
    }
//    存储相关
/*
* 永久存储字符串类型
* */
    public void saveStringKV(String key,String value){
        Map<String,String> map = new HashMap<>();
        map.put(key,value);
        saveStringKV(map);
    }
    public void saveStringKV(Map<String,String> map){
        if (map  == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> keys = map.keySet();
        for (String key:keys){
            editor.putString(key,map.get(key));
        }
        editor.apply();
    }
    public void saveBooleanKV(String key,boolean value){
        Map<String,Boolean> map = new HashMap<>();
        map.put(key,value);
        saveBooleanKV(map);
    }
    public void saveBooleanKV(Map<String,Boolean> map){
        if (map  == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> keys = map.keySet();
        for (String key:keys){
            editor.putBoolean(key, map.get(key));
        }
        editor.apply();
    }

    /*
    * 获取某个字符串
    *
    * */
    public String getStringValue(String key){
        return getStringValue(key,"");
    }
    /*
    * 获取某个字符串
    *
    * */
    public String getStringValue(String key,String defaultValue){
        return sharedPreferences.getString(key, defaultValue);
    }
    public boolean getBooleanValue(String key,boolean defaultValue){
        return sharedPreferences.getBoolean(key, defaultValue);
    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    static public int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}

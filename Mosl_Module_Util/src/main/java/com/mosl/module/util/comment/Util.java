package com.mosl.module.util.comment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;

public class Util {

    public static void LOG(Object object){
        Log.i("---->",object+"");
    }

    /**
     * 获取状态栏高度
     * @param context 上下文
     * @return 已经转换成 dp 单位的高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 保存缓存数据
     * @param context 上下文
     * @param xmlName xml以及key的名字
     * @param value 要保存数据
     */
    public static void saveSharedPreferences(Context context, String xmlName, String value){
        try {
            SharedPreferences sp = context.getSharedPreferences(xmlName,Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(xmlName,value);
            edit.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存数据
     * @param context 上下文
     * @param xmlName xml以及key的名字
     * @return 已保存的数据
     */
    public static String getSharedPreferences(Context context,String xmlName){
        SharedPreferences sp = context.getSharedPreferences(xmlName,Context.MODE_PRIVATE);
        return sp.getString(xmlName,"");
    }

    /**
     * 将px转换成dp
     * @param context 上下文
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale =  context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}

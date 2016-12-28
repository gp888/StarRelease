package com.xingfabu.direct.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import com.vhall.business.WatchLive;

public class VhallUtil {
    private static DisplayMetrics metrics;

    //1	直播2	预约3	结束4	回放
    public static String getStatusStr(int type) {
        String statusStr = "";
        switch (type) {
            case 1:
                statusStr = "直播";
                break;
            case 2:
                statusStr = "预约";
                break;
            case 3:
                statusStr = "结束";
                break;
            case 4:
                statusStr = "回放";
                break;
            default:
                break;
        }

        return "当前直播处在" + statusStr + "状态！";
    }

    public static String getFixType(int type) {
        String typeStr = "";
        switch (type) {
            case WatchLive.FIT_DEFAULT:
                typeStr = "FIT_DEFAULT";
                break;
            case WatchLive.FIT_CENTER_INSIDE:
                typeStr = "FIT_CENTER_INSIDE";
                break;
            case WatchLive.FIT_X:
                typeStr = "FIT_X";
                break;
            case WatchLive.FIT_Y:
                typeStr = "FIT_Y";
                break;
            case WatchLive.FIT_XY:
                typeStr = "FIT_XY";
                break;
            default:
                break;
        }
        return typeStr;
    }

    /**
     * 将长整型值转化成字符串
     *
     * @param time
     * @return
     */
    public static String converLongTimeToStr(long time) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;

        //long hour = (time) / hh;
        //long minute = (time - hour * hh) / mi;
        long minute = time / mi;
        long second = (time - minute * mi) / ss;

        //String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        if (minute > 10) {
            //return strHour + ":" + strMinute + ":" + strSecond;
            return minute + ":" + strSecond;
        } else {
            return strMinute + ":" + strSecond;
        }
    }

    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels; // 屏幕宽度（像素）
    }

    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;// 屏幕高度（像素）
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
		if (metrics != null) {
			return metrics;
		}
        metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static int pixelsToDp(int px, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int dp = px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static int dpToPixel(int dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

}

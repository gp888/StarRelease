package com.xingfabu.direct.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xingfabu.direct.R;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.AdverImage;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.rong.imlib.RongIMClient;
import okhttp3.OkHttpClient;
import com.vhall.business.VhallSDK;
/**
 * Created by 郭平 on 2016/3/28 0028.
 */
public class App extends Application{
    private static App mInstance;
    private IWXAPI api;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        VhallSDK.init(this, getResources().getString(R.string.vhall_app_key), getResources().getString(R.string.vhall_app_secret_key));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);

        //友盟统计
//        MobclickAgent.openActivityDurationTrack(false);//禁止默认的页面统计
//        MobclickAgent.setDebugMode( true );

        /**
         * 融云
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(this))
                || "io.rong.push".equals(getCurProcessName(this))) {
            RongIMClient.init(this);
        }

        //友盟分享
        PlatformConfig.setWeixin(getResources().getString(R.string.weixin_key), getResources().getString(R.string.weixin_secret));
        PlatformConfig.setSinaWeibo(getResources().getString(R.string.sina_key), getResources().getString(R.string.sina_secret));
        PlatformConfig.setQQZone(getResources().getString(R.string.qq_key), getResources().getString(R.string.qq_secret));
        UMShareAPI.get(this);

        //友盟推送
        final PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(false);//关闭日志输出
        // 通知声音由服务端控制
		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);
        // 通知呼吸灯由服务端控制
        mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SERVER);
        // 通知震动由服务端控制
		mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SERVER);
        //注册推送服务，每次调用register方法都会回调该接口
        //请勿在调用register方法时做进程判断处理（主进程和channel进程均需要调用register方法才能保证长连接的正确建立）
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                String deviceId = deviceToken;
                //String device_token = mPushAgent.getRegistrationId();
            }

            @Override
            public void onFailure(String s, String s1) {
                String error = s1;
            }
        });
        //注册到微信
        api = WXAPIFactory.createWXAPI(this,UrlConstants.APP_ID);
        api.registerApp(UrlConstants.APP_ID);
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }

    public static App getApp(){
        return mInstance;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 是否第一次启动
     */
    public static boolean IsFristStart() {
        return (boolean) SPCache.getInstance(mInstance).get(UrlConstants.KEY_FRITST_START, false);
    }


    public static void setFristStart(boolean frist) {
        SPCache.getInstance(mInstance).putAndApply(UrlConstants.KEY_FRITST_START, frist);
    }

    /**
     * 广告是否缓存
     * @return
     */
    public static boolean IsAdvertisementCache() {
        return (boolean) SPCache.getInstance(mInstance).get(UrlConstants.IsAdvertisementCache, false);
    }

    public static void setAdvertisementCache(boolean frist) {
        SPCache.getInstance(mInstance).putAndApply(UrlConstants.IsAdvertisementCache, frist);
    }

    /**
     * 广告json 缓存
     * @param frist
     */
    public static void setAdvertisementJsonCache(ArrayList<AdverImage> frist) {
        Gson gson = new Gson();
        String json = gson.toJson(frist);
        SPCache.getInstance(mInstance).putAndApply(UrlConstants.IsAdvertisementJsonCache, json);
    }

    /**
     * 广告json 获取缓存
     */
    public static ArrayList<AdverImage> getAdvertisementJsonCache() {
        Gson gson = new Gson();
        String json1 = (String) SPCache.getInstance(mInstance).get(UrlConstants.IsAdvertisementJsonCache, "");
        ArrayList<AdverImage> advert = gson.fromJson(json1, new TypeToken<List<AdverImage>>() {
        }.getType());
        return advert;
    }

    /**
     * 获取广告 缓存
     * @param frist
     */
    public static void setAdvertisementTimeCache(String frist) {
        SPCache.getInstance(mInstance).putAndApply(UrlConstants.IsAdvertisementJsonCacheTime, frist);
    }

    /**
     * 广告时间 获取缓存
     */
    public static String getAdvertisementTimeCache() {
        String json1 = (String) SPCache.getInstance(mInstance).get(UrlConstants.IsAdvertisementJsonCacheTime, "0");
        return json1;
    }

    public static void setAdvertisementUrlCache(String path){
        SPCache.getInstance(mInstance).putAndApply(UrlConstants.IsAdvertisementUrlCache, path);
    }

    public static String getAdvertisementUrlCache(){
        String json1 = (String) SPCache.getInstance(mInstance).get(UrlConstants.IsAdvertisementUrlCache, "");
        return json1;
    }
    //推送
    public boolean isPush_status() {
        return (boolean)SPCache.getInstance(mInstance).get("status", true);
    }
    //推送
    public void setPush_status(boolean push_status) {
        SPCache.getInstance(mInstance).putAndApply("status", push_status);
    }
}

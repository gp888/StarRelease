package com.xingfabu.direct.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.App;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.AdverImage;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * App启动页
 * Created by 郭平 on 2016/3/29 0029.
 */
public class WelcomeActivity extends BaseActivity{

    private ImageView adver_image;
    private ArrayList<AdverImage> urls;
    private TextView btn_time;
    private Handler handler = new Handler();
    private runble runble;
    private long showTime ;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    public void initViews() {
        btn_time = (TextView) findViewById(R.id.btn_time);
        adver_image = (ImageView) findViewById(R.id.adver_image);
        if(App.IsAdvertisementCache()) {
            showTime = Integer.parseInt((String) SPCache.getInstance(WelcomeActivity.this).get("adver_duration", "2")) * 1000;
            initAdvertise();

            adver_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click_adver();
                    String adv_url = (String) SPCache.getInstance(WelcomeActivity.this).get("adver_url","");
                    if(!TextUtils.isEmpty(adv_url)){
                        Uri uri = Uri.parse(adv_url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);
            Log.e("mac",mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            Log.e("device_id",device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getString(R.string.startAdver));
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MobclickAgent.onPageEnd(getString(R.string.startAdver));
//        MobclickAgent.onPause(this);
    }

    //初始化数据
    private void initAdvertise() {
        urls = App.getAdvertisementJsonCache();
        Glide.with(this)
                .load(urls.get(0).result.path)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(adver_image);
        btn_time.setVisibility(View.VISIBLE);

        runble = new runble();
        handler.postDelayed(runble, showTime);
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runble);
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startMain(intent,0);
            }
        });
    }

    public void startMain(final Intent intent, int time) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(SPCache.getInstance(WelcomeActivity.this).getToken())){
                    startActivity(intent);
                }else{
//                    startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                }
                finish();
            }
        }, time);
    }

    class runble implements Runnable {
        @Override
        public void run() {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startMain(intent,0);
        }
    }
    private void click_adver(){
        String id = (String)SPCache.getInstance(WelcomeActivity.this).get("adver_adid","");
        String token = StarReleaseUtil.getToken(this);
        String sig = MD5Helper.GetMD5Code("type=5&" + "market_id=" + id + "&Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.ADVER_CLICK)
                .addParams("type","5")
                .addParams("market_id",id)
                .addParams("Authorization",token)
                .addParams("sig",sig)
                .build()
                .execute(new MyStringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                    }
                });
    }
}

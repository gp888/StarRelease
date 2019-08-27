package com.xingfabu.direct.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import com.xingfabu.direct.app.App;
import com.xingfabu.direct.cache.SPCache;

/**
 * Created by guoping on 16/6/22.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent1 = new Intent(this, WelcomeActivity.class);
        final Intent intent2 = new Intent(this, MainActivity.class);

        //是否第一次打开
        if (App.IsFristStart()) {
            //如果缓存广告数据了
            if (App.IsAdvertisementCache()) {
                startActivity(intent1);
            } else {
                startMain(intent2,0);
            }
        } else {
            startMain(intent2,0);
        }
    }

    public void startMain(final Intent intent, int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(SPCache.getInstance(SplashActivity.this).getToken())){
                    startActivity(intent);
                }else{
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                }
            }
        }, time);
    }
}
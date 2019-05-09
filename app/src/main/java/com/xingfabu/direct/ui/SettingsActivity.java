package com.xingfabu.direct.ui;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.umeng.message.IUmengCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.utils.ActivityCollector;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.widget.Loading;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.Map;
import okhttp3.Call;

/**
 * Created by guoping on 16/4/27.
 */
public class SettingsActivity extends BaseActivity{
    private Switch wifi_switch,push_switch;
    private LocalBroadcastManager localBroadcastManager;
    private UMShareAPI mShareAPI;

    //注销三方登录
    private UMAuthListener umdelAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            showToast("退出"+platform + "登录");
            SPCache.getInstance(SettingsActivity.this).putAndApply("thirdlogin","");
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.setting_layout);
    }

    @Override
    public void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        wifi_switch = (Switch) findViewById(R.id.wifi_switch);
        wifi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                localBroadcastManager = LocalBroadcastManager.getInstance(SettingsActivity.this);
                Intent intent;
                if(isChecked){
                    showToast("WiFi网络将自动播放视频");
                    intent = new Intent("com.xingfabu.broadcast.WiFiAUTOPLAY");
                    SPCache.getInstance(SettingsActivity.this).saveWiFiFlag(1);
                }else{
                    showToast("WiFi网络将不自动播放视频");
                    intent = new Intent("com.xingfabu.broadcast.WiFiNOTPLAY");
                    SPCache.getInstance(SettingsActivity.this).saveWiFiFlag(2);
                }
                localBroadcastManager.sendBroadcast(intent); // 发送本地广播
            }
        });

        if(SPCache.getInstance(this).getWiFiFlag() == 2){
            wifi_switch.setChecked(false);
        }else {
            wifi_switch.setChecked(true);
        }

        push_switch = (Switch) findViewById(R.id.push_switch);
        push_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PushAgent.getInstance(SettingsActivity.this).enable(new IUmengCallback() {
                        @Override
                        public void onSuccess() {
                            showToast("开启推送通知");
                        }
                        @Override
                        public void onFailure(String s, String s1) {
                        }
                    });
                }else{
                    PushAgent.getInstance(SettingsActivity.this).disable(new IUmengCallback() {
                        @Override
                        public void onSuccess() {
                            showToast("关闭推送通知");
                        }
                        @Override
                        public void onFailure(String s, String s1) {
                        }
                    });
                }
            }
        });
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        mShareAPI = UMShareAPI.get(this);//注销三方登录用
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_logout:
               logout();
                break;
            case R.id.ll_us:
                Intent intent = new Intent(this,WebViewActivity.class);
                intent.putExtra("weburl", UrlConstants.ABOUT_US);
                startActivity(intent);
                break;
        }
    }

    private void logout() {
        String token = SPCache.getInstance(this).getToken();
        String sig = MD5Helper.GetMD5Code("Authorization=" + token + UrlConstants.sign);

        OkHttpUtils.post()
                .url(UrlConstants.LOGOUT)
                .addParams("Authorization",token)
                .build()
                .execute(new MyStringCallback(new Loading(this)){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        BaseResponse res = BaseResponse.fromJson(response,BaseResponse.class);
                        if(res.retCode == 0){
                            SPCache.getInstance(SettingsActivity.this).saveToken("");
                            SPCache.getInstance(SettingsActivity.this).saveTouristToken("");
                            SPCache.getInstance(SettingsActivity.this).saveRongToken("");
                            SPCache.getInstance(SettingsActivity.this).saveUid("");
                            SPCache.getInstance(SettingsActivity.this).savePic("");
                            SPCache.getInstance(SettingsActivity.this).saveName("");
                            SPCache.getInstance(SettingsActivity.this).saveWiFiFlag(1);
                            String  thirdPlatform = (String) SPCache.getInstance(SettingsActivity.this).get("thirdlogin","");
                            SHARE_MEDIA platform = null;
                            if("QQ".equals(thirdPlatform)){
                                platform = SHARE_MEDIA.QQ;
                            }else if("WEIXIN".equals(thirdPlatform)){
                                platform = SHARE_MEDIA.WEIXIN;
                            }else if("SINA".equals(thirdPlatform)){
                                platform = SHARE_MEDIA.SINA;
                            }
                            mShareAPI.deleteOauth(SettingsActivity.this, platform, umdelAuthListener);
//                            startActivity(new Intent(SettingsActivity.this,LoginActivity.class));
//                            ActivityCollector.finishAll();
                        }
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getString(R.string.action_settings));
//        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getString(R.string.action_settings));
//        MobclickAgent.onPause(this);
    }

    //注销三方登录
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}

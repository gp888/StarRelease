package com.xingfabu.direct.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.LoginResponse;
import com.xingfabu.direct.utils.Base64;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.utils.Verification;
import com.xingfabu.direct.widget.Loading;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.Map;
import okhttp3.Call;

/**
 * Created by 郭平 on 2016/3/29 0029.
 */
public class LoginActivity extends BaseActivity{
    private TextView tv_register,tv_comein;
    private EditText et_phone,et_pass;
    private UMShareAPI mShareAPI;

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            showToast("授权成功");
            mShareAPI.getPlatformInfo(LoginActivity.this, platform, UMUserInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            showToast("授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            showToast("授权取消");
        }
    };

    private UMAuthListener UMUserInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if(map != null){
                showToast("获取信息成功");
                SPCache.getInstance(LoginActivity.this).putAndApply("thirdlogin",share_media.toString());
                if(share_media == SHARE_MEDIA.WEIXIN){

                    String userName = map.get("screen_name");
                    String usid = map.get("openid");
                    String iconUrl = map.get("profile_image_url");

                    thirdLogin("2",userName,usid,iconUrl,"2");
                }else if(share_media == SHARE_MEDIA.QQ){

                    String userName = map.get("screen_name");
                    String usid = map.get("openid");
                    String iconUrl = map.get("profile_image_url");

                    thirdLogin("1",userName,usid,iconUrl,"2");
                }else if(share_media == SHARE_MEDIA.SINA){
                    String userName = map.get("screen_name");
                    String usid = map.get("uid");
                    String iconUrl = map.get("profile_image_url");

                    thirdLogin("3",userName,usid,iconUrl,"2");
                }
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast("获取信息失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            showToast("获取信息取消");
        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明通知栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    public void initViews() {
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_comein = (TextView) findViewById(R.id.tv_comein);
        et_phone = (EditText) findViewById(R.id.et_phone);
        String phone = SPCache.getInstance(LoginActivity.this).getPhoneNum();
        if(!TextUtils.isEmpty(phone)){
            et_phone.setText(phone);
        }
        et_pass = (EditText) findViewById(R.id.et_pass);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        mShareAPI = UMShareAPI.get(this);
    }

    //注销三方登录用
    private UMAuthListener umdelAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            showToast("退出"+platform + "登录");
            SPCache.getInstance(LoginActivity.this).putAndApply("thirdlogin","");
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

        }
    };
    //注销三方登录用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.tv_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.tv_login:
                Login();
                break;
            case R.id.tv_comein:
                //getTouristToken();
                //直接进去
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
                break;
            case R.id.wechat_login:
                thirdAuthVerfy(this,SHARE_MEDIA.WEIXIN,umAuthListener);
                break;
            case R.id.qq_login:
                thirdAuthVerfy(this,SHARE_MEDIA.QQ,umAuthListener);
                break;
            case R.id.sina_login:
                thirdAuthVerfy(this,SHARE_MEDIA.SINA,umAuthListener);
                break;
            case R.id.tv_forgetpass:
                startActivity(new Intent(LoginActivity.this, ForgetPaswordActivity.class));
                break;
        }
    }

    private void getTouristToken() {
        String sig = MD5Helper.GetMD5Code(UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.GET_TOURIST_TOKEN)
                .addParams("sig",sig)
                .build()
                .execute(new MyStringCallback(new Loading(LoginActivity.this)){
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        LoginResponse resp = LoginResponse.fromJson(response,LoginResponse.class);

                        if(resp.retCode == 0){
                            String token = Base64.encode(("APH"+":"+resp.result.uid+":"+resp.result.token).getBytes());
                            SPCache.getInstance(LoginActivity.this).saveTouristToken(token);

                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                        }else if(resp.retCode != 0){
                            return;
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }
                });
    }

    private void Login() {

        final String phoneNumber = et_phone.getText().toString().trim();
        String pass = et_pass.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("手机号码不能为空");
            return;
        }// 验证手机号
        if (!Verification.isMobile(phoneNumber)) {
            showToast("请您输入正确的手机号码");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            showToast("密码不能为空");
            return;
        }
        if (pass.length() < 4|| pass.length() > 20) {
            showToast("密码长度要在6-20位之间");
            return;
        }

        StarReleaseUtil.closeImm(LoginActivity.this);
        //保存手机号

        String sig = MD5Helper.GetMD5Code("mobile=" + phoneNumber + "&" +  "password=" + pass + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.ACTION_LOGIN)
                .addParams("mobile",phoneNumber)
                .addParams("password",pass)
                .addParams("sig",sig)
                .build()
                .execute(new MyStringCallback(new Loading(LoginActivity.this)){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        LoginResponse resp = LoginResponse.fromJson(response,LoginResponse.class);
                        if(resp.retCode == 0){
                            SPCache.getInstance(LoginActivity.this).saveUid(resp.result.uid);
                            SPCache.getInstance(LoginActivity.this).savePhoneNum(phoneNumber);
                            String token = Base64.encode(("APH"+":"+resp.result.uid+":"+resp.result.token).getBytes());
                            SPCache.getInstance(LoginActivity.this).saveToken(token);
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                        }else if(resp.retCode != 0){
                            showToast(resp.desc);
                            return;
                        }
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getString(R.string.signin));
//        MobclickAgent.onResume(this);

        //注销三方登录用
        String  thirdPlatform = (String) SPCache.getInstance(LoginActivity.this).get("thirdlogin","");
        if(thirdPlatform == null || thirdPlatform.length() == 0){
            return;
        }
        SHARE_MEDIA platform = null;
        if("QQ".equals(thirdPlatform)){
            platform = SHARE_MEDIA.QQ;
        }else if("WEIXIN".equals(thirdPlatform)){
            platform = SHARE_MEDIA.WEIXIN;
        }else if("SINA".equals(thirdPlatform)){
            platform = SHARE_MEDIA.SINA;
        }
        mShareAPI.deleteOauth(LoginActivity.this, platform, umdelAuthListener);
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getString(R.string.signin));
//        MobclickAgent.onPause(this);
    }
    //三方登录
    private void thirdAuthVerfy(Activity activity, SHARE_MEDIA share_media, UMAuthListener umAuthListener){
        mShareAPI.doOauthVerify(activity, share_media, umAuthListener);
    }
    private void thirdLogin(String type,String userName,String usid,String iconUrl,String platform){
        String sig = MD5Helper.GetMD5Code("type=" + type + "&userName=" + userName +"&usid=" + usid +"&iconUrl=" + iconUrl + "&platform=" + platform + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.THIRDPARTY_LANDED)
                .addParams("type",type)
                .addParams("userName",userName)
                .addParams("usid",usid)
                .addParams("iconUrl",iconUrl)
                .addParams("platform",platform)
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
                        LoginResponse resp = LoginResponse.fromJson(response,LoginResponse.class);
                        if(resp.retCode == 0){
                            SPCache.getInstance(LoginActivity.this).saveUid(resp.result.uid);
                            String token = Base64.encode(("APH"+":"+resp.result.uid+":"+resp.result.token).getBytes());
                            SPCache.getInstance(LoginActivity.this).saveToken(token);
                            if(resp.result.is_binding){
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();
                            }else{
                                startActivity(new Intent(LoginActivity.this, PhoneVerificationActivity.class));
                            }
                        }
                    }
                });
    }
}

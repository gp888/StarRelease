package com.xingfabu.direct.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.utils.Verification;
import com.xingfabu.direct.widget.Loading;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;

/**
 * Created by 郭平 on 2016/3/30 0030.
 */
public class ForgetPaswordActivity extends BaseActivity{
    private EditText et_phone,et_pass,et_code;
    private Button tv_code;
    private Handler handler = new Handler();;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(reclen == 0){
                tv_code.setText("获取验证码");
                tv_code.setEnabled(true);
                reclen = 60;
                return;
            }
            reclen--;
            tv_code.setText(reclen+"");
            handler.postDelayed(this, 1000);
        }
    };
    private int reclen = 60;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_forgetpw);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明通知栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
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
        et_phone = (EditText) findViewById(R.id.et_phone);
        tv_code = (Button) findViewById(R.id.tv_code);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_code = (EditText) findViewById(R.id.et_code);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.tv_code:
                getCode();
                break;
            case R.id.tv_done:
                changePass();
                break;
        }
    }

    private void changePass() {

        final String phoneNumber = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        String passWord = et_pass.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("手机号码不能为空");
            return;
        }// 验证手机号
        if (!Verification.isMobile(phoneNumber)) {
            showToast("请您输入正确的手机号码");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
            return;
        }
        if (code.length() < 4) {
            showToast("验证码不足4位");
            return;
        }
        if (TextUtils.isEmpty(passWord)) {
            showToast("密码不能为空");
            return;
        }
        if (passWord.length() < 4 || passWord.length() > 20) {
            showToast("密码长度要在6-20位之间");
            return;
        }
        StarReleaseUtil.closeImm(ForgetPaswordActivity.this);

        String sig = MD5Helper.GetMD5Code("mobile="+phoneNumber+"&"+"code="+code+"&"+"password="+passWord+ UrlConstants.sign);

        OkHttpUtils.post()
                .url(UrlConstants.CHANGE_PASSWORD)
                .addParams("mobile", phoneNumber)
                .addParams("code", code)
                .addParams("password", passWord)
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(new Loading(ForgetPaswordActivity.this)){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        BaseResponse resp = BaseResponse.fromJson(response, BaseResponse.class);
                        if (resp.retCode != 0) {
                            showToast(resp.desc);
                            return;
                        }
                        SPCache.getInstance(ForgetPaswordActivity.this).savePhoneNum(phoneNumber);
//                        startActivity(new Intent(ForgetPaswordActivity.this, LoginActivity.class));
//                        finish();
                    }
                });
    }

    private void getCode() {

        String phoneNumber = et_phone.getText().toString().trim();
        if(TextUtils.isEmpty(phoneNumber)){
            showToast("手机号码不能为空");
            return;
        }
        if(!Verification.isMobile(phoneNumber)){
            showToast("请输入正确的号码");
            return;
        }
        tv_code.setEnabled(false);
        handler.post(runnable);

        String sig = MD5Helper.GetMD5Code("mobile=" + phoneNumber + "&" + "action=" + "3" + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.ACTION_SENDVERIFyCODE)
                .addParams("mobile", phoneNumber)
                .addParams("action", 3 + "")
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(new Loading(ForgetPaswordActivity.this)){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        BaseResponse resp = BaseResponse.fromJson(response, BaseResponse.class);
                        if (resp.retCode != 0) {
                            showToast(resp.desc);
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getString(R.string.changePassword));
//        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getString(R.string.changePassword));
//        MobclickAgent.onPause(this);
    }
}

package com.xingfabu.direct.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.utils.ActivityCollector;
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
public class PhoneVerificationActivity extends BaseActivity{
    private TextView tv_skip,tv_done,xieyi;
    private EditText et_phone,et_code,et_password;
    private Button button_code;
    private CheckBox cb_agree;
    private Handler handler = new Handler();;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(reclen == 0){
                button_code.setText("获取验证码");
                button_code.setEnabled(true);
                reclen = 60;
                return;
            }
            reclen--;
            button_code.setText(reclen+"");
            handler.postDelayed(this, 1000);
        }
    };
    private int reclen = 60;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_verifier);
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

        tv_skip = (TextView) findViewById(R.id.tv_skip);
        et_phone = (EditText) findViewById(R.id.et_phone);
        button_code = (Button) findViewById(R.id.button_code);
        et_code = (EditText) findViewById(R.id.et_code);
        tv_done = (TextView) findViewById(R.id.tv_done);
        cb_agree = (CheckBox) findViewById(R.id.cb_agree);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.tv_skip:
                startActivity(new Intent(PhoneVerificationActivity.this,MainActivity.class));
                break;
            case R.id.button_code:
                getCode();
                break;
            case R.id.tv_done:
                bindPhone();
                break;
            case R.id.xieyi:
                Intent intent = new Intent(this,WebViewActivity.class);
                intent.putExtra("weburl", UrlConstants.USER_PROTOCOL);
                startActivity(intent);
                break;
        }
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
        button_code.setEnabled(false);
        handler.post(runnable);

        String sig = MD5Helper.GetMD5Code("mobile=" + phoneNumber + "&" + "action=" + "1" + UrlConstants.sign);

        OkHttpUtils.post()
                .url(UrlConstants.ACTION_SENDVERIFyCODE)
                .addParams("mobile", phoneNumber)
                .addParams("action", 1 + "")
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(new Loading(this)) {
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
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getString(R.string.bindPhone));
//        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getString(R.string.bindPhone));
//        MobclickAgent.onPause(this);
    }

    private void bindPhone() {

        final String phoneNumber = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        String passWord = et_password.getText().toString().trim();

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
        if(!cb_agree.isChecked()){
            showToast("您必须得同意星发布用户协议才能继续");
            return;
        }
        StarReleaseUtil.closeImm(PhoneVerificationActivity.this);

        String token = SPCache.getInstance(this).getToken();
        String sig = MD5Helper.GetMD5Code("Authorization=" + token + "&mobile="+phoneNumber+"&code="+code+"&password="+passWord+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.BINDING_PHONENUM)
                .addParams("Authorization",token)
                .addParams("mobile", phoneNumber)
                .addParams("code", code)
                .addParams("password",passWord)
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(new Loading(PhoneVerificationActivity.this)){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        BaseResponse resp = BaseResponse.fromJson(response, BaseResponse.class);
                        if (resp.retCode == 0) {
                            SPCache.getInstance(PhoneVerificationActivity.this).savePhoneNum(phoneNumber);
                            startActivity(new Intent(PhoneVerificationActivity.this, MainActivity.class));
                            ActivityCollector.finishAll();
                        } else if (resp.retCode != 0) {
                            showToast(resp.desc);
                            return;
                        }
                    }
                });
    }
}

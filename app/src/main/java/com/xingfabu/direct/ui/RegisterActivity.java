package com.xingfabu.direct.ui;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.utils.LogUtil;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.utils.Verification;
import com.xingfabu.direct.widget.Loading;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
/**
 * Created by 郭平 on 2016/3/29 0029.
 */
public class RegisterActivity extends BaseActivity{
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(reclen == 0){
                tv_getcode.setText("获取验证码");
                tv_getcode.setEnabled(true);
                reclen = 60;
                return;
            }
            reclen--;
            tv_getcode.setText(reclen+"");
            handler.postDelayed(this, 1000);
        }
    };
    private int reclen = 60;
    private Button tv_getcode;
    private EditText et_phone,et_code,et_password;
    private CheckBox cb_agree;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_register);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
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

        TypedArray actionbarSizeTypedArray = this.obtainStyledAttributes(new int[] {
                android.R.attr.actionBarSize
        });
        float h = actionbarSizeTypedArray.getDimension(0, 0);
        LogUtil.e("actionBarSize",h+"");

        tv_getcode = (Button) findViewById(R.id.tv_getcode);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        et_password = (EditText) findViewById(R.id.et_password);
        cb_agree = (CheckBox) findViewById(R.id.cb_agree);

    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.tv_done:
                register();
                break;
            case R.id.wechat_login:
                startActivity(new Intent(RegisterActivity.this, PhoneVerificationActivity.class));
                break;
            case R.id.qq_login:
                startActivity(new Intent(RegisterActivity.this, PhoneVerificationActivity.class));
                break;
            case R.id.sina_login:
                startActivity(new Intent(RegisterActivity.this, PhoneVerificationActivity.class));
                break;
            case R.id.tv_getcode:
                getCode();
                break;
            case R.id.tv_protocol:
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
        tv_getcode.setEnabled(false);
        handler.post(runnable);

        String sig = MD5Helper.GetMD5Code("mobile="+phoneNumber+"&"+"action="+"1"+ UrlConstants.sign);

        OkHttpUtils.post()
                .url(UrlConstants.ACTION_SENDVERIFyCODE)
                .addParams("mobile", phoneNumber)
                .addParams("action", 1 + "")
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(new Loading(RegisterActivity.this)){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        BaseResponse resp = BaseResponse.fromJson(response, BaseResponse.class);

                        if (resp.retCode == 0) {

                        } else if (resp.retCode != 0) {
                            showToast(resp.desc);
                            return;
                        }
                    }
                });
    }

    private void register() {

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
        StarReleaseUtil.closeImm(RegisterActivity.this);

        String sig = MD5Helper.GetMD5Code("platform=2&"+"mobile="+phoneNumber+"&"+"code="+code+"&"+"password="+passWord+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.ACTION_REGISTER)
                .addParams("platform",2+"")
                .addParams("mobile", phoneNumber)
                .addParams("code", code)
                .addParams("password", passWord)
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(new Loading(RegisterActivity.this)){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        BaseResponse resp = BaseResponse.fromJson(response, BaseResponse.class);

                        if (resp.retCode == 0) {
                            SPCache.getInstance(RegisterActivity.this).savePhoneNum(phoneNumber);
                            showToast("注册成功");
                            //startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        } else if (resp.retCode != 0) {
                            showToast(resp.desc);
                            return;
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
//        MobclickAgent.onPageStart(getString(R.string.signup));
//        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getString(R.string.signup));
//        MobclickAgent.onPause(this);
    }
}

package com.xingfabu.direct.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;
import com.umeng.message.PushAgent;
import com.xingfabu.direct.utils.ActivityCollector;

/**
 * Activity的基类
 * Created by 郭平 on 2016/3/25 0025.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected int mScreenWidth;
    protected int mScreenHeight;
    private Toast mToast;
    protected AlertDialog exitDialog;// 退出提示

    public static final String ACTION_NETWORK_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String ACTION_PUSH_DATA = "fm.data.push.action";//自定义推送广播
    public static final String ACTION_NEW_VERSION = "apk.update.action";//自定义版本更新广播

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        StarReleaseUtil.setMiuiStatusBarDarkMode(this, true);
//        StarReleaseUtil.setMeizuStatusBarDarkIcon(this, true);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        setContentView();
        initViews();
        initListeners();
        initData();
        ActivityCollector.addActivity(this);

        PushAgent.getInstance(this).onAppStart();//友盟推送相关
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NETWORK_CHANGE);
        filter.addAction(ACTION_PUSH_DATA);
        filter.addAction(ACTION_NEW_VERSION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        //关闭统计
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale * dipValue + 0.5f);
    }

    public abstract void setContentView();

    public abstract void initViews();

    public abstract void initListeners();

    public abstract void initData();

    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    /**
     * 弹出退出应用对话框
     *
     * @param context
     */
    public void exitAlert(Context context) {
        AlertDialog.Builder buider = new AlertDialog.Builder(context);
        buider.setTitle("是否退出");
        buider.setMessage("");
        // 点击否什么都不做只是把对话框去掉
        buider.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        exitDialog.dismiss();
                    }
                });
        // 点击是时去掉对话框并做退出处理
        buider.setPositiveButton("是",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        exitDialog.dismiss();

                    }

                });
        exitDialog = buider.create();
        exitDialog.show();
    }

    private void setBase() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 基类广播接收者
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 处理各种情况
            String action = intent.getAction();
            if (ACTION_NETWORK_CHANGE.equals(action)) { // 网络发生变化
//                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//                if(networkInfo != null && networkInfo.isAvailable()){
//                    //showToast("有网");
//                }else{
//                    //showToast("没网");
//                }
                int i = getNetype(context);
                if (-1 == i) {
                    //showToast("无网络");
                } else if (1 == i) {
                    //showToast("WiFi状态");
                } else {
                    //showToast("数据流量");
                }
            } else if (ACTION_PUSH_DATA.equals(action)) { // 可能有新数据

            } else if (ACTION_NEW_VERSION.equals(action)) { // 可能发现新版本
                // VersionDialog 可能是版本提示是否需要下载的对话框
            }
        }
    };

    //返回值 -1：没有网络  1：WIFI网络2：CMwap网络3：CMnet网络
    public int getNetype(Context context) {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = 3;
            } else {
                netType = 2;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }
        return netType;
    }

    /**
     * 判断Moblie网络是否可用
     * @param context
     * @return
     */
    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断WIFI是否可用
     * @param context
     * @return
     */
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断是否网络链接
     * @param context
     * @return
     */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}

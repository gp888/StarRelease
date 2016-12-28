package com.xingfabu.direct.ui;

import android.content.pm.ActivityInfo;
import android.view.View;

import com.xingfabu.direct.R;

/**
 * Created by guoping on 2016/11/30.
 */

public class WatchLiveActivity extends BaseActivity implements View.OnClickListener{
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_watchlive);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {

    }

    public int changeScreenOri() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            immersiveStick();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
//            unImmersiveStick();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        return getRequestedOrientation();
    }
}

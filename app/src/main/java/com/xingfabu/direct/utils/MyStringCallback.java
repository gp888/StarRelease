package com.xingfabu.direct.utils;

import com.xingfabu.direct.widget.Loading;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by guoping on 16/4/11.
 */
public class MyStringCallback extends StringCallback{
    Loading mLoading = null;
    public MyStringCallback(Loading mLoading) {
        this.mLoading = mLoading;
    }

    public MyStringCallback(){

    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if(mLoading != null){
            mLoading.dismiss();
        }
    }

    @Override
    public void onResponse(String response, int id) {
        if(mLoading != null){
            mLoading.dismiss();
        }
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        if(mLoading != null){
            mLoading.show();
        }
    }

    @Override
    public void inProgress(float progress, long total, int id) {
        super.inProgress(progress, total, id);
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
        if(mLoading != null){
            mLoading.dismiss();
        }
    }
}

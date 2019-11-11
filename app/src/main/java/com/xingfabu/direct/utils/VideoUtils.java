package com.xingfabu.direct.utils;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xingfabu.direct.app.App;
import com.xingfabu.direct.app.Param;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.cache.SPCache;
import com.xingfabu.direct.entity.GetVhallToken;
import com.xingfabu.direct.ui.WatchActivity;
import com.xingfabu.direct.widget.Loading;
import com.zhy.http.okhttp.OkHttpUtils;

import io.rong.imlib.RongIMClient;
import okhttp3.Call;

/**
 * Created by guoping on 16/6/27.
 */
public class VideoUtils {
    private String xfbsid = null;
    private String xfbwebinar_id = null;
    private String  webinar_type = null;
    private String vhallToken = null;
    private String share_pic = null;
    private RongIMClient mRongIMClient;
    private Loading mLoading = null;
    private AppCompatActivity activity;
    private static VideoUtils mInstance = null;

    private boolean is_close;//霸屏榜
    private int surplus;//霸屏榜
    private int from;//来自哪

    public static VideoUtils getInstance() {
        if (mInstance == null) {
            synchronized (VideoUtils.class) {
                if (mInstance == null) {
                    mInstance = new VideoUtils();
                }
            }
        }
        return mInstance;
    }

    public void skipWatch(String room_id,String room_token) {
        Param param = getParam(room_id,room_token);
        Intent intent = new Intent(activity, WatchActivity.class);
        intent.putExtra("param", param);

        intent.putExtra("sid",xfbsid);
        intent.putExtra("share_pic",share_pic);

        intent.putExtra("is_close",is_close);
        intent.putExtra("surplus",surplus);
        intent.putExtra("from",from);
        if(mLoading != null){
            mLoading.dismiss();
            mLoading = null;
        }
        activity.startActivity(intent);
    }

    private Param getParam(String room_id,String room_token) {
        Param params = new Param();
        if("1".equals(webinar_type)){
            params.watch_type = Param.WATCH_LIVE;
        }else {
            params.watch_type = Param.WATCH_PLAYBACK ;
        }
        params.id = room_id;
        params.token = room_token;
        params.videoBitrate = 300 * 1000;//码率 300
        params.frameRate = 20;//帧率 20
        params.bufferSecond = 2;//缓冲时间2s
        params.k = "";//k值
        params.pixel_type = Param.XHDPI;//720P
        params.screenOri = 0;
        params.record_id = "";//回放ID
        return params;
    }


    public void prepareToPlay(String sid, String webinar_id, String webinar_type, String share_pic, AppCompatActivity activity, boolean is_close, int surplus, int from){
        this.activity = activity;
        mLoading = new Loading(activity);
        mLoading.show();
        this.xfbsid = sid;
        this.xfbwebinar_id = webinar_id;
        this.webinar_type = webinar_type;
        this.share_pic = share_pic;

        this.is_close = is_close;
        this.surplus = surplus;
        this.from = from;
        if("4".equals(webinar_type)){
            goVideo(xfbsid,xfbwebinar_id,0+"");
        }else if("1".equals(webinar_type)){
            goVideo(xfbsid,xfbwebinar_id,0+"");
        }else {
            mLoading.dismiss();
            return;
        }
    }

    /**
     * @param sid
     * @param webid
     * @param tourist 0代表不是游客1代表游客
     */
    public void goVideo(final String sid, final String webid, String tourist){
        String token = SPCache.getInstance(activity).getToken();
        if(TextUtils.isEmpty(token)){
            token = SPCache.getInstance(activity).getTouristToken();
            tourist = "1";
        }
        String sig = MD5Helper.GetMD5Code("sid=" + sid + "&" +  "webinar_id=" + webid + "&" + "tourist=" + tourist+"&" +"Authorization="+ token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.GET_VHALL_TOKEN)
                .addParams("sid",sid)
                .addParams("webinar_id",webid)
                .addParams("tourist",tourist)
                .addParams("Authorization",token)
                .addParams("sig",sig)
                .build()
                .execute(new MyStringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        if(mLoading != null){
                            mLoading.dismiss();
                            mLoading = null;
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        GetVhallToken res = GetVhallToken.fromJson(response,GetVhallToken.class);
                        if(res.retCode != 0){
                            Toast.makeText(activity,res.desc,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        vhallToken = res.result.token;
                        String rong = res.result.rongToken;
                        //是注册用户
                        if(!TextUtils.isEmpty(SPCache.getInstance(activity).getToken())){
                            SPCache.getInstance(activity).saveRongToken(rong);//保存融云Token
                        }
                        if(TextUtils.isEmpty(vhallToken)){
                            return;
                        }
                        if(TextUtils.isEmpty(rong)){
                            return;
                        }
                        connect(rong,webid,vhallToken);//增加游客逻辑
                    }
                });
    }

    /**
     * 建立与融云服务器的连接
     * @param token
     */
    private void connect(String token, final String webid, final String getToken) {

        if (activity.getApplicationInfo().packageName.equals(App.getCurProcessName(activity))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            mRongIMClient = RongIMClient.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.d("重新获取token", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    SPCache.getInstance(activity).saveRongUid(userid);
                    skipWatch(webid,getToken);
                }
                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.e("链接失败", "--onError" + errorCode);
                }
            });
        }
    }
}

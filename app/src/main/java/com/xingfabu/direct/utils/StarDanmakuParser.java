package com.xingfabu.direct.utils;

import android.content.Context;

import com.xingfabu.direct.emoji.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.android.JSONSource;
import master.flame.danmaku.danmaku.util.DanmakuUtils;

/**
 * Created by guoping on 16/5/23.
 */
public class StarDanmakuParser extends BaseDanmakuParser{
    private Context context;
    public StarDanmakuParser(Context context) {
        this.context = context;
    }

    @Override
    protected Danmakus parse() {
        if (mDataSource != null && mDataSource instanceof JSONSource) {
            JSONSource jsonSource = (JSONSource) mDataSource;
            return doParse(jsonSource.data());
        }
        return new Danmakus();
    }
    /**
     * @param danmakuListData 弹幕数据
     * @return 转换后的Danmakus
     */
    private Danmakus doParse(JSONArray danmakuListData) {
        Danmakus danmakus = new Danmakus();
        if (danmakuListData == null || danmakuListData.length() == 0) {
            return danmakus;
        }
        for (int i = 0; i < danmakuListData.length(); i++) {
            try {
                JSONObject danmakuArray = danmakuListData.getJSONObject(i);
                if (danmakuArray != null) {
                    danmakus = _parse(danmakuArray, danmakus);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return danmakus;
    }

    private Danmakus _parse(JSONObject jsonObject, Danmakus danmakus) {
        if (danmakus == null) {
            danmakus = new Danmakus();
        }
        if (jsonObject == null || jsonObject.length() == 0) {
            return danmakus;
        }
        try {
            JSONObject obj = jsonObject;
            String c = obj.getString("video_time");
            long time = (long) (Float.parseFloat(c)); // 出现时间
            int color = Integer.parseInt(16777215+"") | 0xFF000000; // 颜色

            BaseDanmaku item = mContext.mDanmakuFactory.createDanmaku(1, mContext);
            if (item != null) {
                item.time = time;
                item.textColor = color;
                item.textSize = 18 * (mDispDensity - 0.6f);
                //如果内容有表情,则替换为表情
                DanmakuUtils.fillText(item, StringUtil.stringToSpannableString(obj.optString("content", "...."),context));
                item.setTimer(mTimer);
                danmakus.addItem(item);
            }
        } catch (JSONException e) {
        }
        return danmakus;
    }
}

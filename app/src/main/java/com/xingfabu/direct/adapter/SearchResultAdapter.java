package com.xingfabu.direct.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xingfabu.direct.R;
import com.xingfabu.direct.entity.Search;

import java.util.List;

/**
 * Created by guoping on 16/6/24.
 */
public class SearchResultAdapter extends BaseQuickAdapter<Search> {

    Context context;

    public SearchResultAdapter(Context context, List<Search> data) {
        super(context, R.layout.item_search, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Search item) {
        baseViewHolder.setText(R.id.search_type, getWebinar(item.webinar_type))
                .setText(R.id.search_title, item.subject.trim())
                .setText(R.id.search_time, "直播时间:"+item.time.trim())
                .setText(R.id.search_hits,"播放次数:"+item.hits.trim());

        baseViewHolder.getView(R.id.seach_image).setTag(R.id.image_tag, item.pic);
        Glide.with(mContext)
                .load(item.pic)
                .placeholder(R.drawable.icon_zhan)
                .crossFade()
                .into((ImageView) baseViewHolder.getView(R.id.seach_image));
    }

    private  String getWebinar(String webinar){
        String type = null;
        switch(webinar){
            case "1"://
                type = "直播";
                break;
            case "2":
                type = "预告";
                break;
            case "3":
                type = "结束";
                break;
            case "4":
                type = "回放";
                break;
        }
        return type;
    }
}
package com.xingfabu.direct.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xingfabu.direct.R;
import com.xingfabu.direct.entity.Video;
import java.util.List;

/**
 * Created by guoping on 16/9/18.
 * 收藏,播放记录公用
 */
public class PlayRecordAdapter extends BaseQuickAdapter<Video> {
    public PlayRecordAdapter(Context context,List<Video> data) {
        super(context, R.layout.item_playrecord, data);
    }
    @Override
    protected void convert(BaseViewHolder baseViewHolder, Video item) {
        baseViewHolder.setText(R.id.time_record, item.getTime().substring(0,11))
                .setText(R.id.title_record, item.getSubject().trim())//标题
                .setText(R.id.desc_record, null == item.getAdd_name() ? mContext.getString(R.string.xingfabu) : item.getAdd_name());//发布方
        baseViewHolder.getView(R.id.icon_record).setTag(R.id.image_tag, item.getPic());
        Glide.with(mContext)
                .load(item.getPic())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.icon_zhan)
                .crossFade()
                .into((ImageView) baseViewHolder.getView(R.id.icon_record));
    }
}

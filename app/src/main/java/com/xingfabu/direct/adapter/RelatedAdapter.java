package com.xingfabu.direct.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xingfabu.direct.R;
import com.xingfabu.direct.entity.Video;
import java.util.List;

/**
 * Created by guoping on 16/6/24.
 */
public class RelatedAdapter extends BaseQuickAdapter<Video> {

    Context context;

    public RelatedAdapter(Context context, List<Video> data) {
        super(context, R.layout.item_related, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Video item) {
        baseViewHolder.setText(R.id.related_title, item.subject.trim())
                .setText(R.id.related_hits,"播放次数:"+item.hits.trim());

        baseViewHolder.getView(R.id.related_image).setTag(R.id.image_tag, item.pic);
        Glide.with(mContext)
                .load(item.pic)
                .placeholder(R.drawable.icon_zhan)
                .crossFade()
                .into((ImageView) baseViewHolder.getView(R.id.related_image));
    }
}
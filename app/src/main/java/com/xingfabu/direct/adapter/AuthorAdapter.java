package com.xingfabu.direct.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xingfabu.direct.R;
import com.xingfabu.direct.entity.LateUpdateAuthor;
import com.xingfabu.direct.transform.GlideCircleTransform;
import java.util.List;

/**
 * Created by guoping on 2016/11/15.
 */

public class AuthorAdapter extends BaseQuickAdapter<LateUpdateAuthor> {
    Context context;

    public AuthorAdapter(Context context, List<LateUpdateAuthor> data) {
        super(context, R.layout.item_author, data);
        this.context = context;
    }
    @Override
    protected void convert(BaseViewHolder baseViewHolder, LateUpdateAuthor video) {
        baseViewHolder.setText(R.id.author_subject, video.subject.trim())
                .setText(R.id.author_id,video.username.trim())
                .setText(R.id.author_share,video.share_num)
                .setText(R.id.author_hits,video.hits);

        baseViewHolder.getView(R.id.timeline_pic).setTag(R.id.image_tag, video.pic);
        Glide.with(mContext)
                .load(video.pic)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.icon_zhan)
                .crossFade()
                .into((ImageView) baseViewHolder.getView(R.id.timeline_pic));

        baseViewHolder.getView(R.id.author_head).setTag(R.id.image_tag, video.pic);
        Glide.with(mContext)
                .load(video.user_pic)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade()
                //.placeholder(R.mipmap.ic_launcher)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) baseViewHolder.getView(R.id.author_head));
    }
}

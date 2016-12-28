package com.xingfabu.direct.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xingfabu.direct.R;
import com.xingfabu.direct.entity.HotBean;
import java.util.List;

/**
 * Created by guoping on 16/6/22.
 */
public class HotPlayAdapter extends BaseQuickAdapter<HotBean> {
    Context context;

    public HotPlayAdapter(Context context, List<HotBean> data) {
        super(context, R.layout.item_hotplay, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final HotBean item) {

        baseViewHolder.setText(R.id.hot_title, item.subject.trim())
                      .setText(R.id.hot_content, item.host == "制作人:" ? null : "制作人:"+item.host.trim())
                .setText(R.id.hits,item.support.trim())
                .setText(R.id.at_id,item.position.trim())
                .setText(R.id.share,item.share_num);

        baseViewHolder.getView(R.id.pic_show).setTag(R.id.image_tag, item.pic);
        Glide.with(mContext)
                .load(item.pic)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.icon_zhan)
                .crossFade()
                .into((ImageView) baseViewHolder.getView(R.id.pic_show));
    }
}
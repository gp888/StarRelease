package com.xingfabu.direct.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xingfabu.direct.R;
import com.xingfabu.direct.entity.MessageResponse;
import com.xingfabu.direct.entity.Video;

import java.util.List;

/**
 * Created by guoping on 16/9/20.
 */
public class SystemMessageAdapter extends BaseQuickAdapter<MessageResponse.SystemMessage> {
    public SystemMessageAdapter(Context context, List<MessageResponse.SystemMessage> data) {
        super(context, R.layout.item_message, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MessageResponse.SystemMessage systemMessage) {
        baseViewHolder.setText(R.id.message_title, systemMessage.subject.trim())
                .setText(R.id.message_date, systemMessage.time.trim())
                .setText(R.id.message_detail, null == systemMessage.content ? "" : systemMessage.content);
    }
}

package com.xingfabu.direct.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xingfabu.direct.R;
import com.xingfabu.direct.entity.Video;
import com.xingfabu.direct.transform.GlideCircleTransform;
import com.xingfabu.direct.utils.StarReleaseUtil;

import java.util.List;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by guoping on 2016/10/22.
 */

public class RecommendAdapter extends BaseQuickAdapter<Video> {
    Context context;

    public RecommendAdapter(Context context, List<Video> data) {
        super(context, R.layout.item_video, data);
        this.context = context;
    }
    @Override
    protected void convert(BaseViewHolder baseViewHolder, Video item) {

        String type = item.webinar_type;//1直播2预约3结束4录播
        switch(type){
            case "1":
                baseViewHolder.setVisible(R.id.video_live,false)//隐藏直播
                        .setVisible(R.id.video_yu,false)//8
                        .setVisible(R.id.video_playback,false)
                        .setVisible(R.id.video_finish,false)
                        .setVisible(R.id.video_riqi,false)
                        .setVisible(R.id.video_count,false);
                break;
            case "4":
                baseViewHolder.setVisible(R.id.video_live,false)
                        .setVisible(R.id.video_yu,false)
                        .setVisible(R.id.video_playback,false)//隐藏回放
                        .setVisible(R.id.video_finish,false)
                        .setVisible(R.id.video_riqi,true)
                        .setVisible(R.id.video_count,false);
                break;
            case "2":
                baseViewHolder.setVisible(R.id.video_live,false)
                        .setVisible(R.id.video_yu,false)//隐藏预告
                        .setVisible(R.id.video_playback,false)
                        .setVisible(R.id.video_finish,false)
                        .setVisible(R.id.video_riqi,false)
                        .setVisible(R.id.video_count,true);
                CountdownView c = baseViewHolder.getView(R.id.video_countview);
                c.start(item.count_down*1000);
                break;
            case "3":
                baseViewHolder.setVisible(R.id.video_live,false)
                        .setVisible(R.id.video_yu,false)
                        .setVisible(R.id.video_playback,false)
                        .setVisible(R.id.video_finish,false)//隐藏结束
                        .setVisible(R.id.video_riqi,true)
                        .setVisible(R.id.video_count,false);
                break;
        }
        baseViewHolder.getView(R.id.video_pic).setTag(R.id.image_tag, item.getPic());
        Glide.with(mContext)
                .load(item.getPic())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.icon_zhan)
                .crossFade()
                .into((ImageView) baseViewHolder.getView(R.id.video_pic));

        baseViewHolder.getView(R.id.video_logo).setTag(R.id.image_tag,item.getUser_pic());
        Glide.with(mContext)
                .load(item.getUser_pic())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade()
                .placeholder(R.mipmap.ic_launcher)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) baseViewHolder.getView(R.id.video_logo));


        baseViewHolder.setText(R.id.video_subject,item.subject.trim())
                .setText(R.id.video_date,StarReleaseUtil.timet(item.start_time))
                .setText(R.id.video_hits,item.hits)
                .setText(R.id.author_share,item.share_num)
                .setText(R.id.video_id,item.add_name.trim());


//        baseViewHolder.getView(R.id.icon_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //String position = (String) v.getTag(R.id.image_tag);
//            }
//        });
    }
}

package com.xingfabu.direct.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.entity.LateUpdateAuthor;
import com.xingfabu.direct.entity.MySection;
import com.xingfabu.direct.entity.MySectionItem;
import com.xingfabu.direct.transform.GlideCircleTransform;
import com.xingfabu.direct.utils.VideoUtils;

import java.util.List;

/**
 * Created by guoping on 2016/11/8.
 */

public class SectionAdapter extends BaseSectionQuickAdapter<MySection> {
    private Activity activity;
    /**
     * @param sectionHeadResId The section head horigrid id for each item
     * @param layoutResId The horigrid resource id of each item.
     *@param data  A new list is created out of this one to avoid mutable list
    */
    public SectionAdapter(Activity activity, int layoutResId, int sectionHeadResId, List<MySection> data) {
        super(activity,layoutResId, sectionHeadResId, data);
        this.activity = activity;
    }
    @Override
    protected void convertHead(BaseViewHolder baseViewHolder, MySection item) {
        baseViewHolder.setText(R.id.section_name,item.header);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MySection item) {
        final MySectionItem item_author = item.t;
        baseViewHolder.setText(R.id.author_name,item_author.username)
                .setText(R.id.author_describe,item_author.describe == null ? "ta很懒,什么也没有留下!":item_author.describe)
                .setText(R.id.video_count,item_author.video_num+"个视频");

        if(item_author.videos == null || item_author.videos.size() == 0){
            baseViewHolder.setVisible(R.id.author_video,false);
        }else{
            baseViewHolder.setVisible(R.id.author_video,true);

            RecyclerView recyclerView = baseViewHolder.getView(R.id.author_video);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));
            HoriScrollAdapter horiScrollAdapter = new HoriScrollAdapter(activity,item_author.videos);
            recyclerView.setAdapter(horiScrollAdapter);
            horiScrollAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    LateUpdateAuthor v = item_author.videos.get(position);
                    VideoUtils.getInstance().prepareToPlay(v.sid,v.webinar_id,v.webinar_type,v.pic,activity,true,0, Constants.STAR_INFO);
                }
            });
        }
        Glide.with(mContext)
                .load(item_author.pic)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade()
                //.placeholder(R.mipmap.ic_launcher)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) baseViewHolder.getView(R.id.author_pic));
    }

    //图片
    class HoriScrollAdapter extends BaseQuickAdapter<LateUpdateAuthor>{
        private Context context;
        public HoriScrollAdapter(Context context, List<LateUpdateAuthor> data){
            super(context,R.layout.horigrid, data);
            this.context = context;
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, LateUpdateAuthor video) {
            baseViewHolder.setText(R.id.grid_title,video.subject)
                    .setText(R.id.grid_scribe,"#" + video.host + "  /  " + video.time.replace("-","."));

            baseViewHolder.getView(R.id.grid_pic).setTag(R.id.image_tag, video.pic);
            Glide.with(mContext)
                    .load(video.pic)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.icon_zhan)
                    .crossFade()
                    .into((ImageView) baseViewHolder.getView(R.id.grid_pic));
        }
    }
}

package com.xingfabu.direct.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xingfabu.direct.R;
import com.xingfabu.direct.entity.Video;
import com.xingfabu.direct.transform.GlideCircleTransform;
import com.xingfabu.direct.utils.StarReleaseUtil;

import java.util.List;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by guoping on 16/4/5.
 */
public class RecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private List<Video> data;
    private OnRecyclerViewItemClick mItemClickListener;

    public static interface OnRecyclerViewItemClick{
        void onItemClick(View view, Video data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClick mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }
    public RecyclerAdapter(List<Video> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        if(viewType == 3 || viewType == 4) {
//            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.horigrid.item_playback, parent, false));
//            return holder;
//        }else if(viewType == 1 || viewType == 2){
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live,parent,false);
//            view.setOnClickListener(this);
        View view = null;
            MyViewHolderland holder = new MyViewHolderland(view);
            return holder;
//        }
//        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

//        if(holder instanceof MyViewHolder){
//            //holder.tv.setT();
//        }else if(holder instanceof MyViewHolderland){
//
//        }
        if(holder instanceof MyViewHolderland){
            String type = data.get(position).webinar_type;//1直播2预约3结束4录播
            if("1".equals(type)){
                ((MyViewHolderland) holder).ll_live.setVisibility(View.VISIBLE);
                ((MyViewHolderland) holder).ll_yu.setVisibility(View.GONE);
                ((MyViewHolderland) holder).tv_flag.setVisibility(View.GONE);
                ((MyViewHolderland) holder).tv_finish.setVisibility(View.GONE);

                ((MyViewHolderland) holder).ll_riqi.setVisibility(View.GONE);
                ((MyViewHolderland) holder).ll_count.setVisibility(View.GONE);
            }else if("4".equals(type)){
                ((MyViewHolderland) holder).ll_live.setVisibility(View.GONE);
                ((MyViewHolderland) holder).ll_yu.setVisibility(View.GONE);
                ((MyViewHolderland) holder).tv_flag.setVisibility(View.VISIBLE);
                ((MyViewHolderland) holder).tv_finish.setVisibility(View.GONE);

                ((MyViewHolderland) holder).ll_riqi.setVisibility(View.VISIBLE);
                ((MyViewHolderland) holder).ll_count.setVisibility(View.GONE);
            }else if("2".equals(type)){
                ((MyViewHolderland) holder).ll_live.setVisibility(View.GONE);
                ((MyViewHolderland) holder).ll_yu.setVisibility(View.VISIBLE);
                ((MyViewHolderland) holder).tv_flag.setVisibility(View.GONE);
                ((MyViewHolderland) holder).tv_finish.setVisibility(View.GONE);

                ((MyViewHolderland) holder).ll_riqi.setVisibility(View.GONE);
                ((MyViewHolderland) holder).ll_count.setVisibility(View.VISIBLE);
                ((MyViewHolderland) holder).count_view.start(data.get(position).count_down*1000);
            }else if("3".equals(type)){
                ((MyViewHolderland) holder).ll_live.setVisibility(View.GONE);
                ((MyViewHolderland) holder).ll_yu.setVisibility(View.GONE);
                ((MyViewHolderland) holder).tv_flag.setVisibility(View.GONE);
                ((MyViewHolderland) holder).tv_finish.setVisibility(View.VISIBLE);

                ((MyViewHolderland) holder).ll_riqi.setVisibility(View.VISIBLE);
                ((MyViewHolderland) holder).ll_count.setVisibility(View.GONE);
            }
            ((MyViewHolderland) holder).tv_title.setText(data.get(position).subject.trim());

            ((MyViewHolderland) holder).land.setTag(R.id.image_tag, data.get(position).getPic());
            Glide.with(((MyViewHolderland) holder).land.getContext())
                    .load(data.get(position).getPic())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .crossFade()
                    .placeholder(R.drawable.icon_zhan)
                    .into(((MyViewHolderland) holder).land);

            ((MyViewHolderland) holder).tv_date.setText(StarReleaseUtil.timet(data.get(position).start_time));
            ((MyViewHolderland) holder).times.setText(data.get(position).hits + "次");

            ((MyViewHolderland) holder).icon_head.setTag(R.id.image_tag, data.get(position).getUser_pic());
            Glide.with(((MyViewHolderland) holder).icon_head.getContext())
                    .load(data.get(position).getUser_pic())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .crossFade()
                    .placeholder(R.mipmap.ic_launcher)
                    .transform(new GlideCircleTransform(((MyViewHolderland) holder).icon_head.getContext()))
                    .into(((MyViewHolderland) holder).icon_head);

            ((MyViewHolderland) holder).tv_id.setText(data.get(position).add_name.trim());

            holder.itemView.setTag(data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {
        if(mItemClickListener != null) {
            mItemClickListener.onItemClick(v,(Video) v.getTag());
        }
    }

//    class MyViewHolder extends RecyclerView.ViewHolder{
//        TextView title,name,time,flag;
//        ImageView icon;
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            title = (TextView) itemView.findViewById(R.id.tv_title);
//            name = (TextView) itemView.findViewById(R.id.tv_desc);
//            time = (TextView) itemView.findViewById(R.id.tv_time);
//            icon = (ImageView) itemView.findViewById(R.id.icon_back);
//            flag = (TextView) itemView.findViewById(R.id.tv_flag);
//
//        }
//    }
    class MyViewHolderland extends RecyclerView.ViewHolder{
        ImageView land,icon_head;
        TextView tv_flag,times,tv_date,tv_id,tv_title,tv_finish;
        LinearLayout ll_live,ll_yu,ll_riqi,ll_count;
        CountdownView count_view;
        public MyViewHolderland(View itemView){
            super(itemView);
//            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
//            ll_live = (LinearLayout) itemView.findViewById(R.id.ll_live);
//            land = (ImageView) itemView.findViewById(R.id.icon_land);
//            icon_head = (ImageView) itemView.findViewById(R.id.icon_head);
//            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
//            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
//            times = (TextView) itemView.findViewById(R.id.tv_times);
//            tv_flag = (TextView) itemView.findViewById(R.id.tv_flag);
//            ll_yu = (LinearLayout) itemView.findViewById(R.id.ll_yu);
//            tv_finish = (TextView) itemView.findViewById(R.id.tv_finish);
//            ll_riqi = (LinearLayout) itemView.findViewById(R.id.ll_riqi);//普通时间
//            ll_count = (LinearLayout) itemView.findViewById(R.id.ll_count);//倒计时
//            count_view = (CountdownView) itemView.findViewById(R.id.count_view);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(data.get(position).webinar_type);
    }
}

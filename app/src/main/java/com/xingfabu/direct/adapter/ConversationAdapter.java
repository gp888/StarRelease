package com.xingfabu.direct.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.xingfabu.direct.R;
import com.xingfabu.direct.emoji.StringUtil;
import com.xingfabu.direct.entity.Barrage;
import com.xingfabu.direct.transform.GlideCircleTransform;
import java.util.List;

/**
 * Created by guoping on 16/5/9.
 */
public class ConversationAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    private List<Barrage> data;
    private OnRecyclerViewItemClick mItemClickListener;
    private Context context;

    public static interface OnRecyclerViewItemClick{
        void onItemClick(View view, Barrage barrage);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClick mItemClickListener){
        this.mItemClickListener = mItemClickListener;
    }
    public ConversationAdapter(List<Barrage> data,Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversationitem,parent,false);
        view.setOnClickListener(this);
        MyViewHolderland holder = new MyViewHolderland(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolderland){

            ((MyViewHolderland) holder).icon_head.setTag(R.id.image_tag, data.get(position).pic);
            Glide.with(context.getApplicationContext())
                    .load(data.get(position).pic)
                    .placeholder(R.drawable.icon_mo)
                    .transform(new GlideCircleTransform(((MyViewHolderland) holder).icon_head.getContext()))
                    .into(((MyViewHolderland) holder).icon_head);

//            Glide.with(((MyViewHolderland) holder).icon_head.getContext())
//                    .load(data.get(position).pic)
//                    .asBitmap().centerCrop()
//                    .placeholder(R.drawable.icon_mo)
//                    .into(new BitmapImageViewTarget(((MyViewHolderland) holder).icon_head) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    RoundedBitmapDrawable circularBitmapDrawable =
//                            RoundedBitmapDrawableFactory.create(((MyViewHolderland) holder).icon_head.getContext().getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    ((MyViewHolderland) holder).icon_head.setImageDrawable(circularBitmapDrawable);
//                }
//            });
            ((MyViewHolderland) holder).conversation.setImageResource(R.drawable.conversation);
            ((MyViewHolderland) holder).tv_nickName.setText(data.get(position).name);
            //如果有表情就显示表情
            ((MyViewHolderland) holder).tv_speak.setText(StringUtil.stringToSpannableString(data.get(position).content,context));

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
            mItemClickListener.onItemClick(v,(Barrage) v.getTag());
        }
    }

    class MyViewHolderland extends RecyclerView.ViewHolder{
        ImageView icon_head,conversation;
        TextView tv_nickName,tv_speak;
        public MyViewHolderland(View itemView){
            super(itemView);
            icon_head = (ImageView) itemView.findViewById(R.id.icon_head);
            conversation = (ImageView) itemView.findViewById(R.id.conversation);
            tv_nickName = (TextView) itemView.findViewById(R.id.tv_nickName);
            tv_speak = (TextView) itemView.findViewById(R.id.tv_speak);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return 0;
    }
}

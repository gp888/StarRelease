package com.xingfabu.direct.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.xingfabu.direct.R;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.VideoDetail;
import com.xingfabu.direct.transform.GlideCircleTransform;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;

public class VideoDetailFragment extends Fragment {
    private ImageView icon_head;
    private TextView tv_orgnazition,tv_title,tv_time,tv_place,tv_yiren,tv_detail;
    private String sid,webinar_id;

    public static VideoDetailFragment newInstance(String sid,String webinar_id) {
        VideoDetailFragment vf = new VideoDetailFragment();
        Bundle b = new Bundle();
        b.putString("detail_sid",sid);
        b.putString("detail_inarid",webinar_id);
        vf.setArguments(b);
        return vf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sid = getArguments().getString("detail_sid");
        webinar_id = getArguments().getString("detail_inarid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video_detail, container, false);

        icon_head = (ImageView) v.findViewById(R.id.icon_head);
        tv_orgnazition = (TextView) v.findViewById(R.id.tv_orgnazition);
        tv_title = (TextView) v.findViewById(R.id.tv_title);
        tv_time = (TextView) v.findViewById(R.id.tv_time);
        tv_place = (TextView) v.findViewById(R.id.tv_place);
        tv_yiren = (TextView) v.findViewById(R.id.tv_yiren);//星发布官方
        tv_detail = (TextView) v.findViewById(R.id.tv_detail);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getVideoContent(sid,webinar_id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * 获取视频详情
     */
    public void getVideoContent(String sid,String webinar_id) {
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("sid=" + sid + "&"+"webinar_id="+ webinar_id +"&"+"Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.VIDEO_DETAIL)
                .addParams("sid",sid)
                .addParams("webinar_id",webinar_id)
                .addParams("Authorization",token)
                .addParams("sig",sig)
                .build()
                .execute(new MyStringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        VideoDetail res = VideoDetail.fromJson(response,VideoDetail.class);
                        if(res.result.star.size() > 0){
                            if(getActivity() != null && !getActivity().isFinishing()){
                                Glide.with(getContext())
                                        .load(res.result.star.get(0).pic)
                                        .placeholder(R.mipmap.ic_launcher)
                                        .crossFade()
                                        .transform(new GlideCircleTransform(getContext()))
                                        .into(icon_head);
                            }
                        }
                        tv_orgnazition.setText(res.result.name);
                        tv_title.setText(res.result.subject);
                        tv_time.setText("直播时间:"+res.result.time);
                        tv_place.setText("直播地点:"+res.result.city_name);
                        tv_detail.setText(res.result.introduction);
                    }
                });
    }

    public void setVideoDetail(VideoDetail videoDetail){
        VideoDetail.Data data = videoDetail.result;
        if(data.star.size() > 0){
            if(getActivity() != null && !getActivity().isFinishing()) {
                Glide.with(icon_head.getContext())
                        .load(data.star.get(0).pic)
                        .placeholder(R.mipmap.ic_launcher)
                        .crossFade()
                        .transform(new GlideCircleTransform(icon_head.getContext()))
                        .into(icon_head);
            }
        }
        tv_orgnazition.setText(data.name);
        tv_title.setText(data.subject);
        tv_time.setText("直播时间:"+data.time);
        tv_place.setText("直播地点:"+data.city_name);
        tv_detail.setText(data.introduction);
    }
}




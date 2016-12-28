package com.xingfabu.direct.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.RelatedAdapter;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.app.Param;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.ClassResponse;
import com.xingfabu.direct.entity.Video;
import com.xingfabu.direct.ui.WatchActivity;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.utils.VideoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.List;

import master.flame.danmaku.ui.widget.DanmakuView;
import okhttp3.Call;

public class RelatedFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private String sidxfb;
    private RelatedAdapter relatedAdapter;
    private List<Video> related_data;

    public static RelatedFragment newInstance(String webinar_id) {
        RelatedFragment rf = new RelatedFragment();
        Bundle b = new Bundle();
        b.putString("sidxfb",webinar_id);
        rf.setArguments(b);
        return rf;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sidxfb = getArguments().getString("sidxfb");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view= inflater.inflate(R.layout.fragment_related,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_related);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        initAdapter();
        getData(sidxfb);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("相关");
    }
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("相关");
    }

    private void initAdapter() {
        related_data = new ArrayList<>();
        relatedAdapter = new RelatedAdapter(getContext(),related_data);
        relatedAdapter.openLoadMore(false);
        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.empty_recyclerview, (ViewGroup) mRecyclerView.getParent(), false);
        relatedAdapter.setEmptyView(emptyView);

        relatedAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                WatchActivity watchActivity = (WatchActivity) getActivity();
                if(watchActivity !=null){
                    DanmakuView danmu = watchActivity.mDanmakuView;
                    if(danmu != null){
                        danmu.release();
                    }
                    watchActivity.pb.setVisibility(View.GONE);
//                    WatchPlayback watchPlayback = watchActivity.getWatchPlayback();
//                    WatchLive watchLive = watchActivity.getWatchLive();
                    int watchType = watchActivity.param.watch_type;
                    if(watchType == Param.WATCH_PLAYBACK){
                        watchActivity.stopPlay();
//                        watchPlayback.pause();
//                        watchPlayback.stop();
//                        watchPlayback.destory();
                    }else {
                        watchActivity.stopPlay();
//                        watchLive.stop();
//                        watchLive.destory();
                    }
                }
                Video v = related_data.get(position);
                VideoUtils.getInstance().prepareToPlay(v.sid,v.webinar_id,v.webinar_type,v.pic,getActivity(),true,0,Constants.RELATED);
            }
        });
        mRecyclerView.setAdapter(relatedAdapter);
    }

    public void getData(String inner_id) {
        String token = StarReleaseUtil.getToken(getContext());
        OkHttpUtils.post()
                .url(UrlConstants.GET_RELATED)
                .addParams("sid",inner_id)
                .addParams("Authorization",token)
                .build()
                .execute(new MyStringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        ClassResponse resp = ClassResponse.fromJson(response, ClassResponse.class);
                        if (resp.retCode == 0) {
                            if (resp.retCode == 0) {
                                related_data = resp.result;
                                if(related_data == null || related_data.size() == 0){
                                    return;
                                }
                                relatedAdapter.setNewData(related_data);
                            }
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
}
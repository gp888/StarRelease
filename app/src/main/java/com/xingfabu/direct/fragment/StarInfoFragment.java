package com.xingfabu.direct.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.SectionAdapter;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.MySection;
import com.xingfabu.direct.entity.MySectionItem;
import com.xingfabu.direct.entity.StarInfoResponse;
import com.xingfabu.direct.entity.Video;
import com.xingfabu.direct.ui.AuthorDetail;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.widget.DividerItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;

/**
 * Created by 郭平 on 2016/3/26 0026.
 */
public class StarInfoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecycler;
    private SectionAdapter adapter;
    private List<MySection> mData;
    private int page  = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starinfo,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.info_swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.newpink);

        mRecycler = (RecyclerView) view.findViewById(R.id.info_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        initAdapter();
        onRefresh();
    }

    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        page = 1;
        getData(0, page + "");
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        getData(1, page + "");
    }

    private void initAdapter() {
        mData = new ArrayList<>();
        adapter = new SectionAdapter(getActivity(),R.layout.item_section_content, R.layout.item_section_head, mData);
        adapter.openLoadAnimation();
        adapter.openLoadMore(true);
        adapter.setOnLoadMoreListener(this);

//        View hearderView = getView(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        adapter.addHeaderView(hearderView);
            //暂时屏蔽
//        View emptyView = getEmptyView(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onRefresh();
//            }
//        });
//        adapter.setEmptyView(true,true,emptyView);

        mRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mRecycler.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MySection setionItemt = mData.get(position);
                if(setionItemt.isHeader){
                    return;
                }else {
                    MySectionItem mySectionItem = setionItemt.t;
                    String id = mySectionItem.id;
                    String user_pic = mySectionItem.pic;
                    Intent author_detail = new Intent(getActivity(), AuthorDetail.class);
                    author_detail.putExtra("author_id",id);
                    author_detail.putExtra("author_pic",user_pic);
                    startActivity(author_detail);
                }
            }
        });
    }

    private void getData(final int flag, final String page) {
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("page=" + page +"&Authorization="+token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.STAR_INFO)
                .addParams("page", page)
                .addParams("Authorization",token)
                .addParams("sig",sig)
                .build()
                .execute(new MyStringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        StarInfoResponse resp = StarInfoResponse.fromJson(response, StarInfoResponse.class);
                        if (resp.retCode == 0) {
                            if(flag == 0){
                                mData.clear();
                                List<MySectionItem> hotRows = resp.result.hotRows;
                                if(hotRows != null || hotRows.size() != 0){
                                    mData.add(new MySection(true,"热门"));
                                    for(int i= 0; i<hotRows.size(); i++){
                                        mData.add(new MySection(hotRows.get(i)));
                                    }
                                }
                                List<MySectionItem> latelyRows = resp.result.latelyRows;
                                if(latelyRows != null || latelyRows.size() != 0){
                                    mData.add(new MySection(true,"最新"));
                                    for(int i= 0; i<latelyRows.size(); i++){
                                        mData.add(new MySection(latelyRows.get(i)));
                                    }
                                }
                                List<MySectionItem> allRows = resp.result.allRows;
                                if(allRows != null || allRows.size() != 0){
                                    mData.add(new MySection(true,"全部"));
                                    for(int i= 0; i<allRows.size(); i++){
                                        mData.add(new MySection(allRows.get(i)));
                                    }
                                }
                                adapter.setNewData(mData);
                                adapter.openLoadMore(mData.size() - 3,true);
                                mSwipeRefreshLayout.setRefreshing(false);
                            } else if(flag == 1){
                                if(resp.result.allRows.size() == 0){
                                    Toast.makeText(getContext(),"没有了.",Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataChangedAfterLoadMore(false);
                                    return;
                                }
                                adapter.notifyDataChangedAfterLoadMore(resp.result.allRows, true);
                            }
                        }
                    }
                });
    }

//    private View getView(View.OnClickListener listener) {
//        View view = getActivity().getLayoutInflater().inflate(R.layout.hot_play_head, null);
//        view.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        view.setOnClickListener(listener);
//        return view;
//    }

    private View getEmptyView(View.OnClickListener listener) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.empty_recyclerview, (ViewGroup) mRecycler.getParent(), false);
        View emptyView = view.findViewById(R.id.ll_empty);
        emptyView.setOnClickListener(listener);
        return view;
    }
}

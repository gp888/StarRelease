package com.xingfabu.direct.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.HotPlayAdapter;
import com.xingfabu.direct.adapter.NetworkImageHolderView;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.HotAdver;
import com.xingfabu.direct.entity.HotBean;
import com.xingfabu.direct.entity.HotModel;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.utils.VideoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
/**
 * Created by guoping on 16/4/5.
 */
public class HotPlayFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener,ViewPager.OnPageChangeListener,OnItemClickListener {
    private RecyclerView mRecyclerView;
    private List<HotBean> data = new ArrayList<>();
    private HotPlayAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int page  = 1;
    private ConvenientBanner adver_scroll;
    private List<HotAdver.ItemAdver> adverImages = new ArrayList<>();
    private LinearLayout ll_adver;
    private TextView adver_title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view= inflater.inflate(R.layout.fragment_hotplay,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.hotplay_swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.prac_end);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.hotplay_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));

        initAdapter();
        onRefresh();
    }

    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("霸屏榜");
        adver_scroll.startTurning(3000);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                onRefresh();
//            }
//        }, 1000);
    }
    public void onPause() {
        super.onPause();
        adver_scroll.stopTurning();
//        MobclickAgent.onPageEnd("霸屏榜");
    }

    private void getData(final int flag, final String page) {
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("page=" + page +"&Authorization="+token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.RANK)
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
                        HotModel resp = HotModel.fromJson(response, HotModel.class);
                        if (resp.retCode == 0) {
                            List<HotBean> list = resp.result.items;
                            if(flag == 0) {
                                if(list == null || list.size() == 0){
                                    return;
                                }

                                data.clear();
                                data.addAll(list);
                                //1-10
                                for(int i = 0;i<data.size();i++){
                                    data.get(i).position = (i+1) + "" ;
                                }
                                adapter.setNewData(data);
                                adapter.openLoadMore(10,true);

                                mSwipeRefreshLayout.setRefreshing(false);
                            }else if(flag == 1){
                                if(list.size() == 0){
                                    Toast.makeText(getContext(),"没有了",Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataChangedAfterLoadMore(false);
                                    return;
                                }
                                int pageSize = Integer.parseInt(page);
                                for(int i = 0;i< resp.result.items.size();i++){
                                    list.get(i).position = (pageSize - 1) * 10 +i + 1 +"";
                                }
                                adapter.notifyDataChangedAfterLoadMore(resp.result.items, true);
                            }
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        page = 1;
        getAdver();
        getData(0, page + "");
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        getData(1, page + "");
    }

    private void addHeadView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.hot_play_head,null);
        adver_scroll = (ConvenientBanner) view.findViewById(R.id.hot_banner);
        ll_adver = (LinearLayout) view.findViewById(R.id.ll_adver);
        adver_title = (TextView) view.findViewById(R.id.adver_title);
        adapter.addHeaderView(view);
    }

    private View getEmptyView(View.OnClickListener listener) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.empty_recyclerview, (ViewGroup) mRecyclerView.getParent(), false);
        View emptyView = view.findViewById(R.id.ll_empty);
        emptyView.setOnClickListener(listener);
        return view;
    }

    private void initAdapter() {
        adapter = new HotPlayAdapter(getContext(),null);
        adapter.openLoadAnimation();
        adapter.openLoadMore(true);
        adapter.setOnLoadMoreListener(this);

        addHeadView();
//        View headPic = LayoutInflater.from(getContext()).inflate(R.horigrid.hot_play_head,null);
//        View emptyView = LayoutInflater.from(getContext()).inflate(R.horigrid.empty_recyclerview, (ViewGroup) mRecyclerView.getParent(), false);
        View emptyView = getEmptyView(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //空数据点击
                onRefresh();
            }
        });
        adapter.setEmptyView(true,true,emptyView);
//        adapter.setEmptyView(emptyView);

        mRecyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HotBean hot = data.get(position);
                VideoUtils.getInstance().prepareToPlay(hot.sid,hot.webinar_id,hot.webinar_type,hot.pic,getActivity(),hot.is_close,hot.surplus, Constants.BAPINGBANG);
            }
        });
    }

    private void getAdver(){
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.HOT_ADVER)
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
                        HotAdver resp = HotAdver.fromJson(response, HotAdver.class);
                        List<HotAdver.ItemAdver> data = resp.result.list;
                        if(data == null ||data.size() == 0){
                            return;
                        }
                        adver_scroll.setVisibility(View.VISIBLE);
                        ll_adver.setVisibility(View.VISIBLE);

                        adverImages.clear();
                        adverImages.addAll(data);

                        List<String> url_pic = new ArrayList<>();
                        for(HotAdver.ItemAdver s:adverImages){
                            url_pic.add(s.pic);
                        }
                        adver_scroll.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                            @Override
                            public NetworkImageHolderView createHolder() {
                                return new NetworkImageHolderView();
                            }
                        },url_pic)
                                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                                .setOnPageChangeListener(HotPlayFragment.this)
                                .setOnItemClickListener(HotPlayFragment.this);

                        adver_title.setText(adverImages.get(0).title);
                    }
                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        adver_title.setText(adverImages.get(position).title);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(int position) {
        click_adver(adverImages.get(position).id);
        String action_url = adverImages.get(position).url;
        if(action_url != null && action_url.length() != 0){
            Uri uri = Uri.parse(action_url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void click_adver(String id){
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("type=3&" + "market_id=" + id + "&Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.ADVER_CLICK)
                .addParams("type","3")
                .addParams("market_id",id)
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
                    }
                });
    }
}
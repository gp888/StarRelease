package com.xingfabu.direct.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.NetworkImageHolderView;
import com.xingfabu.direct.adapter.RecommendAdapter;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.Advertisment;
import com.xingfabu.direct.entity.Banner;
import com.xingfabu.direct.entity.BannerDetail;
import com.xingfabu.direct.entity.DirectBroadcastResponse;
import com.xingfabu.direct.entity.Video;
import com.xingfabu.direct.ui.NoticeActivity;
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
public class RecommendFragment extends Fragment implements ViewPager.OnPageChangeListener,OnItemClickListener,BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recommend_rv;
    private List<Video> click_data = new ArrayList<>();
    private List<Video> video_data = new ArrayList<>();
    private RecommendAdapter recommendAdapter;
    private int page  = 1;
    private ConvenientBanner auto_scroll;
    private List<BannerDetail> networkImages = new ArrayList<>();
    private TextView banner_title;
    ImageView banner_ad;
    private static final int PAGE_SIZE = 10;
    private RelativeLayout ll_top_ad;
    private CardView card_view;
    private List<Advertisment> banner_adver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewReco = inflater.inflate(R.layout.fragment_recommend,container,false);
        return viewReco;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.recommend_swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.prac_end);//R.color.start,R.color.center,

        recommend_rv = (RecyclerView) view.findViewById(R.id.recommend_recycler);
        recommend_rv.setHasFixedSize(true);
        recommend_rv.setLayoutManager(new GridLayoutManager(getContext(),1));//new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        initAdapter();
        onRefresh();
    }

    private void initAdapter(){
        recommendAdapter = new RecommendAdapter(getContext(),null);
        recommendAdapter.openLoadAnimation();
//		View view getLayoutInflater().inflate(R.horigrid.,null);
//		recommendAdapter.setLoadingView(view);

        //mCurrentCounter = recommendAdapter.getData().size();

        recommendAdapter.openLoadMore(true);
//		or mQuickAdapter.openLoadMore(PAGE_SIZE, true);
//		or mQuickAdapter.setPageSize(PAGE_SIZE);
        recommendAdapter.setOnLoadMoreListener(this);

        recommendAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Video v = click_data.get(position);

                String type = v.getWebinar_type();
                switch (type){
                    case "1":
                        VideoUtils.getInstance().prepareToPlay(v.sid,v.webinar_id, v.webinar_type,v.pic,getActivity(),true,0, Constants.TUIJIAN);
                        break;
                    case "2":
                        Intent intent = new Intent(getActivity(), NoticeActivity.class);
                        intent.putExtra("sid",v.getSid());
                        intent.putExtra("webinar_id",v.getWebinar_id());
                        intent.putExtra("share_pic",v.getPic());
                        intent.putExtra("count_time",v.getCount_down());
                        intent.putExtra("title",v.getSubject());
                        startActivity(intent);
                        break;
                    case "3":
                        Toast.makeText(getContext(),"当前直播处于结束状态",Toast.LENGTH_SHORT).show();
                        break;
                    case "4":
                        VideoUtils.getInstance().prepareToPlay(v.sid,v.webinar_id,v.webinar_type,v.pic,getActivity(),true,0, Constants.TUIJIAN);
                        break;
                }
            }
        });
        addHeadView();
    }

    private void addHeadView(){
        View banner = LayoutInflater.from(getContext()).inflate(R.layout.recommend_banner,null);
        card_view = (CardView) banner.findViewById(R.id.card_view);

        auto_scroll = (ConvenientBanner) banner.findViewById(R.id.banner);
        banner_title = (TextView) banner.findViewById(R.id.ba_title);

        banner_ad = (ImageView) banner.findViewById(R.id.top_ad);
        ll_top_ad = (RelativeLayout) banner.findViewById(R.id.rl_top_ad);

        recommendAdapter.addHeaderView(banner);

        View emptyView = getEmptyView(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefresh();
            }
        });
        recommendAdapter.setEmptyView(true,true,emptyView);

        recommend_rv.setAdapter(recommendAdapter);
    }

    private View getEmptyView(View.OnClickListener listener) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.empty_recyclerview, (ViewGroup) recommend_rv.getParent(), false);
        View emptyView = view.findViewById(R.id.ll_empty);
        emptyView.setOnClickListener(listener);
        return view;
    }

    private void getData(final int flag, String page){
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("page=" + page + "&"+"Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.RECOMMEND)
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
                        DirectBroadcastResponse resp = DirectBroadcastResponse.fromJson(response, DirectBroadcastResponse.class);
                        if (resp.retCode == 0) {
                            List<Video> list = resp.result.list;
                            List<Video> top = resp.result.top;
                            banner_adver= resp.result.adver;
                            if(flag == 0){
                                if(list == null || list.size() == 0){
                                    return;
                                }
                                card_view.setVisibility(View.VISIBLE);
                                ll_top_ad.setVisibility(View.VISIBLE);

                                if(banner_adver.size() == 0 ||banner_adver == null){
                                    ll_top_ad.setVisibility(View.GONE);
                                }else{
                                    if(banner_adver.get(0).pic != null){
                                        Glide.with(getContext())
                                                .load(banner_adver.get(0).pic)
                                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .placeholder(R.drawable.icon_zhan)
                                                .crossFade()
                                                .into(banner_ad);
                                    }
                                    if(banner_adver.get(0).url != null){
                                        //广告点击
                                        banner_ad.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                click_adver();
                                                Uri uri = Uri.parse(banner_adver.get(0).url);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }

                                click_data.clear();
                                click_data.addAll(0,top);
                                click_data.addAll(click_data.size(),list);

                                video_data.addAll(top);
                                video_data.addAll(list);
                                recommendAdapter.setNewData(video_data);
                                recommendAdapter.openLoadMore(video_data.size(),true);

                                mSwipeRefreshLayout.setRefreshing(false);
                            }else {
                                if(list.size() == 0){
                                    Toast.makeText(getContext(),"没有了.",Toast.LENGTH_SHORT).show();
                                    recommendAdapter.notifyDataChangedAfterLoadMore(false);
                                    return;
                                }
                                click_data.addAll(click_data.size(),list);
                                recommendAdapter.notifyDataChangedAfterLoadMore(list, true);
                            }
                        }
                    }
                });
    }

    private void getBanner(){
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.BANNER)
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
                        Banner resp = Banner.fromJson(response, Banner.class);

                        networkImages.addAll(resp.result);
                        List<String> url_pic = new ArrayList<>();

                        for(BannerDetail s:resp.result){
                            url_pic.add(s.pic);
                        }
                        //自定义你的Holder，实现更多复杂的界面，不一定是图片翻页，其他任何控件翻页亦可。
                        auto_scroll.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                            @Override
                            public NetworkImageHolderView createHolder() {
                                return new NetworkImageHolderView();
                            }
                        },url_pic)
                                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                                //设置指示器的方向
                                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                                .setOnPageChangeListener(RecommendFragment.this)
                                .setOnItemClickListener(RecommendFragment.this);
                        //设置翻页的效果，不需要翻页效果可用不设
                        //.setPageTransformer(Transformer.DefaultTransformer);    //集成特效之后会有白屏现象，新版已经分离，如果要集成特效的例子可以看Demo的点击响应。
                        //convenientBanner.setManualPageable(false);//设置不能手动影响
                        banner_title.setText(networkImages.get(0).title);
                    }
                });
    }

    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("推荐");
        auto_scroll.startTurning(3000);
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("推荐");
        auto_scroll.stopTurning();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //Toast.makeText(getContext(),"监听到翻到第"+position+"了",Toast.LENGTH_SHORT).show();
        banner_title.setText(networkImages.get(position).title);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(int position) {
        //banner点击
       BannerDetail bannerDetail = networkImages.get(position);
        VideoUtils.getInstance().prepareToPlay(bannerDetail.sid,bannerDetail.webinar_id,bannerDetail.webinar_type,bannerDetail.pic,getActivity(),true,0,Constants.TUIJIAN);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        page = 1;
        getBanner();
        getData(0, page + "");
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        getData(1, page + "");
    }
    private void click_adver(){
        String id = banner_adver.get(0).id;
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("type=1&" + "market_id=" + id + "&Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.ADVER_CLICK)
                .addParams("type","1")
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
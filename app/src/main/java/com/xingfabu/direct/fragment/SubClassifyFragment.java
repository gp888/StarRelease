package com.xingfabu.direct.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.xingfabu.direct.adapter.RecommendAdapter;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.ClassResponse;
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

public class SubClassifyFragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{
	private  String TONGJI;
	private String position;
	private RecyclerView mRecyclerView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecommendAdapter recommendAdapter;
	private List<Video> clickData = new ArrayList<>();
	private int page = 1;
	private static final int PAGE_SIZE = 10;

	public static SubClassifyFragment newInstance(String name, String position) {
		SubClassifyFragment f = new SubClassifyFragment();
		Bundle b = new Bundle();
		b.putString("position", position);
		b.putString("name",name);
		f.setArguments(b);
		return f;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getString("position");
		TONGJI = getArguments().getString("name");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_subclassify,container,false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.sbuclassif_recycler);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sbuclassify_swipeLayout);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.prac_end);//R.color.start,R.color.center,
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

		initAdapter();
		onRefresh();
	}

	public void onResume() {
		super.onResume();
//		MobclickAgent.onPageStart(TONGJI);
	}
	public void onPause() {
		super.onPause();
//		MobclickAgent.onPageEnd(TONGJI);
	}

	@Override
	public void onRefresh() {
		mSwipeRefreshLayout.setRefreshing(true);
		page = 1;
		getData(0,page+"",position);
	}

	@Override
	public void onLoadMoreRequested() {
		page++;
		getData(1,page+"",position);
	}

	private View getEmptyView(View.OnClickListener listener) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.empty_recyclerview, (ViewGroup) mRecyclerView.getParent(), false);
		View emptyView = view.findViewById(R.id.ll_empty);
		emptyView.setOnClickListener(listener);
		return view;
	}

	private void initAdapter() {
		recommendAdapter = new RecommendAdapter(getContext(),null);
		View enptyView = getEmptyView(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onRefresh();
			}
		});
//		View emptyView = LayoutInflater.from(getContext()).inflate(R.horigrid.empty_recyclerview, (ViewGroup) mRecyclerView.getParent(), false);
		recommendAdapter.setEmptyView(true,true,enptyView);
		recommendAdapter.openLoadAnimation();

//		View view getLayoutInflater().inflate(R.horigrid.,null);
//		mQuickAdapter.setLoadingView(view);
//		mQuickAdapter.openLoadMore(true);
//		or mQuickAdapter.openLoadMore(PAGE_SIZE, true);
//		or mQuickAdapter.setPageSize(PAGE_SIZE);
		recommendAdapter.setOnLoadMoreListener(this);
		addHeadView();
		recommendAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Video v = clickData.get(position);
				String type = v.getWebinar_type();
				switch (type){
					case "1":
						VideoUtils.getInstance().prepareToPlay(v.sid,v.webinar_id, v.webinar_type,v.pic,getActivity(),true,0, Constants.FENLEI);
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
						VideoUtils.getInstance().prepareToPlay(v.sid,v.webinar_id,v.webinar_type,v.pic,getActivity(),true,0, Constants.FENLEI);
						break;
				}
			}
		});
//		mQuickAdapter.setOnRecyclerViewItemLongClickListener(new BaseQuickAdapter.OnRecyclerViewItemLongClickListener() {
//			@Override
//			public boolean onItemLongClick(View view, int position) {
//				Toast.makeText(getContext(), Integer.toString(position)+"long", Toast.LENGTH_SHORT).show();
//				return true;
//			}
//		});
		mRecyclerView.setAdapter(recommendAdapter);
	}

	private void getData(final int flag, final String p, String type) {
		String token = StarReleaseUtil.getToken(getContext());
		String sig = MD5Helper.GetMD5Code("page=" + p + "&"+"Authorization="+token + "&type="+ type + UrlConstants.sign);
		OkHttpUtils.post()
				.url(UrlConstants.DISCOVER)
				.addParams("page", p)
				.addParams("Authorization",token)
				.addParams("type",type)
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
						ClassResponse resp = ClassResponse.fromJson(response, ClassResponse.class);
						if (resp.retCode == 0) {
							List<Video> data_yuan = resp.result;
							if(flag == 0) {
								if(data_yuan == null || data_yuan.size() == 0){
									return;
								}
								recommendAdapter.setNewData(data_yuan);
								recommendAdapter.openLoadMore(data_yuan.size(),true);

								clickData.clear();
								clickData.addAll(0,data_yuan);

								mSwipeRefreshLayout.setRefreshing(false);
							}else if(flag == 1){
								if(data_yuan.size() == 0){
									Toast.makeText(getContext(),"没有了.",Toast.LENGTH_SHORT).show();
									recommendAdapter.notifyDataChangedAfterLoadMore(false);
									return;
								}
								clickData.addAll(clickData.size(),data_yuan);
								recommendAdapter.notifyDataChangedAfterLoadMore(data_yuan, true);
							}
						}
					}
				});
	}

	private void addHeadView() {
		View headView = LayoutInflater.from(getContext()).inflate(R.layout.classify_header, null, false);
		recommendAdapter.addHeaderView(headView);
	}
}
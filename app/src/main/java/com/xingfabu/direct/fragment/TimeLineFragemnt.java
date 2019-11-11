package com.xingfabu.direct.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.AuthorAdapter;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.AuthorEntity;
import com.xingfabu.direct.entity.LateUpdateAuthor;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.utils.VideoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;

/**
 * Created by guoping on 2016/11/15.
 */

public class TimeLineFragemnt extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener{
    private RecyclerView mRecyclerView;
    private AuthorAdapter adapter;
    private List<LateUpdateAuthor> time_data = new ArrayList<>();
    private String aid;
    private int page = 1;
    private String api_url;
    private String user_pic;

    public static TimeLineFragemnt newInstance(String aid,String api_url,String pic) {
        TimeLineFragemnt f = new TimeLineFragemnt();
        Bundle b = new Bundle();
        b.putString("aid",aid);
        b.putString("api_url",api_url);
        b.putString("author_pic",pic);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aid = getArguments().getString("aid");
        api_url = getArguments().getString("api_url");
        user_pic = getArguments().getString("author_pic");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view= inflater.inflate(R.layout.fragment_timeline,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.timeline_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));

        initAdapter();
        onRefresh();
    }

    private void initAdapter() {
        adapter = new AuthorAdapter(getContext(),null);
        adapter.openLoadMore(true);
        adapter.openLoadAnimation();
        adapter.setOnLoadMoreListener(this);
        //暂时屏蔽
//        View emptyView = getEmptyView(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //空数据点击
//                onRefresh();
//            }
//        });
//        adapter.setEmptyView(true,true,emptyView);

        mRecyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LateUpdateAuthor v = time_data.get(position);
                VideoUtils.getInstance().prepareToPlay(v.sid,v.webinar_id,v.webinar_type,v.pic,getActivity(),true,0, Constants.STAR_INFO);
            }
        });
    }

    public void onRefresh() {
        page = 1;
        getData(0, page + "",aid);
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        getData(1, page + "",aid);
    }
    private View getEmptyView(View.OnClickListener listener) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.empty_recyclerview, (ViewGroup) mRecyclerView.getParent(), false);
        View emptyView = view.findViewById(R.id.ll_empty);
        emptyView.setOnClickListener(listener);
        return view;
    }

    private void getData(final int flag, final String page,String aid) {
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("aid=" + aid+ "&page=" + page +"&Authorization="+token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(api_url)
                .addParams("aid",aid)
                .addParams("page", page)
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
                        AuthorEntity resp = AuthorEntity.fromJson(response, AuthorEntity.class);
                        if (resp.retCode == 0) {
                            List<LateUpdateAuthor> list = resp.result.rows;
                            if(flag == 0) {
                                time_data.clear();
                                time_data.addAll(list);
                                if(time_data == null || time_data.size() == 0){
                                    return;
                                }
                                for(int i= 0 ;i<time_data.size();i++){
                                    time_data.get(i).user_pic = user_pic;
                                }
                                adapter.setNewData(time_data);
                                adapter.openLoadMore(10,true);
                            }else if(flag == 1){
                                if(list.size() == 0){
                                    Toast.makeText(getContext(),"没有了",Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataChangedAfterLoadMore(false);
                                    return;
                                }
                                for(int i = 0;i<list.size();i++){
                                    list.get(i).user_pic = user_pic;
                                }
                                adapter.notifyDataChangedAfterLoadMore(list, true);
                            }
                        }
                    }
                });
    }
}

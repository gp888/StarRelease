package com.xingfabu.direct.ui;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.SystemMessageAdapter;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.MessageResponse;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.List;
import okhttp3.Call;
/**
 * Created by guoping on 16/9/20.
 */
public class MyMessageActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private SystemMessageAdapter mQuickAdapter;
    private int page = 1;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_message);
    }

    @Override
    public void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout_message);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.prac_end);//R.color.start,R.color.center,

        mRecyclerView = (RecyclerView) findViewById(R.id.message_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        initAdapter();
        getMessage(0,0+"");
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    private void getMessage(final int flag, String page) {
        String token = StarReleaseUtil.getToken(this);
        String sig = MD5Helper.GetMD5Code("page=" + page + "&Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.SYSTEM_MESSAGE)
                .addParams("page",page)
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
                        MessageResponse resp = MessageResponse.fromJson(response,MessageResponse.class);
                        if(resp.retCode == 0){
                            List<MessageResponse.SystemMessage> data = resp.result;
                            if(flag == 0) {
                                mQuickAdapter.setNewData(data);
                                mQuickAdapter.openLoadMore(data.size(),true);

                                //mCurrentCounter = PAGE_SIZE;
                                mSwipeRefreshLayout.setRefreshing(false);
                            }else if(flag == 1){
                                if(data.size() == 0){
                                    Toast.makeText(MyMessageActivity.this,"没有了.",Toast.LENGTH_SHORT).show();
                                    mQuickAdapter.notifyDataChangedAfterLoadMore(false);
                                    return;
                                }
                                mQuickAdapter.notifyDataChangedAfterLoadMore(data, true);
                                //mCurrentCounter = mQuickAdapter.getData().size();
                            }
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        getMessage(0,1+"");
        page = 1;
    }

    private void initAdapter(){
        mQuickAdapter = new SystemMessageAdapter(MyMessageActivity.this,null);

//        View emptyView = LayoutInflater.from(MyMessageActivity.this).inflate(R.horigrid.empty_recyclerview, (RelativeLayout) mSwipeRefreshLayout.getParent(), false);
//        mQuickAdapter.setEmptyView(emptyView);
        mQuickAdapter.openLoadAnimation();
        mQuickAdapter.setOnLoadMoreListener(MyMessageActivity.this);

        mRecyclerView.setAdapter(mQuickAdapter);
        mQuickAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        getMessage(1,page+"");
    }

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getString(R.string.systemMessage));
//        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getString(R.string.systemMessage));
//        MobclickAgent.onPause(this);
    }
}

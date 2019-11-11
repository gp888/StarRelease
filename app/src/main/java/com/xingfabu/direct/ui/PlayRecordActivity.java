package com.xingfabu.direct.ui;

import android.content.DialogInterface;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.PlayRecordAdapter;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.entity.ClassResponse;
import com.xingfabu.direct.entity.Video;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.utils.VideoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
/**
 * Created by guoping on 16/9/18.
 */
public class PlayRecordActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private PlayRecordAdapter mQuickAdapter;
    private List<Video> record_data = new ArrayList<>();
    private int page = 1;
    private TextView tv_clean;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_record);
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


        tv_clean = (TextView) findViewById(R.id.tv_clean);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout_record);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.prac_end);//R.color.start,R.color.center,

        mRecyclerView = (RecyclerView) findViewById(R.id.record_recycler);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        initAdapter();
        getData(0,0+"");
    }

    @Override
    public void initListeners() {
        tv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayRecordActivity.this,R.style.MyDialogTheme);
                builder.setTitle("");
                builder.setMessage("确认要清除所有的播放记录吗?");
                builder.setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clean_playLog();
                            }
                        });
                builder.setNegativeButton(getString(R.string.cancal),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // negative button logic
                            }
                        });
                AlertDialog dialog = builder.create();
                // display dialog
                dialog.show();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onRefresh() {
        getData(0,1+"");
        page = 1;
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        getData(1,page+"");
    }

    private void initAdapter(){
        mQuickAdapter = new PlayRecordAdapter(PlayRecordActivity.this,null);

        View emptyView = LayoutInflater.from(PlayRecordActivity.this).inflate(R.layout.empty_recyclerview, (CoordinatorLayout) mSwipeRefreshLayout.getParent(), false);
        mQuickAdapter.setEmptyView(emptyView);
        mQuickAdapter.openLoadAnimation();
        mQuickAdapter.setOnLoadMoreListener(PlayRecordActivity.this);

        mRecyclerView.setAdapter(mQuickAdapter);
        mQuickAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Video v = record_data.get(position);
                //视频播放
                VideoUtils.getInstance().prepareToPlay(v.sid,v.webinar_id,v.webinar_type,v.pic,PlayRecordActivity.this,true,0, Constants.BOFANGJILU);
            }
        });
    }

    private void getData(final int flag, final String p) {
        String token = StarReleaseUtil.getToken(PlayRecordActivity.this);
        String sig = MD5Helper.GetMD5Code("page=" + p + "&"+"Authorization="+token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.PLAY_RECORD)
                .addParams("page", p)
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
                        //play_time这个字段没加
                        ClassResponse resp = ClassResponse.fromJson(response, ClassResponse.class);
                        if (resp.retCode == 0) {
                        List<Video> data = resp.result;
                            if(flag == 0) {
                                mQuickAdapter.setNewData(data);
                                mQuickAdapter.openLoadMore(20,true);
                                record_data.clear();
                                record_data.addAll(0,data);
                                //mCurrentCounter = PAGE_SIZE;
                                mSwipeRefreshLayout.setRefreshing(false);
                            }else if(flag == 1){
                                if(data.size() == 0){
                                    Toast.makeText(PlayRecordActivity.this,"没有了.",Toast.LENGTH_SHORT).show();
                                    mQuickAdapter.notifyDataChangedAfterLoadMore(false);
                                    return;
                                }
                                record_data.addAll(record_data.size(),data);
                                mQuickAdapter.notifyDataChangedAfterLoadMore(data, true);
                                //mCurrentCounter = mQuickAdapter.getData().size();
                            }
                        }
                    }
                });
    }

    private void clean_playLog() {
        String token = StarReleaseUtil.getToken(this);
        String sig = MD5Helper.GetMD5Code("Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.CLEAN_PLAY_RECORD)
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
                        BaseResponse resp = BaseResponse.fromJson(response,BaseResponse.class);
                        if(resp.retCode == 0){
                            record_data.clear();
                            mQuickAdapter.setNewData(record_data);
                            //mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getString(R.string.myPalyrecord));
//        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getString(R.string.myPalyrecord));
//        MobclickAgent.onPause(this);
    }
}

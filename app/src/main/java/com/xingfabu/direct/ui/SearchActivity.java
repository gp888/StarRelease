package com.xingfabu.direct.ui;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.SearchResultAdapter;
import com.xingfabu.direct.app.Constants;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.Search;
import com.xingfabu.direct.entity.SearchLog;
import com.xingfabu.direct.entity.SearchModel;
import com.xingfabu.direct.entity.BaseResponse;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.xingfabu.direct.utils.VideoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
/**
 * Created by guoping on 16/6/24.
 */
public class SearchActivity extends BaseActivity{
    private AppCompatEditText search_edit;
    private int page = 1;
    private RecyclerView recyclerView;
    private ListView search_log;
    private List<Search> data;
    private SearchResultAdapter adapter;
    private TagFlowLayout mFlowlayout;
    private LinearLayoutCompat ll_hot;

    @Override
    public void setContentView() {
        setContentView(R.layout.search_layout);
    }

    @Override
    public void initViews() {
        search_log = (ListView) findViewById(R.id.search_log);
        ll_hot = (LinearLayoutCompat) findViewById(R.id.ll_hot);
        mFlowlayout = (TagFlowLayout) findViewById(R.id.id_flowlayout);
        search_edit = (AppCompatEditText) findViewById(R.id.searchtext);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(search_edit.getText().toString())){
                    recyclerView.setVisibility(View.GONE);
                    ll_hot.setVisibility(View.VISIBLE);
                }
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.listview);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        data = new ArrayList<>();

        adapter = new SearchResultAdapter(this,data);
        adapter.openLoadAnimation();

        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_recyclerview, (ViewGroup) recyclerView.getParent(), false);
        adapter.setEmptyView(emptyView);

        recyclerView.setAdapter(adapter);

        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Search search = data.get(position);
                //播放视频
                VideoUtils.getInstance().prepareToPlay(search.sid,search.webinar_id,search.webinar_type,search.pic,SearchActivity.this,true,0, Constants.SOUSUO);
            }
        });

        getSearchLog();
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.btn_search:
                String content = search_edit.getText().toString().trim();
                if(TextUtils.isEmpty(content)){
                    showToast("请输入关键词");
                    return;
                }
                getData(content,page+"");
                break;
            case R.id.clear_log:
                clearLog();
                break;
        }
    }

    private void getData(String keyword,String page) {
        search_edit.setText(keyword);
        search_edit.setSelection(keyword.length());

        String token = StarReleaseUtil.getToken(this);
        String sig = MD5Helper.GetMD5Code("keyword=" + keyword + "&anonymity=" + "0" + "&page=" + page + "&"+"Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.SEARCH)
                .addParams("keyword", keyword)
                .addParams("anonymity",0+"")//1无痕,0有痕
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
                        SearchModel res = SearchModel.fromJson(response, SearchModel.class);
                        ll_hot.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        data = res.result;
                        if(data == null || data.size()== 0){
                            return;
                        }
                        adapter.setNewData(data);
                    }
                });
    }


    private void getSearchLog() {
        String token = StarReleaseUtil.getToken(this);
        String sig = MD5Helper.GetMD5Code("Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.SEARCH_LOG)
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
                        SearchLog searchLog = SearchLog.fromJson(response,SearchLog.class);
                        final List<String> data = searchLog.result.hot;
                        final List<String> log = searchLog.result.logs;
                        mFlowlayout.setAdapter(new TagAdapter<String>(data){
                            @Override
                            public View getView(FlowLayout parent, int position, String s){
                                TextView tv = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.tv,mFlowlayout,false);
                                tv.setText(s);
                                return tv;
                            }
                        });

                        mFlowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
                        {
                            @Override
                            public boolean onTagClick(View view, int position, FlowLayout parent)
                            {
                                getData(data.get(position),page+"");
                                ll_hot.setVisibility(View.GONE);
                                return true;
                            }
                        });
                        ArrayAdapter<String> myArrayAdapter =
                                new ArrayAdapter<>(getApplicationContext(),R.layout.simple_item_tv,log.toArray(new String[log.size()]));
                        search_log.setAdapter(myArrayAdapter);
                        myArrayAdapter.notifyDataSetChanged();

                        search_log.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getData(log.get(position),page+"");
                            }
                        });
                    }
                });
    }
    private void clearLog() {
        String token = StarReleaseUtil.getToken(this);
        String sig = MD5Helper.GetMD5Code("Authorization="+token+ UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.CLEAR_SEARCH_LOG)
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

                            ArrayAdapter<String> myArrayAdapter =
                                    new ArrayAdapter<>(getApplicationContext(),R.layout.simple_item_tv,new String[]{});
                            search_log.setAdapter(myArrayAdapter);
                            myArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(getString(R.string.search));
//        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(getString(R.string.search));
//        MobclickAgent.onPause(this);
    }
}
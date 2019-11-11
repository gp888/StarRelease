package com.xingfabu.direct.ui;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.MyFragmentpagerAdapter;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.fragment.TimeLineFragemnt;
import com.xingfabu.direct.transform.GlideCircleTransform;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by guoping on 2016/11/11.
 */

public class AuthorDetail extends BaseActivity {
    private ViewPager author_viewpager;
    private TabLayout author_tab;
    private List<String> tabs;
    private List<Fragment> fragments;
    private MyFragmentpagerAdapter adapter;
    private String author_id;//作者id
    private String author_pic;
    private ImageView author_head;
    private TextView author_name,author_des;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_author);
    }

    @Override
    public void initViews() {
        author_id = getIntent().getStringExtra("author_id");
        author_pic = getIntent().getStringExtra("author_pic");
        Toolbar toolbar = (Toolbar) findViewById(R.id.author_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        author_name = (TextView) findViewById(R.id.author_name);
        author_head = (ImageView) findViewById(R.id.author_head);
        author_des = (TextView) findViewById(R.id.author_des);

        author_viewpager = (ViewPager) findViewById(R.id.author_viewpager);
        author_tab = (TabLayout) findViewById(R.id.author_tab);

        tabs = new ArrayList<>();
        tabs.add("按时间顺序");
        tabs.add("分享排行榜");
        fragments = new ArrayList<>();
        fragments.add(TimeLineFragemnt.newInstance(author_id,UrlConstants.AUTHOR_DETAIL,author_pic));
        fragments.add(TimeLineFragemnt.newInstance(author_id,UrlConstants.SHARE_RANK,author_pic));

        adapter = new MyFragmentpagerAdapter(getSupportFragmentManager(),tabs,fragments);

        author_tab.setTabMode(TabLayout.MODE_FIXED);
        author_tab.addTab(author_tab.newTab().setText(tabs.get(0)));

        author_tab.addTab(author_tab.newTab().setText(tabs.get(1)));
        author_viewpager.setOffscreenPageLimit(1);
        author_viewpager.setAdapter(adapter);
        author_tab.setupWithViewPager(author_viewpager);
        author_viewpager.setCurrentItem(0);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        getData(1+"",author_id);
    }

    private void getData(final String page,String aid) {
        String token = StarReleaseUtil.getToken(this);
        String sig = MD5Helper.GetMD5Code("aid=" + aid+ "&page=" + page +"&Authorization="+token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.AUTHOR_DETAIL)
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
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject author = jsonObject.getJSONObject("result").getJSONObject("authors");
                            String userName = author.getString("username");
                            String pic = author.getString("pic");
                            String describe = author.getString("describe");

                            Glide.with(AuthorDetail.this)
                                    .load(pic)//pic
                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                    .crossFade()
                                    //.placeholder(R.mipmap.ic_launcher)
                                    .transform(new GlideCircleTransform(AuthorDetail.this))
                                    .into(author_head);
                            author_name.setText(userName);//userName
                            author_des.setText(describe == null ? "ta很懒,什么也没有留下!":describe);//describe
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}

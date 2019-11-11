package com.xingfabu.direct.ui;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.MyFragmentpagerAdapter;
import com.xingfabu.direct.fragment.VideoDetailFragment;
import com.xingfabu.direct.widget.CustomViewPager;
import java.util.ArrayList;
import java.util.List;
import cn.iwgang.countdownview.CountdownView;

/**
 * 预告详情页
 * Created by guoping on 16/9/28.
 */
public class NoticeActivity extends BaseActivity{
    private String sid,webinar_id,share_pic,title;
    private int count_time;
    private ImageView iv_notice;
    private CountdownView notice_countview;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private UMImage image = null;

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            com.umeng.socialize.utils.Log.d("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                showToast("收藏成功");
            }else{
                showToast("分享成功");
            }
        }
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showToast("分享失败");
        }
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showToast("分享取消了");
        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_notice);
    }

    @Override
    public void initViews() {
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        webinar_id = intent.getStringExtra("webinar_id");
        share_pic = intent.getStringExtra("share_pic");
        count_time = intent.getIntExtra("count_time",0);
        title = intent.getStringExtra("title");

        image = new UMImage(NoticeActivity.this,share_pic);

        iv_notice = (ImageView) findViewById(R.id.iv_notice);
        notice_countview = (CountdownView)findViewById(R.id.notice_countview);
        notice_countview.start(count_time*1000);
        Glide.with(this)
                .load(share_pic)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.icon_zhan)
                .crossFade()
                .into(iv_notice);

        viewPager = (CustomViewPager) findViewById(R.id.notice_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.notice_tablayout);

        List<String> tabs = new ArrayList<>();
        tabs.add("聊天");
        tabs.add("详情");//1
        //tabs.add("相关");

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new Fragment());
        fragments.add(VideoDetailFragment.newInstance(sid,webinar_id));//2
        //fragments.add(new Fragment());

        MyFragmentpagerAdapter adapter = new MyFragmentpagerAdapter(getSupportFragmentManager(),tabs,fragments);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
//        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(0)));
//        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(1)));//3
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        viewPager.setPagingEnabled(false);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
//        if(fragment instanceof VideoDetailFragment){
//            VideoDetailFragment v = (VideoDetailFragment)fragment;
//            v.getVideoContent(sid,webinar_id);
//        }
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.notice_back:
                finish();
                break;
            case R.id.notice_share:
                action_share("星发布直播",title,image,"http://xingfabu.cn/");
                break;
        }
    }

    //注意新浪分享的title是不显示的，URL链接只能加在分享文字后显示，并且需要确保withText()不为空
    //当同时传递URL参数和图片时，注意确保图片不能超过32K，否则无法分享，不传递URL参数时图片不受32K限制
    private void action_share(final String title, final String content, final UMImage imagee, final String url) {
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ};
        new ShareAction(this)
                .setDisplayList(displaylist)
//				.withTitle(title)
//				.withMedia(imagee)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        new ShareAction(NoticeActivity.this)
                                .setPlatform(share_media)
                                .setCallback(umShareListener)
                                .withText(content)
                                .withTitle(title)
                                .withMedia(imagee)
                                .withTargetUrl(url)
                                //.withExtra(new UMImage(WatchHLSActivity.this,R.mipmap.ic_launcher))
                                .share();
                    }
                }).open();
    }
}

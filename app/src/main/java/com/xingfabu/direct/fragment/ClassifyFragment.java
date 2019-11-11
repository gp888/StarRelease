package com.xingfabu.direct.fragment;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.MyFragmentpagerAdapter;
import com.xingfabu.direct.app.UrlConstants;
import com.xingfabu.direct.entity.TabText;
import com.xingfabu.direct.utils.MD5Helper;
import com.xingfabu.direct.utils.MyStringCallback;
import com.xingfabu.direct.utils.StarReleaseUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
/**
 * Created by 郭平 on 2016/3/26 0026.
 */
public class ClassifyFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager pager;
    private MyFragmentpagerAdapter adapter;
    private List<String> tabs;
    private List<Fragment> fragments;
    private LinearLayout ll_empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classify,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        pager = (ViewPager) view.findViewById(R.id.classify_view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.classify_tab_layout);
        ll_empty = (LinearLayout) view.findViewById(R.id.classify_empty);
        ll_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTabText();
            }
        });
        getTabText();
    }

    private void getTabText(){
        String token = StarReleaseUtil.getToken(getContext());
        String sig = MD5Helper.GetMD5Code("Authorization=" + token + UrlConstants.sign);
        OkHttpUtils.post()
                .url(UrlConstants.GETCOLUMN)
                .addParams("Authorization",token)
                .addParams("sig", sig)
                .build()
                .execute(new MyStringCallback(){
                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        ll_empty.setVisibility(View.GONE);
                        TabText tabText = TabText.fromJson(response,TabText.class);
                        initTab(tabText.result);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        ll_empty.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(),"请检查网络,稍后重试.",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initTab(List<TabText.TabClassify> list){
        tabs = new ArrayList<>();
        fragments = new ArrayList<>();

        for(TabText.TabClassify i :list){
            tabs.add(i.name);
            fragments.add(SubClassifyFragment.newInstance(i.name,i.id));
        }
        adapter = new MyFragmentpagerAdapter(getChildFragmentManager(),tabs,fragments);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//TabLayout.MODE_FIXED
        for(int i = 0;i<tabs.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(tabs.get(i)));
        }
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.setCurrentItem(0);
    }
}

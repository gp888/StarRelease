package com.xingfabu.direct.fragment;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xingfabu.direct.R;
import com.xingfabu.direct.adapter.MyFragmentpagerAdapter;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by 郭平 on 2016/3/26 0026.
 */
public class DirectFragment extends Fragment{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> tabs;
    private List<Fragment> fragments;
    private MyFragmentpagerAdapter adapter;
    private TextView tabTextView;//与分割线布局有关

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directplay,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View v) {
        viewPager = (ViewPager) v.findViewById(R.id.direct_viewPager);
        tabLayout = (TabLayout) v.findViewById(R.id.direct_tabLayout);

        tabs = new ArrayList<>();
        tabs.add("头条");
        tabs.add("霸屏榜");//1
        fragments = new ArrayList<>();
        fragments.add(new RecommendFragment());
        fragments.add(new HotPlayFragment());//2

        adapter = new MyFragmentpagerAdapter(getChildFragmentManager(),tabs,fragments);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(0)));

        tabLayout.addTab(tabLayout.newTab().setText(tabs.get(1)));//3
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //添加分割线
//        for(int i= 0;i<1;i++){
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            RelativeLayout relativeLayout = (RelativeLayout)
//                    LayoutInflater.from(getContext()).inflate(R.horigrid.layout_divider, tabLayout, false);
//            tabTextView = (TextView) relativeLayout.findViewById(R.id.tab_title);
//            tabTextView.setText(tab.getText());
//            tab.setCustomView(relativeLayout);
//        }
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (position == 0) {
//                    tabTextView.setTextColor(getResources().getColor(R.color.white));
//                } else if (position == 1) {
//                    tabTextView.setTextColor(getResources().getColor(R.color.colorBlack));
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}

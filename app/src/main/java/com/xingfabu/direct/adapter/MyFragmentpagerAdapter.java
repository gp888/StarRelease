package com.xingfabu.direct.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import java.util.List;

/**
 * Created by guoping on 16/4/5.
 */
public class MyFragmentpagerAdapter extends FragmentPagerAdapter {
    public FragmentManager fm;
    private List<String> tabs;
    private List<Fragment> fragments;

    public MyFragmentpagerAdapter(FragmentManager fm, List<String> tabs, List<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.tabs = tabs;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        fragment = fragments.get(position);
        return fragment;
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        if(fragment instanceof RelatedFragment){
//            fm.beginTransaction().show(fragment).commit();
//        }else if(fragment instanceof VideoDetailFragment){
//            fm.beginTransaction().show(fragment).commit();
//        }
//        return fragment;
        return super.instantiateItem(container,position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        Fragment fragment = fragments.get(position);
//        if(fragment instanceof RelatedFragment){
//            fm.beginTransaction().hide(fragment).commit();
//            return;
//        }else if(fragment instanceof VideoDetailFragment){
//            fm.beginTransaction().hide(fragment).commit();
//            return;
//        }
        super.destroyItem(container, position, object);
    }
}

package com.ka.noder.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class ViewPagerAdapter extends FragmentPagerAdapter{
    private List<BasicTabFragment> tabs;

    ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        tabs = new ArrayList<>();
        tabs.add(PersonalNotesFragment.newInstance());
        tabs.add(SecretNotesFragment.newInstance());
        Log.e("TAG_Adapter", "frg1: " + tabs.get(0));
        Log.e("TAG_Adapter", "frg2: " + tabs.get(1));
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }
}
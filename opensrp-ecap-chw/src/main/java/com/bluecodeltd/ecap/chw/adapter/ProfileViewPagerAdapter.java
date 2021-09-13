package com.bluecodeltd.ecap.chw.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "ProfileViewPagerAdapter";

    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ProfileViewPagerAdapter(FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}

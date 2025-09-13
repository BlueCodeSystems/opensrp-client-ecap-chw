package com.bluecodeltd.ecap.chw.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewPagerAdapter extends FragmentStatePagerAdapter {

    private static class FragmentEntry {
        final Class<? extends Fragment> clazz;
        final Bundle args;
        
        FragmentEntry(Class<? extends Fragment> clazz, Bundle args) {
            this.clazz = clazz;
            this.args = args != null ? new Bundle(args) : null;
        }
        
        Fragment createFragment() {
            try {
                Fragment f = clazz.newInstance();
                if (args != null) f.setArguments(new Bundle(args));
                return f;
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("Unable to instantiate fragment: " + clazz.getName(), ex);
            }
        }
    }

    private final List<FragmentEntry> fragmentEntries = new ArrayList<>();

    public ProfileViewPagerAdapter(FragmentManager fm) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    // Add fragment by class to prevent instantiation issues
    public void addFragmentClass(Class<? extends Fragment> fragmentClass) {
        addFragmentClass(fragmentClass, null);
    }
    
    public void addFragmentClass(Class<? extends Fragment> fragmentClass, Bundle args) {
        // Check for duplicate fragment classes to prevent adding the same type twice
        for (FragmentEntry entry : fragmentEntries) {
            if (entry.clazz.equals(fragmentClass)) {
                return; // Fragment class already added
            }
        }
        
        fragmentEntries.add(new FragmentEntry(fragmentClass, args));
    }

    // Backwards-compatible API: accept an instance but use factory internally
    public void addFragment(Fragment fragment){
        final Class<? extends Fragment> clazz = fragment.getClass();
        final Bundle args = fragment.getArguments();
        addFragmentClass(clazz, args);
    }

    public void clear() {
        fragmentEntries.clear();
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentEntries.get(position).createFragment();
    }

    @Override
    public int getCount() {
        return fragmentEntries.size();
    }

    // Keep the same fragments; do not force recreation to avoid duplicate add
    @Override
    public int getItemPosition(Object object) {
        return POSITION_UNCHANGED;
    }

    // Note: We rely on default itemId (position) for compatibility with support libs

    // Disable state saving to prevent FragmentManager from restoring old
    // fragments into the adapter and causing "already added" collisions.
    @Override
    public Parcelable saveState() { return null; }
}

package com.bluecodeltd.ecap.chw.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerAdapterFragment extends FragmentStatePagerAdapter {

    private static class Entry {
        final Class<? extends Fragment> clazz;
        final Bundle args;
        Entry(Class<? extends Fragment> clazz, Bundle args) {
            this.clazz = clazz;
            this.args = args != null ? new Bundle(args) : null;
        }
    }

    private final List<Entry> entries = new ArrayList<>();


    public ViewPagerAdapterFragment(@NonNull FragmentManager fm, List<Fragment> fragments) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f != null) entries.add(new Entry(f.getClass(), f.getArguments()));
            }
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Entry e = entries.get(position);
        try {
            Fragment f = e.clazz.newInstance();
            if (e.args != null) f.setArguments(new Bundle(e.args));
            return f;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Unable to instantiate fragment: " + e.clazz.getName(), ex);
        }
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    // Note: We rely on default itemId (position) for broad compatibility

    // Disable state saving to avoid restoring old fragments into adapter
    @Override
    public Parcelable saveState() { return null; }
}

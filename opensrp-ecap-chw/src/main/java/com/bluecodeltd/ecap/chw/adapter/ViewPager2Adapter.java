package com.bluecodeltd.ecap.chw.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Adapter extends FragmentStateAdapter {

    private static class Entry {
        final Class<? extends Fragment> clazz;
        final Bundle args;
        Entry(Class<? extends Fragment> clazz, Bundle args) {
            this.clazz = clazz;
            this.args = args != null ? new Bundle(args) : null;
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f != null) entries.add(new Entry(f.getClass(), f.getArguments()));
            }
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
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
    public int getItemCount() {
        return entries.size();
    }
}


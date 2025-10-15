package org.smartregister.chw.core.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.smartregister.chw.core.fragment.DailyTalliesFragment;
import org.smartregister.chw.core.fragment.DraftMonthlyFragment;
import org.smartregister.chw.core.fragment.SentMonthlyFragment;

public class Hia2ViewPager2Adapter extends FragmentStateAdapter {

    public Hia2ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return DailyTalliesFragment.newInstance();
            case 1:
                return DraftMonthlyFragment.newInstance();
            case 2:
            default:
                return SentMonthlyFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}


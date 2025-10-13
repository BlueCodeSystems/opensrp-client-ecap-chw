package com.bluecodeltd.ecap.chw.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.job.ChwIndicatorGeneratingJob;
import com.bluecodeltd.ecap.chw.fragment.JobAidsDashboardFragment;
import com.bluecodeltd.ecap.chw.fragment.GuideBooksFragment;
import com.bluecodeltd.ecap.chw.listener.JobsAidsBottomNavigationListener;
import com.bluecodeltd.ecap.chw.util.Utils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.reporting.domain.TallyStatus;
import org.smartregister.reporting.event.IndicatorTallyEvent;

import timber.log.Timber;

public class JobAidsActivity extends FamilyRegisterActivity {

    private static final String REPORT_LAST_PROCESSED_DATE = "REPORT_LAST_PROCESSED_DATE";
    private ViewPager2 mViewPager;
    private TabLayoutMediator tabMediator;
    private ImageView refreshIndicatorsIcon;
    private ProgressBar refreshIndicatorsProgressBar;

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Handle Indicator Tallying complete event from reporting lib
     * When done tallying counts, update view
     *
     * @param event The Indicator tally event we're handling
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIndicatorTallyingComplete(IndicatorTallyEvent event) {
        if (event.getStatus() == TallyStatus.COMPLETE) {
            if (mViewPager != null) {
                mViewPager.getAdapter().notifyDataSetChanged();
            }
            refreshIndicatorsProgressBar.setVisibility(View.GONE);
            refreshIndicatorsIcon.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), getString(R.string.indicators_updating_complete), Toast.LENGTH_LONG).show();
        }
    }

    public class SectionsPagerAdapter extends FragmentStateAdapter {

        public SectionsPagerAdapter(@NonNull androidx.fragment.app.FragmentActivity activity) {
            super(activity);
        }

        @Override
        public int getItemCount() { return 2; }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return JobAidsDashboardFragment.newInstance();
                case 1:
                    return GuideBooksFragment.newInstance();
                default:
                    return JobAidsDashboardFragment.newInstance();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_aids);
        setUpView();
        registerBottomNavigation();
        ChwApplication.prepareDirectories();

        ChwIndicatorGeneratingJob.scheduleJobImmediately(ChwIndicatorGeneratingJob.TAG);
    }

    private void setUpView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
        }
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(this);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        if (tabMediator != null) { try { tabMediator.detach(); } catch (Exception ignored) {} }
        tabMediator = new TabLayoutMediator(tabLayout, mViewPager, (tab, pos) -> {
            if (pos == 0) tab.setText(getString(R.string.tab_text_1));
            else if (pos == 1) tab.setText(getString(R.string.tab_text_2));
        });
        tabMediator.attach();

        refreshIndicatorsIcon = findViewById(R.id.refreshIndicatorsIcon);
        refreshIndicatorsProgressBar = findViewById(R.id.refreshIndicatorsPB);
        // Initial view until we determined by the refresh function
        refreshIndicatorsProgressBar.setVisibility(View.GONE);

        refreshIndicatorsIcon.setOnClickListener(view -> {
            refreshIndicatorsIcon.setVisibility(View.GONE);
            FadingCircle circle = new FadingCircle();
            refreshIndicatorsProgressBar.setIndeterminateDrawable(circle);
            refreshIndicatorsProgressBar.setVisibility(View.VISIBLE);
            refreshIndicatorData();
        });
    }

    @Override
    protected void registerBottomNavigation() {

        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        JobsAidsBottomNavigationListener navigationListener = new JobsAidsBottomNavigationListener(this);
        Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView, navigationListener);
        if (bottomNavigationView != null)
            bottomNavigationView.getMenu().findItem(R.id.action_job_aids).setChecked(true);
    }

    /**
     * Refresh the indicator data by scheduling the IndicatorGeneratingJob immediately
     */
    public void refreshIndicatorData() {
        // Compute everything afresh. Last processed date is set to null to avoid messing with the processing timeline
        ChwApplication.getInstance().getContext().allSharedPreferences().savePreference(REPORT_LAST_PROCESSED_DATE, null);
        ChwIndicatorGeneratingJob.scheduleJobImmediately(ChwIndicatorGeneratingJob.TAG);
        Timber.d("ChwIndicatorGeneratingJob scheduled immediately to compute latest counts...");
        Toast.makeText(getApplicationContext(), getString(R.string.indicators_udpating), Toast.LENGTH_LONG).show();
    }

}

package com.bluecodeltd.ecap.chw.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.core.job.ChwIndicatorGeneratingJob;
import com.bluecodeltd.ecap.chw.fragment.ReportsFragment;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.SecuredActivity;

import timber.log.Timber;

public class ReportsActivity extends SecuredActivity {

    protected BottomNavigationHelper bottomNavigationHelper;
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        if (savedInstanceState == null) {
            switchToFragment(new ReportsFragment());
        }

        onCreation();
        ChwIndicatorGeneratingJob.scheduleJobImmediately(ChwIndicatorGeneratingJob.TAG);
    }

    private void switchToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    @Override
    protected void onCreation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);

    }

    @Override
    protected void onResumption() {
        Timber.v("Empty onResumption");
    }
}

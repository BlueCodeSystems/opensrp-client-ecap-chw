package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import timber.log.Timber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.dao.MotherDao;
import com.bluecodeltd.ecap.chw.fragment.HouseholdChildrenFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdVisitsFragment;
import com.bluecodeltd.ecap.chw.fragment.MotherChildrenFragment;
import com.bluecodeltd.ecap.chw.fragment.MotherOverviewFragment;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.Map;
import java.util.UUID;

public class MotherDetail extends AppCompatActivity {


    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private Toolbar toolbar;
    public ProfileViewPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mother_detail);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);
        mTabLayout =  findViewById(R.id.tabs);
        mViewPager  = findViewById(R.id.viewpager);
        setupViewPager();
       // updateChildTabTitle();

    }


    private void setupViewPager(){
        mPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new MotherOverviewFragment());
        mPagerAdapter.addFragment(new MotherChildrenFragment());


        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Overview");
        mTabLayout.getTabAt(1).setText(getString(R.string.fragment_members));


    }

    public void animateFAB(){


        if (isFabOpen){

            fab.startAnimation(rotate_backward);
            isFabOpen = false;


        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);


        }
    }

}
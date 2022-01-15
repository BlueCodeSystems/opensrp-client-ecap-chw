package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import timber.log.Timber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.MotherDao;
import com.bluecodeltd.ecap.chw.fragment.HouseholdChildrenFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdVisitsFragment;
import com.bluecodeltd.ecap.chw.fragment.MotherChildrenFragment;
import com.bluecodeltd.ecap.chw.fragment.MotherOverviewFragment;
import com.bluecodeltd.ecap.chw.model.Household;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MotherDetail extends AppCompatActivity {


    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private Toolbar toolbar;
    public ProfileViewPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private TextView childTabCount;
    private FloatingActionButton fab;
    CommonPersonObjectClient commonPersonObjectClient;

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

        commonPersonObjectClient = (CommonPersonObjectClient) getIntent().getSerializableExtra("mother");

        setupViewPager();
        updateChildTabTitle();

    }

    public HashMap<String, CommonPersonObjectClient> getData() {
        return  populateMapWithMother(commonPersonObjectClient);

    }

    public HashMap<String, CommonPersonObjectClient> populateMapWithMother(CommonPersonObjectClient commonPersonObjectClient)
    {
        HashMap<String, CommonPersonObjectClient> motherHashMap = new HashMap<>();
        motherHashMap.put("mother", commonPersonObjectClient);

        return motherHashMap;
    }

    private void updateChildTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.child_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("CHILDREN");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);


        String children = IndexPersonDao.countChildren(commonPersonObjectClient.getColumnmaps().get("household_id"));

        childTabCount.setText(children);

        mTabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }


    private void setupViewPager(){
        mPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new MotherOverviewFragment());
        mPagerAdapter.addFragment(new MotherChildrenFragment());


        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Overview");
        mTabLayout.getTabAt(1).setText("Children");


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
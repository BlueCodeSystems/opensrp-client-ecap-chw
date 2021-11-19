package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

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
import com.bluecodeltd.ecap.chw.fragment.HouseholdChildrenFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdVisitsFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileContactFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileVisitsFragment;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.util.HashMap;
import java.util.UUID;

import timber.log.Timber;

public class HouseholdDetails extends AppCompatActivity {

    public ProfileViewPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private Toolbar toolbar;
    private TextView visitTabCount;
    private TextView childTabCount;
    private FloatingActionButton fab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private RelativeLayout rchild, rvisit, rcase_plan, rassessment, rscreen, hvisit20, child_form;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_details);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fab = findViewById(R.id.fabx);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        rchild = findViewById(R.id.child_form);
        rscreen = findViewById(R.id.hh_screening);
        rassessment = findViewById(R.id.cassessment);
        rcase_plan = findViewById(R.id.hcase_plan);
        rvisit = findViewById(R.id.hh_visit);
        hvisit20 = findViewById(R.id.hh_visit20);
        child_form = findViewById(R.id.child_form);

        mTabLayout =  findViewById(R.id.tabs);
        mViewPager  = findViewById(R.id.viewpager);
        setupViewPager();
        updateTasksTabTitle();
        updateChildTabTitle();
    }

    public HashMap<String, String> getData() {

        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("household");

        HashMap<String, String> map = new HashMap<>();

        map.put("base_entity_id", client.getColumnmaps().get("base_entity_id"));
        //adolescent_name_of_caregiver

        return map;

    }

    private void setupViewPager(){
        mPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new HouseholdOverviewFragment());
        mPagerAdapter.addFragment(new HouseholdChildrenFragment());
        mPagerAdapter.addFragment(new HouseholdVisitsFragment());

        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText(getString(R.string.fragment_overview));
        mTabLayout.getTabAt(1).setText(getString(R.string.fragment_children));
        mTabLayout.getTabAt(2).setText(getString(R.string.fragment_housevisits));

    }

    private void updateTasksTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText(this.getString(R.string.visits));
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        mTabLayout.getTabAt(2).setCustomView(taskTabTitleLayout);
    }

    private void updateChildTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.child_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("CHILDREN");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);

        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("household");
        String children = IndexPersonDao.countChildren(client.getColumnmaps().get("base_entity_id"));

        childTabCount.setText(children);

        mTabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }

    public void onClick(View v) {
        int id = v.getId();
        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("household");
        // assert client != null;
        switch (id) {
            case R.id.fabx:

                animateFAB();

                break;

            case R.id.hcase_plan:
                try {
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("caregiver_case_plan");

                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.cassessment:
                try {
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("caregiver_assessment");

                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.hh_visit:

                try {
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("household_visitation_assessment");

                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.hh_visit20:

                try {
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("hh_visitation_20");

                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.child_form:

                try {
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("vca_screening");

                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    public void startFormActivity(JSONObject jsonObject) {


        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);

        Form form = new Form();
        try {
            if (jsonObject.has(JsonFormConstants.ENCOUNTER_TYPE) &&
                    jsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE)
                            .equalsIgnoreCase(Constants.EcapEncounterType.CHILD_INDEX)) {
                form.setWizard(true);
                form.setName(getString(R.string.child_details));
                form.setHideSaveLabel(true);
                form.setNextLabel(getString(R.string.next));
                form.setPreviousLabel(getString(R.string.previous));
                form.setSaveLabel(getString(R.string.submit));
                form.setNavigationBackground(R.color.primary);
            } else {
                form.setWizard(false);
                form.setHideSaveLabel(true);
                form.setNextLabel("");
            }
            intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        } catch (JSONException e) {
            Timber.e(e);
        }
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }

    public void animateFAB(){


        if (isFabOpen){

            fab.startAnimation(rotate_backward);
            isFabOpen = false;
            rvisit.setVisibility(View.GONE);
            rchild.setVisibility(View.GONE);
            hvisit20.setVisibility(View.GONE);
            rscreen.setVisibility(View.GONE);
            rassessment.setVisibility(View.GONE);
            rcase_plan.setVisibility(View.GONE);
            child_form.setVisibility(View.GONE);

        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);
            rvisit.setVisibility(View.VISIBLE);
            hvisit20.setVisibility(View.VISIBLE);
            rchild.setVisibility(View.VISIBLE);
            rscreen.setVisibility(View.VISIBLE);
            rassessment.setVisibility(View.VISIBLE);
            rcase_plan.setVisibility(View.VISIBLE);
            child_form.setVisibility(View.VISIBLE);

        }
    }
}
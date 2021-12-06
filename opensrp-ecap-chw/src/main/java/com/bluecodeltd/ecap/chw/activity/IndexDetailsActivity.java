package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.ChooseLoginMethodFragment;
import com.bluecodeltd.ecap.chw.fragment.PinLoginFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileContactFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileVisitsFragment;
import com.bluecodeltd.ecap.chw.interactor.IndexRegisterInteractor;
import com.bluecodeltd.ecap.chw.model.ChildRegisterModel;
import com.bluecodeltd.ecap.chw.model.IndexRegisterModel;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterPresenter;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.contract.CoreChildProfileContract;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON;
import static org.smartregister.chw.core.utils.CoreJsonFormUtils.getSyncHelper;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

public class IndexDetailsActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;

    private RelativeLayout rhousehold, rassessment, rcase_plan, referral, household_visitation_caregiver, household_visitation_for_vca, grad, grad_sub,hiv_assessment;

    private TextView txtName, txtGender, txtAge;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private AppExecutors appExecutors;
    public ProfileViewPagerAdapter mPagerAdapter;
    private TextView visitTabCount;
    private AppBarLayout myAppbar;
    private Toolbar toolbar;
    String myAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vca_content);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = findViewById(R.id.collapsing_toolbar_appbarlayout);

        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("clients");

        String gender =  client.getColumnmaps().get("gender");

        if(gender.equals("male")){

            toolbar.setBackgroundDrawable(new ColorDrawable(0xff218CC5));
            myAppbar.setBackgroundDrawable(new ColorDrawable(0xff218CC5));

        } else {

            toolbar.setBackgroundDrawable(new ColorDrawable(0xffe36b89));
            myAppbar.setBackgroundDrawable(new ColorDrawable(0xffe36b89));

        }




        fab = findViewById(R.id.fab);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        rhousehold = findViewById(R.id.household);
        rassessment = findViewById(R.id.assessment);
        rcase_plan = findViewById(R.id.case_plan);
        referral = findViewById(R.id.referral);
        household_visitation_caregiver = findViewById(R.id.household_visitation_caregiver);
        household_visitation_for_vca = findViewById(R.id.household_visitation_for_vca);
        grad = findViewById(R.id.grad);

        grad_sub = findViewById(R.id.grad_sub);
        hiv_assessment = findViewById(R.id.hiv_assessment);

        txtName = findViewById(R.id.vca_name);
        txtGender = findViewById(R.id.vca_gender);
        txtAge = findViewById(R.id.vca_age);

        mTabLayout =  findViewById(R.id.tabs);
        mViewPager  = findViewById(R.id.viewpager);

        setupViewPager();
        //updateTasksTabTitle();


    }




    public HashMap<String, String> getData() {

        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("clients");

        String full_name = client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name");
        String gender =  client.getColumnmaps().get("gender");
        String birthdate = client.getColumnmaps().get("adolescent_birthdate");

        if(birthdate != null){

            String[] items1 = birthdate.split("-");
            String date1 = items1[0];
            String month = items1[1];
            String year = items1[2];

             myAge = getAge(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date1));

            txtAge.setText(myAge);

        } else {

            txtAge.setText("Not Set");

        }

        txtName.setText(full_name);
        txtGender.setText(gender);




        HashMap<String, String> map = new HashMap<>();


        map.put("subpop1", client.getColumnmaps().get("subpop1"));
        map.put("subpop2", client.getColumnmaps().get("subpop2"));
        map.put("subpop3", client.getColumnmaps().get("subpop3"));
        map.put("subpop4", client.getColumnmaps().get("subpop4"));
        map.put("subpop5", client.getColumnmaps().get("subpop5"));
        map.put("subpop6", client.getColumnmaps().get("subpop6"));
        map.put("date_referred", client.getColumnmaps().get("date_referred"));
        map.put("date_enrolled", client.getColumnmaps().get("date_enrolled"));
        map.put("art_check_box", client.getColumnmaps().get("art_check_box"));
        map.put("art_number", client.getColumnmaps().get("art_number"));
        map.put("date_started_art", client.getColumnmaps().get("date_started_art"));
        map.put("date_last_vl", client.getColumnmaps().get("date_last_vl"));
        map.put("date_next_vl", client.getColumnmaps().get("date_next_vl"));
        map.put("vl_last_result", client.getColumnmaps().get("vl_last_result"));
        map.put("vl_suppressed", client.getColumnmaps().get("vl_suppressed"));
        map.put("child_mmd", client.getColumnmaps().get("child_mmd"));
        map.put("level_mmd", client.getColumnmaps().get("level_mmd"));
        map.put("caregiver_name", client.getColumnmaps().get("caregiver_name"));
        map.put("caregiver_birth_date", client.getColumnmaps().get("caregiver_birth_date"));
        map.put("caregiver_sex", client.getColumnmaps().get("caregiver_sex"));
        map.put("caregiver_hiv_status", client.getColumnmaps().get("caregiver_hiv_status"));
        map.put("relation", client.getColumnmaps().get("relation"));
        map.put("caregiver_phone", client.getColumnmaps().get("caregiver_phone"));
        map.put("health_facility", client.getColumnmaps().get("health_facility"));


        return map;

    }

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    private void setupViewPager(){
        mPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new ProfileOverviewFragment());
        mPagerAdapter.addFragment(new ProfileContactFragment());
        mPagerAdapter.addFragment(new ProfileVisitsFragment());


        mViewPager.setAdapter(mPagerAdapter);
        //mViewPager.setOffscreenPageLimit(1);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText(getString(R.string.fragment_overview));
        mTabLayout.getTabAt(1).setText(getString(R.string.fragment_contact));
        mTabLayout.getTabAt(2).setText(getString(R.string.fragment_visits));

    }

    private void updateTasksTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText(this.getString(R.string.visits));
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        mTabLayout.getTabAt(2).setCustomView(taskTabTitleLayout);
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



    public void onClick(View v) {
        int id = v.getId();
        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("clients");
        assert client != null;
        switch (id){
            case R.id.fab:

                animateFAB();
                break;

            case R.id.household:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;


                    indexRegisterForm = formUtils.getFormJson("family_register");

                    indexRegisterForm.getJSONObject("step1").put("title", client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name") + " : " + txtAge.getText().toString() + "Yrs - " + txtGender.getText().toString());
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", txtAge.getText().toString());

                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).getJSONArray("options").getJSONObject(0).put("value", client.getColumnmaps().get("subpop1"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).getJSONArray("options").getJSONObject(1).put("value", client.getColumnmaps().get("subpop2"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).getJSONArray("options").getJSONObject(2).put("value", client.getColumnmaps().get("subpop3"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).getJSONArray("options").getJSONObject(3).put("value", client.getColumnmaps().get("subpop4"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).getJSONArray("options").getJSONObject(4).put("value", client.getColumnmaps().get("subpop5"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).getJSONArray("options").getJSONObject(5).put("value", client.getColumnmaps().get("subpop6"));


                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.assessment:


                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;


                    indexRegisterForm = formUtils.getFormJson("vca_assessment");

                    indexRegisterForm.getJSONObject("step1").put("title", client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name") + " : " + txtAge.getText().toString() + "Yrs - " + txtGender.getText().toString());


                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(40).getJSONArray("options").getJSONObject(0).put("value", client.getColumnmaps().get("subpop1"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(40).getJSONArray("options").getJSONObject(1).put("value", client.getColumnmaps().get("subpop2"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(40).getJSONArray("options").getJSONObject(2).put("value", client.getColumnmaps().get("subpop3"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(40).getJSONArray("options").getJSONObject(3).put("value", client.getColumnmaps().get("subpop4"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(40).getJSONArray("options").getJSONObject(4).put("value", client.getColumnmaps().get("subpop5"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(40).getJSONArray("options").getJSONObject(5).put("value", client.getColumnmaps().get("subpop6"));

                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.case_plan:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("case_plan");

                    //  startFormActivity(indexRegisterForm);
                    Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);

                    Form form = new Form();
                    try {
                        if (indexRegisterForm.has(JsonFormConstants.ENCOUNTER_TYPE) &&
                                indexRegisterForm.getString(JsonFormConstants.ENCOUNTER_TYPE)
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

                    indexRegisterForm.getJSONObject("step1").put("title", client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name") + " : " + txtAge.getText().toString() + "Yrs - " + txtGender.getText().toString());


                   if(client.getColumnmaps().get("subpop1") != null){

                        indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).put("value", client.getColumnmaps().get("subpop1"));

                    } else {

                        indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).put("value", "false");
                    }


                    startFormActivity(indexRegisterForm);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.referral:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("referral");

                    //  startFormActivity(indexRegisterForm);
                    Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);

                    Form form = new Form();
                    try {
                        if (indexRegisterForm.has(JsonFormConstants.ENCOUNTER_TYPE) &&
                                indexRegisterForm.getString(JsonFormConstants.ENCOUNTER_TYPE)
                                        .equalsIgnoreCase(Constants.EcapEncounterType.CHILD_INDEX)) {
                            form.setWizard(true);
                            form.setName("Referral Form");
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

                    indexRegisterForm.getJSONObject("step1").put("title", client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name") + " : " + txtAge.getText().toString() + "Yrs - " + txtGender.getText().toString());


                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, indexRegisterForm.toString());
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.myservice:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("service_report");

                    indexRegisterForm.getJSONObject("step1").put("title", client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name") + " : " + txtAge.getText().toString() + "Yrs - " + txtGender.getText().toString());

                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.household_profile:


            Intent intent = new Intent(this, HouseholdDetails.class);
            intent.putExtra("household",  client);
            startActivity(intent);


                break;

            case R.id.household_visitation_caregiver:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("household_visitation_caregiver");

                    indexRegisterForm.getJSONObject("step1").put("title", client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name") + " : " + txtAge.getText().toString() + "Yrs - " + txtGender.getText().toString());


                   // CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("household_visitation_for_caregiver");

                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);
                    String uniqueId = UUID.randomUUID().toString();
                    String hID = uniqueId.substring(0, 8);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String caseworker = prefs.getString("ecap", "");
                    String[] csw = caseworker.split("\\s+");


                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).put("value", csw[0]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(3).put("value", csw[1]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(7).put("value", hID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.household_visitation_for_vca:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("household_visitation_for_vca_0_20_years");

                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", txtAge.getText().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.grad:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("graduation");
                    indexRegisterForm.getJSONObject("step1").put("title", client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name") + " : " + txtAge.getText().toString() + "Yrs - " + txtGender.getText().toString());
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.grad_sub:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("graduation_assessment_sub_for_repeating_fields");

                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.hiv_assessment:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;
                    if(Integer.valueOf(myAge) >15)
                    {
                        indexRegisterForm = formUtils.getFormJson("hiv_assessment");
                    }
                    else {
                        indexRegisterForm = formUtils.getFormJson("risk_assessment_under_15");
                    }


                   CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {

            boolean is_edit_mode = false;

            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);

            JSONObject jsonFormObject = null;
            try {
                jsonFormObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (Constants.EcapEncounterType.CACE_STATUS.equalsIgnoreCase(
                    jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))) {

                is_edit_mode = true;
            }

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, is_edit_mode);

            } catch (Exception e) {
                Timber.e(e);
            }

        }
        //Intent i = new Intent(this, IndexRegisterActivity.class);
        //startActivity(i);
    }

    public ChildIndexEventClient processRegistration(String jsonString){

        try {
            JSONObject formJsonObject = new JSONObject(jsonString);

            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            String entityId = "";

            if(encounterType.equals("Case Record Status")){

                entityId  = formJsonObject.getString("entity_id");
            } else {
                entityId  = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }


            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);


            JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);

            switch (encounterType) {
                case "VCA Case Plan":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_VCA_CASE_PLAN);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Family Registration":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_FAMILY);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }

                    break;
                case "Case Worker Service Report":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_SERVICE_REPORT);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Assessment Form":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_ASSESSMENT);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Household Visitation For Caregiver":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD_CAREGIVER);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Household Visitation Form 0-20 years":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD_VCA);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Graduation Assessment Form":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_GRADUATION);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Graduation Assessment 0-17 Years":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_GRADUATION_SUB);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;


                case "Case Record Status":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CLIENT_INDEX);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode) {

        Runnable runnable = () -> {

            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();

            if (event != null && client != null) {
                try {
                    ECSyncHelper ecSyncHelper = getECSyncHelper();

                    JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));
                    JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());

                    if (isEditMode) {
                        JSONObject mergedClientJsonObject =
                                org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
                        ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);
                    } else {
                        ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);
                    }

                    JSONObject eventJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));
                    ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);

                    Long lastUpdatedAtDate = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                    Date currentSyncDate = new Date(lastUpdatedAtDate);

                    //Get saved event for processing
                    List<EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                    getClientProcessorForJava().processClient(savedEvents);
                    getAllSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());


                } catch (Exception e) {
                    Timber.e(e);
                }
            }

        };

        Toasty.success(IndexDetailsActivity.this, "Form Saved", Toast.LENGTH_LONG, true).show();

        try {
            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(runnable);
            return true;
        } catch (Exception exception) {
            Timber.e(exception);
            return false;
        }
    }

    private ECSyncHelper getECSyncHelper() {
        return ChwApplication.getInstance().getEcSyncHelper();
    }

    public FormTag getFormTag() {
        FormTag formTag = new FormTag();
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
        return formTag;
    }

    public void updateChildProfile(String jsonString) {
       // getView().showProgressDialog(R.string.updating);
        Pair<Client, Event> pair = new ChildRegisterModel().processRegistration(jsonString);
        if (pair == null) {
            return;
        }

        updateRegistration(pair, jsonString, true);
    }

    public void updateRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode) {
        Runnable runnable = () -> {
            finishUpdate(pair, jsonString, isEditMode);
            appExecutors.mainThread().execute(() -> {
               Intent i = new Intent(IndexDetailsActivity.this, IndexRegisterActivity.class);
               startActivity(i);
            });
        };

        appExecutors.diskIO().execute(runnable);
    }

    public void finishUpdate(Pair<Client, Event> pair, String jsonString, boolean isEditMode) {

        try {

            Client baseClient = pair.first;
            Event baseEvent = pair.second;

            if (baseClient != null) {
                JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                if (isEditMode) {
                    JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
                } else {
                    getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                }
            }

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
            }

            if (!isEditMode && baseClient != null) {
                String opensrpId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);
                //mark OPENSRP ID as used
               // getUniqueIdRepository().close(opensrpId);
            }

            if (baseClient != null || baseEvent != null) {
                String imageLocation = JsonFormUtils.getFieldValue(jsonString, org.smartregister.family.util.Constants.KEY.PHOTO);
                JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public AllSharedPreferences getAllSharedPreferences () {
        return ChwApplication.getInstance().getContext().allSharedPreferences();
    }

    private ClientProcessorForJava getClientProcessorForJava() {
        return ChwApplication.getInstance().getClientProcessorForJava();
    }

    public void animateFAB(){


        if (isFabOpen){

            fab.startAnimation(rotate_backward);
            isFabOpen = false;
            rhousehold.setVisibility(View.GONE);
            rassessment.setVisibility(View.GONE);
            rcase_plan.setVisibility(View.GONE);
            referral.setVisibility(View.GONE);
            household_visitation_caregiver.setVisibility(View.GONE);
            household_visitation_for_vca.setVisibility(View.GONE);
            grad.setVisibility(View.GONE);
            grad_sub.setVisibility(View.GONE);
            hiv_assessment.setVisibility(View.GONE);
            //hiv_assessment_Above15.setVisibility(View.GONE);
            // hiv_assessment_Below15.setVisibility(View.GONE);

        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);
            rhousehold.setVisibility(View.VISIBLE);
            rassessment.setVisibility(View.VISIBLE);
            rcase_plan.setVisibility(View.VISIBLE);
            referral.setVisibility(View.VISIBLE);
            household_visitation_caregiver.setVisibility(View.VISIBLE);
            household_visitation_for_vca.setVisibility(View.VISIBLE);
            grad.setVisibility(View.VISIBLE);
            grad_sub.setVisibility(View.VISIBLE);
            hiv_assessment.setVisibility(View.VISIBLE);
           // hiv_assessment_Above15.setVisibility(View.VISIBLE);
            //hiv_assessment_Below15.setVisibility(View.VISIBLE);

        }
    }
    public String getCareGiverFullname(CommonPersonObjectClient client){

        return client.getColumnmaps().get("caregiver_firstname");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.call:
                CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("clients");
                Toast.makeText(getApplicationContext(),"Calling Caregiver...",Toast.LENGTH_LONG).show();

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + client.getColumnmaps().get("caregiver_phone")));
                startActivity(callIntent);
                return true;
            case R.id.case_status:

                try {
                    openFormUsingFormUtils(IndexDetailsActivity.this,"case_status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void openFormUsingFormUtils(Context context, String formName) throws JSONException {
        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("clients");
        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        formToBeOpened.put("entity_id", client.getColumnmaps().get("base_entity_id"));
        formToBeOpened.getJSONObject("step1").put("title", client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name") + " : " + txtAge.getText().toString() + "Yrs - " + txtGender.getText().toString());
        CoreJsonFormUtils.populateJsonForm(formToBeOpened, client.getColumnmaps());

        startFormActivity(formToBeOpened);
    }
}
package com.bluecodeltd.ecap.chw.activity;

import androidx.appcompat.app.AppCompatActivity;
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
import com.bluecodeltd.ecap.chw.model.IndexRegisterModel;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterPresenter;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

public class IndexDetailsActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private RelativeLayout rhousehold, rassessment, rcase_plan, referral;
    private TextView txtName, txtGender, txtAge;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    public ProfileViewPagerAdapter mPagerAdapter;
    private TextView visitTabCount;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vca_content);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        fab = findViewById(R.id.fab);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        rhousehold = findViewById(R.id.household);
        rassessment = findViewById(R.id.assessment);
        rcase_plan = findViewById(R.id.case_plan);
        referral = findViewById(R.id.referral);

        txtName = findViewById(R.id.vca_name);
        txtGender = findViewById(R.id.vca_gender);
        txtAge = findViewById(R.id.vca_age);

        /*String art_number =  client.getColumnmaps().get("art_number");
        String province =  client.getColumnmaps().get("province");
        String district =  client.getColumnmaps().get("district");
        String facility =  client.getColumnmaps().get("health_facility");
        String subpop1 =  client.getColumnmaps().get("subpo1");
        String subpop2 =  client.getColumnmaps().get("subpo2");
        String subpop3 =  client.getColumnmaps().get("subpo3");
        String subpop4 =  client.getColumnmaps().get("subpo4");*/


        mTabLayout =  findViewById(R.id.tabs);
        mViewPager  = findViewById(R.id.viewpager);

        setupViewPager();
        //updateTasksTabTitle();


    }
    

    public HashMap<String, String> getData() {

        CommonPersonObjectClient client = (CommonPersonObjectClient) getIntent().getSerializableExtra("clients");

        String full_name = client.getColumnmaps().get("first_name") + " " + client.getColumnmaps().get("last_name");
        String gender =  client.getColumnmaps().get("gender");
        String birthdate = "DOB : " + client.getColumnmaps().get("birthdate");

        txtName.setText(full_name);
        txtGender.setText(gender);
        txtAge.setText(birthdate);

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
        map.put("caregiver_firstname", client.getColumnmaps().get("caregiver_firstname"));
        map.put("caregiver_sex", client.getColumnmaps().get("caregiver_sex"));
        map.put("caregiver_hiv_status", client.getColumnmaps().get("caregiver_hiv_status"));
        map.put("relation", client.getColumnmaps().get("relation"));
        map.put("caregiver_phone", client.getColumnmaps().get("caregiver_phone"));


        return map;

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
                    String fullCaregiverName = getCareGiverFullname(client);
                    String[] caregiverNAmes = fullCaregiverName.split("\\s+");
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String caseworker = prefs.getString("ecap", "");
                  /*  indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", client.getColumnmaps().get("first_name"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).put("value", client.getColumnmaps().get("last_name"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(3).put("value", client.getColumnmaps().get("age"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(4).put("value", client.getColumnmaps().get("gender"));

                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(4).put("value", "add41");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(4).put("read_only", "true");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(5).put("value", client.getColumnmaps().get("caregiver_id"));
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(5).put("read_only", "true");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(6).put("value", client.getColumnmaps().get("gender"));
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(6).put("read_only", "true");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(7).put("value", caregiverNAmes[0]);
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(7).put("read_only", "true");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(8).put("value", caregiverNAmes[1]);
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(8).put("read_only", "true");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(9).put("value", client.getColumnmaps().get("caregiver_birth_date"));
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(9).put("read_only", "true");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(25).put("value", client.getColumnmaps().get("caregiver_phone"));
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(25).put("read_only", "true");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(28).put("value", client.getColumnmaps().get("health_facility"));
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(28).put("read_only", "true");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(45).put("value", caseworker);
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(45).put("read_only", "true");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(46).put("value", client.getColumnmaps().get("case_worker_nrc"));
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(46).put("read_only", "true");
                   */
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    startFormActivity(indexRegisterForm);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.assessment:

                try {
                    FormUtils formUtils = new FormUtils(IndexDetailsActivity.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("ass");

                    //  startFormActivity(indexRegisterForm);
                    Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);

                    Form form = new Form();
                    try {
                        if (indexRegisterForm.has(JsonFormConstants.ENCOUNTER_TYPE) &&
                                indexRegisterForm.getString(JsonFormConstants.ENCOUNTER_TYPE)
                                        .equalsIgnoreCase(Constants.EcapEncounterType.CHILD_INDEX)) {
                            form.setWizard(true);
                            form.setName("VCA Vulnerability Assessment");
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

                    String caregiver_name = client.getColumnmaps().get("adolescent_name_of_caregiver");
                    String[] splitStr = caregiver_name.split("\\s+");

                    String uniqueId = UUID.randomUUID().toString();
                    String hID = uniqueId.substring(0,8);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String caseworker = prefs.getString("ecap", "");
                    String[] csw = caseworker.split("\\s+");

                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("value", client.getColumnmaps().get("first_name"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", client.getColumnmaps().get("last_name"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).put("value", client.getColumnmaps().get("adolescent_birthdate"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(3).put("value", client.getColumnmaps().get("gender"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(5).put("value", splitStr[0]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(6).put("value", splitStr[1]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(7).put("value", client.getColumnmaps().get("adolescent_phone"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(9).put("value", client.getColumnmaps().get("adolescent_village"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(10).put("value", client.getColumnmaps().get("province"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(53).put("value", csw[0]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(54).put("value", csw[1]);


                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, indexRegisterForm.toString());
                    startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

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

                    String caregiver_name = client.getColumnmaps().get("adolescent_name_of_caregiver");
                    String[] splitStr = caregiver_name.split("\\s+");

                    String uniqueId = UUID.randomUUID().toString();
                    String hID = uniqueId.substring(0,8);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String caseworker = prefs.getString("ecap", "");
                    String[] csw = caseworker.split("\\s+");


                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("value", client.getColumnmaps().get("base_entity_id"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", client.getColumnmaps().get("base_entity_id"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).put("value", client.getColumnmaps().get("province"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(3).put("value", client.getColumnmaps().get("district"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(4).put("value", client.getColumnmaps().get("adolescent_village"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(5).put("value", client.getColumnmaps().get("health_facility"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(6).put("value", splitStr[0]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(7).put("value", splitStr[1]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(8).put("value", hID);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(9).put("value", client.getColumnmaps().get("adolescent_phone"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(10).put("value", client.getColumnmaps().get("physical_address"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(11).put("value", csw[0]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(12).put("value", csw[1]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(13).put("value", client.getColumnmaps().get("caregiver_nrc"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(16).put("value", client.getColumnmaps().get("first_name"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(17).put("value", client.getColumnmaps().get("last_name"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(18).put("value", client.getColumnmaps().get("adolescent_birthdate"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(20).put("value", client.getColumnmaps().get("gender"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(21).put("value", client.getColumnmaps().get("subpop1"));


                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, indexRegisterForm.toString());
                    startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

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

                    String caregiver_name = client.getColumnmaps().get("adolescent_name_of_caregiver");
                    String[] splitStr = caregiver_name.split("\\s+");

                    String uniqueId = UUID.randomUUID().toString();
                    String hID = uniqueId.substring(0,8);

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String caseworker = prefs.getString("ecap", "");
                    String[] csw = caseworker.split("\\s+");


                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("value", client.getColumnmaps().get("province"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", client.getColumnmaps().get("district"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).put("value", client.getColumnmaps().get("adolescent_village"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(3).put("value", client.getColumnmaps().get("health_facility"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(5).put("value", csw[0]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(6).put("value", csw[1]);
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(7).put("value", client.getColumnmaps().get("first_name"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(8).put("value", client.getColumnmaps().get("last_name"));
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(10).put("value", client.getColumnmaps().get("adolescent_birthdate"));


                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, indexRegisterForm.toString());
                    startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

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

            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, false);

            } catch (Exception e) {
                Timber.e(e);
            }

        }
    }

    public ChildIndexEventClient processRegistration(String jsonString){

        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            String entityId  = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
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

                    Toast.makeText(IndexDetailsActivity.this, "Form Data Saved", Toast.LENGTH_LONG).show();


                } catch (Exception e) {
                    Timber.e(e);
                }
            }

        };

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

        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);
            rhousehold.setVisibility(View.VISIBLE);
            rassessment.setVisibility(View.VISIBLE);
            rcase_plan.setVisibility(View.VISIBLE);
            referral.setVisibility(View.VISIBLE);
        }
    }
     public String getCareGiverFullname(CommonPersonObjectClient client){

        return client.getColumnmaps().get("caregiver_firstname");
     }
}
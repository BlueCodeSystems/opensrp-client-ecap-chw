package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.family.util.JsonFormUtils.STEP2;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.CaregiverAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.CaregiverDao;
import com.bluecodeltd.ecap.chw.dao.CaregiverHivAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.HouseholdCasePlanFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdChildrenFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdVisitsFragment;
import com.bluecodeltd.ecap.chw.model.Caregiver;
import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;
import com.bluecodeltd.ecap.chw.model.CaregiverHivAssessmentModel;
import com.bluecodeltd.ecap.chw.model.CaregiverHouseholdvisitationModel;
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
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
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class HouseholdDetails extends AppCompatActivity {

    public ProfileViewPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private Toolbar toolbar;
    private TextView visitTabCount, cname, txtDistrict, txtVillage,casePlanTabCount;
    private TextView childTabCount;
    private FloatingActionButton fab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private RelativeLayout rcase_plan, rassessment, rscreen, child_form, household_visitation_caregiver, grad_form, chivAssessment;
    public String countFemales, countMales, virally_suppressed, childrenCount, householdId, positiveChildren;
    private UniqueIdRepository uniqueIdRepository;
    public Household house;
    Caregiver caregiver;

    ObjectMapper oMapper, householdMapper, caregiverMapper, assessmentMapper, graduationMapper;
    CommonPersonObjectClient household;
    Random Number;
    int Rnumber;
    List<String> allMalesBirthDates;
    List<String> allFemalesBirthDates;
    List<String> allChildrenBirthDates;
    public String lessThanFiveMales, malesBetweenTenAndSevenTeen, caregiverTested,
            lessThanFiveFemales, totalChildren, testedChildren, testedChildrenabove15,
            allTested, FemalesBetweenTenAndSevenTeen;

    CaregiverAssessmentModel caregiverAssessmentModel;
    CaregiverVisitationModel caregiverVisitationModel;
    CaregiverHivAssessmentModel caregiverHivAssessmentModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_details);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);

        householdId = getIntent().getExtras().getString("householdId");

        caregiverAssessmentModel = CaregiverAssessmentDao.getCaregiverAssessment(householdId);
        caregiverVisitationModel = CaregiverVisitationDao.getCaregiverVisitation(householdId);
        caregiverHivAssessmentModel = CaregiverHivAssessmentDao.getCaregiverHivAssessment(householdId);

        house = getHousehold(householdId);


        caregiver = CaregiverDao.getCaregiver(householdId);

        oMapper = new ObjectMapper();
        caregiverMapper = new ObjectMapper();

        fab = findViewById(R.id.fabx);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        rscreen = findViewById(R.id.hh_screening);
        grad_form = findViewById(R.id.graduation);
        chivAssessment = findViewById(R.id.hiv_assessment_caregiver);
        //caregiver_name
        cname = findViewById(R.id.caregiver_name);
        txtDistrict = findViewById(R.id.myaddress);
        txtVillage = findViewById(R.id.address1);
        rassessment = findViewById(R.id.cassessment);
        rcase_plan = findViewById(R.id.hcase_plan);
        child_form = findViewById(R.id.child_form);
        household_visitation_caregiver = findViewById(R.id.household_visitation_caregiver);
        mTabLayout =  findViewById(R.id.tabs);
        mViewPager  = findViewById(R.id.viewpager);
        setupViewPager();
        updateTasksTabTitle();
        updateChildTabTitle();
        updateCaseplanTitle();
        txtDistrict.setText(householdId);

        if(house.getCaregiver_name() == null || house.getCaregiver_name().equals("null")){

            cname.setText("No Household");

        } else {
            cname.setText(house.getCaregiver_name() + " Household");
        }

        countFemales = IndexPersonDao.countFemales(householdId);
        countMales = IndexPersonDao.countMales(householdId);
        allMalesBirthDates =IndexPersonDao.getMalesBirthdates(householdId);
        allFemalesBirthDates = IndexPersonDao.getAllFemalesBirthdate(householdId);
        allChildrenBirthDates = IndexPersonDao.getAllChildrenBirthdate(householdId);
        assert allMalesBirthDates != null;
        assert allFemalesBirthDates !=null;
        assert allChildrenBirthDates !=null;
         countNumberOfMales(allMalesBirthDates);
         countNumberOfFemales(allFemalesBirthDates);
         countNumberofChildren(allChildrenBirthDates);
    }


    public HashMap<String, Household> getData() {
        return  populateMapWithHouse(house);
    }

    public HashMap<String, CaregiverAssessmentModel> getVulnerabilities() {
        return  populateMapWithVulnerabilities(caregiverAssessmentModel);
    }


    public HashMap<String, Household> populateMapWithHouse(Household houseToAdd)
    {
        HashMap<String, Household> householdHashMap= new HashMap<>();
        householdHashMap.put("house",houseToAdd);
        return householdHashMap;
    }

    public HashMap<String, CaregiverAssessmentModel> populateMapWithVulnerabilities(CaregiverAssessmentModel vToAdd)
    {
        HashMap<String, CaregiverAssessmentModel> vHashMap= new HashMap<>();
        vHashMap.put("vulnerabilities", vToAdd);
        return vHashMap;
    }


    private void setupViewPager(){
        mPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new HouseholdOverviewFragment());
        mPagerAdapter.addFragment(new HouseholdChildrenFragment());
        mPagerAdapter.addFragment(new HouseholdCasePlanFragment());
        mPagerAdapter.addFragment(new HouseholdVisitsFragment());

        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText(getString(R.string.fragment_overview));
        mTabLayout.getTabAt(1).setText(getString(R.string.fragment_members));
        mTabLayout.getTabAt(3).setText(getString(R.string.fragment_housevisits));
        mTabLayout.getTabAt(2).setText("CP");

    }

    private void updateTasksTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText(this.getString(R.string.visits));
        visitTabCount = taskTabTitleLayout.findViewById(R.id.household_plans_count);
        mTabLayout.getTabAt(3).setCustomView(taskTabTitleLayout);
    }

    private void updateCaseplanTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.household_plan_tab_title, null);
        TextView casePlanTabTitle = taskTabTitleLayout.findViewById(R.id.household_plans_title);
        casePlanTabTitle.setText("CP");
        casePlanTabCount = taskTabTitleLayout.findViewById(R.id.household_plans_count);
        int plans = CasePlanDao.getByIDNumberOfCaregiverCasepalns(house.getHousehold_id());

        if (plans > 0)
        {
            casePlanTabCount.setText(String.valueOf(plans));
        }
        else{
            casePlanTabCount.setText("0");
        }

        mTabLayout.getTabAt(2).setCustomView(taskTabTitleLayout);
    }

    private void updateChildTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.child_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("MEMBERS");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);

        childrenCount = IndexPersonDao.countChildren(householdId);

        childTabCount.setText(childrenCount);

        mTabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }


    public void onClick(View v) {
        int id = v.getId();

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(HouseholdDetails.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject indexRegisterForm;

        switch (id) {

            case R.id.graduation:

                testedChildren = IndexPersonDao.countTestedChildren(householdId);
                testedChildrenabove15 = IndexPersonDao.countTestedAbove15Children(householdId);
                int sumtested = Integer.parseInt(testedChildren) +  Integer.parseInt(testedChildrenabove15);


                virally_suppressed = IndexPersonDao.countSuppressedChildren(householdId);
                positiveChildren = IndexPersonDao.countPositiveChildren(householdId);

                try {

                    oMapper = new ObjectMapper();

                    indexRegisterForm = formUtils.getFormJson("graduation");


                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, oMapper.convertValue(house, Map.class));

                    //Populate form details
                    JSONObject ftime = getFieldJSONObject(fields(indexRegisterForm, "step1"), "asmt");
                    ftime.put(JsonFormUtils.VALUE, "no");

                    //Populate Caregiver Details
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm,oMapper.convertValue(house, Map.class));


                    //Count everyone who has been tested
                    if(sumtested < Integer.parseInt(totalChildren)){
                        allTested = "no";
                    } else {
                        allTested = "yes";
                    }

                    JSONObject hiv_status_enrolled = getFieldJSONObject(fields(indexRegisterForm, "step2"), "hiv_status_enrolled");
                    hiv_status_enrolled.put(JsonFormUtils.VALUE, allTested);

                    //Check if Caregiver Has been Tested using HIV Assessment
                    if(caregiverHivAssessmentModel == null || caregiverHivAssessmentModel.getHiv_status() == null || caregiverHivAssessmentModel.getHiv_status().equals("never_tested")){
                        caregiverTested = "no";
                    } else {
                        caregiverTested = "yes";
                    }


                    if(Integer.parseInt(virally_suppressed) < Integer.parseInt(positiveChildren)){

                        virally_suppressed = "no";
                    } else {
                        virally_suppressed = "yes";
                    }

                    JSONObject tested = getFieldJSONObject(fields(indexRegisterForm, "step2"), "caregiver_hiv_status_enrolled");
                    tested.put(JsonFormUtils.VALUE, caregiverTested);

                    JSONObject suppressed = getFieldJSONObject(fields(indexRegisterForm, "step3"), "virally_suppressed");
                    suppressed.put(JsonFormUtils.VALUE, virally_suppressed);


                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case R.id.myservice:

                try {

                    indexRegisterForm = formUtils.getFormJson("service_report");

                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm,oMapper.convertValue(house, Map.class));
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.fabx:

                animateFAB();

                break;

            case R.id.screenBtn:
            case R.id.hh_screening:

                try {


                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String caseworkerphone = prefs.getString("phone", "Anonymous");
                    String caseworkername = prefs.getString("caseworker_name", "Anonymous");

                    householdMapper = new ObjectMapper();

                    indexRegisterForm = formUtils.getFormJson("hh_screening_entry");
                    indexRegisterForm.put("entity_id", this.house.getBid());
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm,householdMapper.convertValue(house, Map.class));


                    JSONObject cphone = getFieldJSONObject(fields(indexRegisterForm, "step2"), "phone");
                    if (cphone != null) {
                        cphone.remove(JsonFormUtils.VALUE);
                        try {
                            cphone.put(JsonFormUtils.VALUE, caseworkerphone);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    JSONObject caseworker_name_object = getFieldJSONObject(fields(indexRegisterForm, "step4"), "caseworker_name");
                    if (caseworker_name_object != null) {
                        caseworker_name_object.remove(JsonFormUtils.VALUE);
                        try {
                            caseworker_name_object.put(JsonFormUtils.VALUE, caseworkername);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //caseworker_name


                    JSONArray subPopulation = getFieldJSONObject(fields(indexRegisterForm, STEP2), "sub_population").getJSONArray("options");

                    subPopulation.getJSONObject(0).put("value", house.getSubpop1());
                    subPopulation.getJSONObject(1).put("value", house.getSubpop2());
                    subPopulation.getJSONObject(2).put("value", house.getSubpop3());
                    subPopulation.getJSONObject(3).put("value", house.getSubpop4());
                    subPopulation.getJSONObject(5).put("value", house.getSubpop());
                    subPopulation.getJSONObject(4).put("value", house.getSubpop5());

                    indexRegisterForm.getJSONObject("step3").getJSONArray("fields").getJSONObject(3).put("value", "true");

                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;



            case R.id.hcase_plan:
                try {

                    indexRegisterForm = formUtils.getFormJson("care_case_plan");

                    //TODO
                    // CoreJsonFormUtils.populateJsonForm(indexRegisterForm, client.getColumnmaps());
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm,oMapper.convertValue(house, Map.class));
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.cassessment:
                try {

                    assessmentMapper = new ObjectMapper();

                    indexRegisterForm = formUtils.getFormJson("hh_caregiver_assessment");

                    if(caregiverAssessmentModel == null) {
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, oMapper.convertValue(house, Map.class));

                        indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(32).put("value", house.getActive_on_treatment());

                    }
                    else{
                        indexRegisterForm.put("entity_id", this.caregiverAssessmentModel.getBase_entity_id());
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, assessmentMapper.convertValue(caregiverAssessmentModel, Map.class));
                    }

                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case R.id.household_visitation_caregiver:
                try {

                    indexRegisterForm = formUtils.getFormJson("household_visitation_for_caregiver");

                    CaregiverHouseholdvisitationModel householdVisitationCaregiver = new CaregiverHouseholdvisitationModel();
                    if(caregiver.getCaregiver_hiv_status().equals("HIV+"))
                    {
                        householdVisitationCaregiver.setCaregiver_hiv_status("positive");
                    }
                    else if(caregiver.getCaregiver_hiv_status().equals("HIV-"))
                    {
                        householdVisitationCaregiver.setCaregiver_hiv_status("negative");
                    }
                    else if(caregiver.getCaregiver_hiv_status().equals("Unknown"))
                    {
                        householdVisitationCaregiver.setCaregiver_hiv_status("unknown");
                    }
                    else if(caregiver.getCaregiver_hiv_status().equals("not_required"))
                    {
                        householdVisitationCaregiver.setCaregiver_hiv_status("status_not_required");
                    }
                    householdVisitationCaregiver.setCaregiver_art(caregiver.getActive_on_treatment());
                    if (caregiverVisitationModel == null) {
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, caregiverMapper.convertValue(house, Map.class));
                    }
                    else {
                        indexRegisterForm.put("entity_id", this.caregiverVisitationModel.getBase_entity_id());
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, caregiverMapper.convertValue(caregiverVisitationModel, Map.class));
                    }
                    startFormActivity(indexRegisterForm);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.hiv_assessment_caregiver:
                try {

                    indexRegisterForm = formUtils.getFormJson("hh_hiv_assessment_caregiver");
                    if (caregiverHivAssessmentModel == null) {
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, caregiverMapper.convertValue(house, Map.class));
                    }
                    else {
                        indexRegisterForm.put("entity_id", this.caregiverHivAssessmentModel.getBase_entity_id());
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, caregiverMapper.convertValue(caregiverHivAssessmentModel, Map.class));
                    }

                    startFormActivity(indexRegisterForm);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.child_form:

                try {

                    indexRegisterForm = formUtils.getFormJson("family_member");

                    //Populate Caseworker Name
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(HouseholdDetails.this);
                    String caseworker = sp.getString("caseworker_name", "Anonymous");
                    Object obj = sp.getAll();


                    JSONObject ccname = getFieldJSONObject(fields(indexRegisterForm, "step2"), "caseworker_name");

                    if (ccname != null) {
                        ccname.remove(JsonFormUtils.VALUE);
                        try {
                            ccname.put(JsonFormUtils.VALUE, caseworker);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //******** POPULATE JSON FORM WITH VCA UNIQUE ID ******//
                    JSONObject stepOneHouseholdId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "household_id");

                    if (stepOneHouseholdId != null) {
                        stepOneHouseholdId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                        try {
                            stepOneHouseholdId.put(JsonFormUtils.VALUE, house.getHousehold_id());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    Number = new Random();
                    Rnumber = Number.nextInt(900000000);
                    String newEntityId =  Integer.toString(Rnumber);


                    //******** POPULATE JSON FORM WITH VCA UNIQUE ID ******//
                    JSONObject stepOneUniqueId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");

                    if (stepOneUniqueId != null) {
                        stepOneUniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                        try {
                            stepOneUniqueId.put(JsonFormUtils.VALUE, newEntityId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(house.getSubpop() != null && house.getSubpop().equals("true")){
                        JSONObject fswObect = getFieldJSONObject(fields(indexRegisterForm, STEP1), "fsw");
                        fswObect.put(JsonFormUtils.VALUE, "yes");
                    }


                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm,oMapper.convertValue(obj, Map.class));
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm,caregiverMapper.convertValue(caregiver, Map.class));
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

            String EncounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");


            if(!jsonFormObject.optString("entity_id").isEmpty()){
                is_edit_mode = true;
            }

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, is_edit_mode, EncounterType);


                switch (EncounterType) {

                    case "Caregiver Assessment":

                        closeFab();
                        Toasty.success(HouseholdDetails.this, "Vulnerabilities Saved", Toast.LENGTH_LONG, true).show();
                        finish();
                        startActivity(getIntent());
                        break;

                    case "Household Screening":

                        closeFab();
                        Toasty.success(HouseholdDetails.this, "Household Updated", Toast.LENGTH_LONG, true).show();
                        finish();
                        startActivity(getIntent());
                        break;

                    case "Family Member":
                        closeFab();
                        Toasty.success(HouseholdDetails.this, "Family Member Saved", Toast.LENGTH_LONG, true).show();
                        finish();
                        startActivity(getIntent());
                        break;

                    case "MUAC Score":

                        closeFab();
                        Toasty.success(HouseholdDetails.this, "MUAC Updated", Toast.LENGTH_LONG, true).show();
                        finish();
                        startActivity(getIntent());
                        break;

                    case "Grad":
                    case "Hiv Assessment For Caregiver":

                        closeFab();
                        Toasty.success(HouseholdDetails.this, "Form Updated", Toast.LENGTH_LONG, true).show();
                        finish();
                        startActivity(getIntent());
                        break;

                    case "Caregiver Case Plan":
                        String dateId = jsonFormObject.getJSONObject("step1").getJSONArray("fields").getJSONObject(4).optString("value");
                        AddVulnarabilitiesToCasePlan(dateId);
                        break;

                }


            } catch (Exception e) {
                Timber.e(e);
            }

        }

    }

    private void AddVulnarabilitiesToCasePlan(String dateId) {
        Intent i = new Intent(HouseholdDetails.this, HouseholdCasePlanActivity.class);
        i.putExtra("unique_id",  house.getUnique_id());
        i.putExtra("householdId",  house.getHousehold_id());
        i.putExtra("dateId",  dateId);
        startActivity(i);
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }


    public ChildIndexEventClient processRegistration(String jsonString){

        try {
            JSONObject formJsonObject = new JSONObject(jsonString);

            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);


            String entityId = formJsonObject.optString("entity_id");

            if(entityId.isEmpty()){
                entityId  = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }


            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);

            JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);

            switch (encounterType) {

                case "MUAC Score":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_muac");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;

                case "Family Member":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CLIENT_INDEX);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;

                case "Household Screening":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;

                case "Caregiver Assessment":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CAREGIVER_HOUSEHOLD_ASSESSMENT);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;

                case "Caregiver Case Plan":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CAREGIVER_CASEPLAN);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;
                case "Household Visitation For Caregiver":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable. EC_CAREGIVER_VISITATION);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;
                case "Hiv Assessment For Caregiver":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable. EC_CAREGIVER_HIV_ASSESSMENT);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Grad":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_GRAD);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;

            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode, String encounterType) {

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

            closeFab();

        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);
            rscreen.setVisibility(View.VISIBLE);
            grad_form.setVisibility(View.VISIBLE);
            chivAssessment.setVisibility(View.VISIBLE);
            rassessment.setVisibility(View.VISIBLE);
            rcase_plan.setVisibility(View.VISIBLE);
            child_form.setVisibility(View.VISIBLE);
            household_visitation_caregiver.setVisibility(View.VISIBLE);

        }
    }

    public void closeFab(){
        fab.startAnimation(rotate_backward);
        isFabOpen = false;
        rscreen.setVisibility(View.GONE);
        chivAssessment.setVisibility(View.GONE);
        grad_form.setVisibility(View.GONE);
        rassessment.setVisibility(View.GONE);
        rcase_plan.setVisibility(View.GONE);
        child_form.setVisibility(View.GONE);
        household_visitation_caregiver.setVisibility(View.GONE);
    }

    public void countNumberOfMales(List<String> allBirthDates){
            int totalNumberOfMalesBelowFive = 0;
            int totalNumberOfMalesBetweenTenAndSeventeen =0 ;
            DateTimeFormatter formatter = formatDateByPattern("dd-MM-u");
            if( allBirthDates != null)
            {
            for(int i = 0; i < allBirthDates.size(); i++)
            {
                LocalDate localDateBirthdate = LocalDate.parse(allBirthDates.get(i), formatter);
                LocalDate today =LocalDate.now();
                Period periodBetweenDateOfBirthAndNow = getPeriodBetweenDateOfBirthAndNow(localDateBirthdate, today);
                if(periodBetweenDateOfBirthAndNow.getYears() > 0 &&  periodBetweenDateOfBirthAndNow.getYears() < 5)
                {
                    totalNumberOfMalesBelowFive =totalNumberOfMalesBelowFive + 1;
                }
                else if(periodBetweenDateOfBirthAndNow.getYears() > 10 &&  periodBetweenDateOfBirthAndNow.getYears() < 17)
                {
                    totalNumberOfMalesBetweenTenAndSeventeen =  totalNumberOfMalesBetweenTenAndSeventeen + 1;
                 }
            }
            }

        lessThanFiveMales = String.valueOf(totalNumberOfMalesBelowFive);
        malesBetweenTenAndSevenTeen = String.valueOf(totalNumberOfMalesBetweenTenAndSeventeen);

    }
    public void countNumberOfFemales(List<String> allBirthDates){
        int totalNumberOfFemalesBelowFive = 0;
        int totalNumberOfFemalesBetweenTenAndSeventeen =0 ;
        DateTimeFormatter formatter = formatDateByPattern("dd-MM-u");
        if( allBirthDates != null)
        {
            for(int i = 0; i < allBirthDates.size(); i++)
            {
                LocalDate localDateBirthdate = LocalDate.parse(allBirthDates.get(i), formatter);
                LocalDate today =LocalDate.now();
                Period periodBetweenDateOfBirthAndNow = getPeriodBetweenDateOfBirthAndNow(localDateBirthdate, today);
                if(periodBetweenDateOfBirthAndNow.getYears() > 0 &&  periodBetweenDateOfBirthAndNow.getYears() < 5)
                {
                    totalNumberOfFemalesBelowFive =totalNumberOfFemalesBelowFive + 1;
                }
                else if(periodBetweenDateOfBirthAndNow.getYears() > 10 &&  periodBetweenDateOfBirthAndNow.getYears() < 17)
                {
                    totalNumberOfFemalesBetweenTenAndSeventeen =  totalNumberOfFemalesBetweenTenAndSeventeen + 1;
                }
            }
        }

        lessThanFiveFemales = String.valueOf(totalNumberOfFemalesBelowFive);
        FemalesBetweenTenAndSevenTeen = String.valueOf(totalNumberOfFemalesBetweenTenAndSeventeen);

    }
    public void countNumberofChildren(List<String> allBirthDates){
        int totalNumberOfChildren = 0;
        DateTimeFormatter formatter = formatDateByPattern("dd-MM-u");
        if( allBirthDates != null)
        {
            for(int i = 0; i < allBirthDates.size(); i++)
            {
                LocalDate localDateBirthdate = LocalDate.parse(allBirthDates.get(i), formatter);
                LocalDate today =LocalDate.now();
                Period periodBetweenDateOfBirthAndNow = getPeriodBetweenDateOfBirthAndNow(localDateBirthdate, today);
                if(periodBetweenDateOfBirthAndNow.getYears() > 0 &&  periodBetweenDateOfBirthAndNow.getYears() < 18)
                {
                    totalNumberOfChildren = totalNumberOfChildren + 1;
                }
            }
        }

        totalChildren = String.valueOf(totalNumberOfChildren);

    }

    public Period getPeriodBetweenDateOfBirthAndNow(LocalDate localDateBirthdate, LocalDate today){
      return   Period.between(localDateBirthdate, today);
    }

    public DateTimeFormatter formatDateByPattern(String pattern)
    {
        return DateTimeFormatter.ofPattern(pattern);
    }

    public Household getHousehold(String householdId)
    {
        return HouseholdDao.getHousehold(householdId);
    }
}
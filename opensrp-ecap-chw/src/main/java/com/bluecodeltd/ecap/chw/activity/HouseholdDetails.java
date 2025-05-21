package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.constants.JsonFormConstants.OPTIONS_FIELD_NAME;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.family.util.JsonFormUtils.STEP2;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.bluecodeltd.ecap.chw.dao.GradDao;
import com.bluecodeltd.ecap.chw.dao.GraduationDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdServiceReportDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.MotherDao;
import com.bluecodeltd.ecap.chw.dao.MuacDao;
import com.bluecodeltd.ecap.chw.dao.VCAServiceReportDao;
import com.bluecodeltd.ecap.chw.dao.VcaAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.dao.WeServiceCaregiverDoa;
import com.bluecodeltd.ecap.chw.dao.newCaregiverDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.domain.Mother;
import com.bluecodeltd.ecap.chw.fragment.CaregiverHivAssessmentFragment;
import com.bluecodeltd.ecap.chw.fragment.GraduationAssessmentFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdCasePlanFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdChildrenFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdVisitsFragment;
import com.bluecodeltd.ecap.chw.model.Caregiver;
import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;
import com.bluecodeltd.ecap.chw.model.CaregiverHivAssessmentModel;
import com.bluecodeltd.ecap.chw.model.CaregiverHouseholdvisitationModel;
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.GraduationModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.WeServiceCaregiverModel;
import com.bluecodeltd.ecap.chw.model.newCaregiverModel;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class HouseholdDetails extends AppCompatActivity {

    public ProfileViewPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private Toolbar toolbar;
    private TextView visitTabCount, cname,updatedCaregiverName, txtDistrict, txtVillage,casePlanTabCount;
    public TextView childTabCount;
    private FloatingActionButton fab,callFab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private RelativeLayout refferal, rcase_plan, rassessment, rscreen, child_form, household_visitation_caregiver, grad_form, chivAssessment,we_service_caregiver;
    public String countFemales, countMales, virally_suppressed, childrenCount, householdId, positiveChildren;
    private UniqueIdRepository uniqueIdRepository;
    public Household house;
    public ArrayList houseHoldsContainingSameId;

    Caregiver caregiver;
    AlertDialog.Builder builder, screeningBuilder;

    ObjectMapper oMapper, householdMapper, caregiverMapper,weServiceMapper, assessmentMapper, graduationMapper;
    CommonPersonObjectClient household;
    Random number;
    int rNumber;
    List<String> allMalesBirthDates;
    List<String> allFemalesBirthDates;
    List<String> allChildrenBirthDates;
    public String lessThanFiveMales, malesBetweenTenAndSevenTeen, caregiverTested,
            lessThanFiveFemales, totalChildren, testedChildren, testedChildrenabove15,
            allTested, FemalesBetweenTenAndSevenTeen;

    CaregiverAssessmentModel caregiverAssessmentModel;
    CaregiverVisitationModel caregiverVisitationModel;
    CaregiverHivAssessmentModel caregiverHivAssessmentModel;
    GraduationModel graduationModel;
    WeServiceCaregiverModel weServiceCaregiverModel;
    newCaregiverModel updatedCaregiver;
    private ArrayList<Child> childList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_details);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);

        householdId = getIntent().getExtras().getString("householdId");

        childList.addAll(IndexPersonDao.getFamilyChildren(householdId));

        weServiceCaregiverModel = WeServiceCaregiverDoa.getWeServiceCaregiver(householdId);
        caregiverAssessmentModel = CaregiverAssessmentDao.getCaregiverAssessment(householdId);
        caregiverVisitationModel = CaregiverVisitationDao.getCaregiverVisitation(householdId);
        caregiverHivAssessmentModel = CaregiverHivAssessmentDao.getCaregiverHivAssessment(householdId);
        graduationModel = GraduationDao.getGraduation(householdId);
        updatedCaregiver = newCaregiverDao.getNewCaregiverById(householdId);

        builder = new AlertDialog.Builder(HouseholdDetails.this);

        house = getHousehold(householdId);
        refreshActivity(house);

       caregiver = CaregiverDao.getCaregiver(householdId);

        oMapper = new ObjectMapper();
        caregiverMapper = new ObjectMapper();
        weServiceMapper = new ObjectMapper();
        assessmentMapper = new ObjectMapper();

        callFab = findViewById(R.id.callFab);

        try{
            if(caregiverAssessmentModel.getHousehold_type() == null){
                callFab.setImageResource(android.R.drawable.ic_input_add);
            }
        } catch (Exception e){

        }


        fab = findViewById(R.id.fabx);
        changeFabIconColor();
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);



        rscreen = findViewById(R.id.hh_screening);
        grad_form = findViewById(R.id.graduation);
        we_service_caregiver = findViewById(R.id.we_service_caregiver);
        chivAssessment = findViewById(R.id.hiv_assessment_caregiver);
        //caregiver_name
        cname = findViewById(R.id.caregiver_name);
        updatedCaregiverName = findViewById(R.id.updated_caregiver_name);
        txtDistrict = findViewById(R.id.myaddress);
        txtVillage = findViewById(R.id.address1);
        rassessment = findViewById(R.id.cassessment);
        rcase_plan = findViewById(R.id.hcase_plan);
        refferal = findViewById(R.id.h_referral);
        child_form = findViewById(R.id.child_form);
        household_visitation_caregiver = findViewById(R.id.household_visitation_caregiver);
        mTabLayout =  findViewById(R.id.tabs);
        mViewPager  = findViewById(R.id.viewpager);
        setupViewPager();
        setupFabVisibility();
        updateTasksTabTitle();
        updateChildTabTitle();
        updateCaseplanTitle();

        updateOverviewTabTitle();
        updateGradTabTitle();
        txtDistrict.setText(householdId);


        if(house.getCaregiver_name() == null || house.getCaregiver_name().equals("null")){

            cname.setText("No Household");

        } else {
            cname.setText(house.getCaregiver_name() + " Household");
        }

        try {
            if(updatedCaregiver.getNew_caregiver_name() != null && !updatedCaregiver.getNew_caregiver_name().isEmpty()){
                updatedCaregiverName.setVisibility(View.VISIBLE);
                updatedCaregiverName.setText("Current Caregiver: "+ updatedCaregiver.getNew_caregiver_name());
            }
        } catch (NullPointerException e) {
            Log.e("TAG", "Error: " + e.getMessage());
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

    public HashMap<String, WeServiceCaregiverModel> getWeServices() {
        return populateMapWithWeServicesCaregiverModel(weServiceCaregiverModel);
    }

    public HashMap<String, CaregiverAssessmentModel> getVulnerabilities() {
        return  populateMapWithVulnerabilities(caregiverAssessmentModel);
    }
    private String checkAndConvertDateFormat(String date) {
        if (date == null || date.isEmpty()) {
            return "Date is null or empty";
        }

        if (date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return date;
        } else {
            DateTimeFormatter oldFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
            DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate localDate = LocalDate.parse(date, oldFormatter);
                return localDate.format(newFormatter);
            } catch (DateTimeParseException e) {
                Log.e("TAG", "Invalid date format: " + e.getMessage());
                return "Invalid date format";
            }
        }
    }


    public HashMap<String, Household> populateMapWithHouse(Household houseToAdd)
    {
        HashMap<String, Household> householdHashMap= new HashMap<>();
        householdHashMap.put("house",houseToAdd);
        return householdHashMap;
    }

    public HashMap<String,WeServiceCaregiverModel> populateMapWithWeServicesCaregiverModel(WeServiceCaregiverModel weServicesToAdd)
    {
        HashMap<String, WeServiceCaregiverModel> weServiceCaregiverModelHashMap= new HashMap<>();
        weServiceCaregiverModelHashMap.put("we services",weServicesToAdd);
        return weServiceCaregiverModelHashMap;
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
        mPagerAdapter.addFragment(new GraduationAssessmentFragment());


        String hivStatus = house.getCaregiver_hiv_status();

        if (hivStatus != null && "negative".equalsIgnoreCase(hivStatus)) {
            mPagerAdapter.addFragment(new CaregiverHivAssessmentFragment());
        }


        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText(getString(R.string.fragment_overview));
        mTabLayout.getTabAt(1).setText(getString(R.string.fragment_members));
        mTabLayout.getTabAt(2).setText("CP");
        mTabLayout.getTabAt(3).setText(getString(R.string.fragment_housevisits));
        mTabLayout.getTabAt(4).setText("Grad");
        if (mPagerAdapter.getCount() > 5) {
            mTabLayout.getTabAt(5).setText("HIV ASSESSMENT");
            updateHivRiskTabTitle();
        }

    }
    private void setupFabVisibility() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onPageSelected(int position) {
                if (position == 1 || position == 2 || position == 3) {
                    fab.setVisibility(View.GONE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updateTasksTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText(this.getString(R.string.visits));
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        int visits = CaregiverVisitationDao.countVisits(householdId);

        visitTabCount.setText(String.valueOf(visits));

        mTabLayout.getTabAt(3).setCustomView(taskTabTitleLayout);
    }

    private void updateCaseplanTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.household_plan_tab_title, null);
        TextView casePlanTabTitle = taskTabTitleLayout.findViewById(R.id.household_plans_title);
        casePlanTabTitle.setText("CASE PLAN");
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
    private void updateHivRiskTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText("HIV RISK ASSESSMENT");
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        int visits = CaregiverVisitationDao.countVisits(householdId);

        visitTabCount.setText(String.valueOf(visits));
        visitTabCount.setVisibility(View.GONE);

        mTabLayout.getTabAt(5).setCustomView(taskTabTitleLayout);
    }
    private void updateGradTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText("GRADUATION ASSESSMENT");
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        int visits = GraduationDao.countVisits(householdId);

        visitTabCount.setText(String.valueOf(visits));
//        visitTabCount.setVisibility(View.GONE);

        mTabLayout.getTabAt(4).setCustomView(taskTabTitleLayout);
    }
    private void updateOverviewTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText("OVERVIEW");
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        int visits = CaregiverVisitationDao.countVisits(householdId);

        visitTabCount.setText(String.valueOf(visits));
        visitTabCount.setVisibility(View.GONE);

        mTabLayout.getTabAt(0).setCustomView(taskTabTitleLayout);
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
                boolean areAllVcasVisited = VcaVisitationDao.areAllVcasVisited(householdId);
                boolean areAllVcasAssessed = VcaAssessmentDao.areAllVcasAssessed(householdId);
                boolean hasVisitsByID = CaregiverVisitationDao.hasVisitsByID(householdId);
                boolean hasCaregiverAssessment = CaregiverAssessmentDao.hasCaregiverAssessment(householdId);
                boolean hasHouseholdServices = HouseholdServiceReportDao.hasHouseholdServices(householdId);
                boolean areAllVcasServiced = VCAServiceReportDao.areAllVcasServiced(householdId);

                if(areAllVcasVisited && areAllVcasAssessed && hasVisitsByID && hasCaregiverAssessment && hasHouseholdServices && areAllVcasServiced) {


                    testedChildren = IndexPersonDao.countTestedChildren(householdId);
                    testedChildrenabove15 = IndexPersonDao.countTestedAbove15Children(householdId);
                    int sumtested = Integer.parseInt(testedChildren) + Integer.parseInt(testedChildrenabove15);


                    virally_suppressed = IndexPersonDao.countSuppressedChildren(householdId);
                    positiveChildren = IndexPersonDao.countPositiveChildren(householdId);

                    try {

                        oMapper = new ObjectMapper();
                        graduationMapper = new ObjectMapper();

                        indexRegisterForm = formUtils.getFormJson("graduation");

                        //Populate form details
                        JSONObject ftime = getFieldJSONObject(fields(indexRegisterForm, "step1"), "asmt");
                        ftime.put(JsonFormUtils.VALUE, "no");

                        //Populate Caregiver Details
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, oMapper.convertValue(house, Map.class));

//                    if(graduationModel != null) {
//
//                        indexRegisterForm.put("entity_id", this.graduationModel.getBase_entity_id());
//                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, graduationMapper.convertValue(graduationModel, Map.class));
//
//                    }

                        //Populate for Benchmark 3
                        String bench3 = GradDao.bench3Answers(householdId);
                        int answered = Integer.parseInt(bench3);
                        Integer childrenabove10to17 = countNumberofChildren10to17(allChildrenBirthDates);
                        indexRegisterForm.getJSONObject("step4").getJSONArray("fields").getJSONObject(3).put("value", "1");

                        JSONObject toast_reminder_benchmark_3 = getFieldJSONObject(fields(indexRegisterForm, "step4"), "toast_reminder_benchmark_3");
//                        toast_reminder_benchmark_3.put("type", "toaster_notes");




                        //Benchmark **** 1 **** logic
                        Boolean checkForVCAHivStatus = IndexPersonDao.checkHivStatusInHousehold(householdId);

                        if (checkForVCAHivStatus.equals(true)) {

                            allTested = "yes";
                        } else {
                            allTested = "no";
                        }

                        JSONObject hiv_status_enrolled = getFieldJSONObject(fields(indexRegisterForm, "step2"), "hiv_status_enrolled");
                        hiv_status_enrolled.put(JsonFormUtils.VALUE, allTested);

                        Boolean checkForHivStatus = HouseholdDao.checkCaregiverHivStatusInHousehold(householdId);


                        if (checkForHivStatus.equals(true)) {
                            caregiverTested = "yes";
                        } else {
                            caregiverTested = "no";
                        }


                        JSONObject tested = getFieldJSONObject(fields(indexRegisterForm, "step2"), "caregiver_hiv_status_enrolled");
                        tested.put(JsonFormUtils.VALUE, caregiverTested);



                        //Benchmark **** 2 **** logic updated

                        JSONObject suppressed = getFieldJSONObject(fields(indexRegisterForm, "step3"), "virally_suppressed");
//                    suppressed.put(JsonFormUtils.VALUE, virally_suppressed);

                        JSONObject suppressed_caregiver = getFieldJSONObject(fields(indexRegisterForm, "step3"), "suppressed_caregiver");


                        JSONObject toast_applicable = getFieldJSONObject(fields(indexRegisterForm, "step3"), "toast_applicable");
                        JSONObject toast_na = getFieldJSONObject(fields(indexRegisterForm, "step3"), "toast_na");

                        Boolean hasPositiveVCA = IndexPersonDao.checkForAtLeastOnePositiveVca(householdId);
                        Boolean isCaregiverPositive = HouseholdDao.isCaregiverPositive(householdId);
                        Boolean checkIfVcasWithVLBelow1000MeetingRequirement = IndexPersonDao.doTheVCAsMeetBenchmarkTwo(householdId);
                        Boolean checkForVCAsVL = VCAServiceReportDao.checkAllVcaViralLoads(householdId);
                        Boolean checkForCaregiverVL = HouseholdServiceReportDao.checkForHouseholdViralLoad(householdId);

                        if (hasPositiveVCA.equals(true)) {
                            suppressed.put("hidden", false);
                            toast_applicable.put("type", "hidden");

                            if (checkForVCAsVL.equals(true)) {
                                suppressed.put(JsonFormUtils.VALUE, "yes");
                            } else {
                                suppressed.put(JsonFormUtils.VALUE, "no");
                            }

                        } else {
                            suppressed.put(JsonFormUtils.VALUE, "N/A");
                        }

                        if (isCaregiverPositive.equals(true)) {
                            if (checkForCaregiverVL.equals(true)) {
                                suppressed_caregiver.put(JsonFormUtils.VALUE, "yes");
                            } else {
                                suppressed_caregiver.put(JsonFormUtils.VALUE, "no");
                            }
                        } else {
                            suppressed_caregiver.put(JsonFormUtils.VALUE, "N/A");
                        }

                        if(hasPositiveVCA.equals(false) && isCaregiverPositive.equals(false)){
                            toast_na.put("type", "toaster_notes");
                            toast_na.put("text",cname.getText().toString() + " has no HIV-positive beneficiaries in the household");
                        }


                        //Benchmark **** 3 **** logic

                        Boolean isEveryVCAKnowledgeableAboutHIVPrevention = GradDao.doTheVCAsMeetBenchMarkThree(householdId);
                        Boolean hasBeneficiary10to17InHousehold = IndexPersonDao.hasBeneficiary10to17InHousehold(householdId);
                        Boolean hasVCAInAgeRange = GradDao.hasVCAInAgeRange(householdId);
                        JSONObject prevention = getFieldJSONObject(fields(indexRegisterForm, "step4"), "prevention");

                        if (hasBeneficiary10to17InHousehold) {
                            if (isEveryVCAKnowledgeableAboutHIVPrevention.equals(false)) {
                                prevention.put(JsonFormUtils.VALUE, "no");
                            } else {
                                prevention.put(JsonFormUtils.VALUE, "yes");
                            }
                        } else {
                            toast_reminder_benchmark_3.put("type", "toaster_notes");
                            toast_reminder_benchmark_3.put("text", cname.getText().toString() + " doesn’t have adolescents aged 10 to 17 to be assessed on their knowledge about HIV prevention");
                            prevention.put(JsonFormUtils.VALUE, "N/A");

                        }

                        //Benchmark **** 4 **** logic

                        Boolean hasAtLeastOneVCAUnderFiveYearsOld = IndexPersonDao.hasAtLeastOneVCAUnderFiveYearsOld(householdId);

                        Boolean hasAtLeastOneVCAFiveMonthsAndBelow = IndexPersonDao.hasAtLeastOneVCAFiveMonthsAndBelow(householdId);
                        Boolean hasAtLeastOneVCABetweenSixMonthsAndFiveYearsOld = IndexPersonDao.hasAtLeastOneVCABetweenSixMonthsAndFiveYearsOld(householdId);
                        Boolean checkForMuac = MuacDao.areAllMuacGreen(householdId);
                        Boolean nutritionStatus = VcaVisitationDao.getNutritionStatusForAgeFiveAndBelowByHousehold(householdId);

                        JSONObject malnutrition = getFieldJSONObject(fields(indexRegisterForm, "step5"), "undernourished");
                        JSONObject underFiveToast = getFieldJSONObject(fields(indexRegisterForm, "step5"), "toaster_underFive");

                        // Check for children between 6 months and 5 years
                        if (hasAtLeastOneVCABetweenSixMonthsAndFiveYearsOld) {
                            if (checkForMuac && nutritionStatus) {
                                malnutrition.put(JsonFormUtils.VALUE, "yes");
                            } else {
                                malnutrition.put(JsonFormUtils.VALUE, "no");
                            }
                            // Remove N/A option
                            JSONArray options = malnutrition.getJSONArray(OPTIONS_FIELD_NAME);
                            for (int i = 0; i < options.length(); i++) {
                                JSONObject option = options.getJSONObject(i);
                                if (option.getString("key").equals("N/A")) {
                                    options.remove(i);
                                    break;
                                }
                            }
                        }

                        // Check for children 5 months and below
                        if (hasAtLeastOneVCAFiveMonthsAndBelow) {
                            if (nutritionStatus) {
                                malnutrition.put(JsonFormUtils.VALUE, "yes");
                            } else {
                                malnutrition.put(JsonFormUtils.VALUE, "no");
                            }
                            // Remove N/A option
                            JSONArray options = malnutrition.getJSONArray(OPTIONS_FIELD_NAME);
                            for (int i = 0; i < options.length(); i++) {
                                JSONObject option = options.getJSONObject(i);
                                if (option.getString("key").equals("N/A")) {
                                    options.remove(i);
                                    break;
                                }
                            }
                        }

                        // If no children in either age group
                        if (!hasAtLeastOneVCABetweenSixMonthsAndFiveYearsOld && !hasAtLeastOneVCAFiveMonthsAndBelow) {
                            malnutrition.put(JsonFormUtils.READ_ONLY, true);
                            malnutrition.put(JsonFormUtils.VALUE, "N/A");
                            underFiveToast.put("type", "toaster_notes");
                            underFiveToast.put("text", cname.getText().toString() +
                                    " does not have any adolescents aged 5 and below who need to be assessed for undernourishment");
                        }

                        startFormActivity(indexRegisterForm);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    // List to store error messages for toasts and logging
                    List<String> errorMessages = new ArrayList<>();

                    // Collect error messages for failed conditions
                    if (!areAllVcasVisited) {
                        errorMessages.add("Conduct visits for all VCAs in the household.");
                    }
                    if (!areAllVcasAssessed) {
                        errorMessages.add("Complete vulnerability assessments for all VCAs.");
                    }
                    if (!hasVisitsByID) {
                        errorMessages.add("Record at least one caregiver visit for the household.");
                    }
                    if (!hasCaregiverAssessment) {
                        errorMessages.add("Complete the caregiver household assessment.");
                    }
                    if (!hasHouseholdServices) {
                        errorMessages.add("Provide household services as required.");
                    }
                    if (!areAllVcasServiced) {
                        errorMessages.add("Ensure all VCAs have received required services.");
                    }

                    Log.e("GraduationCheck", "Failed conditions: " + String.join(", ", errorMessages));

                    // Display each error message as a separate toast
                    if (!errorMessages.isEmpty()) {
                        for (String message : errorMessages) {
                            Toasty.error(HouseholdDetails.this, message, Toast.LENGTH_LONG, true).show();
                            // Add a slight delay to prevent overlapping toasts
                            try {
                                Thread.sleep(1000); // 1-second delay between toasts
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // Fallback message if no specific conditions are identified (unlikely)
                        Toasty.error(HouseholdDetails.this, "Cannot proceed with graduation. Please check household requirements.", Toast.LENGTH_LONG, true).show();
                    }
                }
                break;

            case R.id.myservice:

                Intent intent = new Intent(this, HouseholdServiceActivity.class);
                intent.putExtra("householdId",  txtDistrict.getText().toString());
                intent.putExtra("cname",  cname.getText().toString());
                startActivity(intent);

                break;
            case R.id.householdReferrals:

                Intent showReferrals = new Intent(HouseholdDetails.this, ShowHouseholdReferralsActivity.class);
                Bundle referral = new Bundle();
                referral.putString("householdId",house.getHousehold_id());
                referral.putString("householdName",house.getCaregiver_name());
                showReferrals.putExtras(referral);

                startActivity(showReferrals);
                break;

            case R.id.fabx:
                getDeregistrationStatus();
                break;

            case R.id.screenBtn:
            case R.id.hh_screening:

                try {


                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String caseworkerphone = prefs.getString("phone", "Anonymous");
                    String caseworkername = prefs.getString("caseworker_name", "Anonymous");
                    String caseworkerProvince= prefs.getString("province", "Anonymous");

                    householdMapper = new ObjectMapper();

                    indexRegisterForm = formUtils.getFormJson("hh_screening_entry");
                    indexRegisterForm.put("entity_id", this.house.getBid());
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm,householdMapper.convertValue(house, Map.class));

                    JSONObject recentLocation = getFieldJSONObject(fields(indexRegisterForm, "step2"), "recent_location");


//                    recentLocation.put("text",formatGpsCoordinates(house.getHousehold_location() != null ? house.getHousehold_location() : ""));
//                    recentLocation.put("text","Latitude: -15.378761 \nLongitude: 28.320772 1249.0 \nAccuracy: 7.504");



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
                    JSONObject caseworker_province = getFieldJSONObject(fields(indexRegisterForm, "step2"), "province");
                    if (caseworker_province != null) {
                        caseworker_province.remove(JsonFormUtils.VALUE);
                        try {
                            caseworker_province.put(JsonFormUtils.VALUE, caseworkerProvince);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONObject subPop = getFieldJSONObject(fields(indexRegisterForm, "step2"), "sub_population");
                    if (house.getSub_population() != null) {
                        String subPopulationString = house.getSub_population();
                        JSONArray subPopulations = new JSONArray(subPopulationString);
                        JSONArray options = subPop.getJSONArray("options");

                        for (int i = 0; i < options.length(); i++) {
                            JSONObject option = options.getJSONObject(i);
                            String key = option.getString("key");
                            if (subPopulations.toString().contains("\"" + key + "\"")) {
                                option.put("value", "true");
                            } else {
                                option.put("value", "false");
                            }
                        }
                    }
                    else {
                        JSONArray subPopulation = getFieldJSONObject(fields(indexRegisterForm, STEP2), "sub_population").getJSONArray("options");

                        subPopulation.getJSONObject(0).put("value", house.getSubpop1());
                        subPopulation.getJSONObject(1).put("value", house.getSubpop2());
                        subPopulation.getJSONObject(2).put("value", house.getSubpop3());
                        subPopulation.getJSONObject(3).put("value", house.getSubpop4());
                        subPopulation.getJSONObject(5).put("value", house.getSubpop());
                        subPopulation.getJSONObject(4).put("value", house.getSubpop5());
                    }
                    indexRegisterForm.getJSONObject("step3").getJSONArray("fields").getJSONObject(3).put("value", "true");

                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.we_service_caregiver:
                try {

                    indexRegisterForm = formUtils.getFormJson("we_services_caregiver");

                    if (weServiceCaregiverModel == null) {
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, weServiceMapper.convertValue(house, Map.class));
                    }
                    else {
                        indexRegisterForm.put("entity_id", this.weServiceCaregiverModel.getBase_entity_id());
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, weServiceMapper.convertValue(weServiceCaregiverModel, Map.class));
                    }

                    startFormActivity(indexRegisterForm);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case R.id.hcase_plan:
                try {
                    indexRegisterForm = formUtils.getFormJson("care_case_plan");

                    number = new Random();
                    rNumber = number.nextInt(900000000);
                    String assignCasePlanId =  Integer.toString(rNumber);
                    JSONObject case_plan_id = getFieldJSONObject(fields(indexRegisterForm, "step1"), "case_plan_id");


                    if (case_plan_id != null) {
                        case_plan_id.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                        try {
                            case_plan_id.put(JsonFormUtils.VALUE, "CP"+assignCasePlanId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm,oMapper.convertValue(house, Map.class));
                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.h_referral:
                try {

                    indexRegisterForm = formUtils.getFormJson("household_referral");

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



                    indexRegisterForm = formUtils.getFormJson("hh_caregiver_assessment");

                    if(caregiverAssessmentModel == null) {

                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, oMapper.convertValue(house, Map.class));

                        indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(32).put("value", house.getActive_on_treatment());

                    }
                    else{
                        indexRegisterForm.put("entity_id", this.caregiverAssessmentModel.getBase_entity_id());
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, assessmentMapper.convertValue(caregiverAssessmentModel, Map.class));

                        try {

                            JSONObject dateOfVL = getFieldJSONObject(fields(indexRegisterForm, "step1"), "date_of_last_viral_load");


                            if (dateOfVL != null) {

                                if (caregiverAssessmentModel != null && caregiverAssessmentModel.getDate_of_last_viral_load() != null && !caregiverAssessmentModel.getDate_of_last_viral_load().isEmpty()) {
                                    dateOfVL.put(JsonFormUtils.VALUE, caregiverAssessmentModel.getDate_of_last_viral_load());
                                } else {
                                    dateOfVL.put(JsonFormUtils.VALUE, "");
                                }
                            }


                            JSONObject artNumber = getFieldJSONObject(fields(indexRegisterForm, "step1"), "caregiver_art_number");


                            if (artNumber != null) {

                                if (house != null && house.getCaregiver_art_number() != null && !house.getCaregiver_art_number().isEmpty()) {
                                    artNumber.put(JsonFormUtils.VALUE, house.getCaregiver_art_number());
                                } else {

                                    if (caregiverAssessmentModel != null && caregiverAssessmentModel.getCaregiver_art_number() != null) {
                                        artNumber.put(JsonFormUtils.VALUE, caregiverAssessmentModel.getCaregiver_art_number());
                                    } else {
                                        artNumber.put(JsonFormUtils.VALUE, "");
                                    }
                                }
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();

                        } catch (NullPointerException e) {

                            e.printStackTrace();

                        }


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
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm, oMapper.convertValue(house, Map.class));
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


                    number = new Random();
                    rNumber = number.nextInt(900000000);
                    String newEntityId =  Integer.toString(rNumber);


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

            if(EncounterType.equals("Household Screening") || EncounterType.equals("Hiv Assessment For Caregiver") || EncounterType.equals("Referral") || EncounterType.equals("Household Visitation For Caregiver")) {
                Intent openSignatureIntent = new Intent(this, SignatureActivity.class);
                openSignatureIntent.putExtra("jsonForm", jsonFormObject.toString());
                openSignatureIntent.putExtra("householdId",householdId);
                startActivity(openSignatureIntent);
            }
            else
            {
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
//                        finish();
//                        startActivity(getIntent());
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        break;
                    case "WE Services Caregiver":

                        closeFab();
                        Toasty.success(HouseholdDetails.this, "WE form Updated", Toast.LENGTH_LONG, true).show();
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
                    case "Graduation":
                    case "Household Case Status":
                    case "Household Visitation For Caregiver Edit":

                        closeFab();
                        Toasty.success(HouseholdDetails.this, "Form Updated and Saved", Toast.LENGTH_LONG, true).show();
                        finish();
                        startActivity(getIntent());
                        break;

                    case "Caregiver Case Plan":

                        JSONObject date = getFieldJSONObject(fields(jsonFormObject, "step1"), "case_plan_date");
                        String dateId = date.optString("value");

                        JSONObject cpId = getFieldJSONObject(fields(jsonFormObject, "step1"), "case_plan_id");
                        String cp_Id = cpId.optString("value");

                        AddVulnarabilitiesToCasePlan(dateId,cp_Id);
                        break;

                }
            } catch (Exception e) {
                Timber.e(e);
            }

        }}

    }

    public Intent openSignatureActivity(JSONObject jsonFormObject){
        Intent openSignatureIntent   =  new Intent(this,SignatureActivity.class);
        openSignatureIntent.putExtra("jsonForm", jsonFormObject.toString());
        return openSignatureIntent;
    }


    private void AddVulnarabilitiesToCasePlan(String dateId,String cpId) {
        Intent i = new Intent(HouseholdDetails.this, HouseholdCasePlanActivity.class);
        i.putExtra("unique_id",  house.getUnique_id());
        i.putExtra("householdId",  house.getHousehold_id());
        i.putExtra("status",house.getCaregiver_hiv_status());
        i.putExtra("dateId",  dateId);
        i.putExtra("case_plan_id",cpId);
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

                case "Mother Edit":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_mother_index");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;

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

                case "Sub Population":
                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CLIENT_INDEX);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;


                case "Sub Population Edit":
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


                case "Household Screening Edit":

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
                case "Referral":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_REFERRAL);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Household Visitation For Caregiver Edit":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable. EC_CAREGIVER_VISITATION);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;
                case "Hiv Assessment For Caregiver Edit":

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

                case "Graduation":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_GRADUATION);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }

                    break;

                case "WE Services Caregiver":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_WE_SERVICES_CAREGIVER);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                case "Household Case Status":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD);
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
                    Timber.e(e.getMessage());
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
    public boolean saveRegistrationForHouseholdEditing(ChildIndexEventClient newHouseholdIndexEventClient, boolean isEditMode, String encounterType,ChildIndexEventClient oldIndex) {

        Runnable runnable = () -> {

            Event event = newHouseholdIndexEventClient.getEvent();
            Client client = newHouseholdIndexEventClient.getClient();

            Client olDclient = oldIndex.getClient();

            if (event != null && client != null) {
                try {
                    ECSyncHelper ecSyncHelper = getECSyncHelper();

                    JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));
                    JSONObject existingClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(olDclient));

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
                    Timber.e(e.getMessage());
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
            if(house.getCaregiver_hiv_status().equals("positive") ||
                    house.getCaregiver_hiv_status().equals("HIV+")
                    ) {
                chivAssessment.setVisibility(View.GONE);
            } else {
                chivAssessment.setVisibility(View.VISIBLE);
            }

            rassessment.setVisibility(View.VISIBLE);
            rcase_plan.setVisibility(View.VISIBLE);
            refferal.setVisibility(View.VISIBLE);
            child_form.setVisibility(View.VISIBLE);
            household_visitation_caregiver.setVisibility(View.VISIBLE);
            we_service_caregiver.setVisibility(View.VISIBLE);

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
        refferal.setVisibility(View.GONE);
        child_form.setVisibility(View.GONE);
        household_visitation_caregiver.setVisibility(View.GONE);
        we_service_caregiver.setVisibility(View.GONE);
    }

    public void countNumberOfMales(List<String> allBirthDates) {
        int totalNumberOfMalesBelowFive = 0;
        int totalNumberOfMalesBetweenTenAndSeventeen = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (allBirthDates != null) {
            for (int i = 0; i < allBirthDates.size(); i++) {
                try {
                    String dobInCorrectFormat = checkAndConvertDateFormat(allBirthDates.get(i));
                    if (!"Invalid date format".equals(dobInCorrectFormat)) {
                        LocalDate localDateBirthdate = LocalDate.parse(dobInCorrectFormat, formatter);
                        LocalDate today = LocalDate.now();
                        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);

                        if (periodBetweenDateOfBirthAndNow.getYears() >= 0 && periodBetweenDateOfBirthAndNow.getYears() < 5) {
                            totalNumberOfMalesBelowFive++;
                        } else if (periodBetweenDateOfBirthAndNow.getYears() > 10 && periodBetweenDateOfBirthAndNow.getYears() <= 17) {
                            totalNumberOfMalesBetweenTenAndSeventeen++;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing birth date: " + allBirthDates.get(i) + ". Skipping this entry.");
                }
            }
        }

        lessThanFiveMales = String.valueOf(totalNumberOfMalesBelowFive);
        malesBetweenTenAndSevenTeen = String.valueOf(totalNumberOfMalesBetweenTenAndSeventeen);
    }

    public void countNumberOfFemales(List<String> allBirthDates){
        int totalNumberOfFemalesBelowFive = 0;
        int totalNumberOfFemalesBetweenTenAndSeventeen =0 ;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if( allBirthDates != null)
        {
            for(int i = 0; i < allBirthDates.size(); i++)
            {
                String dobInCorrectFormat = checkAndConvertDateFormat(allBirthDates.get(i));
                if (!dobInCorrectFormat.equals("Invalid date format")) {
                    LocalDate localDateBirthdate = LocalDate.parse(dobInCorrectFormat, formatter);
                    LocalDate today =LocalDate.now();
                    Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
                    if(periodBetweenDateOfBirthAndNow.getYears() >= 0 &&  periodBetweenDateOfBirthAndNow.getYears() < 5)
                    {
                        totalNumberOfFemalesBelowFive =totalNumberOfFemalesBelowFive + 1;
                    }
                    else if(periodBetweenDateOfBirthAndNow.getYears() > 10 &&  periodBetweenDateOfBirthAndNow.getYears() <= 17)
                    {
                        totalNumberOfFemalesBetweenTenAndSeventeen =  totalNumberOfFemalesBetweenTenAndSeventeen + 1;
                    }
                }
            }
        }

        lessThanFiveFemales = String.valueOf(totalNumberOfFemalesBelowFive);
        FemalesBetweenTenAndSevenTeen = String.valueOf(totalNumberOfFemalesBetweenTenAndSeventeen);
    }

    public void countChildren10to17(List<String> allBirthDates){
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if( allBirthDates != null)
        {
            for(int i = 0; i < allBirthDates.size(); i++)
            {
                String dobInCorrectFormat = checkAndConvertDateFormat(allBirthDates.get(i));
                if (!dobInCorrectFormat.equals("Invalid date format")) {
                    LocalDate localDateBirthdate = LocalDate.parse(dobInCorrectFormat, formatter);
                    LocalDate today =LocalDate.now();
                    Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);

                    if(periodBetweenDateOfBirthAndNow.getYears() > 2 &&  periodBetweenDateOfBirthAndNow.getYears() < 18)
                    {
                        totalNumberOfChildren = totalNumberOfChildren + 1;
                    }
                }
            }
        }

        totalChildren = String.valueOf(totalNumberOfChildren);
    }

    public int countNumberofChildren10to17(List<String> allBirthDates){
        int totalNumberOfChildren = 0;
        DateTimeFormatter formatter = formatDateByPattern("dd-MM-u");
        if( allBirthDates != null)
        {
            for(int i = 0; i < allBirthDates.size(); i++)
            {
                LocalDate localDateBirthdate = LocalDate.parse(allBirthDates.get(i), formatter);
                LocalDate today =LocalDate.now();
                Period periodBetweenDateOfBirthAndNow = getPeriodBetweenDateOfBirthAndNow(localDateBirthdate, today);
                if(periodBetweenDateOfBirthAndNow.getYears() > 9 &&  periodBetweenDateOfBirthAndNow.getYears() < 18)
                {
                    totalNumberOfChildren = totalNumberOfChildren + 1;
                }
            }
        }

        return totalNumberOfChildren;

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

    public WeServiceCaregiverModel getWeServiceCaregiverModel(String householdId)
    {
        return WeServiceCaregiverDoa.getWeServiceCaregiver(householdId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.household_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.refresh:
                finish();
                startActivity(getIntent());

                break;

            case R.id.call:
                try {
                    String caregiverPhoneNumber = house.getCaregiver_phone();
                    if (caregiverPhoneNumber != null && !caregiverPhoneNumber.equals("")) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + caregiverPhoneNumber));
                        startActivity(callIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No number for caregiver found", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("Phone Number Error", "Exception", e);
                }

                return true;


            case R.id.delete_record:

                builder.setMessage("You are about to delete this household and all its forms.");
                builder.setNegativeButton("NO", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();

                }).setPositiveButton("YES",((dialogInterface, i) -> {

//                    HouseholdDao.deleteRecord(house.getHousehold_id(), house.getBase_entity_id(), childList);
//                    HouseholdDao.deleteRecordfromSearch(house.getHousehold_id(), house.getBase_entity_id(), childList);

                    try {
                       houseHoldsContainingSameId = (ArrayList) HouseholdDao.getDuplicatedHousehold(householdId);
                       if(houseHoldsContainingSameId != null || houseHoldsContainingSameId.size() > 0 )
                       {
                           for(int houseHoldIterator=0; houseHoldIterator < houseHoldsContainingSameId.size(); houseHoldIterator++)
                           {
                               Household householdToDelete = (Household) houseHoldsContainingSameId.get(houseHoldIterator);

                               changeHouseholdStatus(householdToDelete);
                           }
                       }
                     deleteFamilyChildren(householdId);
                       deleteMothers(householdId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toasty.success(HouseholdDetails.this, "Deleted", Toast.LENGTH_LONG, true).show();
                    super.onBackPressed();
                }));

                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert");
                alert.show();


                break;
            case R.id.case_status:
//                Boolean status = IndexPersonDao.checkGraduationStatus(householdId);
//                if(status.equals(false)){
//                    showDialogBox("You need to deregister all the vcas in the household");
//                } else {
                    try {
                        openFormUsingFormUtils(getBaseContext(),"household_case_status");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
//                }
//                FormUtils formUtils = null;
//                try {
//                    formUtils = new FormUtils(HouseholdDetails.this);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                JSONObject indexRegisterForm = formUtils.getFormJson("household_case_status");
//
//                startFormActivity(indexRegisterForm);




                break;
            case R.id.update_caregiver_details:
                try {
                    openFormUsingFormUtils(getBaseContext(),"update_caregiver_details");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                break;



        }
        return super.onOptionsItemSelected(item);
    }

    public void changeHouseholdStatus(Household house) throws Exception {

        //open household screening form
        FormUtils formUtils = null;
        formUtils = new FormUtils(this);
        JSONObject hhScreeningForm = formUtils.getFormJson("hh_edit");
        house.setStatus("1");
        CoreJsonFormUtils.populateJsonForm(hhScreeningForm, new ObjectMapper().convertValue(house, Map.class));
        hhScreeningForm.put("entity_id", house.getBase_entity_id());
        Log.d("Household JSON RESULTS", hhScreeningForm.toString());

        try {

            ChildIndexEventClient childIndexEventClient = processRegistration(hhScreeningForm.toString());
            if (childIndexEventClient == null) {
                return;
            }
            saveRegistration(childIndexEventClient,true,"Household Screening Edit");
           // saveRegistrationForHouseholdEditing(childIndexEventClient, true, "Household Screening",oldIndexEventClient);


        } catch (Exception e) {
            Timber.e(e);
        }

    }


    public void deleteMothers(String HouseholdID) throws Exception {
        //get all Mothers
        List<Mother> allMothers = MotherDao.getMothers(householdId);
        FormUtils formUtils = null;
        formUtils = new FormUtils(this);
        if(allMothers != null && allMothers.size() > 0)
        {
            for( int i = 0; i < allMothers.size(); i++)
            {
                Mother mother = allMothers.get(i);
                mother.setDeleted("1");
                JSONObject motherForm = formUtils.getFormJson("mother_index_edit");
                CoreJsonFormUtils.populateJsonForm(motherForm, new ObjectMapper().convertValue(mother, Map.class));
                motherForm.put("entity_id", mother.getBaseEntityID());

                try {

                    ChildIndexEventClient childIndexEventClient = processRegistration(motherForm.toString());
                    if (childIndexEventClient == null) {
                        return;
                    }
                    saveRegistration(childIndexEventClient,true,"Mother Edit");


                } catch (Exception e) {
                    Timber.e(e);
                }

            }
        }


    }


    public void deleteFamilyChildren(String HouseholdID) throws Exception {
        //get all family children
        List<Child> allChildren = IndexPersonDao.getFamilyChildren(householdId);
        FormUtils formUtils = null;
        formUtils = new FormUtils(this);
        if(allChildren != null && allChildren.size() > 0)
        {
            for( int i = 0; i < allChildren.size(); i++)
            {
                Child child = allChildren.get(i);
                child.setDeleted("1");
                JSONObject vcaScreeningForm = formUtils.getFormJson("vca_edit");
                CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue(child, Map.class));
                vcaScreeningForm.put("entity_id", child.getBase_entity_id());

                try {

                    ChildIndexEventClient childIndexEventClient = processRegistration(vcaScreeningForm.toString());
                    if (childIndexEventClient == null) {
                        return;
                    }
                    saveRegistration(childIndexEventClient,true,"Family Member");


                } catch (Exception e) {
                    Timber.e(e);
                }

            }
        }


    }
    public void getDeregistrationStatus(){
            if (house.getHousehold_case_status() != null && (house.getHousehold_case_status().equals("0") || house.getHousehold_case_status().equals("2"))){
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                showDialogBox(house.getCaregiver_name() + "`s household has been inactive or de-registered");
            } else {
                animateFAB();
            }


    }
    public void changeFabIconColor(){
        if (house.getHousehold_case_status() != null && (house.getHousehold_case_status().equals("0") || house.getHousehold_case_status().equals("2"))){
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            showDialogBox(house.getCaregiver_name() + "`s household has been inactive or de-registered");
        }

    }
    public void showDialogBox(String message){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();

        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        dialogMessage.setText(message);

        Button dialogButton = dialog.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

    }
    public void openFormUsingFormUtils(Context context, String formName) throws JSONException {


        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);

        switch (formName) {

            case "household_case_status":

                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(house, Map.class));
                formToBeOpened.put("entity_id", this.house.getBase_entity_id());
                Boolean vcaGradStatus = IndexPersonDao.checkGraduationStatus(householdId);
                if(vcaGradStatus.equals(false)){
                    JSONObject status = getFieldJSONObject(fields(formToBeOpened, "step1"), "household_case_status");
                    JSONArray options = status.getJSONArray("options");

                    for (int i = 0; i < options.length(); i++) {
                        JSONObject option = options.getJSONObject(i);
                        if ("0".equals(option.getString("key"))) {
                            options.remove(i);
                            break;
                        }
                    }


                    JSONObject info = getFieldJSONObject(fields(formToBeOpened, "step1"), "info");
                    info.put("type", "toaster_notes");
                    info.put("text","If you need to close the case for this household, please deregister the following VCA(s) in the household: \n\n"+IndexPersonDao.returnVcaNames(householdId));

                } else {
                    JSONObject info = getFieldJSONObject(fields(formToBeOpened, "step1"), "info");
                    info.put("type", "hidden");
                }

                GraduationModel graduationModel = GraduationDao.getGraduationStatus(householdId);
                if (graduationModel == null || "0".equals(graduationModel.getGraduation_status()) || graduationModel.getGraduation_status() == null) {

                    JSONObject graduationStatus = getFieldJSONObject(fields(formToBeOpened, "step1"), "graduation_benchmark");
                    if (graduationStatus != null) {
                        graduationStatus.put("type", "toaster_notes");
                        graduationStatus.put("text", house.getCaregiver_name() + "' "  + " household needs to meet all eight graduation benchmarks in order to graduate");
                    }

                    JSONObject reasonField = getFieldJSONObject(fields(formToBeOpened, "step1"), "de_registration_reason");
                    if (reasonField != null) {
                        JSONArray optionsArray = reasonField.getJSONArray("options");
                        if (optionsArray != null) {
                            for (int i = 0; i < optionsArray.length(); i++) {
                                JSONObject option = optionsArray.getJSONObject(i);
                                if (option != null && "Graduated (Household has met the graduation benchmarks in ALL domains)".equals(option.getString("key"))) {
                                    optionsArray.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                }


                break;
            case "update_caregiver_details":
                if (updatedCaregiver != null){
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(updatedCaregiver, Map.class));
                    formToBeOpened.put("entity_id", this.house.getBase_entity_id());
                } else {
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(house, Map.class));
                    formToBeOpened.put("entity_id", this.house.getBase_entity_id());
                }


                break;

        }

        startFormActivity(formToBeOpened);
    }


    public void buildDialog(){
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("VCA Screening");
        alert.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnToHouseholdIndexActivity = new Intent(getBaseContext(), HouseholdIndexActivity.class);
        startActivity(returnToHouseholdIndexActivity);
        finish();
    }
    public void refreshActivity(Household house){
        if(house == null){
            finish();
            startActivity(getIntent());
        }

    }
}
package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.chw.core.utils.CoreJsonFormUtils.getSyncHelper;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
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
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.GraduationDao;
import com.bluecodeltd.ecap.chw.dao.HivAssessmentAbove15Dao;
import com.bluecodeltd.ecap.chw.dao.HivAssessmentUnder15Dao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.ReferralDao;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.dao.VcaAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.VcaCasePlanDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.dao.WeServiceVcaDao;
import com.bluecodeltd.ecap.chw.dao.newCaregiverDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.ChildCasePlanFragment;
import com.bluecodeltd.ecap.chw.fragment.ChildVisitsFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.VcaHivAssesmentFragment;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.ChildRegisterModel;
import com.bluecodeltd.ecap.chw.model.GraduationModel;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentAbove15Model;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentUnder15Model;
import com.bluecodeltd.ecap.chw.model.ReferralModel;
import com.bluecodeltd.ecap.chw.model.VCAModel;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;
import com.bluecodeltd.ecap.chw.model.VcaCasePlanModel;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import com.bluecodeltd.ecap.chw.model.WeServiceVcaModel;
import com.bluecodeltd.ecap.chw.model.newCaregiverModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class IndexDetailsActivity extends AppCompatActivity {
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

    private FloatingActionButton fab, fabHiv,fabHiv2, fabGradSub, fabGrad, fabCasePlan, fabVisitation, fabReferal,  fabAssessment;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    public String childId, uniqueId, vcaAge,is_screened, is_hiv_positive, caseworkerphone;
    private RelativeLayout txtScreening, rassessment, rcase_plan, referral,  household_visitation_for_vca, hiv_assessment,hiv_assessment2,childPlan,weServicesVca;

    public VcaScreeningModel indexVCA;
    private  VcaAssessmentModel assessmentModel;
    private TextView txtName, txtGender, txtAge, txtChildid;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private AppExecutors appExecutors;
    public ProfileViewPagerAdapter mPagerAdapter;
    private TextView visitTabCount, plansTabCount;
    private AppBarLayout myAppbar;
    private Toolbar toolbar;
    private UniqueIdRepository uniqueIdRepository;
    public String gender;

    ObjectMapper oMapper, clientMapper;
    Child child;

    VcaAssessmentModel vcaAssessmentModel;
    ReferralModel referralModel;
    WeServiceVcaModel weServiceVcaModel;
    HivRiskAssessmentAbove15Model hivRiskAssessmentAbove15Model;
    HivRiskAssessmentUnder15Model hivRiskAssessmentUnder15Model;
    VcaVisitationModel vcaVisitationModel;
    VcaCasePlanModel vcaCasePlanModel;
    newCaregiverModel updatedCaregiver;


    public VCAModel client;
    AlertDialog.Builder builder, screeningBuilder;

    Random number;
    int rNumber;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vca_content);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = findViewById(R.id.collapsing_toolbar_appbarlayout);
        NavigationMenu.getInstance(this, null, toolbar);

        builder = new AlertDialog.Builder(IndexDetailsActivity.this);
        screeningBuilder = new AlertDialog.Builder(IndexDetailsActivity.this);

        childId = getIntent().getExtras().getString("Child");
        String hhIntent = getIntent().getExtras().getString("fromHousehold");
        if(hhIntent == null){
            hhIntent = getIntent().getExtras().getString("fromIndex");
        }

        indexVCA = VCAScreeningDao.getVcaScreening(childId);
        child = IndexPersonDao.getChildByBaseId(childId);
        gender = null;


        if (indexVCA != null) {
            if (indexVCA.getGender() != null) {
                gender = indexVCA.getGender();
            } else {
            }
            uniqueId = null;
            if (indexVCA.getUnique_id() != null) {
                uniqueId = indexVCA.getUnique_id();
            } else {
            }
        } else {

        }

//        is_screened = HouseholdDao.checkIfScreened(indexVCA.getHousehold_id());
        if (indexVCA != null) {
            String householdId = indexVCA.getHousehold_id();
            if (householdId != null) {
                is_screened = HouseholdDao.checkIfScreened(householdId);
            }
        }

        is_hiv_positive = null;

        if (indexVCA != null && indexVCA.getUnique_id() != null ) {
            String uniqueId = indexVCA.getUnique_id();
            if (uniqueId != null) {
                is_hiv_positive = VCAScreeningDao.checkStatus(uniqueId);
            }
        }

        fabHiv = findViewById(R.id.hiv_risk);
        fabHiv2 = findViewById(R.id.hiv_risk2);
        fabVisitation = findViewById(R.id.household_visitation_for_vca_fab);
        fabReferal = findViewById(R.id.refer_to_facility_fab);
        fabCasePlan =  findViewById(R.id.case_plan_fab);
        fabAssessment = findViewById(R.id.fabAssessment);

        vcaAssessmentModel = VcaAssessmentDao.getVcaAssessment(childId);
        referralModel = ReferralDao.getReferral(childId);
        hivRiskAssessmentAbove15Model = HivAssessmentAbove15Dao.getHivAssessmentAbove15(childId);
        hivRiskAssessmentUnder15Model = HivAssessmentUnder15Dao.getHivAssessmentUnder15(childId);
        vcaVisitationModel = VcaVisitationDao.getVcaVisitation(childId);
        vcaCasePlanModel = VcaCasePlanDao.getVcaCasePlan(childId);
        weServiceVcaModel = WeServiceVcaDao.getWeServiceVca(childId);
        updatedCaregiver = newCaregiverDao.getNewCaregiverById(indexVCA.getHousehold_id());



        oMapper = new ObjectMapper();
        clientMapper = new ObjectMapper();

        if(vcaAssessmentModel == null){
            fabAssessment.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        }

        if(referralModel == null){
            fabReferal.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        }
        if(hivRiskAssessmentUnder15Model == null){
            fabHiv.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        }
        if(hivRiskAssessmentAbove15Model == null){
            fabHiv2.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        }
        if(vcaVisitationModel == null){
            fabVisitation.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        }
        if(vcaCasePlanModel == null){
            fabCasePlan.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        }

        if( gender != null && gender.equals("male")){

            toolbar.setBackgroundDrawable(new ColorDrawable(0xff218CC5));
            myAppbar.setBackgroundDrawable(new ColorDrawable(0xff218CC5));

        } else {

            toolbar.setBackgroundDrawable(new ColorDrawable(0xffDA70D6));
            myAppbar.setBackgroundDrawable(new ColorDrawable(0xffDA70D6));

        }


        fab = findViewById(R.id.fab);
        if(indexVCA.getCase_status() != null && (indexVCA.getCase_status().equals("0") || indexVCA.getCase_status().equals("2"))){
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        }

//        String subpop1 = child.getSubpop1();
//        String subpop2 = child.getSubpop2();
//        String subpop3 = child.getSubpop3();
//        String subpop4 = child.getSubpop4();
//        String subpop5 = child.getSubpop5();
//        String subpop6 = child.getSubpop6();
//
//        if (subpop1 != null || subpop2 != null || subpop3 != null ||
//                subpop4 != null || subpop5 != null || subpop6 != null) {
//            fab.setVisibility(View.VISIBLE);
//        }
//        else {
//            fab.setVisibility(View.INVISIBLE);
//        }
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        txtScreening = findViewById(R.id.vca_screening);
        rassessment = findViewById(R.id.assessment);
        rcase_plan = findViewById(R.id.case_plan);
        referral = findViewById(R.id.referral);
        household_visitation_for_vca = findViewById(R.id.household_visitation_for_vca);



        hiv_assessment = findViewById(R.id.hiv_assessment);
        hiv_assessment2 = findViewById(R.id.hiv_assessment2);
        childPlan = findViewById(R.id.childPlan);
        weServicesVca = findViewById(R.id.we_services_vca);

        txtName = findViewById(R.id.vca_name);
        txtGender = findViewById(R.id.vca_gender);
        txtAge = findViewById(R.id.vca_age);
        txtChildid = findViewById(R.id.childid);

        mTabLayout =  findViewById(R.id.tabs);
        mViewPager  = findViewById(R.id.viewpager);

        setupViewPager();
        setupFabVisibility();
        updateOverviewTabTitle();
        updateVisitsTabTitle();
        updatePlanTabTitle();

        int page = getIntent().getIntExtra("tab",0);
        mViewPager.setCurrentItem(page);

createDialogForScreening(hhIntent,Constants.EcapConstants.POP_UP_DIALOG_MESSAGE);


    }


    public HashMap<String, Child> getData() {
        String full_name = indexVCA.getFirst_name() + " " + indexVCA.getLast_name();
        String gender =  indexVCA.getGender();
        String birthdate = checkAndConvertDateFormat(indexVCA.getAdolescent_birthdate());

        if(birthdate != null){
            txtAge.setText(getAge(birthdate));
            vcaAge = getAgeWithoutText(birthdate);

        } else {
            txtAge.setText("Not Set");
        }

        try {
            txtName.setText(full_name);
            txtGender.setText(gender.toUpperCase());
            txtChildid.setText("ID : " + indexVCA.getUnique_id());
        } catch (NullPointerException e) {
            txtGender.setText("");
            e.printStackTrace();
        }


        HashMap<String, Child> map = new HashMap<>();

        map.put("Child",child);

        return map;

    }
    private String checkAndConvertDateFormat(String date){
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
    public HashMap<String, newCaregiverModel> getUpdatedCaregiverData() {

        HashMap<String, newCaregiverModel> map = new HashMap<>();

        map.put("UpdatedCaregiver",updatedCaregiver);

        return map;

    }



    public int calculateAge(String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);
        Period period = Period.between(birthDate, currentDate);
        int age = period.getYears();
        return age;
    }

    private String getAgeWithoutText(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            return String.valueOf(periodBetweenDateOfBirthAndNow.getYears());
        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){
            return String.valueOf(periodBetweenDateOfBirthAndNow.getMonths());
        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return String.valueOf(periodBetweenDateOfBirthAndNow.getDays());
        }
        else return "Not Set";
    }



    private String getAge(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
         if(periodBetweenDateOfBirthAndNow.getYears() >0)
         {
             return periodBetweenDateOfBirthAndNow.getYears() +" Years";
         }
         else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){
                 return periodBetweenDateOfBirthAndNow.getMonths() +" Months ";
             }
         else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
             return periodBetweenDateOfBirthAndNow.getDays() +" Days ";
         }
         else return "Not Set";
    }

    private void setupViewPager(){
        mPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new ProfileOverviewFragment());
        mPagerAdapter.addFragment(new ChildCasePlanFragment());
        mPagerAdapter.addFragment(new ChildVisitsFragment());

        String hivStatus = indexVCA.getIs_hiv_positive();

        if (hivStatus != null && "no".equalsIgnoreCase(hivStatus)) {
            mPagerAdapter.addFragment(new VcaHivAssesmentFragment());
        }

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText("OVERVIEW");
        mTabLayout.getTabAt(1).setText("CASE PLANS");
        mTabLayout.getTabAt(2).setText("VISITS");
        if (mPagerAdapter.getCount() > 3) {
            mTabLayout.getTabAt(3).setText("HIV ASSESSMENT");
            updateHivAssessmentTabTitle();
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
    private void updateOverviewTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText("OVERVIEW");
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        visitTabCount.setVisibility(View.GONE);

        mTabLayout.getTabAt(0).setCustomView(taskTabTitleLayout);
    }
    private void updateHivAssessmentTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText("HIV ASSESSMENT");
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        visitTabCount.setVisibility(View.GONE);

        mTabLayout.getTabAt(3).setCustomView(taskTabTitleLayout);
    }
    private void updateVisitsTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText(this.getString(R.string.visits));
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        int visits = VcaVisitationDao.countVisits(uniqueId);

        visitTabCount.setText(String.valueOf(visits));

        mTabLayout.getTabAt(2).setCustomView(taskTabTitleLayout);
    }

    private void updatePlanTabTitle() {
        ConstraintLayout plansTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.plan_tab_title, null);
        TextView visitTabTitle = plansTabTitleLayout.findViewById(R.id.plans_title);
        visitTabTitle.setText("CASE PLANS");
        plansTabCount = plansTabTitleLayout.findViewById(R.id.plans_count);

        int plans = CasePlanDao.checkCasePlan(uniqueId);

        plansTabCount.setText(String.valueOf(plans));

        mTabLayout.getTabAt(1).setCustomView(plansTabTitleLayout);
    }


    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName(getString(R.string.child_details));
        form.setHideSaveLabel(true);
        form.setNextLabel(getString(R.string.next));
        form.setPreviousLabel(getString(R.string.previous));
        form.setSaveLabel(getString(R.string.submit));
        form.setNavigationBackground(R.color.primary);
        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }



    @SuppressLint("RestrictedApi")
    public void onClick(View v) throws JSONException {
        int id = v.getId();

        switch (id){
            case R.id.fab:
                try {
                    if (child.getCase_status() != null &&
                            (child.getCase_status().equals("0") || child.getCase_status().equals("2"))) {
                        showDeregisteredStatus();
                    } else {
                        animateFAB();
                    }
                } catch (NullPointerException e) {
                    animateFAB();
                }

                break;

                case R.id.vca_screening:

                    try {

                        openFormUsingFormUtils(IndexDetailsActivity.this,"vca_screening");
                    } catch (JSONException e) {
                        e.printStackTrace();
                   }

                break;


            case R.id.assessment:

                if(indexVCA.getDate_screened() != null){
                    try {
                        openFormUsingFormUtils(IndexDetailsActivity.this,"vca_assessment");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toasty.warning(IndexDetailsActivity.this, "VCA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }


                break;
            case R.id.case_plan:

                if(indexVCA.getDate_screened() != null){
                    try {
                        openFormUsingFormUtils(IndexDetailsActivity.this,"case_plan");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toasty.warning(IndexDetailsActivity.this, "VCA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }

                break;
            case R.id.referral:

                if(indexVCA.getDate_screened() != null){

                    try {

                        openFormUsingFormUtils(IndexDetailsActivity.this,"referral");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toasty.warning(IndexDetailsActivity.this, "VCA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }


                break;

            case R.id.myservice:

                if(indexVCA.getDate_screened() != null) {

                    Intent intent2 = new Intent(this, VcaServiceActivity.class);
                    intent2.putExtra("hh_id", indexVCA.getHousehold_id());
                    intent2.putExtra("vcaid", indexVCA.getUnique_id());
                    intent2.putExtra("hivstatus", indexVCA.getIs_hiv_positive());
                    intent2.putExtra("vcaname", txtName.getText().toString());
                    startActivity(intent2);
                }
                else{
                    Toasty.warning(IndexDetailsActivity.this, "VCA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;
            case R.id.show_referrals:


                Intent showReferrals = new Intent(IndexDetailsActivity.this, ShowReferralsActivity.class);
                    Bundle referral = new Bundle();
                    referral.putString("childId",child.getUnique_id());
                    referral.putString("name",child.getAdolescent_first_name()+ "  " +child.getAdolescent_last_name());
                    showReferrals.putExtras(referral);
//                    referral.putString("date",child.getDate_referred());
//                showReferrals.putExtra("childId",  child.getUnique_id());
//                showReferrals.putExtra("householdId",  child.getHousehold_id());
                // intent.putExtra("household",  child.getHousehold_id());

                startActivity(showReferrals);


                break;

            case R.id.household_profile:


            Intent intent = new Intent(this, HouseholdDetails.class);
            intent.putExtra("childId",  child.getUnique_id());
            intent.putExtra("householdId",  child.getHousehold_id());
//            intent.putExtra("householdId",  child.getHousehold_id());
           // intent.putExtra("household",  child.getHousehold_id());

            startActivity(intent);


                break;

            case R.id.household_visitation_for_vca:

                if(indexVCA.getDate_screened() != null) {
                    try {

                        openFormUsingFormUtils(IndexDetailsActivity.this,"household_visitation_for_vca_0_20_years");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else{
                    Toasty.warning(IndexDetailsActivity.this, "VCA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }


                break;

            case R.id.hiv_assessment:

                if(indexVCA.getDate_screened() != null) {
                    openFormUsingFormUtils(IndexDetailsActivity.this, "hiv_risk_assessment_under_15_years");
                } else {
                    Toasty.warning(IndexDetailsActivity.this, "VCA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;

            case R.id.hiv_assessment2:
                if(indexVCA.getDate_screened() != null) {
                    openFormUsingFormUtils(IndexDetailsActivity.this, "hiv_risk_assessment_above_15_years");
                }else{
                    Toasty.warning(IndexDetailsActivity.this, "VCA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;

            case R.id.we_services_vca:
                if(indexVCA.getDate_screened() != null) {
                    openFormUsingFormUtils(IndexDetailsActivity.this, "we_services_vca");
                }else{
                    Toasty.warning(IndexDetailsActivity.this, "VCA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;
            case R.id.childPlan:
                Intent i = new Intent(IndexDetailsActivity.this, ChildSafetyPlanActivity.class);
                i.putExtra("vca_id", indexVCA.getUnique_id());
                i.putExtra("vca_name", indexVCA.getFirst_name() + ' ' + indexVCA.getLast_name());
                startActivity(i);
                finish();
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
            String encounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");

            if (!jsonFormObject.optString("entity_id").isEmpty()) {
                is_edit_mode = true;
            }
            if (encounterType.equals("Referral")) {

                Intent openSignatureIntent   =  new Intent(this,SignatureActivity.class);
                openSignatureIntent.putExtra("jsonForm", jsonFormObject.toString());
                startActivity(openSignatureIntent);
            } else if(encounterType.equals("Household Visitation Form 0-20 years")){
                Intent openSignatureIntent   =  new Intent(this,SignatureActivity.class);
                openSignatureIntent.putExtra("Child",childId);
                openSignatureIntent.putExtra("jsonForm", jsonFormObject.toString());
                startActivity(openSignatureIntent);
            } else
            {
                try{

                    ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                    if (childIndexEventClient == null) {
                        return;
                    }

                    saveRegistration(childIndexEventClient, is_edit_mode);


                    switch (encounterType) {
                        case "VCA Case Plan":

                            JSONObject cpdate = getFieldJSONObject(fields(jsonFormObject, "step1"), "case_plan_date");
                            String dateId = cpdate.optString("value");

                            JSONObject cpId = getFieldJSONObject(fields(jsonFormObject, "step1"), "case_plan_id");
                            String cp_Id = cpId.optString("value");

                            finish();
                            startActivity(getIntent());
                            openVcaCasplanToAddVulnarabilities(dateId, cp_Id);

                            break;

                        case "Household Visitation Form 0-20 years":
                        case "Member Sub Population":
                        case "Sub Population":
                        case "VCA Assessment":
                        case "HIV Risk Assessment Above 15":
                        case "HIV Risk Assessment Below 15":

                            finish();
                            startActivity(getIntent());

                            break;
                        case "Case Record Status":

                            finish();
                            startActivity(getIntent());
                            Intent i = new Intent(getApplicationContext(), IndexRegisterActivity.class);
                            startActivity(i);

                            break;

                    }

                    Toasty.success(IndexDetailsActivity.this, "Form Saved", Toast.LENGTH_LONG, true).show();

                } catch (Exception e) {
                    Timber.e(e);
                }

            }
        }

    }

    private void openVcaCasplanToAddVulnarabilities(String dateId,String cpId) {
        Intent i = new Intent(IndexDetailsActivity.this, CasePlan.class);
        i.putExtra("childId", indexVCA.getUnique_id());
        i.putExtra("dateId",  dateId);
        i.putExtra("case_plan_id",cpId);
        i.putExtra("hivStatus",  indexVCA.getIs_hiv_positive());
        startActivity(i);
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

    public void updateCommonPersonObject(String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = getCommonRepository(CoreConstants.TABLE_NAME.EC_CLIENT_INDEX).rawCustomQueryForAdapter("SELECT * FROM " + CoreConstants.TABLE_NAME.EC_CLIENT_INDEX + " WHERE base_entity_id = " + "'" + baseEntityId + "'");
            if (cursor != null && cursor.moveToFirst()) {
                CommonPersonObject personObject = getCommonRepository(CoreConstants.TABLE_NAME.EC_CLIENT_INDEX).readAllcommonforCursorAdapter(cursor);
                client = new VCAModel(personObject.getCaseId(),
                        personObject.getDetails(), "");
                client.setColumnmaps(personObject.getColumnmaps());
            }
        } catch (Exception ex) {
            Timber.e(ex, "CoreChildProfileInteractor --> updateChildCommonPerson");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
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
                case "Member Sub Population":
                case "Sub Population":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CLIENT_INDEX);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
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

                case "WE Services VCA":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_WE_SERVICES_VCA);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "VCA Assessment":

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
                case "Household Visitation Form 0-20 years Edit":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HOUSEHOLD_VCA);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "HIV Risk Assessment Above 15":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HIV_ASSESSMENT_ABOVE_15);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "HIV Risk Assessment Below 15":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HIV_ASSESSMENT_BELOW_15);
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

                case "Child Safety Plan":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CHILD_SAFETY_PLAN);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
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

            closeFab();
        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);
            txtScreening.setVisibility(View.VISIBLE);


            if (is_screened != null && is_screened.equals("true")){

                rcase_plan.setVisibility(View.VISIBLE);
                referral.setVisibility(View.VISIBLE);
                household_visitation_for_vca.setVisibility(View.VISIBLE);
                childPlan.setVisibility(View.VISIBLE);



                if(indexVCA.getIs_hiv_positive() != null){
                    rassessment.setVisibility(View.VISIBLE);
                }
                if(indexVCA.getIs_hiv_positive() != null && indexVCA.getIs_hiv_positive().equals("yes")){
                    hiv_assessment.setVisibility(View.GONE);
                } else {
                    if(Integer.parseInt(vcaAge) > 1){
                        if(Integer.parseInt(vcaAge) < 15){
                            hiv_assessment.setVisibility(View.VISIBLE);
                        }

                        if(Integer.parseInt(vcaAge) >= 15){
                            hiv_assessment2.setVisibility(View.VISIBLE);
                        }


                    }
                }


                if(Integer.parseInt(vcaAge) > 18){
                    weServicesVca.setVisibility(View.VISIBLE);
                }
            }
            else{

                Toasty.warning(IndexDetailsActivity.this, "VCA Household Hasn't Been Screened", Toast.LENGTH_LONG, true).show();

            }
        }

    }

    public void closeFab(){
        fab.startAnimation(rotate_backward);
        isFabOpen = false;
        txtScreening.setVisibility(View.GONE);
        rassessment.setVisibility(View.GONE);
        rcase_plan.setVisibility(View.GONE);
        referral.setVisibility(View.GONE);
        household_visitation_for_vca.setVisibility(View.GONE);
        hiv_assessment.setVisibility(View.GONE);
        hiv_assessment2.setVisibility(View.GONE);
        childPlan.setVisibility(View.GONE);
        weServicesVca.setVisibility(View.GONE);


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
        formToBeOpened.getJSONObject("step1").put("title", this.indexVCA.getFirst_name() + " " + this.indexVCA.getLast_name() + " : " + txtAge.getText().toString() + " - " + txtGender.getText().toString());
        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("value", indexVCA.getUnique_id());

        switch (formName) {

            case "case_status":
                if(indexVCA.getIs_on_hiv_treatment() == null){
                    @NotNull Map<String, String> indexVCAMap = oMapper.convertValue(indexVCA, Map.class);
                    indexVCAMap.remove("date_started_art");
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, indexVCAMap);
                } else {
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));
                }

                //Populate Caseworker Name
                populateCaseworkerPhoneAndName(formToBeOpened);

                if(indexVCA.getIndex_check_box() != null && !indexVCA.getIndex_check_box().equals("1")){
                    formToBeOpened.remove(JsonFormUtils.ENCOUNTER_TYPE);
                    formToBeOpened.put(JsonFormUtils.ENCOUNTER_TYPE, "Member Sub Population");
                }
                GraduationModel graduationModel = GraduationDao.getGraduationStatus(child.getHousehold_id());
                if (graduationModel == null || "0".equals(graduationModel.getGraduation_status()) || graduationModel.getGraduation_status() == null) {

                JSONObject graduationStatus = getFieldJSONObject(fields(formToBeOpened, "step1"), "graduation_benchmark");
                    if (graduationStatus != null) {
                        graduationStatus.put("type", "toaster_notes");
                        graduationStatus.put("text", indexVCA.getFirst_name() + " " + indexVCA.getLast_name() + " household needs to meet all eight graduation benchmarks in order to graduate");
                    }

                    JSONObject reasonField = getFieldJSONObject(fields(formToBeOpened, "step1"), "reason");
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



                formToBeOpened.put("entity_id", this.indexVCA.getBase_entity_id());

                break;

            case "vca_screening":
                if(indexVCA.getIs_on_hiv_treatment() == null){
                    @NotNull Map<String, String> indexVCAMap = oMapper.convertValue(indexVCA(), Map.class);
                    indexVCAMap.remove("date_started_art");
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, indexVCAMap);
                } else {
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA(), Map.class));
                }

                //Populate Caseworker Name
                populateCaseworkerPhoneAndName(formToBeOpened);

                if(indexVCA.getIndex_check_box() != null && !indexVCA.getIndex_check_box().equals("1")){
                    formToBeOpened.remove(JsonFormUtils.ENCOUNTER_TYPE);
                    formToBeOpened.put(JsonFormUtils.ENCOUNTER_TYPE, "Member Sub Population");
                }


                formToBeOpened.put("entity_id", this.indexVCA.getBase_entity_id());

                break;


            case "vca_assessment":
                if(vcaAssessmentModel == null){

                    //Pulls data for populating from indexchild when adding data for the very first time
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", vcaAge);
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(0).put("value", indexVCA.getSubpop1());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(1).put("value", indexVCA.getSubpop2());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(2).put("value", indexVCA.getSubpop3());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(3).put("value", indexVCA.getSubpop4());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(5).put("value", indexVCA.getSubpop());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(4).put("value", indexVCA.getSubpop5());


                } else {

                    formToBeOpened.put("entity_id", this.vcaAssessmentModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vcaAssessmentModel, Map.class));

                }

            break;

                case "household_visitation_for_vca_0_20_years":
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", vcaAge);
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));

                break;
            case "we_services_vca":
                if(weServiceVcaModel == null){

                    //Pulls data for populating from indexchild when adding data for the very first time
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));

                } else {

                    formToBeOpened.put("entity_id", this.weServiceVcaModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(weServiceVcaModel, Map.class));
                }
                break;

            case "case_plan":
                number = new Random();
                rNumber = number.nextInt(900000000);
                String assignCasePlanId =  Integer.toString(rNumber);
                JSONObject case_plan_id = getFieldJSONObject(fields(formToBeOpened, "step1"), "case_plan_id");


                if (case_plan_id != null) {
                    case_plan_id.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                    try {
                        case_plan_id.put(JsonFormUtils.VALUE, "CP"+assignCasePlanId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));
                formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", vcaAge);

             /*   if(vcaCasePlanModel == null){

                } else {
                    formToBeOpened.put("entity_id", this.vcaCasePlanModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vcaCasePlanModel, Map.class));

                }*/

                break;

            case "hiv_risk_assessment_under_15_years":

//                if(hivRiskAssessmentUnder15Model == null){

                    //Pulls data for populating from indexchild when adding data for the very first time
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));

//                } else {
//
//                    formToBeOpened.put("entity_id", this.hivRiskAssessmentUnder15Model.getBase_entity_id());
//                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(hivRiskAssessmentUnder15Model, Map.class));
//                }

                break;

            case "hiv_risk_assessment_above_15_years":

//                if(hivRiskAssessmentAbove15Model == null){

                    //Pulls data for populating from indexchild when adding data for the very first time
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));

//                } else {
//
//                    formToBeOpened.put("entity_id", this.hivRiskAssessmentAbove15Model.getBase_entity_id());
//                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(hivRiskAssessmentAbove15Model, Map.class));
//                }

                break;

            case "child_safety_plan":
            case "referral":
                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));
                populateCaseworkerPhoneAndName(formToBeOpened);
            break;

    }

        startFormActivity(formToBeOpened);
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
                case R.id.refresh:
                    finish();
                    startActivity(getIntent());

                    break;

                case R.id.call:
                    try {
                        String caregiverPhoneNumber = child.getCaregiver_phone();
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
            case R.id.case_status:

                try {
                    openFormUsingFormUtils(IndexDetailsActivity.this, "case_status");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.delete_record:

                    if(txtGender.getText().toString().equals("MALE")){
                        gender = "his";
                    } else {
                        gender = "her";
                    }

                    builder.setMessage("You are about to delete this VCA and all " + gender + " forms.");
                    builder.setNegativeButton("NO", (dialog, id) -> {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }).setPositiveButton("YES",((dialogInterface, i) -> {
                        FormUtils formUtils = null;
                        try {
                            formUtils = new FormUtils(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        child.setDeleted("1");
                            JSONObject vcaScreeningForm = formUtils.getFormJson("vca_edit");
                            try {
                                CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue(child, Map.class));
                                vcaScreeningForm.put("entity_id", child.getBase_entity_id());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            try {

                                ChildIndexEventClient childIndexEventClient = processRegistration(vcaScreeningForm.toString());
                                if (childIndexEventClient == null) {
                                    return;
                                }
                                saveRegistration(childIndexEventClient,true);


                            } catch (Exception e) {
                                Timber.e(e);
                            }



                        Toasty.success(IndexDetailsActivity.this, "Deleted", Toast.LENGTH_LONG, true).show();
                        super.onBackPressed();
                    }));

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Alert");
                    alert.show();

                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent returnToHouseholdIndexActivity = new Intent(IndexDetailsActivity.this, IndexRegisterActivity.class);
        startActivity(returnToHouseholdIndexActivity);
        finish();

    }
 public void createDialogForScreening(String entryPoint, String message){
        if(entryPoint != null ) {
            if (entryPoint.equals("123") && is_screened.equals("false")) {

                builder.setMessage(message + indexVCA.getFirst_name() + " " + indexVCA.getLast_name() + "?");
                builder.setNegativeButton("Later", (dialog, id) -> {
                    //  Action for 'NO' Button
                    getIntent().removeExtra("fromHousehold");
                    dialog.cancel();

                }).setPositiveButton(Constants.EcapConstants.PROCEED, ((dialogInterface, i) -> {
                    getIntent().removeExtra("fromHousehold");
                    try {
                        openFormUsingFormUtils(IndexDetailsActivity.this, "vca_screening");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }));
                buildDialog();

            } else if (entryPoint.equals("321") && is_screened.equals("false")) {
                builder.setMessage(Constants.EcapConstants.POP_UP_DIALOG_MESSAGE_FOR_HOUSEHOLD + indexVCA.getFirst_name() + " " + indexVCA.getLast_name() + "?");
                builder.setNegativeButton("Later", (dialog, id) -> {
                    //  Action for 'NO' Button
                    getIntent().removeExtra("fromIndex");
                    dialog.cancel();

                }).setPositiveButton(Constants.EcapConstants.PROCEED, ((dialogInterface, i) -> {
                    getIntent().removeExtra("fromIndex");
                    try {
                        Intent intent = new Intent(this, HouseholdDetails.class);
                        intent.putExtra("childId", child.getUnique_id());
                        intent.putExtra("householdId", child.getHousehold_id());
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                buildDialog();
            }

        }


 }
 public void populateCaseworkerPhoneAndName(JSONObject formToBeOpened){
     SharedPreferences cp = PreferenceManager.getDefaultSharedPreferences(IndexDetailsActivity.this);
     caseworkerphone = cp.getString("phone", "Anonymous");

     JSONObject cphone = getFieldJSONObject(fields(formToBeOpened, "step1"), "phone");

     if (cphone  != null) {
         cphone .remove(JsonFormUtils.VALUE);
         try {
             cphone .put(JsonFormUtils.VALUE, caseworkerphone);
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

 }
    public void showDeregisteredStatus(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();

        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        dialogMessage.setText(indexVCA.getFirst_name() + " " + indexVCA.getLast_name() + " was either de-registered or inactive in the program");

        Button dialogButton = dialog.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

    }
    private int calculateAndReturnAge(String inputDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date dateOfBirth = sdf.parse(inputDate);

            Calendar dob = Calendar.getInstance();
            dob.setTime(dateOfBirth);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void buildDialog(){
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("VCA Screening");
        alert.show();
    }
    public Object indexVCA() {
        VcaScreeningModel screeningModel = new VcaScreeningModel();

        screeningModel.setBase_entity_id(indexVCA.getBase_entity_id());
        screeningModel.setDeleted(indexVCA.getDeleted());
        screeningModel.setHousehold_id(indexVCA.getHousehold_id());
        screeningModel.setUnique_id(indexVCA.getUnique_id());
        screeningModel.setSignature(indexVCA.getSignature());
        screeningModel.setDate_edited(indexVCA.getDate_edited());
        screeningModel.setPhone(indexVCA.getPhone());
        screeningModel.setCaseworker_name(indexVCA.getCaseworker_name());
        screeningModel.setProvince(indexVCA.getProvince());
        screeningModel.setDistrict(indexVCA.getDistrict());
        screeningModel.setWard(indexVCA.getWard());
        screeningModel.setFacility(indexVCA.getFacility());
        screeningModel.setPartner(indexVCA.getPartner());
        screeningModel.setAdolescent_first_name(indexVCA.getAdolescent_first_name());
        screeningModel.setAdolescent_last_name(indexVCA.getAdolescent_last_name());
        screeningModel.setAdolescent_birthdate(indexVCA.getAdolescent_birthdate());
        screeningModel.setHomeaddress(indexVCA.getHomeaddress());
        screeningModel.setLandmark(indexVCA.getLandmark());
        screeningModel.setAdolescent_gender(indexVCA.getAdolescent_gender());
        screeningModel.setSchool(indexVCA.getSchool());
        screeningModel.setSchoolName(indexVCA.getSchoolName());
        screeningModel.setOther_school(indexVCA.getOther_school());
        screeningModel.setIs_hiv_positive(indexVCA.getIs_hiv_positive());
        screeningModel.setIs_on_hiv_treatment(indexVCA.getIs_on_hiv_treatment());
        screeningModel.setArt_number(indexVCA.getArt_number());
        screeningModel.setViral_load_results_on_file(indexVCA.getViral_load_results_on_file());
        screeningModel.setIs_tb_screening_results_on_file(indexVCA.getIs_tb_screening_results_on_file());
        screeningModel.setClient_screened(indexVCA.getClient_screened());
        screeningModel.setClient_result(indexVCA.getClient_result());
        screeningModel.setTpt_client_eligibility(indexVCA.getTpt_client_eligibility());
        screeningModel.setTpt_client_initiated(indexVCA.getTpt_client_initiated());
        screeningModel.setScreened_for_malnutrition(indexVCA.getScreened_for_malnutrition());
        screeningModel.setTakes_drugs_to_prevent_other_diseases(indexVCA.getTakes_drugs_to_prevent_other_diseases());
        screeningModel.setLess_3(indexVCA.getLess_3());
        screeningModel.setPositive_mother(indexVCA.getPositive_mother());
        screeningModel.setActive_on_treatment(indexVCA.getActive_on_treatment());
        screeningModel.setCaregiver_art_number(indexVCA.getCaregiver_art_number());
        if (indexVCA.getDate_next_vl_vca() != null){
            screeningModel.setDate_next_vl_vca(indexVCA.getDate_next_vl_vca());
        } else {
            screeningModel.setDate_next_vl_vca(indexVCA.getDate_next_vl());
        }

        screeningModel.setAdhering_to_treatment(indexVCA.getAdhering_to_treatment());
        screeningModel.setIs_mother_virally_suppressed(indexVCA.getIs_mother_virally_suppressed());
        screeningModel.setChild_been_tested_for_hiv(indexVCA.getChild_been_tested_for_hiv());
        screeningModel.setChild_receiving_breastfeeding(indexVCA.getChild_receiving_breastfeeding());
        screeningModel.setChild_tested_for_hiv_inline_with_guidelines(indexVCA.getChild_tested_for_hiv_inline_with_guidelines());
        screeningModel.setReceives_drugs_to_prevent_hiv_and_other_illnesses_hei(indexVCA.getReceives_drugs_to_prevent_hiv_and_other_illnesses_hei());
        screeningModel.setChild_been_screened_for_malnutrition_hei(indexVCA.getChild_been_screened_for_malnutrition_hei());
        screeningModel.setChild_gets_drugs_to_prevent_tb_hei(indexVCA.getChild_gets_drugs_to_prevent_tb_hei());
        screeningModel.setChild_enrolled_in_early_childhood_development_program(indexVCA.getChild_enrolled_in_early_childhood_development_program());
        screeningModel.setIs_biological_child_of_mother_living_with_hiv(indexVCA.getIs_biological_child_of_mother_living_with_hiv());
        screeningModel.setChild_tested_for_hiv_with_mother_as_index_client(indexVCA.getChild_tested_for_hiv_with_mother_as_index_client());
        screeningModel.setIs_mother_currently_on_treatment_wlhiv(indexVCA.getIs_mother_currently_on_treatment_wlhiv());
        screeningModel.setMother_art_number_wlhiv(indexVCA.getMother_art_number_wlhiv());
        screeningModel.setIs_mother_adhering_to_treatment_wlhiv(indexVCA.getIs_mother_adhering_to_treatment_wlhiv());
        screeningModel.setIs_mother_virally_suppressed_wlhiv(indexVCA.getIs_mother_virally_suppressed_wlhiv());
        screeningModel.setChild_receiving_any_hiv_and_violence_prevention_intervention(indexVCA.getChild_receiving_any_hiv_and_violence_prevention_intervention());
        screeningModel.setAgyw_sexually_active(indexVCA.getAgyw_sexually_active());
        screeningModel.setHiv_or_pregnancy_prevention_method_used(indexVCA.getHiv_or_pregnancy_prevention_method_used());
        screeningModel.setHiv_or_pregnancy_prevention_method_used_other(indexVCA.getHiv_or_pregnancy_prevention_method_used_other());
        screeningModel.setAgyw_having_sex_with_older_men(indexVCA.getAgyw_having_sex_with_older_men());
        screeningModel.setAgyw_transactional_sex(indexVCA.getAgyw_transactional_sex());
        screeningModel.setAgyw_engaged_in_transactional_sex(indexVCA.getAgyw_engaged_in_transactional_sex());
        screeningModel.setAgwy_engaged_in_sex_work(indexVCA.getAgwy_engaged_in_sex_work());
        screeningModel.setAgyw_food_or_economically_insecure(indexVCA.getAgyw_food_or_economically_insecure());
        screeningModel.setAgyw_marry_early(indexVCA.getAgyw_marry_early());
        screeningModel.setAgyw_give_birth_before_the_age_of_18(indexVCA.getAgyw_give_birth_before_the_age_of_18());
        screeningModel.setAgyw_have_a_partner_who_is_violent_or_has_experienced_violence(indexVCA.getAgyw_have_a_partner_who_is_violent_or_has_experienced_violence());
        screeningModel.setAgyw_ever_been_diagnosed_with_a_Sexually_transmitted_illness(indexVCA.getAgyw_ever_been_diagnosed_with_a_Sexually_transmitted_illness());
        screeningModel.setAgyw_in_school(indexVCA.getAgyw_in_school());
        screeningModel.setAgyw_receiving_an_economic_strengthening_intervention(indexVCA.getAgyw_receiving_an_economic_strengthening_intervention());
        screeningModel.setChild_ever_experienced_sexual_violence(indexVCA.getChild_ever_experienced_sexual_violence());
        screeningModel.setChild_still_living_in_the_same_household_as_the_perpetrator(indexVCA.getChild_still_living_in_the_same_household_as_the_perpetrator());
        screeningModel.setChild_supported_to_seek_justice(indexVCA.getChild_supported_to_seek_justice());
        screeningModel.setDid_the_child_access_clinical_care(indexVCA.getDid_the_child_access_clinical_care());
        screeningModel.setChild_clinical_care_services_received(indexVCA.getChild_clinical_care_services_received());
        screeningModel.setChild_clinical_care_services_received_other(indexVCA.getChild_clinical_care_services_received_other());
        screeningModel.setOther_child_clinical_care_services_received(indexVCA.getOther_child_clinical_care_services_received());
        screeningModel.setIs_the_child_caregiver_an_fsw(indexVCA.getIs_the_child_caregiver_an_fsw());
        screeningModel.setFsw_child_tested(indexVCA.getFsw_child_tested());
        screeningModel.setFsw_child_positive(indexVCA.getFsw_child_positive());
        screeningModel.setFsw_prevention_intervention(indexVCA.getFsw_prevention_intervention());
        screeningModel.setFsw_economic_strengthening_intervention(indexVCA.getFsw_economic_strengthening_intervention());
        screeningModel.setDate_screened(indexVCA.getDate_screened());
        screeningModel.setApproved_by(indexVCA.getApproved_by());
        screeningModel.setConsent_check_box(indexVCA.getConsent_check_box());
        screeningModel.setSubpop1(indexVCA.getSubpop1());
        screeningModel.setSubpop2(indexVCA.getSubpop2());
        screeningModel.setSubpop3(indexVCA.getSubpop3());
        screeningModel.setSubpop4(indexVCA.getSubpop4());
        screeningModel.setSubpop5(indexVCA.getSubpop5());
        screeningModel.setScreening_location(indexVCA.getScreening_location());
        screeningModel.setSubpop(indexVCA.getSubpop());
        screeningModel.setFirst_name(indexVCA.getFirst_name());
        screeningModel.setLast_name(indexVCA.getLast_name());
        screeningModel.setGender(indexVCA.getGender());
        screeningModel.setBirthdate(indexVCA.getBirthdate());
        screeningModel.setIndex_check_box(indexVCA.getIndex_check_box());
        screeningModel.setCase_status(indexVCA.getCase_status());
        screeningModel.setDate_referred(indexVCA.getDate_referred());
        screeningModel.setDate_offered_enrollment(indexVCA.getDate_offered_enrollment());
        screeningModel.setAcceptance(indexVCA.getAcceptance());
        screeningModel.setDate_enrolled(indexVCA.getDate_enrolled());
        screeningModel.setDate_hiv_known(indexVCA.getDate_hiv_known());
        screeningModel.setArt_check_box(indexVCA.getArt_check_box());
        screeningModel.setDate_started_art(indexVCA.getDate_started_art());
        screeningModel.setVl_check_box(indexVCA.getVl_check_box());
        screeningModel.setDate_last_vl(indexVCA.getDate_last_vl());
        screeningModel.setVl_last_result(indexVCA.getVl_last_result());
//        screeningModel.setDate_next_vl(indexVCA.getDate_next_vl());
        screeningModel.setChild_mmd(indexVCA.getChild_mmd());
        screeningModel.setLevel_mmd(indexVCA.getLevel_mmd());
        screeningModel.setCaregiver_name(indexVCA.getCaregiver_name());
        screeningModel.setCaregiver_nrc(indexVCA.getCaregiver_nrc());
        screeningModel.setCaregiver_sex(indexVCA.getCaregiver_sex());
        screeningModel.setCaregiver_birth_date(indexVCA.getCaregiver_birth_date());
        screeningModel.setCaregiver_hiv_status(indexVCA.getCaregiver_hiv_status());
        screeningModel.setRelation(indexVCA.getRelation());
        screeningModel.setCaregiver_phone(indexVCA.getCaregiver_phone());
        screeningModel.setDe_registration_date(indexVCA.getDe_registration_date());
        screeningModel.setReason(indexVCA.getReason());
        screeningModel.setTransfer_reason(indexVCA.getTransfer_reason());
        screeningModel.setOther_reason(indexVCA.getOther_reason());
        screeningModel.setExited_graduation_reason(indexVCA.getExited_graduation_reason());
        screeningModel.setAbym_years(indexVCA.getAbym_years());
        screeningModel.setAbym_sexually_active(indexVCA.getAbym_sexually_active());
        screeningModel.setAbym_preventions(indexVCA.getAbym_preventions());
        screeningModel.setAbym_preventions_other(indexVCA.getAbym_preventions_other());
        screeningModel.setAbym_sex_older_women(indexVCA.getAbym_sex_older_women());
        screeningModel.setAbym_transactional_sex(indexVCA.getAbym_transactional_sex());
        screeningModel.setAbym_sex_work(indexVCA.getAbym_sex_work());
        screeningModel.setAbym_economically_insecure(indexVCA.getAbym_economically_insecure());
        screeningModel.setAbym_violent_partner(indexVCA.getAbym_violent_partner());
        screeningModel.setAbym_diagnosed(indexVCA.getAbym_diagnosed());
        screeningModel.setAbym_hiv_tested(indexVCA.getAbym_hiv_tested());
        screeningModel.setAbym_test_positive(indexVCA.getAbym_test_positive());
        screeningModel.setAbym_undergone_vmmc(indexVCA.getAbym_undergone_vmmc());
        screeningModel.setAbym_in_school(indexVCA.getAbym_in_school());
        screeningModel.setAbym_economic_strengthening(indexVCA.getAbym_economic_strengthening());
        screeningModel.setVca_receiving_caseworker(indexVCA.getVca_receiving_caseworker());
        screeningModel.setDistrict_moved_to(indexVCA.getDistrict_moved_to());
        screeningModel.setName_ovc(indexVCA.getName_ovc());
        return screeningModel;
    }
}
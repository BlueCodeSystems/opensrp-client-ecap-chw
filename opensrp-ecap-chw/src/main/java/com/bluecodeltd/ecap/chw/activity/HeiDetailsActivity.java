package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.chw.core.utils.CoreJsonFormUtils.getSyncHelper;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.text.TextUtils;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bluecodeltd.ecap.chw.dao.ChildMonitoringDao;
import com.google.android.material.tabs.TabLayoutMediator;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.adapter.ViewPagerAdapterFragment;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdServiceReportDao;

import com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao;
import com.bluecodeltd.ecap.chw.dao.PmtctChildDao;
import com.bluecodeltd.ecap.chw.dao.PmtctChildOutcomeDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.ChildCasePlanFragment;
import com.bluecodeltd.ecap.chw.fragment.ChildVisitsFragment;
import com.bluecodeltd.ecap.chw.fragment.PmctChildDbsMonitoringFragment;
import com.bluecodeltd.ecap.chw.fragment.PmctChildMonitoringFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.UnderFiveCardFragment;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.ChildMonitoringModel;
import com.bluecodeltd.ecap.chw.model.ChildRegisterModel;
import com.bluecodeltd.ecap.chw.model.HouseholdServiceReportModel;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentAbove15Model;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentUnder15Model;
import com.bluecodeltd.ecap.chw.model.PmtctChildModel;
import com.bluecodeltd.ecap.chw.model.PmtctChildOutcomeModel;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;
import com.bluecodeltd.ecap.chw.model.ReferralModel;
import com.bluecodeltd.ecap.chw.model.VCAModel;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;
import com.bluecodeltd.ecap.chw.model.VcaCasePlanModel;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import com.bluecodeltd.ecap.chw.model.WeServiceVcaModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.PmtctChildClientIndexUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;

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
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class HeiDetailsActivity extends AppCompatActivity {
    private com.bluecodeltd.ecap.chw.databinding.ActivityHeiDetailsBinding binding;

    @Override
    protected void onResume() {
        super.onResume();
    }

    private FloatingActionButton fab, fabHiv,fabHiv2, fabGradSub, fabGrad, fabCasePlan, fabVisitation, fabReferal,  fabAssessment;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    public String clientId, uniqueId, vcaAge,is_screened, is_hiv_positive, caseworkerphone;
    private RelativeLayout txtScreening, addIndexClients, rcase_plan, referral,  household_visitation_for_vca, hiv_assessment,hiv_assessment2,childPlan,weServicesVca;
    public VcaScreeningModel indexVCA;
    private VcaAssessmentModel assessmentModel;

    private TextView txtName, txtGender, txtAge, txtChildid;
    private TabLayout mTabLayout;
    public ViewPager2 mViewPager;
    private AppExecutors appExecutors;
    public ProfileViewPagerAdapter mPagerAdapter;
    private TextView htsCount,visitTabCount, plansTabCount, genderSpacer,ageSpacer;
    private AppBarLayout myAppbar;
    private Toolbar toolbar;
    private UniqueIdRepository uniqueIdRepository;
    public String gender;
    public  Button motherProfile;

    ObjectMapper oMapper, clientMapper;
    Child child;
    private TextView childTabCount;

    VcaAssessmentModel vcaAssessmentModel;
    ReferralModel referralModel;
    WeServiceVcaModel weServiceVcaModel;
    HivRiskAssessmentAbove15Model hivRiskAssessmentAbove15Model;
    HivRiskAssessmentUnder15Model hivRiskAssessmentUnder15Model;
    VcaVisitationModel vcaVisitationModel;
    VcaCasePlanModel vcaCasePlanModel;
    PmtctChildModel pmtctChild;
    PmtctChildOutcomeModel childOutcomeModel;
    ChildMonitoringModel childMonitoring;
    String full_name = "";
    String birthdate = "";

    public VCAModel client;
    AlertDialog.Builder builder, screeningBuilder;

    private ViewPager2 viewPager;
    TabLayout tabLayout;
    private TabLayoutMediator tabMediator;
    private Dialog dimDialog;

    @SuppressLint({"RestrictedApi", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.bluecodeltd.ecap.chw.databinding.ActivityHeiDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbarx;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        myAppbar = binding.collapsingToolbarAppbarlayout;
        NavigationMenu.getInstance(this, null, toolbar);

        motherProfile = binding.motherProfile;
        setupToolbarBackNavigation();

        builder = new AlertDialog.Builder(HeiDetailsActivity.this);

        fab = binding.fab;
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fabHiv = binding.hivRisk;
        fabHiv2 = binding.hivRisk2;
        fabVisitation = binding.householdVisitationForVcaFab;
        fabReferal = binding.referToFacilityFab;
        fabCasePlan =  binding.casePlanFab;
        fabAssessment = binding.fabAssessment;
        txtScreening = binding.vcaScreening;
        addIndexClients = binding.assessment;
//        builder = new AlertDialog.Builder(HTSDetailsActivity.this);
//        screeningBuilder = new AlertDialog.Builder(HTSDetailsActivity.this);
//
        clientId = getIntent().getExtras().getString("client_id");
//        pmtctChild = PmtctChildDao.getPmctChildHei(clientId);
        try {
            pmtctChild = PmtctChildDao.getPMCTChild(clientId);
        } catch (Exception e) {
            Timber.e(e);
        }
        if (pmtctChild == null) {
            Toasty.warning(HeiDetailsActivity.this, "Infant record not found", Toast.LENGTH_LONG, true).show();
            finish();
            return;
        }
        childMonitoring = ChildMonitoringDao.getPMCTChildMonitoring(clientId);
        childOutcomeModel = PmtctChildOutcomeDao.getPMCTChildOutcome(clientId);

        txtName = binding.vcaName;
        txtGender = binding.vcaGender;
        txtAge = binding.vcaAge;
        txtChildid = binding.childid;
        genderSpacer = binding.genderSpacer;
        ageSpacer = binding.ageSpacer;
//        try {
//            if(hivTestingServiceModel.getTesting_modality() !=null && hivTestingServiceModel.getTesting_modality().equals("Other Community")){
        txtAge.setVisibility(View.GONE);
        txtGender.setVisibility(View.GONE);
        genderSpacer.setVisibility(View.GONE);
        ageSpacer.setVisibility(View.GONE);
//            }
//        } catch (NullPointerException e) {
//            Log.e("YourActivityName", "NullPointerException", e);
//        }

//        try {
//            if (hivTestingServiceModel != null && hivTestingServiceModel.getGender() != null) {
//                txtGender.setText(hivTestingServiceModel.getGender().toUpperCase());
//            }
//        } catch (Exception e) {
//            Log.e("YourActivityName", "Exception", e);
//        }


        try {
            if (pmtctChild != null && pmtctChild.getInfant_first_name() != null && pmtctChild.getInfant_lastname() != null) {
                String full_name = pmtctChild.getInfant_first_name() + " " + pmtctChild.getInfant_lastname();
                txtName.setText(full_name);
            }
        } catch (Exception e) {
            Log.e("YourActivityName", "Exception", e);
        }

        try {
            if(pmtctChild != null && pmtctChild.getInfants_date_of_birth() != null && !pmtctChild.getInfants_date_of_birth().isEmpty()){
                txtAge.setText("AGE: "+getAge(pmtctChild.getInfants_date_of_birth()));
            } else {
                txtAge.setText("Not Set");
            }
        } catch (Exception e) {
            Log.e("YourActivityName", "Exception", e);
        }

        if (clientId != null && !clientId.isEmpty()){
            txtChildid.setText("ID : " + clientId);
        }

        oMapper = new ObjectMapper();
        clientMapper = new ObjectMapper();


        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
//        updateTasksTabTitle();
        returnViewPager();
        updateOverviewTabTitle();
        updateAncTabTitle();
        updateDbsTabTitle();


        updateMotherProfileButton();
        refreshProfileFlags();
    }

    private void setupToolbarBackNavigation() {
        try {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);

                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
                upArrow.setColorFilter(getResources().getColor(org.smartregister.R.color.white), PorterDuff.Mode.SRC_ATOP);
                actionBar.setHomeAsUpIndicator(upArrow);
                actionBar.setElevation(0);
            }

            toolbar.setNavigationOnClickListener(v -> navigateUpToMotherProfile());
        } catch (Exception ignored) { }
    }

    private void updateMotherProfileButton() {
        if (motherProfile == null) return;

        final String pmtctId = getLinkedMotherPmtctId();

        PtctMotherModel mother = null;
        try {
            if (!TextUtils.isEmpty(pmtctId)) {
                mother = PMTCTMotherDao.getPMCTMother(pmtctId);
            }
        } catch (Exception ignored) { }

        boolean hide = false;
        if (mother != null && !TextUtils.isEmpty(mother.getSource_from())) {
            hide = "service_report_vca".equalsIgnoreCase(mother.getSource_from().trim());
        }

        motherProfile.setVisibility(hide ? View.GONE : View.VISIBLE);
        if (hide) return;

        motherProfile.setOnClickListener(v -> {
            if (TextUtils.isEmpty(pmtctId)) {
                Toasty.warning(HeiDetailsActivity.this, "Mother record not available", Toast.LENGTH_LONG, true).show();
                return;
            }
            Intent intent = new Intent(this, MotherPmtctProfileActivity.class);
            intent.putExtra("client_id", pmtctId);
            startActivity(intent);
        });
    }

    private String getLinkedMotherPmtctId() {
        try {
            if (pmtctChild != null) return pmtctChild.getPmtct_id();
        } catch (Exception ignored) { }
        return null;
    }

    private void navigateUpToMotherProfile() {
        if (!isTaskRoot()) {
            finish();
            return;
        }

        String pmtctId = getLinkedMotherPmtctId();

        String householdId = null;
        String sourceFrom = null;
        try {
            if (!TextUtils.isEmpty(pmtctId)) {
                PtctMotherModel mother = PMTCTMotherDao.getPMCTMother(pmtctId);
                if (mother != null) {
                    householdId = mother.getHousehold_id();
                    sourceFrom = mother.getSource_from();
                }
            }
        } catch (Exception ignored) { }

        Intent intent = new Intent(this, MotherPmtctProfileActivity.class);
        intent.putExtra("client_id", pmtctId);
        if (!TextUtils.isEmpty(householdId)) intent.putExtra("household_id", householdId);
        if (!TextUtils.isEmpty(sourceFrom)) intent.putExtra("source_from", sourceFrom);
        startActivity(intent);
        finish();
    }

    public void animateFAB(){


        if (isFabOpen){

            closeFab();
        } else {

            isFabOpen = true;
            fab.startAnimation(rotate_forward);
            txtScreening.setVisibility(View.VISIBLE);
//            if(hivTestingServiceModel.getTesting_modality() != null && (hivTestingServiceModel.getTesting_modality().equals("SNT") || hivTestingServiceModel.getTesting_modality().equals("Index"))){
            addIndexClients.setVisibility(View.VISIBLE);
//            }

        }

    }

    public void closeFab(){
        fab.startAnimation(rotate_backward);
        isFabOpen = false;
        txtScreening.setVisibility(View.GONE);
        addIndexClients.setVisibility(View.GONE);
//        rcase_plan.setVisibility(View.GONE);
//        referral.setVisibility(View.GONE);
//        household_visitation_for_vca.setVisibility(View.GONE);
//        hiv_assessment.setVisibility(View.GONE);
//        hiv_assessment2.setVisibility(View.GONE);
//        childPlan.setVisibility(View.GONE);
//        weServicesVca.setVisibility(View.GONE);


    }

    public HashMap<String, Child> getData() {
        if (indexVCA == null) {
            Timber.w("HeiDetailsActivity indexVCA data missing");
            return new HashMap<>();
        }
        String full_name = indexVCA.getFirst_name() + " " + indexVCA.getLast_name();
        String gender =  indexVCA.getGender();
        String birthdate = indexVCA.getAdolescent_birthdate();

        if(birthdate != null){
            txtAge.setText(getAge(birthdate));
            vcaAge = getAgeWithoutText(birthdate);

        } else {
            txtAge.setText("Not Set");
        }


        txtGender.setText(gender.toUpperCase());
        txtChildid.setText("ID : " + indexVCA.getUnique_id());

        HashMap<String, Child> map = new HashMap<>();

        map.put("Child",child);

        return map;

    }
    public HashMap<String, PmtctChildModel> getLinkID() {

        HashMap<String, PmtctChildModel> map = new HashMap<>();

        map.put("client", pmtctChild);

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

    private void setupViewPager(){ }

    private void updateVisitsTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText(this.getString(org.smartregister.opd.R.string.visits));
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
        form.setName(getString(org.smartregister.chw.core.R.string.child_details));
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

                animateFAB();
                break;

            case R.id.vca_screening:

                try {

                    openFormUsingFormUtils(HeiDetailsActivity.this,"pmct_child_hei");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;


            case R.id.assessment:
                if (shouldBlockMonitoringService()) {
                    Toasty.warning(HeiDetailsActivity.this, "Add the 24-month final outcome test result before adding another service.", Toast.LENGTH_LONG, true).show();
                    break;
                }
                try {
                    openFormUsingFormUtils(HeiDetailsActivity.this,"pmtct_child_monitoring");
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

            final JSONObject jsonFormObject;
            try {
                jsonFormObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            String encounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");

            if(!jsonFormObject.optString("entity_id").isEmpty()){
                is_edit_mode = true;
            }

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                Runnable postSave = () -> {
                    switch (encounterType) {
                        case "VCA Case Plan":

                            try {
                                JSONObject cpdate = getFieldJSONObject(fields(jsonFormObject, "step1"), "case_plan_date");
                                String dateId = cpdate != null ? cpdate.optString("value") : "";
                                refreshActivity();
                                openVcaCasplanToAddVulnarabilities(dateId);
                            } catch (Exception e) {
                                Timber.e(e);
                                refreshActivity();
                            }

                            break;

                        case "Household Visitation Form 0-20 years":
                        case "Member Sub Population":
                        case "Sub Population":
                        case "VCA Assessment":
                        case "HIV Risk Assessment Above 15":
                        case "HIV Risk Assessment Below 15":
                        case "Ptmct Child Monitoring":
                        case "Mother Pmtct Child":

                            refreshActivity();

                            break;
                        case "Case Record Status":

                            refreshActivity();
                            Intent i = new Intent(getApplicationContext(), IndexRegisterActivity.class);
                            startActivity(i);

                            break;

                        default:
                            refreshActivity();
                            break;

                    }

                    Toasty.success(HeiDetailsActivity.this, "Form Saved", Toast.LENGTH_LONG, true).show();
                };

                boolean scheduled = saveRegistration(childIndexEventClient, is_edit_mode, postSave);
                if (!scheduled) {
                    postSave.run();
                }

            } catch (Exception e) {
                Timber.e(e);
            }

        }

    }

    private void openVcaCasplanToAddVulnarabilities(String dateId) {
        Intent i = new Intent(HeiDetailsActivity.this, CasePlan.class);
        i.putExtra("childId", indexVCA.getUnique_id());
        i.putExtra("dateId",  dateId);
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
                client = new VCAModel(personObject.getCaseId(), personObject.getDetails(), "");
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

                case "Mother Pmtct Child":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_child");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Ptmct Child Monitoring":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_child_monitoring");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "Ptmct Child Outcome":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_child_outcome");
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

    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode, Runnable onComplete) {

        Runnable runnable = () -> {

            Event event = childIndexEventClient.getEvent();
            Client client = childIndexEventClient.getClient();

            try {
                if (event != null && client != null) {
                    try {
                        ECSyncHelper ecSyncHelper = getECSyncHelper();

                        JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));

                        JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());

                        if (isEditMode && existingClientJsonObject != null) {
                            JSONObject mergedClientJsonObject =
                                    org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
                            PmtctChildClientIndexUtils.mirrorClientIndexAttributes(mergedClientJsonObject, event);
                            ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);

                        } else {
                            PmtctChildClientIndexUtils.mirrorClientIndexAttributes(newClientJsonObject, event);
                            ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);
                        }

                        JSONObject eventJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));
                        ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);

                        Long lastUpdatedAtDate = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                        Date currentSyncDate = new Date(lastUpdatedAtDate);

                        List<EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                        if (savedEvents == null || savedEvents.isEmpty()) {
                            savedEvents = ecSyncHelper.getEvents(currentSyncDate, BaseRepository.TYPE_Unprocessed);
                        }
                        if (savedEvents != null && !savedEvents.isEmpty()) {
                            getClientProcessorForJava().processClient(savedEvents);
                        } else {
                            Timber.w("No saved events found for PMTCT monitoring form %s / %s",
                                    event.getFormSubmissionId(), event.getBaseEntityId());
                        }
                        getAllSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());


                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            } finally {
                if (onComplete != null) {
                    runOnUiThread(onComplete);
                }
            }

        };

        try {
            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(runnable);
            return true;
        } catch (Exception exception) {
            Timber.e(exception);
            if (onComplete != null) {
                runOnUiThread(onComplete);
            }
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
                Intent i = new Intent(HeiDetailsActivity.this, IndexRegisterActivity.class);
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

    private void refreshActivity() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        Intent intent = new Intent(getIntent());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
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
//        formToBeOpened.getJSONObject("step1").put("title", hivTestingServiceModel.getFirst_name() != null ? hivTestingServiceModel.getFirst_name() : "" + " " + hivTestingServiceModel.getLast_name() != null ? hivTestingServiceModel.getLast_name() : "" + " : " + txtAge.getText().toString() + " - " + txtGender.getText().toString());
        // formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("value", .getUnique_id());

        switch (formName) {

            //case "case_status":
            case "vca_screening":

                // CoreJsonFormUtils.populateJsonForm(formToBeOpened));
                //Populate Caseworker Name
                //populateCaseworkerPhoneAndName(formToBeOpened);


//                formToBeOpened.put("entity_id", this.hivTestingServiceModel.getBase_entity_id());
//                startFormActivity(formToBeOpened);

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

                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(pmtctChild, Map.class));
//                formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", vcaAge);

             /*   if(vcaCasePlanModel == null){

                } else {
                    formToBeOpened.put("entity_id", this.vcaCasePlanModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vcaCasePlanModel, Map.class));

                }*/

                break;

            case "hiv_risk_assessment_under_15_years":

                if(hivRiskAssessmentUnder15Model == null){

                    //Pulls data for populating from indexchild when adding data for the very first time
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));

                } else {

                    formToBeOpened.put("entity_id", this.hivRiskAssessmentUnder15Model.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(hivRiskAssessmentUnder15Model, Map.class));
                }

                break;

            case "hiv_risk_assessment_above_15_years":

                if(hivRiskAssessmentAbove15Model == null){

                    //Pulls data for populating from indexchild when adding data for the very first time
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));

                } else {

                    formToBeOpened.put("entity_id", this.hivRiskAssessmentAbove15Model.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(hivRiskAssessmentAbove15Model, Map.class));
                }

                break;

            case "child_safety_plan":
            case "referral":
                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(indexVCA, Map.class));
                populateCaseworkerPhoneAndName(formToBeOpened);
                break;
            case "pmct_child_hei":

                formToBeOpened.put("entity_id", this.pmtctChild.getBase_entity_id());
                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(pmtctChild, Map.class));
                populateCaseworkerPhoneAndName(formToBeOpened);
                populateProgramInfoFromSharedPreferences(formToBeOpened);
                populateLatestMotherVl(formToBeOpened);
                JSONObject dateEdited = getFieldJSONObject(fields(formToBeOpened, "step1"),"date_edited");
                if (dateEdited  != null) {
                    dateEdited.remove(JsonFormUtils.VALUE);
                    try {
                        dateEdited.put(JsonFormUtils.VALUE, getFormattedDate());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "pmtct_child_monitoring":
                PmtctChildModel modifiedChildModel = new PmtctChildModel();
                modifiedChildModel.setUnique_id(pmtctChild.getUnique_id());
                modifiedChildModel.setPmtct_id(pmtctChild.getPmtct_id());
//                if(childMonitoring == null){
                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(modifiedChildModel, Map.class));
                populateHivStatus(formToBeOpened,pmtctChild.getInfants_date_of_birth());


                break;

            case "pmtct_child_outcome":
                if(childOutcomeModel == null){
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(pmtctChild, Map.class));
                } else{
                    formToBeOpened.put("entity_id", this.childOutcomeModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(childOutcomeModel, Map.class));

                }

                break;


        }

        startFormActivity(formToBeOpened);
    }
    private void refreshProfileFlags() {
        updateDbsDueFlag();
        updateFinalOutcomeFlag();
        updateMotherVlFlag();
    }

    private void updateDbsDueFlag() {
        Button flag = findViewById(R.id.hei_dbs_due_flag);
        if (flag == null) return;
        String message = buildUpcomingDbsDueMessage();
        if (TextUtils.isEmpty(message)) {
            flag.setVisibility(View.GONE);
            return;
        }
        flag.setText(message);
        flag.setVisibility(View.VISIBLE);
    }

    private void updateFinalOutcomeFlag() {
        Button flag = findViewById(R.id.hei_outcome_due_flag);
        if (flag == null) return;
        if (!isFinalOutcomeDue()) {
            flag.setVisibility(View.GONE);
            return;
        }
        flag.setText("24m outcome due");
        flag.setVisibility(View.VISIBLE);
    }

    private void updateMotherVlFlag() {
        Button flag = findViewById(R.id.hei_mother_vl_flag);
        View alert = findViewById(R.id.hei_unsuppressed_alert);
        MotherVlSummary summary = resolveLatestMotherVlSummary();
        boolean unsuppressed = summary != null && summary.unsuppressed;

        if (flag != null) {
            if (unsuppressed) {
                flag.setText("Mother VL unsuppressed");
                flag.setVisibility(View.VISIBLE);
            } else {
                flag.setVisibility(View.GONE);
            }
        }

        if (alert != null) {
            if (unsuppressed) {
                alert.setVisibility(View.VISIBLE);
                TextView title = alert.findViewById(R.id.tv_title);
                TextView actions = alert.findViewById(R.id.tv_actions_list);
                TextView chip = alert.findViewById(R.id.chip_flagged);
                if (title != null) {
                    title.setText("Mother latest VL is unsuppressed");
                }
                if (chip != null) {
                    chip.setText("FLAGGED");
                }
                if (actions != null) {
                    String result = TextUtils.isEmpty(summary.result) ? "Unknown" : summary.result;
                    String date = TextUtils.isEmpty(summary.date) ? "Unknown date" : summary.date;
                    actions.setText("Latest VL: " + result + " on " + date + "\nReview adherence and repeat testing.");
                }
            } else {
                alert.setVisibility(View.GONE);
            }
        }
    }

    private boolean shouldBlockMonitoringService() {
        return isFinalOutcomeDue();
    }

    private boolean isFinalOutcomeDue() {
        int ageMonths = getChildAgeInMonths();
        if (ageMonths < 24) {
            return false;
        }
        return childOutcomeModel == null || TextUtils.isEmpty(childOutcomeModel.getChild_outcome());
    }

    private String buildUpcomingDbsDueMessage() {
        List<ChildMonitoringModel> dbsVisits = getDbsMonitoringVisits();
        if (dbsVisits.isEmpty()) {
            return null;
        }

        LocalDate firstMonitoringDate = parseMonitoringDate(dbsVisits.get(0).getDate());
        if (firstMonitoringDate == null) {
            return null;
        }

        LocalDate dueDate = resolveNextDbsDueDate(firstMonitoringDate, dbsVisits.size());
        if (dueDate == null) {
            return null;
        }

        long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        String fullDate = dueDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy"));
        if (daysUntilDue <= 0) {
            return "DBS overdue since " + fullDate;
        }
        if (daysUntilDue <= 14) {
            return "DBS due in " + daysUntilDue + " days (" + fullDate + ")";
        }
        if (daysUntilDue <= 30) {
            long weeks = Math.max(1, (long) Math.ceil(daysUntilDue / 7.0));
            return "DBS due in " + weeks + " weeks (" + fullDate + ")";
        }
        return "DBS due on " + fullDate;
    }

    private LocalDate resolveNextDbsDueDate(LocalDate firstMonitoringDate, int completedDbsVisits) {
        switch (completedDbsVisits) {
            case 1:
                return firstMonitoringDate.plusMonths(6);
            case 2:
                return firstMonitoringDate.plusMonths(9);
            case 3:
                return firstMonitoringDate.plusMonths(12);
            case 4:
                return firstMonitoringDate.plusMonths(18);
            case 5:
                return firstMonitoringDate.plusMonths(24);
            default:
                return null;
        }
    }

    private LocalDate parseMonitoringDate(String dateValue) {
        return parseChildDate(dateValue);
    }

    private List<ChildMonitoringModel> getDbsMonitoringVisits() {
        List<ChildMonitoringModel> visits = ChildMonitoringDao.getPmctChildMonitoringListDBS(pmtctChild.getUnique_id());
        if (visits == null || visits.isEmpty()) {
            return new ArrayList<>();
        }
        visits.sort((left, right) -> {
            LocalDate leftDate = parseMonitoringDate(left.getDate());
            LocalDate rightDate = parseMonitoringDate(right.getDate());
            if (leftDate == null && rightDate == null) return 0;
            if (leftDate == null) return 1;
            if (rightDate == null) return -1;
            return leftDate.compareTo(rightDate);
        });
        return visits;
    }

    private LocalDate parseChildBirthDate() {
        if (pmtctChild == null || TextUtils.isEmpty(pmtctChild.getInfants_date_of_birth())) {
            return null;
        }
        return parseChildDate(pmtctChild.getInfants_date_of_birth());
    }

    private LocalDate parseChildDate(String dateValue) {
        if (TextUtils.isEmpty(dateValue)) {
            return null;
        }
        List<DateTimeFormatter> formatters = new ArrayList<>();
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-u"));
        formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateValue.trim(), formatter);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private int getChildAgeInMonths() {
        LocalDate birthDate = parseChildBirthDate();
        if (birthDate == null) {
            return 0;
        }
        Period period = Period.between(birthDate, LocalDate.now());
        return period.getYears() * 12 + period.getMonths();
    }

    private int safeParseInt(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String getNextDbsDueLabel() {
        String message = buildUpcomingDbsDueMessage();
        return TextUtils.isEmpty(message) ? "" : message;
    }

    public String getDbsDueMessage() {
        return getNextDbsDueLabel();
    }

    private void populateLatestMotherVl(JSONObject formToBeOpened) {
        MotherVlSummary summary = resolveLatestMotherVlSummary();
        if (summary == null) {
            return;
        }
        setFormFieldValue(formToBeOpened, "date_last_vl", summary.date);
        setFormFieldValue(formToBeOpened, "vl_last_result", summary.result);
    }

    private MotherVlSummary resolveLatestMotherVlSummary() {
        String householdId = null;
        if (pmtctChild != null) {
            householdId = pmtctChild.getHousehold_id();
            if (TextUtils.isEmpty(householdId)) {
                try {
                    PtctMotherModel mother = PMTCTMotherDao.getPMCTMother(pmtctChild.getPmtct_id());
                    if (mother != null) {
                        householdId = mother.getHousehold_id();
                    }
                } catch (Exception ignored) {
                }
            }
        }

        if (!TextUtils.isEmpty(householdId)) {
            try {
                HouseholdServiceReportModel latestService = HouseholdServiceReportDao.getLatestVLSummaryByHousehold(householdId);
                if (latestService != null && !TextUtils.isEmpty(latestService.getVl_last_result())) {
                    return buildMotherVlSummary(latestService.getDate(), latestService.getVl_last_result());
                }
            } catch (Exception ignored) {
            }
        }

        try {
            if (pmtctChild != null && !TextUtils.isEmpty(pmtctChild.getPmtct_id())) {
                PtctMotherModel mother = PMTCTMotherDao.getPMCTMother(pmtctChild.getPmtct_id());
                if (mother != null) {
                    return buildMotherVlSummaryFromMother(mother);
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private MotherVlSummary buildMotherVlSummary(String date, String result) {
        MotherVlSummary summary = new MotherVlSummary();
        summary.date = date;
        summary.result = result;
        summary.unsuppressed = isUnsuppressedVl(result);
        return summary;
    }

    private MotherVlSummary buildMotherVlSummaryFromMother(PtctMotherModel mother) {
        String result = firstNonEmpty(mother == null ? null : mother.getVl_result_3rd_trimester(),
                mother == null ? null : mother.getVl_result_2nd_trimester(),
                mother == null ? null : mother.getVl_result_1st_trimester());
        String date = firstNonEmpty(mother == null ? null : mother.getAgyw_date_1st_visit(),
                mother == null ? null : mother.getDate_1st_visit(),
                mother == null ? null : mother.getDate_enrolled_pmtct());
        MotherVlSummary summary = new MotherVlSummary();
        summary.date = date;
        summary.result = result;
        summary.unsuppressed = isUnsuppressedVl(result)
                || isYes(mother == null ? null : mother.getUnsuppressed_vl_1st())
                || isYes(mother == null ? null : mother.getUnsuppressed_vl_2nd())
                || isYes(mother == null ? null : mother.getUnsuppressed_vl_3rd());
        return summary;
    }

    private boolean isUnsuppressedVl(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.US);
        if ("unsuppressed".equals(normalized) || "yes".equals(normalized) || "positive".equals(normalized)) {
            return true;
        }
        try {
            return Integer.parseInt(normalized) >= 1000;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean isYes(String value) {
        return value != null && "yes".equalsIgnoreCase(value.trim());
    }

    private String firstNonEmpty(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        }
        return null;
    }

    private void setFormFieldValue(JSONObject formToBeOpened, String key, String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        try {
            JSONObject field = getFieldJSONObject(fields(formToBeOpened, "step1"), key);
            if (field != null) {
                field.put(JsonFormUtils.VALUE, value);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private static class MotherVlSummary {
        String date;
        String result;
        boolean unsuppressed;
    }
    private String getFormattedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return today.format(formatter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pmtct_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateUpToMotherProfile();
                return true;
            case R.id.refresh:
                finish();
                startActivity(getIntent());

                break;
            case R.id.case_status:

                try {
                    openFormUsingFormUtils(HeiDetailsActivity.this, "pmtct_child_outcome");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;

            case R.id.delete_record:

                builder.setMessage("You are about to delete this record");
                builder.setNegativeButton("NO", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();

                }).setPositiveButton("YES", ((dialogInterface, i) -> {
                    FormUtils formUtils = null;
                    try {
                        formUtils = new FormUtils(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pmtctChild.setDelete_status("1");
                    JSONObject openForm = formUtils.getFormJson("pmct_child_hei");
                    try {
                        CoreJsonFormUtils.populateJsonForm(openForm, new ObjectMapper().convertValue(pmtctChild, Map.class));
                        openForm.put("entity_id", pmtctChild.getBase_entity_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {

                        ChildIndexEventClient childIndexEventClient = processRegistration(openForm.toString());
                        if (childIndexEventClient == null) {
                            return;
                        }
                        Runnable onComplete = () -> {
                            Toasty.success(HeiDetailsActivity.this, "Deleted", Toast.LENGTH_LONG, true).show();
                            Intent intent = new Intent(this, MotherPmtctProfileActivity.class);
                            intent.putExtra("client_id",  pmtctChild.getPmtct_id());
                            startActivity(intent);
                            this.finish();
                        };
                        boolean scheduled = saveRegistration(childIndexEventClient, true, onComplete);
                        if (!scheduled) {
                            onComplete.run();
                        }


                    } catch (Exception e) {
                        Timber.e(e);
                    }


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
                        openFormUsingFormUtils(HeiDetailsActivity.this, "vca_screening");
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
        SharedPreferences cp = PreferenceManager.getDefaultSharedPreferences(HeiDetailsActivity.this);
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

    private void populateProgramInfoFromSharedPreferences(JSONObject formToBeOpened) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HeiDetailsActivity.this);

        String province = prefs.getString("province", "");
        String district = prefs.getString("district", "");
        String ward = prefs.getString("ward", "");
        String facility = prefs.getString("facility", "");
        String partner = prefs.getString("partner", "");

        setStep1FieldValue(formToBeOpened, "province", province);
        setStep1FieldValue(formToBeOpened, "district", district);
        setStep1FieldValue(formToBeOpened, "ward", ward);
        setStep1FieldValue(formToBeOpened, "facility", facility);
        setStep1FieldValue(formToBeOpened, "partner", partner);
    }

    private void setStep1FieldValue(JSONObject formToBeOpened, String key, String value) {
        if (android.text.TextUtils.isEmpty(value)) return;
        JSONObject field = getFieldJSONObject(fields(formToBeOpened, "step1"), key);
        if (field == null) return;
        field.remove(JsonFormUtils.VALUE);
        try {
            field.put(JsonFormUtils.VALUE, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void populateHivStatus(JSONObject formToBeOpened, String dateString) {
        // Parse the input date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            Date inputDate = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(inputDate);

            Calendar oneYearAgo = Calendar.getInstance();
            oneYearAgo.add(Calendar.YEAR, -1);

            JSONObject hivStatus = getFieldJSONObject(fields(formToBeOpened, "step1"), "hiv_test");

            if (hivStatus != null) {
                JSONArray valuesArray = new JSONArray();

                if (calendar.before(oneYearAgo)) {
                    valuesArray.put("R");
                    valuesArray.put("NR");
                } else {
                    valuesArray.put("ND");
                    valuesArray.put("D");
                }
                hivStatus.put("values", valuesArray);
                hivStatus.put("keys", valuesArray);
            }
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
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
    public  void returnViewPager(){
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new UnderFiveCardFragment());
        fragments.add(new PmctChildMonitoringFragment());
        fragments.add(new PmctChildDbsMonitoringFragment());

        com.bluecodeltd.ecap.chw.adapter.ViewPager2Adapter adapter = new com.bluecodeltd.ecap.chw.adapter.ViewPager2Adapter(this, fragments);
        viewPager.setAdapter(adapter);
        // Attach TabLayoutMediator for titles
        if (tabMediator != null) { try { tabMediator.detach(); } catch (Exception ignored) {} }
        tabMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Overview");
            else if (position == 1) tab.setText("Monitoring");
            else if (position == 2) tab.setText("DBS MONITORING");
        });
        tabMediator.attach();
    }

    private void updateOverviewTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.pmct_titles, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("OVERVIEW");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);
        childTabCount.setVisibility(View.GONE);

        tabLayout.getTabAt(0).setCustomView(taskTabTitleLayout);
    }
    private void updateAncTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.pmct_titles, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("MONITORING");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);


        String countANC = ChildMonitoringDao.countChildMonitoring(pmtctChild.getUnique_id());
        childTabCount.setText(countANC);

        tabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }

    private void updateDbsTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.pmct_titles, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("DBS MONITORING");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);


        String countANC = ChildMonitoringDao.countDBSChildMonitoring(pmtctChild.getUnique_id());
        childTabCount.setText(countANC);
        tabLayout.getTabAt(2).setCustomView(taskTabTitleLayout);
    }

    public HashMap<String, PmtctChildModel> getClientDetails() {

        HashMap<String, PmtctChildModel> map = new HashMap<>();

        map.put("client", pmtctChild);

        return map;

    }

    public void buildDialog(){
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("CA Screening");
        alert.show();
    }
    protected void goToMotherDetailActivity(String clientId) {

        Intent intent = new Intent(this, MotherPmtctProfileActivity.class);
        intent.putExtra("client_id",  clientId);
        startActivity(intent);
        this.finish();
    }
    @Override
    public void onBackPressed() {
        navigateUpToMotherProfile();

    }
}








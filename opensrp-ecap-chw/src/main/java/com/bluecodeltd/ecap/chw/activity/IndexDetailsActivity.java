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
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bluecodeltd.ecap.chw.fragment.VcaNutritionAssessmentFragment;
import com.google.android.material.tabs.TabLayoutMediator;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.GraduationDao;
import com.bluecodeltd.ecap.chw.dao.HivAssessmentAbove15Dao;
import com.bluecodeltd.ecap.chw.dao.HivAssessmentUnder15Dao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexMotherDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao;
import com.bluecodeltd.ecap.chw.dao.PmtctChildDao;
import com.bluecodeltd.ecap.chw.dao.ReferralDao;
import com.bluecodeltd.ecap.chw.dao.NutritionAssessmentInterventionDao;
import com.bluecodeltd.ecap.chw.dao.VCAScreeningDao;
import com.bluecodeltd.ecap.chw.dao.VcaAssessmentDao;
import com.bluecodeltd.ecap.chw.dao.VcaCasePlanDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.dao.WeServiceVcaDao;
import com.bluecodeltd.ecap.chw.dao.TbScreeningDao;
import com.bluecodeltd.ecap.chw.dao.CaregiverDao;
import com.bluecodeltd.ecap.chw.dao.newCaregiverDao;
import com.bluecodeltd.ecap.chw.model.Caregiver;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.ChildCasePlanFragment;
import com.bluecodeltd.ecap.chw.fragment.ChildVisitsFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.VcaHivAssesmentFragment;
import com.bluecodeltd.ecap.chw.fragment.VcaTbScreeningFragment;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.ChildRegisterModel;
import com.bluecodeltd.ecap.chw.model.GraduationModel;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentAbove15Model;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentUnder15Model;
import com.bluecodeltd.ecap.chw.model.IndexMotherModel;
import com.bluecodeltd.ecap.chw.model.PmtctChildModel;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;
import com.bluecodeltd.ecap.chw.model.ReferralModel;
import com.bluecodeltd.ecap.chw.model.EcClientIndexSummary;
import com.bluecodeltd.ecap.chw.model.VCAModel;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;
import com.bluecodeltd.ecap.chw.model.VcaCasePlanModel;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import com.bluecodeltd.ecap.chw.model.WeServiceVcaModel;
import com.bluecodeltd.ecap.chw.model.newCaregiverModel;
import com.bluecodeltd.ecap.chw.model.TbScreeningModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Threading;
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
    private com.bluecodeltd.ecap.chw.databinding.VcaContentBinding binding;
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

    private FloatingActionButton fab, fabHiv,fabHiv2, fabGradSub, fabGrad, fabCasePlan, fabVisitation, fabReferal,  fabAssessment;
    private View fabScrim;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    public String childId, uniqueId, vcaAge,is_screened, is_hiv_positive, caseworkerphone;
    private String refresh;
    private RelativeLayout txtScreening, rassessment, rcase_plan, referral,  household_visitation_for_vca, hiv_assessment,hiv_assessment2,childPlan,weServicesVca, nutrition_assessment_intervention, tb_screening;

    public VcaScreeningModel indexVCA;
    private Caregiver householdCaregiver;
    private  VcaAssessmentModel assessmentModel;
    private TextView txtName, txtGender, txtAge, txtChildid;
    private android.widget.ImageView profileGenderImage;
    private View profileGenderAvatar;
    private View profileNameLayout;
    private TabLayout mTabLayout;
    public ViewPager2 mViewPager;
    private TabLayoutMediator tabMediator;
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
    private boolean hideHouseholdProfileButton;
    private boolean allowNonScreeningFabActionsWhenSourceIsVcaScreening;
    private volatile boolean sourceFromVcaScreeningResolved;


    public VCAModel client;
    AlertDialog.Builder builder, screeningBuilder;

    Random number;
    int rNumber;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.bluecodeltd.ecap.chw.databinding.VcaContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbarx;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        myAppbar = binding.collapsingToolbarAppbarlayout;
        NavigationMenu.getInstance(this, null, toolbar);

        builder = new AlertDialog.Builder(IndexDetailsActivity.this);
        screeningBuilder = new AlertDialog.Builder(IndexDetailsActivity.this);

        // Refresh flag (optional extra)
        try {
            refresh = getIntent() != null && getIntent().getExtras() != null ? getIntent().getExtras().getString("refresh") : null;
        } catch (Exception ignored) {}

        try {
            childId = getIntent() != null && getIntent().getExtras() != null ? getIntent().getExtras().getString("Child") : null;
            if (childId == null && getIntent() != null && getIntent().getExtras() != null) {
                childId = getIntent().getExtras().getString("childId");
            }
            if (childId == null && getIntent() != null && getIntent().getExtras() != null) {
                childId = getIntent().getExtras().getString("base_entity_id");
            }
        } catch (Exception ignored) {}

        Bundle extras = getIntent() != null ? getIntent().getExtras() : null;
        String hhIntent = extras != null ? extras.getString("fromHousehold") : null;
        if(hhIntent == null && extras != null){
            hhIntent = extras.getString("fromIndex");
        }
        // Avoid ANRs: do SQLCipher/DAO work off the main thread.
        loadIndexDataAsync(hhIntent);

    }

    private void loadIndexDataAsync(String hhIntent) {
        final String finalChildId = childId;
        final String finalHhIntent = hhIntent;
        Threading.io(() -> {
            if (TextUtils.isEmpty(finalChildId)) {
                Threading.main(this::finish);
                return;
            }

            try { indexVCA = VCAScreeningDao.getVcaScreening(finalChildId); } catch (Exception ignored) { indexVCA = null; }
            try { child = IndexPersonDao.getChildByBaseId(finalChildId); } catch (Exception ignored) { child = null; }

            hideHouseholdProfileButton = false;
            allowNonScreeningFabActionsWhenSourceIsVcaScreening = false;
            sourceFromVcaScreeningResolved = false;
            try {
                String householdId = resolveHouseholdIdForSourceChecks();
                if (!TextUtils.isEmpty(householdId)) {
                    /* First priority: ec_pmtct_mother source_from=service_report_vca for this household_id */
                    java.util.List<PtctMotherModel> pmtctMothers = PMTCTMotherDao.getPMTCTMothersByHouseholdId(householdId);
                    if (pmtctMothers != null && !pmtctMothers.isEmpty()) {
                        for (PtctMotherModel mother : pmtctMothers) {
                            String sourceFrom = mother != null ? mother.getSource_from() : null;
                            if (!TextUtils.isEmpty(sourceFrom) &&
                                    ("service_report_vca".equalsIgnoreCase(sourceFrom.trim()) || "vca_service".equalsIgnoreCase(sourceFrom.trim()))) {
                                hideHouseholdProfileButton = true;
                                break;
                            }
                        }
                    }

                    if (!hideHouseholdProfileButton) {
                        java.util.List<IndexMotherModel> mothers = IndexMotherDao.getIndexMothersByHouseholdId(householdId);
                        if (mothers != null && !mothers.isEmpty()) {
                            for (IndexMotherModel mother : mothers) {
                                String sourceFrom = mother != null ? mother.getSource_from() : null;
                                if (!TextUtils.isEmpty(sourceFrom) &&
                                        ("service_report_vca".equalsIgnoreCase(sourceFrom.trim()) || "vca_service".equalsIgnoreCase(sourceFrom.trim()))) {
                                    hideHouseholdProfileButton = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) { }

            // When the household context is from vca_screening (index mother or PMTCT mother),
            // allow opening other FAB actions (assessment, referral, etc.) even if ec_household.screened is false.
            try {
                String householdId = resolveHouseholdIdForSourceChecks();
                if (!TextUtils.isEmpty(householdId)) {
                    allowNonScreeningFabActionsWhenSourceIsVcaScreening =
                            computeIsVcaScreeningSource(householdId, resolveUniqueIdForSourceChecks());
                    sourceFromVcaScreeningResolved = true;
                }
            } catch (Exception ignored) { }

            gender = indexVCA != null ? indexVCA.getGender() : null;
            uniqueId = indexVCA != null ? indexVCA.getUnique_id() : null;

            is_screened = null;
            if (indexVCA != null) {
                String householdId = indexVCA.getHousehold_id();
                if (!TextUtils.isEmpty(householdId)) {
                    try { is_screened = HouseholdDao.checkIfScreened(householdId); } catch (Exception ignored) {}
                }
            }

            is_hiv_positive = null;
            if (!TextUtils.isEmpty(uniqueId)) {
                try { is_hiv_positive = VCAScreeningDao.checkStatus(uniqueId); } catch (Exception ignored) {}
            }

            try { vcaAssessmentModel = VcaAssessmentDao.getVcaAssessment(finalChildId); } catch (Exception ignored) { vcaAssessmentModel = null; }
            try { referralModel = ReferralDao.getReferral(finalChildId); } catch (Exception ignored) { referralModel = null; }
            try { hivRiskAssessmentAbove15Model = HivAssessmentAbove15Dao.getHivAssessmentAbove15(finalChildId); } catch (Exception ignored) { hivRiskAssessmentAbove15Model = null; }
            try { hivRiskAssessmentUnder15Model = HivAssessmentUnder15Dao.getHivAssessmentUnder15(finalChildId); } catch (Exception ignored) { hivRiskAssessmentUnder15Model = null; }
            try { vcaVisitationModel = VcaVisitationDao.getVcaVisitation(finalChildId); } catch (Exception ignored) { vcaVisitationModel = null; }
            try { vcaCasePlanModel = VcaCasePlanDao.getVcaCasePlan(finalChildId); } catch (Exception ignored) { vcaCasePlanModel = null; }
            try { weServiceVcaModel = WeServiceVcaDao.getWeServiceVca(finalChildId); } catch (Exception ignored) { weServiceVcaModel = null; }

            if (indexVCA != null && !TextUtils.isEmpty(indexVCA.getHousehold_id())) {
                try { updatedCaregiver = newCaregiverDao.getNewCaregiverById(indexVCA.getHousehold_id()); } catch (Exception ignored) { updatedCaregiver = null; }
                try { householdCaregiver = CaregiverDao.getCaregiver(indexVCA.getHousehold_id()); } catch (Exception ignored) { householdCaregiver = null; }
            } else {
                updatedCaregiver = null;
                householdCaregiver = null;
            }

            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                initAfterLoad(finalHhIntent);
            });
        });
    }

    private void initAfterLoad(String hhIntent) {
        fabHiv = binding.hivRisk;
        fabHiv2 = binding.hivRisk2;
        fabVisitation = binding.householdVisitationForVcaFab;
        fabReferal = binding.referToFacilityFab;
        fabCasePlan =  binding.casePlanFab;
        fabAssessment = binding.fabAssessment;

        applyHouseholdProfileButtonVisibility();

        oMapper = new ObjectMapper();
        clientMapper = new ObjectMapper();

        if(vcaAssessmentModel == null){
            fabAssessment.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        }

        if(referralModel == null){
            fabReferal.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        }
//        if(hivRiskAssessmentUnder15Model == null){
            fabHiv.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
//        }
//        if(hivRiskAssessmentAbove15Model == null){
            fabHiv2.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
//        }
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

        fab = binding.fab;
        fabScrim = binding.fabScrim;
        fabScrim.setOnClickListener(v -> closeFab());
        if (indexVCA != null && indexVCA.getCase_status() != null &&
                ("0".equals(indexVCA.getCase_status()) || "2".equals(indexVCA.getCase_status()))) {
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        }

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        txtScreening = binding.vcaScreening;
        rassessment = binding.assessment;
        rcase_plan = binding.casePlan;
        referral = binding.referral;
        referral = binding.referral;
        household_visitation_for_vca = binding.householdVisitationForVca;

        hiv_assessment = binding.hivAssessment;
        hiv_assessment2 = binding.hivAssessment2;
        childPlan = binding.childPlan;
        weServicesVca = binding.weServicesVca;
        nutrition_assessment_intervention = binding.nutritionAssessmentIntervention;
        tb_screening = binding.tbScreening;

        txtName = binding.vcaName;
        txtGender = binding.vcaGender;
        txtAge = binding.vcaAge;
        txtChildid = binding.childid;
        profileGenderImage = binding.profileGenderImage;
        profileGenderAvatar = binding.profileGenderAvatar;
        profileNameLayout = binding.profileNameLayout;

        mTabLayout =  binding.tabs;
        mViewPager  = binding.viewpager;
        try { mViewPager.setSaveEnabled(false); } catch (Throwable ignored) {}

        setupViewPager();
        setupFabVisibility();
        updateOverviewTabTitle();
        updateVisitsTabTitle();
        updatePlanTabTitle();

        int page = getIntent().getIntExtra("tab",0);
        mViewPager.setCurrentItem(page, false);

        createDialogForScreening(hhIntent,Constants.EcapConstants.POP_UP_DIALOG_MESSAGE);
    }

    private void applyHouseholdProfileButtonVisibility() {
        try {
            View btn = findViewById(R.id.household_profile);
            if (btn == null) return;
            btn.setVisibility(hideHouseholdProfileButton ? View.GONE : View.VISIBLE);
        } catch (Exception ignored) { }
    }


    public HashMap<String, Child> getData() {
        String displayFirstName = null;
        String displayLastName = null;
        String displayGender = null;
        String displayBirthdate = null;

        if (indexVCA != null) {
            // Prefer adolescent fields when available, fall back to generic ones
            displayFirstName = !TextUtils.isEmpty(indexVCA.getAdolescent_first_name()) ? indexVCA.getAdolescent_first_name() : indexVCA.getFirst_name();
            displayLastName = !TextUtils.isEmpty(indexVCA.getAdolescent_last_name()) ? indexVCA.getAdolescent_last_name() : indexVCA.getLast_name();
            displayGender = !TextUtils.isEmpty(indexVCA.getAdolescent_gender()) ? indexVCA.getAdolescent_gender() : indexVCA.getGender();
            displayBirthdate = !TextUtils.isEmpty(indexVCA.getAdolescent_birthdate()) ? indexVCA.getAdolescent_birthdate() : indexVCA.getBirthdate();
        }

        // If no screening record, use values from the child index
        if (TextUtils.isEmpty(displayFirstName) && child != null) displayFirstName = child.getAdolescent_first_name();
        if (TextUtils.isEmpty(displayLastName) && child != null) displayLastName = child.getAdolescent_last_name();
        if (TextUtils.isEmpty(displayGender) && child != null) displayGender = child.getAdolescent_gender();
        if (TextUtils.isEmpty(displayBirthdate) && child != null) displayBirthdate = child.getAdolescent_birthdate();

        String full_name = ((displayFirstName != null ? displayFirstName : "").trim() + " " + (displayLastName != null ? displayLastName : "").trim()).trim();

        String birthdate = displayBirthdate != null ? checkAndConvertDateFormat(displayBirthdate) : null;

        if (birthdate != null && !"Invalid date format".equals(birthdate)) {
            if (txtAge != null) {
                txtAge.setText(getAge(birthdate));
            }
            vcaAge = getAgeWithoutText(birthdate);
        } else if (txtAge != null) {
            txtAge.setText("Not Set");
        }

        if (txtName != null && !TextUtils.isEmpty(full_name)) {
            txtName.setText(full_name);
        }
        if (txtGender != null) {
            txtGender.setText(!TextUtils.isEmpty(displayGender) ? displayGender.toUpperCase() : "");
        }
        if (profileGenderImage != null && profileGenderAvatar != null) {
            if ("male".equalsIgnoreCase(displayGender)) {
                profileGenderImage.setImageResource(R.drawable.row_boy);
                profileGenderAvatar.setVisibility(View.VISIBLE);
            } else if ("female".equalsIgnoreCase(displayGender)) {
                profileGenderImage.setImageResource(R.drawable.row_girl);
                profileGenderAvatar.setVisibility(View.VISIBLE);
            } else {
                profileGenderAvatar.setVisibility(View.GONE);
            }
        }
        if (profileNameLayout != null) {
            // Match the exact colors initAfterLoad() already uses for the toolbar/app bar by gender,
            // so this section blends with them instead of introducing a third, mismatched palette.
            if ("male".equalsIgnoreCase(displayGender)) {
                profileNameLayout.setBackgroundColor(0xff218CC5);
            } else {
                profileNameLayout.setBackgroundColor(0xffDA70D6);
            }
        }
        if (txtChildid != null) {
            String idForDisplay = indexVCA != null ? indexVCA.getUnique_id() : (child != null ? child.getUnique_id() : null);
            if (!TextUtils.isEmpty(idForDisplay)) {
                txtChildid.setText("ID : " + idForDisplay);
            }
        }

        HashMap<String, Child> map = new HashMap<>();
        map.put("Child", child);
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
        java.util.List<androidx.fragment.app.Fragment> fragments = new java.util.ArrayList<>();
        fragments.add(new ProfileOverviewFragment());
        fragments.add(new ChildCasePlanFragment());
        fragments.add(new ChildVisitsFragment());
        fragments.add(new VcaTbScreeningFragment());


        String hivStatus = indexVCA != null ? indexVCA.getIs_hiv_positive() : null;
        if (hivStatus != null && "no".equalsIgnoreCase(hivStatus)) {
            fragments.add(new VcaHivAssesmentFragment());
        }

        // Add Nutrition Assessment fragment only for children 5 years and below
        try {
            String dob = indexVCA != null ? indexVCA.getAdolescent_birthdate() : null;
            if (dob != null && !dob.trim().isEmpty()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
                java.util.Date d = sdf.parse(dob);
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.setTime(d);
                java.util.Calendar now = java.util.Calendar.getInstance();
                int years = now.get(java.util.Calendar.YEAR) - c.get(java.util.Calendar.YEAR);
                if (now.get(java.util.Calendar.DAY_OF_YEAR) < c.get(java.util.Calendar.DAY_OF_YEAR)) years--;
                if (years <= 5) {
                    fragments.add(new com.bluecodeltd.ecap.chw.fragment.VcaNutritionAssessmentFragment());
                }
            }
        } catch (Exception ignored) { }

        com.bluecodeltd.ecap.chw.adapter.ViewPager2Adapter adapter = new com.bluecodeltd.ecap.chw.adapter.ViewPager2Adapter(this, fragments);
        mViewPager.setAdapter(adapter);
        try { mViewPager.setNestedScrollingEnabled(true); } catch (Throwable ignored) {}
        try { mViewPager.setOffscreenPageLimit(Math.min(5, fragments.size())); } catch (Throwable ignored) {}
        if (tabMediator != null) { try { tabMediator.detach(); } catch (Exception ignored) {} }
        tabMediator = new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("OVERVIEW");
                    break;
                case 1:
                    tab.setText("CASE PLANS");
                    break;
                case 2:
                    tab.setText("VISITS");
                    break;
                default:
                    // For positions beyond the first three, determine label by fragment type
                    try {
                        androidx.fragment.app.Fragment f = fragments.get(position);
                        if (f instanceof com.bluecodeltd.ecap.chw.fragment.VcaNutritionAssessmentFragment) {
                            tab.setText("NUTRITION");
                        } else if (f instanceof com.bluecodeltd.ecap.chw.fragment.VcaTbScreeningFragment) {
                            tab.setText("TB SCREENING");
                        } else if (f instanceof com.bluecodeltd.ecap.chw.fragment.VcaHivAssesmentFragment) {
                            tab.setText("HIV ASSESSMENT");
                        }
                    } catch (Exception ignored) { }
                    break;
            }
        });
        tabMediator.attach();
        if (fragments.size() > 3) {
            // Apply custom titles like other tabs for better consistency
            // HIV Assessment tab (if present at any index)
            for (int i = 0; i < fragments.size(); i++) {
                androidx.fragment.app.Fragment f = fragments.get(i);
                if (f instanceof com.bluecodeltd.ecap.chw.fragment.VcaHivAssesmentFragment) {
                    updateHivAssessmentTabTitleAtIndex(i);
                    break;
                }
            }
            // Nutrition tab (if present at any index)
            for (int i = 0; i < fragments.size(); i++) {
                androidx.fragment.app.Fragment f = fragments.get(i);
                if (f instanceof com.bluecodeltd.ecap.chw.fragment.VcaNutritionAssessmentFragment) {
                    updateNutritionTabTitleAtIndex(i);
                    break;
                }
            }
            // TB Screening tab (if present at any index)
            for (int i = 0; i < fragments.size(); i++) {
                androidx.fragment.app.Fragment f = fragments.get(i);
                if (f instanceof com.bluecodeltd.ecap.chw.fragment.VcaTbScreeningFragment) {
                    updateTbTabTitleAtIndex(i);
                    break;
                }
            }
        }

        // One-time self-serve tip to guide scrolling and swiping
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            boolean shown = prefs.getBoolean("index_scroll_tip_shown", false);
            if (!shown) {
                com.google.android.material.snackbar.Snackbar.make(findViewById(R.id.viewpager),
                        "Scroll inside tabs to see more. Swipe tabs to navigate.",
                        com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                        .setAction("Got it", v -> {})
                        .show();
                prefs.edit().putBoolean("index_scroll_tip_shown", true).apply();
            }
        } catch (Exception ignored) { }
    }
   private void updateTbTabTitleAtIndex(int index) {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText("TB SCREENING");
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        int count = 0;
        try {
            count = com.bluecodeltd.ecap.chw.dao.TbScreeningDao.countByVcaId(uniqueId);
        } catch (Exception ignored) { }
        visitTabCount.setText(String.valueOf(count));
        visitTabCount.setVisibility(View.VISIBLE);

        if (mTabLayout.getTabCount() > index && mTabLayout.getTabAt(index) != null) {
            mTabLayout.getTabAt(index).setCustomView(taskTabTitleLayout);
        }
    }

    private void setupFabVisibility() {
        // Ensure initial state: show only on Overview (position 0)
        updateFabVisibilityForPosition( safeViewPagerPosition() );
        try {
            TabLayout.Tab selectedTab = mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition());
            if (selectedTab != null) {
                updateFabVisibilityForPosition(selectedTab.getPosition());
            }
        } catch (Exception ignored) {}

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Floating menu should only appear on the Overview fragment; close it when leaving
                if (position != 0 && isFabOpen) {
                    closeFab();
                }
                updateFabVisibilityForPosition(position);
            }

        });

        // Defensive: also react to tab selections in case page callbacks are skipped
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == null) return;
                if (tab.getPosition() != 0 && isFabOpen) {
                    closeFab();
                }
                updateFabVisibilityForPosition(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void updateFabVisibilityForPosition(int position) {
        if (fab == null) {
            return;
        }
        try {
            if (position == 0) {
                fab.show();
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.hide();
                fab.setVisibility(View.GONE);
            }
        } catch (Exception ignored) { }
    }

    private int safeViewPagerPosition() {
        try {
            return mViewPager.getCurrentItem();
        } catch (Exception e) {
            return 0;
        }
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

        TextView countView = visitTabCount;
        countView.setText("ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¦");
        visitTabCount.setVisibility(View.VISIBLE);

        final String uid = uniqueId;
        Threading.ioBestEffort(() -> {
            int count = 0;
            try {
                int u15 = HivAssessmentUnder15Dao.getHivAssessment(uid).size();
                int a15 = HivAssessmentAbove15Dao.getHivAssessment(uid).size();
                count = u15 + a15;
            } catch (Exception ignored) { }
            int finalCount = count;
            Threading.main(() -> {
                if (isFinishing()) return;
                countView.setText(String.valueOf(finalCount));
            });
        });

        mTabLayout.getTabAt(3).setCustomView(taskTabTitleLayout);
    }

    // Allows setting HIV tab title at a dynamic index when tab order changes
    private void updateHivAssessmentTabTitleAtIndex(int index) {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText("HIV ASSESSMENT");
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        TextView countView = visitTabCount;
        countView.setText("ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¦");
        visitTabCount.setVisibility(View.VISIBLE);

        final String uid = uniqueId;
        Threading.ioBestEffort(() -> {
            int count = 0;
            try {
                int u15 = HivAssessmentUnder15Dao.getHivAssessment(uid).size();
                int a15 = HivAssessmentAbove15Dao.getHivAssessment(uid).size();
                count = u15 + a15;
            } catch (Exception ignored) { }
            int finalCount = count;
            Threading.main(() -> {
                if (isFinishing()) return;
                countView.setText(String.valueOf(finalCount));
            });
        });

        if (mTabLayout.getTabCount() > index && mTabLayout.getTabAt(index) != null) {
            mTabLayout.getTabAt(index).setCustomView(taskTabTitleLayout);
        }
    }
    private void updateVisitsTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText(this.getString(org.smartregister.opd.R.string.visits));
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        TextView countView = visitTabCount;
        countView.setText("ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¦");

        final String uid = uniqueId;
        Threading.ioBestEffort(() -> {
            int visits = 0;
            try { visits = VcaVisitationDao.countVisits(uid); } catch (Exception ignored) { }
            int finalVisits = visits;
            Threading.main(() -> {
                if (isFinishing()) return;
                countView.setText(String.valueOf(finalVisits));
            });
        });

        mTabLayout.getTabAt(2).setCustomView(taskTabTitleLayout);
    }

    private void updatePlanTabTitle() {
        ConstraintLayout plansTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.plan_tab_title, null);
        TextView visitTabTitle = plansTabTitleLayout.findViewById(R.id.plans_title);
        visitTabTitle.setText("CASE PLANS");
        plansTabCount = plansTabTitleLayout.findViewById(R.id.plans_count);

        TextView countView = plansTabCount;
        countView.setText("ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¦");

        final String uid = uniqueId;
        Threading.ioBestEffort(() -> {
            int plans = 0;
            try { plans = CasePlanDao.checkCasePlan(uid); } catch (Exception ignored) { }
            int finalPlans = plans;
            Threading.main(() -> {
                if (isFinishing()) return;
                countView.setText(String.valueOf(finalPlans));
            });
        });

        mTabLayout.getTabAt(1).setCustomView(plansTabTitleLayout);
    }

    // New: Nutrition tab custom title similar to visits tab pattern
    private void updateNutritionTabTitleAtIndex(int index) {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText("NUTRITION");
        visitTabCount = taskTabTitleLayout.findViewById(R.id.visits_count);

        TextView countView = visitTabCount;
        countView.setText("ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã¢â‚¬Å¡Ãƒâ€šÃ‚Â¦");
        visitTabCount.setVisibility(View.VISIBLE);

        final String uid = uniqueId;
        Threading.ioBestEffort(() -> {
            int count = 0;
            try {
                // Count nutrition assessments for this beneficiary
                count = NutritionAssessmentInterventionDao.countByVcaId(uid);
            } catch (Exception ignored) { }
            int finalCount = count;
            Threading.main(() -> {
                if (isFinishing()) return;
                countView.setText(String.valueOf(finalCount));
            });
        });

        if (mTabLayout.getTabCount() > index && mTabLayout.getTabAt(index) != null) {
            mTabLayout.getTabAt(index).setCustomView(taskTabTitleLayout);
        }
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
        final VcaScreeningModel vca = indexVCA;

        switch (id){
            case R.id.fab:
                if (child != null && child.getCase_status() != null &&
                        (child.getCase_status().equals("0") || child.getCase_status().equals("2"))) {
                    showDeregisteredStatus();
                } else {
                    animateFAB();
                }

                break;

            case R.id.vca_screening:

                    if (!ensureIndexVcaAvailable()) {
                        break;
                    }
                    try {
                        openFormUsingFormUtils(IndexDetailsActivity.this, resolveVcaScreeningFormName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                   }

                break;


            case R.id.assessment:

                if (!ensureIndexVcaAvailable()) {
                    break;
                }

                if (vca != null && vca.getDate_screened() != null){
                    try {
                        openFormUsingFormUtils(IndexDetailsActivity.this,"vca_assessment");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }


                break;
            case R.id.case_plan:

                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                if (vca != null && vca.getDate_screened() != null){
                    try {
                        openFormUsingFormUtils(IndexDetailsActivity.this,"case_plan");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }

                break;
            case R.id.referral:

                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                if (vca != null && vca.getDate_screened() != null){

                    try {

                        openFormUsingFormUtils(IndexDetailsActivity.this,"referral");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }


                break;

            case R.id.myservice:

                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                if (vca != null && vca.getDate_screened() != null) {
                    String householdId = vca.getHousehold_id();
                    String vcaId = vca.getUnique_id();
                    String vcaName = txtName != null && txtName.getText() != null ? txtName.getText().toString() : "";

                    if (TextUtils.isEmpty(householdId) || TextUtils.isEmpty(vcaId)) {
                        Toasty.warning(IndexDetailsActivity.this, "Member data incomplete", Toast.LENGTH_LONG, true).show();
                        break;
                    }

                    try {
                        Intent intent2 = new Intent(this, VcaServiceActivity.class);
                        intent2.putExtra("hh_id", householdId);
                        intent2.putExtra("vcaid", vcaId);
                        intent2.putExtra("hivstatus", vca.getIs_hiv_positive());
                        intent2.putExtra("vcaname", vcaName);
                        startActivity(intent2);
                    } catch (RuntimeException e) {
                        Timber.e(e, "Failed to open VCA service activity for vcaId=%s", vcaId);
                        Toasty.warning(IndexDetailsActivity.this, "Unable to open services", Toast.LENGTH_LONG, true).show();
                    }
                }
                else{
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;
            case R.id.show_referrals:

                if (child == null) {
                    Toast.makeText(IndexDetailsActivity.this, "Member data incomplete", Toast.LENGTH_LONG).show();
                    Timber.w("Cannot open referrals: child record missing for %s", childId);
                    break;
                }

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

                if (child == null) {
                    Toast.makeText(IndexDetailsActivity.this, "Member data incomplete", Toast.LENGTH_LONG).show();
                    Timber.w("Cannot open household profile: child record missing for %s", childId);
                    break;
                }

                Intent intent = new Intent(this, HouseholdDetails.class);
                intent.putExtra("childId",  child.getUnique_id());
                intent.putExtra("householdId",  child.getHousehold_id());
//            intent.putExtra("householdId",  child.getHousehold_id());
           // intent.putExtra("household",  child.getHousehold_id());

                startActivity(intent);


                break;

            case R.id.household_visitation_for_vca:

                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                if (!isTbScreeningCompliantForVisitation()) {
                    Toasty.warning(IndexDetailsActivity.this, "TB screening for this quarter is required before household visitation for CAs above 10 years.", Toast.LENGTH_LONG, true).show();
                    break;
                }
                if (vca != null && vca.getDate_screened() != null) {
                    try {

                        openFormUsingFormUtils(IndexDetailsActivity.this,"household_visitation_for_vca_0_20_years");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else{
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }


                break;

            case R.id.hiv_assessment:

                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                if (vca != null && vca.getDate_screened() != null) {
                    openFormUsingFormUtils(IndexDetailsActivity.this, "hiv_risk_assessment_under_15_years");
                } else {
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;

            case R.id.hiv_assessment2:
                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                if (vca != null && vca.getDate_screened() != null) {
                    openFormUsingFormUtils(IndexDetailsActivity.this, "hiv_risk_assessment_above_15_years");
                }else{
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;

            case R.id.we_services_vca:
                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                if (vca != null && vca.getDate_screened() != null) {
                    openFormUsingFormUtils(IndexDetailsActivity.this, "we_services_vca");
                }else{
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;
            case R.id.tb_screening:
                if (!ensureIndexVcaAvailable()) { break; }
                if (vca != null && vca.getDate_screened() != null) {
                    try {
                        long todayMillis = java.util.Calendar.getInstance().getTimeInMillis();
                        boolean alreadyToday = TbScreeningDao.existsOnSameDateByUniqueId(vca.getUnique_id(), todayMillis);
                        if (alreadyToday) {
                            Toasty.warning(IndexDetailsActivity.this, "TB Screening already done today", Toast.LENGTH_LONG, true).show();
                            break;
                        }
                        openFormUsingFormUtils(IndexDetailsActivity.this, "tb_screening");
                    } catch (org.json.JSONException e) { e.printStackTrace(); }
                } else {
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;
            case R.id.nutrition_assessment_intervention:
                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                if (vca != null && vca.getDate_screened() != null) {
                    int years = 0;
                    try { years = calculateAge(vca.getAdolescent_birthdate()); } catch (Exception ignore) {}
                    if (years <= 5) {
                        openFormUsingFormUtils(IndexDetailsActivity.this, "nutrition_assessment_intervention");
                    } else {
                        Toasty.warning(IndexDetailsActivity.this, "Only for CA aged 5 years and below", Toast.LENGTH_LONG, true).show();
                    }
                } else{
                    Toasty.warning(IndexDetailsActivity.this, "CA Screening has not been done", Toast.LENGTH_LONG, true).show();
                }
                break;
            case R.id.childPlan:
                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                if (vca == null) {
                    Toasty.warning(IndexDetailsActivity.this, "Member data incomplete", Toast.LENGTH_LONG, true).show();
                    break;
                }
                Intent i = new Intent(IndexDetailsActivity.this, ChildSafetyPlanActivity.class);
                i.putExtra("vca_id", vca.getUnique_id());
                i.putExtra("vca_name", vca.getFirst_name() + ' ' + vca.getLast_name());
                startActivity(i);
                finish();
                break;

        }
    }

    private boolean ensureIndexVcaAvailable() {
        if (indexVCA != null) {
            return true;
        }
        Toast.makeText(IndexDetailsActivity.this, "Member data incomplete", Toast.LENGTH_LONG).show();
        Timber.w("IndexDetailsActivity action skipped: indexCA missing for childId=%s", childId);
        return false;
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

                    Runnable postSaveAction = () -> {
                        switch (encounterType) {
                            case "VCA Case Plan":
                                try {
                                    JSONObject cpdate = getFieldJSONObject(fields(jsonFormObject, "step1"), "case_plan_date");
                                    String dateId = cpdate != null ? cpdate.optString("value") : "";

                                    JSONObject cpId = getFieldJSONObject(fields(jsonFormObject, "step1"), "case_plan_id");
                                    String cp_Id = cpId != null ? cpId.optString("value") : "";

                                    refreshActivity();
                                    openVcaCasplanToAddVulnarabilities(dateId, cp_Id);
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
                        Toasty.success(IndexDetailsActivity.this, "Form Saved", Toast.LENGTH_LONG, true).show();
                    };

                    boolean refreshScheduled = saveRegistration(childIndexEventClient, is_edit_mode, postSaveAction);
                    if (!refreshScheduled) {
                        postSaveAction.run();
                    }

                } catch (Exception e) {
                    Timber.e(e);
                }

            }
        }

    }

    private void openVcaCasplanToAddVulnarabilities(String dateId,String cpId) {
        if (!ensureIndexVcaAvailable()) {
            return;
        }
        final VcaScreeningModel vca = indexVCA;
        if (vca == null) {
            Toasty.warning(IndexDetailsActivity.this, "Member data incomplete", Toast.LENGTH_LONG, true).show();
            return;
        }
        Intent i = new Intent(IndexDetailsActivity.this, CasePlan.class);
        i.putExtra("childId", vca.getUnique_id());
        i.putExtra("dateId",  dateId);
        i.putExtra("case_plan_id",cpId);
        i.putExtra("hivStatus",  vca.getIs_hiv_positive());
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

                case "TB Screening":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_TB_SCREENING);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        ChildIndexEventClient out = new ChildIndexEventClient(event, client);
                        return out;
                    }
                    break;

                case "Nutrition Assessment and Intervention":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_NUTRITION_ASSESSMENT_INTERVENTION);
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

    private void refreshActivity() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        Intent intent = new Intent(getIntent());
        intent.putExtra("refresh", "true");
        if (childId != null && !childId.isEmpty()) {
            intent.putExtra("Child", childId);
            intent.putExtra("base_entity_id", childId);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void animateFAB(){
        if (fab == null) {
            return;
        }

        if (isFabOpen){
            closeFab();
        } else {
            isFabOpen = true;
            if (rotate_forward != null) {
                fab.startAnimation(rotate_forward);
            }
            if (txtScreening != null) txtScreening.setVisibility(View.VISIBLE);
            if (fabScrim != null) {
                fabScrim.setVisibility(View.VISIBLE);
                fabScrim.setAlpha(0f);
                fabScrim.animate().alpha(1f).setDuration(200).start();
            }


            boolean allowNonScreeningActions =
                    (is_screened != null && is_screened.equals("true")) ||
                            allowNonScreeningFabActionsWhenSourceIsVcaScreening;

            if (allowNonScreeningActions){
                int vcaAgeYears = parseVcaAgeYears(vcaAge);
                final VcaScreeningModel currentVca = indexVCA;

                if (rcase_plan != null) rcase_plan.setVisibility(View.VISIBLE);
                if (referral != null) referral.setVisibility(View.VISIBLE);
                if (household_visitation_for_vca != null) household_visitation_for_vca.setVisibility(View.VISIBLE);
                if (childPlan != null) childPlan.setVisibility(View.VISIBLE);

                if(currentVca != null && currentVca.getIs_hiv_positive() != null){
                    if (rassessment != null) rassessment.setVisibility(View.VISIBLE);
                }
                if(currentVca != null && currentVca.getIs_hiv_positive() != null && currentVca.getIs_hiv_positive().equals("yes")){
                    if (hiv_assessment != null) hiv_assessment.setVisibility(View.GONE);
                } else if (vcaAgeYears > 1) {
                    if (vcaAgeYears < 15) {
                        if (hiv_assessment != null) hiv_assessment.setVisibility(View.VISIBLE);
                    }

                    if (vcaAgeYears >= 15) {
                        if (hiv_assessment2 != null) hiv_assessment2.setVisibility(View.VISIBLE);
                    }
                }

                if (vcaAgeYears > 18){
                    if (weServicesVca != null) weServicesVca.setVisibility(View.VISIBLE);
                }
                // Show Nutrition Assessment & Intervention only for VCA aged 5 years and below
                try {
                    int years = currentVca != null ? calculateAge(currentVca.getAdolescent_birthdate()) : -1;
                    if (years <= 5) {
                        if (nutrition_assessment_intervention != null) nutrition_assessment_intervention.setVisibility(View.VISIBLE);
                    }
                    // TB screening available for all ages when screened
                    if (tb_screening != null) tb_screening.setVisibility(View.VISIBLE);
                } catch (Exception ignore) {}
            }
            else{
                // If we haven't resolved the source_from context yet, resolve it before showing the toast.
                if (!sourceFromVcaScreeningResolved || !allowNonScreeningFabActionsWhenSourceIsVcaScreening) {
                    String householdId = resolveHouseholdIdForSourceChecks();
                    if (!TextUtils.isEmpty(householdId)) {
                        final String finalHouseholdId = householdId;
                        final String finalUniqueId = resolveUniqueIdForSourceChecks();
                        Threading.ioBestEffort(() -> {
                            boolean isVcaScreening = false;
                            try { isVcaScreening = computeIsVcaScreeningSource(finalHouseholdId, finalUniqueId); } catch (Exception ignored) { }
                            final boolean finalIsVcaScreening = isVcaScreening;
                            Threading.main(() -> {
                                allowNonScreeningFabActionsWhenSourceIsVcaScreening = finalIsVcaScreening;
                                sourceFromVcaScreeningResolved = true;
                                if (finalIsVcaScreening && isFabOpen) {
                                    // Re-open menu with allowed actions
                                    closeFab();
                                    animateFAB();
                                } else if (!finalIsVcaScreening) {
                                    Toasty.warning(IndexDetailsActivity.this, "CA Household Hasn't Been Screened", Toast.LENGTH_LONG, true).show();
                                }
                            });
                        });
                        return;
                    }
                }

                Toasty.warning(IndexDetailsActivity.this, "CA Household Hasn't Been Screened", Toast.LENGTH_LONG, true).show();

            }
        }

    }

    private String resolveHouseholdIdForSourceChecks() {
        try {
            final VcaScreeningModel vca = indexVCA;
            String householdId = vca != null ? vca.getHousehold_id() : null;
            if (!TextUtils.isEmpty(householdId)) return householdId;
        } catch (Exception ignored) { }
        try {
            String householdId = child != null ? child.getHousehold_id() : null;
            if (!TextUtils.isEmpty(householdId)) return householdId;
        } catch (Exception ignored) { }
        // Fallback: resolve from client index (unique_id -> household_id mapping)
        try {
            String unique = null;
            try {
                final VcaScreeningModel vca = indexVCA;
                unique = vca != null ? vca.getUnique_id() : null;
            } catch (Exception ignored) { }
            if (TextUtils.isEmpty(unique)) {
                try { unique = childId; } catch (Exception ignored) { }
            }
            if (!TextUtils.isEmpty(unique)) {
                EcClientIndexSummary summary = null;
                try { summary = IndexPersonDao.getClientSummaryByUniqueId(unique); } catch (Exception ignored) { }
                String hh = summary != null ? summary.getHouseholdId() : null;
                if (!TextUtils.isEmpty(hh)) return hh;
            }
        } catch (Exception ignored) { }
        return null;
    }

    private String resolveUniqueIdForSourceChecks() {
        try {
            final VcaScreeningModel vca = indexVCA;
            String uid = vca != null ? vca.getUnique_id() : null;
            if (!TextUtils.isEmpty(uid)) return uid;
        } catch (Exception ignored) { }
        try {
            if (!TextUtils.isEmpty(childId)) return childId;
        } catch (Exception ignored) { }
        return null;
    }

    private boolean computeIsVcaScreeningSource(String householdId, String vcaUniqueId) {
        if (TextUtils.isEmpty(householdId)) return false;
        // In some datasets this context is tagged as "vca_screening" or "vca_service".
        try {
            java.util.List<PtctMotherModel> pmtctMothers = PMTCTMotherDao.getPMTCTMothersByHouseholdId(householdId);
            if (pmtctMothers != null) {
                for (PtctMotherModel mother : pmtctMothers) {
                    String sourceFrom = mother != null ? mother.getSource_from() : null;
                    if (isSourceFromVcaContext(sourceFrom)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) { }

        // Flexible lookup: PMTCT mother records may be keyed by household_id or by a VCA unique_id.
        try {
            PtctMotherModel mother = PMTCTMotherDao.getPMCTMother(householdId);
            String sourceFrom = mother != null ? mother.getSource_from() : null;
            if (isSourceFromVcaContext(sourceFrom)) {
                return true;
            }
        } catch (Exception ignored) { }
        if (!TextUtils.isEmpty(vcaUniqueId)) {
            try {
                PtctMotherModel mother = PMTCTMotherDao.getPMCTMother(vcaUniqueId);
                String sourceFrom = mother != null ? mother.getSource_from() : null;
                if (isSourceFromVcaContext(sourceFrom)) {
                    return true;
                }
            } catch (Exception ignored) { }
        }

        try {
            java.util.List<IndexMotherModel> mothers = IndexMotherDao.getIndexMothersByHouseholdId(householdId);
            if (mothers != null) {
                for (IndexMotherModel mother : mothers) {
                    String sourceFrom = mother != null ? mother.getSource_from() : null;
                    if (isSourceFromVcaContext(sourceFrom)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) { }

        return false;
    }

    private static boolean isSourceFromVcaContext(String sourceFrom) {
        if (TextUtils.isEmpty(sourceFrom)) return false;
        String v = sourceFrom.trim();
        return "vca_screening".equalsIgnoreCase(v) ||
                "vca_service".equalsIgnoreCase(v) ||
                "service_report_vca".equalsIgnoreCase(v);
    }

    private int parseVcaAgeYears(String ageValue) {
        if (TextUtils.isEmpty(ageValue)) {
            return -1;
        }
        try {
            return Integer.parseInt(ageValue);
        } catch (NumberFormatException e) {
            Timber.w(e, "Unable to parse VCA age: %s", ageValue);
            return -1;
        }
    }

    public void closeFab(){
        if (fab == null) {
            isFabOpen = false;
            return;
        }
        if (rotate_backward != null) {
            fab.startAnimation(rotate_backward);
        }
        isFabOpen = false;
        if (fabScrim != null) {
            fabScrim.animate().alpha(0f).setDuration(200)
                    .withEndAction(() -> {
                        if (fabScrim != null) fabScrim.setVisibility(View.GONE);
                    }).start();
        }
        if (txtScreening != null) txtScreening.setVisibility(View.GONE);
        if (rassessment != null) rassessment.setVisibility(View.GONE);
        if (rcase_plan != null) rcase_plan.setVisibility(View.GONE);
        if (referral != null) referral.setVisibility(View.GONE);
        if (household_visitation_for_vca != null) household_visitation_for_vca.setVisibility(View.GONE);
        if (hiv_assessment != null) hiv_assessment.setVisibility(View.GONE);
        if (hiv_assessment2 != null) hiv_assessment2.setVisibility(View.GONE);
        if (childPlan != null) childPlan.setVisibility(View.GONE);
        if (weServicesVca != null) weServicesVca.setVisibility(View.GONE);
        if (nutrition_assessment_intervention != null) nutrition_assessment_intervention.setVisibility(View.GONE);
        if (tb_screening != null) tb_screening.setVisibility(View.GONE);


    }


    public void openFormUsingFormUtils(Context context, String formName) throws JSONException {

        if (!ensureIndexVcaAvailable()) {
            return;
        }
        final VcaScreeningModel vca = indexVCA;
        if (oMapper == null) {
            oMapper = new ObjectMapper();
        }

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);
        if (formToBeOpened == null) {
            Toasty.warning(this, "Unable to open form", Toast.LENGTH_LONG, true).show();
            return;
        }

        String householdIdForForm = resolveHouseholdIdForSourceChecks();

        String uniqueIdForForm = resolveUniqueIdForSourceChecks();
        String safeAgeText = txtAge != null && txtAge.getText() != null ? txtAge.getText().toString() : "";
        String safeGenderText = txtGender != null && txtGender.getText() != null ? txtGender.getText().toString() : "";
        String safeFirstName = !TextUtils.isEmpty(vca.getFirst_name()) ? vca.getFirst_name() : "";
        String safeLastName = !TextUtils.isEmpty(vca.getLast_name()) ? vca.getLast_name() : "";
        formToBeOpened.getJSONObject("step1").put("title", safeFirstName + " " + safeLastName + " : " + safeAgeText + " - " + safeGenderText);
        formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("value", vca.getUnique_id());

        switch (formName) {

            case "case_status":
                if(vca.getIs_on_hiv_treatment() == null){
                    @NotNull Map<String, String> indexVCAMap = oMapper.convertValue(vca, Map.class);
                    indexVCAMap.remove("date_started_art");
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, indexVCAMap);
                } else {
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vca, Map.class));
                }

                //Populate Caseworker Name
                populateCaseworkerPhoneAndName(formToBeOpened);

                if(vca.getIndex_check_box() != null && !vca.getIndex_check_box().equals("1")){
                    formToBeOpened.remove(JsonFormUtils.ENCOUNTER_TYPE);
                    formToBeOpened.put(JsonFormUtils.ENCOUNTER_TYPE, "Member Sub Population");
                }
                GraduationModel graduationModel = TextUtils.isEmpty(householdIdForForm) ? null : GraduationDao.getGraduationStatus(householdIdForForm);
                if (graduationModel == null || "0".equals(graduationModel.getGraduation_status()) || graduationModel.getGraduation_status() == null) {

                JSONObject graduationStatus = getFieldJSONObject(fields(formToBeOpened, "step1"), "graduation_benchmark");
                    if (graduationStatus != null) {
                        graduationStatus.put("type", "toaster_notes");
                        graduationStatus.put("text", safeFirstName + " " + safeLastName + " household needs to meet all eight graduation benchmarks in order to graduate");
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



                formToBeOpened.put("entity_id", vca.getBase_entity_id());

                break;

            case "vca_screening":
            case "vca_edit":
            case "vca_edit_from_vca_service":
                if(vca.getIs_on_hiv_treatment() == null){
                    @NotNull Map<String, String> indexVCAMap = oMapper.convertValue(vca, Map.class);
                    indexVCAMap.remove("date_started_art");
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, indexVCAMap);
                } else {
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vca, Map.class));
                }

                //Populate Caseworker Name
                populateCaseworkerPhoneAndName(formToBeOpened);

                if(vca.getIndex_check_box() != null && !vca.getIndex_check_box().equals("1")){
                    formToBeOpened.remove(JsonFormUtils.ENCOUNTER_TYPE);
                    formToBeOpened.put(JsonFormUtils.ENCOUNTER_TYPE, "Member Sub Population");
                }


                formToBeOpened.put("entity_id", vca.getBase_entity_id());

                break;


            case "vca_assessment":
                if(vcaAssessmentModel == null){

                    //Pulls data for populating from indexchild when adding data for the very first time
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vca, Map.class));
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", vcaAge);
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(0).put("value", vca.getSubpop1());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(1).put("value", vca.getSubpop2());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(2).put("value", vca.getSubpop3());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(3).put("value", vca.getSubpop4());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(5).put("value", vca.getSubpop());
                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(49).getJSONArray("options").getJSONObject(4).put("value", vca.getSubpop5());


                } else {

                    formToBeOpened.put("entity_id", this.vcaAssessmentModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vcaAssessmentModel, Map.class));

                }

            break;

                case "household_visitation_for_vca_0_20_years":
                    Double vAge = getAndCalculateAge(vca.getAdolescent_birthdate());

                    JSONObject hiv_infection = getFieldJSONObject(fields(formToBeOpened, "step1"), "hiv_infection");
                    JSONObject nutrition_status = getFieldJSONObject(fields(formToBeOpened, "step1"), "nutrition_status");
                    JSONObject against_hiv_risk = getFieldJSONObject(fields(formToBeOpened, "step1"), "against_hiv_risk");



                    if (vAge >= 10.0 && vAge <= 17.0) {
                        hiv_infection.put("type", "native_radio");
//                        prevention_support.put("type", "native_radio");
//                        against_hiv_risk.put("type", "native_radio");
                    } else {
                        hiv_infection.put("type", "hidden");
//                        prevention_support.put("type", "hidden");
//                        against_hiv_risk.put("type", "hidden");
                    }


                    JSONObject under_five = getFieldJSONObject(fields(formToBeOpened, "step1"), "under_five");
                    if (vAge > 5) {
                        under_five.put("type", "hidden");
//                        nutrition_status.put("type", "hidden");
                    }

                    JSONObject eid_test = getFieldJSONObject(fields(formToBeOpened, "step1"), "eid_test");
                    JSONObject age_appropriate = getFieldJSONObject(fields(formToBeOpened, "step1"), "age_appropriate");
                    JSONObject  child_receiving_breastfeeding = getFieldJSONObject(fields(formToBeOpened, "step1"), " child_receiving_breastfeeding");


                    if (vAge <= 5) {
                        eid_test.put("type", "edit_text");
                        age_appropriate.put("type", "native_radio");
//                        child_receiving_breastfeeding.put("type", "native_radio");

                    }  else {
                        eid_test.put("type", "hidden");
                        age_appropriate.put("type", "hidden");
//                        child_receiving_breastfeeding.put("type", "hidden");
                    }

                    JSONObject currently_in_school = getFieldJSONObject(fields(formToBeOpened, "step1"), "currently_in_school");
                    JSONObject verified_by_school = getFieldJSONObject(fields(formToBeOpened, "step1"), "verified_by_school");
                    JSONObject current_calendar = getFieldJSONObject(fields(formToBeOpened, "step1"), "current_calendar");
                    JSONObject school_administration_name = getFieldJSONObject(fields(formToBeOpened, "step1"), "school_administration_name");
                    JSONObject telephone_number = getFieldJSONObject(fields(formToBeOpened, "step1"), "telephone_number");
                    JSONObject school_administration_date_signed = getFieldJSONObject(fields(formToBeOpened, "step1"), "school_administration_date_signed");
                    JSONObject school_administration_signature = getFieldJSONObject(fields(formToBeOpened, "step1"), "school_administration_signature");



                    if(vAge < 5){
                        currently_in_school.put("type", "hidden");
                        verified_by_school.put("type", "hidden");
                        current_calendar.put("type", "hidden");
                        school_administration_name.put("type", "hidden");
                        telephone_number.put("type", "hidden");
                        school_administration_date_signed.put("type", "hidden");
                        school_administration_signature.put("type", "hidden");
                    }

                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", vcaAge);

                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vca, Map.class));


                    break;
                case "we_services_vca":
                if(weServiceVcaModel == null){

                    //Pulls data for populating from indexchild when adding data for the very first time
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vca, Map.class));

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

                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vca, Map.class));
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
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vca, Map.class));

//                } else {
//
//                    formToBeOpened.put("entity_id", this.hivRiskAssessmentUnder15Model.getBase_entity_id());
//                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(hivRiskAssessmentUnder15Model, Map.class));
//                }

                break;

            case "hiv_risk_assessment_above_15_years":

//                if(hivRiskAssessmentAbove15Model == null){

                    //Pulls data for populating from indexchild when adding data for the very first time
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vca, Map.class));

//                } else {
//
//                    formToBeOpened.put("entity_id", this.hivRiskAssessmentAbove15Model.getBase_entity_id());
//                    CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(hivRiskAssessmentAbove15Model, Map.class));
//                }

                break;

            case "child_safety_plan":
            case "referral":
                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(vca, Map.class));
                populateCaseworkerPhoneAndName(formToBeOpened);
            break;

            case "nutrition_assessment_intervention":
                // For new nutrition entries leave entity_id blank so a new one is generated on save.
                // Edits (opened from list) will set entity_id to the existing base_entity_id in the adapter.
                try { formToBeOpened.put("entity_id", ""); } catch (Exception ignore) {}
                break;

            case "tb_screening":
                try { formToBeOpened.put("entity_id", vca.getBase_entity_id()); } catch (Exception ignore) {}
                try {
                    // Show age-appropriate TB symptom fields
                    Double tbAge = getAndCalculateAge(vca.getAdolescent_birthdate());
                    JSONObject lt10 = getFieldJSONObject(fields(formToBeOpened, "step1"), "tb_symptoms_child_lt10");
                    JSONObject lt10Other = getFieldJSONObject(fields(formToBeOpened, "step1"), "tb_symptoms_child_lt10_other");
                    JSONObject plus10 = getFieldJSONObject(fields(formToBeOpened, "step1"), "tb_symptoms_10plus");
                    JSONObject plus10Other = getFieldJSONObject(fields(formToBeOpened, "step1"), "tb_symptoms_10plus_other");
                    JSONObject sputumCollected = getFieldJSONObject(fields(formToBeOpened, "step1"), "sputum_collected");



                    if (tbAge != null) {
                        if (tbAge < 10.0) {
                            // Hide 10+ fields
                            if (plus10 != null) plus10.put("type", "hidden");
                            if (plus10Other != null) plus10Other.put("type", "hidden");
                            if (sputumCollected != null) sputumCollected.put("type", "hidden");
                        } else {
                            // Hide <10 fields
                            if (lt10 != null) lt10.put("type", "hidden");
                            if (lt10Other != null) lt10Other.put("type", "hidden");
                        }
                    }
                } catch (Exception ignore) {}
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
                        String caregiverPhoneNumber = resolveCaregiverPhoneNumber();
                        if (!TextUtils.isEmpty(caregiverPhoneNumber)) {
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
                if (!ensureIndexVcaAvailable()) {
                    break;
                }
                try {
                    openFormUsingFormUtils(IndexDetailsActivity.this, "case_status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.delete_record:
                if (!ensureIndexVcaAvailable() && child == null) {
                    Toast.makeText(getApplicationContext(), "Member data incomplete", Toast.LENGTH_LONG).show();
                    break;
                }

                String deletePronoun = resolveDeletePronoun();
                builder.setMessage("You are about to delete this VCA and all " + deletePronoun + " forms.");
                builder.setNegativeButton("NO", (dialog, id) -> dialog.cancel())
                        .setPositiveButton("YES", (dialogInterface, i) -> {
                            Threading.io(() -> {
                                try {
                                    PmtctChildModel linkedHei = resolveLinkedHeiForDeletion();
                                    if (linkedHei != null) {
                                        try {
                                            ChildIndexEventClient heiDeleteClient = buildHeiDeleteClient(linkedHei);
                                            if (heiDeleteClient != null) {
                                                saveRegistration(heiDeleteClient, true, null);
                                            }
                                        } catch (Exception e) {
                                            Timber.e(e);
                                        }
                                    }

                                    FormUtils formUtils = new FormUtils(this);
                                    JSONObject vcaScreeningForm = formUtils.getFormJson(resolveVcaEditFormName());
                                    try {
                                        VcaScreeningModel vcaForDelete = indexVCA;

                                        if (vcaForDelete != null) {
                                            vcaForDelete.setDeleted("1");
                                            CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue(vcaForDelete, Map.class));
                                            vcaScreeningForm.put("entity_id", vcaForDelete.getBase_entity_id());
                                        } else if (child != null) {
                                            child.setDeleted("1");
                                            CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue(child, Map.class));
                                            vcaScreeningForm.put("entity_id", child.getBase_entity_id());
                                        } else {
                                            Toasty.warning(IndexDetailsActivity.this, "Member data incomplete", Toast.LENGTH_LONG, true).show();
                                            return;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    ChildIndexEventClient childIndexEventClient = processRegistration(vcaScreeningForm.toString());
                                    if (childIndexEventClient == null) {
                                        return;
                                    }
                                    Runnable onComplete = () -> {
                                        Toasty.success(IndexDetailsActivity.this, "Deleted", Toast.LENGTH_LONG, true).show();
                                        IndexDetailsActivity.super.onBackPressed();
                                    };
                                    boolean scheduled = saveRegistration(childIndexEventClient, true, onComplete);
                                    if (!scheduled) {
                                        onComplete.run();
                                    }
                                } catch (Exception e) {
                                    Timber.e(e);
                                }
                            });
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("Alert");
                alert.show();

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private String resolveCaregiverPhoneNumber() {
        try {
            final VcaScreeningModel vca = indexVCA;
            String caregiverPhoneNumber = vca != null ? vca.getCaregiver_phone() : null;
            if (!TextUtils.isEmpty(caregiverPhoneNumber)) {
                return caregiverPhoneNumber.trim();
            }
        } catch (Exception ignored) { }

        try {
            String caregiverPhoneNumber = child != null ? child.getCaregiver_phone() : null;
            if (!TextUtils.isEmpty(caregiverPhoneNumber)) {
                return caregiverPhoneNumber.trim();
            }
        } catch (Exception ignored) { }

        return null;
    }

    private String resolveDeletePronoun() {
        try {
            final VcaScreeningModel vca = indexVCA;
            String resolvedGender = vca != null ? vca.getGender() : null;
            if (TextUtils.isEmpty(resolvedGender) && child != null) {
                resolvedGender = child.getGender();
            }
            return "MALE".equalsIgnoreCase(resolvedGender) ? "his" : "her";
        } catch (Exception ignored) {
            return "her";
        }
    }

    private String safeMemberDisplayName() {
        try {
            final VcaScreeningModel vca = indexVCA;
            String firstName = vca != null ? vca.getFirst_name() : null;
            String lastName = vca != null ? vca.getLast_name() : null;
            String displayName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
            if (!TextUtils.isEmpty(displayName)) {
                return displayName;
            }
        } catch (Exception ignored) { }
        try {
            String firstName = child != null ? child.getAdolescent_first_name() : null;
            String lastName = child != null ? child.getAdolescent_last_name() : null;
            String displayName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
            if (!TextUtils.isEmpty(displayName)) {
                return displayName;
            }
        } catch (Exception ignored) { }
        return "this member";
    }

    private PmtctChildModel resolveLinkedHeiForDeletion() {
        final VcaScreeningModel vca = indexVCA;
        if (vca == null) {
            return null;
        }

        PmtctChildModel linkedHei = null;
        try {
            if (!TextUtils.isEmpty(vca.getUnique_id())) {
                linkedHei = PmtctChildDao.getPMCTChild(vca.getUnique_id());
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        if (linkedHei != null) {
            return linkedHei;
        }

        if (TextUtils.isEmpty(vca.getHousehold_id())) {
            return null;
        }

        try {
            List<PmtctChildModel> heiRecords = PmtctChildDao.getPmctChildHei(vca.getHousehold_id());
            if (heiRecords != null && !heiRecords.isEmpty()) {
                if (heiRecords.size() > 1) {
                    Timber.w("Multiple active HEI records matched household %s during VCA delete; deleting the first record",
                            vca.getHousehold_id());
                }
                linkedHei = heiRecords.get(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return linkedHei;
    }

    private ChildIndexEventClient buildHeiDeleteClient(PmtctChildModel hei) throws Exception {
        if (hei == null) {
            return null;
        }

        FormUtils formUtils = new FormUtils(this);
        JSONObject heiForm = formUtils.getFormJson("pmct_child_hei");
        CoreJsonFormUtils.populateJsonForm(heiForm, new ObjectMapper().convertValue(hei, Map.class));
        heiForm.put("entity_id", hei.getBase_entity_id());

        JSONObject deleteField = getFieldJSONObject(fields(heiForm, "step1"), "delete_status");
        if (deleteField != null) {
            deleteField.remove(JsonFormUtils.VALUE);
            deleteField.put(JsonFormUtils.VALUE, "1");
        }

        ChildIndexEventClient heiDeleteClient = processRegistration(heiForm.toString());
        if (heiDeleteClient == null) {
            Timber.w("Skipping HEI delete because the pmct_child_hei form could not be processed for base_entity_id=%s",
                    hei.getBase_entity_id());
        }
        return heiDeleteClient;
    }

    private static void removeVcaEditStep5(JSONObject form) {
        if (form == null) return;
        try { form.remove("step5"); } catch (Exception ignored) { }
        try { form.put("count", "4"); } catch (Exception ignored) { }
        try {
            JSONObject step4 = form.optJSONObject("step4");
            if (step4 != null) {
                String next = step4.optString("next", null);
                if (next != null && "step5".equalsIgnoreCase(next.trim())) {
                    step4.put("next", "");
                }
            }
        } catch (Exception ignored) { }
    }

    private boolean shouldRemoveVcaEditStep5ForServiceReportVca(Child child) {
        if (child == null) return false;
        String householdId = null;
        try { householdId = child.getHousehold_id(); } catch (Exception ignored) { }
        if (TextUtils.isEmpty(householdId)) {
            try { householdId = resolveHouseholdIdForSourceChecks(); } catch (Exception ignored) { }
        }
        String uniqueId = null;
        try { uniqueId = child.getUnique_id(); } catch (Exception ignored) { }
        if (TextUtils.isEmpty(uniqueId)) {
            try { uniqueId = resolveUniqueIdForSourceChecks(); } catch (Exception ignored) { }
        }

        return hasMotherSourceFromServiceReportVca(householdId, uniqueId);
    }

    private boolean hasMotherSourceFromServiceReportVca(String householdId, String vcaUniqueId) {
        if (TextUtils.isEmpty(householdId) && TextUtils.isEmpty(vcaUniqueId)) return false;

        try {
            if (!TextUtils.isEmpty(householdId)) {
                java.util.List<PtctMotherModel> pmtctMothers = PMTCTMotherDao.getPMTCTMothersByHouseholdId(householdId);
                if (pmtctMothers != null) {
                    for (PtctMotherModel mother : pmtctMothers) {
                        String sourceFrom = mother != null ? mother.getSource_from() : null;
                        if (!TextUtils.isEmpty(sourceFrom) && "service_report_vca".equalsIgnoreCase(sourceFrom.trim())) return true;
                    }
                }
            }
        } catch (Exception ignored) { }

        try {
            if (!TextUtils.isEmpty(householdId)) {
                PtctMotherModel mother = PMTCTMotherDao.getPMCTMother(householdId);
                String sourceFrom = mother != null ? mother.getSource_from() : null;
                if (!TextUtils.isEmpty(sourceFrom) && "service_report_vca".equalsIgnoreCase(sourceFrom.trim())) return true;
            }
        } catch (Exception ignored) { }

        try {
            if (!TextUtils.isEmpty(vcaUniqueId)) {
                PtctMotherModel mother = PMTCTMotherDao.getPMCTMother(vcaUniqueId);
                String sourceFrom = mother != null ? mother.getSource_from() : null;
                if (!TextUtils.isEmpty(sourceFrom) && "service_report_vca".equalsIgnoreCase(sourceFrom.trim())) return true;
            }
        } catch (Exception ignored) { }

        try {
            if (!TextUtils.isEmpty(householdId)) {
                java.util.List<IndexMotherModel> mothers = IndexMotherDao.getIndexMothersByHouseholdId(householdId);
                if (mothers != null) {
                    for (IndexMotherModel mother : mothers) {
                        String sourceFrom = mother != null ? mother.getSource_from() : null;
                        if (!TextUtils.isEmpty(sourceFrom) && "service_report_vca".equalsIgnoreCase(sourceFrom.trim())) return true;
                    }
                }
            }
        } catch (Exception ignored) { }

        return false;
    }


    @Override
    public void onBackPressed() {
        Intent returnToHouseholdIndexActivity = new Intent(IndexDetailsActivity.this, IndexRegisterActivity.class);
        startActivity(returnToHouseholdIndexActivity);
        finish();

    }
    public void createDialogForScreening(String entryPoint, String message){
        if (TextUtils.isEmpty(entryPoint) || !ensureIndexVcaAvailable()) {
            return;
        }
        boolean screenedFalse = "false".equalsIgnoreCase(is_screened);
        if (entryPoint.equals("123") && screenedFalse) {
            builder.setMessage(message + safeMemberDisplayName() + "?");
            builder.setNegativeButton("Later", (dialog, id) -> {
                getIntent().removeExtra("fromHousehold");
                dialog.cancel();
            }).setPositiveButton(Constants.EcapConstants.PROCEED, ((dialogInterface, i) -> {
                getIntent().removeExtra("fromHousehold");
                try {
                    openFormUsingFormUtils(IndexDetailsActivity.this, resolveVcaScreeningFormName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }));
            buildDialog();
        } else if (entryPoint.equals("321") && screenedFalse) {
            builder.setMessage(Constants.EcapConstants.POP_UP_DIALOG_MESSAGE_FOR_HOUSEHOLD + safeMemberDisplayName() + "?");
            builder.setNegativeButton("Later", (dialog, id) -> {
                getIntent().removeExtra("fromIndex");
                dialog.cancel();
            }).setPositiveButton(Constants.EcapConstants.PROCEED, ((dialogInterface, i) -> {
                getIntent().removeExtra("fromIndex");
                try {
                    Intent intent = new Intent(this, HouseholdDetails.class);
                    intent.putExtra("childId", resolveUniqueIdForSourceChecks());
                    intent.putExtra("householdId", resolveHouseholdIdForSourceChecks());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            buildDialog();
        }
    }

    private String resolveVcaScreeningFormName() {
        try {
            String householdId = null;
            try { householdId = child != null ? child.getHousehold_id() : null; } catch (Exception ignored) { }
            if (TextUtils.isEmpty(householdId)) {
                try { householdId = resolveHouseholdIdForSourceChecks(); } catch (Exception ignored) { }
            }

            String uniqueId = null;
            try { uniqueId = child != null ? child.getUnique_id() : null; } catch (Exception ignored) { }
            if (TextUtils.isEmpty(uniqueId)) {
                try { uniqueId = resolveUniqueIdForSourceChecks(); } catch (Exception ignored) { }
            }

            if (hasMotherSourceFromServiceReportVca(householdId, uniqueId)) {
                return "vca_edit_from_vca_service";
            }
        } catch (Exception ignored) { }

        return "vca_edit";
    }

    private String resolveVcaEditFormName() {
        try {
            String householdId = resolveHouseholdIdForSourceChecks();
            String uniqueId = resolveUniqueIdForSourceChecks();
            if (hasMotherSourceFromServiceReportVca(householdId, uniqueId)) {
                return "vca_edit_from_vca_service";
            }
        } catch (Exception ignored) { }
        return "vca_edit";
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
        if (!ensureIndexVcaAvailable()) {
            return;
        }
        final VcaScreeningModel vca = indexVCA;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();

        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        if (dialogMessage != null) {
            dialogMessage.setText((vca != null && vca.getFirst_name() != null ? vca.getFirst_name() : "") + " " +
                    (vca != null && vca.getLast_name() != null ? vca.getLast_name() : "") +
                    " was either de-registered or inactive in the program");
        }

        Button dialogButton = dialog.findViewById(R.id.dialog_button);
        if (dialogButton != null) {
            dialogButton.setOnClickListener(v -> dialog.dismiss());
        }

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
        alert.setTitle("CA Screening");
        alert.show();
    }
    private boolean isTbScreeningCompliantForVisitation() {
        Integer ageYears = getCurrentAgeYears();
        if (ageYears != null && ageYears > 10) {
            final VcaScreeningModel vca = indexVCA;
            String vcaId = vca != null ? vca.getUnique_id() : null;
            return hasTbScreeningInCurrentQuarter(vcaId);
        }
        return true;
    }

    private Integer getCurrentAgeYears() {
        String birthdate = getVcaBirthdate();
        if (TextUtils.isEmpty(birthdate)) {
            return null;
        }
        String normalized = normalizeBirthdate(birthdate);
        if (normalized == null) {
            return null;
        }
        try {
            LocalDate dob = LocalDate.parse(normalized, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return Period.between(dob, LocalDate.now()).getYears();
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private String getVcaBirthdate() {
        final VcaScreeningModel vca = indexVCA;
        if (vca != null && !TextUtils.isEmpty(vca.getAdolescent_birthdate())) {
            return vca.getAdolescent_birthdate();
        }
        if (vca != null && !TextUtils.isEmpty(vca.getBirthdate())) {
            return vca.getBirthdate();
        }
        if (child != null && !TextUtils.isEmpty(child.getAdolescent_birthdate())) {
            return child.getAdolescent_birthdate();
        }
        return null;
    }

    private String normalizeBirthdate(String birthdate) {
        String trimmed = birthdate != null ? birthdate.trim() : null;
        if (TextUtils.isEmpty(trimmed)) {
            return null;
        }
        if (trimmed.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return trimmed;
        }
        String[] patterns = new String[]{"dd MMM yyyy", "yyyy-MM-dd", "dd/MM/yyyy"};
        for (String pattern : patterns) {
            try {
                LocalDate parsed = LocalDate.parse(trimmed, DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
                return parsed.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (DateTimeParseException ignored) { }
        }
        return null;
    }

    private boolean hasTbScreeningInCurrentQuarter(String vcaId) {
        if (TextUtils.isEmpty(vcaId)) {
            return true;
        }

        List<TbScreeningModel> screenings = TbScreeningDao.listByVcaId(vcaId);
        if (screenings == null || screenings.isEmpty()) {
            return false;
        }

        Calendar quarterStart = Calendar.getInstance();
        int currentMonth = quarterStart.get(Calendar.MONTH);
        int startMonth = currentMonth - (currentMonth % 3);
        quarterStart.set(Calendar.MONTH, startMonth);
        quarterStart.set(Calendar.DAY_OF_MONTH, 1);
        quarterStart.set(Calendar.HOUR_OF_DAY, 0);
        quarterStart.set(Calendar.MINUTE, 0);
        quarterStart.set(Calendar.SECOND, 0);
        quarterStart.set(Calendar.MILLISECOND, 0);

        for (TbScreeningModel screening : screenings) {
            Date screeningDate = resolveScreeningDate(screening);
            if (screeningDate != null && !screeningDate.before(quarterStart.getTime())) {
                return true;
            }
        }
        return false;
    }

    private Date resolveScreeningDate(TbScreeningModel screening) {
        if (screening == null) return null;

        Date fromTimestamp = parseTimestamp(screening.getLast_interacted_with());
        if (fromTimestamp != null) return fromTimestamp;

        Date fromStringTs = parseDayMonthYear(screening.getLast_interacted_with());
        if (fromStringTs != null) return fromStringTs;

        Date followUp = parseDayMonthYear(screening.getFollowup_date());
        if (followUp != null) return followUp;

        Date facilityDate = parseDayMonthYear(screening.getDate_screened_at_facility());
        if (facilityDate != null) return facilityDate;

        Date treatmentFollowUp = parseDayMonthYear(screening.getTreatment_followup_date());
        if (treatmentFollowUp != null) return treatmentFollowUp;

        return null;
    }

    private Date parseTimestamp(String timestamp) {
        if (TextUtils.isEmpty(timestamp)) return null;
        try {
            long value = Long.parseLong(timestamp);
            if (timestamp.length() <= 10) {
                value *= 1000;
            }
            return new Date(value);
        } catch (NumberFormatException e) {
            Timber.e(e);
            return null;
        }
    }

    private Date parseDayMonthYear(String dateString) {
        if (TextUtils.isEmpty(dateString)) return null;
        String[] patterns = new String[]{"dd-MM-yyyy", "dd/MM/yyyy", "yyyy-MM-dd"};
        for (String pattern : patterns) {
            try {
                return new SimpleDateFormat(pattern, Locale.ENGLISH).parse(dateString);
            } catch (ParseException ignored) { }
        }
        return null;
    }

    private double getAndCalculateAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today = LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);

        int years = periodBetweenDateOfBirthAndNow.getYears();
        int months = periodBetweenDateOfBirthAndNow.getMonths();

        if (years == 0) {
            if (months >= 10) return 0.5; // 10ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã¢â‚¬Å“11 months
            else if (months >= 7) return 0.4; // 7ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã¢â‚¬Å“9 months
            else if (months >= 4) return 0.3; // 4ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã¢â‚¬Å“6 months
            else if (months >= 1) return 0.1; // 1ÃƒÆ’Ã†â€™Ãƒâ€šÃ‚Â¢ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â€šÂ¬Ã…Â¡Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã¢â‚¬Å“3 months
            else return 0.0; // < 1 month
        } else {
            return (double) years;
        }
    }
}






            




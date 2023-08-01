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
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
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
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.adapter.ViewPagerAdapterFragment;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.HIVTestingServiceDao;
import com.bluecodeltd.ecap.chw.dao.HTSLinksDao;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.ChildCasePlanFragment;
import com.bluecodeltd.ecap.chw.fragment.ChildVisitsFragment;
import com.bluecodeltd.ecap.chw.fragment.HTSlinksFragment;
import com.bluecodeltd.ecap.chw.fragment.ProfileOverviewFragment;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.ChildRegisterModel;
import com.bluecodeltd.ecap.chw.model.HIVTestingServiceModel;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentAbove15Model;
import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentUnder15Model;
import com.bluecodeltd.ecap.chw.model.ReferralModel;
import com.bluecodeltd.ecap.chw.model.VCAModel;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;
import com.bluecodeltd.ecap.chw.model.VcaCasePlanModel;
import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;
import com.bluecodeltd.ecap.chw.model.WeServiceVcaModel;
import com.bluecodeltd.ecap.chw.util.Constants;
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

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class HTSDetailsActivity extends AppCompatActivity {
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
    private  VcaAssessmentModel assessmentModel;

    private TextView txtName, txtGender, txtAge, txtChildid;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private AppExecutors appExecutors;
    public ProfileViewPagerAdapter mPagerAdapter;
    private TextView htsCount,visitTabCount, plansTabCount;
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
    HIVTestingServiceModel hivTestingServiceModel;
    String full_name = "";
    String birthdate = "";

    public VCAModel client;
    AlertDialog.Builder builder, screeningBuilder;

    private ViewPager viewPager;
    TabLayout tabLayout;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hts_profile);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        myAppbar = findViewById(R.id.collapsing_toolbar_appbarlayout);
        NavigationMenu.getInstance(this, null, toolbar);

        fab = findViewById(R.id.fab);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fabHiv = findViewById(R.id.hiv_risk);
        fabHiv2 = findViewById(R.id.hiv_risk2);
        fabVisitation = findViewById(R.id.household_visitation_for_vca_fab);
        fabReferal = findViewById(R.id.refer_to_facility_fab);
        fabCasePlan =  findViewById(R.id.case_plan_fab);
        fabAssessment = findViewById(R.id.fabAssessment);
        txtScreening = findViewById(R.id.vca_screening);
        addIndexClients = findViewById(R.id.assessment);
//        builder = new AlertDialog.Builder(HTSDetailsActivity.this);
//        screeningBuilder = new AlertDialog.Builder(HTSDetailsActivity.this);
//
        clientId = getIntent().getExtras().getString("client_id");
        hivTestingServiceModel = HIVTestingServiceDao.getHivServiceClient(clientId);

        txtName = findViewById(R.id.vca_name);
        txtGender = findViewById(R.id.vca_gender);
        txtAge = findViewById(R.id.vca_age);
        txtChildid = findViewById(R.id.childid);

        if (hivTestingServiceModel != null && hivTestingServiceModel.getGender() != null) {
            txtGender.setText(hivTestingServiceModel.getGender().toUpperCase());
        }


        if (hivTestingServiceModel != null && hivTestingServiceModel.getFirst_name() != null && hivTestingServiceModel.getLast_name() != null)
        {
            full_name = hivTestingServiceModel.getFirst_name() + " " + hivTestingServiceModel.getLast_name();
        txtName.setText(full_name);
            }

        if(hivTestingServiceModel != null && hivTestingServiceModel.getBirthdate() != null && !hivTestingServiceModel.getBirthdate().isEmpty()){
            txtAge.setText(getAge(hivTestingServiceModel.getBirthdate()));
            //vcaAge = getAgeWithoutText(birthdate);

        } else {
            txtAge.setText("Not Set");
        }

        if (clientId != null && !clientId.isEmpty()){
            txtChildid.setText("ID : " + clientId);
        }


//        String hhIntent = getIntent().getExtras().getString("fromHousehold");
//        if(hhIntent == null){
//            hhIntent = getIntent().getExtras().getString("fromIndex");
//        }
//
//        indexVCA = VCAScreeningDao.getVcaScreening(childId);
//        child = IndexPersonDao.getChildByBaseId(childId);
//        gender = indexVCA.getGender();
//        uniqueId = indexVCA.getUnique_id();
//
//        is_screened = HouseholdDao.checkIfScreened(indexVCA.getHousehold_id());
//        is_hiv_positive = VCAScreeningDao.checkStatus(indexVCA.getUnique_id());
//

//
//        vcaAssessmentModel = VcaAssessmentDao.getVcaAssessment(childId);
//        referralModel = ReferralDao.getReferral(childId);
//        hivRiskAssessmentAbove15Model = HivAssessmentAbove15Dao.getHivAssessmentAbove15(childId);
//        hivRiskAssessmentUnder15Model = HivAssessmentUnder15Dao.getHivAssessmentUnder15(childId);
//        vcaVisitationModel = VcaVisitationDao.getVcaVisitation(childId);
//        vcaCasePlanModel = VcaCasePlanDao.getVcaCasePlan(childId);
//        weServiceVcaModel = WeServiceVcaDao.getWeServiceVca(childId);
//
//
//
        oMapper = new ObjectMapper();
        clientMapper = new ObjectMapper();
//
//        if(vcaAssessmentModel == null){
//            fabAssessment.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
//        }
//
//        if(referralModel == null){
//            fabReferal.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
//        }
//        if(hivRiskAssessmentUnder15Model == null){
//            fabHiv.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
//        }
//        if(hivRiskAssessmentAbove15Model == null){
//            fabHiv2.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
//        }
//        if(vcaVisitationModel == null){
//            fabVisitation.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
//        }
//        if(vcaCasePlanModel == null){
//            fabCasePlan.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
//        }
//
//        if( gender != null && gender.equals("male")){
//
//            toolbar.setBackgroundDrawable(new ColorDrawable(0xff218CC5));
//            myAppbar.setBackgroundDrawable(new ColorDrawable(0xff218CC5));
//
//        } else {
//
//            toolbar.setBackgroundDrawable(new ColorDrawable(0xffDA70D6));
//            myAppbar.setBackgroundDrawable(new ColorDrawable(0xffDA70D6));
//
//        }
//
//
//
//        if(indexVCA.getCase_status() != null && (indexVCA.getCase_status().equals("0") || indexVCA.getCase_status().equals("2"))){
//            fab.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//        } else if(calculateAge(indexVCA.getAdolescent_birthdate()) >= 19){
//            fab.setVisibility(View.INVISIBLE);
//        }

/*//        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
//        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);*/
//

//        rassessment = findViewById(R.id.assessment);
//        rcase_plan = findViewById(R.id.case_plan);
//        referral = findViewById(R.id.referral);
//        household_visitation_for_vca = findViewById(R.id.household_visitation_for_vca);
//
//
//
//        hiv_assessment = findViewById(R.id.hiv_assessment);
//        hiv_assessment2 = findViewById(R.id.hiv_assessment2);
//        childPlan = findViewById(R.id.childPlan);
//        weServicesVca = findViewById(R.id.we_services_vca);
//

//
//        mTabLayout =  findViewById(R.id.tabs);
//        mViewPager  = findViewById(R.id.viewpager);
//
//        setupViewPager();
//        updateVisitsTabTitle();
//        updatePlanTabTitle();
//
//        int page = getIntent().getIntExtra("tab",0);
//        mViewPager.setCurrentItem(page);
//
//createDialogForScreening(hhIntent,Constants.EcapConstants.POP_UP_DIALOG_MESSAGE);
//

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
//        updateTasksTabTitle();
        returnViewPager();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HTSlinksFragment());

        ViewPagerAdapterFragment adapter = new ViewPagerAdapterFragment(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("HTS Links");
    }


    public HashMap<String, Child> getData() {
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
    public HashMap<String, HIVTestingServiceModel> getLinkID() {

        HashMap<String, HIVTestingServiceModel> map = new HashMap<>();

        map.put("client",hivTestingServiceModel);

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

        mViewPager.setAdapter(mPagerAdapter);
        //mViewPager.setOffscreenPageLimit(1);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("OVERVIEW");
        mTabLayout.getTabAt(1).setText("CASE PLANS");
        mTabLayout.getTabAt(2).setText("VISITS");


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

                    animateFAB();
                break;

                case R.id.vca_screening:

                    try {

                        openFormUsingFormUtils(HTSDetailsActivity.this,"hiv_testing_service");
                    } catch (JSONException e) {
                        e.printStackTrace();
                   }

                break;


            case R.id.assessment:
                    try {
                        openFormUsingFormUtils(HTSDetailsActivity.this,"hiv_testing_links");
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
            String encounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");

            if(!jsonFormObject.optString("entity_id").isEmpty()){
                is_edit_mode = true;
            }

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, is_edit_mode);

                switch (encounterType) {
                    case "VCA Case Plan":

                        JSONObject cpdate = getFieldJSONObject(fields(jsonFormObject, "step1"), "case_plan_date");
                        String dateId = cpdate.optString("value");
                        finish();
                        startActivity(getIntent());
                        openVcaCasplanToAddVulnarabilities(dateId);

                        break;

                    case "Household Visitation Form 0-20 years":
                    case "Member Sub Population":
                    case "Sub Population":
                    case "VCA Assessment":
                    case "HIV Risk Assessment Above 15":
                    case "HIV Risk Assessment Below 15":
                    case "HIV Testing Links":
                    case "HIV Testing Service":

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

                Toasty.success(HTSDetailsActivity.this, "Form Saved", Toast.LENGTH_LONG, true).show();

            } catch (Exception e) {
                Timber.e(e);
            }

        }

    }

    private void openVcaCasplanToAddVulnarabilities(String dateId) {
        Intent i = new Intent(HTSDetailsActivity.this, CasePlan.class);
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

                case "HIV Testing Links":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HIV_TESTING_LINKS);
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
                        return new ChildIndexEventClient(event, client);
                    }
                    break;
                case "HIV Testing Service":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_HIV_TESTING_SERVICE);
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
               Intent i = new Intent(HTSDetailsActivity.this, IndexRegisterActivity.class);
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
            addIndexClients.setVisibility(View.VISIBLE);
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


    public void openFormUsingFormUtils(Context context, String formName) throws JSONException {


        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject formToBeOpened;

        formToBeOpened = formUtils.getFormJson(formName);
        formToBeOpened.getJSONObject("step1").put("title", hivTestingServiceModel.getFirst_name() != null ? hivTestingServiceModel.getFirst_name() : "" + " " + hivTestingServiceModel.getLast_name() != null ? hivTestingServiceModel.getLast_name() : "" + " : " + txtAge.getText().toString() + " - " + txtGender.getText().toString());
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

                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(hivTestingServiceModel, Map.class));
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
            case "hiv_testing_service":

                formToBeOpened.put("entity_id", this.hivTestingServiceModel.getBase_entity_id());
                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(hivTestingServiceModel, Map.class));
                populateCaseworkerPhoneAndName(formToBeOpened);
                break;
            case "hiv_testing_links":
//                CoreJsonFormUtils.populateJsonForm(formToBeOpened, oMapper.convertValue(hivTestingServiceModel, Map.class));
//                populateCaseworkerPhoneAndName(formToBeOpened);
                JSONObject clientNumber = getFieldJSONObject(fields(formToBeOpened, "step1"), "client_number");

                if (clientNumber  != null) {
                    clientNumber .remove(JsonFormUtils.VALUE);
                    try {
                        clientNumber.put(JsonFormUtils.VALUE, hivTestingServiceModel.getClient_number());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;


    }

        startFormActivity(formToBeOpened);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//            switch (item.getItemId()) {
//                case R.id.refresh:
//                    finish();
//                    startActivity(getIntent());
//
//                    break;
//
//                case R.id.call:
//                    String caregiverPhoneNumber = child.getCaregiver_phone();
//                    if (!caregiverPhoneNumber.equals("")) {
//                        Toast.makeText(getApplicationContext(), "Calling Caregiver...", Toast.LENGTH_LONG).show();
//
//                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                        callIntent.setData(Uri.parse("tel:" + caregiverPhoneNumber));
//                        startActivity(callIntent);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "No number for caregiver found", Toast.LENGTH_LONG).show();
//                    }
//
//                return true;
//            case R.id.case_status:
//
//                try {
//                    openFormUsingFormUtils(HTSDetailsActivity.this, "case_status");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//
//            case R.id.delete_record:
//
//                    if(txtGender.getText().toString().equals("MALE")){
//                        gender = "his";
//                    } else {
//                        gender = "her";
//                    }
//
//                    builder.setMessage("You are about to delete this VCA and all " + gender + " forms.");
//                    builder.setNegativeButton("NO", (dialog, id) -> {
//                        //  Action for 'NO' Button
//                        dialog.cancel();
//
//                    }).setPositiveButton("YES",((dialogInterface, i) -> {
//                        FormUtils formUtils = null;
//                        try {
//                            formUtils = new FormUtils(this);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        child.setDeleted("1");
//                            JSONObject vcaScreeningForm = formUtils.getFormJson("vca_edit");
//                            try {
//                                CoreJsonFormUtils.populateJsonForm(vcaScreeningForm, new ObjectMapper().convertValue(child, Map.class));
//                                vcaScreeningForm.put("entity_id", child.getBase_entity_id());
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//
//                            try {
//
//                                ChildIndexEventClient childIndexEventClient = processRegistration(vcaScreeningForm.toString());
//                                if (childIndexEventClient == null) {
//                                    return;
//                                }
//                                saveRegistration(childIndexEventClient,true);
//
//
//                            } catch (Exception e) {
//                                Timber.e(e);
//                            }
//
//
//
//                        Toasty.success(HTSDetailsActivity.this, "Deleted", Toast.LENGTH_LONG, true).show();
//                        super.onBackPressed();
//                    }));
//
//                    //Creating dialog box
//                    AlertDialog alert = builder.create();
//                    //Setting the title manually
//                    alert.setTitle("Alert");
//                    alert.show();
//
//                break;
//
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

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
                        openFormUsingFormUtils(HTSDetailsActivity.this, "vca_screening");
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
     SharedPreferences cp = PreferenceManager.getDefaultSharedPreferences(HTSDetailsActivity.this);
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
    public  void returnViewPager(){
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HTSlinksFragment());

        ViewPagerAdapterFragment adapter = new ViewPagerAdapterFragment(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("HTS Links");
    }
    private void updateTasksTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.hts_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.hts_title);
        visitTabTitle.setText("HTS Links");
        htsCount = taskTabTitleLayout.findViewById(R.id.hts_count);

        int htsLinks = HTSLinksDao.htsCount(hivTestingServiceModel.getClient_number());

        htsCount.setText(String.valueOf(htsLinks));

        mTabLayout.getTabAt(0).setCustomView(taskTabTitleLayout);
    }

    public void buildDialog(){
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("VCA Screening");
        alert.show();
    }
}
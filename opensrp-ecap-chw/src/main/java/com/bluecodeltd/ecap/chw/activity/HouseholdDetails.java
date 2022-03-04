package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
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
import com.bluecodeltd.ecap.chw.dao.CaregiverVisitationDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.HouseholdCasePlanFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdChildrenFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.HouseholdVisitsFragment;
import com.bluecodeltd.ecap.chw.model.Caregiver;
import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;
import com.bluecodeltd.ecap.chw.model.CaregiverHouseholdvisitationModel;
import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;
import com.bluecodeltd.ecap.chw.model.Child;
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
import org.smartregister.domain.UniqueId;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

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
    private RelativeLayout rvisit, rcase_plan, rassessment, rscreen, hvisit20, child_form, household_visitation_caregiver;;
    private String childId;
    public String householdId;
    public String countFemales, countMales;
    private UniqueIdRepository uniqueIdRepository;
    public Household house;
    Caregiver caregiver;
    Child child;
    ObjectMapper oMapper, householdMapper, caregiverMapper, assessmentMapper;
    CommonPersonObjectClient household;
    Random Number;
    int Rnumber;

    CaregiverAssessmentModel caregiverAssessmentModel;
    CaregiverVisitationModel caregiverVisitationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_details);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);

        childId = getIntent().getExtras().getString("childId");
        householdId = getIntent().getExtras().getString("householdId");

        caregiverAssessmentModel = CaregiverAssessmentDao.getCaregiverAssessment(householdId);
        caregiverVisitationModel = CaregiverVisitationDao.getCaregiverVisitation(householdId);

        if(childId != null){
            child = IndexPersonDao.getChildByBaseId(childId);
        }

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
        //caregiver_name
        cname = findViewById(R.id.caregiver_name);
        txtDistrict = findViewById(R.id.myaddress);
        txtVillage = findViewById(R.id.address1);
        rassessment = findViewById(R.id.cassessment);
        rcase_plan = findViewById(R.id.hcase_plan);
        //rvisit = findViewById(R.id.hh_visit);
        //hvisit20 = findViewById(R.id.hh_visit20);
        child_form = findViewById(R.id.child_form);
        household_visitation_caregiver = findViewById(R.id.household_visitation_caregiver);
        mTabLayout =  findViewById(R.id.tabs);
        mViewPager  = findViewById(R.id.viewpager);
        setupViewPager();
        updateTasksTabTitle();
        updateChildTabTitle();
        //updateCaseplanTitle();
        txtDistrict.setText(householdId);

        if(house.getCaregiver_name() == null || house.getCaregiver_name().equals("null")){

            cname.setText("No Household");

        } else {
            cname.setText(house.getCaregiver_name() + " Household");
        }

        countFemales = IndexPersonDao.countFemales(householdId);
        countMales = IndexPersonDao.countMales(householdId);

    }



    public HashMap<String, Household> getData() {
        return  populateMapWithHouse(house);

    }
    public HashMap<String, Household> populateMapWithHouse(Household houseToAdd)
    {
        HashMap<String, Household> householdHashMap= new HashMap<>();
        householdHashMap.put("house",houseToAdd);
        return householdHashMap;
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
        mTabLayout.getTabAt(2).setText("Case plans");

    }

    private void updateTasksTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.visits_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.visits_title);
        visitTabTitle.setText(this.getString(R.string.visits));
        visitTabCount = taskTabTitleLayout.findViewById(R.id.household_plans_count);
        mTabLayout.getTabAt(3).setCustomView(taskTabTitleLayout);
    }

  /*  private void updateCaseplanTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.household_plan_tab_title, null);
        TextView casePlanTabTitle = taskTabTitleLayout.findViewById(R.id.household_plans_title);
        casePlanTabTitle.setText("Case plans");
        casePlanTabCount = taskTabTitleLayout.findViewById(R.id.household_plans_count);
        int plans = CasePlanDao.getByIDNumberOfCaregiverCasepalns(house.getHousehold_id());        //re-visit query in Dao

        if (plans > 0)
        {
            casePlanTabCount.setText(String.valueOf(plans));
        }
        else{
            casePlanTabCount.setText("0");
        }
        //change valueOf to plans after query is re-visited
        mTabLayout.getTabAt(2).setCustomView(taskTabTitleLayout);
    }*/

    private void updateChildTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.child_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("MEMBERS");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);


        String children = IndexPersonDao.countChildren(householdId);

        childTabCount.setText(children);

        mTabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }


    public void onClick(View v) {
        int id = v.getId();


        switch (id) {
            case R.id.myservice:

                try {
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;

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
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;


                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    Object obj = prefs.getAll();//

                    householdMapper = new ObjectMapper();

                    indexRegisterForm = formUtils.getFormJson("hh_screening_entry");
                    indexRegisterForm.put("entity_id", this.house.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(indexRegisterForm,householdMapper.convertValue(house, Map.class));

                    indexRegisterForm.getJSONObject("step1").put("title", house.getCaregiver_name() + " Household");
                    indexRegisterForm.getJSONObject("step2").getJSONArray("fields").getJSONObject(6).put("value", "true");
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(9).getJSONArray("options").getJSONObject(0).put("value", house.getSubpop1());
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(9).getJSONArray("options").getJSONObject(1).put("value", house.getSubpop2());
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(9).getJSONArray("options").getJSONObject(2).put("value", house.getSubpop3());
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(9).getJSONArray("options").getJSONObject(3).put("value", house.getSubpop4());
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(9).getJSONArray("options").getJSONObject(4).put("value", house.getSubpop6());
                    indexRegisterForm.getJSONObject("step1").getJSONArray("fields").getJSONObject(9).getJSONArray("options").getJSONObject(5).put("value", house.getSubpop5());


                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;



            case R.id.hcase_plan:
                try {
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;

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
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;

                    assessmentMapper = new ObjectMapper();

                    indexRegisterForm = formUtils.getFormJson("hh_caregiver_assessment");

                    if(caregiverAssessmentModel == null) {
                        CoreJsonFormUtils.populateJsonForm(indexRegisterForm, oMapper.convertValue(house, Map.class));
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
                    FormUtils formUtils = new FormUtils(HouseholdDetails.this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("household_visitation_for_caregiver");
                    //openFormUsingFormUtils(IndexDetailsActivity.this,"household_visitation_for_caregiver");
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

            case R.id.child_form:

                try {

                    FormUtils formUtils = null;
                    try {
                        formUtils = new FormUtils(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    JSONObject formToBeOpened;

                    formToBeOpened = formUtils.getFormJson("family_member");

                    UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
                    String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
                    String xId = entityId.replaceFirst("^0+(?!$)", "");
                    String x = xId.replace("-", "");


                    formToBeOpened.getJSONObject("step1").getJSONArray("fields").getJSONObject(0).put("value", house.getHousehold_id());

                    Number = new Random();
                    Rnumber = Number.nextInt(900000000);
                    String newEntityId =  Integer.toString(Rnumber);


                    //******** POPULATE JSON FORM VCA UNIQUE ID ******//
                    JSONObject stepOneUniqueId = getFieldJSONObject(fields(formToBeOpened, STEP1), "unique_id");

                    if (stepOneUniqueId != null) {
                        stepOneUniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                        try {
                            stepOneUniqueId.put(JsonFormUtils.VALUE, newEntityId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,caregiverMapper.convertValue(caregiver, Map.class));
                    startFormActivity(formToBeOpened);


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



                try {
                    String  uniqueId = jsonFormObject.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).optString("value");
                    childId = uniqueId;
                    getUniqueIdRepository().close(uniqueId);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                switch (EncounterType) {

                    case "Household Screening":

                        closeFab();
                        //loadInformation(childIndexEventClient);//updates Ui data in activity

                        Toasty.success(HouseholdDetails.this, "Household Updated", Toast.LENGTH_LONG, true).show();
                        finish();
                        startActivity(getIntent());

                        break;

                    case "Family Member":

                        Toasty.success(HouseholdDetails.this, "Family Member Saved", Toast.LENGTH_LONG, true).show();
                        finish();
                        startActivity(getIntent());
                        break;
                    case "Caregiver Case Plan":
                      String dateId = jsonFormObject.getJSONObject("step1").getJSONArray("fields").getJSONObject(4).optString("value");
                        AddVulnarabilitiesToCasePlan(dateId);
                        break;
                }
             //   finish();
           //     startActivity(getIntent());

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
        //    rvisit.setVisibility(View.VISIBLE);
          //  hvisit20.setVisibility(View.VISIBLE);
            rscreen.setVisibility(View.VISIBLE);
            rassessment.setVisibility(View.VISIBLE);
            rcase_plan.setVisibility(View.VISIBLE);
            child_form.setVisibility(View.VISIBLE);
            household_visitation_caregiver.setVisibility(View.VISIBLE);

        }
    }

    public void closeFab(){
        fab.startAnimation(rotate_backward);
        isFabOpen = false;
      //  rvisit.setVisibility(View.GONE);
      //  hvisit20.setVisibility(View.GONE);
        rscreen.setVisibility(View.GONE);
        rassessment.setVisibility(View.GONE);
        rcase_plan.setVisibility(View.GONE);
        child_form.setVisibility(View.GONE);
        household_visitation_caregiver.setVisibility(View.GONE);
    }

    public void loadInformation(ChildIndexEventClient  updatedEventClient){
        house = getHousehold(householdId);
        txtDistrict.setText(updatedEventClient.getClient().getAttribute("household_id").toString());
        cname.setText(new StringBuilder().append(updatedEventClient.getClient().getAttribute("caregiver_name").toString()).append(" household").toString());

        mPagerAdapter.notifyDataSetChanged();
        HouseholdOverviewFragment mFragment = (HouseholdOverviewFragment) mPagerAdapter.getItem(mViewPager.getCurrentItem());
        mFragment.setViews();


    }
    public Household getHousehold(String householdId)
    {
        return HouseholdDao.getHousehold(householdId);
    }
}
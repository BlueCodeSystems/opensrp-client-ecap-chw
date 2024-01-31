package com.bluecodeltd.ecap.chw.activity;

import static com.bluecodeltd.ecap.chw.util.IndexClientsUtils.getFormTag;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import androidx.viewpager.widget.ViewPager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ProfileViewPagerAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao;
import com.bluecodeltd.ecap.chw.dao.PtmctMotherMonitoringDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.MotherOverviewFragment;
import com.bluecodeltd.ecap.chw.fragment.PostnatalCareFragment;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;
import com.bluecodeltd.ecap.chw.model.PtmctMotherMonitoringModel;
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

public class MotherPmtctProfileActivity extends AppCompatActivity {


    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private Toolbar toolbar;
    public ProfileViewPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private String refresh;
    private TextView childTabCount, motherName, txtAge;
    private FloatingActionButton fab;
    CommonPersonObjectClient commonPersonObjectClient, commonMother;
    ObjectMapper oMapper;
    private RelativeLayout cLayout, mLayout,ancLayout,labourLayout,postnatalLayout;
    private UniqueIdRepository uniqueIdRepository;
    public String vca_id;
    public Household family;
    public PtctMotherModel ptctMotherModel;
    public PtmctMotherMonitoringModel ptmctMotherMonitoringModel;
    Random Number;
    int Rnumber;
    ObjectMapper householdMapper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mother_pmtct_detail);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);
        mTabLayout =  findViewById(R.id.tabs);
        mViewPager  = findViewById(R.id.viewpager);
        motherName = findViewById(R.id.mother_name);
        txtAge = findViewById(R.id.mother_age);
        mLayout = findViewById(R.id.mother_form);
        cLayout = findViewById(R.id.child_form);
        ancLayout = findViewById(R.id.anc_details);
        labourLayout = findViewById(R.id.labour_details);
        postnatalLayout = findViewById(R.id.postnatal_details);
        String clientId ="";

        //commonMother = (CommonPersonObjectClient) getIntent().getSerializableExtra("mother");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clientId= extras.getString("client_id");

        }

        ptctMotherModel = PMTCTMotherDao.getPMCTMother(clientId);
        ptmctMotherMonitoringModel = PtmctMotherMonitoringDao.getPMCTMother(clientId);

        if (ptctMotherModel != null) {
            String mothersFullName = ptctMotherModel.getMothers_full_name();
            if (mothersFullName != null) {
                motherName.setText(mothersFullName);
            } else {
                motherName.setText("");
            }

            String mothersAge = ptctMotherModel.getMothers_age();
            if (mothersAge != null) {
                txtAge.setText(getClientAge(mothersAge));
            } else {
                txtAge.setText("N/A");
            }
        } else {
            // Handle the case where ptctMotherModel is null, if necessary
            motherName.setText("Name not available");
            txtAge.setText("Age not available");
        }
//           CommonPersonObject personObject = getCommonRepository("ec_pmtct_mother").findByBaseEntityId(clientId);
//           CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
//           client.setColumnmaps(personObject.getColumnmaps());
//        commonPersonObjectClient = client;
//
//        //Refresh activity using Intent
//        refresh = getIntent().getExtras().getStri HashMap<String, Child> mymap = ( (IndexDetailsActivity) requireActivity()).getData();
//        Child childIndex =mymap.get("Child")ng("1");
//
//        family = HouseholdDao.getHousehold(commonPersonObjectClient.getColumnmaps().get("household_id"));
//
//        motherName.setText(client.getColumnmaps().get("caregiver_name"));
//        String birthdate = commonPersonObjectClient.getColumnmaps().get("caregiver_birth_date");
//        String age = getAge(birthdate);
//        txtAge.setText(age);
//
       oMapper = new ObjectMapper();
//
        fab = findViewById(R.id.fabx);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

       setupViewPager();
        //updateChildTabTitle();

    }

    public HashMap<String, CommonPersonObjectClient> getData() {
        return  populateMapWithMother(commonPersonObjectClient);

    }

    public HashMap<String, CommonPersonObjectClient> populateMapWithMother(CommonPersonObjectClient commonPersonObjectClient)
    {
        HashMap<String, CommonPersonObjectClient> motherHashMap = new HashMap<>();
        motherHashMap.put("mother", commonPersonObjectClient);

        return motherHashMap;
    }

    private void updateChildTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.child_tab_title, null);
        TextView visitTabTitle = taskTabTitleLayout.findViewById(R.id.children_title);
        visitTabTitle.setText("CHILDREN");
        childTabCount = taskTabTitleLayout.findViewById(R.id.children_count);


        String children = IndexPersonDao.countChildren(commonPersonObjectClient.getColumnmaps().get("household_id"));

        childTabCount.setText(children);

        mTabLayout.getTabAt(1).setCustomView(taskTabTitleLayout);
    }


    private void setupViewPager(){
        mPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new MotherOverviewFragment());
        mPagerAdapter.addFragment(new PostnatalCareFragment());


        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Overview");
        mTabLayout.getTabAt(1).setText("Postnatal");

    }
    private String getClientAge(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-u");
        LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
        LocalDate today =LocalDate.now();
        Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
        if(periodBetweenDateOfBirthAndNow.getYears() >0)
        {
            if(periodBetweenDateOfBirthAndNow.getYears() == 1){

                return periodBetweenDateOfBirthAndNow.getYears() +" Year Old";

            } else {
                return periodBetweenDateOfBirthAndNow.getYears() +" Years Old";
            }

        }
        else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0){

            if (periodBetweenDateOfBirthAndNow.getMonths() == 1){

                return periodBetweenDateOfBirthAndNow.getMonths() +" Month Old";

            } else {
                return periodBetweenDateOfBirthAndNow.getMonths() +" Months Old";
            }

        }
        else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() ==0){
            return periodBetweenDateOfBirthAndNow.getDays() +" Days Old";
        }
        else return "Age Not Set";
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

    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.mother_form:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this,"mother_pmtct");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.child_form:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this,"mother_pmtct_monitoring");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.anc_details:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this,"anc_details");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.labour_details:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this,"labour_delivery");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.postnatal_details:

                try {
                    openFormUsingFormUtils(MotherPmtctProfileActivity.this,"postnatal_care");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.fabx:

                animateFAB();

                break;

            case R.id.hh_prof:

                if(childTabCount == null || childTabCount.getText().toString().equals("0")){

                    Toasty.warning(MotherPmtctProfileActivity.this, "Household should have at least 1 Child", Toast.LENGTH_LONG, true).show();

                } else {

                    Intent intent = new Intent(this, HouseholdDetails.class);
                    intent.putExtra("householdId",  commonPersonObjectClient.getColumnmaps().get("household_id"));
                    startActivity(intent);


                }

                break;

        }
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

            case "mother_pmtct":

                householdMapper = new ObjectMapper();

                formToBeOpened.put("entity_id",  this.ptctMotherModel.getBase_entity_id());
              //  formToBeOpened.getJSONObject("step1").put("title", this.commonPersonObjectClient.getColumnmaps().get("caregiver_name") + " "  + txtAge.getText().toString());
                CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptctMotherModel, Map.class));

                break;

            case "mother_pmtct_monitoring":
                householdMapper = new ObjectMapper();
                if(ptmctMotherMonitoringModel == null){
                    formToBeOpened.put("entity_id",  this.ptctMotherModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptctMotherModel, Map.class));

                } else {
                    formToBeOpened.put("entity_id",  this.ptmctMotherMonitoringModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptmctMotherMonitoringModel, Map.class));

                }

                break;
            case "anc_details":
                householdMapper = new ObjectMapper();
                if(ptmctMotherMonitoringModel == null){
                    formToBeOpened.put("entity_id",  this.ptctMotherModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptctMotherModel, Map.class));

                }
//                else {
//                    formToBeOpened.put("entity_id",  this.ptmctMotherMonitoringModel.getBase_entity_id());
//                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptmctMotherMonitoringModel, Map.class));
//                }

                break;

            case "labour_delivery":
                householdMapper = new ObjectMapper();
                if(ptmctMotherMonitoringModel == null){
                    formToBeOpened.put("entity_id",  this.ptctMotherModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptctMotherModel, Map.class));

                }
//                else {
//                    formToBeOpened.put("entity_id",  this.ptmctMotherMonitoringModel.getBase_entity_id());
//                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptmctMotherMonitoringModel, Map.class));
//                }

                break;
            case "postnatal_care":
                householdMapper = new ObjectMapper();
                PtctMotherModel model = new PtctMotherModel();
                model.setPmtct_id(ptctMotherModel.getPmtct_id());
//                if(ptmctMotherMonitoringModel == null){
//                    formToBeOpened.put("entity_id",  this.ptctMotherModel.getBase_entity_id());
                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(model, Map.class));

//                }
//                else {
//                    formToBeOpened.put("entity_id",  this.ptmctMotherMonitoringModel.getBase_entity_id());
//                    CoreJsonFormUtils.populateJsonForm(formToBeOpened,householdMapper.convertValue(ptmctMotherMonitoringModel, Map.class));
//                }

                break;


        }
        startFormActivity(formToBeOpened);

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


            if(!jsonFormObject.optString("entity_id").isEmpty()){
                is_edit_mode = true;
            }

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, is_edit_mode);

                getUniqueIdRepository().close(vca_id);

                Toasty.success(MotherPmtctProfileActivity.this, "Form Saved", Toast.LENGTH_LONG, true).show();

                finish();
                startActivity(getIntent());

            } catch (Exception e) {
                Timber.e(e);
            }

        }

        getData();
        setupViewPager();
//        updateChildTabTitle();
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

    public ChildIndexEventClient processRegistration(String jsonString) {

        try {
            JSONObject formJsonObject = new JSONObject(jsonString);

            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);

            String entityId = formJsonObject.optString("entity_id");

            if (entityId.isEmpty()) {
                entityId = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }


            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);

            JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);

            switch (encounterType) {
                case "Mother Pmtct":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_mother");
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Mother Pmtct Monitoring":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, "ec_pmtct_mother_monitoring");
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
            mLayout.setVisibility(View.VISIBLE);
            cLayout.setVisibility(View.VISIBLE);
            ancLayout.setVisibility(View.VISIBLE);
            labourLayout.setVisibility(View.VISIBLE);
            postnatalLayout.setVisibility(View.VISIBLE);

        }
    }

    public void closeFab(){
        fab.startAnimation(rotate_backward);
        isFabOpen = false;
        cLayout.setVisibility(View.GONE);
        mLayout.setVisibility(View.GONE);
        ancLayout.setVisibility(View.GONE);
        labourLayout.setVisibility(View.GONE);
        postnatalLayout.setVisibility(View.GONE);
    }
    public HashMap<String, PtctMotherModel> getClientDetails() {

        HashMap<String, PtctMotherModel> map = new HashMap<>();

        map.put("client",ptctMotherModel);

        return map;

    }

}
package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.HouseholdServicesOnlyAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.dao.HouseholdServiceReportDao;
import com.bluecodeltd.ecap.chw.dao.IndexMotherDao;
import com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao;
import com.bluecodeltd.ecap.chw.dao.newCaregiverDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.GraduationBenchmarkModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.HouseholdServiceReportModel;
import com.bluecodeltd.ecap.chw.model.newCaregiverModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.MotherIndexEnrollmentUtils;
import com.bluecodeltd.ecap.chw.util.PmtctEnrollmentUtils;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class HouseholdServicesOnlyActivity extends AppCompatActivity {
    private com.bluecodeltd.ecap.chw.databinding.ActivityHouseholdServicesOnlyBinding binding;
    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<HouseholdServiceReportModel> familyServiceList = new ArrayList<>();
    private LinearLayout linearLayout;
    private TextView cname, hh_id,updatedCaregiverName;

    private Toolbar toolbar;
    String intent_householdId;
    newCaregiverModel updatedCaregiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.bluecodeltd.ecap.chw.databinding.ActivityHouseholdServicesOnlyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbarx;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);

        recyclerView = binding.hhrecyclerView;
        linearLayout = binding.serviceContainer;
        cname = binding.caregiverName;
        hh_id = binding.hhid;
        updatedCaregiverName = binding.updatedCaregiverName;

        Bundle extras = getIntent().getExtras();
        String intent_cname = null;
        if (extras != null) {
            intent_householdId = extras.getString("householdId");
            intent_cname = extras.getString("cname");
        }

        if (!TextUtils.isEmpty(intent_householdId)) {
            hh_id.setText(intent_householdId);
        }
        if (!TextUtils.isEmpty(intent_cname)) {
            cname.setText(intent_cname);
        }

        updatedCaregiverName.setVisibility(View.GONE);

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(HouseholdServicesOnlyActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new HouseholdServicesOnlyAdapter(familyServiceList, HouseholdServicesOnlyActivity.this);
        recyclerView.setAdapter(recyclerViewadapter);
        try { if (recyclerViewadapter != null) recyclerViewadapter.notifyDataSetChanged(); } catch (Exception ignored) {}

        linearLayout.setVisibility(View.VISIBLE);

        final String hid = intent_householdId;
        Threading.io(() -> {
            newCaregiverModel updated = null;
            ArrayList<HouseholdServiceReportModel> results = new ArrayList<>();
            try {
                if (!TextUtils.isEmpty(hid)) {
                    updated = newCaregiverDao.getNewCaregiverById(hid);
                }
            } catch (Exception ignored) {}
            try {
                if (!TextUtils.isEmpty(hid)) {
                    results.addAll(HouseholdServiceReportDao.getServicesForHouseholdOnly(hid));
                }
            } catch (Exception ignored) {}

            final newCaregiverModel finalUpdated = updated;
            Threading.main(() -> {
                updatedCaregiver = finalUpdated;
                if (updatedCaregiver != null && !TextUtils.isEmpty(updatedCaregiver.getNew_caregiver_name())) {
                    updatedCaregiverName.setVisibility(View.VISIBLE);
                    updatedCaregiverName.setText("Current: " + updatedCaregiver.getNew_caregiver_name() + " Household");
                } else {
                    updatedCaregiverName.setVisibility(View.GONE);
                }

                familyServiceList.clear();
                familyServiceList.addAll(results);
                try { if (recyclerViewadapter != null) recyclerViewadapter.notifyDataSetChanged(); } catch (Exception ignored) {}
                linearLayout.setVisibility(recyclerViewadapter != null && recyclerViewadapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(recyclerViewadapter);
        try { if (recyclerViewadapter != null) recyclerViewadapter.notifyDataSetChanged(); } catch (Exception ignored) {}
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        int id = v.getId();


        switch (id) {
            case R.id.services1:
                Threading.io(() -> {
                    GraduationBenchmarkModel model = null;
                    Household house = null;
                    int casePlanCount = 0;
                    try { model = HouseholdDao.getGraduationStatus(intent_householdId); } catch (Exception ignored) {}
                    try { house = HouseholdDao.getHousehold(intent_householdId); } catch (Exception ignored) {}
                    try { casePlanCount = CasePlanDao.getByIDNumberOfCaregiverCasepalns(intent_householdId); } catch (Exception ignored) {}

                    final Household finalHouse = house;
                    final int finalCasePlanCount = casePlanCount;
                    Threading.main(() -> {
                        if (finalHouse == null) {
                            showDialogBox("Household details unavailable");
                            return;
                        }
                        if (finalCasePlanCount == 0) {
                            showDialogBox("Unable to add service(s) for " + finalHouse.getCaregiver_name() + "`s household  because no Case Plan(s) have been added");
                        } else if (finalHouse.getHousehold_case_status() != null && ("0".equals(finalHouse.getHousehold_case_status()) || "2".equals(finalHouse.getHousehold_case_status()))) {
                            showDialogBox(finalHouse.getCaregiver_name() + "`s household has been inactive or de-registered");
                        } else {
                            try {
                                FormUtils formUtils = new FormUtils(this);
                                JSONObject indexRegisterForm = formUtils.getFormJson("service_report_household");
                                applyPregnantBreastfeedingVisibility(indexRegisterForm, finalHouse.getCaregiver_sex());

                                JSONObject status = getFieldJSONObject(fields(indexRegisterForm, "step1"), "services");
                                JSONArray options = status.getJSONArray("options");

                                for (int i = 0; i < options.length(); i++) {
                                    JSONObject option = options.getJSONObject(i);
                                    if ("caregiver".equals(option.getString("key"))) {
                                        options.remove(i);
                                        break;
                                    }
                                }

                                JSONObject cId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "household_id");
                                cId.put("value", hh_id.getText().toString());

                                JSONObject hivStatus = getFieldJSONObject(fields(indexRegisterForm, STEP1), "is_hiv_positive");
                                hivStatus.put("value", finalHouse.getCaregiver_hiv_status());

                                startFormActivity(indexRegisterForm);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                });


                break;
        }
    }
    public void showDialogBox(String message){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();

        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        dialogMessage.setText(message);

        android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(v -> dialog.dismiss());

    }
    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Service Report");
        form.setHideSaveLabel(true);
        form.setNextLabel(getString(R.string.next));
        form.setPreviousLabel(getString(R.string.previous));
        form.setSaveLabel(getString(R.string.submit));
        form.setActionBarBackground(org.smartregister.R.color.dark_grey);
        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }

    private static boolean isFemaleCaregiver(String caregiverSex) {
        return caregiverSex != null && caregiverSex.trim().equalsIgnoreCase("female");
    }

    private static void applyPregnantBreastfeedingVisibility(JSONObject form, String caregiverSex) {
        if (isFemaleCaregiver(caregiverSex)) {
            return;
        }
        try {
            JSONArray formFields = fields(form, STEP1);
            if (formFields == null) {
                return;
            }
            for (int i = 0; i < formFields.length(); i++) {
                JSONObject field = formFields.getJSONObject(i);
                if ("pregnant_breastfeeding".equals(field.optString("key"))) {
                    formFields.remove(i);
                    break;
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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

            if (!jsonFormObject.optString("entity_id").isEmpty()) {
                is_edit_mode = true;
            }
            String EncounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");


            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, is_edit_mode,EncounterType);


                switch (EncounterType) {

                    case "Household Service Report":

                        if (!is_edit_mode) {
                            maybeEnrollPmtctMother(jsonFormObject);
                            maybeEnrollMotherIndex(jsonFormObject);
                        }
                        Toasty.success(HouseholdServicesOnlyActivity.this, "Service Report Saved", Toast.LENGTH_LONG, true).show();
                        refreshData();


                        break;

                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
    private void refreshData() {
        familyServiceList.clear();
        List<HouseholdServiceReportModel> updatedList = HouseholdServiceReportDao.getServicesByHousehold(intent_householdId);
        familyServiceList.addAll(updatedList);
        try { if (recyclerViewadapter != null) recyclerViewadapter.notifyDataSetChanged(); } catch (Exception ignored) {}
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

            FormTag formTag = getFormTag();
            String tableName;
            if ("Mother Pmtct".equalsIgnoreCase(encounterType)) {
                tableName = Constants.EcapClientTable.EC_MOTHER_PMTCT;
                PmtctEnrollmentUtils.alignPmtctIdWithHouseholdId(fields);
            } else if ("Mother Register".equalsIgnoreCase(encounterType)) {
                tableName = Constants.EcapClientTable.EC_MOTHER_INDEX;
            } else {
                tableName = "ec_household_service_report";
            }
            Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId, encounterType, tableName);
            tagSyncMetadata(event);
            Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
            return new ChildIndexEventClient(event, client);



        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    private void maybeEnrollPmtctMother(JSONObject serviceForm) {
        if (serviceForm == null || !PmtctEnrollmentUtils.isEligible(serviceForm)) {
            return;
        }
        if (intent_householdId == null || intent_householdId.trim().isEmpty()) {
            return;
        }

        Threading.io(() -> {
            try {
                if (PMTCTMotherDao.hasMotherRecord(intent_householdId)) {
                    return;
                }
                Household household = HouseholdDao.getHousehold(intent_householdId);
                if (household == null) {
                    return;
                }
                if (household.getHousehold_id() == null || household.getHousehold_id().trim().isEmpty()) {
                    household.setHousehold_id(intent_householdId);
                }
                if (household.getHousehold_id() == null || household.getHousehold_id().trim().isEmpty()) {
                    return;
                }
                String serviceDate = PmtctEnrollmentUtils.resolveServiceDate(serviceForm);
                JSONObject pmtctForm = PmtctEnrollmentUtils.buildMotherPmtctForm(this, household, serviceDate);
                if (pmtctForm == null) {
                    return;
                }
                ChildIndexEventClient childIndexEventClient = processRegistration(pmtctForm.toString());
                if (childIndexEventClient == null) {
                    return;
                }
                saveRegistration(childIndexEventClient, false, "Mother Pmtct");
            } catch (Exception e) {
                Timber.e(e);
            }
        });
    }

    private void maybeEnrollMotherIndex(JSONObject serviceForm) {
        if (serviceForm == null || intent_householdId == null || intent_householdId.trim().isEmpty()) {
            return;
        }
        Threading.io(() -> {
            try {
                if (IndexMotherDao.hasIndexMother(intent_householdId)) {
                    return;
                }
                Household household = HouseholdDao.getHousehold(intent_householdId);
                if (household == null) {
                    return;
                }
                if (household.getHousehold_id() == null || household.getHousehold_id().trim().isEmpty()) {
                    household.setHousehold_id(intent_householdId);
                }
                String serviceDate = PmtctEnrollmentUtils.resolveServiceDate(serviceForm);
                if (!MotherIndexEnrollmentUtils.isEligible(serviceForm, household, serviceDate)) {
                    return;
                }
                JSONObject motherIndexForm = MotherIndexEnrollmentUtils.buildMotherIndexForm(this, household, serviceDate);
                if (motherIndexForm == null) {
                    return;
                }
                ChildIndexEventClient childIndexEventClient = processRegistration(motherIndexForm.toString());
                if (childIndexEventClient == null) {
                    return;
                }
                saveRegistration(childIndexEventClient, false, "Mother Register");
            } catch (Exception e) {
                Timber.e(e);
            }
        });
    }

    public boolean saveRegistration(ChildIndexEventClient childIndexEventClient, boolean isEditMode,String encounterType) {

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

                    // Refresh the data on the main thread
                    runOnUiThread(this::refreshData);


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
    public void refresh(){
        finish();
        startActivity(getIntent());
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HouseholdDetails.class);
        intent.putExtra("householdId", intent_householdId);
        startActivity(intent);
        finish();
    }
}

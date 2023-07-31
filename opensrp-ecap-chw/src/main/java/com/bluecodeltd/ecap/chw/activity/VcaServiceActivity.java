package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.bluecodeltd.ecap.chw.adapter.VCAServiceAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.VCAServiceReportDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.VCAServiceModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.rey.material.widget.Button;
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

public class VcaServiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<VCAServiceModel> familyServiceList = new ArrayList<>();
    private LinearLayout linearLayout;
    private TextView vcaname,hh_id;

    private Button hh_services_link;

    private Toolbar toolbar;
    public String hivstatus, household_id,c_name,intent_vcaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vca_service);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);

        recyclerView = findViewById(R.id.hhrecyclerView);
        linearLayout = findViewById(R.id.service_container);
        vcaname = findViewById(R.id.caregiver_name);
        hh_id = findViewById(R.id.hhid);
        hh_services_link = findViewById(R.id.hh_service_link);
        HouseholdLinkFromVca();

         intent_vcaid = getIntent().getExtras().getString("vcaid");
        String intent_cname = getIntent().getExtras().getString("vcaname");
        hivstatus = getIntent().getExtras().getString("hivstatus");
        household_id = getIntent().getExtras().getString("hh_id");
        c_name = getIntent().getExtras().getString("vcaname");



        hh_id.setText(intent_vcaid);
        vcaname.setText(intent_cname);


        familyServiceList.addAll(VCAServiceReportDao.getServicesByVCAID(intent_vcaid));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(VcaServiceActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new VCAServiceAdapter(familyServiceList, VcaServiceActivity.this);
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();

        if (recyclerViewadapter.getItemCount() > 0){

            linearLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        int id = v.getId();


        switch (id) {
            case R.id.services1:
                CaseStatusModel caseStatusModel = IndexPersonDao.getCaseStatus(intent_vcaid);
                if(CasePlanDao.checkCasePlan(intent_vcaid) == 0){
                    Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.dialog_layout);
                    dialog.show();

                    TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
                    dialogMessage.setText("Unable to add service(s) for "+caseStatusModel.getFirst_name() + " " + caseStatusModel.getLast_name() + " because no Case Plan(s) have been added");

                    android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
                    dialogButton.setOnClickListener(view -> dialog.dismiss());

                } else{
                    if( caseStatusModel.getCase_status().equals("0") ||  caseStatusModel.getCase_status().equals("2")) {
                        Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.dialog_layout);
                        dialog.show();

                        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
                        dialogMessage.setText(caseStatusModel.getFirst_name() + " " + caseStatusModel.getLast_name() + " was either de-registered or inactive in the program");

                        android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
                        dialogButton.setOnClickListener(view -> dialog.dismiss());

                    } else {
                        try {
                            FormUtils formUtils = new FormUtils(this);
                            JSONObject indexRegisterForm;

                            indexRegisterForm = formUtils.getFormJson("service_report_vca");

                            JSONObject cId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
                            cId.put("value",hh_id.getText().toString());

                            JSONObject hiv = getFieldJSONObject(fields(indexRegisterForm, STEP1), "is_hiv_positive");
                            hiv.put("value",hivstatus);


                            startFormActivity(indexRegisterForm);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }




                break;
        }
    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Service Report");
        form.setHideSaveLabel(true);
        form.setNextLabel(getString(R.string.next));
        form.setPreviousLabel(getString(R.string.previous));
        form.setSaveLabel(getString(R.string.submit));
        form.setActionBarBackground(R.color.dark_grey);
        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

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

                    case "VCA Service Report":
                      Toasty.success(VcaServiceActivity.this, "Service Report Saved", Toast.LENGTH_LONG, true).show();
                      familyServiceList.clear();
                      familyServiceList.addAll(VCAServiceReportDao.getServicesByVCAID(intent_vcaid));

                        break;

                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
        finish();
        startActivity(getIntent());
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
            Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,encounterType, "ec_vca_service_report");
            tagSyncMetadata(event);
            Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
            return new ChildIndexEventClient(event, client);

        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
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
                        JSONObject mergedClientJsonObject = org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
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
    private void refreshData() {
        familyServiceList.clear();
        List<VCAServiceModel> updatedList = VCAServiceReportDao.getServicesByVCAID(intent_vcaid);
        familyServiceList.addAll(updatedList);
        recyclerViewadapter.notifyDataSetChanged();
    }

    public void HouseholdLinkFromVca(){

        hh_services_link.setOnClickListener(v->{

            if (v.getId() == R.id.hh_service_link) {

                Intent i = new Intent(this, HouseholdServiceActivity.class);
                i.putExtra("householdId", household_id);
                i.putExtra("cname", c_name);
                startActivity(i);

            }

        });
    }
}
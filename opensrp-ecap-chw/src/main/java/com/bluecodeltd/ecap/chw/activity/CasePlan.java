package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.DomainPlanAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class CasePlan extends AppCompatActivity {


    private RecyclerView recyclerView;
    DomainPlanAdapter recyclerViewadapter;
    private ArrayList<CasePlanModel> domainList = new ArrayList<>();
    private Button domainBtn, domainBtn2;
    String childId, caseDate, hivStatus,case_plan_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_plan);

        recyclerView = findViewById(R.id.domainrecyclerView);
        domainBtn = findViewById(R.id.domainBtn);
        domainBtn2 = findViewById(R.id.domainBtn2);

        childId = getIntent().getExtras().getString("childId");
        caseDate = getIntent().getExtras().getString("dateId");
        hivStatus = getIntent().getExtras().getString("hivStatus");
        case_plan_id = getIntent().getExtras().getString("case_plan_id");

        fetchData();

    }

    public void fetchData() {
        domainList.clear();
        domainList.addAll(IndexPersonDao.getDomainsById(childId, caseDate));

        if (recyclerViewadapter == null) {
            RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(eLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerViewadapter = new DomainPlanAdapter(domainList, CasePlan.this, "domain");
            recyclerView.setAdapter(recyclerViewadapter);

            recyclerViewadapter.setOnDataUpdateListener(() -> runOnUiThread(() -> {
                recreate();
            }));
        } else {
            recyclerViewadapter.notifyDataSetChanged();
        }

        if (recyclerViewadapter.getItemCount() > 0) {
            domainBtn.setVisibility(View.GONE);
            domainBtn2.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        int id = v.getId();


        switch (id) {
            case R.id.domainBtn:
            case R.id.domainBtn2:
                CaseStatusModel caseStatusModel = IndexPersonDao.getCaseStatus(childId);
                if(caseStatusModel != null && caseStatusModel.getCase_status() != null) {
                    if(caseStatusModel.getCase_status().equals("0") || caseStatusModel.getCase_status().equals("2")) {
                        Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.dialog_layout);
                        dialog.show();

                        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
                        dialogMessage.setText(caseStatusModel.getFirst_name() + " " + caseStatusModel.getLast_name() + " was either de-registered or inactive in the program");

                        android.widget.Button dialogButton = dialog.findViewById(R.id.dialog_button);
                        dialogButton.setOnClickListener(va -> dialog.dismiss());
                    } else {
                        try {
                            FormUtils formUtils = new FormUtils(CasePlan.this);
                            JSONObject indexRegisterForm = formUtils.getFormJson("domain");

                            JSONObject cId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
                            cId.put("value",childId);

                            JSONObject cDate = getFieldJSONObject(fields(indexRegisterForm, STEP1), "case_plan_date");
                            cDate.put("value", caseDate);

                            JSONObject casePlanId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "case_plan_id");
                            casePlanId.put("value", case_plan_id);

                            if(hivStatus == null || !hivStatus.equals("yes")){
                                JSONArray domainType = getFieldJSONObject(fields(indexRegisterForm, STEP1), "type").getJSONArray("options");
                                domainType.remove(0);
                            }

                            startFormActivity(indexRegisterForm);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.e("CasePlan", "caseStatusModel or caseStatusModel.getCase_status() is null");
                }

                break;
        }
    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Vulnerability Identified");
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

            if (!jsonFormObject.optString("entity_id").isEmpty()) {
                is_edit_mode = true;
            }
            String EncounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");

            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, is_edit_mode, EncounterType);

                switch (EncounterType) {

                    case "Domain":
                        Toasty.success(CasePlan.this, "Vulnerability Saved", Toast.LENGTH_LONG, true).show();
                        recreate();
                        refresh();
                        break;

                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
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
                case "Domain":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_VCA_CASE_PLAN_DOMAIN);
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



}
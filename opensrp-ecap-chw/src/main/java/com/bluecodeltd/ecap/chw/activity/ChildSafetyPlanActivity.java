package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ChildSafetyPlanAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.ChildSafetyPlanDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.ChildSafetyPlanModel;
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

public class ChildSafetyPlanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    private ArrayList<ChildSafetyPlanModel> childSafetyPlanList = new ArrayList<>();
    private LinearLayout linearLayout;
    private TextView vcaname,hh_id;

    private Toolbar toolbar;
    public String hivstatus, household_id, intent_vcaid;
    private Button child_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_safety_plan);

        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);

        recyclerView = findViewById(R.id.hhrecyclerView);
        linearLayout = findViewById(R.id.child_container);
        vcaname = findViewById(R.id.caregiver_name);
        hh_id = findViewById(R.id.hhid);
        child_plan = findViewById(R.id.child_plan);

        intent_vcaid = getIntent().getExtras().getString("vca_id");
        String intent_cname = getIntent().getExtras().getString("vca_name");


        hh_id.setText("VCA ID : " + intent_vcaid);
        vcaname.setText(intent_cname);

        childSafetyPlanList.addAll(ChildSafetyPlanDao.getChildSafetyPlanModel(intent_vcaid));

        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(ChildSafetyPlanActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new ChildSafetyPlanAdapter(childSafetyPlanList, ChildSafetyPlanActivity.this);
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();


        if (recyclerViewadapter.getItemCount() > 0){

            linearLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ArrayList<ChildSafetyPlanModel> reloadChildSafetyPlanList = new ArrayList<>();
       // childSafetyPlanList.addAll(ChildSafetyPlanDao.getChildSafetyPlanModel(intent_vcaid));
        reloadChildSafetyPlanList.addAll(ChildSafetyPlanDao.getChildSafetyPlanModel(intent_vcaid));
        if(reloadChildSafetyPlanList.size() != 0)
        {
            recyclerViewadapter = new ChildSafetyPlanAdapter(reloadChildSafetyPlanList, ChildSafetyPlanActivity.this);
        }else{

            recyclerViewadapter = new ChildSafetyPlanAdapter(childSafetyPlanList, ChildSafetyPlanActivity.this);
        }

        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();


    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        int id = v.getId();


        switch (id) {
            case R.id.child_plan:

                try {
                    FormUtils formUtils = new FormUtils(this);
                    JSONObject indexRegisterForm;

                    indexRegisterForm = formUtils.getFormJson("child_safety_plan");
                    populateCaseworkerPhoneAndName(indexRegisterForm);
                    JSONObject cId = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
                    cId.put("value",intent_vcaid);


                    startFormActivity(indexRegisterForm);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Child Safety Plan");
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

                JSONObject cpdate = getFieldJSONObject(fields(jsonFormObject, "step1"), "initial_date");
                String dateId = cpdate.optString("value");
                finish();
                startActivity(getIntent());
                Toasty.success(ChildSafetyPlanActivity.this, "Child Safety Plan Saved", Toast.LENGTH_LONG, true).show();

                //openChildSafetyPlanAction(dateId);
//                if(encounterType.equals("Child Safety Plan"))
//                {
//
//
//                }


            } catch (Exception e) {
                Timber.e(e);
            }
        }
        //  finish();
        //  startActivity(getIntent());
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
            Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,encounterType, Constants.EcapClientTable.EC_CHILD_SAFETY_PLAN);
            tagSyncMetadata(event);
            Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId );
            return new ChildIndexEventClient(event, client);

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
    public void populateCaseworkerPhoneAndName(JSONObject formToBeOpened){
        SharedPreferences cp = PreferenceManager.getDefaultSharedPreferences(ChildSafetyPlanActivity.this);
        String caseworkerphone = cp.getString("phone", "Anonymous");

        JSONObject cphone = getFieldJSONObject(fields(formToBeOpened, "step1"), "phone");

        if (cphone  != null) {
            cphone .remove(JsonFormUtils.VALUE);
            try {
                cphone .put(JsonFormUtils.VALUE, caseworkerphone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Populate Caseworker Name
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ChildSafetyPlanActivity.this);
        String caseworker = sp.getString("caseworker_name", "Anonymous");

        JSONObject ccname = getFieldJSONObject(fields(formToBeOpened, "step1"), "caseworker_name");

        if (ccname != null) {
            ccname.remove(JsonFormUtils.VALUE);
            try {
                ccname.put(JsonFormUtils.VALUE, caseworker);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
     public void loopThroughChildrensCaseplansList(){

     }

    public void openChildSafetyPlanAction(String dateId) {

//        Intent i = new Intent(ChildSafetyPlanActivity.this, ChildSafetyPlanActions.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("child_ID", intent_vcaid);
//        bundle.putString("action_date",dateId);
//        i.putExtras(bundle);
//        startActivity(i);
        finish();
        startActivity(getIntent());
        Toasty.success(ChildSafetyPlanActivity.this, "Child Safety Plan Saved", Toast.LENGTH_LONG, true).show();


    }

}
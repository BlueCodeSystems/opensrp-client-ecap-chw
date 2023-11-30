package com.bluecodeltd.ecap.chw.activity;

import static org.smartregister.opd.utils.OpdJsonFormUtils.tagSyncMetadata;

import android.annotation.SuppressLint;
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
import com.bluecodeltd.ecap.chw.adapter.ShowHouseholdReferralsAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.ReferralDao;
import com.bluecodeltd.ecap.chw.dao.newCaregiverDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.ReferralModel;
import com.bluecodeltd.ecap.chw.model.newCaregiverModel;
import com.bluecodeltd.ecap.chw.util.Constants;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class ShowHouseholdReferralActivity extends AppCompatActivity {

    public String household_id, intent_household_id;
    RecyclerView.Adapter recyclerViewadapter;
    private RecyclerView recyclerView;
    private final ArrayList<ReferralModel> referralList = new ArrayList<>();
    private LinearLayout linearLayout;
    private TextView vcaname, hh_id, txtReferral,txtBold,updatedCaregiverName;
    private Toolbar toolbar;

    newCaregiverModel updatedCaregiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_household_referrals);
        toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationMenu.getInstance(this, null, toolbar);


        recyclerView = findViewById(R.id.referralRecyclerView);
        linearLayout = findViewById(R.id.child_container);
        vcaname = findViewById(R.id.caregiver_name);
        hh_id = findViewById(R.id.hhid);
        txtReferral = findViewById(R.id.txtNoReferral);

        updatedCaregiverName = findViewById(R.id.updated_caregiver_name);

        Bundle bundle = getIntent().getExtras();
        intent_household_id = bundle.getString("householdId");
        String intent_cname = bundle.getString("householdName");


        if (intent_household_id != null) {
            hh_id.setText("Household ID : " + intent_household_id);
        } else {
            hh_id.setText("Household ID : Not available");
        }

        if (intent_cname != null) {
            vcaname.setText(intent_cname + " Household");
            txtReferral.setText("No referrals have been added for " + intent_cname + " household");
        } else {
            vcaname.setText("Household");
            txtReferral.setText("No referrals have been added for this household");
        }



        updatedCaregiver = newCaregiverDao.getNewCaregiverById(intent_household_id);

        if((updatedCaregiver.getHousehold_case_status() != null && updatedCaregiver.getHousehold_case_status().equals("Update Caregiver Details")) || (updatedCaregiver.getHousehold_case_status() != null && updatedCaregiver.getHousehold_case_status().equals("0") && updatedCaregiver.getNew_caregiver_name() != null && !updatedCaregiver.getNew_caregiver_name().isEmpty())){

            updatedCaregiverName.setVisibility(View.VISIBLE);
            updatedCaregiverName.setText("Current: "+ updatedCaregiver.getNew_caregiver_name()+" Household");

        }


        referralList.addAll(ReferralDao.getReferralsByHouseholdID(intent_household_id));
        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(ShowHouseholdReferralActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(eLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new ShowHouseholdReferralsAdapter(referralList, ShowHouseholdReferralActivity.this);
        recyclerView.setAdapter(recyclerViewadapter);
        recyclerViewadapter.notifyDataSetChanged();

        if (recyclerViewadapter.getItemCount() > 0) {

            linearLayout.setVisibility(View.GONE);
        }
    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Referral Form");
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


            try {

                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, false);

                Toasty.success(ShowHouseholdReferralActivity.this, "Referral Updated", Toast.LENGTH_LONG, true).show();

            } catch (Exception e) {
                Timber.e(e);
            }
        }
        finish();
        startActivity(getIntent());
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

    public AllSharedPreferences getAllSharedPreferences() {
        return ChwApplication.getInstance().getContext().allSharedPreferences();
    }

    private ClientProcessorForJava getClientProcessorForJava() {
        return ChwApplication.getInstance().getClientProcessorForJava();
    }
}
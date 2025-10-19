package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ChildSafetyActionAdapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.ChildSafetyActionDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.model.ChildSafetyActionModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.FormCache;
import com.bluecodeltd.ecap.chw.util.FormLoadingDialog;
import com.bluecodeltd.ecap.chw.util.Threading;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;
import com.bluecodeltd.ecap.chw.util.Threading;

public class ChildSafetyPlanActions extends AppCompatActivity {


    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ChildSafetyActionAdapter recyclerViewadapter;
    private final ArrayList<ChildSafetyActionModel> actionList = new ArrayList<>();
    private Button actionBtn, actionBtn2;
    String vcaName, childId, actionDate;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private interface FormModifier {
        void apply(JSONObject form) throws Exception;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_safety_actions);

        recyclerView = findViewById(R.id.actionrecyclerView);
        actionBtn = findViewById(R.id.actionBtn);
        actionBtn2 = findViewById(R.id.actionBtn2);

        childId = getIntent().getExtras().getString("vca_id");
        vcaName = getIntent().getExtras().getString("vca_name");
        actionDate = getIntent().getExtras().getString("action_date");

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewadapter = new ChildSafetyActionAdapter(actionList, this);
        recyclerViewadapter.setOnDataUpdateListener(() -> uiHandler.post(() -> loadActions(true)));
        recyclerView.setAdapter(recyclerViewadapter);

        FormCache.warmFormAsync(this, "child_safety_action");
        loadActions(false);

    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        int id = v.getId();


        switch (id) {
            case R.id.actionBtn:
            case R.id.actionBtn2:

                launchFormAsync("child_safety_action", form -> {
                    form.getJSONObject("step1").getJSONArray("fields").getJSONObject(1).put("value", actionDate);
                    JSONObject cId = getFieldJSONObject(fields(form, STEP1), "unique_id");
                    cId.put("value", childId);
                });

                break;
        }
    }

    public void startFormActivity(JSONObject jsonObject) {

        Form form = new Form();
        form.setWizard(false);
        form.setName("Safety Plan Actions");
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


            } catch (Exception e) {
                Timber.e(e);
            }
        }
        Toasty.success(ChildSafetyPlanActions.this, "Child Safety Action Saved", Toast.LENGTH_LONG, true).show();
        loadActions(true);
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
                case "Child Safety Actions":

                    if (fields != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CHILD_SAFETY_ACTION);
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

                    uiHandler.post(() -> loadActions(true));

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

    private void launchFormAsync(String formName, FormModifier modifier) {
        AlertDialog loading = FormLoadingDialog.show(this);
        Threading.io(() -> {
            try {
                JSONObject form = FormCache.obtainFormTemplate(this, formName);
                if (modifier != null) {
                    modifier.apply(form);
                }
                Threading.main(() -> {
                    FormLoadingDialog.dismiss(loading);
                    startFormActivity(form);
                });
            } catch (Exception e) {
                Timber.e(e);
                Threading.main(() -> {
                    FormLoadingDialog.dismiss(loading);
                    Toasty.error(this, "Unable to open form", Toast.LENGTH_LONG, true).show();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent directToSafetyPlans = new Intent(getApplicationContext(), ChildSafetyPlanActivity.class);
        directToSafetyPlans.putExtra("vca_id",childId);
        directToSafetyPlans.putExtra("vca_name",vcaName);
        directToSafetyPlans.putExtra("action_date",actionDate);
        startActivity(directToSafetyPlans);
        finish();
    }

    private void loadActions(boolean maintainScroll) {
        if (recyclerViewadapter == null) {
            return;
        }

        int firstVisible = RecyclerView.NO_POSITION;
        int offset = 0;
        if (maintainScroll && layoutManager != null) {
            firstVisible = layoutManager.findFirstVisibleItemPosition();
            View firstChild = recyclerView.getChildAt(0);
            if (firstChild != null) {
                offset = firstChild.getTop() - recyclerView.getPaddingTop();
            }
        }

        final int positionToRestore = firstVisible;
        final int offsetToRestore = offset;

        Threading.io(() -> {
            List<ChildSafetyActionModel> results = new ArrayList<>();
            try {
                results = ChildSafetyActionDao.getActionsById(childId, actionDate);
            } catch (Exception e) {
                Timber.e(e);
            }
            List<ChildSafetyActionModel> finalResults = results;
            Threading.main(() -> {
                actionList.clear();
                actionList.addAll(finalResults);
                recyclerViewadapter.notifyDataSetChanged();
                updateButtons();
                if (maintainScroll && layoutManager != null && positionToRestore != RecyclerView.NO_POSITION) {
                    layoutManager.scrollToPositionWithOffset(positionToRestore, offsetToRestore);
                }
            });
        });
    }

    private void updateButtons() {
        if (recyclerViewadapter == null) {
            return;
        }
        if (recyclerViewadapter.getItemCount() > 0) {
            actionBtn.setVisibility(View.GONE);
            actionBtn2.setVisibility(View.VISIBLE);
        } else {
            actionBtn.setVisibility(View.VISIBLE);
            actionBtn2.setVisibility(View.GONE);
        }
    }
}

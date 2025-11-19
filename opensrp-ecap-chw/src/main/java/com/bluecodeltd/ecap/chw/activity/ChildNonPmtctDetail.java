package com.bluecodeltd.ecap.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ViewPager2Adapter;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.ChildLongitudinalFollowUpDao;
import com.bluecodeltd.ecap.chw.dao.ChildPostnatalCareDao;
import com.bluecodeltd.ecap.chw.domain.ChildIndexEventClient;
import com.bluecodeltd.ecap.chw.fragment.ChildFinalOutcomeFragment;
import com.bluecodeltd.ecap.chw.fragment.ChildLongitudinalFragment;
import com.bluecodeltd.ecap.chw.fragment.ChildPostnatalFragment;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.Collections;
import java.util.Date;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import timber.log.Timber;
import es.dmoral.toasty.Toasty;

import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;
import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

/**
 * Simple child profile screen for non-PMTCT children.
 * Exposes quick links to child_final_outcome, child_longitudinal_follow_up
 * and child_postnatal_care JSON forms.
 */
public class ChildNonPmtctDetail extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_BASE_ENTITY_ID = "base_entity_id";
    public static final String EXTRA_HOUSEHOLD_ID = "household_id";
    public static final String EXTRA_UNIQUE_ID = "unique_id";

    private String baseEntityId;
    private String householdId;
    private String uniqueId;

    private RelativeLayout childFinalOutcomeLayout;
    private RelativeLayout childLongitudinalLayout;
    private RelativeLayout childPostnatalLayout;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabLayoutMediator tabMediator;
    private FloatingActionButton fab;
    private boolean isFabOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_non_pmtct_detail);

        Toolbar toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();
        if (intent != null) {
            baseEntityId = intent.getStringExtra(EXTRA_BASE_ENTITY_ID);
            householdId = intent.getStringExtra(EXTRA_HOUSEHOLD_ID);
            uniqueId = intent.getStringExtra(EXTRA_UNIQUE_ID);
        }

        childFinalOutcomeLayout = findViewById(R.id.child_final_outcome);
        childLongitudinalLayout = findViewById(R.id.child_longitudinal_follow_up);
        childPostnatalLayout = findViewById(R.id.child_postnatal_care);

        childFinalOutcomeLayout.setOnClickListener(this);
        childLongitudinalLayout.setOnClickListener(this);
        childPostnatalLayout.setOnClickListener(this);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fab = findViewById(R.id.fabx);

        setupViewPager();
    }

    public static void start(Activity activity, String baseEntityId, String householdId, String uniqueId) {
        Intent intent = new Intent(activity, ChildNonPmtctDetail.class);
        intent.putExtra(EXTRA_BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(EXTRA_HOUSEHOLD_ID, householdId);
        intent.putExtra(EXTRA_UNIQUE_ID, uniqueId);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.child_final_outcome) {
            openChildForm("child_final_outcome");
        } else if (id == R.id.child_longitudinal_follow_up) {
            openChildForm("child_longitudinal_follow_up");
        } else if (id == R.id.child_postnatal_care) {
            openChildForm("child_postnatal_care");
        } else if (id == R.id.fabx) {
            toggleFabMenu();
        }
    }

    private void toggleFabMenu() {
        if (isFabOpen) {
            closeFabMenu();
        } else {
            isFabOpen = true;
            if (childFinalOutcomeLayout != null) {
                childFinalOutcomeLayout.setVisibility(View.VISIBLE);
            }
            if (childLongitudinalLayout != null) {
                childLongitudinalLayout.setVisibility(View.VISIBLE);
            }
            if (childPostnatalLayout != null) {
                childPostnatalLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void closeFabMenu() {
        isFabOpen = false;
        if (childFinalOutcomeLayout != null) {
            childFinalOutcomeLayout.setVisibility(View.GONE);
        }
        if (childLongitudinalLayout != null) {
            childLongitudinalLayout.setVisibility(View.GONE);
        }
        if (childPostnatalLayout != null) {
            childPostnatalLayout.setVisibility(View.GONE);
        }
    }

    private void updateLongitudinalTabTitle() {
        try {
            ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(this)
                    .inflate(R.layout.visits_tab_title, null);
            TextView title = layout.findViewById(R.id.visits_title);
            TextView countView = layout.findViewById(R.id.visits_count);
            title.setText("LONGITUDINAL");

            int count = 0;
            try {
                if (uniqueId != null) {
                    count = ChildLongitudinalFollowUpDao.listByUniqueId(uniqueId).size();
                }
            } catch (Exception ignored) { }
            countView.setText(String.valueOf(count));

            if (tabLayout.getTabCount() > 0 && tabLayout.getTabAt(0) != null) {
                tabLayout.getTabAt(0).setCustomView(layout);
            }
        } catch (Exception ignored) { }
    }

    private void updatePostnatalTabTitle() {
        try {
            ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(this)
                    .inflate(R.layout.visits_tab_title, null);
            TextView title = layout.findViewById(R.id.visits_title);
            TextView countView = layout.findViewById(R.id.visits_count);
            title.setText("POSTNATAL");

            int count = 0;
            try {
                if (uniqueId != null) {
                    count = ChildPostnatalCareDao.listByUniqueId(uniqueId).size();
                }
            } catch (Exception ignored) { }
            countView.setText(String.valueOf(count));

            if (tabLayout.getTabCount() > 1 && tabLayout.getTabAt(1) != null) {
                tabLayout.getTabAt(1).setCustomView(layout);
            }
        } catch (Exception ignored) { }
    }

    private void setupViewPager() {
        if (viewPager.getAdapter() != null) return;

        java.util.List<androidx.fragment.app.Fragment> fragments = new java.util.ArrayList<>();
        fragments.add(new ChildLongitudinalFragment());
        fragments.add(new ChildPostnatalFragment());
        fragments.add(new ChildFinalOutcomeFragment());

        ViewPager2Adapter adapter = new ViewPager2Adapter(this, fragments);
        viewPager.setAdapter(adapter);

        if (tabMediator != null) {
            try {
                tabMediator.detach();
            } catch (Exception ignored) { }
        }

        tabMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Longitudinal");
            else if (position == 1) tab.setText("Postnatal");
            else if (position == 2) tab.setText("Outcome");
        });
        tabMediator.attach();

        updateLongitudinalTabTitle();
        updatePostnatalTabTitle();
    }

    private void openChildForm(String formName) {
        try {
            FormUtils formUtils = new FormUtils(this);
            JSONObject form = formUtils.getFormJson(formName);

            // Ensure IDs are visible in the form
            try {
                JSONArray flds = form.getJSONObject("step1").getJSONArray("fields");
                for (int i = 0; i < flds.length(); i++) {
                    JSONObject f = flds.getJSONObject(i);
                    String key = f.optString("key");
                    if ("household_id".equals(key) && householdId != null) {
                        f.put("value", householdId);
                    } else if ("unique_id".equals(key) && uniqueId != null) {
                        f.put("value", uniqueId);
                    }
                }
            } catch (Exception ignored) {
                // Use empty catch to avoid crashing form launch for minor mapping issues
            }

            startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startFormActivity(JSONObject jsonObject) {
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

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {

            boolean isEditMode = false;

            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);

            JSONObject jsonFormObject = null;
            try {
                jsonFormObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                Timber.e(e);
            }

            if (jsonFormObject != null && !jsonFormObject.optString("entity_id").isEmpty()) {
                isEditMode = true;
            }

            try {
                ChildIndexEventClient childIndexEventClient = processRegistration(jsonString);

                if (childIndexEventClient == null) {
                    return;
                }

                saveRegistration(childIndexEventClient, isEditMode);

                Toasty.success(this, "Form Saved", android.widget.Toast.LENGTH_LONG, true).show();

                // Refresh lists / fragments by recreating activity
                finish();
                startActivity(getIntent());

            } catch (Exception e) {
                Timber.e(e);
            }
        }
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
            JSONArray fs = org.smartregister.util.JsonFormUtils.fields(formJsonObject);

            switch (encounterType) {
                case "Final Outcome of Infant":

                    if (fs != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(
                                fs, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CHILD_FINAL_OUTCOME
                        );
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fs, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Infant Longitudinal Follow-up":

                    if (fs != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(
                                fs, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CHILD_LONGITUDINAL_FOLLOW_UP
                        );
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fs, formTag, entityId);
                        return new ChildIndexEventClient(event, client);
                    }
                    break;

                case "Post Natal Care-Infant":

                    if (fs != null) {
                        FormTag formTag = getFormTag();
                        Event event = org.smartregister.util.JsonFormUtils.createEvent(
                                fs, metadata, formTag, entityId,
                                encounterType, Constants.EcapClientTable.EC_CHILD_POSTNATAL_CARE
                        );
                        tagSyncMetadata(event);
                        Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fs, formTag, entityId);
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

                    if (isEditMode && existingClientJsonObject != null) {
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

                    java.util.List<EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
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

    public AllSharedPreferences getAllSharedPreferences() {
        return ChwApplication.getInstance().getContext().allSharedPreferences();
    }

    private ClientProcessorForJava getClientProcessorForJava() {
        return ChwApplication.getInstance().getClientProcessorForJava();
    }

    @NonNull
    private FormTag getFormTag() {
        FormTag formTag = new FormTag();
        formTag.providerId = getAllSharedPreferences().fetchRegisteredANM();
        formTag.appVersion = FamilyLibrary.getInstance().getApplicationVersion();
        formTag.databaseVersion = FamilyLibrary.getInstance().getDatabaseVersion();
        return formTag;
    }
}

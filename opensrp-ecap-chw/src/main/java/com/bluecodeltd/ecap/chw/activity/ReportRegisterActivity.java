package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.fragment.ReportRegisterFragment;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.google.android.material.navigation.NavigationBarView;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.presenter.BaseChwNotificationPresenter;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ReportRegisterActivity extends BaseRegisterActivity {

    public static final String EXTRA_REPORT_TYPE = "report_type";
    public static final String REPORT_TYPE_MALARIA = "malaria";
    public static final String REPORT_TYPE_NUTRITION = "nutrition";
    public static final String REPORT_TYPE_TB = "tb";
    public static final String REPORT_TYPE_COMMUNITY_ALERT = "community_alert";
    public static final String REPORT_TYPE_COMMUNITY = "community";
    public static final String REPORT_FORM_MALARIA = "malaria_monthly_reporting";
    public static final String REPORT_FORM_NUTRITION = "monthly_nutrition_report";
    public static final String REPORT_FORM_TB = "monthly_tb_report";
    public static final String REPORT_FORM_COMMUNITY_ALERT = "community_alert_reporting";
    public static final String REPORT_FORM_COMMUNITY = "community_alert_report";
    public static final String REPORT_FORM_ENCOUNTER_MALARIA = "Malaria Monthly Reporting";
    public static final String REPORT_FORM_ENCOUNTER_NUTRITION = "Monthly Nutrition Report";
    public static final String REPORT_FORM_ENCOUNTER_TB = "Monthly TB";
    public static final String REPORT_FORM_ENCOUNTER_COMMUNITY_ALERT = "Community Alert Reporting";
    public static final String REPORT_FORM_ENCOUNTER_COMMUNITY = "Community Alert Report";
    public static final String REPORT_TABLE_MALARIA = "ec_monthly_malaria";
    public static final String REPORT_TABLE_NUTRITION = "ec_monthly_nutrition";
    public static final String REPORT_TABLE_TB = "ec_monthly_tb";
    public static final String REPORT_TABLE_COMMUNITY_ALERT = "ec_community_alert";
    public static final String REPORT_TABLE_COMMUNITY = "ec_community_alert";

    private ReportRegisterFragment reportRegisterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(org.smartregister.R.id.register_toolbar);
        NavigationMenu menu;
        if (toolbar != null) {
            if (getSupportActionBar() == null) {
                try {
                    setSupportActionBar(toolbar);
                } catch (IllegalStateException ignored) {
                }
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            toolbar.setTitle("");
            TextView titleLabel = toolbar.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleLabel != null) {
                titleLabel.setVisibility(View.GONE);
            }
            menu = NavigationMenu.getInstance(this, null, toolbar);
            try {
                if (menu != null) {
                    androidx.drawerlayout.widget.DrawerLayout drawer = menu.getDrawer();
                    androidx.appcompat.graphics.drawable.DrawerArrowDrawable arrow =
                            new androidx.appcompat.graphics.drawable.DrawerArrowDrawable(this);
                    arrow.setColor(android.graphics.Color.WHITE);
                    toolbar.setNavigationIcon(arrow);
                    toolbar.setNavigationOnClickListener(v -> {
                        if (drawer != null) {
                            drawer.openDrawer(androidx.core.view.GravityCompat.START);
                        }
                    });
                }
            } catch (Throwable ignored) {
            }
        } else {
            menu = NavigationMenu.getInstance(this, null, null);
        }

        if (menu != null && menu.getNavigationAdapter() != null) {
            menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.REPORT_REGISTER);
        }
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseChwNotificationPresenter();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        reportRegisterFragment = new ReportRegisterFragment();
        return reportRegisterFragment;
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        // No-op
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        // No-op
    }

    @Override
    public void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        Form wizardForm = new Form();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK && data != null) {
            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
            if (jsonString == null) {
                return;
            }
            try {
                JSONObject jsonFormObject = new JSONObject(jsonString);
                String encounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, "");
                if (isMonthlyReportEncounter(encounterType)) {
                    boolean isEditMode = !jsonFormObject.optString(Constants.JSON_FORM_KEY.ENTITY_ID, "").isEmpty();
                    ReportEventClient reportEventClient = processRegistration(jsonString);
                    if (reportEventClient != null) {
                        saveRegistration(reportEventClient, isEditMode);
                    }
                }
            } catch (Exception e) {
                timber.log.Timber.e(e);
            }
        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_report_menu);
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setOnItemSelectedListener(new ChwBottomNavigationListener(this));
            bottomNavigationView.setItemIconTintList(
                    AppCompatResources.getColorStateList(this, R.color.bottom_navigation_icon_selector));
            bottomNavigationView.setItemTextColor(
                    AppCompatResources.getColorStateList(this, R.color.bottom_navigation_selector));
            bottomNavigationView.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.bottom_nav_bar_background));
            if (bottomNavigationView.getMenu().findItem(getSelectedBottomNavItemId()) != null) {
                bottomNavigationView.getMenu().findItem(getSelectedBottomNavItemId()).setChecked(true);
            }
        }
    }

    @Override
    public void startRegistration() {
        // No-op
    }

    public String getSelectedReportType() {
        String reportType = getIntent() == null ? null : getIntent().getStringExtra(EXTRA_REPORT_TYPE);
        return switch (reportType != null ? reportType : "") {
            case REPORT_TYPE_NUTRITION, REPORT_TYPE_TB, REPORT_TYPE_COMMUNITY_ALERT -> reportType;
            default -> REPORT_TYPE_MALARIA;
        };
    }

    private int getSelectedBottomNavItemId() {
        String selectedReportType = getSelectedReportType();
        if (REPORT_TYPE_NUTRITION.equals(selectedReportType)) {
            return R.id.action_report_nutrition;
        }
        if (REPORT_TYPE_TB.equals(selectedReportType)) {
            return R.id.action_report_tb;
        }
        if (REPORT_TYPE_COMMUNITY_ALERT.equals(selectedReportType)) {
            return R.id.action_report_community_alert;
        }
        return R.id.action_report_malaria;
    }

    public boolean openReportType(String reportType) {
        String selectedReportType = getSelectedReportType();
        if (java.util.Objects.equals(reportType, selectedReportType)) {
            return true;
        }

        Intent intent = new Intent(this, ReportRegisterActivity.class);
        intent.putExtra(EXTRA_REPORT_TYPE, reportType);
        startActivity(intent);
        finish();
        return true;
    }

    public boolean openReportList(String reportType) {
        Intent intent = new Intent(this, ReportSubmissionListActivity.class);
        intent.putExtra(EXTRA_REPORT_TYPE, reportType);
        startActivity(intent);
        return true;
    }

    public boolean launchReportForm(String reportType) {
        String formName = getFormName(reportType);
        try {
            JSONObject form = new FormUtils(this).getFormJson(formName);
            if (form == null) {
                return false;
            }
            populateReportDefaults(form);
            startFormActivity(form);
            return true;
        } catch (Exception e) {
            timber.log.Timber.e(e);
            return false;
        }
    }

    private String getFormName(String reportType) {
        if (REPORT_TYPE_NUTRITION.equals(reportType)) {
            return REPORT_FORM_NUTRITION;
        }
        if (REPORT_TYPE_TB.equals(reportType)) {
            return REPORT_FORM_TB;
        }
        if (REPORT_TYPE_COMMUNITY_ALERT.equals(reportType)) {
            return REPORT_FORM_COMMUNITY_ALERT;
        }
        return REPORT_FORM_MALARIA;
    }

    private void populateReportDefaults(JSONObject form) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setStep1FieldValue(form, "province", prefs.getString("province", ""));
        setStep1FieldValue(form, "district", prefs.getString("district", ""));
        setStep1FieldValue(form, "ward", prefs.getString("ward", ""));
        setStep1FieldValue(form, "facility", prefs.getString("facility", ""));
        setStep1FieldValue(form, "partner", prefs.getString("partner", ""));
        setStep1FieldValue(form, "caseworker_name", getCaseworkerName(prefs));
        setStep1FieldValue(form, "reporting_month", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        setStep1FieldValue(form, "form_id", generateFormId(prefs));
    }

    private String getCaseworkerName(SharedPreferences prefs) {
        String caseworkerName = prefs.getString("caseworker_name", "");
        if (caseworkerName != null && !caseworkerName.trim().isEmpty()) {
            return caseworkerName.trim();
        }
        String username = prefs.getString("last_logged_in_username", "");
        return username == null ? "" : username.trim();
    }

    private String generateFormId(SharedPreferences prefs) {
        String code = prefs.getString("code", "");
        int randomNumber = new Random().nextInt(100000000);
        if (code != null && !code.trim().isEmpty()) {
            return code + "/" + randomNumber;
        }
        return randomNumber + "";
    }

    private void setStep1FieldValue(JSONObject form, String key, String value) {
        JSONObject field = getFieldJSONObject(fields(form, "step1"), key);
        if (field == null) {
            return;
        }
        field.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
        try {
            field.put(org.smartregister.family.util.JsonFormUtils.VALUE, value != null ? value : "");
        } catch (org.json.JSONException e) {
            timber.log.Timber.e(e);
        }
    }

    private boolean isMonthlyReportEncounter(String encounterType) {
        return REPORT_FORM_ENCOUNTER_MALARIA.equalsIgnoreCase(encounterType)
                || REPORT_FORM_ENCOUNTER_NUTRITION.equalsIgnoreCase(encounterType)
                || REPORT_FORM_ENCOUNTER_TB.equalsIgnoreCase(encounterType)
                || REPORT_FORM_ENCOUNTER_COMMUNITY_ALERT.equalsIgnoreCase(encounterType);
    }

    private String getReportTableName(String encounterType) {
        if (REPORT_FORM_ENCOUNTER_MALARIA.equalsIgnoreCase(encounterType)) {
            return REPORT_TABLE_MALARIA;
        }
        if (REPORT_FORM_ENCOUNTER_NUTRITION.equalsIgnoreCase(encounterType)) {
            return REPORT_TABLE_NUTRITION;
        }
        if (REPORT_FORM_ENCOUNTER_TB.equalsIgnoreCase(encounterType)) {
            return REPORT_TABLE_TB;
        }
        if (REPORT_FORM_ENCOUNTER_COMMUNITY_ALERT.equalsIgnoreCase(encounterType)) {
            return REPORT_TABLE_COMMUNITY_ALERT;
        }
        return null;
    }

    private ReportEventClient processRegistration(String jsonString) {
        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            String entityId = formJsonObject.optString(Constants.JSON_FORM_KEY.ENTITY_ID);
            if (entityId.isEmpty()) {
                entityId = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }
            JSONObject metadata = formJsonObject.getJSONObject(Constants.METADATA);
            org.json.JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);
            FormTag formTag = getFormTag();
            String tableName = getReportTableName(encounterType);
            if (tableName == null) {
                return null;
            }
            Event event = org.smartregister.util.JsonFormUtils.createEvent(
                    fields,
                    metadata,
                    formTag,
                    entityId,
                    encounterType,
                    tableName
            );
            tagSyncMetadata(event);
            Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
            return new ReportEventClient(event, client);
        } catch (Exception e) {
            timber.log.Timber.e(e);
            return null;
        }
    }

    private boolean saveRegistration(ReportEventClient reportEventClient, boolean isEditMode) {
        Runnable runnable = () -> {
            Event event = reportEventClient.getEvent();
            Client client = reportEventClient.getClient();

            if (event != null && client != null) {
                try {
                    ECSyncHelper ecSyncHelper = getECSyncHelper();
                    JSONObject newClientJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));
                    JSONObject existingClientJsonObject = ecSyncHelper.getClient(client.getBaseEntityId());

                    if (isEditMode && existingClientJsonObject != null) {
                        JSONObject mergedClientJsonObject = org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject, newClientJsonObject);
                        try {
                            if (existingClientJsonObject.has("attributes") && newClientJsonObject.has("attributes")) {
                                mergedClientJsonObject.put("attributes", org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject.getJSONObject("attributes"), newClientJsonObject.getJSONObject("attributes")));
                            }
                        } catch (Exception e) {
                            timber.log.Timber.e(e, "Error merging attributes");
                        }
                        ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);
                    } else {
                        ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);
                    }

                    JSONObject eventJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));
                    ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);

                    Long lastUpdatedAtDate = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                    Date currentSyncDate = new Date(lastUpdatedAtDate);
                    List<EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                    getClientProcessorForJava().processClient(savedEvents);
                    getAllSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());

                    runOnUiThread(() -> {
                        if (reportRegisterFragment != null) {
                            reportRegisterFragment.refreshReportCards();
                        }
                        Toast.makeText(
                                this,
                                getSavedToastMessage(event.getEventType()),
                                Toast.LENGTH_SHORT
                        ).show();
                    });
                } catch (Exception e) {
                    timber.log.Timber.e(e);
                }
            }
        };

        try {
            new AppExecutors().diskIO().execute(runnable);
            return true;
        } catch (Exception e) {
            timber.log.Timber.e(e);
            return false;
        }
    }

    private ECSyncHelper getECSyncHelper() {
        return com.bluecodeltd.ecap.chw.application.ChwApplication.getInstance().getEcSyncHelper();
    }

    private AllSharedPreferences getAllSharedPreferences() {
        return com.bluecodeltd.ecap.chw.application.ChwApplication.getInstance().getContext().allSharedPreferences();
    }

    private org.smartregister.sync.ClientProcessorForJava getClientProcessorForJava() {
        return com.bluecodeltd.ecap.chw.application.ChwApplication.getInstance().getClientProcessorForJava();
    }

    private FormTag getFormTag() {
        FormTag formTag = new FormTag();
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
        return formTag;
    }

    private void tagSyncMetadata(Event event) {
        org.smartregister.chw.core.utils.CoreJsonFormUtils.tagSyncMetadata(getAllSharedPreferences(), event);
    }

    private String getSavedToastMessage(String encounterType) {
        if (REPORT_FORM_ENCOUNTER_NUTRITION.equalsIgnoreCase(encounterType)) {
            return getString(R.string.report_nutrition_saved);
        }
        if (REPORT_FORM_ENCOUNTER_TB.equalsIgnoreCase(encounterType)) {
            return getString(R.string.report_tb_saved);
        }
        if (REPORT_FORM_ENCOUNTER_COMMUNITY_ALERT.equalsIgnoreCase(encounterType)) {
            return getString(R.string.report_community_alert_saved);
        }
        return getString(R.string.report_malaria_saved);
    }

    private static class ReportEventClient {
        private final Event event;
        private final Client client;

        ReportEventClient(Event event, Client client) {
            this.event = event;
            this.client = client;
        }

        Event getEvent() {
            return event;
        }

        Client getClient() {
            return client;
        }
    }
}

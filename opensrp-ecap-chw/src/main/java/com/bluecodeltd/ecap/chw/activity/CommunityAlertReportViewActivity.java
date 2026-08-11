package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowInsetsController;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.MonthlyReportDao;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.MonthlyReportModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.google.android.material.snackbar.Snackbar;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CommunityAlertReportViewActivity extends AppCompatActivity {

    public static final String EXTRA_BASE_ENTITY_ID = "base_entity_id";
    private static final int EDIT_FORM_REQUEST = JsonFormUtils.REQUEST_CODE_GET_JSON;
    private MonthlyReportModel reportModel;
    private String baseEntityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getIntent().getIntExtra("orientation", android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (orientation != android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(orientation);
        }

        setContentView(R.layout.activity_community_alert_report_view);

        Toolbar toolbar = findViewById(R.id.report_view_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        applyLightStatusBar();
        findViewById(R.id.report_view_back_button).setOnClickListener(v -> finish());

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        loadReport();

        setupExpansionLogic();
        setupEditButton();
    }

    private void applyLightStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.WHITE);
        View decorView = window.getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = decorView.getWindowInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(flags);
        }
    }
    private void loadReport() {
        if (baseEntityId != null) {
            reportModel = MonthlyReportDao.getReport(ReportRegisterActivity.REPORT_TABLE_COMMUNITY_ALERT, baseEntityId);
        }

        if (reportModel != null) {
            populateData();
        }
    }

    private void setupExpansionLogic() {
        setupSection(R.id.header_sb_q1, R.id.content_sb_q1, R.id.icon_sb_q1);
        setupSection(R.id.header_sb_q2, R.id.content_sb_q2, R.id.icon_sb_q2);
        setupSection(R.id.header_sc, R.id.content_sc, R.id.icon_sc);
        setupSection(R.id.header_comments, R.id.content_comments, R.id.icon_comments);
    }

    private void setupSection(int headerId, int contentId, int iconId) {
        View header = findViewById(headerId);
        View content = findViewById(contentId);
        ImageView icon = findViewById(iconId);

        if (header != null && content != null && icon != null) {
            header.setOnClickListener(v -> {
                int visibility = content.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                content.setVisibility(visibility);
                icon.setImageResource(visibility == View.VISIBLE ? R.drawable.baseline_expand_less_24 : R.drawable.baseline_expand_more_24);
            });
        }
    }

    private void setupEditButton() {
        View btnEdit = findViewById(R.id.btn_edit_report);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> openEditForm());
        }
    }

    private void openEditForm() {
        if (reportModel == null) return;

        Threading.io(() -> {
            CaseStatusModel statusModel = null;
            try {
                statusModel = IndexPersonDao.getCaseStatus(reportModel.getBase_entity_id());
            } catch (Exception ignored) {
            }

            // Parse the json.form asset here too, off the main thread.
            JSONObject form = null;
            try {
                form = new FormUtils(this).getFormJson(ReportRegisterActivity.REPORT_FORM_COMMUNITY_ALERT);
            } catch (Exception ignored) {
            }

            CaseStatusModel finalCaseStatusModel = statusModel;
            JSONObject finalForm = form;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                String status = finalCaseStatusModel != null ? finalCaseStatusModel.getCase_status() : null;
                if ("0".equals(status) || "2".equals(status)) {
                    Snackbar.make(findViewById(R.id.header_card), "Beneficiary is inactive or de-registered", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (finalForm == null) {
                    Snackbar.make(findViewById(R.id.header_card), "Unable to open form", Snackbar.LENGTH_LONG).show();
                    return;
                }

                try {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    if (reportModel.getCaseworker_name() == null || reportModel.getCaseworker_name().trim().isEmpty()) {
                        reportModel.setCaseworker_name(getCaseworkerName(prefs));
                    }
                    finalForm.put(Constants.JSON_FORM_KEY.ENTITY_ID, reportModel.getBase_entity_id());
                    org.smartregister.chw.core.utils.CoreJsonFormUtils.populateJsonForm(finalForm, reportModel.toValueMap());

                    Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
                    Form wizardForm = new Form();
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, finalForm.toString());
                    startActivityForResult(intent, EDIT_FORM_REQUEST);
                } catch (Exception e) {
                    Timber.e(e);
                    Snackbar.make(findViewById(R.id.header_card), "Unable to open form", Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }

    private String getCaseworkerName(SharedPreferences prefs) {
        String caseworkerName = prefs.getString("caseworker_name", "");
        if (caseworkerName != null && !caseworkerName.trim().isEmpty()) {
            return caseworkerName.trim();
        }
        String username = prefs.getString("last_logged_in_username", "");
        return username == null ? "" : username.trim();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_FORM_REQUEST && resultCode == RESULT_OK && data != null) {
            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
            if (jsonString != null) {
                saveFromFormJson(jsonString, true);
            }
        }
    }

    private void saveFromFormJson(String jsonString, boolean isEditMode) {
        try {
            ReportEventClient reportEventClient = processRegistration(jsonString);
            if (reportEventClient != null) {
                saveRegistration(reportEventClient, isEditMode);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private ReportEventClient processRegistration(String jsonString) {
        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            String entityId = formJsonObject.optString(com.bluecodeltd.ecap.chw.util.Constants.JSON_FORM_KEY.ENTITY_ID);
            if (entityId.isEmpty()) {
                entityId = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();
            }
            JSONObject metadata = formJsonObject.getJSONObject(com.bluecodeltd.ecap.chw.util.Constants.METADATA);
            org.json.JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJsonObject);
            FormTag formTag = getFormTag();
            String tableName = getReportTableName(encounterType);
            if (tableName == null) {
                return null;
            }
            Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId, encounterType, tableName);
            org.smartregister.chw.core.utils.CoreJsonFormUtils.tagSyncMetadata(getAllSharedPreferences(), event);
            Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
            return new ReportEventClient(event, client);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private String getReportTableName(String encounterType) {
        if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_MALARIA.equalsIgnoreCase(encounterType)) {
            return ReportRegisterActivity.REPORT_TABLE_MALARIA;
        }
        if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_NUTRITION.equalsIgnoreCase(encounterType)) {
            return ReportRegisterActivity.REPORT_TABLE_NUTRITION;
        }
        if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_TB.equalsIgnoreCase(encounterType)) {
            return ReportRegisterActivity.REPORT_TABLE_TB;
        }
        if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_COMMUNITY_ALERT.equalsIgnoreCase(encounterType)) {
            return ReportRegisterActivity.REPORT_TABLE_COMMUNITY_ALERT;
        }
        return null;
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
                        if (existingClientJsonObject.has("attributes") && newClientJsonObject.has("attributes")) {
                            mergedClientJsonObject.put("attributes", org.smartregister.util.JsonFormUtils.merge(existingClientJsonObject.getJSONObject("attributes"), newClientJsonObject.getJSONObject("attributes")));
                        }
                        ecSyncHelper.addClient(client.getBaseEntityId(), mergedClientJsonObject);
                    } else {
                        ecSyncHelper.addClient(client.getBaseEntityId(), newClientJsonObject);
                    }
                    JSONObject eventJsonObject = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event));
                    ecSyncHelper.addEvent(event.getBaseEntityId(), eventJsonObject);
                    Long lastUpdatedAtDate = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                    Date currentSyncDate = new Date(lastUpdatedAtDate);
                    List<org.smartregister.domain.db.EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                    getClientProcessorForJava().processClient(savedEvents);
                    getAllSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());
                    runOnUiThread(() -> {
                        loadReport();
                        Toast.makeText(this, R.string.report_community_alert_saved, Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        };
        try {
            new AppExecutors().diskIO().execute(runnable);
            return true;
        } catch (Exception e) {
            Timber.e(e);
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

    private void populateData() {
        if (reportModel == null) return;

        TextView title = findViewById(R.id.report_view_title);
        if (title != null) {
            String reportingMonth = reportModel.getReporting_month() != null ? reportModel.getReporting_month() : "";
            title.setText(getString(R.string.report_community_alert_title, reportingMonth));
        }

        setText(R.id.txt_reporting_month, reportModel.getReporting_month());
        setText(R.id.txt_facility_name, reportModel.getFacility());

        Map<String, String> data = reportModel.toValueMap();

        setText(R.id.txt_form_id, reportModel.getForm_id());
        setText(R.id.txt_province, reportModel.getProvince());
        setText(R.id.txt_district, reportModel.getDistrict());
        setText(R.id.txt_ward, reportModel.getWard());
        setText(R.id.txt_caseworker_name, reportModel.getCaseworker_name());
        setText(R.id.txt_phone_number, data.get("phone_number"));
        setText(R.id.txt_community, data.get("community"));
        setText(R.id.txt_date_reporting, data.get("date_reporting"));
        setText(R.id.txt_super_mentor_name, data.get("super_mentor_name"));
        setText(R.id.txt_super_mentor_contact, data.get("super_mentor_contact"));
        setText(R.id.txt_illness_type, data.get("illness_type"));

        String eventDate = data.get("event_date") != null ? data.get("event_date") : "";
        String eventTime = data.get("event_time") != null ? data.get("event_time") : "";
        String when = eventDate + (!eventDate.isEmpty() && !eventTime.isEmpty() ? " " : "") + eventTime;
        setText(R.id.txt_event_date, when);

        setText(R.id.txt_location, data.get("location"));

        String diseaseSelected = "pcz".equalsIgnoreCase(data.get("illness_type"))
                ? data.get("pcz_priority_disease")
                : data.get("other_priority_disease");
        setText(R.id.txt_disease_selected, diseaseSelected);

        // Suspected Cases (exact age bands: 0-4Yrs, 5-14Yrs, >=15Yrs)
        setText(R.id.case_f_0_4, data.get("case_f_0_4"), "0");
        setText(R.id.case_f_5_14, data.get("case_f_5_14"), "0");
        setText(R.id.case_f_15_plus, data.get("case_f_15_plus"), "0");
        setText(R.id.case_m_0_4, data.get("case_m_0_4"), "0");
        setText(R.id.case_m_5_14, data.get("case_m_5_14"), "0");
        setText(R.id.case_m_15_plus, data.get("case_m_15_plus"), "0");
        setText(R.id.case_total, data.get("case_total"), "0");

        String cbsPartOfResponse = data.get("cbs_supervisor_part_of_response");
        if ("yes".equalsIgnoreCase(cbsPartOfResponse)) {
            setText(R.id.txt_cbs_supervisor_part_of_response, "Yes");
        } else if ("no".equalsIgnoreCase(cbsPartOfResponse)) {
            setText(R.id.txt_cbs_supervisor_part_of_response, "No");
        } else {
            setText(R.id.txt_cbs_supervisor_part_of_response, "");
        }
        setText(R.id.txt_cbs_supervisor_action_taken, data.get("cbs_supervisor_action_taken"));

        // Section B - Affected
        setText(R.id.affected_f_0_4, data.get("affected_f_0_4"), "0");
        setText(R.id.affected_f_5_9, data.get("affected_f_5_9"), "0");
        setText(R.id.affected_f_10_17, data.get("affected_f_10_17"), "0");
        setText(R.id.affected_f_18_plus, data.get("affected_f_18_plus"), "0");
        setText(R.id.affected_m_0_4, data.get("affected_m_0_4"), "0");
        setText(R.id.affected_m_5_9, data.get("affected_m_5_9"), "0");
        setText(R.id.affected_m_10_17, data.get("affected_m_10_17"), "0");
        setText(R.id.affected_m_18_plus, data.get("affected_m_18_plus"), "0");

        // Section B - Dead
        setText(R.id.dead_f_0_4, data.get("dead_f_0_4"), "0");
        setText(R.id.dead_f_5_9, data.get("dead_f_5_9"), "0");
        setText(R.id.dead_f_10_17, data.get("dead_f_10_17"), "0");
        setText(R.id.dead_f_18_plus, data.get("dead_f_18_plus"), "0");
        setText(R.id.dead_m_0_4, data.get("dead_m_0_4"), "0");
        setText(R.id.dead_m_5_9, data.get("dead_m_5_9"), "0");
        setText(R.id.dead_m_10_17, data.get("dead_m_10_17"), "0");
        setText(R.id.dead_m_18_plus, data.get("dead_m_18_plus"), "0");

        setText(R.id.txt_action_taken, data.get("action_taken"));
        setText(R.id.txt_response_performed, data.get("response_performed"));

        // Section C / Footer
        setText(R.id.txt_supervisor_name, data.get("supervisor_name"));
        setText(R.id.txt_date_reviewed, data.get("date_reviewed"));
        setText(R.id.txt_signature, data.get("signature"));

        TextView commentsView = findViewById(R.id.txt_comments);
        if (commentsView != null) {
            String comment = data.get("supervisor_action_taken"); 
            if (comment == null || comment.isEmpty()) {
                comment = data.get("action_taken");
            }
            commentsView.setText(comment != null && !comment.isEmpty() ? comment : "");
        }
    }

    private void setText(int viewId, String value) {
        setText(viewId, value, "");
    }

    private void setText(int viewId, String value, String defaultValue) {
        TextView textView = findViewById(viewId);
        if (textView != null) {
            textView.setText(value != null && !value.isEmpty() ? value : defaultValue);
        }
    }

    private static class ReportEventClient {
        private final org.smartregister.clientandeventmodel.Event event;
        private final org.smartregister.clientandeventmodel.Client client;

        ReportEventClient(org.smartregister.clientandeventmodel.Event event, org.smartregister.clientandeventmodel.Client client) {
            this.event = event;
            this.client = client;
        }

        org.smartregister.clientandeventmodel.Event getEvent() {
            return event;
        }

        org.smartregister.clientandeventmodel.Client getClient() {
            return client;
        }
    }
}

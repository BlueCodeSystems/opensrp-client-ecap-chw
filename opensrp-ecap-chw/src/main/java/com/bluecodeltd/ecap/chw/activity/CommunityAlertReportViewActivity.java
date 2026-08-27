package com.bluecodeltd.ecap.chw.activity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;

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
import com.bluecodeltd.ecap.chw.util.CbsWeeklyUtils;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CommunityAlertReportViewActivity extends AppCompatActivity {

    public static final String EXTRA_BASE_ENTITY_ID = "base_entity_id";
    private static final int EDIT_FORM_REQUEST = JsonFormUtils.REQUEST_CODE_GET_JSON;
    private MonthlyReportModel reportModel;
    private String baseEntityId;
    private CbsWeeklyUtils.Week week;
    private int weekReportCount = 1;
    private Map<String, String> weekTotals = Collections.emptyMap();
    private List<MonthlyReportModel> weekReports = new ArrayList<>();

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
            // Community Alert reports are reviewed by week (Sunday-Saturday): show every report
            // submitted in the same week as this one as its own numbered row, with a Total row
            // summing the numeric counts across all of them, so the screen is a comprehensive
            // weekly report rather than just this single submission.
            week = CbsWeeklyUtils.weekForReportingDate(reportModel.getReporting_month());
            List<MonthlyReportModel> allReports = MonthlyReportDao.getReports(ReportRegisterActivity.REPORT_TABLE_COMMUNITY_ALERT);
            List<MonthlyReportModel> matched = CbsWeeklyUtils.filterSameWeek(allReports, reportModel.getReporting_month());
            weekReports = new ArrayList<>(matched.isEmpty() ? Collections.singletonList(reportModel) : matched);
            weekReports.sort(Comparator.comparing(this::parseReportDate, Comparator.nullsLast(Comparator.naturalOrder())));
            weekReportCount = weekReports.size();
            weekTotals = CbsWeeklyUtils.aggregateNumericFields(weekReports);
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
            ChwApplication.getInstance().getAppExecutors().diskIO().execute(runnable);
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
            if (week != null) {
                title.setText(getString(R.string.report_community_alert_week_title, week.label, weekReportCount));
            } else {
                String reportingMonth = reportModel.getReporting_month() != null ? reportModel.getReporting_month() : "";
                title.setText(getString(R.string.report_community_alert_title, reportingMonth));
            }
        }

        // Administrative/location fields are shared across the week's reports, so they're shown
        // from this (most recently updated) report.
        setText(R.id.txt_reporting_month, reportModel.getReporting_month());
        setText(R.id.txt_facility_name, reportModel.getFacility());
        setText(R.id.txt_form_id, reportModel.getForm_id());
        setText(R.id.txt_province, reportModel.getProvince());
        setText(R.id.txt_district, reportModel.getDistrict());
        setText(R.id.txt_ward, reportModel.getWard());
        setText(R.id.txt_caseworker_name, reportModel.getCaseworker_name());

        Map<String, String> data = reportModel.toValueMap();
        setText(R.id.txt_phone_number, data.get("phone_number"));
        setText(R.id.txt_community, data.get("community"));
        setText(R.id.txt_date_reporting, data.get("date_reporting"));
        setText(R.id.txt_super_mentor_name, data.get("super_mentor_name"));
        setText(R.id.txt_super_mentor_contact, data.get("super_mentor_contact"));

        String comment = data.get("cbs_supervisor_action_taken");
        if (comment == null || comment.isEmpty()) {
            comment = data.get("action_taken");
        }
        TextView commentsView = findViewById(R.id.txt_comments);
        if (commentsView != null) {
            commentsView.setText(comment != null && !comment.isEmpty() ? comment : "");
        }

        buildDataTable();
    }

    /**
     * Renders the comprehensive weekly table: one numbered row per report submitted this week,
     * followed by a bolded Total row summing the numeric counts across all of them.
     */
    private void buildDataTable() {
        android.widget.TableLayout table = findViewById(R.id.community_alert_data_table);
        if (table == null) {
            return;
        }
        // Table's first child is the static header row (from XML) -- remove everything after it
        // before re-adding rows, so switching between reports doesn't keep stacking old rows.
        while (table.getChildCount() > 1) {
            table.removeViewAt(1);
        }

        int sn = 1;
        for (MonthlyReportModel report : weekReports) {
            table.addView(buildReportRow(table, sn++, report));
        }
        table.addView(buildTotalRow(table));
    }

    private View buildReportRow(android.widget.TableLayout table, int sn, MonthlyReportModel report) {
        View row = getLayoutInflater().inflate(R.layout.community_alert_report_data_row, table, false);
        Map<String, String> data = report.toValueMap();

        setRowCell(row, R.id.row_sn, String.valueOf(sn));

        String illnessType = data.get("illness_type");
        String specificDisease = "pcz".equalsIgnoreCase(illnessType)
                ? data.get("pcz_priority_disease")
                : data.get("other_priority_disease");
        if (specificDisease != null && !specificDisease.isEmpty()) {
            String displayDisease = specificDisease.replace("_", " ");
            displayDisease = displayDisease.substring(0, 1).toUpperCase() + displayDisease.substring(1);
            setRowCell(row, R.id.row_illness_type, displayDisease);
        } else {
            setRowCell(row, R.id.row_illness_type, illnessType);
        }

        String eventDate = data.get("event_date") != null ? data.get("event_date") : "";
        String eventTime = data.get("event_time") != null ? data.get("event_time") : "";
        setRowCell(row, R.id.row_event_date, eventDate + (!eventDate.isEmpty() && !eventTime.isEmpty() ? " " : "") + eventTime);

        setRowCell(row, R.id.row_location, data.get("location"));
        setRowCell(row, R.id.row_gps, data.get("gps"));

        int suspected = parseSafe(data.get("suspected_female")) + parseSafe(data.get("suspected_male"));
        setRowCell(row, R.id.row_case_total, String.valueOf(suspected));

        for (String key : CbsWeeklyUtils.AFFECTED_FIELD_KEYS) {
            setRowCell(row, affectedCellId(key), data.get(key), "0");
        }
        for (String key : CbsWeeklyUtils.DEATH_FIELD_KEYS) {
            setRowCell(row, deathCellId(key), data.get(key), "0");
        }

        setRowCell(row, R.id.row_action_taken, data.get("action_taken"));
        String cbsPartOfResponse = data.get("cbs_supervisor_part_of_response");
        setRowCell(row, R.id.row_response_performed,
                "yes".equalsIgnoreCase(cbsPartOfResponse) ? "Yes" : "no".equalsIgnoreCase(cbsPartOfResponse) ? "No" : "");

        // Zebra-stripe alternate rows for readability, like a real report table.
        if (sn % 2 == 0 && row instanceof android.view.ViewGroup) {
            applyCellBackgroundRecursively((android.view.ViewGroup) row, R.drawable.table_cell_background_alt);
        }
        return row;
    }

    private View buildTotalRow(android.widget.TableLayout table) {
        View row = getLayoutInflater().inflate(R.layout.community_alert_report_data_row, table, false);
        setRowCell(row, R.id.row_sn, getString(R.string.report_community_alert_total_row_label));
        setRowCell(row, R.id.row_illness_type, "");
        setRowCell(row, R.id.row_event_date, "");
        setRowCell(row, R.id.row_location, "");
        setRowCell(row, R.id.row_gps, "");

        int totalSuspected = parseSafe(weekTotals.get("suspected_female")) + parseSafe(weekTotals.get("suspected_male"));
        setRowCell(row, R.id.row_case_total, String.valueOf(totalSuspected));

        for (String key : CbsWeeklyUtils.AFFECTED_FIELD_KEYS) {
            setRowCell(row, affectedCellId(key), weekTotals.get(key), "0");
        }
        for (String key : CbsWeeklyUtils.DEATH_FIELD_KEYS) {
            setRowCell(row, deathCellId(key), weekTotals.get(key), "0");
        }
        setRowCell(row, R.id.row_action_taken, "");
        setRowCell(row, R.id.row_response_performed, "");

        if (row instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) row;
            applyCellBackgroundRecursively(group, R.drawable.table_cell_background_total);
            applyBoldRecursively(group);
        }
        return row;
    }

    private int affectedCellId(String key) {
        switch (key) {
            case "affected_f_0_4": return R.id.row_affected_f_0_4;
            case "affected_f_5_9": return R.id.row_affected_f_5_9;
            case "affected_f_10_17": return R.id.row_affected_f_10_17;
            case "affected_f_18_plus": return R.id.row_affected_f_18_plus;
            case "affected_m_0_4": return R.id.row_affected_m_0_4;
            case "affected_m_5_9": return R.id.row_affected_m_5_9;
            case "affected_m_10_17": return R.id.row_affected_m_10_17;
            default: return R.id.row_affected_m_18_plus;
        }
    }

    private int deathCellId(String key) {
        switch (key) {
            case "dead_f_0_4": return R.id.row_dead_f_0_4;
            case "dead_f_5_9": return R.id.row_dead_f_5_9;
            case "dead_f_10_17": return R.id.row_dead_f_10_17;
            case "dead_f_18_plus": return R.id.row_dead_f_18_plus;
            case "dead_m_0_4": return R.id.row_dead_m_0_4;
            case "dead_m_5_9": return R.id.row_dead_m_5_9;
            case "dead_m_10_17": return R.id.row_dead_m_10_17;
            default: return R.id.row_dead_m_18_plus;
        }
    }

    /**
     * Recolors every "cell" background in a row -- any child whose current background is one of
     * the bordered table_cell_background* shape drawables (a GradientDrawable at runtime) -- to
     * the given drawable, leaving solid-color backgrounds (like the Female/Male group labels)
     * untouched. Used for zebra-striping data rows and highlighting the Total row.
     */
    private void applyCellBackgroundRecursively(android.view.ViewGroup group, int drawableRes) {
        if (group.getBackground() instanceof android.graphics.drawable.GradientDrawable) {
            group.setBackgroundResource(drawableRes);
        }
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child.getBackground() instanceof android.graphics.drawable.GradientDrawable) {
                child.setBackgroundResource(drawableRes);
            }
            if (child instanceof android.view.ViewGroup) {
                applyCellBackgroundRecursively((android.view.ViewGroup) child, drawableRes);
            }
        }
    }

    private void applyBoldRecursively(android.view.ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTypeface(((TextView) child).getTypeface(), android.graphics.Typeface.BOLD);
            } else if (child instanceof android.view.ViewGroup) {
                applyBoldRecursively((android.view.ViewGroup) child);
            }
        }
    }

    private void setRowCell(View row, int viewId, String value) {
        setRowCell(row, viewId, value, "");
    }

    private void setRowCell(View row, int viewId, String value, String defaultValue) {
        TextView textView = row.findViewById(viewId);
        if (textView != null) {
            textView.setText(value != null && !value.isEmpty() ? value : defaultValue);
        }
    }

    private Date parseReportDate(MonthlyReportModel report) {
        if (report == null || report.getReporting_month() == null || report.getReporting_month().trim().isEmpty()) {
            return null;
        }
        try {
            return new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault()).parse(report.getReporting_month().trim());
        } catch (Exception e) {
            return null;
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

    private int parseSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
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

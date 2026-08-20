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
import androidx.core.widget.NestedScrollView;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.MonthlyReportDao;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.MonthlyReportModel;
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

public class MonthlyTbReportViewActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_monthly_tb_report_view);

        Toolbar toolbar = findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        applyLightStatusBar();
        toolbar.setNavigationOnClickListener(v -> finish());

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        loadReport();

        setupTabLogic();
        setupEditButton();
    }

    private void applyLightStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#1B3A4B"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(0);
        }
    }

    private void loadReport() {
        if (baseEntityId != null) {
            reportModel = MonthlyReportDao.getReport(ReportRegisterActivity.REPORT_TABLE_TB, baseEntityId);
        }

        if (reportModel != null) {
            populateData();
        }
    }

    private void setupTabLogic() {
        TextView tabQ1 = findViewById(R.id.tab_q1);
        TextView tabQ2 = findViewById(R.id.tab_q2);
        TextView tabQ3 = findViewById(R.id.tab_q3);
        TextView tabQ4 = findViewById(R.id.tab_q4);
        TextView tabQ5 = findViewById(R.id.tab_q5);
        TextView tabQ6 = findViewById(R.id.tab_q6);
        TextView tabQ7 = findViewById(R.id.tab_q7);
        TextView tabComments = findViewById(R.id.tab_comments);

        View secQ1 = findViewById(R.id.sec_q1);
        View secQ2 = findViewById(R.id.sec_q2);
        View secQ3 = findViewById(R.id.sec_q3);
        View secQ4 = findViewById(R.id.sec_q4);
        View secQ5 = findViewById(R.id.sec_q5);
        View secQ6 = findViewById(R.id.sec_q6);
        View secQ7 = findViewById(R.id.sec_q7);
        View secComments = findViewById(R.id.sec_comments);

        NestedScrollView scrollView = findViewById(R.id.report_scroll);

        View.OnClickListener tabClickListener = v -> {
            View target = null;
            resetTabs(tabQ1, tabQ2, tabQ3, tabQ4, tabQ5, tabQ6, tabQ7, tabComments);
            v.setBackgroundColor(Color.WHITE);
            ((TextView) v).setTextColor(Color.parseColor("#1B3A4B"));

            int id = v.getId();
            if (id == R.id.tab_q1) target = secQ1;
            else if (id == R.id.tab_q2) target = secQ2;
            else if (id == R.id.tab_q3) target = secQ3;
            else if (id == R.id.tab_q4) target = secQ4;
            else if (id == R.id.tab_q5) target = secQ5;
            else if (id == R.id.tab_q6) target = secQ6;
            else if (id == R.id.tab_q7) target = secQ7;
            else if (id == R.id.tab_comments) target = secComments;

            if (target != null && scrollView != null) {
                View finalTarget = target;
                scrollView.post(() -> scrollView.smoothScrollTo(0, finalTarget.getTop()));
            }
        };

        if (tabQ1 != null) tabQ1.setOnClickListener(tabClickListener);
        if (tabQ2 != null) tabQ2.setOnClickListener(tabClickListener);
        if (tabQ3 != null) tabQ3.setOnClickListener(tabClickListener);
        if (tabQ4 != null) tabQ4.setOnClickListener(tabClickListener);
        if (tabQ5 != null) tabQ5.setOnClickListener(tabClickListener);
        if (tabQ6 != null) tabQ6.setOnClickListener(tabClickListener);
        if (tabQ7 != null) tabQ7.setOnClickListener(tabClickListener);
        if (tabComments != null) tabComments.setOnClickListener(tabClickListener);
    }

    private void resetTabs(TextView... tabs) {
        for (TextView tab : tabs) {
            if (tab != null) {
                tab.setBackgroundColor(Color.parseColor("#21603F"));
                tab.setTextColor(Color.parseColor("#CFE3D6"));
            }
        }
    }

    private void setupEditButton() {
        ImageView btnEdit = findViewById(R.id.btn_edit_report);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> openEditForm());
        }
    }

    private void openEditForm() {
        if (reportModel == null) return;

        Threading.io(() -> {
            CaseStatusModel caseStatusModel = null;
            try {
                caseStatusModel = IndexPersonDao.getCaseStatus(reportModel.getBase_entity_id());
            } catch (Exception ignored) {
            }

            JSONObject form = null;
            try {
                form = new FormUtils(this).getFormJson(ReportRegisterActivity.REPORT_FORM_TB);
            } catch (Exception ignored) {
            }

            CaseStatusModel finalCaseStatusModel = caseStatusModel;
            JSONObject finalForm = form;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                String status = finalCaseStatusModel != null ? finalCaseStatusModel.getCase_status() : null;
                if ("0".equals(status) || "2".equals(status)) {
                    Snackbar.make(findViewById(R.id.report_scroll), "Beneficiary is inactive or de-registered", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (finalForm == null) {
                    Snackbar.make(findViewById(R.id.report_scroll), "Unable to open form", Snackbar.LENGTH_LONG).show();
                    return;
                }

                try {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    if (reportModel.getAdditionalField("caseworker_name") == null || reportModel.getAdditionalField("caseworker_name").trim().isEmpty()) {
                        reportModel.setAdditionalField("caseworker_name", getCaseworkerName(prefs));
                    }
                    finalForm.put("entity_id", reportModel.getBase_entity_id());
                    org.smartregister.chw.core.utils.CoreJsonFormUtils.populateJsonForm(finalForm, reportModel.toValueMap());

                    Intent intent = new Intent(this, ReportFormActivity.class);
                    Form wizardForm = new Form();
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, finalForm.toString());
                    startActivityForResult(intent, EDIT_FORM_REQUEST);
                } catch (Exception e) {
                    Timber.e(e);
                    Snackbar.make(findViewById(R.id.report_scroll), "Unable to open form", Snackbar.LENGTH_LONG).show();
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

            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                String entity = field.optString("openmrs_entity");
                if (entity.isEmpty() || "person_attribute".equals(entity)) {
                    field.put("openmrs_entity", "concept");
                    field.put("openmrs_entity_id", field.optString("key"));
                }
            }

            FormTag formTag = getFormTag();
            String tableName = getReportTableName(encounterType);
            if (tableName == null) {
                return null;
            }
            Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId, encounterType, tableName);
            org.smartregister.chw.core.utils.CoreJsonFormUtils.tagSyncMetadata(getAllSharedPreferences(), event);
            Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
            if (client == null) {
                client = new Client(entityId);
                client.setFirstName("Monthly Report");
                client.setLastName(encounterType);
            }
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
                        Toast.makeText(this, R.string.report_tb_saved, Toast.LENGTH_SHORT).show();
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
        TextView meta = findViewById(R.id.report_meta);
        if (meta != null) {
            meta.setText(String.format("Facility Name: %s  · Reporting Period: %s",
                    reportModel.getFacility(), reportModel.getReporting_month()));
        }

        Map<String, String> data = reportModel.toValueMap();

        String[] questions = {"q1", "q2", "q3", "q4", "q5", "q6", "q7"};
        for (String q : questions) {
            int totalF = 0;
            int totalM = 0;

            // Row 1: < 1 Year
            int f1 = parseVal(data.get(q + "_f_lt_1"));
            int m1 = parseVal(data.get(q + "_m_lt_1"));
            setText(getResources().getIdentifier("tv_" + q + "_lt1_f", "id", getPackageName()), String.valueOf(f1));
            setText(getResources().getIdentifier("tv_" + q + "_lt1_m", "id", getPackageName()), String.valueOf(m1));
            totalF += f1; totalM += m1;

            // Row 2: 1-4 Years
            int f2 = parseVal(data.get(q + "_f_1_4"));
            int m2 = parseVal(data.get(q + "_m_1_4"));
            setText(getResources().getIdentifier("tv_" + q + "_1_4_f", "id", getPackageName()), String.valueOf(f2));
            setText(getResources().getIdentifier("tv_" + q + "_1_4_m", "id", getPackageName()), String.valueOf(m2));
            totalF += f2; totalM += m2;

            // Row 3: 5-9 Years
            int f3 = parseVal(data.get(q + "_f_5_9"));
            int m3 = parseVal(data.get(q + "_m_5_9"));
            setText(getResources().getIdentifier("tv_" + q + "_5_9_f", "id", getPackageName()), String.valueOf(f3));
            setText(getResources().getIdentifier("tv_" + q + "_5_9_m", "id", getPackageName()), String.valueOf(m3));
            totalF += f3; totalM += m3;

            // Row 4: 10-14 Years
            int f4 = parseVal(data.get(q + "_f_10_14"));
            int m4 = parseVal(data.get(q + "_m_10_14"));
            setText(getResources().getIdentifier("tv_" + q + "_10_14_f", "id", getPackageName()), String.valueOf(f4));
            setText(getResources().getIdentifier("tv_" + q + "_10_14_m", "id", getPackageName()), String.valueOf(m4));
            totalF += f4; totalM += m4;

            // Row 5: 15-19 Years
            int f5 = parseVal(data.get(q + "_f_15_19"));
            int m5 = parseVal(data.get(q + "_m_15_19"));
            setText(getResources().getIdentifier("tv_" + q + "_15_19_f", "id", getPackageName()), String.valueOf(f5));
            setText(getResources().getIdentifier("tv_" + q + "_15_19_m", "id", getPackageName()), String.valueOf(m5));
            totalF += f5; totalM += m5;

            // Row 6: 20+ Years
            int f6 = parseVal(data.get(q + "_f_20_plus"));
            int m6 = parseVal(data.get(q + "_m_20_plus"));
            setText(getResources().getIdentifier("tv_" + q + "_20_plus_f", "id", getPackageName()), String.valueOf(f6));
            setText(getResources().getIdentifier("tv_" + q + "_20_plus_m", "id", getPackageName()), String.valueOf(m6));
            totalF += f6; totalM += m6;

            // Row 7: PC 18+ Years
            int f7 = parseVal(data.get(q + "_f_pc_18_plus"));
            int m7 = parseVal(data.get(q + "_m_pc_18_plus"));
            setText(getResources().getIdentifier("tv_" + q + "_pc_18_f", "id", getPackageName()), String.valueOf(f7));
            setText(getResources().getIdentifier("tv_" + q + "_pc_18_m", "id", getPackageName()), String.valueOf(m7));
            totalF += f7; totalM += m7;

            // Row 8: Total (1-7)
            setText(getResources().getIdentifier("tv_" + q + "_total_f", "id", getPackageName()), String.valueOf(totalF));
            setText(getResources().getIdentifier("tv_" + q + "_total_m", "id", getPackageName()), String.valueOf(totalM));

            // Sub-Populations (9 to 12) - Aligned with BOTH column (far right)
            int hei = parseVal(data.get(q + "_hei"));
            int calhiv = parseVal(data.get(q + "_calhiv"));
            int wlhiv = parseVal(data.get(q + "_wlhiv"));
            int pcLhiv = parseVal(data.get(q + "_pc_lhiv"));

            setText(getResources().getIdentifier("tv_" + q + "_hei", "id", getPackageName()), String.valueOf(hei));
            setText(getResources().getIdentifier("tv_" + q + "_calhiv", "id", getPackageName()), String.valueOf(calhiv));
            setText(getResources().getIdentifier("tv_" + q + "_wlhiv", "id", getPackageName()), String.valueOf(wlhiv));
            setText(getResources().getIdentifier("tv_" + q + "_pc_lhiv", "id", getPackageName()), String.valueOf(pcLhiv));

            // Row 13: Sub-Population Total (9-12)
            int subTotal = hei + calhiv + wlhiv + pcLhiv;
            setText(getResources().getIdentifier("tv_" + q + "_sub_total", "id", getPackageName()), String.valueOf(subTotal));

            setStatusText(q + "_status", data.get(q + "_status"));
        }

        TextView commentsView = findViewById(R.id.tv_comments);
        if (commentsView != null) {
            String comment = data.get("comment");
            commentsView.setText(comment != null && !comment.isEmpty() ? comment : "No comments");
        }
    }

    private int parseVal(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setText(int viewId, String value) {
        if (viewId == 0) return;
        TextView textView = findViewById(viewId);
        if (textView != null) {
            textView.setText(value != null && !value.isEmpty() ? value : "0");
        }
    }

    private void setStatusText(String idName, String value) {
        int viewId = getResources().getIdentifier(idName, "id", getPackageName());
        if (viewId == 0) return;
        TextView textView = findViewById(viewId);
        if (textView == null) return;
        if ("open".equalsIgnoreCase(value)) {
            textView.setText("Status: Open");
        } else if ("closed".equalsIgnoreCase(value)) {
            textView.setText("Status: Closed");
        } else {
            textView.setText("");
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

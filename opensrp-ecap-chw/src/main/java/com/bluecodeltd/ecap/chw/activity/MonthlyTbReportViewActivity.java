package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowInsetsController;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

        Toolbar toolbar = findViewById(R.id.report_view_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        applyLightStatusBar();
        TextView toolbarTitle = findViewById(R.id.report_view_title);
        if (toolbarTitle != null) {
            toolbarTitle.setText("Monthly TB Report");
        }
        findViewById(R.id.report_view_back_button).setOnClickListener(v -> finish());

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        loadReport();
        
        setupExpansionLogic();
        setupNavigationLogic();
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
            reportModel = MonthlyReportDao.getReport(ReportRegisterActivity.REPORT_TABLE_TB, baseEntityId);
        }

        if (reportModel != null) {
            populateData();
        }
    }

    private void setupExpansionLogic() {
        int[] contents = {R.id.content_q1, R.id.content_q2, R.id.content_q3, R.id.content_q4, R.id.content_q5, R.id.content_q6, R.id.content_q7, R.id.content_comments};
        int[] icons = {R.id.icon_q1, R.id.icon_q2, R.id.icon_q3, R.id.icon_q4, R.id.icon_q5, R.id.icon_q6, R.id.icon_q7, R.id.icon_comments};

        for (int contentId : contents) {
            View content = findViewById(contentId);
            if (content != null) content.setVisibility(View.VISIBLE);
        }

        for (int iconId : icons) {
            View icon = findViewById(iconId);
            if (icon != null) icon.setVisibility(View.GONE);
        }
    }

    private void setupNavigationLogic() {
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.report_scroll);
        if (scrollView == null) return;

        for (int i = 1; i <= 7; i++) {
            int tabId = getResources().getIdentifier("tab_q" + i, "id", getPackageName());
            int secId = getResources().getIdentifier("sec_q" + i, "id", getPackageName());
            setupJump(tabId, secId, scrollView);
        }
        setupJump(R.id.tab_comments, R.id.sec_comments, scrollView);
    }

    private void setupJump(int chipId, int targetId, androidx.core.widget.NestedScrollView scrollView) {
        View chip = findViewById(chipId);
        View target = findViewById(targetId);
        if (chip != null && target != null) {
            chip.setOnClickListener(v -> {
                int top = 0;
                View parent = target;
                while (parent != null && parent != scrollView) {
                    top += parent.getTop();
                    if (parent.getParent() instanceof View) {
                        parent = (View) parent.getParent();
                    } else {
                        break;
                    }
                }
                scrollView.smoothScrollTo(0, top);
            });
        }
    }

    private void setupEditButton() {
        ImageButton btnEdit = findViewById(R.id.btn_edit_report);
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

            CaseStatusModel finalCaseStatusModel = caseStatusModel;
            Threading.main(() -> {
                String status = finalCaseStatusModel != null ? finalCaseStatusModel.getCase_status() : null;
                if ("0".equals(status) || "2".equals(status)) {
                    Snackbar.make(findViewById(R.id.header_card), "Beneficiary is inactive or de-registered", Snackbar.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject form = new FormUtils(this).getFormJson(ReportRegisterActivity.REPORT_FORM_TB);
                    if (form == null) {
                        return;
                    }
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    if (reportModel.getAdditionalField("caseworker_name") == null || reportModel.getAdditionalField("caseworker_name").trim().isEmpty()) {
                        reportModel.setAdditionalField("caseworker_name", getCaseworkerName(prefs));
                    }
                    form.put("entity_id", reportModel.getBase_entity_id());

                    Map<String, String> data = reportModel.toValueMap();
                    Map<String, String> normalizedFields = new java.util.HashMap<>();
                    for (Map.Entry<String, String> entry : data.entrySet()) {
                        normalizedFields.put(entry.getKey().toLowerCase(), entry.getValue());
                    }

                    org.smartregister.chw.core.utils.CoreJsonFormUtils.populateJsonForm(form, normalizedFields);

                    Intent intent = new Intent(this, ReportFormActivity.class);
                    Form wizardForm = new Form();
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
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
                        Toast.makeText(this, R.string.report_tb_saved, Toast.LENGTH_SHORT).show();
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
        TextView title = findViewById(R.id.report_view_title);
        title.setText(getString(R.string.tb_report_title, reportModel.getReporting_month()));

        setText(R.id.txt_reporting_month, reportModel.getReporting_month());
        setText(R.id.txt_facility_name, reportModel.getFacility());

        Map<String, String> data = reportModel.toValueMap();

        for (int q = 1; q <= 7; q++) {
            int f_total = 0;
            int m_total = 0;
            int sub_total = 0;

            String[] f_suffixes = {"_f_lt_1", "_f_1_4", "_f_5_9", "_f_10_14", "_f_15_19", "_f_20_plus", "_f_pc_18_plus"};
            String[] m_suffixes = {"_m_lt_1", "_m_1_4", "_m_5_9", "_m_10_14", "_m_15_19", "_m_20_plus", "_m_pc_18_plus"};
            String[] sub_keys = {"_hei", "_calhiv", "_wlhiv", "_pc_lhiv", "_other"};

            for (String s : f_suffixes) {
                String key = "q" + q + s;
                String val = data.get(key);
                f_total += parseSafeInt(val);
                int resId = getResources().getIdentifier(key.replace("_f_lt_1", "_f_lt1").replace("_f_pc_18_plus", "_f_pc_18"), "id", getPackageName());
                if (resId != 0) setText(resId, val);
            }

            for (String s : m_suffixes) {
                String key = "q" + q + s;
                String val = data.get(key);
                m_total += parseSafeInt(val);
                int resId = getResources().getIdentifier(key.replace("_m_lt_1", "_m_lt1").replace("_m_pc_18_plus", "_m_pc_18"), "id", getPackageName());
                if (resId != 0) setText(resId, val);
            }

            for (String s : sub_keys) {
                String key = "q" + q + s;
                String val = data.get(key);
                sub_total += parseSafeInt(val);
                int resId = getResources().getIdentifier(key, "id", getPackageName());
                if (resId != 0) setText(resId, val);
            }

            setText(getResources().getIdentifier("q" + q + "_f_total", "id", getPackageName()), String.valueOf(f_total));
            setText(getResources().getIdentifier("q" + q + "_m_total", "id", getPackageName()), String.valueOf(m_total));
            setText(getResources().getIdentifier("q" + q + "_sub_total", "id", getPackageName()), String.valueOf(sub_total));
        }

        TextView commentsView = findViewById(R.id.txt_comments);
        if (commentsView != null) {
            String comment = data.get("comment");
            commentsView.setText(comment != null && !comment.isEmpty() ? comment : "No comments");
        }
    }

    private int parseSafeInt(String val) {
        try {
            return (val != null && !val.isEmpty()) ? Integer.parseInt(val) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setText(int viewId, String value) {
        TextView textView = findViewById(viewId);
        if (textView != null) {
            textView.setText(value != null && !value.isEmpty() ? value : "0");
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

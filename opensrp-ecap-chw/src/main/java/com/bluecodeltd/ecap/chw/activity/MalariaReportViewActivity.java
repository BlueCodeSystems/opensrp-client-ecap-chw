package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

public class MalariaReportViewActivity extends AppCompatActivity {

    public static final String EXTRA_BASE_ENTITY_ID = "base_entity_id";
    private static final int EDIT_FORM_REQUEST = JsonFormUtils.REQUEST_CODE_GET_JSON;
    private MonthlyReportModel reportModel;
    private String baseEntityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_malaria_report_view);

        Toolbar toolbar = findViewById(R.id.report_view_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        loadReport();

        setupExpansionLogic();
        setupEditButton();
    }

    private void loadReport() {
        if (baseEntityId != null) {
            reportModel = MonthlyReportDao.getReport(ReportRegisterActivity.REPORT_TABLE_MALARIA, baseEntityId);
        }

        if (reportModel != null) {
            populateData();
        }
    }

    private void setupExpansionLogic() {
        // Section A
        setupSection(R.id.header_q1, R.id.content_q1, R.id.icon_q1);
        setupSection(R.id.header_q2, R.id.content_q2, R.id.icon_q2);
        setupSection(R.id.header_q3, R.id.content_q3, R.id.icon_q3);
        setupSection(R.id.header_q4, R.id.content_q4, R.id.icon_q4);
        setupSection(R.id.header_q5, R.id.content_q5, R.id.icon_q5);
        setupSection(R.id.header_q6, R.id.content_q6, R.id.icon_q6);
        setupSection(R.id.header_q7, R.id.content_q7, R.id.icon_q7);
        setupSection(R.id.header_q8, R.id.content_q8, R.id.icon_q8);
        setupSection(R.id.header_q9, R.id.content_q9, R.id.icon_q9);
        setupSection(R.id.header_q10, R.id.content_q10, R.id.icon_q10);

        // Section B
        setupSection(R.id.header_sb_q1, R.id.content_sb_q1, R.id.icon_sb_q1);
        setupSection(R.id.header_sb_q2, R.id.content_sb_q2, R.id.icon_sb_q2);
        setupSection(R.id.header_sb_q3, R.id.content_sb_q3, R.id.icon_sb_q3);
        setupSection(R.id.header_sb_q4, R.id.content_sb_q4, R.id.icon_sb_q4);
        setupSection(R.id.header_sb_q5, R.id.content_sb_q5, R.id.icon_sb_q5);
        setupSection(R.id.header_sb_q6, R.id.content_sb_q6, R.id.icon_sb_q6);
        setupSection(R.id.header_sb_q7, R.id.content_sb_q7, R.id.icon_sb_q7);
        setupSection(R.id.header_sb_q8, R.id.content_sb_q8, R.id.icon_sb_q8);
        setupSection(R.id.header_sb_q9, R.id.content_sb_q9, R.id.icon_sb_q9);
        setupSection(R.id.header_sb_q10, R.id.content_sb_q10, R.id.icon_sb_q10);

        // Section C & Comments
        setupSection(R.id.header_sc, R.id.content_sc, R.id.icon_sc);
        setupSection(R.id.header_comments, R.id.content_comments, R.id.icon_comments);
    }

    private void setupSection(int headerId, int contentId, int iconId) {
        View header = findViewById(headerId);
        View content = findViewById(contentId);
        ImageView icon = findViewById(iconId);

        if (header != null && content != null && icon != null) {
            header.setOnClickListener(v -> {
                if (content.getVisibility() == View.VISIBLE) {
                    content.setVisibility(View.GONE);
                    icon.setImageResource(R.drawable.baseline_expand_more_24);
                } else {
                    content.setVisibility(View.VISIBLE);
                    icon.setImageResource(R.drawable.baseline_expand_less_24);
                }
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
                    JSONObject form = new FormUtils(this).getFormJson(ReportRegisterActivity.REPORT_FORM_MALARIA);
                    if (form == null) {
                        return;
                    }
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    if (reportModel.getAdditionalField("caseworker_name") == null || reportModel.getAdditionalField("caseworker_name").trim().isEmpty()) {
                        reportModel.setAdditionalField("caseworker_name", getCaseworkerName(prefs));
                    }
                    form.put("entity_id", reportModel.getBase_entity_id());
                    org.smartregister.chw.core.utils.CoreJsonFormUtils.populateJsonForm(form, reportModel.toValueMap());

                    Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
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
                        Toast.makeText(this, R.string.report_malaria_saved, Toast.LENGTH_SHORT).show();
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
        title.setText(getString(R.string.malaria_report_title, reportModel.getReporting_month()));

        setText(R.id.txt_reporting_month, reportModel.getReporting_month());
        setText(R.id.txt_facility_name, reportModel.getFacility());

        Map<String, String> data = reportModel.toValueMap();

        // Section A (Q1-Q10)
        String[] qPrefixes = {"q1", "q2", "q3", "q4", "q5", "q6", "q7", "q8", "q9", "q10"};
        String[] qSuffixes = {
                "_f_0_4", "_m_0_4", "_f_5_15", "_m_5_15", "_f_16_19", "_m_16_19", "_f_20_plus", "_m_20_plus",
                "_f_calhiv", "_m_calhiv", "_f_hei", "_m_hei", "_f_wlhiv", "_m_wlhiv", "_f_sv", "_m_sv",
                "_f_agyw", "_f_hiv_pos", "_f_siblings", "_m_siblings", "_f_caregivers", "_m_caregivers"
        };

        for (String prefix : qPrefixes) {
            for (String suffix : qSuffixes) {
                String key = prefix + suffix;
                int viewId = getResources().getIdentifier(key, "id", getPackageName());
                if (viewId != 0) {
                    setText(viewId, data.get(key));
                }
            }
        }

        // Section B (SB Q1-Q10)
        String[] sbPrefixes = {"sb_q1", "sb_q2", "sb_q3", "sb_q4", "sb_q5", "sb_q6", "sb_q7", "sb_q8", "sb_q9", "sb_q10"};
        String[] sbSuffixes = {"_f_0_4", "_m_0_4", "_f_5_15", "_m_5_15", "_f_16_19", "_m_16_19", "_f_20_plus", "_m_20_plus"};

        for (String prefix : sbPrefixes) {
            for (String suffix : sbSuffixes) {
                String key = prefix + suffix;
                int viewId = getResources().getIdentifier(key, "id", getPackageName());
                if (viewId != 0) {
                    setText(viewId, data.get(key));
                }
            }
        }


        setText(R.id.sc_q1, data.get("sc_q1_value"));
        setText(R.id.sc_q2, data.get("sc_q2_value"));
        setText(R.id.sc_q3, data.get("sc_q3_value"));
        setText(R.id.sc_q4, data.get("sc_q4_value"));
        setText(R.id.sc_q5, data.get("sc_q5_value"));
        setText(R.id.sc_q6, data.get("sc_q6_value"));
        setText(R.id.sc_q7, data.get("sc_q7_value"));

        TextView commentsView = findViewById(R.id.txt_comments);
        if (commentsView != null) {
            String comment = data.get("comments");
            commentsView.setText(comment != null && !comment.isEmpty() ? comment : "No comments");
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

package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

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

public class CommunityReportViewActivity extends AppCompatActivity {

    public static final String EXTRA_BASE_ENTITY_ID = "base_entity_id";
    private static final int EDIT_FORM_REQUEST = JsonFormUtils.REQUEST_CODE_GET_JSON;
    private MonthlyReportModel reportModel;
    private String baseEntityId;
    private WebView webView;

    private String getCaseworkerName(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(MonthlyReportDao.PREF_CASEWORKER_NAME, "");
    }

    private void setStep1FieldValue(JSONObject form, String key, String value) {
        try {
            org.json.JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
            for (int i = 0; i < fields.length(); i++) {
                if (fields.getJSONObject(i).getString("key").equals(key)) {
                    fields.getJSONObject(i).put("value", value);
                    break;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_report_view);

        Toolbar toolbar = findViewById(R.id.report_view_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        webView = findViewById(R.id.webview_report);
        if (webView != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
        }
        loadReport();

        setupEditButton();
    }

    private void loadReport() {
        if (baseEntityId != null) {
            reportModel = MonthlyReportDao.getReport(ReportRegisterActivity.REPORT_TABLE_COMMUNITY, baseEntityId);
        }

        if (reportModel != null) {
            populateData();
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
                    JSONObject form = new FormUtils(this).getFormJson(ReportRegisterActivity.REPORT_FORM_COMMUNITY);
                    if (form == null) {
                        return;
                    }
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    form.put("entity_id", reportModel.getBase_entity_id());
                    org.smartregister.chw.core.utils.CoreJsonFormUtils.populateJsonForm(form, reportModel.toValueMap());

                    // Pre-populate caseworkers
                    String caseworkerName = getCaseworkerName(prefs);
                    if (!caseworkerName.isEmpty()) {
                        setStep1FieldValue(form, "caseworker_name", caseworkerName);
                    }

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
            String tableName = ReportRegisterActivity.REPORT_TABLE_COMMUNITY;
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
                        Toast.makeText(this, R.string.report_community_saved, Toast.LENGTH_SHORT).show();
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
        formTag.appVersion = com.bluecodeltd.ecap.chw.BuildConfig.VERSION_CODE;
        formTag.databaseVersion = com.bluecodeltd.ecap.chw.BuildConfig.DATABASE_VERSION;
        return formTag;
    }

    private void populateData() {
        try {
            TextView title = findViewById(R.id.report_view_title);
            if (title != null) {
                title.setText(getString(R.string.report_community_title) + " - " + reportModel.getReporting_month());
            }

            java.io.InputStream is = getAssets().open("community_report.html");
            int size = is.available();
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            is.close();
            if (read == -1) return;
            String html = new String(buffer);

            Map<String, String> data = reportModel.toValueMap();

            html = html.replace("id=\"district\"></span>", "id=\"district\">" + getValue(data.get("district")) + "</span>");
            html = html.replace("id=\"facility\"></span>", "id=\"facility\">" + getValue(reportModel.getFacility()) + "</span>");
            html = html.replace("id=\"community\"></span>", "id=\"community\">" + getValue(data.get("community_name")) + "</span>");
            html = html.replace("id=\"month\"></span>", "id=\"month\">" + getMonth(reportModel.getReporting_month()) + "</span>");
            html = html.replace("id=\"year\"></span>", "id=\"year\">" + getYear(reportModel.getReporting_month()) + "</span>");

            html = html.replace("id=\"type_1\"></span>", "id=\"type_1\">" + getValue(data.get("illness_type")) + "</span>");
            html = html.replace("id=\"when_1\"></span>", "id=\"when_1\">" + getValue(data.get("date_event_happened")) + "</span>");
            html = html.replace("id=\"where_1\"></span>", "id=\"where_1\">" + getValue(data.get("event_location")) + "</span>");
            html = html.replace("id=\"action_1\"></span>", "id=\"action_1\">" + getValue(data.get("action_taken")) + "</span>");
            html = html.replace("id=\"mentor_1\"></span>", "id=\"mentor_1\">" + getValue(data.get("super_mentor_name")) + "</span>");

            // Affected
            html = html.replace("id=\"af04_1\"></span>", "id=\"af04_1\">" + getValue(data.get("affected_female_0_4")) + "</span>");
            html = html.replace("id=\"af59_1\"></span>", "id=\"af59_1\">" + getValue(data.get("affected_female_5_9")) + "</span>");
            html = html.replace("id=\"af1017_1\"></span>", "id=\"af1017_1\">" + getValue(data.get("affected_female_10_17")) + "</span>");
            html = html.replace("id=\"af18plus_1\"></span>", "id=\"af18plus_1\">" + getValue(data.get("affected_female_18_plus")) + "</span>");
            html = html.replace("id=\"am04_1\"></span>", "id=\"am04_1\">" + getValue(data.get("affected_male_0_4")) + "</span>");
            html = html.replace("id=\"am59_1\"></span>", "id=\"am59_1\">" + getValue(data.get("affected_male_5_9")) + "</span>");
            html = html.replace("id=\"am1017_1\"></span>", "id=\"am1017_1\">" + getValue(data.get("affected_male_10_17")) + "</span>");
            html = html.replace("id=\"am18plus_1\"></span>", "id=\"am18plus_1\">" + getValue(data.get("affected_male_18_plus")) + "</span>");

            // Died
            html = html.replace("id=\"df04_1\"></span>", "id=\"df04_1\">" + getValue(data.get("died_female_0_4")) + "</span>");
            html = html.replace("id=\"df59_1\"></span>", "id=\"df59_1\">" + getValue(data.get("died_female_5_9")) + "</span>");
            html = html.replace("id=\"df1017_1\"></span>", "id=\"df1017_1\">" + getValue(data.get("died_female_10_17")) + "</span>");
            html = html.replace("id=\"df18plus_1\"></span>", "id=\"df18plus_1\">" + getValue(data.get("died_female_18_plus")) + "</span>");
            html = html.replace("id=\"dm04_1\"></span>", "id=\"dm04_1\">" + getValue(data.get("died_male_0_4")) + "</span>");
            html = html.replace("id=\"dm59_1\"></span>", "id=\"dm59_1\">" + getValue(data.get("died_male_5_9")) + "</span>");
            html = html.replace("id=\"dm1017_1\"></span>", "id=\"dm1017_1\">" + getValue(data.get("died_male_10_17")) + "</span>");
            html = html.replace("id=\"dm18plus_1\"></span>", "id=\"dm18plus_1\">" + getValue(data.get("died_male_18_plus")) + "</span>");

            html = html.replace("id=\"supervisor\"></span>", "id=\"supervisor\">" + getValue(data.get("supervisor_name")) + "</span>");
            html = html.replace("id=\"date_reviewed\"></span>", "id=\"date_reviewed\">" + getValue(data.get("date_reviewed")) + "</span>");

            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getValue(String value) {
        return value != null && !value.isEmpty() ? value : "";
    }

    private String getMonth(String date) {
        if (date == null || date.isEmpty()) return "";
        try {
            // Assume date format is dd-MM-yyyy or similar
            String[] parts = date.split("-");
            if (parts.length >= 2) return parts[1];
        } catch (Exception ignored) {}
        return "";
    }

    private String getYear(String date) {
        if (date == null || date.isEmpty()) return "";
        try {
            String[] parts = date.split("-");
            if (parts.length >= 3) return parts[2];
        } catch (Exception ignored) {}
        return "";
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

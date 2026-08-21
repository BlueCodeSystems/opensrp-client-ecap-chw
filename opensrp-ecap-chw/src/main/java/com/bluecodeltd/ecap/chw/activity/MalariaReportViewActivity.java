package com.bluecodeltd.ecap.chw.activity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MalariaReportViewActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_malaria_report_view);

        Toolbar toolbar = findViewById(R.id.report_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        loadReport();

        setupEditButton();
        setupNavigation();
        setupExpandableSections();
        setupExpandCollapseButtons();
    }

    private void loadReport() {
        if (baseEntityId != null) {
            reportModel = MonthlyReportDao.getReport(ReportRegisterActivity.REPORT_TABLE_MALARIA, baseEntityId);
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

    private void setupNavigation() {
        View tabA = findViewById(R.id.tab_a);
        View tabB = findViewById(R.id.tab_b);
        View tabC = findViewById(R.id.tab_c);
        View tabComments = findViewById(R.id.tab_comments);

        if (tabA != null) tabA.setOnClickListener(v -> scrollToView(findViewById(R.id.sec_a)));
        if (tabB != null) tabB.setOnClickListener(v -> scrollToView(findViewById(R.id.sec_b)));
        if (tabC != null) tabC.setOnClickListener(v -> scrollToView(findViewById(R.id.sec_c)));
        if (tabComments != null) tabComments.setOnClickListener(v -> scrollToView(findViewById(R.id.sec_comments)));

        for (int i = 1; i <= 10; i++) {
            int chipAId = getResources().getIdentifier("chip_a_q" + i, "id", getPackageName());
            int panelAId = getResources().getIdentifier("panel_a_q" + i, "id", getPackageName());
            View chipA = findViewById(chipAId);
            View panelA = findViewById(panelAId);
            if (chipA != null && panelA != null) {
                chipA.setOnClickListener(v -> scrollToView(panelA));
            }

            int chipBId = getResources().getIdentifier("chip_b_q" + i, "id", getPackageName());
            int panelBId = getResources().getIdentifier("panel_b_q" + i, "id", getPackageName());
            View chipB = findViewById(chipBId);
            View panelB = findViewById(panelBId);
            if (chipB != null && panelB != null) {
                chipB.setOnClickListener(v -> scrollToView(panelB));
            }
        }
    }

    private void scrollToView(View view) {
        if (view != null) {
            findViewById(R.id.report_scroll).post(() ->
                    ((androidx.core.widget.NestedScrollView) findViewById(R.id.report_scroll)).smoothScrollTo(0, view.getTop())
            );
        }
    }

    private void setupExpandableSections() {
        for (int i = 1; i <= 10; i++) {
            // Section A
            setupExpandableSection("a", i);
            // Section B
            setupExpandableSection("b", i);
        }
    }

    private void setupExpandableSection(String section, int index) {
        int headerId = getResources().getIdentifier("header_" + section + "_q" + index, "id", getPackageName());
        int bodyId = getResources().getIdentifier("body_" + section + "_q" + index, "id", getPackageName());

        View header = findViewById(headerId);
        View body = findViewById(bodyId);

        if (header != null && body != null) {
            header.setOnClickListener(v -> toggleSection(header, body));
        }
    }

    private void toggleSection(View header, View body) {
        boolean isExpanded = body.getVisibility() == View.VISIBLE;
        body.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        
        // Update indicator
        if (header instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) header;
            if (group.getChildCount() > 1) {
                View indicator = group.getChildAt(1);
                if (indicator instanceof TextView) {
                    ((TextView) indicator).setText(isExpanded ? "▸" : "▾");
                }
            }
        }
    }

    private void setupExpandCollapseButtons() {
        View btnExpand = findViewById(R.id.btn_expand_all);
        View btnCollapse = findViewById(R.id.btn_collapse_all);

        if (btnExpand != null) {
            btnExpand.setOnClickListener(v -> setAllSectionsVisibility(View.VISIBLE));
        }

        if (btnCollapse != null) {
            btnCollapse.setOnClickListener(v -> setAllSectionsVisibility(View.GONE));
        }
    }

    private void setAllSectionsVisibility(int visibility) {
        String indicator = (visibility == View.VISIBLE) ? "▾" : "▸";
        for (int i = 1; i <= 10; i++) {
            updateSectionVisibility("a", i, visibility, indicator);
            updateSectionVisibility("b", i, visibility, indicator);
        }
    }

    private void updateSectionVisibility(String section, int index, int visibility, String indicatorText) {
        int headerId = getResources().getIdentifier("header_" + section + "_q" + index, "id", getPackageName());
        int bodyId = getResources().getIdentifier("body_" + section + "_q" + index, "id", getPackageName());

        View header = findViewById(headerId);
        View body = findViewById(bodyId);

        if (body != null) {
            body.setVisibility(visibility);
        }

        if (header instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) header;
            if (group.getChildCount() > 1) {
                View indicator = group.getChildAt(1);
                if (indicator instanceof TextView) {
                    ((TextView) indicator).setText(indicatorText);
                }
            }
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
                    Snackbar.make(findViewById(R.id.sec_a), "Beneficiary is inactive or de-registered", Snackbar.LENGTH_LONG).show();
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

                    Intent intent = new Intent(this, ReportFormActivity.class);
                    Form wizardForm = new Form();
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
                    startActivityForResult(intent, EDIT_FORM_REQUEST);
                } catch (Exception e) {
                    Timber.e(e);
                    Snackbar.make(findViewById(R.id.sec_a), "Unable to open form", Snackbar.LENGTH_LONG).show();
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
                        Toast.makeText(this, R.string.report_malaria_saved, Toast.LENGTH_SHORT).show();
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
        TextView title = findViewById(R.id.report_title);
        title.setText(getString(R.string.malaria_report_title, reportModel.getReporting_month()));

        TextView reportMeta = findViewById(R.id.report_meta);
        if (reportMeta != null) {
            reportMeta.setText(reportModel.getReporting_month() + " | " + reportModel.getFacility());
        }

        Map<String, String> data = reportModel.toValueMap();

        // Section A (Q1-Q10)
        String[] qPrefixes = {"q1", "q2", "q3", "q4", "q5", "q6", "q7", "q8", "q9", "q10"};
        for (String prefix : qPrefixes) {
            int totalF = 0;
            int totalM = 0;

            // Age Groups (1-4)
            String[] ageSuffixes = {"_f_0_4", "_m_0_4", "_f_5_15", "_m_5_15", "_f_16_19", "_m_16_19", "_f_20_plus", "_m_20_plus"};
            for (String suffix : ageSuffixes) {
                String key = prefix + suffix;
                int viewId = getResources().getIdentifier("sa_" + key, "id", getPackageName());
                if (viewId != 0) {
                    int val = parseVal(data.get(key));
                    setText(viewId, String.valueOf(val));
                    if (suffix.contains("_f_")) totalF += val;
                    if (suffix.contains("_m_")) totalM += val;
                }
            }

            // Age Group Total (5)
            setText(getResources().getIdentifier("sa_" + prefix + "_total_f", "id", getPackageName()), String.valueOf(totalF));
            setText(getResources().getIdentifier("sa_" + prefix + "_total_m", "id", getPackageName()), String.valueOf(totalM));

            // Sub-Populations (6-13)
            String[] subPopSuffixes = {"calhiv", "hei", "wlhiv", "sv", "agyw", "hiv_pos", "siblings", "caregivers"};
            int subTotalSumF = 0;
            int subTotalSumM = 0;
            for (String sub : subPopSuffixes) {
                String keyF = prefix + "_f_" + sub;
                String keyM = prefix + "_m_" + sub;

                int valF = parseVal(data.get(keyF));
                int valM = parseVal(data.get(keyM));

                // Renders on BOTH columns in the layout
                setText(getResources().getIdentifier("sa_" + keyF, "id", getPackageName()), String.valueOf(valF));
                int viewIdM = getResources().getIdentifier("sa_" + keyM, "id", getPackageName());
                if (viewIdM != 0) {
                    setText(viewIdM, String.valueOf(valM));
                }

                // Strictly summing all indicators for the Sub-Population Total (Row 14)
                subTotalSumF += valF;
                subTotalSumM += valM;
            }
            setText(getResources().getIdentifier("sa_" + prefix + "_sub_total_f", "id", getPackageName()), String.valueOf(subTotalSumF));
            setText(getResources().getIdentifier("sa_" + prefix + "_sub_total_m", "id", getPackageName()), String.valueOf(subTotalSumM));
        }

        // Section B (SB Q1-Q10)
        String[] sbPrefixes = {"sb_q1", "sb_q2", "sb_q3", "sb_q4", "sb_q5", "sb_q6", "sb_q7", "sb_q8", "sb_q9", "sb_q10"};
        for (String prefix : sbPrefixes) {
            int totalF = 0;
            int totalM = 0;
            String[] sbSuffixes = {"_f_0_4", "_m_0_4", "_f_5_15", "_m_5_15", "_f_16_19", "_m_16_19", "_f_20_plus", "_m_20_plus"};
            for (String suffix : sbSuffixes) {
                String key = prefix + suffix;
                int viewId = getResources().getIdentifier(key, "id", getPackageName());
                if (viewId != 0) {
                    int val = parseVal(data.get(key));
                    setText(viewId, String.valueOf(val));
                    if (suffix.contains("_f_")) totalF += val;
                    if (suffix.contains("_m_")) totalM += val;
                }
            }
            setText(getResources().getIdentifier(prefix + "_total_f", "id", getPackageName()), String.valueOf(totalF));
            setText(getResources().getIdentifier(prefix + "_total_m", "id", getPackageName()), String.valueOf(totalM));
        }

        // Section C
        int scTotal = 0;
        int indicatorsCount = 7;
        for (int i = 1; i <= indicatorsCount; i++) {
            String key = "sc_q" + i + "_value";
            int val = parseVal(data.get(key));
            setText(getResources().getIdentifier("sc_q" + i, "id", getPackageName()), String.valueOf(val));
            scTotal += val;
        }
        setText(R.id.sc_total, String.valueOf(scTotal));

        TextView commentsView = findViewById(R.id.txt_comments);
        if (commentsView != null) {
            String comment = data.get("comments");
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

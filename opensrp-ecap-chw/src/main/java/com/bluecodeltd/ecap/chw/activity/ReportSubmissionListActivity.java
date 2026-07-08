package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.app.Dialog;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.ReportSubmissionAdapter;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.dao.MonthlyReportDao;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.MonthlyReportModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.google.android.material.snackbar.Snackbar;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportSubmissionListActivity extends AppCompatActivity {

    private static final int LIST_FORM_REQUEST = JsonFormUtils.REQUEST_CODE_GET_JSON;

    private RecyclerView recyclerView;
    private View emptyView;
    private TextView metaText;
    private Spinner monthFilterSpinner;
    private final List<MonthlyReportModel> allItems = new ArrayList<>();
    private final List<MonthlyReportModel> items = new ArrayList<>();
    private final List<PeriodOption> periodOptions = new ArrayList<>();
    private ReportSubmissionAdapter adapter;
    private String reportType;
    private String selectedPeriodKey = "";
    private ArrayAdapter<String> monthFilterAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_submission_list);

        reportType = getIntent() == null ? ReportRegisterActivity.REPORT_TYPE_MALARIA : getIntent().getStringExtra(ReportRegisterActivity.EXTRA_REPORT_TYPE);
        if (reportType == null) {
            reportType = ReportRegisterActivity.REPORT_TYPE_MALARIA;
        }

        Toolbar toolbar = findViewById(R.id.report_submission_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        TextView title = findViewById(R.id.report_submission_title_text);
        if (title != null) {
            title.setText(getScreenTitle());
        }
        metaText = findViewById(R.id.report_submission_meta_text);
        monthFilterSpinner = findViewById(R.id.report_submission_month_filter);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        recyclerView = findViewById(R.id.report_submission_recycler);
        emptyView = findViewById(R.id.report_submission_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new ReportSubmissionAdapter(this, items, new ReportSubmissionAdapter.Listener() {
            @Override
            public void onView(MonthlyReportModel item) {
                openViewReport(item);
            }

            @Override
            public void onEdit(MonthlyReportModel item) {
                openEditForm(item);
            }

            @Override
            public void onDelete(MonthlyReportModel item) {
                deleteReport(item);
            }
        });
        recyclerView.setAdapter(adapter);
        setupMonthFilter();
        loadReports();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports();
    }

    private void loadReports() {
        allItems.clear();
        allItems.addAll(filterVisibleReports(MonthlyReportDao.getReports(getTableName())));
        rebuildMonthFilterOptions();
        applyMonthFilter();
        updateMetaText();
        if (emptyView != null) {
            emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void setupMonthFilter() {
        if (monthFilterSpinner == null) {
            return;
        }
        monthFilterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        monthFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthFilterSpinner.setAdapter(monthFilterAdapter);
        monthFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = monthFilterAdapter.getItem(position);
                selectedPeriodKey = getPeriodKeyForLabel(selected);
                applyMonthFilter();
                updateMetaText();
                if (emptyView != null) {
                    emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPeriodKey = "";
                applyMonthFilter();
                updateMetaText();
            }
        });
    }

    private void rebuildMonthFilterOptions() {
        if (monthFilterAdapter == null) {
            return;
        }
        Set<String> uniquePeriods = new LinkedHashSet<>();
        periodOptions.clear();
        for (MonthlyReportModel item : allItems) {
            PeriodOption option = buildPeriodOption(item.getReporting_month());
            if (option != null) {
                uniquePeriods.add(option.key);
            }
        }
        List<PeriodOption> sortedPeriods = allItems.stream()
                .map(item -> buildPeriodOption(item.getReporting_month()))
                .filter(option -> option != null)
                .collect(Collectors.toList());
        List<PeriodOption> distinctPeriods = sortedPeriods.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(option -> option.key, option -> option, (first, second) -> first, java.util.LinkedHashMap::new),
                        map -> new ArrayList<>(map.values())));
        distinctPeriods.sort((first, second) -> comparePeriodDesc(first.key, second.key));
        periodOptions.addAll(distinctPeriods);

        List<String> options = periodOptions.stream()
                .map(option -> option.label)
                .collect(Collectors.toList());
        options.add(0, getString(R.string.report_submission_month_all));
        monthFilterAdapter.clear();
        monthFilterAdapter.addAll(options);
        monthFilterAdapter.notifyDataSetChanged();
        int selectedIndex = findSelectedPeriodIndex(options);
        if (selectedIndex < 0) {
            selectedIndex = 0;
            selectedPeriodKey = "";
        }
        if (monthFilterSpinner.getSelectedItemPosition() != selectedIndex) {
            monthFilterSpinner.setSelection(selectedIndex, false);
        }
    }

    private void applyMonthFilter() {
        items.clear();
        if (selectedPeriodKey == null || selectedPeriodKey.trim().isEmpty()) {
            items.addAll(allItems);
        } else {
            for (MonthlyReportModel item : allItems) {
                PeriodOption option = buildPeriodOption(item.getReporting_month());
                if (option != null && selectedPeriodKey.equals(option.key)) {
                    items.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateMetaText() {
        if (metaText == null) {
            return;
        }
        if (items.size() == allItems.size()) {
            metaText.setText(getString(R.string.report_submission_meta, items.size()));
        } else {
            metaText.setText(getString(R.string.report_submission_meta_filtered, items.size(), allItems.size()));
        }
    }

    private int comparePeriodDesc(String first, String second) {
        Date firstDate = parsePeriodKey(first);
        Date secondDate = parsePeriodKey(second);
        if (firstDate == null && secondDate == null) {
            return second.compareToIgnoreCase(first);
        }
        if (firstDate == null) {
            return 1;
        }
        if (secondDate == null) {
            return -1;
        }
        return secondDate.compareTo(firstDate);
    }

    private Date parsePeriodKey(String value) {
        try {
            return new SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private PeriodOption buildPeriodOption(String reportingMonth) {
        if (reportingMonth == null || reportingMonth.trim().isEmpty()) {
            return null;
        }
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(reportingMonth.trim());
            if (date == null) {
                return null;
            }
            String key = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(date);
            String label = new SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(date);
            return new PeriodOption(key, label);
        } catch (Exception e) {
            return null;
        }
    }

    private int findSelectedPeriodIndex(List<String> options) {
        if (selectedPeriodKey == null || selectedPeriodKey.trim().isEmpty()) {
            return 0;
        }
        for (int i = 0; i < periodOptions.size(); i++) {
            PeriodOption option = periodOptions.get(i);
            if (selectedPeriodKey.equals(option.key)) {
                return i + 1;
            }
        }
        return 0;
    }

    private String getPeriodKeyForLabel(String label) {
        if (label == null || label.trim().isEmpty() || getString(R.string.report_submission_month_all).equalsIgnoreCase(label.trim())) {
            return "";
        }
        for (PeriodOption option : periodOptions) {
            if (option.label.equalsIgnoreCase(label.trim())) {
                return option.key;
            }
        }
        return "";
    }

    private String getScreenTitle() {
        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) {
            return getString(R.string.report_nutrition);
        }
        if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) {
            return getString(R.string.report_tb);
        }
        if (ReportRegisterActivity.REPORT_TYPE_COMMUNITY.equals(reportType)) {
            return getString(R.string.report_community_title);
        }
        return getString(R.string.menu_malaria);
    }

    private String getFormName() {
        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) {
            return ReportRegisterActivity.REPORT_FORM_NUTRITION;
        }
        if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) {
            return ReportRegisterActivity.REPORT_FORM_TB;
        }
        if (ReportRegisterActivity.REPORT_TYPE_COMMUNITY.equals(reportType)) {
            return ReportRegisterActivity.REPORT_FORM_COMMUNITY;
        }
        return ReportRegisterActivity.REPORT_FORM_MALARIA;
    }

    private String getTableName() {
        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) {
            return ReportRegisterActivity.REPORT_TABLE_NUTRITION;
        }
        if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) {
            return ReportRegisterActivity.REPORT_TABLE_TB;
        }
        if (ReportRegisterActivity.REPORT_TYPE_COMMUNITY.equals(reportType)) {
            return ReportRegisterActivity.REPORT_TABLE_COMMUNITY;
        }
        return ReportRegisterActivity.REPORT_TABLE_MALARIA;
    }

    private String getEncounterType() {
        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) {
            return ReportRegisterActivity.REPORT_FORM_ENCOUNTER_NUTRITION;
        }
        if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) {
            return ReportRegisterActivity.REPORT_FORM_ENCOUNTER_TB;
        }
        if (ReportRegisterActivity.REPORT_TYPE_COMMUNITY.equals(reportType)) {
            return ReportRegisterActivity.REPORT_FORM_ENCOUNTER_COMMUNITY;
        }
        return ReportRegisterActivity.REPORT_FORM_ENCOUNTER_MALARIA;
    }

    private void openViewReport(MonthlyReportModel item) {
        Intent intent;
        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) {
            intent = new Intent(this, MonthlyNutritionReportViewActivity.class);
        } else if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) {
            intent = new Intent(this, MonthlyTbReportViewActivity.class);
        } else if (ReportRegisterActivity.REPORT_TYPE_COMMUNITY.equals(reportType)) {
            intent = new Intent(this, CommunityReportViewActivity.class);
        } else {
            intent = new Intent(this, MalariaReportViewActivity.class);
        }
        intent.putExtra(MalariaReportViewActivity.EXTRA_BASE_ENTITY_ID, item.getBase_entity_id());
        startActivity(intent);
    }

    private void openEditForm(MonthlyReportModel item) {
        try {
            Threading.io(() -> {
                CaseStatusModel caseStatusModel = null;
                try {
                    caseStatusModel = IndexPersonDao.getCaseStatus(item.getBase_entity_id());
                } catch (Exception ignored) {
                }

                CaseStatusModel finalCaseStatusModel = caseStatusModel;
                Threading.main(() -> {
                    String status = finalCaseStatusModel != null ? finalCaseStatusModel.getCase_status() : null;
                    if (status != null && ("0".equals(status) || "2".equals(status))) {
                        showInactiveDialog(finalCaseStatusModel);
                        return;
                    }

                    try {
                        JSONObject form = new FormUtils(this).getFormJson(getFormName());
                        if (form == null) {
                            return;
                        }
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                        if (item.getAdditionalField("caseworker_name") == null || item.getAdditionalField("caseworker_name").trim().isEmpty()) {
                            item.setAdditionalField("caseworker_name", getCaseworkerName(prefs));
                        }
                        form.put("entity_id", item.getBase_entity_id());
                        org.smartregister.chw.core.utils.CoreJsonFormUtils.populateJsonForm(form, item.toValueMap());
                        startFormActivity(form);
                    } catch (Exception e) {
                        timber.log.Timber.e(e);
                        Snackbar.make(recyclerView, "Unable to open form", Snackbar.LENGTH_LONG).show();
                    }
                });
            });
        } catch (Exception e) {
            timber.log.Timber.e(e);
            Snackbar.make(recyclerView, "Unable to open form", Snackbar.LENGTH_LONG).show();
        }
    }

    private String getCaseworkerName(SharedPreferences prefs) {
        String caseworkerName = prefs.getString("caseworker_name", "");
        if (caseworkerName != null && !caseworkerName.trim().isEmpty()) {
            return caseworkerName.trim();
        }
        String username = prefs.getString("last_logged_in_username", "");
        return username == null ? "" : username.trim();
    }

    private void deleteReport(MonthlyReportModel item) {
        try {
            FormUtils formUtils = new FormUtils(this);
            JSONObject form = formUtils.getFormJson(getFormName());
            if (form == null) {
                return;
            }
            item.setDelete_status("1");
            form.put("delete_status", "1");
            form.put("entity_id", item.getBase_entity_id());
            org.smartregister.chw.core.utils.CoreJsonFormUtils.populateJsonForm(form, new ObjectMapper().convertValue(item, java.util.Map.class));
            ReportEventClient reportEventClient = processRegistration(form.toString());
            if (reportEventClient != null) {
                saveRegistration(reportEventClient, true);
            }
        } catch (Exception e) {
            timber.log.Timber.e(e);
            Snackbar.make(recyclerView, "Unable to delete report", Snackbar.LENGTH_LONG).show();
        }
    }

    private List<MonthlyReportModel> filterVisibleReports(List<MonthlyReportModel> source) {
        List<MonthlyReportModel> visible = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return visible;
        }
        for (MonthlyReportModel item : source) {
            if (!isSoftDeleted(item)) {
                visible.add(item);
            }
        }
        return visible;
    }

    private boolean isSoftDeleted(MonthlyReportModel item) {
        if (item == null) {
            return false;
        }
        String deleteStatus = item.getDelete_status();
        if (deleteStatus == null || deleteStatus.trim().isEmpty()) {
            deleteStatus = item.getAdditionalField("delete_status");
        }
        return "1".equals(deleteStatus == null ? null : deleteStatus.trim());
    }

    private void showInactiveDialog(CaseStatusModel caseStatusModel) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();

        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        String firstName = caseStatusModel != null && caseStatusModel.getFirst_name() != null
                ? caseStatusModel.getFirst_name()
                : "This beneficiary";
        String lastName = caseStatusModel != null && caseStatusModel.getLast_name() != null
                ? caseStatusModel.getLast_name()
                : "";
        if (dialogMessage != null) {
            dialogMessage.setText(firstName + (lastName.isEmpty() ? "" : (" " + lastName))
                    + " was either de-registered or inactive in the program");
        }

        Button dialogButton = dialog.findViewById(R.id.dialog_button);
        if (dialogButton != null) {
            dialogButton.setOnClickListener(v -> dialog.dismiss());
        }
    }

    private void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, ReportFormActivity.class);
        Form wizardForm = new Form();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
        startActivityForResult(intent, LIST_FORM_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LIST_FORM_REQUEST && resultCode == RESULT_OK && data != null) {
            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
            if (jsonString == null) {
                return;
            }
            try {
                JSONObject jsonFormObject = new JSONObject(jsonString);
                if (getEncounterType().equalsIgnoreCase(jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))) {
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

    private void saveFromFormJson(String jsonString, boolean isEditMode) {
        try {
            ReportEventClient reportEventClient = processRegistration(jsonString);
            if (reportEventClient != null) {
                saveRegistration(reportEventClient, isEditMode);
            }
        } catch (Exception e) {
            timber.log.Timber.e(e);
        }
    }

    private void openReportView(String encounterType, String baseEntityId) {
        // Don't open view if we are deleting
        if (baseEntityId == null) return;
        
        Intent intent = null;
        if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_COMMUNITY.equalsIgnoreCase(encounterType)) {
            intent = new Intent(this, CommunityReportViewActivity.class);
        } else if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_MALARIA.equalsIgnoreCase(encounterType)) {
            intent = new Intent(this, MalariaReportViewActivity.class);
        } else if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_NUTRITION.equalsIgnoreCase(encounterType)) {
            intent = new Intent(this, MonthlyNutritionReportViewActivity.class);
        } else if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_TB.equalsIgnoreCase(encounterType)) {
            intent = new Intent(this, MonthlyTbReportViewActivity.class);
        }

        if (intent != null) {
            intent.putExtra(CommunityReportViewActivity.EXTRA_BASE_ENTITY_ID, baseEntityId);
            startActivity(intent);
        }
    }

    private boolean isMonthlyReportEncounter(String encounterType) {
        return ReportRegisterActivity.REPORT_FORM_ENCOUNTER_MALARIA.equalsIgnoreCase(encounterType)
                || ReportRegisterActivity.REPORT_FORM_ENCOUNTER_NUTRITION.equalsIgnoreCase(encounterType)
                || ReportRegisterActivity.REPORT_FORM_ENCOUNTER_TB.equalsIgnoreCase(encounterType)
                || ReportRegisterActivity.REPORT_FORM_ENCOUNTER_COMMUNITY.equalsIgnoreCase(encounterType);
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
        if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_COMMUNITY.equalsIgnoreCase(encounterType)) {
            return ReportRegisterActivity.REPORT_TABLE_COMMUNITY;
        }
        return null;
    }

    private ReportEventClient processRegistration(String jsonString) {
        try {
            JSONObject formJsonObject = new JSONObject(jsonString);
            String encounterType = formJsonObject.getString(JsonFormConstants.ENCOUNTER_TYPE);
            if (!isMonthlyReportEncounter(encounterType)) {
                return null;
            }
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
            Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId, encounterType, tableName);
            if (event != null && (event.getFormSubmissionId() == null || event.getFormSubmissionId().isEmpty())) {
                event.setFormSubmissionId(org.smartregister.util.JsonFormUtils.generateRandomUUIDString());
            }
            org.smartregister.chw.core.utils.CoreJsonFormUtils.tagSyncMetadata(getAllSharedPreferences(), event);
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
                        loadReports();
                        openReportView(event.getEventType(), event.getBaseEntityId());
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

    private String getSavedToastMessage(String encounterType) {
        if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_NUTRITION.equalsIgnoreCase(encounterType)) {
            return getString(R.string.report_nutrition_saved);
        }
        if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_TB.equalsIgnoreCase(encounterType)) {
            return getString(R.string.report_tb_saved);
        }
        if (ReportRegisterActivity.REPORT_FORM_ENCOUNTER_COMMUNITY.equalsIgnoreCase(encounterType)) {
            return getString(R.string.report_community_saved);
        }
        return getString(R.string.report_malaria_saved);
    }

    private FormTag getFormTag() {
        FormTag formTag = new FormTag();
        AllSharedPreferences allSharedPreferences = getAllSharedPreferences();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
        return formTag;
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

    private static class PeriodOption {
        private final String key;
        private final String label;

        private PeriodOption(String key, String label) {
            this.key = key;
            this.label = label;
        }
    }
}

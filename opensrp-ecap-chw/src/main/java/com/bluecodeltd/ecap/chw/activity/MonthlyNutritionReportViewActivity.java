package com.bluecodeltd.ecap.chw.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.MonthlyReportDao;
import com.bluecodeltd.ecap.chw.dao.IndexPersonDao;
import com.bluecodeltd.ecap.chw.model.MonthlyReportModel;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.google.android.material.snackbar.Snackbar;
import com.vijay.jsonwizard.constants.JsonFormConstants;
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
import timber.log.Timber;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MonthlyNutritionReportViewActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_monthly_nutrition_report_view);

        Toolbar toolbar = findViewById(R.id.report_view_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        applyLightStatusBar();
        TextView toolbarTitle = findViewById(R.id.report_view_title);
        if (toolbarTitle != null) {
            toolbarTitle.setText("Monthly Nutrition Report");
        }
        findViewById(R.id.report_view_back_button).setOnClickListener(v -> finish());

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        loadReport();

        setupTabNavigation();
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
            reportModel = MonthlyReportDao.getReport(ReportRegisterActivity.REPORT_TABLE_NUTRITION, baseEntityId);
        }

        if (reportModel != null) {
            populateData();
        }
    }

    private void setupTabNavigation() {
        findViewById(R.id.tab_a).setOnClickListener(v -> scrollToSection(R.id.sec_a));
        findViewById(R.id.tab_b).setOnClickListener(v -> scrollToSection(R.id.sec_b));
        findViewById(R.id.tab_cd).setOnClickListener(v -> scrollToSection(R.id.sec_cd));
        findViewById(R.id.tab_efg).setOnClickListener(v -> scrollToSection(R.id.sec_efg));
        findViewById(R.id.tab_hi).setOnClickListener(v -> scrollToSection(R.id.sec_hi));
    }

    private void scrollToSection(int sectionId) {
        View section = findViewById(sectionId);
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.report_scroll);
        if (section != null && scrollView != null) {
            scrollView.smoothScrollTo(0, section.getTop());
            updateTabStyles(sectionId);
        }
    }

    private void updateTabStyles(int sectionId) {
        int activeColor = Color.parseColor("#FFFFFF");
        int inactiveColor = Color.parseColor("#21603F");
        int activeText = Color.parseColor("#1B3A4B");
        int inactiveText = Color.parseColor("#CFE3D6");

        setTabStyle(R.id.tab_a, sectionId == R.id.sec_a ? activeColor : inactiveColor, sectionId == R.id.sec_a ? activeText : inactiveText);
        setTabStyle(R.id.tab_b, sectionId == R.id.sec_b ? activeColor : inactiveColor, sectionId == R.id.sec_b ? activeText : inactiveText);
        setTabStyle(R.id.tab_cd, sectionId == R.id.sec_cd ? activeColor : inactiveColor, sectionId == R.id.sec_cd ? activeText : inactiveText);
        setTabStyle(R.id.tab_efg, sectionId == R.id.sec_efg ? activeColor : inactiveColor, sectionId == R.id.sec_efg ? activeText : inactiveText);
        setTabStyle(R.id.tab_hi, sectionId == R.id.sec_hi ? activeColor : inactiveColor, sectionId == R.id.sec_hi ? activeText : inactiveText);
    }

    private void setTabStyle(int tabId, int bgColor, int textColor) {
        TextView tab = findViewById(tabId);
        if (tab != null) {
            tab.setBackgroundColor(bgColor);
            tab.setTextColor(textColor);
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
            CaseStatusModel caseStatusModel = null;
            try {
                caseStatusModel = IndexPersonDao.getCaseStatus(reportModel.getBase_entity_id());
            } catch (Exception ignored) {
            }

            CaseStatusModel finalCaseStatusModel = caseStatusModel;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                String status = finalCaseStatusModel != null ? finalCaseStatusModel.getCase_status() : null;
                if ("0".equals(status) || "2".equals(status)) {
                    Snackbar.make(findViewById(R.id.report_scroll), "Beneficiary is inactive or de-registered", Snackbar.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject form = new FormUtils(this).getFormJson(ReportRegisterActivity.REPORT_FORM_NUTRITION);
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
                    Snackbar.make(findViewById(R.id.report_scroll), "Unable to open form", Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }

    private String getCaseworkerName(SharedPreferences prefs) {
        String caseworkerName = prefs.getString("caseworker_name", "");
        if (!caseworkerName.trim().isEmpty()) {
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
                    List<EventClient> savedEvents = ecSyncHelper.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                    getClientProcessorForJava().processClient(savedEvents);
                    getAllSharedPreferences().saveLastUpdatedAtDate(currentSyncDate.getTime());
                    runOnUiThread(() -> {
                        loadReport();
                        Toast.makeText(this, R.string.report_nutrition_saved, Toast.LENGTH_SHORT).show();
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
        if (title != null) {
            title.setText(getString(R.string.nutrition_report_title, reportModel.getReporting_month()));
        }

        setText(R.id.txt_reporting_month, reportModel.getReporting_month());
        setText(R.id.txt_facility_name, reportModel.getFacility());

        Map<String, String> data = reportModel.toValueMap();

        // Section A
        setText(R.id.subpop_calhiv, data.get("subpop_calhiv"));
        setText(R.id.subpop_hei, data.get("subpop_hei"));
        setText(R.id.subpop_cml_hiv, data.get("subpop_cml_hiv"));
        setText(R.id.subpop_cpbfa, data.get("subpop_cpbfa"));
        setText(R.id.subpop_siblings, data.get("subpop_siblings"));
        calculateAndSetTotal(R.id.tv_a_total, data.get("subpop_calhiv"), data.get("subpop_hei"), data.get("subpop_cml_hiv"), data.get("subpop_cpbfa"), data.get("subpop_siblings"));

        // Section B
        setText(R.id.hh_practicing_diet_diversity, data.get("hh_practicing_diet_diversity"));
        setText(R.id.hh_practicing_exclusive_bf, data.get("hh_practicing_exclusive_bf"));
        setText(R.id.hh_practicing_complementary_feeding, data.get("hh_practicing_complementary_feeding"));
        setText(R.id.hh_wash_activities, data.get("hh_wash_activities"));
        setText(R.id.hh_visited_assessment, data.get("hh_visited_assessment"));
        setText(R.id.ppmam_identified, data.get("ppmam_identified"));
        setText(R.id.ppmam_referred_commenced, data.get("ppmam_referred_commenced"));
        setText(R.id.other_children_pmam, data.get("other_children_pmam"));
        setText(R.id.plw_art_pmtct_nutrition_assessment, data.get("plw_art_pmtct_nutrition_assessment"));
        setText(R.id.plw_received_ifas, data.get("plw_received_ifas"));
        setText(R.id.hh_food_insecurity_counselled, data.get("hh_food_insecurity_counselled"));
        calculateAndSetTotal(R.id.tv_b_total, data.get("hh_practicing_diet_diversity"), data.get("hh_practicing_exclusive_bf"), data.get("hh_practicing_complementary_feeding"), data.get("hh_wash_activities"), data.get("hh_visited_assessment"), data.get("ppmam_identified"), data.get("ppmam_referred_commenced"), data.get("other_children_pmam"), data.get("plw_art_pmtct_nutrition_assessment"), data.get("plw_received_ifas"), data.get("hh_food_insecurity_counselled"));

        // Section C
        setText(R.id.mnp_children_6_23_received, data.get("mnp_children_6_23_received"));
        setText(R.id.mnp_plw_received, data.get("mnp_plw_received"));
        setText(R.id.vita_children_6_11_months, data.get("vita_children_6_11_months"));
        setText(R.id.vita_children_12_59_months, data.get("vita_children_12_59_months"));
        setText(R.id.vita_plw_supplemented, data.get("vita_plw_supplemented"));
        setText(R.id.deworming_children_12_59, data.get("deworming_children_12_59"));
        setText(R.id.deworming_plw, data.get("deworming_plw"));
        calculateAndSetTotal(R.id.tv_c_total, data.get("mnp_children_6_23_received"), data.get("mnp_plw_received"), data.get("vita_children_6_11_months"), data.get("vita_children_12_59_months"), data.get("vita_plw_supplemented"), data.get("deworming_children_12_59"), data.get("deworming_plw"));

        // Section D
        setText(R.id.ecd_centres_supported_monitoring, data.get("ecd_centres_supported_monitoring"));
        setText(R.id.ecd_centres_with_feeding, data.get("ecd_centres_with_feeding"));
        setText(R.id.ecd_children_enrolled, data.get("ecd_children_enrolled"));
        setText(R.id.ecd_caregivers_trained, data.get("ecd_caregivers_trained"));
        setText(R.id.ecd_developmental_screening, data.get("ecd_developmental_screening"));
        calculateAndSetTotal(R.id.tv_d_total, data.get("ecd_centres_supported_monitoring"), data.get("ecd_centres_with_feeding"), data.get("ecd_children_enrolled"), data.get("ecd_caregivers_trained"), data.get("ecd_developmental_screening"));

        // Section E
        setText(R.id.wfa_underweight, data.get("wfa_underweight"));
        setText(R.id.wfa_overweight, data.get("wfa_overweight"));
        setText(R.id.wfa_normal, data.get("wfa_normal"));
        calculateAndSetTotal(R.id.tv_e_total, data.get("wfa_underweight"), data.get("wfa_overweight"), data.get("wfa_normal"));

        // Section F
        setText(R.id.nutrition_grade_1, data.get("nutrition_grade_1"));
        setText(R.id.nutrition_grade_2, data.get("nutrition_grade_2"));
        setText(R.id.nutrition_nr, data.get("nutrition_nr"));
        calculateAndSetTotal(R.id.tv_f_total, data.get("nutrition_grade_1"), data.get("nutrition_grade_2"), data.get("nutrition_nr"));

        // Section G
        setText(R.id.muac_red_below_11_5, data.get("muac_red_below_11_5"));
        setText(R.id.muac_yellow_11_5_to_12_5, data.get("muac_yellow_11_5_to_12_5"));
        setText(R.id.muac_green_12_5_plus, data.get("muac_green_12_5_plus"));
        setText(R.id.muac_oedema, data.get("muac_oedema"));
        calculateAndSetTotal(R.id.tv_g_total, data.get("muac_red_below_11_5"), data.get("muac_yellow_11_5_to_12_5"), data.get("muac_green_12_5_plus"), data.get("muac_oedema"));

        // Section H
        setText(R.id.sti_referred, data.get("sti_referred"));
        setText(R.id.sti_treated, data.get("sti_treated"));
        calculateAndSetTotal(R.id.tv_h_total, data.get("sti_referred"), data.get("sti_treated"));

        // Section I
        setText(R.id.referral_nutrition_to_health, data.get("referral_nutrition_to_health"));
        setText(R.id.referral_feedback_received, data.get("referral_feedback_received"));
        setText(R.id.referral_date_of_referral, data.get("referral_date_of_referral"));
        setText(R.id.referral_date_of_feedback, data.get("referral_date_of_feedback"));
        setText(R.id.referral_hiv_tb_integration, data.get("referral_hiv_tb_integration"));
        calculateAndSetTotal(R.id.tv_i_total, data.get("referral_nutrition_to_health"), data.get("referral_feedback_received"), data.get("referral_hiv_tb_integration"));

        TextView commentsView = findViewById(R.id.txt_comments);
        if (commentsView != null) {
            String comment = data.get("comment");
            commentsView.setText(comment != null && !comment.isEmpty() ? comment : "No comments");
        }
    }

    private void calculateAndSetTotal(int totalViewId, String... values) {
        int total = 0;
        for (String value : values) {
            try {
                if (value != null && !value.isEmpty()) {
                    total += Integer.parseInt(value);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        TextView totalView = findViewById(totalViewId);
        if (totalView != null) {
            totalView.setText(String.valueOf(total));
        }
    }

    private void setText(int viewId, String value) {
        TextView textView = findViewById(viewId);
        if (textView != null) {
            textView.setText(value != null && !value.isEmpty() ? value : "0");
        }
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

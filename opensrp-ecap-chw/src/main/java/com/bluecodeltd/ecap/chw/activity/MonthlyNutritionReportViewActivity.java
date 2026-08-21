package com.bluecodeltd.ecap.chw.activity;
import com.bluecodeltd.ecap.chw.application.ChwApplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
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
import androidx.core.widget.NestedScrollView;
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
        toolbar.setNavigationOnClickListener(v -> finish());
        applyInkStatusBar();

        baseEntityId = getIntent().getStringExtra(EXTRA_BASE_ENTITY_ID);
        loadReport();

        setupTabLogic();
        setupEditButton();
    }

    private void applyInkStatusBar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor("#1B3A4B"));
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

    private void setupTabLogic() {
        TextView tabA = findViewById(R.id.tab_a);
        TextView tabB = findViewById(R.id.tab_b);
        TextView tabCD = findViewById(R.id.tab_cd);
        TextView tabEFG = findViewById(R.id.tab_efg);
        TextView tabHI = findViewById(R.id.tab_hi);

        View secA = findViewById(R.id.sec_a);
        View secB = findViewById(R.id.sec_b);
        View secCD = findViewById(R.id.sec_cd);
        View secEFG = findViewById(R.id.sec_efg);
        View secHI = findViewById(R.id.sec_hi);

        NestedScrollView scrollView = findViewById(R.id.report_scroll);

        View.OnClickListener tabClickListener = v -> {
            View target = null;
            resetTabs(tabA, tabB, tabCD, tabEFG, tabHI);
            v.setBackgroundColor(Color.WHITE);
            ((TextView) v).setTextColor(Color.parseColor("#1B3A4B"));

            int id = v.getId();
            if (id == R.id.tab_a) target = secA;
            else if (id == R.id.tab_b) target = secB;
            else if (id == R.id.tab_cd) target = secCD;
            else if (id == R.id.tab_efg) target = secEFG;
            else if (id == R.id.tab_hi) target = secHI;

            if (target != null) {
                View finalTarget = target;
                scrollView.post(() -> scrollView.smoothScrollTo(0, finalTarget.getTop()));
            }
        };

        tabA.setOnClickListener(tabClickListener);
        tabB.setOnClickListener(tabClickListener);
        tabCD.setOnClickListener(tabClickListener);
        tabEFG.setOnClickListener(tabClickListener);
        tabHI.setOnClickListener(tabClickListener);
    }

    private void resetTabs(TextView... tabs) {
        for (TextView tab : tabs) {
            tab.setBackgroundColor(Color.parseColor("#21603F"));
            tab.setTextColor(Color.parseColor("#CFE3D6"));
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

            // Parse the json.form asset here too, off the main thread.
            JSONObject form = null;
            try {
                form = new FormUtils(this).getFormJson(ReportRegisterActivity.REPORT_FORM_NUTRITION);
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
        setText(R.id.txt_reporting_month, reportModel.getReporting_month());
        setText(R.id.txt_facility_name, reportModel.getFacility());

        Map<String, String> data = reportModel.toValueMap();

        // Section A
        setText(R.id.subpop_calhiv, data.get("subpop_calhiv"));
        setText(R.id.subpop_hei, data.get("subpop_hei"));
        setText(R.id.subpop_cml_hiv, data.get("subpop_cml_hiv"));
        setText(R.id.subpop_cpbfa, data.get("subpop_cpbfa"));
        setText(R.id.subpop_siblings, data.get("subpop_siblings"));

        // Section B
        setText(R.id.hh_practicing_diet_diversity, data.get("hh_practicing_diet_diversity"));
        setText(R.id.hh_practicing_exclusive_bf, data.get("hh_practicing_exclusive_bf"));
        setText(R.id.hh_practicing_complementary_feeding, data.get("hh_practicing_complementary_feeding"));
        setText(R.id.hh_wash_activities, data.get("hh_wash_activities"));
        setText(R.id.et_b_hh_visited, data.get("hh_visited"));
        setText(R.id.et_b_ppmam_id, data.get("ppmam_id"));
        setText(R.id.et_b_ppmam_ref, data.get("ppmam_ref"));
        setText(R.id.et_b_other_children_pmam, data.get("other_children_pmam"));
        setText(R.id.et_b_plw_art, data.get("plw_art"));
        setText(R.id.et_b_plw_ifas, data.get("plw_ifas"));
        setText(R.id.et_b_food_insecurity, data.get("food_insecurity"));

        // Section C
        setText(R.id.mnp_children_6_23_received, data.get("mnp_children_6_23_received"));
        setText(R.id.mnp_plw_received, data.get("mnp_plw_received"));
        setText(R.id.vita_children_6_11_months, data.get("vita_children_6_11_months"));
        setText(R.id.vita_children_12_59_months, data.get("vita_children_12_59_months"));
        setText(R.id.vita_plw_supplemented, data.get("vita_plw_supplemented"));
        setText(R.id.deworming_children_12_59, data.get("deworming_children_12_59"));
        setText(R.id.et_c_deworm_plw, data.get("deworm_plw"));

        // Section D
        setText(R.id.ecd_centres_supported_monitoring, data.get("ecd_centres_supported_monitoring"));
        setText(R.id.ecd_centres_with_feeding, data.get("ecd_centres_with_feeding"));
        setText(R.id.ecd_children_enrolled, data.get("ecd_children_enrolled"));
        setText(R.id.ecd_caregivers_trained, data.get("ecd_caregivers_trained"));
        setText(R.id.et_d_dev_screening, data.get("dev_screening"));

        // Section E
        setText(R.id.wfa_underweight, data.get("wfa_underweight"));
        setText(R.id.wfa_overweight, data.get("wfa_overweight"));
        setText(R.id.wfa_normal, data.get("wfa_normal"));

        // Section F
        setText(R.id.nutrition_grade_1, data.get("nutrition_grade_1"));
        setText(R.id.nutrition_grade_2, data.get("nutrition_grade_2"));
        setText(R.id.nutrition_grade_3, data.get("nutrition_grade_3"));
        setText(R.id.nutrition_nr, data.get("nutrition_nr"));

        // Section G
        setText(R.id.muac_red_below_11_5, data.get("muac_red_below_11_5"));
        setText(R.id.muac_yellow_11_5_to_12_5, data.get("muac_yellow_11_5_to_12_5"));
        setText(R.id.muac_green_12_5_plus, data.get("muac_green_12_5_plus"));
        setText(R.id.et_g_oedema, data.get("muac_oedema"));

        // Section H
        setText(R.id.sti_referred, data.get("sti_referred"));
        setText(R.id.sti_treated, data.get("sti_treated"));

        // Section I
        setText(R.id.referral_nutrition_to_health, data.get("referral_nutrition_to_health"));
        setText(R.id.referral_feedback_received, data.get("referral_feedback_received"));
        setText(R.id.et_i_date_referral, data.get("date_referral"));
        setText(R.id.et_i_date_feedback, data.get("date_feedback"));
        setText(R.id.et_i_hiv_tb_integration, data.get("hiv_tb_integration"));

        calculateAndSetTotals(data);

        TextView commentsView = findViewById(R.id.txt_comments);
        if (commentsView != null) {
            String comment = data.get("comment");
            commentsView.setText(comment != null && !comment.isEmpty() ? comment : "No comments");
        }
    }

    private void calculateAndSetTotals(Map<String, String> data) {
        // Section A
        int totalA = sum(data, "subpop_calhiv", "subpop_hei", "subpop_cml_hiv", "subpop_cpbfa", "subpop_siblings");
        setText(R.id.tv_a_total, String.valueOf(totalA));

        // Section B
        int totalB = sum(data, "hh_practicing_diet_diversity", "hh_practicing_exclusive_bf", "hh_practicing_complementary_feeding", "hh_wash_activities", 
                         "hh_visited", "ppmam_id", "ppmam_ref", "other_children_pmam", "plw_art", "plw_ifas", "food_insecurity");
        setText(R.id.tv_b_total, String.valueOf(totalB));
        
        // Section C
        int totalC = sum(data, "mnp_children_6_23_received", "mnp_plw_received", "vita_children_6_11_months", "vita_children_12_59_months", "vita_plw_supplemented", "deworming_children_12_59", "deworm_plw");
        setText(R.id.tv_c_total, String.valueOf(totalC));

        // Section D
        int totalD = sum(data, "ecd_centres_supported_monitoring", "ecd_centres_with_feeding", "ecd_children_enrolled", "ecd_caregivers_trained", "dev_screening");
        setText(R.id.tv_d_total, String.valueOf(totalD));

        // Section E
        int totalE = sum(data, "wfa_underweight", "wfa_overweight", "wfa_normal");
        setText(R.id.tv_e_total, String.valueOf(totalE));

        // Section F
        int totalF = sum(data, "nutrition_grade_1", "nutrition_grade_2", "nutrition_grade_3", "nutrition_nr");
        setText(R.id.tv_f_total, String.valueOf(totalF));

        // Section G
        int totalG = sum(data, "muac_red_below_11_5", "muac_yellow_11_5_to_12_5", "muac_green_12_5_plus", "muac_oedema");
        setText(R.id.tv_g_total, String.valueOf(totalG));

        // Section H
        int totalH = sum(data, "sti_referred", "sti_treated");
        setText(R.id.tv_h_total, String.valueOf(totalH));

        // Section I
        int totalI = sum(data, "referral_nutrition_to_health", "referral_feedback_received", "hiv_tb_integration");
        setText(R.id.tv_i_total, String.valueOf(totalI));
    }

    private int sum(Map<String, String> data, String... keys) {
        int total = 0;
        for (String key : keys) {
            String val = data.get(key);
            if (val != null && !val.isEmpty()) {
                try {
                    total += Integer.parseInt(val);
                } catch (NumberFormatException ignored) {}
            }
        }
        return total;
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

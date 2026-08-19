package com.bluecodeltd.ecap.chw.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.MalariaMonthlyDao;
import com.bluecodeltd.ecap.chw.dao.NutritionMonthlyDao;
import com.bluecodeltd.ecap.chw.dao.TbMonthlyDao;
import com.bluecodeltd.ecap.chw.model.MalariaMonthlyModel;
import com.bluecodeltd.ecap.chw.model.NutritionMonthlyModel;
import com.bluecodeltd.ecap.chw.model.TbMonthlyModel;
import com.bluecodeltd.ecap.chw.util.ReportFormUtils;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.bluecodeltd.ecap.chw.interactor.ReportRegisterInteractor;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class ReportViewActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "base_entity_id";
    public static final String EXTRA_TYPE = "report_type";

    private String reportId;
    private String reportType;
    private LinearLayout container;

    public static void start(Context context, String id, String type) {
        Intent intent = new Intent(context, ReportViewActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view);

        reportId = getIntent().getStringExtra(EXTRA_ID);
        reportType = getIntent().getStringExtra(EXTRA_TYPE);

        container = findViewById(R.id.report_container);

        setupToolbar();
        renderReport();
    }

    private void editReport() {
        try {
            String formName = getFormName();
            JSONObject form = FormUtils.getInstance(this).getFormJson(formName);
            if (form == null) return;

            if (ReportRegisterActivity.REPORT_TYPE_MALARIA.equals(reportType)) {
                MalariaMonthlyModel model = new MalariaMonthlyDao().getMalariaMonthlyByBaseEntityId(reportId);
                if (model != null) ReportFormUtils.prePopulateMalariaForm(form, model);
            } else if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) {
                TbMonthlyModel model = new TbMonthlyDao().getTbMonthlyByBaseEntityId(reportId);
                if (model != null) ReportFormUtils.prePopulateTbForm(form, model);
            } else if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) {
                NutritionMonthlyModel model = new NutritionMonthlyDao().getNutritionMonthlyByBaseEntityId(reportId);
                if (model != null) ReportFormUtils.prePopulateNutritionForm(form, model);
            }

            startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getFormName() {
        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) return ReportRegisterActivity.REPORT_FORM_NUTRITION;
        if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) return ReportRegisterActivity.REPORT_FORM_TB;
        return ReportRegisterActivity.REPORT_FORM_MALARIA;
    }

    private void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, ReportFormActivity.class);
        Form wizardForm = new Form();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
            if (jsonString != null) {
                try {
                    JSONObject jsonForm = new JSONObject(jsonString);
                    String entityId = jsonForm.optString(com.bluecodeltd.ecap.chw.util.JsonFormUtils.ENTITY_ID);
                    if (entityId != null && !entityId.isEmpty()) {
                        reportId = entityId;
                    }
                    
                    boolean isDraft = data.getBooleanExtra(JsonFormConstants.SKIP_VALIDATION, false);
                    // saveForm()'s processClient() call takes the same ChwClientProcessor lock a
                    // concurrent background sync can hold for a while; run it off the main thread
                    // to avoid an ANR (main thread blocked waiting on ChwClientProcessor's monitor),
                    // matching ReportHomeActivity's onActivityResult.
                    Threading.io(() -> {
                        new ReportRegisterInteractor().saveForm(jsonString, isDraft ? "draft" : "complete");
                        Threading.main(() -> {
                            container.removeAllViews();
                            renderReport();
                        });
                    });
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        
        TextView title = toolbar.findViewById(R.id.txt_title_label);
        title.setText(getString(R.string.report_summary));
    }

    private void renderReport() {
        if (reportType == null) return;
        
        switch (reportType) {
            case ReportRegisterActivity.REPORT_TYPE_MALARIA:
                renderMalaria();
                break;
            case ReportRegisterActivity.REPORT_TYPE_TB:
                renderTb();
                break;
            case ReportRegisterActivity.REPORT_TYPE_NUTRITION:
                renderNutrition();
                break;
            default:
                break;
        }
    }

    private void renderMalaria() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_report_malaria_content, container, true);
        MalariaMonthlyModel model = new MalariaMonthlyDao().getMalariaMonthlyByBaseEntityId(reportId);
        if (model == null) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String reportingMonth = model.getReportingMonth();
        if (reportingMonth == null || reportingMonth.isEmpty()) {
            reportingMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        }
        if (model.getReportingYear() != null && !model.getReportingYear().isEmpty() && !reportingMonth.contains(model.getReportingYear())) {
            reportingMonth += " " + model.getReportingYear();
        }

        renderMetadata(reportingMonth, model.getFacilityName(), model.getProvince(), model.getDistrict(), model.getWard(), model.getPartner());

        // Section A Q1
        setText(view, R.id.sa_q1_f_0_4, model.getSaQ1F04());
        setText(view, R.id.sa_q1_f_5_15, model.getSaQ1F515());
        setText(view, R.id.sa_q1_f_16_19, model.getSaQ1F1619());
        setText(view, R.id.sa_q1_f_20_plus, model.getSaQ1F20Plus());
        setText(view, R.id.sa_q1_f_calhiv, model.getSaQ1FCalhiv());
        setText(view, R.id.sa_q1_f_hei, model.getSaQ1FHei());
        setText(view, R.id.sa_q1_f_wlhiv, model.getSaQ1FWlhiv());
        setText(view, R.id.sa_q1_f_sv, model.getSaQ1FSv());
        setText(view, R.id.sa_q1_f_agyw, model.getSaQ1FAgyw());
        setText(view, R.id.sa_q1_f_hiv_pos, model.getSaQ1FHivPos());
        setText(view, R.id.sa_q1_f_siblings, model.getSaQ1FSiblings());
        setText(view, R.id.sa_q1_f_caregivers, model.getSaQ1FCaregivers());
        setText(view, R.id.sa_q1_m_0_4, model.getSaQ1M04());
        setText(view, R.id.sa_q1_m_5_15, model.getSaQ1M515());
        setText(view, R.id.sa_q1_m_16_19, model.getSaQ1M1619());
        setText(view, R.id.sa_q1_m_20_plus, model.getSaQ1M20Plus());
        setText(view, R.id.sa_q1_m_calhiv, model.getSaQ1MCalhiv());
        setText(view, R.id.sa_q1_m_hei, model.getSaQ1MHei());
        setText(view, R.id.sa_q1_m_wlhiv, model.getSaQ1MWlhiv());
        setText(view, R.id.sa_q1_m_sv, model.getSaQ1MSv());
        setText(view, R.id.sa_q1_m_siblings, model.getSaQ1MSiblings());
        setText(view, R.id.sa_q1_m_caregivers, model.getSaQ1MCaregivers());

        // Section A Q2
        setText(view, R.id.q2_f_0_4, model.getSaQ2F04());
        setText(view, R.id.q2_f_5_15, model.getQ2F515());
        setText(view, R.id.q2_f_16_19, model.getQ2F1619());
        setText(view, R.id.q2_f_20_plus, model.getQ2F20Plus());
        setText(view, R.id.q2_f_calhiv, model.getQ2FCalhiv());
        setText(view, R.id.q2_f_hei, model.getQ2FHei());
        setText(view, R.id.q2_f_wlhiv, model.getQ2FWlhiv());
        setText(view, R.id.q2_f_sv, model.getQ2FSv());
        setText(view, R.id.q2_f_agyw, model.getQ2FAgyw());
        setText(view, R.id.q2_f_hiv_pos, model.getQ2FHivPos());
        setText(view, R.id.q2_f_siblings, model.getQ2FSiblings());
        setText(view, R.id.q2_f_caregivers, model.getQ2FCaregivers());
        setText(view, R.id.q2_m_0_4, model.getQ2M04());
        setText(view, R.id.q2_m_5_15, model.getQ2M515());
        setText(view, R.id.q2_m_16_19, model.getQ2M1619());
        setText(view, R.id.q2_m_20_plus, model.getQ2M20Plus());
        setText(view, R.id.q2_m_calhiv, model.getQ2MCalhiv());
        setText(view, R.id.q2_m_hei, model.getQ2MHei());
        setText(view, R.id.q2_m_wlhiv, model.getQ2MWlhiv());
        setText(view, R.id.q2_m_sv, model.getQ2MSv());
        setText(view, R.id.q2_m_siblings, model.getQ2MSiblings());
        setText(view, R.id.q2_m_caregivers, model.getQ2MCaregivers());

        // Section A Q3
        setText(view, R.id.q3_f_0_4, model.getQ3F04());
        setText(view, R.id.q3_f_5_15, model.getQ3F515());
        setText(view, R.id.q3_f_16_19, model.getQ3F1619());
        setText(view, R.id.q3_f_20_plus, model.getQ3F20Plus());
        setText(view, R.id.q3_f_calhiv, model.getQ3FCalhiv());
        setText(view, R.id.q3_f_hei, model.getQ3FHei());
        setText(view, R.id.q3_f_wlhiv, model.getQ3FWlhiv());
        setText(view, R.id.q3_f_sv, model.getQ3FSv());
        setText(view, R.id.q3_f_agyw, model.getQ3FAgyw());
        setText(view, R.id.q3_f_hiv_pos, model.getQ3FHivPos());
        setText(view, R.id.q3_f_siblings, model.getQ3FSiblings());
        setText(view, R.id.q3_f_caregivers, model.getQ3FCaregivers());
        setText(view, R.id.q3_m_0_4, model.getQ3M04());
        setText(view, R.id.q3_m_5_15, model.getQ3M515());
        setText(view, R.id.q3_m_16_19, model.getQ3M1619());
        setText(view, R.id.q3_m_20_plus, model.getQ3M20Plus());
        setText(view, R.id.q3_m_calhiv, model.getQ3MCalhiv());
        setText(view, R.id.q3_m_hei, model.getQ3MHei());
        setText(view, R.id.q3_m_wlhiv, model.getQ3MWlhiv());
        setText(view, R.id.q3_m_sv, model.getQ3MSv());
        setText(view, R.id.q3_m_siblings, model.getQ3MSiblings());
        setText(view, R.id.q3_m_caregivers, model.getQ3MCaregivers());

        // Section A Q4
        setText(view, R.id.q4_f_0_4, model.getQ4F04());
        setText(view, R.id.q4_f_5_15, model.getQ4F515());
        setText(view, R.id.q4_f_16_19, model.getQ4F1619());
        setText(view, R.id.q4_f_20_plus, model.getQ4F20Plus());
        setText(view, R.id.q4_f_calhiv, model.getQ4FCalhiv());
        setText(view, R.id.q4_f_hei, model.getQ4FHei());
        setText(view, R.id.q4_f_wlhiv, model.getQ4FWlhiv());
        setText(view, R.id.q4_f_sv, model.getQ4FSv());
        setText(view, R.id.q4_f_agyw, model.getQ4FAgyw());
        setText(view, R.id.q4_f_hiv_pos, model.getQ4FHivPos());
        setText(view, R.id.q4_f_siblings, model.getQ4FSiblings());
        setText(view, R.id.q4_f_caregivers, model.getQ4FCaregivers());
        setText(view, R.id.q4_m_0_4, model.getQ4M04());
        setText(view, R.id.q4_m_5_15, model.getQ4M515());
        setText(view, R.id.q4_m_16_19, model.getQ4M1619());
        setText(view, R.id.q4_m_20_plus, model.getQ4M20Plus());
        setText(view, R.id.q4_m_calhiv, model.getQ4MCalhiv());
        setText(view, R.id.q4_m_hei, model.getQ4MHei());
        setText(view, R.id.q4_m_wlhiv, model.getQ4MWlhiv());
        setText(view, R.id.q4_m_sv, model.getQ4MSv());
        setText(view, R.id.q4_m_siblings, model.getQ4MSiblings());
        setText(view, R.id.q4_m_caregivers, model.getQ4MCaregivers());

        // Section A Q5
        setText(view, R.id.q5_f_0_4, model.getQ5F04());
        setText(view, R.id.q5_f_5_15, model.getQ5F515());
        setText(view, R.id.q5_f_16_19, model.getQ5F1619());
        setText(view, R.id.q5_f_20_plus, model.getQ5F20Plus());
        setText(view, R.id.q5_f_calhiv, model.getQ5FCalhiv());
        setText(view, R.id.q5_f_hei, model.getQ5FHei());
        setText(view, R.id.q5_f_wlhiv, model.getQ5FWlhiv());
        setText(view, R.id.q5_f_sv, model.getQ5FSv());
        setText(view, R.id.q5_f_agyw, model.getQ5FAgyw());
        setText(view, R.id.q5_f_hiv_pos, model.getQ5FHivPos());
        setText(view, R.id.q5_f_siblings, model.getQ5FSiblings());
        setText(view, R.id.q5_f_caregivers, model.getQ5FCaregivers());
        setText(view, R.id.q5_m_0_4, model.getQ5M04());
        setText(view, R.id.q5_m_5_15, model.getQ5M515());
        setText(view, R.id.q5_m_16_19, model.getQ5M1619());
        setText(view, R.id.q5_m_20_plus, model.getQ5M20Plus());
        setText(view, R.id.q5_m_calhiv, model.getQ5MCalhiv());
        setText(view, R.id.q5_m_hei, model.getQ5MHei());
        setText(view, R.id.q5_m_wlhiv, model.getQ5MWlhiv());
        setText(view, R.id.q5_m_sv, model.getQ5MSv());
        setText(view, R.id.q5_m_siblings, model.getQ5MSiblings());
        setText(view, R.id.q5_m_caregivers, model.getQ5MCaregivers());

        // Section A Q6
        setText(view, R.id.q6_f_0_4, model.getQ6F04());
        setText(view, R.id.q6_f_5_15, model.getQ6F515());
        setText(view, R.id.q6_f_16_19, model.getQ6F1619());
        setText(view, R.id.q6_f_20_plus, model.getQ6F20Plus());
        setText(view, R.id.q6_f_calhiv, model.getQ6FCalhiv());
        setText(view, R.id.q6_f_hei, model.getQ6FHei());
        setText(view, R.id.q6_f_wlhiv, model.getQ6FWlhiv());
        setText(view, R.id.q6_f_sv, model.getQ6FSv());
        setText(view, R.id.q6_f_agyw, model.getQ6FAgyw());
        setText(view, R.id.q6_f_hiv_pos, model.getQ6FHivPos());
        setText(view, R.id.q6_f_siblings, model.getQ6FSiblings());
        setText(view, R.id.q6_f_caregivers, model.getQ6FCaregivers());
        setText(view, R.id.q6_m_0_4, model.getQ6M04());
        setText(view, R.id.q6_m_5_15, model.getQ6M515());
        setText(view, R.id.q6_m_16_19, model.getQ6M1619());
        setText(view, R.id.q6_m_20_plus, model.getQ6M20Plus());
        setText(view, R.id.q6_m_calhiv, model.getQ6MCalhiv());
        setText(view, R.id.q6_m_hei, model.getQ6MHei());
        setText(view, R.id.q6_m_wlhiv, model.getQ6MWlhiv());
        setText(view, R.id.q6_m_sv, model.getQ6MSv());
        setText(view, R.id.q6_m_siblings, model.getQ6MSiblings());
        setText(view, R.id.q6_m_caregivers, model.getQ6MCaregivers());

        // Section A Q7
        setText(view, R.id.q7_f_0_4, model.getQ7F04());
        setText(view, R.id.q7_f_5_15, model.getQ7F515());
        setText(view, R.id.q7_f_16_19, model.getQ7F1619());
        setText(view, R.id.q7_f_20_plus, model.getQ7F20Plus());
        setText(view, R.id.q7_f_calhiv, model.getQ7FCalhiv());
        setText(view, R.id.q7_f_hei, model.getQ7FHei());
        setText(view, R.id.q7_f_wlhiv, model.getQ7FWlhiv());
        setText(view, R.id.q7_f_sv, model.getQ7FSv());
        setText(view, R.id.q7_f_agyw, model.getQ7FAgyw());
        setText(view, R.id.q7_f_hiv_pos, model.getQ7FHivPos());
        setText(view, R.id.q7_f_siblings, model.getQ7FSiblings());
        setText(view, R.id.q7_f_caregivers, model.getQ7FCaregivers());
        setText(view, R.id.q7_m_0_4, model.getQ7M04());
        setText(view, R.id.q7_m_5_15, model.getQ7M515());
        setText(view, R.id.q7_m_16_19, model.getQ7M1619());
        setText(view, R.id.q7_m_20_plus, model.getQ7M20Plus());
        setText(view, R.id.q7_m_calhiv, model.getQ7MCalhiv());
        setText(view, R.id.q7_m_hei, model.getQ7MHei());
        setText(view, R.id.q7_m_wlhiv, model.getQ7MWlhiv());
        setText(view, R.id.q7_m_sv, model.getQ7MSv());
        setText(view, R.id.q7_m_siblings, model.getQ7MSiblings());
        setText(view, R.id.q7_m_caregivers, model.getQ7MCaregivers());

        // Section A Q8
        setText(view, R.id.q8_f_0_4, model.getQ8F04());
        setText(view, R.id.q8_f_5_15, model.getQ8F515());
        setText(view, R.id.q8_f_16_19, model.getQ8F1619());
        setText(view, R.id.q8_f_20_plus, model.getQ8F20Plus());
        setText(view, R.id.q8_f_calhiv, model.getQ8FCalhiv());
        setText(view, R.id.q8_f_hei, model.getQ8FHei());
        setText(view, R.id.q8_f_wlhiv, model.getQ8FWlhiv());
        setText(view, R.id.q8_f_sv, model.getQ8FSv());
        setText(view, R.id.q8_f_agyw, model.getQ8FAgyw());
        setText(view, R.id.q8_f_hiv_pos, model.getQ8FHivPos());
        setText(view, R.id.q8_f_siblings, model.getQ8FSiblings());
        setText(view, R.id.q8_f_caregivers, model.getQ8FCaregivers());
        setText(view, R.id.q8_m_0_4, model.getQ8M04());
        setText(view, R.id.q8_m_5_15, model.getQ8M515());
        setText(view, R.id.q8_m_16_19, model.getQ8M1619());
        setText(view, R.id.q8_m_20_plus, model.getQ8M20Plus());
        setText(view, R.id.q8_m_calhiv, model.getQ8MCalhiv());
        setText(view, R.id.q8_m_hei, model.getQ8MHei());
        setText(view, R.id.q8_m_wlhiv, model.getQ8MWlhiv());
        setText(view, R.id.q8_m_sv, model.getQ8MSv());
        setText(view, R.id.q8_m_siblings, model.getQ8MSiblings());
        setText(view, R.id.q8_m_caregivers, model.getQ8MCaregivers());

        // Section A Q9
        setText(view, R.id.q9_f_0_4, model.getQ9F04());
        setText(view, R.id.q9_f_5_15, model.getQ9F515());
        setText(view, R.id.q9_f_16_19, model.getQ9F1619());
        setText(view, R.id.q9_f_20_plus, model.getQ9F20Plus());
        setText(view, R.id.q9_f_calhiv, model.getQ9FCalhiv());
        setText(view, R.id.q9_f_hei, model.getQ9FHei());
        setText(view, R.id.q9_f_wlhiv, model.getQ9FWlhiv());
        setText(view, R.id.q9_f_sv, model.getQ9FSv());
        setText(view, R.id.q9_f_agyw, model.getQ9FAgyw());
        setText(view, R.id.q9_f_hiv_pos, model.getQ9FHivPos());
        setText(view, R.id.q9_f_siblings, model.getQ9FSiblings());
        setText(view, R.id.q9_f_caregivers, model.getQ9FCaregivers());
        setText(view, R.id.q9_m_0_4, model.getQ9M04());
        setText(view, R.id.q9_m_5_15, model.getQ9M515());
        setText(view, R.id.q9_m_16_19, model.getQ9M1619());
        setText(view, R.id.q9_m_20_plus, model.getQ9M20Plus());
        setText(view, R.id.q9_m_calhiv, model.getQ9MCalhiv());
        setText(view, R.id.q9_m_hei, model.getQ9MHei());
        setText(view, R.id.q9_m_wlhiv, model.getQ9MWlhiv());
        setText(view, R.id.q9_m_sv, model.getQ9MSv());
        setText(view, R.id.q9_m_siblings, model.getQ9MSiblings());
        setText(view, R.id.q9_m_caregivers, model.getQ9MCaregivers());

        // Section A Q10
        setText(view, R.id.q10_f_0_4, model.getQ10F04());
        setText(view, R.id.q10_f_5_15, model.getQ10F515());
        setText(view, R.id.q10_f_16_19, model.getQ10F1619());
        setText(view, R.id.q10_f_20_plus, model.getQ10F20Plus());
        setText(view, R.id.q10_f_calhiv, model.getQ10FCalhiv());
        setText(view, R.id.q10_f_hei, model.getQ10FHei());
        setText(view, R.id.q10_f_wlhiv, model.getQ10FWlhiv());
        setText(view, R.id.q10_f_sv, model.getQ10FSv());
        setText(view, R.id.q10_f_agyw, model.getQ10FAgyw());
        setText(view, R.id.q10_f_hiv_pos, model.getQ10FHivPos());
        setText(view, R.id.q10_f_siblings, model.getQ10FSiblings());
        setText(view, R.id.q10_f_caregivers, model.getQ10FCaregivers());
        setText(view, R.id.q10_m_0_4, model.getQ10M04());
        setText(view, R.id.q10_m_5_15, model.getQ10M515());
        setText(view, R.id.q10_m_16_19, model.getQ10M1619());
        setText(view, R.id.q10_m_20_plus, model.getQ10M20Plus());
        setText(view, R.id.q10_m_calhiv, model.getQ10MCalhiv());
        setText(view, R.id.q10_m_hei, model.getQ10MHei());
        setText(view, R.id.q10_m_wlhiv, model.getQ10MWlhiv());
        setText(view, R.id.q10_m_sv, model.getQ10MSv());
        setText(view, R.id.q10_m_siblings, model.getQ10MSiblings());
        setText(view, R.id.q10_m_caregivers, model.getQ10MCaregivers());

        // Section B
        setText(view, R.id.sb_q1_f_0_4, model.getSbQ1F04());
        setText(view, R.id.sb_q1_f_5_15, model.getSbQ1F515());
        setText(view, R.id.sb_q1_f_16_19, model.getSbQ1F1619());
        setText(view, R.id.sb_q1_f_20_plus, model.getSbQ1F20Plus());
        setText(view, R.id.sb_q1_m_0_4, model.getSbQ1M04());
        setText(view, R.id.sb_q1_m_5_15, model.getSbQ1M515());
        setText(view, R.id.sb_q1_m_16_19, model.getSbQ1M1619());
        setText(view, R.id.sb_q1_m_20_plus, model.getSbQ1M20Plus());

        setText(view, R.id.sb_q2_f_0_4, model.getSbQ2F04());
        setText(view, R.id.sb_q2_f_5_15, model.getSbQ2F515());
        setText(view, R.id.sb_q2_f_16_19, model.getSbQ2F1619());
        setText(view, R.id.sb_q2_f_20_plus, model.getSbQ2F20Plus());
        setText(view, R.id.sb_q2_m_0_4, model.getSbQ2M04());
        setText(view, R.id.sb_q2_m_5_15, model.getSbQ2M515());
        setText(view, R.id.sb_q2_m_16_19, model.getSbQ2M1619());
        setText(view, R.id.sb_q2_m_20_plus, model.getSbQ2M20Plus());

        setText(view, R.id.sb_q3_f_0_4, model.getSbQ3F04());
        setText(view, R.id.sb_q3_f_5_15, model.getSbQ3F515());
        setText(view, R.id.sb_q3_f_16_19, model.getSbQ3F1619());
        setText(view, R.id.sb_q3_f_20_plus, model.getSbQ3F20Plus());
        setText(view, R.id.sb_q3_m_0_4, model.getSbQ3M04());
        setText(view, R.id.sb_q3_m_5_15, model.getSbQ3M515());
        setText(view, R.id.sb_q3_m_16_19, model.getSbQ3M1619());
        setText(view, R.id.sb_q3_m_20_plus, model.getSbQ3M20Plus());

        setText(view, R.id.sb_q4_f_0_4, model.getSbQ4F04());
        setText(view, R.id.sb_q4_f_5_15, model.getSbQ4F515());
        setText(view, R.id.sb_q4_f_16_19, model.getSbQ4F1619());
        setText(view, R.id.sb_q4_f_20_plus, model.getSbQ4F20Plus());
        setText(view, R.id.sb_q4_m_0_4, model.getSbQ4M04());
        setText(view, R.id.sb_q4_m_5_15, model.getSbQ4M515());
        setText(view, R.id.sb_q4_m_16_19, model.getSbQ4M1619());
        setText(view, R.id.sb_q4_m_20_plus, model.getSbQ4M20Plus());

        setText(view, R.id.sb_q5_f_0_4, model.getSbQ5F04());
        setText(view, R.id.sb_q5_f_5_15, model.getSbQ5F515());
        setText(view, R.id.sb_q5_f_16_19, model.getSbQ5F1619());
        setText(view, R.id.sb_q5_f_20_plus, model.getSbQ5F20Plus());
        setText(view, R.id.sb_q5_m_0_4, model.getSbQ5M04());
        setText(view, R.id.sb_q5_m_5_15, model.getSbQ5M515());
        setText(view, R.id.sb_q5_m_16_19, model.getSbQ5M1619());
        setText(view, R.id.sb_q5_m_20_plus, model.getSbQ5M20Plus());

        setText(view, R.id.sb_q6_f_0_4, model.getSbQ6F04());
        setText(view, R.id.sb_q6_f_5_15, model.getSbQ6F515());
        setText(view, R.id.sb_q6_f_16_19, model.getSbQ6F1619());
        setText(view, R.id.sb_q6_f_20_plus, model.getSbQ6F20Plus());
        setText(view, R.id.sb_q6_m_0_4, model.getSbQ6M04());
        setText(view, R.id.sb_q6_m_5_15, model.getSbQ6M515());
        setText(view, R.id.sb_q6_m_16_19, model.getSbQ6M1619());
        setText(view, R.id.sb_q6_m_20_plus, model.getSbQ6M20Plus());

        setText(view, R.id.sb_q7_f_0_4, model.getSbQ7F04());
        setText(view, R.id.sb_q7_f_5_15, model.getSbQ7F515());
        setText(view, R.id.sb_q7_f_16_19, model.getSbQ7F1619());
        setText(view, R.id.sb_q7_f_20_plus, model.getSbQ7F20Plus());
        setText(view, R.id.sb_q7_m_0_4, model.getSbQ7M04());
        setText(view, R.id.sb_q7_m_5_15, model.getSbQ7M515());
        setText(view, R.id.sb_q7_m_16_19, model.getSbQ7M1619());
        setText(view, R.id.sb_q7_m_20_plus, model.getSbQ7M20Plus());

        setText(view, R.id.sb_q8_f_0_4, model.getSbQ8F04());
        setText(view, R.id.sb_q8_f_5_15, model.getSbQ8F515());
        setText(view, R.id.sb_q8_f_16_19, model.getSbQ8F1619());
        setText(view, R.id.sb_q8_f_20_plus, model.getSbQ8F20Plus());
        setText(view, R.id.sb_q8_m_0_4, model.getSbQ8M04());
        setText(view, R.id.sb_q8_m_5_15, model.getSbQ8M515());
        setText(view, R.id.sb_q8_m_16_19, model.getSbQ8M1619());
        setText(view, R.id.sb_q8_m_20_plus, model.getSbQ8M20Plus());

        setText(view, R.id.sb_q9_f_0_4, model.getSbQ9F04());
        setText(view, R.id.sb_q9_f_5_15, model.getSbQ9F515());
        setText(view, R.id.sb_q9_f_16_19, model.getSbQ9F1619());
        setText(view, R.id.sb_q9_f_20_plus, model.getSbQ9F20Plus());
        setText(view, R.id.sb_q9_m_0_4, model.getSbQ9M04());
        setText(view, R.id.sb_q9_m_5_15, model.getSbQ9M515());
        setText(view, R.id.sb_q9_m_16_19, model.getSbQ9M1619());
        setText(view, R.id.sb_q9_m_20_plus, model.getSbQ9M20Plus());

        setText(view, R.id.sb_q10_f_0_4, model.getSbQ10F04());
        setText(view, R.id.sb_q10_f_5_15, model.getSbQ10F515());
        setText(view, R.id.sb_q10_f_16_19, model.getSbQ10F1619());
        setText(view, R.id.sb_q10_f_20_plus, model.getSbQ10F20Plus());
        setText(view, R.id.sb_q10_m_0_4, model.getSbQ10M04());
        setText(view, R.id.sb_q10_m_5_15, model.getSbQ10M515());
        setText(view, R.id.sb_q10_m_16_19, model.getSbQ10M1619());
        setText(view, R.id.sb_q10_m_20_plus, model.getSbQ10M20Plus());

        // Section C
        setText(view, R.id.sc_q1, model.getScQ1());
        setText(view, R.id.sc_q2, model.getScQ2());
        setText(view, R.id.sc_q3, model.getScQ3());
        setText(view, R.id.sc_q4, model.getScQ4());
        setText(view, R.id.sc_q5, model.getScQ5());
        setText(view, R.id.sc_q6, model.getScQ6());
        setText(view, R.id.sc_q7, model.getScQ7());

        // Comments
        setText(view, R.id.txt_comments, model.getComment() != null ? model.getComment() : "No comments");

        setupExpandableSection(view, R.id.sa_q1_title, R.id.table_sa_q1);
        setupExpandableSection(view, R.id.sa_q2_title, R.id.table_sa_q2);
        setupExpandableSection(view, R.id.sa_q3_title, R.id.table_sa_q3);
        setupExpandableSection(view, R.id.sa_q4_title, R.id.table_sa_q4);
        setupExpandableSection(view, R.id.sa_q5_title, R.id.table_sa_q5);
        setupExpandableSection(view, R.id.sa_q6_title, R.id.table_sa_q6);
        setupExpandableSection(view, R.id.sa_q7_title, R.id.table_sa_q7);
        setupExpandableSection(view, R.id.sa_q8_title, R.id.table_sa_q8);
        setupExpandableSection(view, R.id.sa_q9_title, R.id.table_sa_q9);
        setupExpandableSection(view, R.id.sa_q10_title, R.id.table_sa_q10);

        setupExpandableSection(view, R.id.sb_q1_title, R.id.table_sb_q1);
        setupExpandableSection(view, R.id.sb_q2_title, R.id.table_sb_q2);
        setupExpandableSection(view, R.id.sb_q3_title, R.id.table_sb_q3);
        setupExpandableSection(view, R.id.sb_q4_title, R.id.table_sb_q4);
        setupExpandableSection(view, R.id.sb_q5_title, R.id.table_sb_q5);
        setupExpandableSection(view, R.id.sb_q6_title, R.id.table_sb_q6);
        setupExpandableSection(view, R.id.sb_q7_title, R.id.table_sb_q7);
        setupExpandableSection(view, R.id.sb_q8_title, R.id.table_sb_q8);
        setupExpandableSection(view, R.id.sb_q9_title, R.id.table_sb_q9);
        setupExpandableSection(view, R.id.sb_q10_title, R.id.table_sb_q10);

        setupExpandableSection(view, R.id.sc_title, R.id.table_sc);
    }

    private void renderTb() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_report_tb_content, container, true);
        TbMonthlyModel model = new TbMonthlyDao().getTbMonthlyByBaseEntityId(reportId);
        if (model == null) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String reportingMonth = model.getReportingMonth();
        if (reportingMonth == null || reportingMonth.isEmpty()) {
            reportingMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        }
        if (model.getReportingYear() != null && !model.getReportingYear().isEmpty() && !reportingMonth.contains(model.getReportingYear())) {
            reportingMonth += " " + model.getReportingYear();
        }

        renderMetadata(reportingMonth, model.getFacilityName(), model.getProvince(), model.getDistrict(), model.getWard(), model.getPartner());

        // Q1
        setText(view, R.id.q1_f_lt1, model.getQ1FLt1());
        setText(view, R.id.q1_f_1_4, model.getQ1F14());
        setText(view, R.id.q1_f_5_9, model.getQ1F59());
        setText(view, R.id.q1_f_10_14, model.getQ1F1014());
        setText(view, R.id.q1_f_15_19, model.getQ1F1519());
        setText(view, R.id.q1_f_20_plus, model.getQ1F20Plus());
        setText(view, R.id.q1_f_pc_18, model.getQ1FPc18Plus());
        setText(view, R.id.q1_f_total, model.getQ1FTotal());
        setText(view, R.id.q1_m_lt1, model.getQ1MLt1());
        setText(view, R.id.q1_m_1_4, model.getQ1M14());
        setText(view, R.id.q1_m_5_9, model.getQ1M59());
        setText(view, R.id.q1_m_10_14, model.getQ1M1014());
        setText(view, R.id.q1_m_15_19, model.getQ1M1519());
        setText(view, R.id.q1_m_20_plus, model.getQ1M20Plus());
        setText(view, R.id.q1_m_pc_18, model.getQ1MPc18Plus());
        setText(view, R.id.q1_m_total, model.getQ1MTotal());
        setText(view, R.id.q1_hei, model.getQ1SubHei());
        setText(view, R.id.q1_calhiv, model.getQ1SubCalhiv());
        setText(view, R.id.q1_wlhiv, model.getQ1SubCPlhiv());
        setText(view, R.id.q1_pc_lhiv, model.getQ1SubPcLhiv());

        // Q2
        setText(view, R.id.q2_f_lt1, model.getQ2FLt1());
        setText(view, R.id.q2_f_1_4, model.getQ2F14());
        setText(view, R.id.q2_f_5_9, model.getQ2F59());
        setText(view, R.id.q2_f_10_14, model.getQ2F1014());
        setText(view, R.id.q2_f_15_19, model.getQ2F1519());
        setText(view, R.id.q2_f_20_plus, model.getQ2F20Plus());
        setText(view, R.id.q2_f_pc_18, model.getQ2FPc18Plus());
        setText(view, R.id.q2_f_total, model.getQ2FTotal());
        setText(view, R.id.q2_m_lt1, model.getQ2MLt1());
        setText(view, R.id.q2_m_1_4, model.getQ2M14());
        setText(view, R.id.q2_m_5_9, model.getQ2M59());
        setText(view, R.id.q2_m_10_14, model.getQ2M1014());
        setText(view, R.id.q2_m_15_19, model.getQ2M1519());
        setText(view, R.id.q2_m_20_plus, model.getQ2M20Plus());
        setText(view, R.id.q2_m_pc_18, model.getQ2MPc18Plus());
        setText(view, R.id.q2_m_total, model.getQ2MTotal());
        setText(view, R.id.q2_hei, model.getQ2SubHei());
        setText(view, R.id.q2_calhiv, model.getQ2SubCalhiv());
        setText(view, R.id.q2_wlhiv, model.getQ2SubCPlhiv());
        setText(view, R.id.q2_pc_lhiv, model.getQ2SubPcLhiv());

        // Q3
        setText(view, R.id.q3_f_lt1, model.getQ3FLt1());
        setText(view, R.id.q3_f_1_4, model.getQ3F14());
        setText(view, R.id.q3_f_5_9, model.getQ3F59());
        setText(view, R.id.q3_f_10_14, model.getQ3F1014());
        setText(view, R.id.q3_f_15_19, model.getQ3F1519());
        setText(view, R.id.q3_f_20_plus, model.getQ3F20Plus());
        setText(view, R.id.q3_f_pc_18, model.getQ3FPc18Plus());
        setText(view, R.id.q3_f_total, model.getQ3FTotal());
        setText(view, R.id.q3_m_lt1, model.getQ3MLt1());
        setText(view, R.id.q3_m_1_4, model.getQ3M14());
        setText(view, R.id.q3_m_5_9, model.getQ3M59());
        setText(view, R.id.q3_m_10_14, model.getQ3M1014());
        setText(view, R.id.q3_m_15_19, model.getQ3M1519());
        setText(view, R.id.q3_m_20_plus, model.getQ3M20Plus());
        setText(view, R.id.q3_m_pc_18, model.getQ3MPc18Plus());
        setText(view, R.id.q3_m_total, model.getQ3MTotal());
        setText(view, R.id.q3_hei, model.getQ3SubHei());
        setText(view, R.id.q3_calhiv, model.getQ3SubCalhiv());
        setText(view, R.id.q3_wlhiv, model.getQ3SubCPlhiv());
        setText(view, R.id.q3_pc_lhiv, model.getQ3SubPcLhiv());

        // Q4
        setText(view, R.id.q4_f_lt1, model.getQ4FLt1());
        setText(view, R.id.q4_f_1_4, model.getQ4F14());
        setText(view, R.id.q4_f_5_9, model.getQ4F59());
        setText(view, R.id.q4_f_10_14, model.getQ4F1014());
        setText(view, R.id.q4_f_15_19, model.getQ4F1519());
        setText(view, R.id.q4_f_20_plus, model.getQ4F20Plus());
        setText(view, R.id.q4_f_pc_18, model.getQ4FPc18Plus());
        setText(view, R.id.q4_f_total, model.getQ4FTotal());
        setText(view, R.id.q4_m_lt1, model.getQ4MLt1());
        setText(view, R.id.q4_m_1_4, model.getQ4M14());
        setText(view, R.id.q4_m_5_9, model.getQ4M59());
        setText(view, R.id.q4_m_10_14, model.getQ4M1014());
        setText(view, R.id.q4_m_15_19, model.getQ4M1519());
        setText(view, R.id.q4_m_20_plus, model.getQ4M20Plus());
        setText(view, R.id.q4_m_pc_18, model.getQ4MPc18Plus());
        setText(view, R.id.q4_m_total, model.getQ4MTotal());
        setText(view, R.id.q4_hei, model.getQ4SubHei());
        setText(view, R.id.q4_calhiv, model.getQ4SubCalhiv());
        setText(view, R.id.q4_wlhiv, model.getQ4SubCPlhiv());
        setText(view, R.id.q4_pc_lhiv, model.getQ4SubPcLhiv());

        // Q5
        setText(view, R.id.q5_f_lt1, model.getQ5FLt1());
        setText(view, R.id.q5_f_1_4, model.getQ5F14());
        setText(view, R.id.q5_f_5_9, model.getQ5F59());
        setText(view, R.id.q5_f_10_14, model.getQ5F1014());
        setText(view, R.id.q5_f_15_19, model.getQ5F1519());
        setText(view, R.id.q5_f_20_plus, model.getQ5F20Plus());
        setText(view, R.id.q5_f_pc_18, model.getQ5FPc18Plus());
        setText(view, R.id.q5_f_total, model.getQ5FTotal());
        setText(view, R.id.q5_m_lt1, model.getQ5MLt1());
        setText(view, R.id.q5_m_1_4, model.getQ5M14());
        setText(view, R.id.q5_m_5_9, model.getQ5M59());
        setText(view, R.id.q5_m_10_14, model.getQ5M1014());
        setText(view, R.id.q5_m_15_19, model.getQ5M1519());
        setText(view, R.id.q5_m_20_plus, model.getQ5M20Plus());
        setText(view, R.id.q5_m_pc_18, model.getQ5MPc18Plus());
        setText(view, R.id.q5_m_total, model.getQ5MTotal());
        setText(view, R.id.q5_hei, model.getQ5SubHei());
        setText(view, R.id.q5_calhiv, model.getQ5SubCalhiv());
        setText(view, R.id.q5_wlhiv, model.getQ5SubCPlhiv());
        setText(view, R.id.q5_pc_lhiv, model.getQ5SubPcLhiv());

        // Q6
        setText(view, R.id.q6_f_lt1, model.getQ6FLt1());
        setText(view, R.id.q6_f_1_4, model.getQ6F14());
        setText(view, R.id.q6_f_5_9, model.getQ6F59());
        setText(view, R.id.q6_f_10_14, model.getQ6F1014());
        setText(view, R.id.q6_f_15_19, model.getQ6F1519());
        setText(view, R.id.q6_f_20_plus, model.getQ6F20Plus());
        setText(view, R.id.q6_f_pc_18, model.getQ6FPc18Plus());
        setText(view, R.id.q6_f_total, model.getQ6FTotal());
        setText(view, R.id.q6_m_lt1, model.getQ6MLt1());
        setText(view, R.id.q6_m_1_4, model.getQ6M14());
        setText(view, R.id.q6_m_5_9, model.getQ6M59());
        setText(view, R.id.q6_m_10_14, model.getQ6M1014());
        setText(view, R.id.q6_m_15_19, model.getQ6M1519());
        setText(view, R.id.q6_m_20_plus, model.getQ6M20Plus());
        setText(view, R.id.q6_m_pc_18, model.getQ6MPc18Plus());
        setText(view, R.id.q6_m_total, model.getQ6MTotal());
        setText(view, R.id.q6_hei, model.getQ6SubHei());
        setText(view, R.id.q6_calhiv, model.getQ6SubCalhiv());
        setText(view, R.id.q6_wlhiv, model.getQ6SubCPlhiv());
        setText(view, R.id.q6_pc_lhiv, model.getQ6SubPcLhiv());

        // Q7
        setText(view, R.id.q7_f_lt1, model.getQ7FLt1());
        setText(view, R.id.q7_f_1_4, model.getQ7F14());
        setText(view, R.id.q7_f_5_9, model.getQ7F59());
        setText(view, R.id.q7_f_10_14, model.getQ7F1014());
        setText(view, R.id.q7_f_15_19, model.getQ7F1519());
        setText(view, R.id.q7_f_20_plus, model.getQ7F20Plus());
        setText(view, R.id.q7_f_pc_18, model.getQ7FPc18Plus());
        setText(view, R.id.q7_f_total, model.getQ7FTotal());
        setText(view, R.id.q7_m_lt1, model.getQ7MLt1());
        setText(view, R.id.q7_m_1_4, model.getQ7M14());
        setText(view, R.id.q7_m_5_9, model.getQ7M59());
        setText(view, R.id.q7_m_10_14, model.getQ7M1014());
        setText(view, R.id.q7_m_15_19, model.getQ7M1519());
        setText(view, R.id.q7_m_20_plus, model.getQ7M20Plus());
        setText(view, R.id.q7_m_pc_18, model.getQ7MPc18Plus());
        setText(view, R.id.q7_m_total, model.getQ7MTotal());
        setText(view, R.id.q7_hei, model.getQ7SubHei());
        setText(view, R.id.q7_calhiv, model.getQ7SubCalhiv());
        setText(view, R.id.q7_wlhiv, model.getQ7SubCPlhiv());
        setText(view, R.id.q7_pc_lhiv, model.getQ7SubPcLhiv());

        // Comments
        setText(view, R.id.txt_comments, model.getComment() != null ? model.getComment() : "No comments");

        setupExpandableSection(view, R.id.q1_title, R.id.table_q1);
        setupExpandableSection(view, R.id.q2_title, R.id.table_q2);
        setupExpandableSection(view, R.id.q3_title, R.id.table_q3);
        setupExpandableSection(view, R.id.q4_title, R.id.table_q4);
        setupExpandableSection(view, R.id.q5_title, R.id.table_q5);
        setupExpandableSection(view, R.id.q6_title, R.id.table_q6);
        setupExpandableSection(view, R.id.q7_title, R.id.table_q7);
    }

    private void renderNutrition() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_report_nutrition_content, container, true);
        NutritionMonthlyModel model = new NutritionMonthlyDao().getNutritionMonthlyByBaseEntityId(reportId);
        if (model == null) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String reportingMonth = model.getReportingPeriod();
        if (reportingMonth == null || reportingMonth.isEmpty()) {
            reportingMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
        }
        if (model.getReportingYear() != null && !model.getReportingYear().isEmpty() && !reportingMonth.contains(model.getReportingYear())) {
            reportingMonth += " " + model.getReportingYear();
        }
        renderMetadata(reportingMonth, model.getFacilityName(), model.getProvince(), model.getDistrict(), model.getWard(), model.getPartner());

        // Section A
        setText(view, R.id.subpop_calhiv, model.getCAlhiv());
        setText(view, R.id.subpop_hei, model.getHei());
        setText(view, R.id.subpop_cml_hiv, model.getCwlhiv());
        setText(view, R.id.subpop_cpbfa, model.getCPbfa());
        setText(view, R.id.subpop_siblings, model.getSiblings());

        // Section B
        setText(view, R.id.hh_practicing_diet_diversity, model.getHhPracticingDietDiversity());
        setText(view, R.id.hh_practicing_exclusive_bf, model.getHhPracticingExclusiveBf());
        setText(view, R.id.hh_practicing_complementary_feeding, model.getHhPracticingComplementaryFeeding());
        setText(view, R.id.hh_wash_activities, model.getHhWashActivities());
        setText(view, R.id.hh_visited_assessment, model.getHhVisitedAssessment());
        setText(view, R.id.ppmam_identified, model.getPpmamIdentified());
        setText(view, R.id.ppmam_referred_commenced, model.getPpmamReferredCommenced());
        setText(view, R.id.other_children_pmam, model.getOtherChildrenPmam());
        setText(view, R.id.plw_art_pmtct_nutrition_assessment, model.getPlwArtPmtctNutritionAssessment());
        setText(view, R.id.plw_received_ifas, model.getPlwReceivedIfas());
        setText(view, R.id.hh_food_insecurity_counselled, model.getHhFoodInsecurityCounselled());

        // Section C
        setText(view, R.id.mnp_children_6_23_received, model.getMnpChildren623Received());
        setText(view, R.id.mnp_plw_received, model.getMnpPlwReceived());
        setText(view, R.id.vita_children_6_11_months, model.getVitaChildren611Months());
        setText(view, R.id.vita_children_12_59_months, model.getVitaChildren1259Months());
        setText(view, R.id.vita_plw_supplemented, model.getVitaPlwSupplemented());
        setText(view, R.id.deworming_children_12_59, model.getDewormingChildren1259());
        setText(view, R.id.deworming_plw, model.getDewormingPlw());

        // Section D
        setText(view, R.id.ecd_centres_supported_monitoring, model.getEcdCentresSupportedMonitoring());
        setText(view, R.id.ecd_centres_with_feeding, model.getEcdCentresWithFeeding());
        setText(view, R.id.ecd_children_enrolled, model.getEcdChildrenEnrolled());
        setText(view, R.id.ecd_caregivers_trained, model.getEcdCaregiversTrained());
        setText(view, R.id.ecd_developmental_screening, model.getEcdDevelopmentalScreening());

        // Section E
        setText(view, R.id.wfa_underweight, model.getWfaUnderweight());
        setText(view, R.id.wfa_overweight, model.getWfaOverweight());
        setText(view, R.id.wfa_normal, model.getWfaNormal());

        // Section F
        setText(view, R.id.nutrition_grade_1, model.getNutritionGrade1());
        setText(view, R.id.nutrition_grade_2, model.getNutritionGrade2());
        setText(view, R.id.nutrition_nr, model.getNutritionNr());

        // Section G
        setText(view, R.id.muac_red_below_11_5, model.getMuacRedBelow115());
        setText(view, R.id.muac_yellow_11_5_to_12_5, model.getMuacYellow115To125());
        setText(view, R.id.muac_green_12_5_plus, model.getMuacGreen125Plus());
        setText(view, R.id.muac_oedema, model.getMuacOedema());

        // Section H
        setText(view, R.id.sti_referred, model.getStiReferred());
        setText(view, R.id.sti_treated, model.getStiTreated());

        // Section I
        setText(view, R.id.referral_nutrition_to_health, model.getReferralNutritionToHealth());
        setText(view, R.id.referral_feedback_received, model.getReferralFeedbackReceived());
        setText(view, R.id.referral_date_of_referral, (model.getReferralDateOfReferral() != null && !model.getReferralDateOfReferral().isEmpty()) ? model.getReferralDateOfReferral() : "-");
        setText(view, R.id.referral_date_of_feedback, (model.getReferralDateOfFeedback() != null && !model.getReferralDateOfFeedback().isEmpty()) ? model.getReferralDateOfFeedback() : "-");
        setText(view, R.id.referral_hiv_tb_integration, model.getReferralHivTbIntegration());

        // Comments
        setText(view, R.id.txt_comments, (model.getComment() != null && !model.getComment().isEmpty()) ? model.getComment() : "No comments");

        setupExpandableSection(view, R.id.section_a_title, R.id.table_section_a);
        setupExpandableSection(view, R.id.section_b_title, R.id.table_section_b);
        setupExpandableSection(view, R.id.section_c_title, R.id.table_section_c);
        setupExpandableSection(view, R.id.section_d_title, R.id.table_section_d);
        setupExpandableSection(view, R.id.section_e_title, R.id.table_section_e);
        setupExpandableSection(view, R.id.section_f_title, R.id.table_section_f);
        setupExpandableSection(view, R.id.section_g_title, R.id.table_section_g);
        setupExpandableSection(view, R.id.section_h_title, R.id.table_section_h);
        setupExpandableSection(view, R.id.section_i_title, R.id.table_section_i);
    }

    private void renderMetadata(String reportingMonth, String facility, String province, String district, String ward, String partner) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setText(findViewById(android.R.id.content), R.id.txt_reporting_month, reportingMonth, "--");
        setText(findViewById(android.R.id.content), R.id.txt_facility_name, getValue(facility, "facility", prefs), "--");
        setText(findViewById(android.R.id.content), R.id.txt_province, getValue(province, "province", prefs), "--");
        setText(findViewById(android.R.id.content), R.id.txt_district, getValue(district, "district", prefs), "--");
        setText(findViewById(android.R.id.content), R.id.txt_ward, getValue(ward, "ward", prefs), "--");
        setText(findViewById(android.R.id.content), R.id.txt_partner, getValue(partner, "partner", prefs), "--");

        View btnEdit = findViewById(R.id.btn_edit_report);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> editReport());
        }
    }

    private void setupExpandableSection(View root, int titleId, int contentId) {
        View titleView = root.findViewById(titleId);
        View contentView = root.findViewById(contentId);

        if (titleView != null && contentView != null) {
            titleView.setOnClickListener(v -> {
                if (contentView.getVisibility() == View.VISIBLE) {
                    contentView.setVisibility(View.GONE);
                    if (titleView instanceof TextView) {
                        ((TextView) titleView).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_expand_more_24, 0);
                    }
                } else {
                    contentView.setVisibility(View.VISIBLE);
                    if (titleView instanceof TextView) {
                        ((TextView) titleView).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_expand_less_24, 0);
                    }
                }
            });
        }
    }

    private String getValue(String value, String key, SharedPreferences prefs) {
        if (value == null || value.isEmpty()) {
            return prefs.getString(key, "--");
        }
        return value;
    }

    private void setText(View root, int id, String value) {
        setText(root, id, value, "0");
    }

    private void setText(View root, int id, String value, String defaultValue) {
        TextView tv = root.findViewById(id);
        if (tv != null) {
            tv.setText(value == null || value.isEmpty() ? defaultValue : value);
        }
    }
}

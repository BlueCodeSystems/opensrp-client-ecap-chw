package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.dao.MalariaMonthlyDao;
import com.bluecodeltd.ecap.chw.dao.NutritionMonthlyDao;
import com.bluecodeltd.ecap.chw.dao.TbMonthlyDao;
import com.bluecodeltd.ecap.chw.model.MalariaMonthlyModel;
import com.bluecodeltd.ecap.chw.model.NutritionMonthlyModel;
import com.bluecodeltd.ecap.chw.model.TbMonthlyModel;
import com.bluecodeltd.ecap.chw.util.ReportFormUtils;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import timber.log.Timber;

public class ReportHomeActivity extends AppCompatActivity {

    public static final String EXTRA_REPORT_TYPE = "report_type";
    
    private String reportType;
    private RecyclerView recyclerHistory;
    private View cardRecent;
    private TextView txtRecentMonth, txtRecentDate, txtNoHistory, txtRecentStatus;
    private List<Object> reportModels = new ArrayList<>();

    public static void start(Context context, String reportType) {
        Intent intent = new Intent(context, ReportHomeActivity.class);
        intent.putExtra(EXTRA_REPORT_TYPE, reportType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_home);

        reportType = getIntent().getStringExtra(EXTRA_REPORT_TYPE);
        if (reportType == null) reportType = ReportRegisterActivity.REPORT_TYPE_MALARIA;

        setupToolbar();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
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
        title.setText(getReportTitle());
    }

    private String getReportTitle() {
        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) return getString(R.string.report_nutrition);
        if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) return getString(R.string.report_tb);
        return getString(R.string.menu_malaria);
    }

    private void initViews() {
        recyclerHistory = findViewById(R.id.recycler_history);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        cardRecent = findViewById(R.id.card_recent_report);
        txtRecentMonth = findViewById(R.id.txt_recent_month);
        txtRecentDate = findViewById(R.id.txt_recent_date);
        txtRecentStatus = findViewById(R.id.txt_recent_status);
        txtNoHistory = findViewById(R.id.txt_no_history);

        findViewById(R.id.card_new_report).setOnClickListener(v -> launchReportForm(null, false));
    }

    private void loadData() {
        List<ReportInfo> history = new ArrayList<>();
        reportModels.clear();
        
        if (ReportRegisterActivity.REPORT_TYPE_MALARIA.equals(reportType)) {
            List<MalariaMonthlyModel> reports = new MalariaMonthlyDao().getAllMalariaMonthly();
            for (MalariaMonthlyModel m : reports) {
                history.add(new ReportInfo(m.getReportingMonth(), getString(R.string.submitted_at, m.getReportingMonth()), m.getBaseEntityId()));
                reportModels.add(m);
            }
        } else if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) {
            List<TbMonthlyModel> reports = new TbMonthlyDao().getAllTbMonthly();
            for (TbMonthlyModel m : reports) {
                history.add(new ReportInfo(m.getReportingMonth(), getString(R.string.submitted_at, m.getReportingMonth()), m.getBaseEntityId()));
                reportModels.add(m);
            }
        } else {
            List<NutritionMonthlyModel> reports = new NutritionMonthlyDao().getAllNutritionMonthly();
            for (NutritionMonthlyModel m : reports) {
                history.add(new ReportInfo(m.getReportingPeriod(), getString(R.string.submitted_at, m.getReportingPeriod()), m.getBaseEntityId()));
                reportModels.add(m);
            }
        }

        if (!history.isEmpty()) {
            ReportInfo recent = history.get(0);
            Object recentModel = reportModels.get(0);
            String status = getModelStatus(recentModel);
            
            cardRecent.setVisibility(View.VISIBLE);
            findViewById(R.id.lbl_recent_report).setVisibility(View.VISIBLE);
            txtRecentMonth.setText(recent.month);
            txtRecentDate.setText(recent.date);
            txtRecentStatus.setText(status != null ? status.toUpperCase() : "SUBMITTED");
            txtRecentStatus.setTextColor(status != null && status.equalsIgnoreCase("draft") ? 
                    getResources().getColor(R.color.pie_chart_no_red) : 
                    getResources().getColor(R.color.status_green));

            findViewById(R.id.btn_edit_recent).setOnClickListener(v -> launchReportForm(recentModel, false));
            findViewById(R.id.btn_view_recent).setOnClickListener(v -> ReportViewActivity.start(this, recent.id, reportType));
            
            List<ReportInfo> historical = history.size() > 1 ? history.subList(1, history.size()) : new ArrayList<>();
            if (historical.isEmpty()) {
                txtNoHistory.setVisibility(View.VISIBLE);
                recyclerHistory.setVisibility(View.GONE);
            } else {
                txtNoHistory.setVisibility(View.GONE);
                recyclerHistory.setVisibility(View.VISIBLE);
                recyclerHistory.setAdapter(new HistoryAdapter(historical, reportModels.subList(1, reportModels.size())));
            }
        } else {
            cardRecent.setVisibility(View.GONE);
            findViewById(R.id.lbl_recent_report).setVisibility(View.GONE);
            txtNoHistory.setVisibility(View.VISIBLE);
            recyclerHistory.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
            if (jsonString != null) {
                boolean isDraft = data.getBooleanExtra(JsonFormConstants.SKIP_VALIDATION, false);
                ChwApplication.getInstance().getAppExecutors().diskIO().execute(() -> {
                    new com.bluecodeltd.ecap.chw.interactor.ReportRegisterInteractor().saveForm(jsonString, isDraft ? "draft" : "complete");
                    ChwApplication.getInstance().getAppExecutors().mainThread().execute(this::loadData);
                });
            }
        }
    }

    private String getModelStatus(Object model) {
        if (model instanceof MalariaMonthlyModel) return ((MalariaMonthlyModel) model).getReportStatus();
        if (model instanceof TbMonthlyModel) return ((TbMonthlyModel) model).getReportStatus();
        if (model instanceof NutritionMonthlyModel) return ((NutritionMonthlyModel) model).getReportStatus();
        return null;
    }

    private void launchReportForm(Object model, boolean isReadOnly) {
        String formName = getFormName();
        try {
            JSONObject form = new FormUtils(this).getFormJson(formName);
            if (form != null) {
                if (model == null) {
                    populateReportDefaults(form);
                } else {
                    prePopulateForm(form, model);
                }
                
                if (isReadOnly) {
                    makeFormReadOnly(form);
                }
                
                startFormActivity(form);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void prePopulateForm(JSONObject form, Object model) {
        if (model instanceof MalariaMonthlyModel) {
            ReportFormUtils.prePopulateMalariaForm(form, (MalariaMonthlyModel) model);
        } else if (model instanceof TbMonthlyModel) {
            ReportFormUtils.prePopulateTbForm(form, (TbMonthlyModel) model);
        } else if (model instanceof NutritionMonthlyModel) {
            ReportFormUtils.prePopulateNutritionForm(form, (NutritionMonthlyModel) model);
        }
    }

    private void makeFormReadOnly(JSONObject form) {
        try {
            int stepCount = form.optInt("count", 1);
            for (int i = 1; i <= stepCount; i++) {
                String stepName = "step" + i;
                if (form.has(stepName)) {
                    JSONArray fields = form.getJSONObject(stepName).getJSONArray("fields");
                    for (int j = 0; j < fields.length(); j++) {
                        JSONObject field = fields.getJSONObject(j);
                        field.put("read_only", true);
                        if ("submit_button".equals(field.optString("key"))) {
                            field.put("type", "hidden");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getFormName() {
        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) return ReportRegisterActivity.REPORT_FORM_NUTRITION;
        if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) return ReportRegisterActivity.REPORT_FORM_TB;
        return ReportRegisterActivity.REPORT_FORM_MALARIA;
    }

    private void populateReportDefaults(JSONObject form) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setStep1FieldValue(form, "province", prefs.getString("province", ""));
        setStep1FieldValue(form, "district", prefs.getString("district", ""));
        setStep1FieldValue(form, "ward", prefs.getString("ward", ""));
        setStep1FieldValue(form, "facility", prefs.getString("facility", ""));
        setStep1FieldValue(form, "partner", prefs.getString("partner", ""));
        setStep1FieldValue(form, "reporting_month", new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date()));
        setStep1FieldValue(form, "reporting_year", new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        setStep1FieldValue(form, "form_id", generateFormId(prefs));
    }

    private String generateFormId(SharedPreferences prefs) {
        String code = prefs.getString("code", "");
        int randomNumber = new Random().nextInt(100000000);
        return (code != null && !code.trim().isEmpty()) ? code + "/" + randomNumber : String.valueOf(randomNumber);
    }

    private void setStep1FieldValue(JSONObject form, String key, String value) {
        try {
            JSONArray fields = fields(form, "step1");
            JSONObject field = getFieldJSONObject(fields, key);
            if (field != null) {
                field.put(org.smartregister.family.util.JsonFormUtils.VALUE, value == null ? "" : value);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, ReportFormActivity.class);
        Form wizardForm = new Form();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    private static class ReportInfo {
        String month;
        String date;
        String id;
        ReportInfo(String month, String date, String id) {
            this.month = month;
            this.date = date;
            this.id = id;
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private final List<ReportInfo> items;
        private final List<Object> models;

        HistoryAdapter(List<ReportInfo> items, List<Object> models) { 
            this.items = items; 
            this.models = models;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.report_history_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ReportInfo item = items.get(position);
            Object model = models.get(position);
            holder.txtMonth.setText(item.month);
            holder.txtDate.setText(item.date);
            
            // Set icon based on type
            int iconRes = R.mipmap.sidemenu_malaria;
            if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) iconRes = R.drawable.ic_nutrition_24;
            else if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) iconRes = R.drawable.ic_tb_24;
            holder.imgIcon.setImageResource(iconRes);
            
            holder.itemView.setOnClickListener(v -> ReportViewActivity.start(ReportHomeActivity.this, item.id, reportType));
            holder.btnEdit.setOnClickListener(v -> launchReportForm(model, false));
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtMonth, txtDate;
            android.widget.ImageView imgIcon, btnEdit;
            ViewHolder(View itemView) {
                super(itemView);
                txtMonth = itemView.findViewById(R.id.txt_history_month);
                txtDate = itemView.findViewById(R.id.txt_history_date);
                imgIcon = itemView.findViewById(R.id.img_history_icon);
                btnEdit = itemView.findViewById(R.id.btn_edit_history);
            }
        }
    }
}

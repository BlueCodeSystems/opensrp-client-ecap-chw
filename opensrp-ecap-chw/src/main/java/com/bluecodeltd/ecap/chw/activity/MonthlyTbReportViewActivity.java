package com.bluecodeltd.ecap.chw.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.databinding.MonthlyTbReportBinding;
import com.bluecodeltd.ecap.chw.dao.MonthlyReportDao;
import com.bluecodeltd.ecap.chw.model.MonthlyReportModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import org.json.JSONObject;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.util.FormUtils;
import java.util.Map;

public class MonthlyTbReportViewActivity extends AppCompatActivity {

    private static final String EXTRA_ENTITY_ID = "base_entity_id";
    private MonthlyTbReportBinding binding;
    private MonthlyReportModel reportModel;

    public static void start(Context context, String baseEntityId) {
        Intent intent = new Intent(context, MonthlyTbReportViewActivity.class);
        intent.putExtra(EXTRA_ENTITY_ID, baseEntityId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MonthlyTbReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupClickListeners();

        String entityId = getIntent().getStringExtra(EXTRA_ENTITY_ID);
        if (entityId != null) {
            loadData(entityId);
        }
    }

    private void setupClickListeners() {
        binding.headerQ1.setOnClickListener(v -> toggleSection(binding.tableQ1, binding.iconQ1));
        binding.headerQ2.setOnClickListener(v -> toggleSection(binding.tableQ2, binding.iconQ2));
        binding.headerQ3.setOnClickListener(v -> toggleSection(binding.tableQ3, binding.iconQ3));
        binding.headerQ4.setOnClickListener(v -> toggleSection(binding.tableQ4, binding.iconQ4));
        binding.headerQ5.setOnClickListener(v -> toggleSection(binding.tableQ5, binding.iconQ5));
        binding.headerQ6.setOnClickListener(v -> toggleSection(binding.tableQ6, binding.iconQ6));
        binding.headerQ7.setOnClickListener(v -> toggleSection(binding.tableQ7, binding.iconQ7));
        binding.headerComments.setOnClickListener(v -> toggleSection(binding.containerComments, binding.iconComments));

        binding.btnEditReport.setOnClickListener(v -> openEditForm());
    }

    private void toggleSection(View container, ImageView icon) {
        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
            icon.setImageResource(R.drawable.baseline_expand_more_24);
        } else {
            container.setVisibility(View.VISIBLE);
            icon.setImageResource(R.drawable.baseline_expand_less_24);
        }
    }

    private void openEditForm() {
        if (reportModel == null) return;
        try {
            JSONObject form = new FormUtils(this).getFormJson(ReportRegisterActivity.REPORT_FORM_TB);
            if (form != null) {
                form.put("entity_id", reportModel.getBase_entity_id());
                org.smartregister.chw.core.utils.CoreJsonFormUtils.populateJsonForm(form, reportModel.toValueMap());
                
                Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
                org.smartregister.client.utils.domain.Form wizardForm = new org.smartregister.client.utils.domain.Form();
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
                startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
            }
        } catch (Exception e) {
            timber.log.Timber.e(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            // Refresh data after edit
            String entityId = getIntent().getStringExtra(EXTRA_ENTITY_ID);
            if (entityId != null) {
                loadData(entityId);
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.report_submission_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(R.string.report_tb);
            }
        }
    }

    private void loadData(String entityId) {
        reportModel = MonthlyReportDao.getReport(ReportRegisterActivity.REPORT_TABLE_TB, entityId);
        if (reportModel != null) {
            populateView(reportModel);
        }
    }

    private void populateView(MonthlyReportModel model) {
        if (model == null) return;

        if (model.getReporting_month() != null) {
            binding.txtReportingMonth.setText(model.getReporting_month());
        }
        if (model.getFacility() != null) {
            binding.txtFacilityName.setText(model.getFacility());
        }
        
        Map<String, String> data = model.toValueMap();
        
        // Map all fields from the model to the corresponding TextViews in the layout
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (key == null) continue;

            // Try to find the view by ID string (matching the JSON key)
            int resId = getResources().getIdentifier(key, "id", getPackageName());
            if (resId != 0) {
                TextView tv = findViewById(resId);
                if (tv != null) {
                    tv.setText(value != null && !value.isEmpty() ? value : "0");
                }
            }
        }
        
        // Handle comments specifically if needed
        String comments = model.getAdditionalField("comment"); // Fixed key from JSON
        if (comments != null && !comments.isEmpty()) {
            binding.txtComments.setText(comments);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

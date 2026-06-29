package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.bluecodeltd.ecap.chw.R;
import com.google.android.material.navigation.NavigationBarView;
import com.bluecodeltd.ecap.chw.fragment.ReportRegisterFragment;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.presenter.BaseChwNotificationPresenter;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ReportRegisterActivity extends BaseRegisterActivity {

    public static final String EXTRA_REPORT_TYPE = "report_type";
    public static final String REPORT_TYPE_MALARIA = "malaria";
    public static final String REPORT_TYPE_NUTRITION = "nutrition";
    public static final String REPORT_TYPE_TB = "tb";
    public static final String REPORT_FORM_MALARIA = "malaria_monthly_reporting";
    public static final String REPORT_FORM_NUTRITION = "monthly_nutrition_report";
    public static final String REPORT_FORM_TB = "monthly_tb_report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(org.smartregister.R.id.register_toolbar);
        NavigationMenu menu;
        if (toolbar != null) {
            if (getSupportActionBar() == null) {
                try {
                    setSupportActionBar(toolbar);
                } catch (IllegalStateException ignored) {
                }
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            toolbar.setTitle("");
            TextView titleLabel = toolbar.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleLabel != null) {
                titleLabel.setVisibility(View.GONE);
            }
            menu = NavigationMenu.getInstance(this, null, toolbar);
            try {
                if (menu != null) {
                    androidx.drawerlayout.widget.DrawerLayout drawer = menu.getDrawer();
                    androidx.appcompat.graphics.drawable.DrawerArrowDrawable arrow = new androidx.appcompat.graphics.drawable.DrawerArrowDrawable(this);
                    arrow.setColor(android.graphics.Color.WHITE);
                    toolbar.setNavigationIcon(arrow);
                    toolbar.setNavigationOnClickListener(v -> {
                        if (drawer != null) {
                            drawer.openDrawer(androidx.core.view.GravityCompat.START);
                        }
                    });
                }
            } catch (Throwable ignored) {
            }
        } else {
            menu = NavigationMenu.getInstance(this, null, null);
        }

        if (menu != null && menu.getNavigationAdapter() != null) {
            menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.REPORT_REGISTER);
        }
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseChwNotificationPresenter();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ReportRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        // No-op
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        // No-op
    }

    @Override
    public void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, ReportFormActivity.class);
        Form wizardForm = new Form();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, wizardForm);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
            if (jsonString != null) {
                boolean isDraft = data.getBooleanExtra(JsonFormConstants.SKIP_VALIDATION, false);
                new com.bluecodeltd.ecap.chw.interactor.ReportRegisterInteractor().saveForm(jsonString, isDraft ? "draft" : "complete");
                refreshList();
            }
        }
    }

    private void refreshList() {
        if (getRegisterFragment() instanceof ReportRegisterFragment) {
            ((ReportRegisterFragment) getRegisterFragment()).initializeAdapter();
        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_report_menu);
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new ChwBottomNavigationListener(this));
            bottomNavigationView.setItemIconTintList(
                    AppCompatResources.getColorStateList(this, R.color.bottom_navigation_icon_selector));
            bottomNavigationView.setItemTextColor(
                    AppCompatResources.getColorStateList(this, R.color.bottom_navigation_selector));
            bottomNavigationView.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.bottom_nav_bar_background));
            if (bottomNavigationView.getMenu().findItem(getSelectedBottomNavItemId()) != null) {
                bottomNavigationView.getMenu().findItem(getSelectedBottomNavItemId()).setChecked(true);
            }
        }
    }

    @Override
    public void startRegistration() {
        // No-op
    }

    public String getSelectedReportType() {
        String reportType = getIntent() == null ? null : getIntent().getStringExtra(EXTRA_REPORT_TYPE);
        if (REPORT_TYPE_NUTRITION.equals(reportType) || REPORT_TYPE_TB.equals(reportType)) {
            return reportType;
        }
        return REPORT_TYPE_MALARIA;
    }

    private int getSelectedBottomNavItemId() {
        String selectedReportType = getSelectedReportType();
        if (REPORT_TYPE_NUTRITION.equals(selectedReportType)) {
            return R.id.action_report_nutrition;
        }
        if (REPORT_TYPE_TB.equals(selectedReportType)) {
            return R.id.action_report_tb;
        }
        return R.id.action_report_malaria;
    }

    public boolean openReportType(String reportType) {
        String selectedReportType = getSelectedReportType();
        if (reportType.equals(selectedReportType)) {
            return true;
        }

        Intent intent = new Intent(this, ReportRegisterActivity.class);
        intent.putExtra(EXTRA_REPORT_TYPE, reportType);
        startActivity(intent);
        finish();
        return true;
    }

    public boolean launchReportForm(String reportType) {
        String formName = getFormName(reportType);
        try {
            JSONObject form = new FormUtils(this).getFormJson(formName);
            if (form == null) {
                return false;
            }
            if (REPORT_TYPE_MALARIA.equals(reportType)) {
                populateMalariaReportDefaults(form);
            } else if (REPORT_TYPE_TB.equals(reportType)) {
                populateTbReportDefaults(form);
            } else if (REPORT_TYPE_NUTRITION.equals(reportType)) {
                populateNutritionReportDefaults(form);
            }
            startFormActivity(form);
            return true;
        } catch (Exception e) {
            timber.log.Timber.e(e);
            return false;
        }
    }

    private String getFormName(String reportType) {
        if (REPORT_TYPE_NUTRITION.equals(reportType)) {
            return REPORT_FORM_NUTRITION;
        }
        if (REPORT_TYPE_TB.equals(reportType)) {
            return REPORT_FORM_TB;
        }
        return REPORT_FORM_MALARIA;
    }

    private void populateMalariaReportDefaults(JSONObject form) {
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

    private void populateTbReportDefaults(JSONObject form) {
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

    private void populateNutritionReportDefaults(JSONObject form) {
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
        if (code != null && !code.trim().isEmpty()) {
            return code + "/" + randomNumber;
        }
        return String.valueOf(randomNumber);
    }

    private void setStep1FieldValue(JSONObject form, String key, String value) {
        JSONObject field = getFieldJSONObject(fields(form, "step1"), key);
        if (field == null) {
            return;
        }
        field.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
        try {
            field.put(org.smartregister.family.util.JsonFormUtils.VALUE, value == null ? "" : value);
        } catch (org.json.JSONException e) {
            timber.log.Timber.e(e);
        }
    }
}

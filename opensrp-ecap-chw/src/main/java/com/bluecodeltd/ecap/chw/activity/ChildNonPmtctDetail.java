package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bluecodeltd.ecap.chw.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.util.FormUtils;

import timber.log.Timber;

/**
 * Simple child profile screen for non-PMTCT children.
 * Exposes quick links to child_final_outcome, child_longitudinal_follow_up
 * and child_postnatal_care JSON forms.
 */
public class ChildNonPmtctDetail extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_BASE_ENTITY_ID = "base_entity_id";
    public static final String EXTRA_HOUSEHOLD_ID = "household_id";
    public static final String EXTRA_UNIQUE_ID = "unique_id";

    private String baseEntityId;
    private String householdId;
    private String uniqueId;

    private LinearLayout childFinalOutcomeLayout;
    private LinearLayout childLongitudinalLayout;
    private LinearLayout childPostnatalLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_non_pmtct_detail);

        Toolbar toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();
        if (intent != null) {
            baseEntityId = intent.getStringExtra(EXTRA_BASE_ENTITY_ID);
            householdId = intent.getStringExtra(EXTRA_HOUSEHOLD_ID);
            uniqueId = intent.getStringExtra(EXTRA_UNIQUE_ID);
        }

        childFinalOutcomeLayout = findViewById(R.id.child_final_outcome);
        childLongitudinalLayout = findViewById(R.id.child_longitudinal_follow_up);
        childPostnatalLayout = findViewById(R.id.child_postnatal_care);

        childFinalOutcomeLayout.setOnClickListener(this);
        childLongitudinalLayout.setOnClickListener(this);
        childPostnatalLayout.setOnClickListener(this);
    }

    public static void start(Activity activity, String baseEntityId, String householdId, String uniqueId) {
        Intent intent = new Intent(activity, ChildNonPmtctDetail.class);
        intent.putExtra(EXTRA_BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(EXTRA_HOUSEHOLD_ID, householdId);
        intent.putExtra(EXTRA_UNIQUE_ID, uniqueId);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.child_final_outcome) {
            openChildForm("child_final_outcome");
        } else if (id == R.id.child_longitudinal_follow_up) {
            openChildForm("child_longitudinal_follow_up");
        } else if (id == R.id.child_postnatal_care) {
            openChildForm("child_postnatal_care");
        }
    }

    private void openChildForm(String formName) {
        try {
            FormUtils formUtils = new FormUtils(this);
            JSONObject form = formUtils.getFormJson(formName);

            if (baseEntityId != null) {
                form.put("entity_id", baseEntityId);
            }

            // Ensure IDs are visible in the form
            try {
                JSONArray flds = form.getJSONObject("step1").getJSONArray("fields");
                for (int i = 0; i < flds.length(); i++) {
                    JSONObject f = flds.getJSONObject(i);
                    String key = f.optString("key");
                    if ("household_id".equals(key) && householdId != null) {
                        f.put("value", householdId);
                    } else if ("unique_id".equals(key) && uniqueId != null) {
                        f.put("value", uniqueId);
                    }
                }
            } catch (Exception ignored) {
                // Use empty catch to avoid crashing form launch for minor mapping issues
            }

            // Basic pre-population hook if needed later
            try {
                JSONArray flds = fields(form, JsonFormConstants.STEP1);
                JSONObject baseEntityField = getFieldJSONObject(flds, "base_entity_id");
                if (baseEntityField != null && baseEntityId != null) {
                    baseEntityField.put(JsonFormConstants.VALUE, baseEntityId);
                }
            } catch (Exception ignored) {
            }

            startFormActivity(form);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startFormActivity(JSONObject jsonObject) {
        Form form = new Form();
        form.setWizard(false);
        form.setName(getString(org.smartregister.chw.core.R.string.child_details));
        form.setHideSaveLabel(true);
        form.setNextLabel(getString(R.string.next));
        form.setPreviousLabel(getString(R.string.previous));
        form.setSaveLabel(getString(R.string.submit));
        form.setNavigationBackground(R.color.primary);

        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivity(intent);
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public String getUniqueId() {
        return uniqueId;
    }
}

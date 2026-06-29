package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.fragment.app.Fragment;
import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONObject;
import timber.log.Timber;

public class ReportFormActivity extends JsonWizardFormActivity {

    @Override
    public void onBackPressed() {
        saveAndFinish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        saveAndFinish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveAndFinish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAndFinish() {
        try {
            if (getCurrentFocus() != null) {
                getCurrentFocus().clearFocus();
            }

            // Force current fragment to save its state to mJSONObject
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof JsonFormFragment) {
                    ((JsonFormFragment) fragment).save(true);
                }
            }

            if (getmJSONObject() != null) {
                Intent intent = new Intent();
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, getmJSONObject().toString());
                intent.putExtra(JsonFormConstants.SKIP_VALIDATION, true);
                setResult(RESULT_OK, intent);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        finish();
    }
}

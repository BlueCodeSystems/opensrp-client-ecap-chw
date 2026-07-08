package com.bluecodeltd.ecap.chw.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

public class ReportFormActivity extends JsonWizardFormActivity {

    @Override
    protected void toggleViewVisibility(View view, boolean visible, boolean popup) {
        try {
            JSONArray canvasViewIds = new JSONArray((String) view.getTag(com.vijay.jsonwizard.R.id.canvas_ids));
            String addressString = (String) view.getTag(com.vijay.jsonwizard.R.id.address);
            String[] address = addressString.split(":");
            JSONObject object = getObjectUsingAddress(address, popup);
            boolean enabled = visible;
            if (object != null && object.has(JsonFormConstants.READ_ONLY) &&
                    object.getBoolean(JsonFormConstants.READ_ONLY) && visible) {
                enabled = false;
            }

            view.setEnabled(enabled);
            if (view instanceof com.rengwuxian.materialedittext.MaterialEditText || view instanceof RelativeLayout || view instanceof LinearLayout) {
                view.setFocusable(enabled);
                if (view instanceof com.rengwuxian.materialedittext.MaterialEditText) {
                    view.setFocusableInTouchMode(enabled);
                }
            }

            // Custom logic instead of calling private updateCanvas
            for (int i = 0; i < canvasViewIds.length(); i++) {
                int curId = canvasViewIds.getInt(i);
                View curCanvasView = view.getRootView().findViewById(curId);

                if (curCanvasView == null) {
                    continue;
                }

                if (visible) {
                    curCanvasView.setEnabled(true);
                    curCanvasView.setVisibility(View.VISIBLE);

                    if (curCanvasView instanceof RelativeLayout || view instanceof LinearLayout) {
                        curCanvasView.setFocusable(true);
                    }
                    if (view instanceof android.widget.EditText) {
                        view.setFocusable(true);
                    }

                    curCanvasView.invalidate();
                } else {
                    // Requirement #2: DO NOT clear values when hidden
                    // clearHiddenViewsValues(object, addressString); // OMITTED
                    curCanvasView.setEnabled(false);
                    curCanvasView.setVisibility(View.GONE);
                    // refreshViews(curCanvasView); // OMITTED - this clears the UI fields
                }

                curCanvasView.setTag(com.vijay.jsonwizard.R.id.relevance_decided, visible);

                if (object != null) {
                    object.put(JsonFormConstants.IS_VISIBLE, visible);
                    //Only keep track of required fields that are invisible
                    if (object.has(JsonFormConstants.V_REQUIRED) && object.getJSONObject(JsonFormConstants.V_REQUIRED)
                            .getBoolean(JsonFormConstants.VALUE)) {
                        trackInvisibleFields(object, visible);
                    }
                }
            }

            // We cannot call private setReadOnlyAndFocus, but it mostly does the same enabled/focusable logic we already did above.
            // The only missing piece is setReadOnlyRadioButtonOptions for RadioGroups.

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void trackInvisibleFields(final JSONObject object, final boolean visible) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    synchronized (invisibleRequiredFields) {
                        if (visible) {
                            invisibleRequiredFields.remove(object.getString(JsonFormConstants.KEY));
                        } else {
                            invisibleRequiredFields.add(object.getString(JsonFormConstants.KEY));
                        }
                        getmJSONObject().put(JsonFormConstants.INVISIBLE_REQUIRED_FIELDS, invisibleRequiredFields);
                    }
                } catch (JSONException e) {
                    Timber.e(e);
                }
                return null;
            }
        }.execute();
    }

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

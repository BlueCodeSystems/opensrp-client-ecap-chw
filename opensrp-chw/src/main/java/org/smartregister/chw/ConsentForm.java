package org.smartregister.chw;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.factory.FileSourceFactoryHelper;

import org.json.JSONArray;
import org.json.JSONObject;

public class ConsentForm extends AppCompatActivity  implements View.OnClickListener{

    private static final int REQUEST_CODE_GET_JSON = 1234;
    private static final String TAG = ConsentForm.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_form);
        findViewById(R.id.consent_form).setOnClickListener(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String jsonString = data.getStringExtra("json");
            Log.i(getClass().getName(), "Result json String !!!! " + jsonString);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        try {
            switch (id) {


                case R.id.consent_form:
                    startForm(REQUEST_CODE_GET_JSON, "consent_form", null, false);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    public void startForm(int jsonFormActivityRequestCode, String formName, String entityId, boolean translate) throws Exception {

        final String STEP1 = "step1";
        final String FIELDS = "fields";
        final String KEY = "key";
        final String ZEIR_ID = "ZEIR_ID";
        final String VALUE = "value";

        String currentLocationId = "Kenya";

        JSONObject jsonForm = FileSourceFactoryHelper.getFileSource("").getFormFromFile(getApplicationContext(), formName);
        if (jsonForm != null) {
            jsonForm.getJSONObject("metadata").put("encounter_location", currentLocationId);

            switch (formName) {

                case "consent_form": {
                    Intent intent = new Intent(this, JsonWizardFormActivity.class);
                    intent.putExtra("json", jsonForm.toString());
                    Log.d(getClass().getName(), "form is " + jsonForm.toString());

                    Form form = new Form();
                    form.setName("Consent Form");
                    form.setWizard(true);
                    form.setHideSaveLabel(true);
                    form.setNextLabel(getString(R.string.next));
                    form.setPreviousLabel(getString(R.string.previous));
                    form.setSaveLabel(getString(R.string.save));
                    form.setBackIcon(R.drawable.ic_icon_positive);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

                    startActivityForResult(intent, jsonFormActivityRequestCode);
                    break;
                }



                default: {


                    if (entityId == null) {
                        entityId = "ABC" + Math.random();
                    }


                    // Inject zeir id into the form
                    JSONObject stepOne = jsonForm.getJSONObject(STEP1);
                    JSONArray jsonArray = stepOne.getJSONArray(FIELDS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString(KEY)
                                .equalsIgnoreCase(ZEIR_ID)) {
                            jsonObject.remove(VALUE);
                            jsonObject.put(VALUE, entityId);
                            continue;
                        }
                    }

                    Intent intent = new Intent(this, JsonFormActivity.class);
                    intent.putExtra("json", jsonForm.toString());
                    intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION, translate);
                    Log.d(getClass().getName(), "form is " + jsonForm.toString());
                    startActivityForResult(intent, jsonFormActivityRequestCode);
                    break;
                }
            }

        }

    }

}
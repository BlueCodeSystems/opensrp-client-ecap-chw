package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.family.util.JsonFormUtils.STEP2;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.contract.MotherIndexContract;
import com.bluecodeltd.ecap.chw.fragment.IndexFragmentRegister;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.presenter.ChwAllClientRegisterPresenter;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterPresenter;
import com.bluecodeltd.ecap.chw.presenter.MotherIndexPresenter;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.referral.R.id;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.UniqueId;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class IndexRegisterActivity extends BaseRegisterActivity implements IndexRegisterContract.View {

    public String action = null;
    ObjectMapper oMapper;
    ObjectMapper oMapper_hh_screen;
    private UniqueIdRepository uniqueIdRepository;
    Random Number;
    int Rnumber;
    private String uniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);

        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        String password = extras.getString("password");

        if (username != null && password != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(IndexRegisterActivity.this);
            String code = sp.getString("code", "0000");

            if (!sp.contains("code") || code.equals("0000")) {

                getToken(username, password);

            }

        }

    }
    private void getToken (final String username, final String password) {

        String tag_string_req = "req_login";

        String url = "https://keycloak.who.bluecodeltd.com/auth/realms/anc-stage/protocol/openid-connect/token";
        StringRequest
                stringRequest
                = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {


                        String jsonInString = new Gson().toJson(response.toString().trim());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString().trim());

                            String token  = jsonObject.getString("access_token");

                            getCreds(token);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                error -> {

                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("grant_type","password");
                params.put("username",username);
                params.put("password",password);
                params.put("scope","openid");
                params.put("client_id", BuildConfig.OAUTH_CLIENT_ID);
                params.put("client_secret",BuildConfig.OAUTH_CLIENT_SECRET);
                return params;
            }};

        ChwApplication.getApplicationFlavor().chwAppInstance().addToRequestQueue(stringRequest, tag_string_req);

    }

    private void getCreds(String token){

        String tag_string_creds = "req_creds";

        String url = "https://keycloak.who.bluecodeltd.com/auth/realms/anc-stage/protocol/openid-connect/userinfo";
        StringRequest
                stringRequest
                = new StringRequest(
                Request.Method.GET,
                url,
                (Response.Listener<String>) response -> {

                    try {
                        JSONObject jObj = new JSONObject(response);

                        String sub = jObj.getString("sub");
                        String code = jObj.getString("code");
                        String name = jObj.getString("name");
                        String given_name = jObj.getString("given_name");
                        String family_name = jObj.getString("family_name");
                        String province = jObj.getString("province");
                        String partner = jObj.getString("partner");
                        String phone = jObj.getString("phone");
                        String district = jObj.getString("district");
                        String facility = jObj.getString("facility");
                        String email = jObj.getString("email");
                        String nrc = jObj.getString("nrc");

                        // save user data
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(IndexRegisterActivity.this);
                        SharedPreferences.Editor edit = sp.edit();


                        edit.putString("sub", sub);
                        edit.putString("code", code);
                        edit.putString("caseworker_name", name);
                        edit.putString("given_name", given_name);
                        edit.putString("family_name", family_name);
                        edit.putString("province", province);
                        edit.putString("partner", partner);
                        edit.putString("phone", phone);
                        edit.putString("district", district);
                        edit.putString("facility", facility);
                        edit.putString("email", email);
                        edit.putString("nrc", nrc);

                        edit.commit();

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                },
                error -> {

                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }};


        ChwApplication.getApplicationFlavor().chwAppInstance().addToRequestQueue(stringRequest, tag_string_creds);

    }


    @Override
    protected void initializePresenter() {
        this.presenter = new IndexRegisterPresenter(this);
    }

    private IndexRegisterContract.Presenter indexRegisterPresenter(){
        return (IndexRegisterPresenter) this.presenter;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new IndexFragmentRegister();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {

    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            String locationId = Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            ((IndexRegisterPresenter) this.presenter).startForm(formName, entityId, metaData, locationId);

        } catch (Exception e) {
            Timber.e(e);
            displayToast(org.smartregister.family.R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

        oMapper = new ObjectMapper();
        oMapper_hh_screen = new ObjectMapper();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(IndexRegisterActivity.this);
        String code = sp.getString("code", "00000");
        Object obj = sp.getAll();

        //******** HOUSEHOLD ID ******//
        Number = new Random();
        Rnumber = Number.nextInt(100000000);
        String xId =  Integer.toString(Rnumber);
        String household_id = code + "/" + xId;


        Number = new Random();
        Rnumber = Number.nextInt(900000000);
        String newEntityId =  Integer.toString(Rnumber);


        //******** POPULATE JSON FORM VCA UNIQUE ID ******//
        JSONObject stepOneUniqueId = getFieldJSONObject(fields(jsonObject, STEP1), "unique_id");

        if (stepOneUniqueId != null) {
            stepOneUniqueId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
            try {
                stepOneUniqueId.put(JsonFormUtils.VALUE, newEntityId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            //******** POPULATE JSON FORM WITH HOUSEHOLD ID ******//
            CoreJsonFormUtils.populateJsonForm(jsonObject,oMapper.convertValue(obj, Map.class));

            JSONObject stepHouseholdId = getFieldJSONObject(fields(jsonObject, STEP1), "household_id");

            if (stepHouseholdId != null) {
                stepHouseholdId.remove(org.smartregister.family.util.JsonFormUtils.VALUE);
                try {
                    stepHouseholdId.put(JsonFormUtils.VALUE, household_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
            Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
            Form form = new Form();
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
            startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if(requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK){

            String jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
            try {

                JSONObject jsonFormObject = new JSONObject(jsonString);


                if (Constants.EcapEncounterType.CHILD_INDEX.equalsIgnoreCase(
                        jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))) {
                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));
                    showProgressDialog(R.string.saving_dialog_title);
                    indexRegisterPresenter().saveForm(jsonString, registerParam);
                    uniqueId = getFieldJSONObject(fields(jsonFormObject, STEP1), "unique_id").optString("value");
                    //JSONObject stepHouseholdId = getFieldJSONObject(fields(jsonObject, STEP1), "unique_id");

                    gotToChildProfile(uniqueId);
                } else if (Constants.EcapEncounterType.FSW.equalsIgnoreCase(
                        jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))) {


                    String age = getFieldJSONObject(fields(jsonFormObject, STEP1), "age").optString("value");
                    String children = getFieldJSONObject(fields(jsonFormObject, STEP1), "children_under18").optString("value");


                    if (Integer.parseInt(age) > 19 && children.equals("yes")){

                        openForm("hh_screening_entry", jsonFormObject);


                    } else if (Integer.parseInt(age) > 10 && Integer.parseInt(age) < 18) {

                        openForm("vca_screening", jsonFormObject);

                    } else {
                        Toasty.error(this, "This FSW Cannot be enrolled into the program", Toast.LENGTH_LONG, true).show();
                    }



                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    public void openForm(String formName, JSONObject jsonFormObject){

        org.smartregister.util.FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(IndexRegisterActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(IndexRegisterActivity.this);
        Object obj = sp.getAll();


        String fname = getFieldJSONObject(fields(jsonFormObject, STEP1), "first_name").optString("value");
        String lname = getFieldJSONObject(fields(jsonFormObject, STEP1), "last_name").optString("value");
        String dob = getFieldJSONObject(fields(jsonFormObject, STEP1), "adolescent_birthdate").optString("value");
        String hh_id = getFieldJSONObject(fields(jsonFormObject, STEP1), "household_id").optString("value");
        String u_id = getFieldJSONObject(fields(jsonFormObject, STEP1), "unique_id").optString("value");
        String gender = "female";
        String sexually_active = "yes";

        JSONObject indexRegisterForm = formUtils.getFormJson(formName);

        if (formName.equals("vca_screening")){

            JSONObject hh_idObject = getFieldJSONObject(fields(indexRegisterForm, STEP1), "household_id");
            JSONObject unique_idObject = getFieldJSONObject(fields(indexRegisterForm, STEP1), "unique_id");
            JSONObject first_name = getFieldJSONObject(fields(indexRegisterForm, STEP1), "first_name");
            JSONObject last_name = getFieldJSONObject(fields(indexRegisterForm, STEP1), "last_name");
            JSONObject vca_dob = getFieldJSONObject(fields(indexRegisterForm, STEP1), "adolescent_birthdate");
            JSONObject vca_gender = getFieldJSONObject(fields(indexRegisterForm, STEP1), "gender");
            JSONObject agyActive = getFieldJSONObject(fields(indexRegisterForm, STEP1), "agyw_sexually_active");

            try {
                CoreJsonFormUtils.populateJsonForm(jsonFormObject,oMapper.convertValue(obj, Map.class));
                hh_idObject.put(JsonFormUtils.VALUE, hh_id);
                unique_idObject.put(JsonFormUtils.VALUE, u_id);
                first_name.put(JsonFormUtils.VALUE, fname);
                last_name.put(JsonFormUtils.VALUE, lname);
                vca_dob.put(JsonFormUtils.VALUE, dob);
                vca_gender.put(JsonFormUtils.VALUE, gender);
                agyActive.put(JsonFormUtils.VALUE, sexually_active);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            JSONObject hh_idObject = getFieldJSONObject(fields(indexRegisterForm, STEP2), "household_id");
            JSONObject unique_idObject = getFieldJSONObject(fields(indexRegisterForm, STEP2), "unique_id");
            JSONObject cc_name = getFieldJSONObject(fields(indexRegisterForm, STEP2), "caregiver_name");
            JSONObject cc_dob = getFieldJSONObject(fields(indexRegisterForm, STEP2), "caregiver_birth_date");
            JSONObject cc_sex = getFieldJSONObject(fields(indexRegisterForm, STEP2), "caregiver_sex");

            try {
                CoreJsonFormUtils.populateJsonForm(jsonFormObject,oMapper.convertValue(obj, Map.class));
                hh_idObject.put(JsonFormUtils.VALUE, hh_id);
                unique_idObject.put(JsonFormUtils.VALUE, u_id);
                cc_name.put(JsonFormUtils.VALUE, fname + " " + lname);
                cc_dob.put(JsonFormUtils.VALUE, dob);
                cc_sex.put(JsonFormUtils.VALUE, gender);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        startFormActivity(indexRegisterForm);

    }

    public void gotToChildProfile(String id){
        Intent intent = new Intent(this,IndexDetailsActivity.class);

        intent.putExtra("Child",id);
        Toasty.success(this, "Form Saved", Toast.LENGTH_LONG, true).show();
        startActivity(intent);
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void startRegistration() {
        //Overridden
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView,
                    new ChwBottomNavigationListener(this));
            bottomNavigationView.getMenu().removeItem(R.id.action_register);
            bottomNavigationView.getMenu().removeItem(R.id.action_register_index);
         //   bottomNavigationView.getMenu().findItem(R.id.action_identifcation).setTitle( "Add VCA");

        }
    }

    @Override
    public void toggleDialogVisibility(boolean showDialog) {
        if (showDialog) {
            showProgressDialog(R.string.saving_index);
        } else {
            hideProgressDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profilemenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {


            case R.id.user:

                Intent i = new Intent(this, Profile.class);
                startActivity(i);


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

}

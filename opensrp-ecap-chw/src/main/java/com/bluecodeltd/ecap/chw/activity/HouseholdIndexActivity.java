package com.bluecodeltd.ecap.chw.activity;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_EXTRA.STEP3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.contract.HouseholdIndexContract;
import com.bluecodeltd.ecap.chw.fragment.HouseholdIndexFragment;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.presenter.HouseholdIndexPresenter;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

public class HouseholdIndexActivity extends BaseRegisterActivity implements HouseholdIndexContract.View{

    public String action = null;
    private UniqueIdRepository uniqueIdRepository;
    Random Number;
    int Rnumber;
    ObjectMapper oMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(HouseholdIndexActivity.this, null, null);
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new HouseholdIndexPresenter(this);
    }

    private HouseholdIndexContract.Presenter householdIndexPresenter(){
        return (HouseholdIndexPresenter) this.presenter;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new HouseholdIndexFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        //Overridden
    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

        oMapper = new ObjectMapper();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String code = sp.getString("code", "00000");
        Object obj = sp.getAll();


        Number = new Random();
        Rnumber = Number.nextInt(100000000);


        String xId =  Integer.toString(Rnumber);

        String household_id = code + "/" + xId;

        try {
            CoreJsonFormUtils.populateJsonForm(jsonObject,oMapper.convertValue(obj, Map.class));//
            jsonObject.getJSONObject("step2").getJSONArray("fields").getJSONObject(3).put("value",household_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //******** VCA ID *********//

        Number = new Random();
        Rnumber = Number.nextInt(900000000);
        String newEntityId =  Integer.toString(Rnumber);

        //******** POPULATE AS INDEX VCA ******//
        JSONObject indexCheckObject = getFieldJSONObject(fields(jsonObject, STEP3), "index_check_box");

        if (indexCheckObject != null) {
            try {
                indexCheckObject.put(JsonFormUtils.VALUE, "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //******** POPULATE JSON FORM WITH VCA UNIQUE ID ******//
        JSONObject stepOneUniqueId = getFieldJSONObject(fields(jsonObject, "step2"), "unique_id");

        if (stepOneUniqueId != null) {
            stepOneUniqueId.remove(JsonFormUtils.VALUE);
            try {
                stepOneUniqueId.put(JsonFormUtils.VALUE, newEntityId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        Form form = new Form();
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            String locationId = Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            ((HouseholdIndexPresenter) this.presenter).startForm(formName, entityId, metaData, locationId);

        } catch (Exception e) {
            Timber.e(e);
            displayToast(org.smartregister.family.R.string.error_unable_to_start_form);
        }
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if(requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK){

            String jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);


            try {

                JSONObject jsonFormObject = new JSONObject(jsonString);


                if (Constants.EcapEncounterType.HOUSEHOLD_INDEX.equalsIgnoreCase(
                        jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))) {
//                    RegisterParams registerParam = new RegisterParams();
//                    registerParam.setEditMode(false);
//                    registerParam.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));
//                    showProgressDialog(R.string.saving_dialog_title);
              //householdIndexPresenter().saveForm(jsonString, registerParam);

                    //Gson gson = new Gson();
                    //String json = gson.toJson(formToBeOpened);
                    Intent passClosureForm   =  new Intent(this,SignatureActivity.class);
                    passClosureForm.putExtra("jsonForm", jsonString);
                    startActivity(passClosureForm);

                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
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
            bottomNavigationView.getMenu().removeItem(R.id.action_fsw);
            bottomNavigationView.getMenu().removeItem(R.id.action_hts);
            bottomNavigationView.getMenu().findItem(R.id.action_identifcation).setTitle( "Add Household");

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

    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }
}

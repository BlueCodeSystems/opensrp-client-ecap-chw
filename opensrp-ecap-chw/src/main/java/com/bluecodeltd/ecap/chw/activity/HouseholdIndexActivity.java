package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.contract.HouseholdIndexContract;
import com.bluecodeltd.ecap.chw.contract.MotherIndexContract;
import com.bluecodeltd.ecap.chw.fragment.HouseholdIndexFragment;
import com.bluecodeltd.ecap.chw.fragment.MotherIndexFragment;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.presenter.HouseholdIndexPresenter;
import com.bluecodeltd.ecap.chw.presenter.MotherIndexPresenter;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Utils;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HouseholdIndexActivity extends BaseRegisterActivity implements HouseholdIndexContract.View{

    public String action = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
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

        try {

            jsonObject.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).put("x","My Ward X");

            Log.d("jjson", "myjson : " + jsonObject.toString());

            Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
            Form form = new Form();
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonObject.toString());
            startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if(requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK){
            String json = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);

            try {
                if (json != null) {
                    JSONObject jsonFormObject = new JSONObject(json);
                    if (Constants.EcapEncounterType.HOUSEHOLD.equalsIgnoreCase(
                            jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))) {
                        householdIndexPresenter().saveForm(json, false);
                    }
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
}

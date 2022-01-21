package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.contract.MotherIndexContract;
import com.bluecodeltd.ecap.chw.fragment.IndexFragmentRegister;
import com.bluecodeltd.ecap.chw.fragment.MotherIndexFragment;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterPresenter;
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
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import timber.log.Timber;

public class MotherIndexActivity extends BaseRegisterActivity implements MotherIndexContract.View {

    public String action = null;
    Random Number;
    int Rnumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new MotherIndexPresenter(this);
    }

    private MotherIndexContract.Presenter motherIndexPresenter(){
        return (MotherIndexPresenter) this.presenter;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new MotherIndexFragment();
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


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String code = sp.getString("code", "00000");

        Number = new Random();
        Rnumber = Number.nextInt(100000000);


        String xId =  Integer.toString(Rnumber);

        String household_id = code + "/" + xId;

        try {
            jsonObject.getJSONObject("step1").getJSONArray("fields").getJSONObject(2).put("value",household_id);


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
            String json = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);

            try {
                if (json != null) {
                    JSONObject jsonFormObject = new JSONObject(json);
                    if (Constants.EcapEncounterType.MOTHER_INDEX.equalsIgnoreCase(
                            jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE, ""))) {

                        RegisterParams registerParam = new RegisterParams();
                        registerParam.setEditMode(false);
                        registerParam.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));

                        motherIndexPresenter().saveForm(json, false);
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
            bottomNavigationView.getMenu().removeItem(R.id.action_fsw);
            bottomNavigationView.getMenu().findItem(R.id.action_identifcation).setTitle( "Add Mother");

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

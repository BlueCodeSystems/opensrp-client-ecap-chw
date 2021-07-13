package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.fragment.IndexFragmentRegister;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterPresenter;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Utils;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;
import java.util.Map;

public class IndexRegisterActivity extends BaseRegisterActivity {

    public String action = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);


    }

    @Override
    protected void initializePresenter() {
        this.presenter = new IndexRegisterPresenter();
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
        //Overridden
    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {
        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra(Constants.JSON, jsonObject.toString());
        Form form = new Form();
        form.setName(getString(R.string.add_fam));
        form.setActionBarBackground(R.color.family_actionbar);
        form.setNavigationBackground(R.color.family_navigation);
        form.setHomeAsUpIndicator(R.mipmap.ic_cross_white);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {
        //Overridden
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
        }
    }
}

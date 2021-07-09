package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.bluecodeltd.ecap.chw.fragment.IndexFragmentRegister;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterPresenter;
import com.bluecodeltd.ecap.chw.util.Utils;

import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;
import java.util.Map;

public class IndexRegisterActivity extends BaseRegisterActivity {

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
        //Overridden
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

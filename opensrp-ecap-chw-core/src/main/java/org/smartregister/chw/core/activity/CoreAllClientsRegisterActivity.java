package org.smartregister.chw.core.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.fragment.CoreAllClientsRegisterFragment;
import org.smartregister.chw.core.presenter.CoreAllClientsRegisterPresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.opd.activity.BaseOpdRegisterActivity;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Map;

public class CoreAllClientsRegisterActivity extends BaseOpdRegisterActivity {

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new CoreAllClientsRegisterFragment();
    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {
        //implement
    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {
        //implement
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected BaseOpdRegisterActivityPresenter createPresenter(@NonNull OpdRegisterActivityContract.View view, @NonNull OpdRegisterActivityContract.Model model) {
        return new CoreAllClientsRegisterPresenter(view, model);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(CoreConstants.DrawerMenu.ALL_CLIENTS);
        }
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        // to do
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, CoreFamilyRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void startRegistration() {
        //implement
    }
}

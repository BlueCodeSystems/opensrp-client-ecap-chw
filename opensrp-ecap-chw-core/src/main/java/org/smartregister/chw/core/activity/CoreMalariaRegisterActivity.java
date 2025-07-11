package org.smartregister.chw.core.activity;

import android.os.Bundle;

import org.json.JSONObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.chw.malaria.activity.BaseMalariaRegisterActivity;
import org.smartregister.family.util.JsonFormUtils;

import java.util.Collections;
import java.util.List;

public abstract class CoreMalariaRegisterActivity extends BaseMalariaRegisterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        startActivityForResult(FormUtils.getStartFormActivity(jsonForm, this.getString(R.string.malaria_registration), this), JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Collections.singletonList(CoreConstants.CONFIGURATION.MALARIA_REGISTER);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(CoreConstants.DrawerMenu.MALARIA);
        }
    }
}
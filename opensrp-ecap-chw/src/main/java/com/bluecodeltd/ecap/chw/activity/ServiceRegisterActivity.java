package com.bluecodeltd.ecap.chw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterContract;
import com.bluecodeltd.ecap.chw.fragment.ServiceRegisterFragment;
import com.bluecodeltd.ecap.chw.interactor.IndexRegisterInteractor;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.model.EventClient;
import com.bluecodeltd.ecap.chw.util.IndexClientsUtils;
import com.bluecodeltd.ecap.chw.util.ServiceRegister;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.bluecodeltd.ecap.chw.util.Utils;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

/**
 * Hosts a {@link ServiceRegisterFragment}; one subclass per register so each has its own
 * manifest entry and drawer target. The "Add Client" button opens the register's form and
 * submissions are saved like the HTS register's.
 */
public abstract class ServiceRegisterActivity extends BaseRegisterActivity {

    public abstract ServiceRegister getServiceRegister();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(org.smartregister.R.id.register_toolbar);
        NavigationMenu menu;
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            menu = NavigationMenu.getInstance(this, null, toolbar);
            toolbar.setTitle("");
            TextView titleLabel = toolbar.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleLabel != null) {
                titleLabel.setVisibility(View.GONE);
            }
            try {
                if (menu != null) {
                    androidx.drawerlayout.widget.DrawerLayout drawer = menu.getDrawer();
                    androidx.appcompat.graphics.drawable.DrawerArrowDrawable arrow = new androidx.appcompat.graphics.drawable.DrawerArrowDrawable(this);
                    arrow.setColor(android.graphics.Color.WHITE);
                    toolbar.setNavigationIcon(arrow);
                    toolbar.setNavigationOnClickListener(v -> {
                        if (drawer != null) drawer.openDrawer(androidx.core.view.GravityCompat.START);
                    });
                }
            } catch (Throwable ignored) {}
        } else {
            menu = NavigationMenu.getInstance(this, null, null);
        }

        if (menu != null && menu.getNavigationAdapter() != null) {
            menu.getNavigationAdapter().setSelectedView(getServiceRegister().drawerTag);
        }
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new BaseRegisterContract.Presenter() {
            @Override
            public void registerViewConfigurations(List<String> viewIdentifiers) {
                //Overridden
            }

            @Override
            public void unregisterViewConfiguration(List<String> viewIdentifiers) {
                //Overridden
            }

            @Override
            public void onDestroy(boolean isChangingConfiguration) {
                //Overridden
            }

            @Override
            public void updateInitials() {
                //Overridden
            }
        };
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return ServiceRegisterFragment.newInstance(getServiceRegister());
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    /** Opens the register's enrollment form; called from the bottom navigation "Add Client" button. */
    public void openRegisterForm() {
        String formName = getServiceRegister().formName;
        Threading.io(() -> {
            try {
                JSONObject form = new FormUtils(this).getFormJson(formName);
                Threading.main(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    startFormActivity(form);
                });
            } catch (Exception e) {
                Timber.e(e, "Unable to open %s", formName);
                Threading.main(() -> displayToast(R.string.error_unable_to_start_form));
            }
        });
    }

    @Override
    public void startFormActivity(String formName, String entityId, Map<String, String> metaData) {
        openRegisterForm();
    }

    @Override
    public void startFormActivity(JSONObject form) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            JSONObject clientNumber = JsonFormUtils.getFieldJSONObject(JsonFormUtils.fields(form), "client_number");
            if (clientNumber != null) {
                clientNumber.put(JsonFormUtils.VALUE, Integer.toString(new Random().nextInt(900000000)));
            }
            JSONObject facility = JsonFormUtils.getFieldJSONObject(JsonFormUtils.fields(form), "health_facility");
            if (facility != null) {
                facility.put(JsonFormUtils.VALUE, sp.getString("facility", ""));
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        Form formParams = new Form();
        formParams.setWizard(false);
        formParams.setHideSaveLabel(true);
        formParams.setSaveLabel(getString(R.string.submit));
        formParams.setNavigationBackground(R.color.primary);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParams);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, form.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode != JsonFormUtils.REQUEST_CODE_GET_JSON || resultCode != RESULT_OK || data == null) {
            return;
        }
        try {
            String jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
            JSONObject form = new JSONObject(jsonString);
            if (getServiceRegister().encounterType.equalsIgnoreCase(form.optString(JsonFormConstants.ENCOUNTER_TYPE))) {
                saveForm(jsonString);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    /**
     * Saves the submission the same way the HTS and PMTCT registers do: the event and client go to the
     * sync tables, and the client processor fills the register table via ec_client_classification.json.
     */
    private void saveForm(String jsonString) {
        List<EventClient> eventClients = IndexClientsUtils.getEventClients(jsonString);
        if (eventClients == null || eventClients.isEmpty()) {
            Toasty.error(this, getString(R.string.service_register_save_failed), Toast.LENGTH_LONG, true).show();
            return;
        }

        RegisterParams registerParams = new RegisterParams();
        registerParams.setEditMode(false);
        registerParams.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));
        showProgressDialog(R.string.service_register_saving);

        new IndexRegisterInteractor(null).saveRegistration(eventClients, jsonString, registerParams,
                new IndexRegisterContract.InteractorCallBack() {
                    @Override
                    public void onNoUniqueId() {
                        //Not used: these registers generate their own client number
                    }

                    @Override
                    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
                        //Not used: these registers generate their own client number
                    }

                    @Override
                    public void onRegistrationSaved(boolean isEdit) {
                        if (isFinishing() || isDestroyed()) return;
                        hideProgressDialog();
                        refreshList(FetchStatus.fetched);
                        NavigationMenu navigationMenu = NavigationMenu.getInstance(ServiceRegisterActivity.this, null, null);
                        if (navigationMenu != null) {
                            navigationMenu.refreshCount();
                        }
                        Toasty.success(ServiceRegisterActivity.this, getString(R.string.service_register_saved),
                                Toast.LENGTH_SHORT, true).show();
                    }
                });
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
            bottomNavigationView.getMenu().findItem(R.id.action_identifcation).setTitle(R.string.service_register_add_client);
        }
    }
}

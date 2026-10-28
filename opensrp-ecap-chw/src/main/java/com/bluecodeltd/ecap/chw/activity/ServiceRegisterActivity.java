package com.bluecodeltd.ecap.chw.activity;

import static com.bluecodeltd.ecap.chw.util.JsonFormUtils.tagSyncMetadata;

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
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import com.bluecodeltd.ecap.chw.fragment.ServiceRegisterFragment;
import com.bluecodeltd.ecap.chw.listener.ChwBottomNavigationListener;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.IndexClientsUtils;
import com.bluecodeltd.ecap.chw.util.ServiceRegister;
import com.bluecodeltd.ecap.chw.util.ServiceRegisterRules;
import com.bluecodeltd.ecap.chw.util.ServiceRegisterValues;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.bluecodeltd.ecap.chw.util.Utils;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.client.utils.domain.Form;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.dmoral.toasty.Toasty;
import timber.log.Timber;

/**
 * Hosts a {@link ServiceRegisterFragment}; one subclass per register so each has its own
 * manifest entry and drawer target. The "Add Client" button opens the register's enrolment form; a row's
 * action and its details sheet open the follow-up form.
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
            showDrawerIcon(toolbar, menu);
        } else {
            menu = NavigationMenu.getInstance(this, null, null);
        }

        if (menu != null && menu.getNavigationAdapter() != null) {
            menu.getNavigationAdapter().setSelectedView(getServiceRegister().drawerTag);
        }
    }

    /** White drawer icon on the green toolbar; re-applied after a save, which rebuilds the menu without it. */
    private void showDrawerIcon(Toolbar toolbar, NavigationMenu menu) {
        try {
            if (toolbar == null || menu == null) return;
            androidx.drawerlayout.widget.DrawerLayout drawer = menu.getDrawer();
            androidx.appcompat.graphics.drawable.DrawerArrowDrawable arrow = new androidx.appcompat.graphics.drawable.DrawerArrowDrawable(this);
            arrow.setColor(android.graphics.Color.WHITE);
            toolbar.setNavigationIcon(arrow);
            toolbar.setNavigationOnClickListener(v -> {
                if (drawer != null) drawer.openDrawer(androidx.core.view.GravityCompat.START);
            });
        } catch (Throwable ignored) {}
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

    /** Opens the register's enrolment form; called from the bottom navigation "Add Client" button. */
    public void openRegisterForm() {
        openForm(getServiceRegister().formName, null);
    }

    /** Opens the follow-up form for a record, filled with what the record already holds. */
    public void openFollowUpForm(Map<String, String> columns) {
        openForm(getServiceRegister().followUpFormName, columns);
    }

    private void openForm(String formName, Map<String, String> record) {
        Threading.io(() -> {
            try {
                JSONObject form = new FormUtils(this).getFormJson(formName);
                if (record == null) {
                    prefillNew(form);
                } else {
                    prefillFollowUp(form, record);
                }
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

    /** Fills the hidden fields that say who recorded a new record, where and when. */
    private void prefillNew(JSONObject form) throws JSONException {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String caseworker = sp.getString("caseworker_name", "");
        if (caseworker == null || caseworker.trim().isEmpty()) {
            caseworker = sp.getString("last_logged_in_username", "");
        }
        JSONArray fields = JsonFormUtils.fields(form);
        ServiceRegisterRules.set(fields, "client_number", Integer.toString(new Random().nextInt(900000000)));
        ServiceRegisterRules.set(fields, "health_facility", sp.getString("facility", ""));
        ServiceRegisterRules.set(fields, "caseworker_name", caseworker);
        ServiceRegisterRules.set(fields, "date_created", ServiceRegisterValues.stored(LocalDate.now()));
        ServiceRegisterRules.set(fields, "referred_by", caseworker);
    }

    /** Ties the form to the record and shows the record's current answers, so a follow-up only adds to them. */
    private void prefillFollowUp(JSONObject form, Map<String, String> record) throws JSONException {
        form.put(Constants.JSON_FORM_KEY.ENTITY_ID, ServiceRegisterValues.get(record, "base_entity_id"));
        JSONArray fields = JsonFormUtils.fields(form);
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String value = ServiceRegisterValues.get(record, field.optString(JsonFormUtils.KEY));
            if (value.isEmpty() || "hidden".equals(field.optString("type"))) continue;
            if ("check_box".equals(field.optString("type"))) {
                List<String> ticked = ServiceRegisterValues.ticked(value);
                JSONArray options = field.optJSONArray("options");
                for (int o = 0; options != null && o < options.length(); o++) {
                    JSONObject option = options.getJSONObject(o);
                    option.put(JsonFormUtils.VALUE, Boolean.toString(ticked.contains(option.optString(JsonFormUtils.KEY))));
                }
                field.put(JsonFormUtils.VALUE, new JSONArray(ticked));
            } else {
                field.put(JsonFormUtils.VALUE, value);
            }
        }
    }

    @Override
    public void startFormActivity(JSONObject form) {
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
            if (getServiceRegister().handles(form.optString(JsonFormConstants.ENCOUNTER_TYPE))) {
                save(form);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    /**
     * Saves a submission like the other registers: the event and client go to the sync tables and the
     * client processor fills the register table via ec_client_classification.json. A follow-up form keeps
     * the record's id, and its answers are merged into the client it already has, so the row keeps the
     * enrolment answers and gains the new ones.
     */
    private void save(JSONObject form) {
        showProgressDialog(R.string.service_register_saving);
        Threading.io(() -> {
            boolean saved = false;
            try {
                String encounterType = form.getString(JsonFormConstants.ENCOUNTER_TYPE);
                String entityId = form.optString(Constants.JSON_FORM_KEY.ENTITY_ID);
                boolean followUp = !entityId.isEmpty();
                if (!followUp) entityId = org.smartregister.util.JsonFormUtils.generateRandomUUIDString();

                ECSyncHelper sync = ChwApplication.getInstance().getEcSyncHelper();
                JSONObject existing = followUp ? sync.getClient(entityId) : null;
                JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                ServiceRegisterRules.apply(encounterType, fields, attributes(existing));

                Event event = org.smartregister.util.JsonFormUtils.createEvent(fields, form.getJSONObject(Constants.METADATA),
                        IndexClientsUtils.getFormTag(), entityId, encounterType, getServiceRegister().table);
                tagSyncMetadata(event);
                Client client = org.smartregister.util.JsonFormUtils.createBaseClient(fields, IndexClientsUtils.getFormTag(), entityId);
                JSONObject clientJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(client));
                if (existing != null) clientJson = mergeAttributes(existing, clientJson);

                sync.addClient(entityId, clientJson);
                sync.addEvent(entityId, new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(event)));

                long lastUpdatedAt = IndexClientsUtils.getAllSharedPreferences().fetchLastUpdatedAtDate(0);
                List<org.smartregister.domain.db.EventClient> savedEvents =
                        sync.getEvents(Collections.singletonList(event.getFormSubmissionId()));
                ChwApplication.getInstance().getClientProcessorForJava().processClient(savedEvents);
                IndexClientsUtils.getAllSharedPreferences().saveLastUpdatedAtDate(lastUpdatedAt);
                saved = true;
            } catch (Exception e) {
                Timber.e(e, "Unable to save the %s record", getServiceRegister().name());
            }
            boolean finalSaved = saved;
            Threading.main(() -> {
                if (isFinishing() || isDestroyed()) return;
                hideProgressDialog();
                if (!finalSaved) {
                    Toasty.error(this, getString(R.string.service_register_save_failed), Toast.LENGTH_LONG, true).show();
                    return;
                }
                refreshList(FetchStatus.fetched);
                Toolbar toolbar = findViewById(org.smartregister.R.id.register_toolbar);
                NavigationMenu navigationMenu = NavigationMenu.getInstance(this, null, toolbar);
                if (navigationMenu != null) {
                    navigationMenu.refreshCount();
                }
                showDrawerIcon(toolbar, navigationMenu);
                Toasty.success(this, getString(R.string.service_register_saved), Toast.LENGTH_SHORT, true).show();
            });
        });
    }

    private static Map<String, String> attributes(JSONObject client) {
        Map<String, String> values = new HashMap<>();
        JSONObject attributes = client != null ? client.optJSONObject("attributes") : null;
        if (attributes == null) return values;
        Iterator<String> keys = attributes.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            values.put(key, attributes.optString(key));
        }
        return values;
    }

    /** The existing client with the follow-up's attributes laid over its own. */
    private static JSONObject mergeAttributes(JSONObject existing, JSONObject update) throws JSONException {
        JSONObject merged = new JSONObject(existing.toString());
        JSONObject attributes = merged.optJSONObject("attributes");
        if (attributes == null) {
            attributes = new JSONObject();
            merged.put("attributes", attributes);
        }
        JSONObject updates = update.optJSONObject("attributes");
        if (updates != null) {
            Iterator<String> keys = updates.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                attributes.put(key, updates.get(key));
            }
        }
        if (update.has("dateCreated")) merged.put("dateEdited", update.get("dateCreated"));
        return merged;
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

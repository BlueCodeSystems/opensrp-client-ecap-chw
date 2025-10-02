package org.smartregister.chw.anc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.MenuRes;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.fragment.BaseAncRegisterFragment;
import org.smartregister.chw.anc.interactor.BaseAncRegisterInteractor;
import org.smartregister.chw.anc.listener.BaseAncBottomNavigationListener;
import org.smartregister.chw.anc.model.BaseAncRegisterModel;
import org.smartregister.chw.anc.presenter.BaseAncRegisterPresenter;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.opensrp_chw_anc.R;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BaseAncRegisterActivity extends BaseRegisterActivity implements BaseAncRegisterContract.View {

    protected String BASE_ENTITY_ID;
    protected String ACTION;
    protected String TABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BASE_ENTITY_ID = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID);
        ACTION = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        TABLE = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.TABLE_NAME);
        onStartActivityWithAction();
    }

    /**
     * Process a payload when an activity is started with an action
     */
    protected void onStartActivityWithAction() {
        if (ACTION != null && ACTION.equals(Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION)) {
            startFormActivity(getRegistrationForm(), BASE_ENTITY_ID, (String)null);
        }
    }

    @Override
    public void startRegistration() {
        startFormActivity(getRegistrationForm(), null, (String)null);
    }

    public String getRegistrationForm() {
        return Constants.FORMS.ANC_REGISTRATION;
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            if (mBaseFragment instanceof BaseAncRegisterFragment) {
                presenter().startForm(formName, entityId, metaData, getLocationID());
            }
        } catch (Exception e) {
            Timber.e(e);
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    protected String getLocationID() {
        return Context.getInstance().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, getAncFormActivity());
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        if (getFormConfig() != null) {
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, getFormConfig());
        }

        startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    public Form getFormConfig() {
        return null;
    }

    @Override
    public void onRegistrationSaved(String register, boolean isEdit, boolean hasChildren) {
        startRegisterActivity(getRegisterActivity(register));
    }

    public Class getRegisterActivity(String register) {
        return BaseAncRegisterActivity.class;
    }

    protected void startRegisterActivity(Class registerClass) {
        Intent intent = new Intent(this, registerClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
        this.finish();
    }

    public Class getAncFormActivity() {
        return BaseAncRegisterActivity.class;
    }

    public String getFormRegistrationEvent() {
        return Constants.EVENT_TYPE.ANC_REGISTRATION;
    }

    public String getFormEditRegistrationEvent() {
        return Constants.EVENT_TYPE.ANC_REGISTRATION;
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(Constants.CONFIGURATION.ANC_REGISTER);
    }

    /**
     * Returns the event type for a malaria registration
     *
     * @return
     */
    public String getRegisterEventType() {
        return Constants.EVENT_TYPE.ANC_REGISTRATION;
    }

    /**
     * Override this to subscribe to bottom navigation
     */
    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(R.id.action_register);
            bottomNavigationView.getMenu().removeItem(R.id.action_search);
            bottomNavigationView.getMenu().removeItem(R.id.action_library);

            bottomNavigationView.inflateMenu(getMenuResource());

            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            BottomNavigationListener familyBottomNavigationListener = getBottomNavigation(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(familyBottomNavigationListener);

        }
    }

    @MenuRes
    public int getMenuResource() {
        return R.menu.bottom_nav_menu;
    }

    public BottomNavigationListener getBottomNavigation(Activity activity) {
        return new BaseAncBottomNavigationListener(activity);
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseAncRegisterPresenter(this, new BaseAncRegisterModel(), new BaseAncRegisterInteractor());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new BaseAncRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        // do nothing
    }

    @Override
    public BaseAncRegisterContract.Presenter presenter() {
        return (BaseAncRegisterContract.Presenter) presenter;
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_GET_JSON) {
//            process the form
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSONResult %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounter_type = form.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);
                String table = data.getStringExtra(Constants.ACTIVITY_PAYLOAD.TABLE_NAME);

                if (encounter_type.equalsIgnoreCase(getRegisterEventType())) {

                    presenter().saveForm(jsonString, false, table);

                } else if (encounter_type.equalsIgnoreCase(Constants.EVENT_TYPE.PREGNANCY_OUTCOME)) {

                    presenter().saveForm(jsonString, false, table);

                } else if (encounter_type.startsWith(Constants.EVENT_TYPE.UPDATE_EVENT_CONDITION)) {

                    presenter().saveForm(form.toString(), true, TABLE);

                }
            } catch (Exception e) {
                Timber.e(e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}

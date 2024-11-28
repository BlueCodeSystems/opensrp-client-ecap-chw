package org.smartregister.chw.core.activity;

import android.content.Intent;
import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.activity.BaseAncRegisterActivity;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CoreAncRegisterActivity extends BaseAncRegisterActivity {
    protected String phone_number;
    protected String form_name;
    protected String unique_id;
    protected String familyBaseEntityId;
    protected String familyName;
    protected String lastMenstrualPeriod;

    public String getFormTable() {
        if (form_name != null && form_name.equals(CoreConstants.JSON_FORM.getAncRegistration())) {
            return CoreConstants.TABLE_NAME.ANC_MEMBER;
        }
        return CoreConstants.TABLE_NAME.ANC_PREGNANCY_OUTCOME;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getFormName() {
        return form_name;
    }

    public void setFormName(String form_name) {
        this.form_name = form_name;
    }

    public String getUniqueId() {
        return unique_id;
    }

    public void setUniqueId(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public void setFamilyBaseEntityId(String familyBaseEntityId) {
        this.familyBaseEntityId = familyBaseEntityId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getLastMenstrualPeriod() {
        return lastMenstrualPeriod;
    }

    public void setLastMenstrualPeriod(String lastMenstrualPeriod) {
        this.lastMenstrualPeriod = lastMenstrualPeriod;
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        if (getIntent() != null) {
            setPhoneNumber(getIntent().getStringExtra(CoreConstants.ACTIVITY_PAYLOAD.PHONE_NUMBER));
            setFormName(getIntent().getStringExtra(CoreConstants.ACTIVITY_PAYLOAD.FORM_NAME));
            setUniqueId(getIntent().getStringExtra(CoreConstants.ACTIVITY_PAYLOAD.UNIQUE_ID));
            setFamilyBaseEntityId(getIntent().getStringExtra(CoreConstants.ACTIVITY_PAYLOAD.FAMILY_BASE_ENTITY_ID));
            setFamilyName(getIntent().getStringExtra(CoreConstants.ACTIVITY_PAYLOAD.FAMILY_NAME));
            setLastMenstrualPeriod(getIntent().getStringExtra(CoreConstants.ACTIVITY_PAYLOAD.LAST_LMP));
        }
    }

    @Override
    public Class getRegisterActivity(String register) {
        if (register.equals(CoreConstants.SERVICE_GROUPS.ANC))
            return CoreAncRegisterActivity.class;
        else
            return CorePncRegisterActivity.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    public String getRegistrationForm() {
        return form_name;
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {

        try {
            JSONObject stepOne = jsonForm.getJSONObject(JsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);

            Map<String, String> values = new HashMap<>();

            values.put(DBConstants.KEY.TEMP_UNIQUE_ID, unique_id);
            values.put(CoreConstants.JsonAssets.FAM_NAME, familyName);
            values.put(CoreConstants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER, phone_number);
            values.put(org.smartregister.family.util.DBConstants.KEY.RELATIONAL_ID, familyBaseEntityId);
            values.put(DBConstants.KEY.LAST_MENSTRUAL_PERIOD, lastMenstrualPeriod);
            try {
                JSONObject min_date = CoreJsonFormUtils.getFieldJSONObject(jsonArray, "delivery_date");
                min_date.put("min_date", lastMenstrualPeriod);
            } catch (Exception e) {
                Timber.e(e);
            }

            FormUtils.updateFormField(jsonArray, values);

            Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
            intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

            Form form = new Form();
            form.setActionBarBackground(R.color.family_actionbar);
            form.setWizard(false);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

            startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String getFormRegistrationEvent() {
        return org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_REGISTRATION;
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(CoreConstants.CONFIGURATION.ANC_REGISTER);
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
     /*   if (!BuildConfig.SUPPORT_QR) {
            bottomNavigationView.getMenu().removeItem(R.id.action_scan_qr);
        }

        AncBottomNavigationListener listener = new AncBottomNavigationListener(this, bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);*/
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return super.getRegisterFragment();
    }

    protected void startRegisterActivity(Class registerClass) {
        Intent intent = new Intent(this, registerClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        this.finish();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter()
                    .setSelectedView(CoreConstants.DrawerMenu.ANC);
        }
    }

    @Override
    public void switchToBaseFragment() {
        /*Intent intent = new Intent(this, FamilyRegisterActivity.class);
        startActivity(intent);
        finish();*/
    }

}

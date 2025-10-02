package org.smartregister.chw.core.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.activity.BaseAncHomeVisitActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.util.LangUtils;

import java.text.MessageFormat;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.Utils.getDuration;


public abstract class CoreChildHomeVisitActivity extends BaseAncHomeVisitActivity {

    public static void startMe(Activity activity, MemberObject memberObject, Boolean isEditMode, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, memberObject);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.EDIT_MODE, isEditMode);
        activity.startActivityForResult(intent, org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT);
    }

    @Override
    protected abstract void registerPresenter();


    @Override
    public void startFormActivity(JSONObject jsonForm) {
        try {

            if (memberObject.getDob() != null) {
                JSONObject stepOne = jsonForm.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                JSONObject min_date = CoreJsonFormUtils.getFieldJSONObject(jsonArray, "birth_cert_issue_date");
                if (min_date != null) {
                    int days = CoreJsonFormUtils.getDayFromDate(memberObject.getDob());

                    min_date.put("min_date", "today-" + days + "d");
                }

            }
            Form form = new Form();
            form.setActionBarBackground(R.color.family_actionbar);
            form.setWizard(false);

            Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
            intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
            intent.putExtra(Constants.WizardFormActivity.EnableOnCloseDialog, false);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    @Override
    public void redrawHeader(MemberObject memberObject) {
        tvTitle.setText(MessageFormat.format("{0}, {1} {2}", memberObject.getFullName(), getDuration(memberObject.getDob()), getString(R.string.home_visit_suffix)));
    }

    /*
    @Override
    protected void attachBaseContext(Context base) {
        // get language from prefs
        String lang = LangUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(LangUtils.setAppLocale(base, lang));
    }
     */
}

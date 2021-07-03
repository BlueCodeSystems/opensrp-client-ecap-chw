package com.bluecodeltd.ecap.chw.presenter;

import android.app.Activity;

import org.json.JSONObject;
import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.activity.FamilyPlanningMemberProfileActivity;
import com.bluecodeltd.ecap.chw.activity.ReferralRegistrationActivity;
import com.bluecodeltd.ecap.chw.contract.AncMemberProfileContract;
import com.bluecodeltd.ecap.chw.contract.FamilyPlanningMemberProfileContract;
import org.smartregister.chw.core.presenter.CoreFamilyPlanningProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp.domain.FpMemberObject;
import com.bluecodeltd.ecap.chw.model.ReferralTypeModel;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.Utils;

import java.util.List;

import timber.log.Timber;

public class FamilyPlanningMemberProfilePresenter extends CoreFamilyPlanningProfilePresenter
        implements AncMemberProfileContract.Presenter {

    private FpMemberObject fpMemberObject;
    private List<ReferralTypeModel> referralTypeModels;

    public FamilyPlanningMemberProfilePresenter(FamilyPlanningMemberProfileContract.View view, FamilyPlanningMemberProfileContract.Interactor interactor,
                                                FpMemberObject fpMemberObject) {
        super(view, interactor, fpMemberObject);
        this.fpMemberObject = fpMemberObject;
    }

    @Override
    public void referToFacility() {
        referralTypeModels = ((FamilyPlanningMemberProfileActivity) getView()).getReferralTypeModels();
        if (referralTypeModels.size() == 1) {
            startFamilyPlanningReferral();
        } else {
            Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, fpMemberObject.getBaseEntityId());
        }
    }

    @Override
    public void startFamilyPlanningReferral() {
        try {
            if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
                JSONObject formJson = getFormUtils().getFormJson(Constants.JSON_FORM.getFamilyPlanningUnifiedReferralForm(fpMemberObject.getGender()));
                formJson.put(Constants.REFERRAL_TASK_FOCUS, referralTypeModels.get(0).getReferralType());
                ReferralRegistrationActivity.startGeneralReferralFormActivityForResults((Activity) getView(), fpMemberObject.getBaseEntityId(), formJson, false);
            } else {
                getView().startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getFamilyPlanningReferralForm(fpMemberObject.getGender())), fpMemberObject);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}

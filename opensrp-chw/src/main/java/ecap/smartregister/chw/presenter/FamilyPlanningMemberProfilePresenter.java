package ecap.smartregister.chw.presenter;

import android.app.Activity;

import org.json.JSONObject;
import ecap.smartregister.chw.BuildConfig;
import ecap.smartregister.chw.activity.FamilyPlanningMemberProfileActivity;
import ecap.smartregister.chw.activity.ReferralRegistrationActivity;
import ecap.smartregister.chw.contract.AncMemberProfileContract;
import ecap.smartregister.chw.contract.FamilyPlanningMemberProfileContract;
import org.smartregister.chw.core.presenter.CoreFamilyPlanningProfilePresenter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp.domain.FpMemberObject;
import ecap.smartregister.chw.model.ReferralTypeModel;
import ecap.smartregister.chw.util.Constants;
import ecap.smartregister.chw.util.Utils;

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

package ecap.smartregister.chw.presenter;

import android.app.Activity;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import ecap.smartregister.chw.BuildConfig;
import ecap.smartregister.chw.activity.AncMemberProfileActivity;
import ecap.smartregister.chw.activity.ReferralRegistrationActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.contract.AncMemberProfileContract;
import org.smartregister.chw.core.presenter.CoreAncMemberProfilePresenter;
import ecap.smartregister.chw.model.ReferralTypeModel;
import ecap.smartregister.chw.util.Constants;
import ecap.smartregister.chw.util.Utils;

import java.util.List;

import timber.log.Timber;

public class AncMemberProfilePresenter extends CoreAncMemberProfilePresenter
        implements ecap.smartregister.chw.contract.AncMemberProfileContract.Presenter {

    private List<ReferralTypeModel> referralTypeModels;

    public AncMemberProfilePresenter(AncMemberProfileContract.View view, AncMemberProfileContract.Interactor interactor,
                                     MemberObject memberObject) {
        super(view, interactor, memberObject);
    }

    @Override
    public void referToFacility() {
        referralTypeModels = ((AncMemberProfileActivity) getView()).getReferralTypeModels();
        if (referralTypeModels.size() == 1) {
            startAncReferralForm();
        } else {
            Utils.launchClientReferralActivity((Activity) getView(), referralTypeModels, getEntityId());
        }
    }

    @Override
    public void startAncReferralForm() {
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {
            try {
                Activity context = ((Activity) getView());
                JSONObject formJson = (new FormUtils()).getFormJsonFromRepositoryOrAssets(context, Constants.JSON_FORM.getAncUnifiedReferralForm());
                formJson.put(Constants.REFERRAL_TASK_FOCUS, referralTypeModels.get(0).getReferralType());
                ReferralRegistrationActivity.startGeneralReferralFormActivityForResults(context,
                        getEntityId(), formJson, true);
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else {
            super.startAncReferralForm();
        }

    }
}

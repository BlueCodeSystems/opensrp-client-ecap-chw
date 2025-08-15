package com.bluecodeltd.ecap.chw.presenter;

import org.jetbrains.annotations.NotNull;
import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.referral.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.referral.presenter.BaseReferralRegisterFragmentPresenter;
import org.smartregister.chw.referral.util.DBConstants;
import com.bluecodeltd.ecap.chw.util.ChwDBConstants;
import com.bluecodeltd.ecap.chw.util.Constants;

import static org.smartregister.chw.referral.util.Constants.ReferralType;
import static org.smartregister.chw.referral.util.Constants.Tables;

public class ReferralFollowupFragmentPresenter extends BaseReferralRegisterFragmentPresenter {

    public ReferralFollowupFragmentPresenter(BaseReferralRegisterFragmentContract.View view, BaseReferralRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @NotNull
    @Override
    public String getMainCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.Key.DATE_REMOVED + " is null " +
                "AND " + Constants.TABLE_NAME.TASK + "." + ChwDBConstants.TaskTable.BUSINESS_STATUS + " = '" + CoreConstants.BUSINESS_STATUS.REFERRED + "' "+
                "AND " + Tables.REFERRAL + "." + DBConstants.Key.REFERRAL_TYPE + " = '" + ReferralType.FACILITY_TO_COMMUNITY_REFERRAL + "' ";
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (getConfig().getSearchBarText() != null && getView() != null) {
            //TODO implement updateSearchBarHint in the view contract
//            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @NotNull
    @Override
    public String getMainTable() {
        return Tables.REFERRAL;
    }
}

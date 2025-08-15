package com.bluecodeltd.ecap.chw.presenter;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.referral.presenter.BaseReferralRegisterFragmentPresenter;
import org.smartregister.chw.referral.util.DBConstants;

import com.bluecodeltd.ecap.chw.fragment.ReferralRegisterFragment;
import com.bluecodeltd.ecap.chw.model.ReferralRegisterFragmentModel;
import com.bluecodeltd.ecap.chw.util.Constants;

import static org.smartregister.chw.referral.util.Constants.ReferralType;
import static org.smartregister.chw.referral.util.Constants.Tables;

public class ReferralRegisterFragmentPresenter extends BaseReferralRegisterFragmentPresenter {

    public ReferralRegisterFragmentPresenter(ReferralRegisterFragment view, ReferralRegisterFragmentModel model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    @NotNull
    public String getMainCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.Key.DATE_REMOVED + " is null " +
                "AND " + Tables.REFERRAL + "." + DBConstants.Key.REFERRAL_TYPE + " = '" + ReferralType.COMMUNITY_TO_FACILITY_REFERRAL + "' ";

    }

    @Override
    @NotNull
    public String getDueFilterCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.Key.DATE_REMOVED + " is null " +
                "AND " + Constants.TABLE_NAME.TASK + "." + ChwDBConstants.TaskTable.BUSINESS_STATUS + " = '" + CoreConstants.BUSINESS_STATUS.REFERRED + "' " +
                "AND " + Tables.REFERRAL + "." + DBConstants.Key.REFERRAL_TYPE + " = '" + ReferralType.COMMUNITY_TO_FACILITY_REFERRAL + "' ";

    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (getConfig().getSearchBarText() != null && getView() != null) {
            //TODO fix updateSearchBarHint not found
//            getView().updateSearchBarHint(getView().getContext().getString(org.smartregister.chw.core.R.string.search_name_or_id));
        }
    }

    @Override
    public String getMainTable() {
        return Tables.REFERRAL;
    }
}

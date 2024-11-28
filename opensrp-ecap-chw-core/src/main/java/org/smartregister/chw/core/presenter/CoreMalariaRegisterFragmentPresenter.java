package org.smartregister.chw.core.presenter;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.malaria.contract.MalariaRegisterFragmentContract;
import org.smartregister.chw.malaria.presenter.BaseMalariaRegisterFragmentPresenter;

public class CoreMalariaRegisterFragmentPresenter extends BaseMalariaRegisterFragmentPresenter {

    public CoreMalariaRegisterFragmentPresenter(MalariaRegisterFragmentContract.View view,
                                                MalariaRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return " ec_family_member.date_removed is null AND ec_malaria_confirmation.malaria  = 1 " +
                "AND datetime('NOW') <= datetime(ec_malaria_confirmation.last_interacted_with/1000, 'unixepoch', 'localtime','+15 days') AND ec_malaria_confirmation.is_closed = 0";

    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public String getMainTable() {
        return CoreConstants.TABLE_NAME.MALARIA_CONFIRMATION;
    }
}

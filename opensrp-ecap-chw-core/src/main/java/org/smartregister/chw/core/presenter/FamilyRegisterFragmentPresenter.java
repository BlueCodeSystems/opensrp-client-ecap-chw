package org.smartregister.chw.core.presenter;


import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.CoreFamilyRegisterFragmentContract;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.family.presenter.BaseFamilyRegisterFragmentPresenter;
import org.smartregister.family.util.DBConstants;

public class FamilyRegisterFragmentPresenter extends BaseFamilyRegisterFragmentPresenter implements CoreFamilyRegisterFragmentContract.Presenter {

    public FamilyRegisterFragmentPresenter(org.smartregister.family.contract.FamilyRegisterFragmentContract.View view, org.smartregister.family.contract.FamilyRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public String getDefaultSortQuery() {
        return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

    @Override
    public String getDueFilterCondition() {
        return getMainCondition() + " AND " + ChildDBConstants.childDueFilter();
    }

    @Override
    public String getMainCondition() {
        return  String.format(" %s is NULL AND (%s is NULL OR %s = '%s' OR %s = '%s')", DBConstants.KEY.DATE_REMOVED,
                DBConstants.KEY.ENTITY_TYPE, DBConstants.KEY.ENTITY_TYPE, CoreConstants.TABLE_NAME.FAMILY, DBConstants.KEY.ENTITY_TYPE, CoreConstants.TABLE_NAME.FAMILY_MEMBER);
    }
}

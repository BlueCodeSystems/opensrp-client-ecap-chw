package org.smartregister.chw.core.presenter;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.opd.contract.OpdRegisterFragmentContract;
import org.smartregister.opd.presenter.OpdRegisterFragmentPresenter;

public class CoreAllClientsRegisterFragmentPresenter extends OpdRegisterFragmentPresenter {

    public CoreAllClientsRegisterFragmentPresenter(OpdRegisterFragmentContract.View view, OpdRegisterFragmentContract.Model model) {
        super(view, model);
    }

    @Override
    public void initializeQueries(String mainCondition) {
        getView().initializeQueryParams(CoreConstants.TABLE_NAME.FAMILY_MEMBER, null, null);
        getView().initializeAdapter();
        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

}

package org.smartregister.chw.core.presenter;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.contract.BaseChwNotificationFragmentContract;
import org.smartregister.chw.core.model.BaseChwNotificationModel;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.lang.ref.WeakReference;

public abstract class BaseChwNotificationFragmentPresenter implements
        BaseChwNotificationFragmentContract.Presenter {

    private WeakReference<BaseChwNotificationFragmentContract.View> viewReference;
    protected BaseChwNotificationModel model;

    public BaseChwNotificationFragmentPresenter(BaseChwNotificationFragmentContract.View view,
                                                BaseChwNotificationModel model) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
    }

    @Override
    public void processViewConfigurations() {
        if (getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {
        getView().initializeQueryParams("ec_family_member", null, null);
        getView().initializeAdapter();
        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    protected BaseChwNotificationFragmentContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void startSync() {
        //Overridden not required
    }

    @Override
    public void searchGlobally(String s) {
        //Overridden not required
    }

    @Override
    abstract public void displayDetailsActivity(CommonPersonObjectClient client, String notificationId, String notificationType);
}

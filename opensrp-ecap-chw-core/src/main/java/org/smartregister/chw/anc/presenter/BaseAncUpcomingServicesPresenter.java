package org.smartregister.chw.anc.presenter;

import org.smartregister.chw.anc.contract.BaseAncUpcomingServicesContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseUpcomingService;

import java.lang.ref.WeakReference;
import java.util.List;

public class BaseAncUpcomingServicesPresenter implements BaseAncUpcomingServicesContract.Presenter, BaseAncUpcomingServicesContract.InteractorCallBack {

    private BaseAncUpcomingServicesContract.Interactor interactor;
    private WeakReference<BaseAncUpcomingServicesContract.View> view;
    private MemberObject memberObject;

    public BaseAncUpcomingServicesPresenter(MemberObject memberObject, BaseAncUpcomingServicesContract.Interactor interactor, BaseAncUpcomingServicesContract.View view) {
        this.interactor = interactor;
        this.view = new WeakReference<>(view);
        this.memberObject = memberObject;

        initialize();
    }

    @Override
    public void initialize() {
        if (getView() != null) {
            getView().displayLoadingState(true);
            interactor.getUpComingServices(memberObject, getView().getContext(), this);
        }
    }

    @Override
    public BaseAncUpcomingServicesContract.View getView() {
        if (view != null && view.get() != null) {
            return view.get();
        }
        return null;
    }

    @Override
    public void onDataFetched(List<BaseUpcomingService> serviceList) {
        if (getView() != null) {
            getView().displayLoadingState(false);
            getView().refreshServices(serviceList);
        }
    }
}

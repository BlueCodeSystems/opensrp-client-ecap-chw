package org.smartregister.chw.anc.presenter;

import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.Visit;

import java.lang.ref.WeakReference;
import java.util.List;

public class BaseAncMedicalHistoryPresenter implements BaseAncMedicalHistoryContract.Presenter, BaseAncMedicalHistoryContract.InteractorCallBack {

    private BaseAncMedicalHistoryContract.Interactor interactor;
    private WeakReference<BaseAncMedicalHistoryContract.View> view;
    private String memberID;

    public BaseAncMedicalHistoryPresenter(BaseAncMedicalHistoryContract.Interactor interactor, BaseAncMedicalHistoryContract.View view, String memberID) {
        this.interactor = interactor;
        this.view = new WeakReference<>(view);
        this.memberID = memberID;

        initialize();
    }

    @Override
    public void initialize() {
        if (getView() == null)
            return;

        interactor.getMemberHistory(memberID, getView().getViewContext(), this);
    }

    @Override
    public BaseAncMedicalHistoryContract.View getView() {
        if (view.get() != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void onDataFetched(List<Visit> visits) {
        if (getView() != null) {
            getView().onDataReceived(visits);
        }
    }
}

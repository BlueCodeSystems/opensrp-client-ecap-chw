package org.smartregister.chw.anc.presenter;

import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.domain.AlertStatus;
import org.smartregister.view.contract.BaseProfileContract;

import java.lang.ref.WeakReference;
import java.util.Date;

public class BaseAncMemberProfilePresenter implements BaseProfileContract.Presenter, BaseAncMemberProfileContract.InteractorCallBack, BaseAncMemberProfileContract.Presenter {

    protected WeakReference<BaseAncMemberProfileContract.View> view;
    protected BaseAncMemberProfileContract.Interactor interactor;
    private MemberObject memberObject;

    public BaseAncMemberProfilePresenter(BaseAncMemberProfileContract.View view, BaseAncMemberProfileContract.Interactor interactor, MemberObject memberObject) {
        this.view = new WeakReference<>(view);
        this.interactor = interactor;
        this.memberObject = memberObject;
    }

    @Override
    public void fetchProfileData() {
        boolean usesPregnancyRiskProfileLayout = getView().usesPregnancyRiskProfileLayout();
        interactor.refreshProfileView(memberObject, false, usesPregnancyRiskProfileLayout, this);
    }

    @Override
    public void refreshProfileBottom() {
        interactor.refreshProfileInfo(memberObject, this);
    }

    @Override
    public void reloadMemberDetails(String memberID) {
        view.get().showProgressBar(true);
        interactor.reloadMemberDetails(memberID, this);
    }

    @Override
    public void refreshProfileTopSection(MemberObject memberObject) {
        String entityType = memberObject.getBaseEntityId();
        getView().setMemberName(memberObject.getMemberName());
        getView().setMemberGA(String.valueOf(memberObject.getGestationAge()));
        getView().setMemberAddress(memberObject.getAddress());
        getView().setMemberChwMemberId(memberObject.getChwMemberId());
        getView().setProfileImage(memberObject.getBaseEntityId(), entityType);
        getView().setMemberGravida(memberObject.getGravida());
        getView().setPregnancyRiskLabel(memberObject.getPregnancyRiskLevel());
    }

    @Override
    public void setPregnancyRiskTransportProfileDetails(MemberObject memberObject) {
        getView().setPregnancyRiskProfileHeaderActive();
        getView().setPgRiskMemberGA(String.valueOf(memberObject.getGestationAge()));
        getView().setMemberPgRiskGravida(memberObject.getGravida());
        getView().setMemberPgRiskAddress(memberObject.getAddress());
        getView().setMemberPgRiskChwMemberId(memberObject.getChwMemberId());
        getView().setPregnancyRiskLabel(memberObject.getPregnancyRiskLevel());
    }

    @Override
    public void refreshLastVisit(Date lastVisitDate) {
        if (getView() != null) {
            getView().setLastVisit(lastVisitDate);
        }
    }

    @Override
    public void refreshUpComingServicesStatus(String service, AlertStatus status, Date date) {
        if (getView() != null) {
            getView().setUpComingServicesStatus(service, status, date);
            getView().showProgressBar(false);
        }
    }

    @Override
    public void refreshFamilyStatus(AlertStatus status) {
        if (getView() != null) {
            getView().setFamilyStatus(status);
        }
    }

    @Override
    public void onMemberDetailsReloaded(MemberObject memberObject) {
        if (view.get() != null) {
            this.memberObject = memberObject;

            view.get().showProgressBar(false);
            view.get().onMemberDetailsReloaded(memberObject);
        }
    }

    @Override
    public BaseAncMemberProfileContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy(boolean b) {
//        TODO implement onDestroy
    }
}

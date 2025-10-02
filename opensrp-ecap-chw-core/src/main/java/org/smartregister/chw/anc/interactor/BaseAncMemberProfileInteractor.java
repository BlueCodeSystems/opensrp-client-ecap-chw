package org.smartregister.chw.anc.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.anc.contract.BaseAncMemberProfileContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.AppExecutors;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.domain.AlertStatus;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseAncMemberProfileInteractor implements BaseAncMemberProfileContract.Interactor {
    protected AppExecutors appExecutors;
    private Map<String, Date> vaccineList = new LinkedHashMap<>();

    @VisibleForTesting
    BaseAncMemberProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseAncMemberProfileInteractor() {
        this(new AppExecutors());
    }

    public Map<String, Date> getVaccineList() {
        return vaccineList;
    }

    @Override
    public void reloadMemberDetails(String memberID, final BaseAncMemberProfileContract.InteractorCallBack callBack) {
        Runnable runnable = () -> {
            MemberObject memberObject = getMemberClient(memberID);
            appExecutors.mainThread().execute(() -> callBack.onMemberDetailsReloaded(memberObject));
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public MemberObject getMemberClient(String memberID) {
        MemberObject memberObject = new MemberObject();
        memberObject.setBaseEntityId(memberID);
        return memberObject;
    }

    @Override
    public void refreshProfileView(MemberObject memberObject, final boolean isForEdit, boolean usesPregnancyRiskProfileLayout, final BaseAncMemberProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshProfileTopSection(memberObject);
            if (usesPregnancyRiskProfileLayout) {
                MemberObject etMemberObject = getPregnancyRiskDetails(memberObject);
                callback.setPregnancyRiskTransportProfileDetails(etMemberObject);
            }
        });
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void refreshProfileInfo(MemberObject memberObject, final BaseAncMemberProfileContract.InteractorCallBack callBack) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callBack.refreshFamilyStatus(AlertStatus.normal);
            callBack.refreshLastVisit(new Date());
            callBack.refreshUpComingServicesStatus("ANC Visit", AlertStatus.normal, new Date());
        });
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateVisitNotDone(long value, BaseAncMemberProfileContract.InteractorCallBack callback) {
        // Implement
    }

    protected MemberObject getPregnancyRiskDetails(MemberObject memberObject) {
      //  memberObject.setGravida("1");
        memberObject.setPregnancyRiskLevel(Constants.HOME_VISIT.PREGNANCY_RISK_LOW);
        return memberObject;
    }
}

package org.smartregister.chw.core.presenter;

import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.presenter.BaseAncMedicalHistoryPresenter;
import org.smartregister.chw.core.contract.CoreChildMedicalHistoryContract;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.util.List;
import java.util.Map;

public class CoreChildMedicalHistoryPresenter extends BaseAncMedicalHistoryPresenter implements CoreChildMedicalHistoryContract.InteractorCallBack {

    public CoreChildMedicalHistoryPresenter(CoreChildMedicalHistoryContract.Interactor interactor, CoreChildMedicalHistoryContract.View view, String memberID) {
        super(interactor, view, memberID);
    }

    @Override
    public CoreChildMedicalHistoryContract.View getView() {
        if (super.getView() != null) {
            return (CoreChildMedicalHistoryContract.View) super.getView();
        } else {
            return null;
        }
    }

    @Override
    public void onDataFetched(List<Visit> visits, Map<String, List<Vaccine>> vaccines, List<ServiceRecord> serviceRecords) {
        if (getView() != null)
            getView().onDataReceived(visits, vaccines, serviceRecords);
    }
}

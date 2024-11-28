package org.smartregister.chw.core.contract;

import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;

import java.util.List;
import java.util.Map;

public interface CoreChildMedicalHistoryContract extends BaseAncMedicalHistoryContract {

    interface View extends BaseAncMedicalHistoryContract.View {
        void onDataReceived(List<Visit> visits,
                            Map<String, List<Vaccine>> vaccines,
                            List<ServiceRecord> serviceRecords);

        android.view.View renderView(List<Visit> visits,
                                     Map<String, List<Vaccine>> vaccines,
                                     List<ServiceRecord> serviceRecords);
    }

    interface InteractorCallBack extends BaseAncMedicalHistoryContract.InteractorCallBack {

        void onDataFetched(List<Visit> visits, Map<String, List<Vaccine>> vaccines, List<ServiceRecord> serviceRecords);

    }

    interface Interactor extends BaseAncMedicalHistoryContract.Interactor {

        List<Visit> getVisits(String baseEntityID);

        List<ServiceRecord> getServiceRecords(String baseEntityID);

        Map<String, List<Vaccine>> getVaccinesReceivedGroup(String baseEntityID);
    }
}

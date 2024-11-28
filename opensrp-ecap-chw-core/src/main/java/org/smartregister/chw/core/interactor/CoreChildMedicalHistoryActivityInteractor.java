package org.smartregister.chw.core.interactor;

import android.content.Context;

import org.smartregister.chw.anc.contract.BaseAncMedicalHistoryContract;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.interactor.BaseAncMedicalHistoryInteractor;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.CoreChildMedicalHistoryContract;
import org.smartregister.chw.core.dao.VisitDao;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.RecurringServiceUtil;
import org.smartregister.chw.core.utils.VaccineScheduleUtil;
import org.smartregister.chw.core.utils.VisitVaccineUtil;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CoreChildMedicalHistoryActivityInteractor extends BaseAncMedicalHistoryInteractor implements CoreChildMedicalHistoryContract.Interactor {

    @Override
    public void getMemberHistory(final String memberID, final Context context, final BaseAncMedicalHistoryContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {
            List<Visit> visits = getVisits(memberID);
            List<ServiceRecord> serviceRecords = getServiceRecords(memberID);
            Map<String, List<Vaccine>> vaccines = getVaccinesReceivedGroup(memberID);
            appExecutors.mainThread().execute(() -> ((CoreChildMedicalHistoryContract.InteractorCallBack) callBack).onDataFetched(visits, vaccines, serviceRecords));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private Map<String, Visit> getVisitsMap(String memberID) {
        List<Visit> visits = VisitDao.getVisitsByMemberID(memberID);
        Map<String, Visit> map = new LinkedHashMap<>();
        if (visits == null) return map;

        for (Visit v : visits) {
            map.put(v.getVisitId(), v);
        }
        return map;
    }

    @Override
    public List<Visit> getVisits(String baseEntityID) {
        List<Visit> visits = new ArrayList<>();
        Map<String, Visit> visitMap = getVisitsMap(baseEntityID);
        Map<String, List<VisitDetail>> detailList = VisitDao.getMedicalHistory(baseEntityID);
        for (Map.Entry<String, List<VisitDetail>> entry : detailList.entrySet()) {

            Visit v = visitMap.get(entry.getKey());
            if (v == null) {
                v = new Visit();
                v.setVisitId(entry.getKey());
            }
            v.setVisitDetails(VisitUtils.getVisitGroups(entry.getValue()));
            visits.add(v);
        }
        return visits;
    }

    @Override
    public List<ServiceRecord> getServiceRecords(String baseEntityID) {
        return RecurringServiceUtil.getServiceRecords(baseEntityID, true);
    }

    @Override
    public Map<String, List<Vaccine>> getVaccinesReceivedGroup(String baseEntityID) {
        List<VaccineGroup> vaccineGroups =
                VaccineScheduleUtil.getVaccineGroups(CoreChwApplication.getInstance().getApplicationContext(), CoreConstants.SERVICE_GROUPS.CHILD);


        Map<String, Date> receivedDateMap = VisitVaccineUtil.getIssuedVaccines(baseEntityID, true);

        // create a result map
        Map<String, VaccineRepo.Vaccine> vaccineMap = VisitVaccineUtil.getAllVaccines();
        Map<String, List<Vaccine>> result = new LinkedHashMap<>();

        for (VaccineGroup group : vaccineGroups) {
            for (org.smartregister.immunization.domain.jsonmapping.Vaccine vaccine : group.vaccines) {

                String vacName = vaccine.getName().replace(" ", "").toLowerCase();
                Date receivedDate = receivedDateMap.get(vacName);
                if (receivedDate == null) continue;

                List<Vaccine> vaccines = result.get(group.name);
                if (vaccines == null)
                    vaccines = new ArrayList<>();

                VaccineRepo.Vaccine repoObj = vaccineMap.get(vacName);
                if (repoObj != null) {
                    Vaccine domainVaccine = new Vaccine();
                    domainVaccine.setDate(receivedDate);
                    domainVaccine.setName(repoObj.display());
                    vaccines.add(domainVaccine);
                }

                if (vaccines.size() > 0)
                    result.put(group.name, vaccines);
            }
        }

        return result;
    }
}

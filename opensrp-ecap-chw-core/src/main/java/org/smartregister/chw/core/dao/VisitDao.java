package org.smartregister.chw.core.dao;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.core.domain.VisitSummary;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.dao.AbstractDao;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.Nullable;
import timber.log.Timber;

public class VisitDao extends AbstractDao {

    @Nullable
    public static Map<String, VisitSummary> getVisitSummary(String baseEntityID) {
        String sql = "select base_entity_id , visit_type , max(visit_date) visit_date , max(created_at) created_at from visits " +
                " where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE " +
                " group by base_entity_id , visit_type ";

        DataMap<VisitSummary> dataMap = c -> {
            Long visit_date = getCursorLongValue(c, "visit_date");
            Long created_at = getCursorLongValue(c, "created_at");
            return new VisitSummary(
                    getCursorValue(c, "visit_type"),
                    visit_date != null ? new Date(visit_date) : null,
                    created_at != null ? new Date(created_at) : null,
                    getCursorValue(c, "base_entity_id")
            );
        };

        List<VisitSummary> summaries = AbstractDao.readData(sql, dataMap);
        if (summaries == null) return null;

        Map<String, VisitSummary> map = new HashMap<>();
        for (VisitSummary summary : summaries) {
            map.put(summary.getVisitType(), summary);
        }

        return map;
    }

    public static Long getChildDateCreated(String baseEntityID) {
        String sql = "select date_created from ec_child where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE ";

        DataMap<String> dataMap = c -> getCursorValue(c, "date_created");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0) return null;

        try {
            return getDobDateFormat().parse(values.get(0)).getTime();
        } catch (ParseException e) {
            Timber.e(e);
            return null;
        }
    }

    public static void undoChildVisitNotDone(String baseEntityID) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);

        long date = calendar.getTime().getTime();

        String sql = "delete from visits where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE and visit_type = '" +
                CoreConstants.EventType.CHILD_VISIT_NOT_DONE + "' and visit_date >= " + date + " and created_at >=  " + date + "";
        updateDB(sql);
    }

    public static boolean memberHasBirthCert(String baseEntityID) {
        String sql = "select count(*) certificates " +
                "from visit_details d " +
                "inner join visits v on v.visit_id = d.visit_id COLLATE NOCASE " +
                "where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE and v.processed = 1 " +
                "and (visit_key in ('birth_certificate','birth_cert') and (details = 'GIVEN' or human_readable_details = 'Yes'))";

        DataMap<String> dataMap = c -> getCursorValue(c, "certificates");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return false;

        return Integer.valueOf(values.get(0)) > 0;
    }

    public static boolean memberHasVaccineCard(String baseEntityID) {
        String sql = "select count(*) certificates " +
                "from visit_details d " +
                "inner join visits v on v.visit_id = d.visit_id COLLATE NOCASE " +
                "where base_entity_id = '" + baseEntityID + "' COLLATE NOCASE and v.processed = 1 " +
                "and (visit_key in ('vaccine_card', 'child_vaccine_card') and human_readable_details = 'Yes')";

        DataMap<String> dataMap = c -> getCursorValue(c, "certificates");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return false;

        return Integer.valueOf(values.get(0)) > 0;
    }

    public static String getMemberPregnancyRiskLevel(String baseEntityID) {
        String sql = String.format("select details, max(visit_date)  \n" +
                "                from visit_details d  \n" +
                "                inner join visits v on v.visit_id = d.visit_id COLLATE NOCASE  \n" +
                "                where base_entity_id = '%s' COLLATE NOCASE \n" +
                "                and visit_key in ('preg_risk')", baseEntityID);

        DataMap<String> dataMap = c -> getCursorValue(c, "details");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return "Low";

        return values.get(0) == null ? "Low" : values.get(0); // Return a default value of Low
    }

    public static boolean memberHasVisits(String baseEntityID) {
        String sql = "select count(*) total from visits where base_entity_id = '" + baseEntityID + "' AND visit_type IN ('ANC Home Visit', 'PNC Home Visit')";

        DataMap<String> dataMap = c -> getCursorValue(c, "total");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return false;

        return Integer.valueOf(values.get(0)) > 0;
    }

    public static Map<String, Date> getUnprocessedVaccines(String baseEntityID) {
        String sql = "select (case when vd.preprocessed_type = 'VACCINE' then preprocessed_details else vd.visit_key end) visit_key , vd.details " +
                "from visit_details vd " +
                "inner join visits v on v.visit_id = vd.visit_id " +
                "where v.base_entity_id = '" + baseEntityID + "'" +
                "and v.processed = 0 and vd.parent_code = 'vaccine' and vd.details <> 'Vaccine not given'";

        Map<String, Date> res = new HashMap<>();

        DataMap<VisitDetail> dataMap = c -> {
            VisitDetail detail = new VisitDetail();
            detail.setVisitKey(getCursorValue(c, "visit_key"));
            detail.setDetails(getCursorValue(c, "details"));
            return detail;
        };

        List<VisitDetail> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return res;

        for (VisitDetail det : values) {
            try {
                res.put(det.getVisitKey(), getDobDateFormat().parse(det.getDetails()));
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        return res;
    }

    public static List<ServiceRecord> getUnprocessedServiceRecords(String baseEntityID) {
        String sql = "select rt.type , rt.name , vd.details , rt._id  from visit_details vd " +
                "inner join visits v on vd.visit_id = v.visit_id " +
                "inner join recurring_service_types rt on rt.name = vd.preprocessed_details " +
                "where v.base_entity_id = '" + baseEntityID + "' " +
                "and vd.preprocessed_type = 'SERVICE' and details <> 'Dose not given' and v.processed = 0 " +
                "and vd.details like '____-__-__'";

        DataMap<ServiceRecord> dataMap = c -> {
            try {
                ServiceRecord record = new ServiceRecord();
                record.setBaseEntityId(baseEntityID);
                record.setRecurringServiceId(getCursorLongValue(c, "_id"));
                record.setDate(getDobDateFormat().parse(getCursorValue(c, "details")));
                record.setType(getCursorValue(c, "type"));
                record.setName(getCursorValue(c, "name"));
                record.setSyncStatus(RecurringServiceTypeRepository.TYPE_Unsynced);
                return record;
            } catch (ParseException e) {
                Timber.e(e);
            }
            return null;
        };

        List<ServiceRecord> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        List<ServiceRecord> res = new ArrayList<>();
        for (ServiceRecord serviceRecord : values) {
            if (serviceRecord != null)
                res.add(serviceRecord);
        }

        return res;
    }

    public static List<Visit> getPNCVisitsMedicalHistory(String motherBaseEntityId) {
        String sql = "SELECT  visit_id , visit_date , base_entity_id from visits " +
                "WHERE base_entity_id  = '" + motherBaseEntityId + "' COLLATE NOCASE " +
                "OR base_entity_id in (SELECT base_entity_id from ec_child WHERE mother_entity_id  = '" + motherBaseEntityId + "' COLLATE NOCASE )";

        DataMap<Visit> dataMap = c -> {
            Visit visit = new Visit();
            visit.setBaseEntityId(getCursorValue(c, "base_entity_id"));
            visit.setVisitId(getCursorValue(c, "visit_id"));
            visit.setDate(getCursorValueAsDate(c, "visit_date"));
            return visit;
        };

        List<Visit> visits = readData(sql, dataMap);
        if (visits != null) return visits;

        return new ArrayList<>();
    }

    public static List<VisitDetail> getPNCMedicalHistoryVisitDetails(String baseEntityID) {
        String sql = "select v.visit_date,  vd.visit_key , vd.parent_code , vd.preprocessed_type , vd.details, vd.human_readable_details , vd.visit_id , v.base_entity_id " +
                "from visit_details vd " +
                "inner join visits v on vd.visit_id = v.visit_id " +
                "where ( v.base_entity_id  = '" + baseEntityID + "' or v.base_entity_id in (select base_entity_id " +
                "from ec_child c where c.mother_entity_id = '" + baseEntityID + "')) " +
                "order by v.visit_date desc ";

        DataMap<VisitDetail> dataMap = c -> {
            VisitDetail detail = new VisitDetail();
            detail.setVisitId(getCursorValue(c, "visit_id"));
            detail.setBaseEntityId(getCursorValue(c, "base_entity_id"));
            detail.setVisitKey(getCursorValue(c, "visit_key"));
            detail.setParentCode(getCursorValue(c, "parent_code"));
            detail.setPreProcessedType(getCursorValue(c, "preprocessed_type"));
            detail.setDetails(getCursorValue(c, "details"));
            detail.setHumanReadable(getCursorValue(c, "human_readable_details"));
            return detail;
        };

        List<VisitDetail> details = readData(sql, dataMap);
        if (details != null)
            return details;

        return new ArrayList<>();
    }

    public static Map<String, List<VisitDetail>> getMedicalHistory(String baseEntityID) {
        String sql = "select v.visit_date,  vd.visit_key , vd.parent_code , vd.preprocessed_type , vd.details, vd.human_readable_details , vd.visit_id , v.base_entity_id " +
                "from visit_details vd " +
                "inner join visits v on vd.visit_id = v.visit_id " +
                "where ( v.base_entity_id  = '" + baseEntityID + "' or v.base_entity_id in (select base_entity_id " +
                "from ec_child c where c.mother_entity_id = '" + baseEntityID + "')) " +
                "order by v.visit_date desc ";

        DataMap<VisitDetail> dataMap = c -> {
            VisitDetail detail = new VisitDetail();
            detail.setVisitId(getCursorValue(c, "visit_id"));
            detail.setBaseEntityId(getCursorValue(c, "base_entity_id"));
            detail.setVisitKey(getCursorValue(c, "visit_key"));
            detail.setParentCode(getCursorValue(c, "parent_code"));
            detail.setPreProcessedType(getCursorValue(c, "preprocessed_type"));
            detail.setDetails(getCursorValue(c, "details"));
            detail.setHumanReadable(getCursorValue(c, "human_readable_details"));
            return detail;
        };

        HashMap<String, List<VisitDetail>> detailsMap = new LinkedHashMap<>();

        List<VisitDetail> details = readData(sql, dataMap);
        if (details != null) {
            for (VisitDetail d : details) {
                List<VisitDetail> currentDetails = detailsMap.get(d.getVisitId());
                if (currentDetails == null) currentDetails = new ArrayList<>();

                currentDetails.add(d);
                detailsMap.put(d.getVisitId(), currentDetails);
            }
        }

        return detailsMap;
    }

    public static List<Visit> getVisitsByMemberID(String baseEntityID) {
        String sql = "SELECT * from visits WHERE base_entity_id  = '" + baseEntityID + "' COLLATE NOCASE " +
                " OR base_entity_id = (SELECT base_entity_id from ec_child WHERE mother_entity_id  = '" + baseEntityID + "' COLLATE NOCASE )";

        DataMap<Visit> dataMap = c -> {
            Visit visit = new Visit();
            visit.setBaseEntityId(getCursorValue(c, "base_entity_id"));
            visit.setVisitId(getCursorValue(c, "visit_id"));
            visit.setDate(getCursorValueAsDate(c, "visit_date"));
            visit.setBaseEntityId(getCursorValue(c, "base_entity_id"));
            visit.setVisitType(getCursorValue(c, "visit_type"));
            visit.setParentVisitID(getCursorValue(c, "parent_visit_id"));
            visit.setPreProcessedJson(getCursorValue(c, "pre_processed"));
            visit.setBaseEntityId(getCursorValue(c, "base_entity_id"));
            visit.setDate(getCursorValueAsDate(c, "visit_date"));
            visit.setJson(getCursorValue(c, "visit_json"));
            visit.setFormSubmissionId(getCursorValue(c, "form_submission_id"));
            visit.setProcessed(getCursorIntValue(c, "processed", 0) == 1);
            visit.setCreatedAt(getCursorValueAsDate(c, "created_at"));
            visit.setUpdatedAt(getCursorValueAsDate(c, "updated_at"));
            return visit;
        };

        List<Visit> visits = readData(sql, dataMap);
        if (visits != null) return visits;

        return new ArrayList<>();
    }

    /**
     * returns a list of visits from visit tables that can be deleted without having any effect
     * to the home visit (Architecture bug on ANC)
     *
     * @return
     */
    public static List<String> getVisitsToDelete() {
        String sql = "select v.visit_id " +
                "from visits v  " +
                "inner join ( " +
                " select STRFTIME('%Y-%m-%d', datetime(visit_date/1000,'unixepoch')) visit_day, max(visit_date) visit_date " +
                " from visits " +
                " where visit_type in ('ANC Home Visit Not Done','ANC Home Visit Not Done Undo') " +
                " group by STRFTIME('%Y-%m-%d', datetime(visit_date/1000,'unixepoch'))  " +
                " having count(DISTINCT visit_type) > 1 " +
                ") x on x.visit_day = STRFTIME('%Y-%m-%d', datetime(v.visit_date/1000,'unixepoch')) and x.visit_date <> v.visit_date " +
                "where v.visit_type in  ('ANC Home Visit Not Done','ANC Home Visit Not Done Undo')  ";

        DataMap<String> dataMap = c -> getCursorValue(c, "visit_id");

        List<String> details = readData(sql, dataMap);
        if (details != null)
            return details;

        return new ArrayList<>();
    }

    public static Map<String, Map<String, String>> getPncHealthFacilityVisits(String motherBaseEntityId) {

        String sql = "SELECT v.visit_id, vd.visit_key, vd.details " +
                "FROM visits v " +
                "INNER JOIN visit_details vd " +
                "ON vd.visit_id = v.visit_id " +
                "WHERE v.base_entity_id = '" + motherBaseEntityId + "' COLLATE NOCASE " +
                "AND  v.visit_type = PNC Home Visit " +
                "AND vd.visit_key IN ('baby_temp', 'baby_weight','pnc_hf_visit1_date','pnc_hf_visit2_date','pnc_hf_visit3_date','pnc_visit_date') " +
                "order by v.visit_date desc ";

        DataMap<VisitDetail> dataMap = cursor -> {
            VisitDetail detail = new VisitDetail();
            detail.setVisitId(getCursorValue(cursor, "visit_id"));
            detail.setVisitKey(getCursorValue(cursor, "visit_key"));
            detail.setDetails(getCursorValue(cursor, "details"));
            return detail;
        };

        List<VisitDetail> results = readData(sql, dataMap);

        Map<String, Map<String, String>> hfVisits = new HashMap<>();

        Map<String, String> details = new HashMap<>();
        ArrayList<String> visitIds = new ArrayList<>();

        if (results != null && results.size() > 0) {
            for (VisitDetail res : results) {
                visitIds.add(res.getVisitId());

                for (String ids : visitIds) {
                    if (ids.equalsIgnoreCase(res.getVisitId())) {
                        details.put(res.getVisitKey(), res.getDetails());
                        hfVisits.put(res.getVisitId(), details);
                    }
                }
            }
        }
        return hfVisits;
    }

    public static String getMUACValue(String baseEntityID) {
        String sql = String.format("select details, human_readable_details  \n" +
                "                from visit_details d  \n" +
                "                inner join visits v on v.visit_id = d.visit_id COLLATE NOCASE  \n" +
                "                where base_entity_id = '%s' COLLATE NOCASE \n" +
                "                and visit_key == 'muac'", baseEntityID);

        DataMap<VisitDetail> dataMap = c -> {
            VisitDetail detail = new VisitDetail();
            detail.setHumanReadable(getCursorValue(c, "human_readable_details"));
            detail.setDetails(getCursorValue(c, "details"));
            return detail;
        };

        List<VisitDetail> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return "";
        else {
            if (StringUtils.isNotBlank(values.get(0).getHumanReadable()))
                return values.get(0).getHumanReadable();
            else return values.get(0).getDetails();
        }
    }

}

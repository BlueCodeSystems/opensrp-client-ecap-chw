package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.TbScreeningCaregiverModel;

import org.smartregister.dao.AbstractDao;

import java.util.Calendar;
import java.util.List;

public class TbScreeningCaregiverDao extends AbstractDao {

    public static TbScreeningCaregiverModel getByCaregiverId(String caregiverId) {
        String sql = "SELECT * FROM ec_tb_screening_caregiver WHERE household_id = '" + caregiverId + "'";
        List<TbScreeningCaregiverModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.isEmpty()) return null;
        return values.get(0);
    }

    public static List<TbScreeningCaregiverModel> listByCaregiverId(String caregiverId) {
        String sql = "SELECT * FROM ec_tb_screening_caregiver WHERE household_id = '" + caregiverId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static TbScreeningCaregiverModel getByUniqueTbId(String uniqueTbId) {
        String sql = "SELECT * FROM ec_tb_screening_caregiver WHERE unique_tb_id = '" + uniqueTbId + "'";
        List<TbScreeningCaregiverModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.isEmpty()) return null;
        return values.get(0);
    }

    public static int countByCaregiverId(String caregiverId) {
        String sql = "SELECT COUNT(*) AS count FROM ec_tb_screening_caregiver WHERE household_id = '" + caregiverId + "'";
        DataMap<Integer> mapper = c -> Integer.parseInt(getCursorValue(c, "count"));
        List<Integer> out = AbstractDao.readData(sql, mapper);
        return out != null && out.size() > 0 ? out.get(0) : 0;
    }

    public static TbScreeningCaregiverModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_tb_screening_caregiver WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<TbScreeningCaregiverModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.isEmpty()) return null;
        return values.get(0);
    }

    public static boolean existsOnSameDateByUniqueId(String uniqueId, long dayMillis) {
        List<TbScreeningCaregiverModel> values = listByCaregiverId(uniqueId);
        return existsOnSameDate(values, dayMillis);
    }

    private static boolean existsOnSameDate(List<TbScreeningCaregiverModel> records, long dayMillis) {
        if (records == null || records.isEmpty()) return false;
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(dayMillis);
        int y = day.get(Calendar.YEAR);
        int d = day.get(Calendar.DAY_OF_YEAR);
        for (TbScreeningCaregiverModel m : records) {
            if (m == null) continue;
            String ts = m.getLast_interacted_with();
            if (ts == null || ts.trim().isEmpty()) continue;
            try {
                long millis = Long.parseLong(ts);
                Calendar last = Calendar.getInstance();
                last.setTimeInMillis(millis);
                if (y == last.get(Calendar.YEAR) && d == last.get(Calendar.DAY_OF_YEAR)) {
                    return true;
                }
            } catch (Exception ignored) { }
        }
        return false;
    }

    public static DataMap<TbScreeningCaregiverModel> getMap() {
        return c -> {
            TbScreeningCaregiverModel record = new TbScreeningCaregiverModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUnique_tb_id(getCursorValue(c, "unique_tb_id"));
            record.setPrevious_tb_treatment(getCursorValue(c, "previous_tb_treatment"));
            record.setTreatment_year(getCursorValue(c, "treatment_year"));
            record.setTreatment_duration_months(getCursorValue(c, "treatment_duration_months"));
            record.setHistory_close_tb_contact(getCursorValue(c, "history_close_tb_contact"));
            record.setHistory_close_tb_contact_year(getCursorValue(c, "history_close_tb_contact_year"));
            record.setTb_symptoms_child_lt10(getCursorValue(c, "tb_symptoms_child_lt10"));
            record.setTb_symptoms_child_lt10_other(getCursorValue(c, "tb_symptoms_child_lt10_other"));
            record.setTb_symptoms_10plus(getCursorValue(c, "tb_symptoms_10plus"));
            record.setTb_symptoms_10plus_other(getCursorValue(c, "tb_symptoms_10plus_other"));
            record.setSputum_collected(getCursorValue(c, "sputum_collected"));
            record.setReferred_for_tb_evaluation(getCursorValue(c, "referred_for_tb_evaluation"));
            record.setTb_referral_comment(getCursorValue(c, "tb_referral_comment"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setFollowup_date(getCursorValue(c, "followup_date"));
            record.setFacility_referral_completed(getCursorValue(c, "facility_referral_completed"));
            record.setDate_screened_at_facility(getCursorValue(c, "date_screened_at_facility"));
            record.setTb_diagnosis_at_facility(getCursorValue(c, "tb_diagnosis_at_facility"));
            record.setInitiated_tb_treatment(getCursorValue(c, "initiated_tb_treatment"));
            record.setInitiated_tpt(getCursorValue(c, "initiated_tpt"));
            record.setSection_c_comments(getCursorValue(c, "section_c_comments"));
            record.setTreatment_followup_date(getCursorValue(c, "treatment_followup_date"));
            record.setTb_treatment_outcome(getCursorValue(c, "tb_treatment_outcome"));
            record.setTb_treatment_outcome_comment(getCursorValue(c, "tb_treatment_outcome_comment"));
            record.setTb_treatment_outcome_other_comment(getCursorValue(c, "tb_treatment_outcome_other_comment"));
            DaoModelFieldMapper.captureAdditionalFields(c, record);
            return record;
        };
    }
}



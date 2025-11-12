package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.TbScreeningModel;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class TbScreeningDao extends AbstractDao {

    public static TbScreeningModel getByVcaId(String vcaId) {
        String sql = "SELECT * FROM ec_tb_screening WHERE unique_id = '" + vcaId + "'";
        List<TbScreeningModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) return null;
        return values.get(0);
    }

    public static DataMap<TbScreeningModel> getMap() {
        return c -> {
            TbScreeningModel record = new TbScreeningModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setUnique_tb_id(getCursorValue(c, "unique_tb_id"));
            record.setHistory_close_tb_contact(getCursorValue(c, "history_close_tb_contact"));
            record.setHistory_close_tb_contact_year(getCursorValue(c, "history_close_tb_contact_year"));
            record.setTb_symptoms_child_lt10(getCursorValue(c, "tb_symptoms_child_lt10"));
            record.setTb_symptoms_child_lt10_other(getCursorValue(c, "tb_symptoms_child_lt10_other"));
            record.setTb_symptoms_10plus(getCursorValue(c, "tb_symptoms_10plus"));
            record.setTb_symptoms_10plus_other(getCursorValue(c, "tb_symptoms_10plus_other"));
            record.setReferred_for_tb_evaluation(getCursorValue(c, "referred_for_tb_evaluation"));
            record.setTb_referral_comment(getCursorValue(c, "tb_referral_comment"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            // Outcome fields (now in ec_tb_screening)
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
            record.setTb_treatment_outcome_other_comment(getCursorValue(c,"tb_treatment_outcome_other_comment"));
            return record;
        };
    }

    public static List<TbScreeningModel> listByVcaId(String vcaId) {
        String sql = "SELECT * FROM ec_tb_screening WHERE unique_id = '" + vcaId + "' ORDER BY last_interacted_with DESC";
        List<TbScreeningModel> values = AbstractDao.readData(sql, getMap());
        return values;
    }

    public static TbScreeningModel getByUniqueTbId(String uniqueTbId) {
        String sql = "SELECT * FROM ec_tb_screening WHERE unique_tb_id = '" + uniqueTbId + "'";
        List<TbScreeningModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.isEmpty()) return null;
        return values.get(0);
    }

    public static int countByVcaId(String vcaId) {
        String sql = "SELECT COUNT(*) AS count FROM ec_tb_screening WHERE unique_id = '" + vcaId + "'";
        DataMap<Integer> mapper = c -> Integer.parseInt(getCursorValue(c, "count"));
        List<Integer> out = AbstractDao.readData(sql, mapper);
        return out != null && out.size() > 0 ? out.get(0) : 0;
    }
    public static TbScreeningModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_tb_screening WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        java.util.List<TbScreeningModel> values = org.smartregister.dao.AbstractDao.readData(sql, getMap());
        if (values == null || values.isEmpty()) return null;
        return values.get(0);
    }
}


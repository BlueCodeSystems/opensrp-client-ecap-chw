package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.TbScreeningOutcomeModel;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class TbScreeningOutcomeDao extends AbstractDao {

    public static TbScreeningOutcomeModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_tb_screening_outcome WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<TbScreeningOutcomeModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) return null;
        return values.get(0);
    }

    public static DataMap<TbScreeningOutcomeModel> getMap() {
        return c -> {
            TbScreeningOutcomeModel record = new TbScreeningOutcomeModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_tb_id(getCursorValue(c, "unique_tb_id"));
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
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            return record;
        };
    }

    public static List<TbScreeningOutcomeModel> listByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_tb_screening_outcome WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static List<TbScreeningOutcomeModel> listByUniqueTbId(String uniqueTbId) {
        String sql = "SELECT * FROM ec_tb_screening_outcome WHERE unique_tb_id = '" + uniqueTbId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }
}

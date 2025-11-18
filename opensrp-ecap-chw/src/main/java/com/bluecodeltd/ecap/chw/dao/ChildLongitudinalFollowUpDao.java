package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.ChildLongitudinalFollowUpModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChildLongitudinalFollowUpDao extends AbstractDao {

    public static ChildLongitudinalFollowUpModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_child_longitudinal_follow_up WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<ChildLongitudinalFollowUpModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    public static List<ChildLongitudinalFollowUpModel> listByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_child_longitudinal_follow_up WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static DataMap<ChildLongitudinalFollowUpModel> getMap() {
        return c -> {
            ChildLongitudinalFollowUpModel record = new ChildLongitudinalFollowUpModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setInfant_visit_number(getCursorValue(c, "infant_visit_number"));
            record.setInfant_date_of_visit(getCursorValue(c, "infant_date_of_visit"));
            record.setInfant_age(getCursorValue(c, "infant_age"));
            record.setInfant_vaccinations_given(getCursorValue(c, "infant_vaccinations_given"));
            record.setInfant_muac_reading(getCursorValue(c, "infant_muac_reading"));
            record.setInfant_oedema_present(getCursorValue(c, "infant_oedema_present"));
            record.setInfant_breastfeeding_status(getCursorValue(c, "infant_breastfeeding_status"));
            record.setInfant_vitamin_a_given(getCursorValue(c, "infant_vitamin_a_given"));
            record.setInfant_growth_monitoring_done(getCursorValue(c, "infant_growth_monitoring_done"));
            record.setInfant_deworming_given(getCursorValue(c, "infant_deworming_given"));
            record.setInfant_followup_comments(getCursorValue(c, "infant_followup_comments"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setEntity_type(getCursorValue(c, "entity_type"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            return record;
        };
    }
}

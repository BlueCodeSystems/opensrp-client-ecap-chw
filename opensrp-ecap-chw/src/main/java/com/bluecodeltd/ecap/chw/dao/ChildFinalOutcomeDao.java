package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.ChildFinalOutcomeModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChildFinalOutcomeDao extends AbstractDao {

    public static ChildFinalOutcomeModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_child_final_outcome WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<ChildFinalOutcomeModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    public static List<ChildFinalOutcomeModel> listByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_child_final_outcome WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static List<ChildFinalOutcomeModel> listByUniqueId(String uniqueId) {
        String sql = "SELECT * FROM ec_child_final_outcome WHERE unique_id = '" + uniqueId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static DataMap<ChildFinalOutcomeModel> getMap() {
        return c -> {
            ChildFinalOutcomeModel record = new ChildFinalOutcomeModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setInfant_final_outcome_date(getCursorValue(c, "infant_final_outcome_date"));
            record.setInfant_final_hiv_status(getCursorValue(c, "infant_final_hiv_status"));
            record.setInfant_discharged_hiv_negative(getCursorValue(c, "infant_discharged_hiv_negative"));
            record.setInfant_hiv_positive_on_art(getCursorValue(c, "infant_hiv_positive_on_art"));
            record.setInfant_final_outcome(getCursorValue(c, "infant_final_outcome"));
            record.setInfant_exited_ovc_reason(getCursorValue(c, "infant_exited_ovc_reason"));
            record.setInfant_final_outcome_comments(getCursorValue(c, "infant_final_outcome_comments"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setEntity_type(getCursorValue(c, "entity_type"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            return record;
        };
    }
}

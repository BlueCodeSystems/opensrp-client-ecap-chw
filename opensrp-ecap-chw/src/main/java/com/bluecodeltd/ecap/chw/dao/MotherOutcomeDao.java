package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MotherOutcomeModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class MotherOutcomeDao extends AbstractDao {

    public static MotherOutcomeModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_outcome WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<MotherOutcomeModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    public static List<MotherOutcomeModel> listByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_outcome WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static DataMap<MotherOutcomeModel> getMap() {
        return c -> {
            MotherOutcomeModel record = new MotherOutcomeModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setMother_final_outcome_date(getCursorValue(c, "mother_final_outcome_date"));
            record.setMother_lost_to_follow_up(getCursorValue(c, "mother_lost_to_follow_up"));
            record.setMother_dead(getCursorValue(c, "mother_dead"));
            record.setMother_transited_out(getCursorValue(c, "mother_transited_out"));
            record.setMother_transferred_community_pmtct(getCursorValue(c, "mother_transferred_community_pmtct"));
            record.setMother_exited_ovc_program(getCursorValue(c, "mother_exited_ovc_program"));
            record.setMother_exited_ovc_reason(getCursorValue(c, "mother_exited_ovc_reason"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setEntity_type(getCursorValue(c, "entity_type"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            return record;
        };
    }
}

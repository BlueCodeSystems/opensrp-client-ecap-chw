package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MotherAncModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class MotherAncDao extends AbstractDao {

    public static MotherAncModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_anc WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<MotherAncModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    public static List<MotherAncModel> listByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_anc WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static List<MotherAncModel> listByHouseholdId(String householdId) {
        String sql = "SELECT * FROM ec_mother_anc WHERE household_id = '" + householdId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static DataMap<MotherAncModel> getMap() {
        return c -> {
            MotherAncModel record = new MotherAncModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setDate_1st_visit(getCursorValue(c, "date_1st_visit"));
            record.setGestation_age_in_weeks(getCursorValue(c, "gestation_age_in_weeks"));
            record.setHiv_tested(getCursorValue(c, "hiv_tested"));
            record.setDate_tested(getCursorValue(c, "date_tested"));
            record.setResult_of_hiv_test(getCursorValue(c, "result_of_hiv_test"));
            record.setMale_hiv_tested(getCursorValue(c, "male_hiv_tested"));
            record.setMale_result_of_hiv_test(getCursorValue(c, "male_result_of_hiv_test"));
            record.setGravida(getCursorValue(c, "gravida"));
            record.setParity(getCursorValue(c, "parity"));
            record.setLmp_date(getCursorValue(c, "lmp_date"));
            record.setEdd_date(getCursorValue(c, "edd_date"));
            record.setTt_previous_doses(getCursorValue(c, "tt_previous_doses"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setEntity_type(getCursorValue(c, "entity_type"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            return record;
        };
    }
}

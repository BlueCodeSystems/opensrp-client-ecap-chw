package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MotherPostnatalCareModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class MotherPostnatalCareDao extends AbstractDao {

    public static MotherPostnatalCareModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_postnatal_care WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<MotherPostnatalCareModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    public static List<MotherPostnatalCareModel> listByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_postnatal_care WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static List<MotherPostnatalCareModel> listByHouseholdId(String householdId) {
        String sql = "SELECT * FROM ec_mother_postnatal_care WHERE household_id = '" + householdId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static DataMap<MotherPostnatalCareModel> getMap() {
        return c -> {
            MotherPostnatalCareModel record = new MotherPostnatalCareModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setPnc_visit_type(getCursorValue(c, "pnc_visit_type"));
            record.setPnc_type_of_feeding(getCursorValue(c, "pnc_type_of_feeding"));
            record.setPnc_hiv_test_done(getCursorValue(c, "pnc_hiv_test_done"));
            record.setPnc_on_prep(getCursorValue(c, "pnc_on_prep"));
            record.setPnc_fp_counselling(getCursorValue(c, "pnc_fp_counselling"));
            record.setPnc_cervical_cancer_screening(getCursorValue(c, "pnc_cervical_cancer_screening"));
            record.setPnc_sti_screening(getCursorValue(c, "pnc_sti_screening"));
            record.setPnc_comments(getCursorValue(c, "pnc_comments"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setEntity_type(getCursorValue(c, "entity_type"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            DaoModelFieldMapper.captureAdditionalFields(c, record);
            return record;
        };
    }
}



package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.ChildPostnatalCareModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChildPostnatalCareDao extends AbstractDao {

    public static ChildPostnatalCareModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_child_postnatal_care WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<ChildPostnatalCareModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    public static List<ChildPostnatalCareModel> listByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_child_postnatal_care WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static List<ChildPostnatalCareModel> listByUniqueId(String uniqueId) {
        String sql = "SELECT * FROM ec_child_postnatal_care WHERE unique_id = '" + uniqueId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static DataMap<ChildPostnatalCareModel> getMap() {
        return c -> {
            ChildPostnatalCareModel record = new ChildPostnatalCareModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setPnc_infant_visit_type(getCursorValue(c, "pnc_infant_visit_type"));
            record.setPnc_infant_feeding_type(getCursorValue(c, "pnc_infant_feeding_type"));
            record.setPnc_infant_hiv_test_done(getCursorValue(c, "pnc_infant_hiv_test_done"));
            record.setPnc_infant_on_art_if_positive(getCursorValue(c, "pnc_infant_on_art_if_positive"));
            record.setPnc_infant_ctx_given(getCursorValue(c, "pnc_infant_ctx_given"));
            record.setPnc_infant_immunization_up_to_date(getCursorValue(c, "pnc_infant_immunization_up_to_date"));
            record.setPnc_infant_growth_monitoring_done(getCursorValue(c, "pnc_infant_growth_monitoring_done"));
            record.setPnc_infant_growth_normal(getCursorValue(c, "pnc_infant_growth_normal"));
            record.setPnc_infant_referred_for_complications(getCursorValue(c, "pnc_infant_referred_for_complications"));
            record.setPnc_infant_comments(getCursorValue(c, "pnc_infant_comments"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setEntity_type(getCursorValue(c, "entity_type"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            return record;
        };
    }
}

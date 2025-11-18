package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MotherDeliveryModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class MotherDeliveryDao extends AbstractDao {

    public static MotherDeliveryModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_delivery WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<MotherDeliveryModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    public static List<MotherDeliveryModel> listByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_delivery WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static DataMap<MotherDeliveryModel> getMap() {
        return c -> {
            MotherDeliveryModel record = new MotherDeliveryModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setDate_of_delivery(getCursorValue(c, "date_of_delivery"));
            record.setPlace_of_delivery(getCursorValue(c, "place_of_delivery"));
            record.setHiv_status_at_delivery(getCursorValue(c, "hiv_status_at_delivery"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setEntity_type(getCursorValue(c, "entity_type"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            return record;
        };
    }
}

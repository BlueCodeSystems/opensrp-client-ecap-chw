package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.WeServiceCaregiverModel;
import com.bluecodeltd.ecap.chw.model.WeServiceVcaModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class WeServiceVcaDoa extends AbstractDao {
    public static WeServiceVcaModel getWeServiceVca (String householdID) {

        String sql = "SELECT * FROM ec_caregiver_household_assessment WHERE household_id = '" + householdID + "' ";

        List<WeServiceVcaModel> values = AbstractDao.readData(sql, getWeServiceVcaMap());

        if (values.size() == 0) {
            return null;
        }

        return values.get(0);
    }

    public static DataMap<WeServiceVcaModel> getWeServiceVcaMap() {
        return c -> {

            WeServiceVcaModel record = new WeServiceVcaModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setVCA_id(getCursorValue(c, "vca_id"));
            record.setDate_joined_we(getCursorValue(c, "date_joined_we"));
            return record;
        };
    }

}

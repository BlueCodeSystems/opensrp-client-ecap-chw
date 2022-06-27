package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.WeServiceCaregiverModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class WeServiceCaregiverDoa extends AbstractDao {
    public static WeServiceCaregiverModel getWeServiceCaregiver (String householdID) {

        String sql = "SELECT * FROM ec_caregiver_household_assessment WHERE household_id = '" + householdID + "' ";

        List<WeServiceCaregiverModel> values = AbstractDao.readData(sql, getWeServiceCaregiverMap());

        if (values.size() == 0) {
            return null;
        }


        //return values.get(0);
        return values.get(0);
    }

    public static DataMap<WeServiceCaregiverModel> getWeServiceCaregiverMap() {
        return c -> {

            WeServiceCaregiverModel record = new WeServiceCaregiverModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setCaregiver_id(getCursorValue(c, "caregiver_id"));
            record.setDate_joined_we(getCursorValue(c, "date_joined_we"));


            return record;
        };
    }

}

package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.newCaregiverModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class newCaregiverDao extends AbstractDao {
    public static newCaregiverModel getNewCaregiverById(String householdID) {

        String sql = "SELECT *" +
                "FROM ec_household " +
                "WHERE household_id = '" + householdID + "' " +
                "AND (status IS NULL OR status <> '1')";

        List<newCaregiverModel> values = AbstractDao.readData(sql, newCaregiverModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<newCaregiverModel> newCaregiverModelMap() {
        return c -> {

            newCaregiverModel record = new newCaregiverModel();
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setNew_caregiver_name(getCursorValue(c, "new_caregiver_name"));
            record.setNew_caregiver_nrc(getCursorValue(c, "new_caregiver_nrc"));
            record.setNew_caregiver_birth_date(getCursorValue(c, "new_caregiver_birth_date"));
            record.setNew_caregiver_sex(getCursorValue(c, "new_caregiver_sex"));
            record.setNew_relation(getCursorValue(c, "new_relation"));
            record.setNew_caregiver_hiv_status(getCursorValue(c, "new_caregiver_hiv_status"));
            record.setNew_caregiver_phone(getCursorValue(c, "new_caregiver_phone"));
            record.setHousehold_case_status(getCursorValue(c, "household_case_status"));


            return record;
        };
    }
}

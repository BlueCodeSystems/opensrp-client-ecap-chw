package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.Caregiver;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class CaregiverHouseholdVistationDao extends AbstractDao {

    public static Caregiver getCaregiver (String householdID) {

        String sql = "SELECT caregiver_name, caregiver_nrc, caregiver_sex, caregiver_birth_date, caregiver_phone, caregiver_hiv_status, caregiver_art_number, relation FROM ec_household WHERE ec_household.household_id = '" + householdID + "' ";

        List<Caregiver> values = AbstractDao.readData(sql, getCaregiverMap());

        return values.get(0);

    }

    public static DataMap<Caregiver> getCaregiverMap() {
        return c -> {

            Caregiver record = new Caregiver();

            record.setCaregiver_name(getCursorValue(c, "caregiver_name"));
            record.setCaregiver_nrc(getCursorValue(c, "caregiver_nrc"));
            record.setCaregiver_sex(getCursorValue(c, "caregiver_sex"));
            record.setCaregiver_birth_date(getCursorValue(c, "caregiver_birth_date"));
            record.setPhysical_address(getCursorValue(c, "physical_address"));
            record.setCaregiver_phone(getCursorValue(c, "caregiver_phone"));
            record.setCaregiver_hiv_status(getCursorValue(c, "caregiver_hiv_status"));
            record.setActive_on_treatment(getCursorValue(c, "active_on_treatment"));
            record.setCaregiver_art_number(getCursorValue(c, "caregiver_art_number"));
            record.setRelation(getCursorValue(c, "relation"));
            return record;
        };
    }
}

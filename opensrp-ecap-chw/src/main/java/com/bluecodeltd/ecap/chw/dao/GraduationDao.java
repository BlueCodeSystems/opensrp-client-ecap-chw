package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.GraduationModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class GraduationDao extends AbstractDao {

    public static GraduationModel getGraduation (String hhID) {

        String sql = "SELECT * FROM ec_graduation WHERE household_id = '" + hhID + "'";

        List<GraduationModel> values = AbstractDao.readData(sql, getGraduationModelMap());

        if (values.size() == 0) {
            return null;
        }

        return values.get(0);
    }

    public static DataMap<GraduationModel> getGraduationModelMap() {
        return c -> {

            GraduationModel record = new GraduationModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setDate_assessment(getCursorValue(c, "date_assessment"));
            record.setVirally_suppressed(getCursorValue(c, "virally_suppressed"));
            record.setSuppressed_caregiver(getCursorValue(c,"suppressed_caregiver"));
            record.setPrevention(getCursorValue(c, "prevention"));
            record.setUndernourished(getCursorValue(c,"undernourished"));
            record.setSchool_fees(getCursorValue(c, "school_fees"));
            record.setMedical_costs(getCursorValue(c, "medical_costs"));
            record.setRecord_abuse(getCursorValue(c, "record_abuse"));
            record.setCaregiver_beaten(getCursorValue(c, "caregiver_beaten"));
            record.setChild_beaten(getCursorValue(c, "child_beaten"));
            record.setAware_sexual(getCursorValue(c, "aware_sexual"));
            record.setAgainst_will(getCursorValue(c, "against_will"));
            record.setStable_guardian(getCursorValue(c, "stable_guardian"));
            record.setChildren_in_school(getCursorValue(c, "children_in_school"));
            record.setIn_school(getCursorValue(c, "in_school"));
            record.setYear_school(getCursorValue(c, "year_school"));
            record.setRepeat_school(getCursorValue(c, "repeat_school"));
            record.setAdditional_information(getCursorValue(c, "additional_information"));

            return record;
        };
    }

}

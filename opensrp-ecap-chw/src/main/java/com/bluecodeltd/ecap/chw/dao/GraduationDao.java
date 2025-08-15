package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.GraduationModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
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
    public static GraduationModel getGraduationStatus(String hhID) {
        String sql = "SELECT graduation_status FROM ec_graduation " +
                "WHERE household_id = '" + hhID + "' " +
                "ORDER BY ROWID DESC LIMIT 1";

        List<GraduationModel> values = AbstractDao.readData(sql, getGraduationModelMap());
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }
    public static int countVisits(String householdID){

        String sql = "SELECT COUNT(*) AS visitCount FROM ec_graduation WHERE household_id = '" + householdID + "'";

        DataMap<String> dataMap = c -> getCursorValue(c, "visitCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return Integer.parseInt(values.get(0));

    }
    public static List<GraduationModel> getAssessment(String householdID) {

        String sql = "SELECT *,strftime('%Y-%m-%d', substr(date_assessment,7,4) || '-' || substr(date_assessment,4,2) || '-' || substr(date_assessment,1,2)) as sortable_date FROM ec_graduation WHERE household_id = '" + householdID + "'  ORDER BY sortable_date DESC";

        List<GraduationModel> values = AbstractDao.readData(sql, getGraduationModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static DataMap<GraduationModel> getGraduationModelMap() {
        return c -> {

            GraduationModel record = new GraduationModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setEnrollment_date(getCursorValue(c, "enrollment_date"));
            record.setAsmt(getCursorValue(c, "asmt"));
            record.setCaregiver_name(getCursorValue(c, "caregiver_name"));
            record.setCaregiver_sex(getCursorValue(c, "caregiver_sex"));
            record.setCaregiver_birth_date(getCursorValue(c, "caregiver_birth_date"));
            record.setDate_assessment(getCursorValue(c, "date_assessment"));
            record.setPrevious_asmt_date(getCursorValue(c, "previous_asmt_date"));
            record.setHiv_status_enrolled(getCursorValue(c, "hiv_status_enrolled"));
            record.setCaregiver_hiv_status_enrolled(getCursorValue(c, "caregiver_hiv_status_enrolled"));
            record.setVirally_suppressed(getCursorValue(c, "virally_suppressed"));
            record.setSuppressed_caregiver(getCursorValue(c, "suppressed_caregiver"));
            record.setPrevention(getCursorValue(c, "prevention"));
            record.setUndernourished(getCursorValue(c, "undernourished"));
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
            record.setGraduation_status(getCursorValue(c, "graduation_status"));
            record.setTouching_in_sexual(getCursorValue(c,"touching_in_sexual"));
            record.setDelete_status(getCursorValue(c, "delete_status"));


            return record;
        };
    }

}

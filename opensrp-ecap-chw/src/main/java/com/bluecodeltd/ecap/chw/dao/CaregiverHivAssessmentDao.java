package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.CaregiverHivAssessmentModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class CaregiverHivAssessmentDao extends AbstractDao {
    public static CaregiverHivAssessmentModel getCaregiverHivAssessment (String householdID) {

        String sql = "SELECT * FROM ec_caregiver_hiv_assessment WHERE household_id = '" + householdID + "' ";

        List<CaregiverHivAssessmentModel> values = AbstractDao.readData(sql, getCaregiverHivAssessmentModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<CaregiverHivAssessmentModel> getCaregiverHivAssessmentModelMap() {
        return c -> {

            CaregiverHivAssessmentModel record = new CaregiverHivAssessmentModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setInformed_consent(getCursorValue(c, "informed_consent"));
            record.setHiv_test(getCursorValue(c, "hiv_test"));
            record.setHiv_status(getCursorValue(c, "hiv_status"));
            record.setOn_art(getCursorValue(c, "on_art"));
            record.setStart_date(getCursorValue(c, "start_date"));
            record.setHealth_facility(getCursorValue(c, "health_facility"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setSymptoms(getCursorValue(c, "symptoms"));
            record.setPrivate_parts(getCursorValue(c, "private_parts"));
            record.setExposed_to_HIV(getCursorValue(c, "exposed_to_HIV"));
            record.setUnprotected_sex(getCursorValue(c, "unprotected_sex"));
            record.setPregnant_breastfeeding(getCursorValue(c, "pregnant_breastfeeding"));
            record.setHiv_tb_sti(getCursorValue(c, "hiv_tb_sti"));
            record.setHiv_test_result(getCursorValue(c, "hiv_test_result"));
            record.setDate_of_hiv_test(getCursorValue(c, "date_of_hiv_test"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setPhone(getCursorValue(c, "phone"));

            return record;
        };
    }

}

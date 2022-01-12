package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentAbove15Model;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.ReferralModel;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HivAssessmentAbove15Dao extends AbstractDao {
    public static HivRiskAssessmentAbove15Model getHivAssessmentAbove15 (String vcaID) {

        String sql = "SELECT * FROM ec_hiv_assessment_above_15 WHERE unique_id = '" + vcaID + "' ";

        List<HivRiskAssessmentAbove15Model> values = AbstractDao.readData(sql, getHivRiskAssessmentAbove15ModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<HivRiskAssessmentAbove15Model> getHivRiskAssessmentAbove15ModelMap() {
        return c -> {

            HivRiskAssessmentAbove15Model record = new HivRiskAssessmentAbove15Model();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
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

            return record;
        };
    }

}

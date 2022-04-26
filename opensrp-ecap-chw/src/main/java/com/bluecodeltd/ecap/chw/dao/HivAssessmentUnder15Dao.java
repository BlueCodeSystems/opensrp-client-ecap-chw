package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.HivRiskAssessmentUnder15Model;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HivAssessmentUnder15Dao extends AbstractDao {
    public static HivRiskAssessmentUnder15Model getHivAssessmentUnder15 (String vcaID) {

        String sql = "SELECT * FROM ec_hiv_assessment_below_15 WHERE unique_id = '" + vcaID + "' ";

        List<HivRiskAssessmentUnder15Model> values = AbstractDao.readData(sql, getHivRiskAssessmentUnder15ModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<HivRiskAssessmentUnder15Model> getHivRiskAssessmentUnder15ModelMap() {
        return c -> {

            HivRiskAssessmentUnder15Model record = new HivRiskAssessmentUnder15Model();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setInformed_consent(getCursorValue(c, "informed_consent"));
            record.setHiv_test(getCursorValue(c, "hiv_test"));
            record.setOn_art(getCursorValue(c, "on_art"));
            record.setStart_date(getCursorValue(c, "start_date"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setBiological_mother(getCursorValue(c, "biological_mother"));
            record.setDeceased_parents(getCursorValue(c, "deceased_parents"));
            record.setTb_symptoms(getCursorValue(c, "tb_symptoms"));
            record.setChild_been_sick(getCursorValue(c, "child_been_sick"));
            record.setFrequent_rashes(getCursorValue(c, "frequent_rashes"));
            record.setChild_had_pus(getCursorValue(c, "child_had_pus"));
            record.setHiv_risk(getCursorValue(c, "hiv_risk"));
            record.setHiv_tb(getCursorValue(c, "hiv_tb"));
            record.setHiv_test_result(getCursorValue(c, "hiv_test_result"));
            record.setDate_of_hiv_test(getCursorValue(c, "date_of_hiv_test"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setPhone(getCursorValue(c, "phone"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));

            return record;
        };
    }

}

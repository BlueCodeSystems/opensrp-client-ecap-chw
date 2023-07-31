package com.bluecodeltd.ecap.chw.dao;


import com.bluecodeltd.ecap.chw.model.HivTestingServiceModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HivTestingServiceDao extends AbstractDao{
    public static HivTestingServiceModel getHivTestingServiceModel (String vcaID) {

        String sql = "SELECT * FROM ec_hiv_testing_service WHERE unique_id = '" + vcaID + "' ";

        List<HivTestingServiceModel> values = AbstractDao.readData(sql, getHivTestingServiceModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static AbstractDao.DataMap<HivTestingServiceModel> getHivTestingServiceModelMap() {
        return c -> {

            HivTestingServiceModel record = new HivTestingServiceModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setRelational_id(getCursorValue(c, "relational_id"));
            record.setImplementing_partner(getCursorValue(c, "implementing_partner"));
            record.setHealth_facility(getCursorValue(c, "health_facility"));
            record.setProvince(getCursorValue(c, "province"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setClient_number(getCursorValue(c, "client_number"));
            record.setTesting_modality(getCursorValue(c, "testing_modality"));
            record.setCase_name(getCursorValue(c, "case_name"));
            record.setCaseName_on_art(getCursorValue(c, "caseName_on_art"));
            record.setArtNumber_for_caseName(getCursorValue(c, "artNumber_for_caseName"));
            record.setCase_snn_gender(getCursorValue(c, "case_snn_gender"));
            record.setCase_snn_birthdate(getCursorValue(c, "case_snn_birthdate"));
            record.setCase_snn_entry_point(getCursorValue(c, "case_snn_entry_point"));
            record.setCase_snn_house_number(getCursorValue(c, "case_snn_house_number"));
            record.setIndividual_tested(getCursorValue(c, "individual_tested"));
            record.setEcap_id(getCursorValue(c, "ecap_id"));
            record.setSub_population(getCursorValue(c, "sub_population"));
            record.setIndividual_index_SNS_birthdate(getCursorValue(c, "individual_index_SNS_birthdate"));
            record.setIndex_sns_relationship(getCursorValue(c, "index_sns_relationship"));
            record.setCurrent_address_landmarks(getCursorValue(c, "current_address_landmarks"));
            record.setHiv_status(getCursorValue(c, "hiv_status"));
            record.setDate_tested(getCursorValue(c, "date_tested"));
            record.setHiv_result(getCursorValue(c, "hiv_result"));
            record.setTest_done_hf(getCursorValue(c, "test_done_hf"));
            record.setHiv_recent_test(getCursorValue(c, "hiv_recent_test"));
            record.setArt_date(getCursorValue(c, "art_date"));
            record.setArt_date_initiated(getCursorValue(c, "art_date_initiated"));
            record.setComment(getCursorValue(c, "comment"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setChecked_by(getCursorValue(c, "checked_by"));



            return record;
        };
    }
}

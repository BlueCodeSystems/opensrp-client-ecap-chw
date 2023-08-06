package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.HIVTestingServiceModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HIVTestingServiceDao extends AbstractDao {

    public static HIVTestingServiceModel getHivServiceClient (String clientID) {

        String sql = "SELECT * FROM ec_hiv_testing_service WHERE client_number = '" + clientID + "' ";

        List<HIVTestingServiceModel> values = AbstractDao.readData(sql, getHIVTestingServiceModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<HIVTestingServiceModel> getHIVTestingServiceModelMap() {
        return c -> {

            HIVTestingServiceModel record = new HIVTestingServiceModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setRelational_id(getCursorValue(c, "relational_id"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setPhone(getCursorValue(c, "phone"));
            record.setImplementing_partner(getCursorValue(c, "implementing_partner"));
            record.setHealth_facility(getCursorValue(c, "health_facility"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setProvince(getCursorValue(c, "province"));
            record.setClient_number(getCursorValue(c, "client_number"));
            record.setTesting_modality(getCursorValue(c, "testing_modality"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setMiddle_name(getCursorValue(c, "middle_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setGender(getCursorValue(c, "gender"));
            record.setBirthdate(getCursorValue(c, "birthdate"));
            record.setEntry_point(getCursorValue(c, "entry_point"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setDate_edited(getCursorValue(c,"date_edited"));
            record.setDate_client_created(getCursorValue(c, "date_client_created"));


            return record;
        };
    }

}

package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.HTSlinksModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class HTSLinksDao extends AbstractDao {
    public static List<HTSlinksModel> getHTSLinks (String clientID) {

        String sql = "SELECT * FROM ec_hiv_testing_links WHERE client_number = '" + clientID + "' ";

        List<HTSlinksModel> values = AbstractDao.readData(sql, getHTSlinksModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }

    public static DataMap<HTSlinksModel> getHTSlinksModelMap() {
        return c -> {

            HTSlinksModel record = new HTSlinksModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setRelational_id(getCursorValue(c, "relational_id"));
            record.setClient_number(getCursorValue(c, "client_number"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setMiddle_name(getCursorValue(c, "middle_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setGender(getCursorValue(c, "gender"));
            record.setBirthdate(getCursorValue(c, "birthdate"));
            record.setEntry_point(getCursorValue(c, "entry_point"));
            record.setHouse_number(getCursorValue(c, "house_number"));
            record.setIndividual_tested(getCursorValue(c, "individual_tested"));
            record.setEcap_id(getCursorValue(c, "ecap_id"));
            record.setSub_population(getCursorValue(c, "sub_population"));
            record.setRelationship(getCursorValue(c, "relationship"));
            record.setAddress(getCursorValue(c, "address"));
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

package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.HTSlinksModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class HTSLinksDao extends AbstractDao {
    public static int htsCount(String clientID){

        String sql = "SELECT COUNT(*) AS htsCount FROM ec_hiv_testing_links WHERE client_number = '" + clientID + "' AND (delete_status IS NULL OR delete_status <> '1') ";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "htsCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return Integer.parseInt(values.get(0));

    }
    public static List<HTSlinksModel> getHTSLinks (String clientID) {


        String sql = "SELECT *,strftime('%Y-%m-%d', substr(date_linked,7,4) || '-' || substr(date_linked,4,2) || '-' || substr(date_linked,1,2)) as sortable_date FROM ec_hiv_testing_links WHERE client_number = '" + clientID + "'" +
                " AND (delete_status IS NULL OR delete_status <> '1') ORDER BY sortable_date DESC ";

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
            record.setDate_linked(getCursorValue(c, "date_linked"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setMiddle_name(getCursorValue(c, "middle_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setEcap_id(getCursorValue(c, "ecap_id"));
            record.setSub_population(getCursorValue(c, "sub_population"));
            record.setBirthdate(getCursorValue(c, "birthdate"));
            record.setRelationship(getCursorValue(c, "relationship"));
            record.setOther_relationship(getCursorValue(c, "other_relationship"));
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
            record.setDelete_status(getCursorValue(c, "delete_status"));


            return record;
        };
    }
}

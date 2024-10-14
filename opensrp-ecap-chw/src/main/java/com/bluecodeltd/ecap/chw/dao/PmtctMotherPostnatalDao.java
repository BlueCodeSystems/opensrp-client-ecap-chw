package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.PmtctMotherPostnatalModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class PmtctMotherPostnatalDao extends AbstractDao {
    public static List<PmtctMotherPostnatalModel> getPostnatalMother(String pmtctID) {

        String sql = "SELECT *,strftime('%Y-%m-%d', substr(date_of_st_post_natal_care,7,4) || '-' || substr(date_of_st_post_natal_care,4,2) || '-' || substr(date_of_st_post_natal_care,1,2)) as sortable_date  FROM ec_pmtct_mother_postnatal WHERE pmtct_id = '" + pmtctID + "'  ORDER BY sortable_date DESC";

        List<PmtctMotherPostnatalModel> values = AbstractDao.readData(sql, getPmtctMotherPostnatalModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static DataMap<PmtctMotherPostnatalModel> getPmtctMotherPostnatalModelMap() {
        return c -> {

            PmtctMotherPostnatalModel record = new PmtctMotherPostnatalModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setRelational_id(getCursorValue(c, "relational_id"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setDate_of_st_post_natal_care(getCursorValue(c, "date_of_st_post_natal_care"));
            record.setMother_tested_for_hiv(getCursorValue(c, "mother_tested_for_hiv"));
            record.setPostnatal_care_visit(getCursorValue(c, "postnatal_care_visit"));
            record.setArt_initiated(getCursorValue(c, "art_initiated"));
            record.setArt_adherence_counselling_support(getCursorValue(c, "art_adherence_counselling_support"));
            record.setVl_result(getCursorValue(c, "vl_result"));
            record.setFamily_planning_counselling(getCursorValue(c, "family_planning_counselling"));
            record.setNumber_of_condoms_distributed(getCursorValue(c, "number_of_condoms_distributed"));
            record.setComments_at_postnatal_care_visit(getCursorValue(c, "comments_at_postnatal_care_visit"));
            record.setDelete_status(getCursorValue(c, "delete_status"));

            return record;
        };
    }
}

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
            record.setHiv_test_result_r_nr_at_6_weeks(getCursorValue(c, "hiv_test_result_r_nr_at_6_weeks"));
            record.setArt_initiated_at_6_weeks(getCursorValue(c, "art_initiated_at_6_weeks"));
            record.setArt_adherence_counselling_support_at_6_weeks(getCursorValue(c, "art_adherence_counselling_support_at_6_weeks"));
            record.setFamily_planning_counselling_at_6_weeks(getCursorValue(c, "family_planning_counselling_at_6_weeks"));
            record.setComments_at_postnatal_care_visit_6_weeks(getCursorValue(c, "comments_at_postnatal_care_visit_6_weeks"));
            record.setHiv_test_result_r_nr_at_6_months(getCursorValue(c, "hiv_test_result_r_nr_at_6_months"));
            record.setArt_initiated_at_6_months(getCursorValue(c, "art_initiated_at_6_months"));
            record.setFamily_planning_counselling_at_6_months(getCursorValue(c, "family_planning_counselling_at_6_months"));
            record.setNumber_of_condoms_distributed_at_6_months(getCursorValue(c, "number_of_condoms_distributed_at_6_months"));
            record.setComments_at_postnatal_care_visit_6_months(getCursorValue(c, "comments_at_postnatal_care_visit_6_months"));
            record.setHiv_test_result_r_nr_at_9_months(getCursorValue(c, "hiv_test_result_r_nr_at_9_months"));
            record.setArt_initiated_at_9_months(getCursorValue(c, "art_initiated_at_9_months"));
            record.setArt_adherence_counselling_support_at_9_months(getCursorValue(c, "art_adherence_counselling_support_at_9_months"));
            record.setFamily_planning_counselling_at_9_months(getCursorValue(c, "family_planning_counselling_at_9_months"));
            record.setNumber_of_condoms_distributed_at_9_months(getCursorValue(c, "number_of_condoms_distributed_at_9_months"));
            record.setComments_at_postnatal_care_visit_9(getCursorValue(c, "comments_at_postnatal_care_visit_9"));
            record.setHiv_test_result_r_nr_at_12_months(getCursorValue(c, "hiv_test_result_r_nr_at_12_months"));
            record.setArt_initiated_at_12_months(getCursorValue(c, "art_initiated_at_12_months"));
            record.setArt_adherence_counselling_support_at_12_months(getCursorValue(c, "art_adherence_counselling_support_at_12_months"));
            record.setFamily_planning_counselling_at_12_months(getCursorValue(c, "family_planning_counselling_at_12_months"));
            record.setNumber_of_condoms_distributed_at_12_months(getCursorValue(c, "number_of_condoms_distributed_at_12_months"));
            record.setComments_at_postnatal_care_visit_12(getCursorValue(c, "comments_at_postnatal_care_visit_12"));
            record.setHiv_test_result_r_nr_at_18_months(getCursorValue(c, "hiv_test_result_r_nr_at_18_months"));
            record.setArt_initiated_at_18_months(getCursorValue(c, "art_initiated_at_18_months"));
            record.setArt_adherence_counselling_support_at_18_months(getCursorValue(c, "art_adherence_counselling_support_at_18_months"));
            record.setFamily_planning_counselling_at_18_months(getCursorValue(c, "family_planning_counselling_at_18_months"));
            record.setNumber_of_condoms_distributed_at_18_months(getCursorValue(c, "number_of_condoms_distributed_at_18_months"));
            record.setComments_at_postnatal_care_visit_18(getCursorValue(c, "comments_at_postnatal_care_visit_18"));
            record.setDelete_status(getCursorValue(c, "delete_status"));

            return record;
        };
    }
}

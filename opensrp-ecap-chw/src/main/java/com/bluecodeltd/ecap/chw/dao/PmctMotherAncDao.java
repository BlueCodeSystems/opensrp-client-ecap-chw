package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.PmctMotherAncModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class PmctMotherAncDao extends AbstractDao {

    public static PmctMotherAncModel getPMCTMotherAnc(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_mother_anc WHERE pmtct_id = '" + pmtctID + "' ";

        List<PmctMotherAncModel> values = AbstractDao.readData(sql, getPmctMotherAncModelMap());

        if (values.size() == 0) {
            return null;
        }

        return values.get(0);
    }

    public static List<PmctMotherAncModel> getPostnatalAncMother(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_mother_anc WHERE pmtct_id = '" + pmtctID + "' ";

        List<PmctMotherAncModel> values = AbstractDao.readData(sql, getPmctMotherAncModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static DataMap<PmctMotherAncModel> getPmctMotherAncModelMap() {
        return c -> {

            PmctMotherAncModel record = new PmctMotherAncModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setRelational_id(getCursorValue(c, "relational_id"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setDate_of_st_contact(getCursorValue(c, "date_of_st_contact"));
            record.setGestation_age_in_weeks(getCursorValue(c, "gestation_age_in_weeks"));
            record.setHiv_tested(getCursorValue(c, "hiv_tested"));
            record.setDate_tested(getCursorValue(c, "date_tested"));
            record.setResult_of_hiv_test(getCursorValue(c, "result_of_hiv_test"));
            record.setRecency_test_result_if_applicable(getCursorValue(c, "recency_test_result_if_applicable"));
            record.setVl_result_at_trimester_1(getCursorValue(c, "vl_result_at_trimester_1"));
            record.setVl_result_at_trimester_2(getCursorValue(c, "vl_result_at_trimester_2"));
            record.setVl_result_at_trimester_3(getCursorValue(c, "vl_result_at_trimester_3"));
            record.setMale_partner_tested(getCursorValue(c, "male_partner_tested"));
            record.setDate_male_partner_tested(getCursorValue(c, "date_male_partner_tested"));
            record.setResult_r_nr(getCursorValue(c, "result_r_nr"));
            record.setTreatment_initiated(getCursorValue(c, "treatment_initiated"));
            record.setDate_initiated_on_treatment(getCursorValue(c, "date_initiated_on_treatment"));
            record.setOn_art_st_anc(getCursorValue(c, "on_art_st_anc"));
            record.setTb_screening(getCursorValue(c, "tb_screening"));
            record.setSyphilis_testing(getCursorValue(c, "syphilis_testing"));
            record.setSyphilis_test_type(getCursorValue(c, "syphilis_test_type"));
            record.setSyphilis_other(getCursorValue(c, "syphilis_other"));
            record.setDate_tested_for_syphilis(getCursorValue(c, "date_tested_for_syphilis"));
            record.setSyphilis_result(getCursorValue(c, "syphilis_result"));
            record.setDelete_status(getCursorValue(c, "delete_status"));

            return record;
        };
    }

}

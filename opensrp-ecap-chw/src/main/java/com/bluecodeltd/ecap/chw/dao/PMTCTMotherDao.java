package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.domain.Mother;
import com.bluecodeltd.ecap.chw.model.PtctMotherModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class PMTCTMotherDao extends AbstractDao {


    public static Mother getMotherByBaseEntityId(String baseEntityID) {
        String sql = "select ec_mother_index.base_entity_id , ec_mother_index.mother_first_name AS first_name, ec_mother_index.mother_surname AS last_name from ec_mother_index where ec_mother_index.base_entity_id = '" + baseEntityID + "' "  ;
        DataMap<Mother> dataMap = c -> {
            return new Mother(
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "deleted"),
                    getCursorValue(c, "caregiver_name")
            );
        };

      List <Mother> mother =  AbstractDao.readData(sql, dataMap);
        if (mother == null) {
            return null;
        }
        return mother.get(0);
    }

    public static List<Mother> getMothers(String householdID) {

        String sql = "SELECT * FROM ec_mother_index WHERE household_id = '"+ householdID +"' ";

        DataMap<Mother> dataMap = c -> {
            return new Mother(
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "deleted"),
                    getCursorValue(c, "caregiver_name")
            );
        };

        List<Mother> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static PtctMotherModel getPMCTMother(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_mother WHERE pmtct_id = '" + pmtctID + "' AND (delete_status IS NULL OR delete_status <> '1')";

        List<PtctMotherModel> values = AbstractDao.readData(sql, getPtctMotherModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }



    public static List<PtctMotherModel> getPostnatalMother(String pmtctID) {

        String sql = "SELECT *,strftime('%Y-%m-%d', substr(date_of_st_post_natal_care,7,4) || '-' || substr(date_of_st_post_natal_care,4,2) || '-' || substr(date_of_st_post_natal_care,1,2)) as sortable_date  FROM ec_pmtct_mother_postnatal WHERE pmtct_id = '" + pmtctID + "'  ORDER BY sortable_date DESC";

        List<PtctMotherModel> values = AbstractDao.readData(sql, getPtctMotherModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static String countMotherPostnatal (String pmtctID){

        String sql = "SELECT COUNT(*) v FROM ec_pmtct_mother_postnatal WHERE pmtct_id = '" + pmtctID + "' ";
        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }

    public static DataMap<PtctMotherModel> getPtctMotherModelMap() {
        return c -> {

            PtctMotherModel record = new PtctMotherModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setSm_number(getCursorValue(c, "sm_number"));
            record.setProvince(getCursorValue(c, "province"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setWard(getCursorValue(c, "ward"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setPartner(getCursorValue(c, "partner"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setEcap_id_question(getCursorValue(c, "ecap_id_question"));
            record.setDate_enrolled_ecap(getCursorValue(c, "date_enrolled_ecap"));
            record.setEcap_id(getCursorValue(c, "ecap_id"));
            record.setDate_enrolled_pmtct(getCursorValue(c, "date_enrolled_pmtct"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setMothers_age(getCursorValue(c, "mothers_age"));
            record.setHome_address(getCursorValue(c, "home_address"));
            record.setNearest_landmark(getCursorValue(c, "nearest_landmark"));
            record.setMothers_phone(getCursorValue(c, "mothers_phone"));
            record.setAgyw_date_1st_visit(getCursorValue(c, "agyw_date_1st_visit"));
            record.setAgyw_gestation_age_in_weeks(getCursorValue(c, "agyw_gestation_age_in_weeks"));
            record.setAgyw_hiv_tested(getCursorValue(c, "agyw_hiv_tested"));
            record.setAgyw_date_tested(getCursorValue(c, "agyw_date_tested"));
            record.setAgyw_result_of_hiv_test(getCursorValue(c, "agyw_result_of_hiv_test"));
            record.setAgyw_recency_test_result(getCursorValue(c, "agyw_recency_test_result"));
            record.setAgyw_applicable_recency_result(getCursorValue(c, "agyw_applicable_recency_result"));
            record.setAgyw_male_hiv_tested(getCursorValue(c, "agyw_male_hiv_tested"));
            record.setAgyw_male_date_tested(getCursorValue(c, "agyw_male_date_tested"));
            record.setAgyw_male_result_of_hiv_test(getCursorValue(c, "agyw_male_result_of_hiv_test"));
            record.setAgyw_positive_male_partner(getCursorValue(c, "agyw_positive_male_partner"));
            record.setAgyw_date_initiated_art(getCursorValue(c, "agyw_date_initiated_art"));
            record.setAgyw_art_number(getCursorValue(c, "agyw_art_number"));
            record.setAgyw_on_treatment_anc_visit(getCursorValue(c, "agyw_on_treatment_anc_visit"));
            record.setAgyw_tb_screening(getCursorValue(c, "agyw_tb_screening"));
            record.setAgyw_syphilis_testing(getCursorValue(c, "agyw_syphilis_testing"));
            record.setAgyw_date_tested_syphilis(getCursorValue(c, "agyw_date_tested_syphilis"));
            record.setAgyw_syphilis_test_result(getCursorValue(c, "agyw_syphilis_test_result"));
            record.setAgyw_hiv_result_1st_trimester(getCursorValue(c, "agyw_hiv_result_1st_trimester"));
            record.setAgyw_hiv_result_2nd_trimester(getCursorValue(c, "agyw_hiv_result_2nd_trimester"));
            record.setAgyw_hiv_result_3rd_trimester(getCursorValue(c, "agyw_hiv_result_3rd_trimester"));
            record.setAgyw_vl_result_1st_trimester(getCursorValue(c, "agyw_vl_result_1st_trimester"));
            record.setAgyw_unsuppressed_vl_1st(getCursorValue(c, "agyw_unsuppressed_vl_1st"));
            record.setAgyw_vl_result_2nd_trimester(getCursorValue(c, "agyw_vl_result_2nd_trimester"));
            record.setAgyw_unsuppressed_vl_2nd(getCursorValue(c, "agyw_unsuppressed_vl_2nd"));
            record.setAgyw_vl_result_3rd_trimester(getCursorValue(c, "agyw_vl_result_3rd_trimester"));
            record.setAgyw_unsuppressed_vl_3rd(getCursorValue(c, "agyw_unsuppressed_vl_3rd"));
            record.setPreventive_services(getCursorValue(c, "preventive_services"));
            record.setOther_preventive_service(getCursorValue(c, "other_preventive_service"));
            record.setDate_initiated_art(getCursorValue(c, "date_initiated_art"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setDate_1st_visit(getCursorValue(c, "date_1st_visit"));
            record.setGestation_age_in_weeks(getCursorValue(c, "gestation_age_in_weeks"));
            record.setHiv_tested(getCursorValue(c, "hiv_tested"));
            record.setDate_tested(getCursorValue(c, "date_tested"));
            record.setResult_of_hiv_test(getCursorValue(c, "result_of_hiv_test"));
            record.setRecency_test_result(getCursorValue(c, "recency_test_result"));
            record.setMale_hiv_tested(getCursorValue(c, "male_hiv_tested"));
            record.setMale_date_tested(getCursorValue(c, "male_date_tested"));
            record.setMale_result_of_hiv_test(getCursorValue(c, "male_result_of_hiv_test"));
            record.setTreatment_initiated(getCursorValue(c, "treatment_initiated"));
            record.setTb_screening(getCursorValue(c, "tb_screening"));
            record.setSyphilis_testing(getCursorValue(c, "syphilis_testing"));
            record.setDate_tested_syphilis(getCursorValue(c, "date_tested_syphilis"));
            record.setSyphilis_test_result(getCursorValue(c, "syphilis_test_result"));
            record.setUnsuppressed_vl_3rd(getCursorValue(c, "unsuppressed_vl_3rd"));
            record.setDelete_status(getCursorValue(c, "delete_status"));

            return record;
        };
    }

}

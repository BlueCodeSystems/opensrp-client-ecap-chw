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

        String sql = "SELECT * FROM ec_pmtct_mother WHERE pmtct_id = '" + pmtctID + "' ";

        List<PtctMotherModel> values = AbstractDao.readData(sql, getPtctMotherModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
    public static PtctMotherModel getPostnatalDate(String pmtctID) {
        String sql = "SELECT *,strftime('%Y-%m-%d', substr(date_of_st_post_natal_care,7,4) || '-' || substr(date_of_st_post_natal_care,4,2) || '-' || substr(date_of_st_post_natal_care,1,2)) as sortable_date  FROM ec_pmtct_mother_postnatal WHERE pmtct_id = '" + pmtctID + "'  ORDER BY sortable_date DESC LIMIT 1";

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
            record.setProvince(getCursorValue(c, "province"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setEcap_id_question(getCursorValue(c, "ecap_id_question"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setWard(getCursorValue(c, "ward"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setPartner(getCursorValue(c, "partner"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setDate_enrolled_ecap(getCursorValue(c, "date_enrolled_ecap"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setDate_enrolled_pmtct(getCursorValue(c, "date_enrolled_pmtct"));
            record.setMothers_full_name(getCursorValue(c, "mothers_full_name"));
            record.setNick_name(getCursorValue(c, "nick_name"));
            record.setMothers_age(getCursorValue(c, "mothers_age"));
            record.setDate_initiated_on_art(getCursorValue(c, "date_initiated_on_art"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setMothers_smh_no(getCursorValue(c, "mothers_smh_no"));
            record.setHome_address(getCursorValue(c, "home_address"));
            record.setNearest_landmark(getCursorValue(c, "nearest_landmark"));
            record.setMothers_phone(getCursorValue(c, "mothers_phone"));
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
            record.setDate_of_delivery(getCursorValue(c, "date_of_delivery"));
            record.setPlace_of_delivery(getCursorValue(c, "place_of_delivery"));
            record.setOn_art_at_time_of_delivery(getCursorValue(c, "on_art_at_time_of_delivery"));
            record.setDate_of_st_post_natal_care(getCursorValue(c, "date_of_st_post_natal_care"));
            record.setMother_tested_for_hiv(getCursorValue(c, "mother_tested_for_hiv"));
            record.setHiv_test_result_r_nr_at_6_weeks(getCursorValue(c, "hiv_test_result_r_nr_at_6_weeks"));
            record.setArt_initiated_at_6_weeks(getCursorValue(c, "art_initiated_at_6_weeks"));
            record.setArt_adherence_counselling_support_at_6_weeks(getCursorValue(c, "art_adherence_counselling_support_at_6_weeks"));
            record.setFamily_planning_counselling_at_6_weeks(getCursorValue(c, "family_planning_counselling_at_6_weeks"));
            record.setComments_at_postnatal_care_visit_6_weeks(getCursorValue(c, "comments_at_postnatal_care_visit_6_weeks"));
            record.setHiv_test_result_r_nr_at_6_months(getCursorValue(c, "hiv_test_result_r_nr_at_6_months"));
            record.setArt_initiated_at_6_months(getCursorValue(c, "art_initiated_at_6_months"));
            record.setFamily_planning_counselling_at_6_months(getCursorValue(c, "family_planning_counselling_at_6_months"));
            record.setNumber_of_condoms_distributed_at_6_months(getCursorValue(c, "number_of_condoms_distributed_at_6_months"));
            record.setComments_at_postnatal_care_visit_6(getCursorValue(c, "comments_at_postnatal_care_visit_6"));
            record.setHiv_test_result_r_nr_at_9_weeks(getCursorValue(c, "hiv_test_result_r_nr_at_9_weeks"));
            record.setArt_initiated_at_9_weeks(getCursorValue(c, "art_initiated_at_9_weeks"));
            record.setArt_adherence_counselling_support_at_9_weeks(getCursorValue(c, "art_adherence_counselling_support_at_9_weeks"));
            record.setFamily_planning_counselling_at_9_weeks(getCursorValue(c, "family_planning_counselling_at_9_weeks"));
            record.setComments_at_postnatal_care_visit_9_weeks(getCursorValue(c, "comments_at_postnatal_care_visit_9_weeks"));
            record.setHiv_test_result_r_nr_at_9_months(getCursorValue(c, "hiv_test_result_r_nr_at_9_months"));
            record.setArt_initiated_at_9_months(getCursorValue(c, "art_initiated_at_9_months"));
            record.setFamily_planning_counselling_at_9_months(getCursorValue(c, "family_planning_counselling_at_9_months"));
            record.setNumber_of_condoms_distributed_at_9_months(getCursorValue(c, "number_of_condoms_distributed_at_9_months"));
            record.setComments_at_postnatal_care_visit_9(getCursorValue(c, "comments_at_postnatal_care_visit_9"));
            record.setHiv_test_result_r_nr_at_12_weeks(getCursorValue(c, "hiv_test_result_r_nr_at_12_weeks"));
            record.setArt_initiated_at_12_weeks(getCursorValue(c, "art_initiated_at_12_weeks"));
            record.setArt_adherence_counselling_support_at_12_weeks(getCursorValue(c, "art_adherence_counselling_support_at_12_weeks"));
            record.setFamily_planning_counselling_at_12_weeks(getCursorValue(c, "family_planning_counselling_at_12_weeks"));
            record.setComments_at_postnatal_care_visit_12_weeks(getCursorValue(c, "comments_at_postnatal_care_visit_12_weeks"));
            record.setHiv_test_result_r_nr_at_12_months(getCursorValue(c, "hiv_test_result_r_nr_at_12_months"));
            record.setArt_initiated_at_12_months(getCursorValue(c, "art_initiated_at_12_months"));
            record.setFamily_planning_counselling_at_12_months(getCursorValue(c, "family_planning_counselling_at_12_months"));
            record.setNumber_of_condoms_distributed_at_12_months(getCursorValue(c, "number_of_condoms_distributed_at_12_months"));
            record.setComments_at_postnatal_care_visit_12(getCursorValue(c, "comments_at_postnatal_care_visit_12"));
            record.setHiv_test_result_r_nr_at_6_weeks(getCursorValue(c, "hiv_test_result_r_nr_at_6_weeks"));
            record.setArt_initiated_at_6_weeks(getCursorValue(c, "art_initiated_at_6_weeks"));
            record.setArt_adherence_counselling_support_at_6_weeks(getCursorValue(c, "art_adherence_counselling_support_at_6_weeks"));
            record.setFamily_planning_counselling_at_18_months(getCursorValue(c, "family_planning_counselling_at_18_months"));
            record.setNumber_of_condoms_distributed_at_18_months(getCursorValue(c, "number_of_condoms_distributed_at_18_months"));
            record.setComments_at_postnatal_care_visit_18(getCursorValue(c, "comments_at_postnatal_care_visit_18"));
            record.setMothers_outcome(getCursorValue(c, "mothers_outcome"));
            record.setPostnatal_care_visit(getCursorValue(c, "postnatal_care_visit"));
            record.setDelete_status(getCursorValue(c, "delete_status"));

            return record;
        };
    }

}

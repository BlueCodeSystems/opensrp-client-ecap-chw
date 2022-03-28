package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.VcaScreeningModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class VCAScreeningDao extends AbstractDao {
    public static VcaScreeningModel getVcaScreening (String UID) {

        String sql = "SELECT * FROM ec_client_index WHERE unique_id = '" + UID + "' ";

        List<VcaScreeningModel> values = AbstractDao.readData(sql, getVcaScreeningModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static String checkStatus (String UID) {

        String sql = "SELECT is_hiv_positive FROM ec_client_index WHERE unique_id = '" + UID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "is_hiv_positive");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }

    public static DataMap<VcaScreeningModel> getVcaScreeningModelMap() {
        return c -> {

            VcaScreeningModel record = new VcaScreeningModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setProvince(getCursorValue(c, "province"));
            record.setDistrict(getCursorValue(c, "district"));
            record.setWard(getCursorValue(c, "ward"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setViral_load_results_on_file(getCursorValue(c, "viral_load_results_on_file"));
            record.setIs_tb_screening_results_on_file(getCursorValue(c, "is_tb_screening_results_on_file"));
            record.setClient_screened(getCursorValue(c, "client_screened"));
            record.setPartner(getCursorValue(c, "partner"));
            record.setAdolescent_first_name(getCursorValue(c, "adolescent_first_name"));
            record.setAdolescent_last_name(getCursorValue(c, "adolescent_last_name"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            record.setHomeaddress(getCursorValue(c, "homeaddress"));
            record.setLandmark(getCursorValue(c, "landmark"));
            record.setAdolescent_gender(getCursorValue(c, "adolescent_gender"));
            record.setSchool(getCursorValue(c, "school"));
            record.setOther_school(getCursorValue(c, "other_school"));
            record.setIs_hiv_positive(getCursorValue(c, "is_hiv_positive"));
            record.setIs_on_hiv_treatment(getCursorValue(c, "is_on_hiv_treatment"));
            record.setClient_result(getCursorValue(c, "client_result"));
            record.setTpt_client_eligibility(getCursorValue(c, "tpt_client_eligibility"));
            record.setTpt_client_initiated(getCursorValue(c, "tpt_client_initiated"));
            record.setScreened_for_malnutrition(getCursorValue(c, "screened_for_malnutrition"));
            record.setTakes_drugs_to_prevent_other_diseases(getCursorValue(c, "takes_drugs_to_prevent_other_diseases"));
            record.setLess_3(getCursorValue(c, "less_3"));
            record.setPositive_mother(getCursorValue(c, "positive_mother"));
            record.setActive_on_treatment(getCursorValue(c, "active_on_treatment"));
            record.setCaregiver_art_number(getCursorValue(c, "caregiver_art_number"));
            record.setAdhering_to_treatment(getCursorValue(c, "adhering_to_treatment"));
            record.setIs_mother_virally_suppressed(getCursorValue(c, "is_mother_virally_suppressed"));
            record.setChild_been_tested_for_hiv(getCursorValue(c, "child_been_tested_for_hiv"));
            record.setChild_receiving_breastfeeding(getCursorValue(c, "child_receiving_breastfeeding"));
            record.setChild_tested_for_hiv_inline_with_guidelines(getCursorValue(c, "child_tested_for_hiv_inline_with_guidelines"));
            record.setReceives_drugs_to_prevent_hiv_and_other_illnesses_hei(getCursorValue(c, "receives_drugs_to_prevent_hiv_and_other_illnesses_hei"));
            record.setChild_been_screened_for_malnutrition_hei(getCursorValue(c, "child_been_screened_for_malnutrition_hei"));
            record.setChild_gets_drugs_to_prevent_tb_hei(getCursorValue(c, "child_gets_drugs_to_prevent_tb_hei"));
            record.setChild_enrolled_in_early_childhood_development_program(getCursorValue(c, "child_enrolled_in_early_childhood_development_program"));
            record.setIs_biological_child_of_mother_living_with_hiv(getCursorValue(c, "is_biological_child_of_mother_living_with_hiv"));
            record.setChild_tested_for_hiv_with_mother_as_index_client(getCursorValue(c, "child_tested_for_hiv_with_mother_as_index_client"));
            record.setIs_mother_currently_on_treatment_wlhiv(getCursorValue(c, "is_mother_currently_on_treatment_wlhiv"));
            record.setMother_art_number_wlhiv(getCursorValue(c, "mother_art_number_wlhiv"));
            record.setIs_mother_adhering_to_treatment_wlhiv(getCursorValue(c, "is_mother_adhering_to_treatment_wlhiv"));
            record.setIs_mother_virally_suppressed_wlhiv(getCursorValue(c, "is_mother_virally_suppressed_wlhiv"));
            record.setChild_receiving_any_hiv_and_violence_prevention_intervention(getCursorValue(c, "child_receiving_any_hiv_and_violence_prevention_intervention"));
            record.setAgyw_sexually_active(getCursorValue(c, "agyw_sexually_active"));
            record.setHiv_or_pregnancy_prevention_method_used(getCursorValue(c, "hiv_or_pregnancy_prevention_method_used"));
            record.setHiv_or_pregnancy_prevention_method_used_other(getCursorValue(c, "hiv_or_pregnancy_prevention_method_used_other"));
            record.setAgyw_having_sex_with_older_men(getCursorValue(c, "agyw_having_sex_with_older_men"));
            record.setAgyw_transactional_sex(getCursorValue(c, "agyw_transactional_sex"));
            record.setAgyw_engaged_in_transactional_sex(getCursorValue(c, "agyw_engaged_in_transactional_sex"));
            record.setAgyw_engaged_in_sex_work(getCursorValue(c, "agyw_engaged_in_sex_work"));
            record.setAgyw_food_or_economically_insecure(getCursorValue(c, "agyw_food_or_economically_insecure"));
            record.setAgyw_marry_early(getCursorValue(c, "agyw_marry_early"));
            record.setAgyw_give_birth_before_the_age_of_18(getCursorValue(c, "agyw_give_birth_before_the_age_of_18"));
            record.setAgyw_have_a_partner_who_is_violent_or_has_experienced_violence(getCursorValue(c, "agyw_have_a_partner_who_is_violent_or_has_experienced_violence"));
            record.setAgyw_ever_been_diagnosed_with_a_Sexually_transmitted_illness(getCursorValue(c, "agyw_ever_been_diagnosed_with_a_Sexually_transmitted_illness"));
            record.setAgyw_in_school(getCursorValue(c, "agyw_in_school"));
            record.setAgyw_receiving_an_economic_strengthening_intervention(getCursorValue(c, "agyw_receiving_an_economic_strengthening_intervention"));
            record.setChild_ever_experienced_sexual_violence(getCursorValue(c, "child_ever_experienced_sexual_violence"));
            record.setChild_still_living_in_the_same_household_as_the_perpetrator(getCursorValue(c, "child_still_living_in_the_same_household_as_the_perpetrator"));
            record.setChild_supported_to_seek_justice(getCursorValue(c, "child_supported_to_seek_justice"));
            record.setDid_the_child_access_clinical_care(getCursorValue(c, "did_the_child_access_clinical_care"));
            record.setChild_clinical_care_services_received(getCursorValue(c, "child_clinical_care_services_received"));
            record.setChild_clinical_care_services_received_other(getCursorValue(c, "child_clinical_care_services_received_other"));
            record.setOther_child_clinical_care_services_received(getCursorValue(c, "other_child_clinical_care_services_received"));
            record.setIs_the_child_caregiver_an_fsw(getCursorValue(c, "is_the_child_caregiver_an_fsw"));
            record.setFsw_child_tested(getCursorValue(c, "fsw_child_tested"));
            record.setFsw_child_positive(getCursorValue(c, "fsw_child_positive"));
            record.setFsw_prevention_intervention(getCursorValue(c, "fsw_prevention_intervention"));
            record.setFsw_economic_strengthening_intervention(getCursorValue(c, "fsw_economic_strengthening_intervention"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setDate_screened(getCursorValue(c, "date_screened"));
            record.setApproved_by(getCursorValue(c, "approved_by"));
            record.setConsent_check_box(getCursorValue(c, "consent_check_box"));
            record.setSubpop1_toast(getCursorValue(c, "subpop1_toast"));
            record.setSubpop6_toast(getCursorValue(c, "subpop6_toast"));
            record.setSubpop2_toast(getCursorValue(c, "subpop2_toast"));
            record.setSubpop3_toast(getCursorValue(c, "subpop3_toast"));
            record.setSubpop4_toast(getCursorValue(c, "subpop4_toast"));
            record.setSubpop5_toast(getCursorValue(c, "subpop5_toast"));
            record.setSubpop1(getCursorValue(c, "subpop1"));
            record.setSubpop2(getCursorValue(c, "subpop2"));
            record.setSubpop3(getCursorValue(c, "subpop3"));
            record.setSubpop4(getCursorValue(c, "subpop4"));
            record.setSubpop5(getCursorValue(c, "subpop5"));
            record.setSubpop(getCursorValue(c, "subpop"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setGender(getCursorValue(c, "gender"));
            record.setBirthdate(getCursorValue(c, "birthdate"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            record.setIndex_check_box(getCursorValue(c, "index_check_box"));
            record.setCase_status(getCursorValue(c, "case_status"));
            record.setDate_referred(getCursorValue(c, "date_referred"));
            record.setDate_offered_enrollment(getCursorValue(c, "date_offered_enrollment"));
            record.setAcceptance(getCursorValue(c, "acceptance"));
            record.setDate_enrolled(getCursorValue(c, "date_enrolled"));
            record.setDate_hiv_known(getCursorValue(c, "date_hiv_known"));
            record.setArt_check_box(getCursorValue(c, "art_check_box"));
            record.setDate_started_art(getCursorValue(c, "date_started_art"));
            record.setVl_check_box(getCursorValue(c, "vl_check_box"));
            record.setDate_last_vl(getCursorValue(c, "date_last_vl"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setDate_next_vl(getCursorValue(c, "date_next_vl"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setChild_mmd(getCursorValue(c, "child_mmd"));
            record.setLevel_mmd(getCursorValue(c, "level_mmd"));
            record.setCaregiver_name(getCursorValue(c, "caregiver_name"));
            record.setCaregiver_nrc(getCursorValue(c, "caregiver_nrc"));
            record.setCaregiver_sex(getCursorValue(c, "caregiver_sex"));
            record.setCaregiver_birth_date(getCursorValue(c, "caregiver_birth_date"));
            record.setCaregiver_hiv_status(getCursorValue(c, "caregiver_hiv_status"));
            record.setRelation(getCursorValue(c, "relation"));
            record.setCaregiver_phone(getCursorValue(c, "caregiver_phone"));
            record.setDe_registration_date(getCursorValue(c, "de_registration_date"));
            record.setReason(getCursorValue(c, "reason"));
            record.setTransfer_reason(getCursorValue(c, "transfer_reason"));
            record.setOther_reason(getCursorValue(c, "other_reason"));
            record.setScreening_location(getCursorValue(c,"screening_location"));
            record.setPhone(getCursorValue(c,"phone"));
            return record;
        };
    }

}

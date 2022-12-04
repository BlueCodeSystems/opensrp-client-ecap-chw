package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.FamilyServiceModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class VcaVisitationDao extends AbstractDao {

    public static int countVisits(String householdID){

        String sql = "SELECT COUNT(*) AS visitCount FROM ec_household_visitation_for_vca_0_20_years WHERE unique_id = '" + householdID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "visitCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return Integer.parseInt(values.get(0));

    }

    public static VcaVisitationModel getVcaVisitation (String vcaID) {

        String sql = "SELECT * FROM ec_household_visitation_for_vca_0_20_years WHERE unique_id = '" + vcaID + "' ";

        List<VcaVisitationModel> values = AbstractDao.readData(sql, getVcaVisitationModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static List<VcaVisitationModel> getVisitsByCaseWorkerPhone(String casePhone) {

        String sql = "SELECT ec_household_visitation_for_vca_0_20_years.unique_id,  MAX(ec_household_visitation_for_vca_0_20_years.visit_date) AS visit_date,  " +
                "ec_client_index.first_name,  ec_client_index.last_name, ec_client_index.subpop2 AS hei, ec_client_index.adolescent_birthdate AS birthdate " +
                "FROM ec_household_visitation_for_vca_0_20_years LEFT JOIN ec_client_index ON " +
                "ec_household_visitation_for_vca_0_20_years.unique_id = ec_client_index.unique_id WHERE " +
                "ec_household_visitation_for_vca_0_20_years.phone = '" + casePhone + "' AND ec_client_index.subpop2 = 'true' GROUP BY " +
                "ec_household_visitation_for_vca_0_20_years.unique_id";

        List<VcaVisitationModel> values = AbstractDao.readData(sql, getVcaVisitationModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }


    public static List<VcaVisitationModel> getVisitsByID(String childID) {

        String sql = "SELECT * FROM ec_household_visitation_for_vca_0_20_years WHERE unique_id = '" + childID + "' ORDER BY visit_date DESC ";

        List<VcaVisitationModel> values = AbstractDao.readData(sql, getVcaVisitationModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static AbstractDao.DataMap<VcaVisitationModel> getVcaVisitationModelMap() {
        return c -> {

            VcaVisitationModel record = new VcaVisitationModel();
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setBirthdate(getCursorValue(c, "birthdate"));
            record.setHei(getCursorValue(c, "hei"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setAge(getCursorValue(c, "age"));
            record.setVisit_date(getCursorValue(c, "visit_date"));
            record.setIs_hiv_positive(getCursorValue(c, "is_hiv_positive"));
            record.setChild_art(getCursorValue(c, "child_art"));
            record.setClinical_care(getCursorValue(c, "clinical_care"));
            record.setArt_appointment(getCursorValue(c, "art_appointment"));
            record.setCounselling(getCursorValue(c, "counselling"));
            record.setArt_medication(getCursorValue(c, "art_medication"));
            record.setMmd(getCursorValue(c, "child_mmd"));
            record.setMmd_months(getCursorValue(c, "level_mmd"));
            record.setDrug_regimen(getCursorValue(c, "drug_regimen"));
            record.setDate_art(getCursorValue(c, "date_art"));
            record.setSix_months(getCursorValue(c, "six_months"));
            record.setHealth_facility(getCursorValue(c, "health_facility"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setDate_hiv(getCursorValue(c, "date_hiv"));
            record.setVisit_hiv_test(getCursorValue(c, "visit_hiv_test"));
            record.setHiv_risk(getCursorValue(c, "referred_for_testing"));
            record.setHiv_risk(getCursorValue(c, "hiv_risk"));
            record.setHiv_muac(getCursorValue(c, "hiv_muac"));
            record.setHiv_assessment(getCursorValue(c, "hiv_assessment"));
            record.setReferred_facility(getCursorValue(c, "referred_facility"));
            record.setEid_test(getCursorValue(c, "eid_test"));
            record.setAge_appropriate(getCursorValue(c, "age_appropriate"));
            record.setNot_appropriate_age(getCursorValue(c, "not_appropriate_age"));
            record.setBreastfeeding(getCursorValue(c, "breastfeeding"));
            record.setNot_breastfeeding(getCursorValue(c, "not_breastfeeding"));
            record.setNot_tested(getCursorValue(c, "not_tested"));
            record.setHiv_infection(getCursorValue(c, "hiv_infection"));
            record.setAgainst_hiv_risk(getCursorValue(c, "against_hiv_risk"));
            record.setPrevention_support(getCursorValue(c, "prevention_support"));
            record.setSubstance_support(getCursorValue(c, "substance_support"));
            record.setUnder_five(getCursorValue(c, "under_five"));
            record.setAttending_under_five_clinic(getCursorValue(c, "attending_under_five_clinic"));
            record.setAppropriate_vaccinations(getCursorValue(c, "appropriate_vaccinations"));
            record.setAll_appropriate_vaccinations(getCursorValue(c, "all_appropriate_vaccinations"));
            record.setMuac_measure(getCursorValue(c, "muac_measure"));
            record.setNutrition_counselling_a(getCursorValue(c, "nutrition_counselling_a"));
            record.setHeps_a(getCursorValue(c, "heps_a"));
            record.setMuac_other_b(getCursorValue(c, "muac_other_a"));
            record.setMuac_measurement_b(getCursorValue(c, "muac_measurement_b"));
            record.setNutrition_counselling_b(getCursorValue(c, "nutrition_counselling_b"));
            record.setHeps_b(getCursorValue(c, "heps_b"));
            record.setMuac_other_b(getCursorValue(c, "muac_other_b"));
            record.setNutrition_status(getCursorValue(c, "nutrition_status"));
            record.setNeglected(getCursorValue(c, "neglected"));
            record.setNeglected_child_exploitation(getCursorValue(c, "neglected_child_exploitation"));
            record.setVl_last_result(getCursorValue(c, "neglected_child_relationships"));
            record.setChild_above_12_a(getCursorValue(c, "child_above_12_a"));
            record.setType_of_neglect(getCursorValue(c, "type_of_neglect"));
            record.setSigns_of_violence(getCursorValue(c, "signs_of_violence"));
            record.setRelationships_neglected(getCursorValue(c, "relationships_neglected"));
            record.setPhysical_violence(getCursorValue(c, " physical_violence"));
            record.setExperiencing_neglected(getCursorValue(c, "experiencing_neglected"));
            record.setType_of_neglect_physical(getCursorValue(c, "type_of_neglect_physical"));
            record.setSexually_abused(getCursorValue(c, "sexually_abused"));
            record.setPost_gbv_care(getCursorValue(c, "post_gbv_care"));
            record.setParenting_intervention(getCursorValue(c, "parenting_intervention"));
            record.setSexual_abuse(getCursorValue(c, "sexual_abuse"));
            record.setType_of_neglect_sexual(getCursorValue(c, "type_of_neglect_sexual"));
            record.setChild_psychosocial(getCursorValue(c, "child_psychosocial"));
            record.setPsychosocial_needs(getCursorValue(c, "psychosocial_needs"));
            record.setPsychosocial_services(getCursorValue(c, "psychosocial_services"));
            record.setChild_other_services(getCursorValue(c, "child_other_services"));
            record.setReferred_services(getCursorValue(c, "referred_services"));
            record.setCurrently_in_school(getCursorValue(c, "currently_in_school"));
            record.setNot_in_school(getCursorValue(c, "not_in_school"));
            record.setChild_missed(getCursorValue(c, "child_missed"));
            record.setChallenges_barriers(getCursorValue(c, "challenges_barriers"));
            record.setChild_household(getCursorValue(c, "child_household"));
            record.setChild_household_services(getCursorValue(c, "child_household_services"));
            record.setOvc_caregiver(getCursorValue(c, "ovc_caregiver"));
            record.setVerified_by_school_days(getCursorValue(c, "verified_by_school_days"));
            record.setVerified_by_school(getCursorValue(c, "verified_by_school"));
            record.setCurrent_calendar(getCursorValue(c, "current_calendar"));
            record.setDid_not_progress(getCursorValue(c, "did_not_progress"));
            record.setProgression_child_household(getCursorValue(c, "progression_child_household"));
            record.setSexually_abused(getCursorValue(c, "progression_child_household_services"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setCaseworker_date_signed(getCursorValue(c, "caseworker_date_signed"));
            record.setCaseworker_signature(getCursorValue(c, "caseworker_signature"));
            record.setManager_name(getCursorValue(c, "manager_name"));
            record.setManager_date_signed(getCursorValue(c, "manager_date_signed"));
            record.setManager_signature(getCursorValue(c, "manager_signature"));
            record.setSchool_administration_name(getCursorValue(c, "school_administration_name"));
            record.setTelephone_number(getCursorValue(c, "telephone_number"));
            record.setSchool_administration_date_signed(getCursorValue(c, "school_administration_date_signed"));
            record.setSchool_administration_signature(getCursorValue(c, "school_administration_signature"));
            record.setPhone(getCursorValue(c, "phone"));




            return record;
        };
    }



}

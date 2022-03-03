package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.CaregiverVisitationModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class CaregiverVisitationDao extends AbstractDao {
    public static CaregiverVisitationModel getCaregiverVisitation (String householdID) {

        String sql = "SELECT * FROM ec_household_visitation_for_caregiver WHERE household_id = '" + householdID + "' ";

        List<CaregiverVisitationModel> values = AbstractDao.readData(sql, getCaregiverVisitationMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<CaregiverVisitationModel> getCaregiverVisitationMap() {
        return c -> {

            CaregiverVisitationModel record = new CaregiverVisitationModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setCaregiver_hiv_status(getCursorValue(c, "caregiver_hiv_status"));
            record.setCaregiver_art(getCursorValue(c, "caregiver_art"));
            record.setClinical_care(getCursorValue(c, "clinical_care"));
            record.setDate_art(getCursorValue(c, "date_art"));
            record.setArt_appointment(getCursorValue(c, "art_appointment"));
            record.setCounselling(getCursorValue(c, "counselling"));
            record.setArt_medication(getCursorValue(c, "art_medication"));
            record.setMmd(getCursorValue(c, "mmd"));
            record.setMmd_months(getCursorValue(c, "mmd_months"));
            record.setSix_months(getCursorValue(c, "six_months"));
            record.setRelation(getCursorValue(c, "relation"));
            record.setHealth_facility(getCursorValue(c, "health_facility"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setDate_hiv(getCursorValue(c, "date_hiv"));
            record.setVisit_hiv_test(getCursorValue(c, "visit_hiv_test"));
            record.setReferred_for_testing(getCursorValue(c, "referred_for_testing"));
            record.setHiv_risk(getCursorValue(c, "hiv_risk"));
            record.setHiv_assessment(getCursorValue(c, "hiv_assessment"));
            record.setPrevention_support(getCursorValue(c, "prevention_support"));
            record.setReferred_facility(getCursorValue(c, "referred_facility"));
            record.setNumber_children(getCursorValue(c, "number_children"));
            record.setHiv_awareness_status(getCursorValue(c, "hiv_awareness_status"));
            record.setCaregiver_not_aware(getCursorValue(c, "caregiver_not_aware"));
            record.setHiv_test_referral(getCursorValue(c, "hiv_test_referral"));
            record.setSchool_fees(getCursorValue(c, "school_fees"));
            record.setUnpaid_school_fees(getCursorValue(c, "unpaid_school_fees"));
            record.setLinked_economic(getCursorValue(c, "linked_economic"));
            record.setReferred_mcdss(getCursorValue(c, "referred_mcdss"));
            record.setSource_income(getCursorValue(c, "source_income"));
            record.setList_source(getCursorValue(c, "list_source"));
            record.setBills_associated(getCursorValue(c, "bills_associated"));
            record.setBarriers_challenges(getCursorValue(c, "barriers_challenges"));
            record.setEconomic_strengthening(getCursorValue(c, "economic_strengthening"));
            record.setSocial_cash(getCursorValue(c, "social_cash"));
            record.setIncome_source_medical(getCursorValue(c, "income_source_medical"));
            record.setList_source_medical(getCursorValue(c, "list_source_medical"));
            record.setCase_worker(getCursorValue(c, "case_worker"));
            record.setCaseworker_date_signed(getCursorValue(c, "caseworker_date_signed"));
            record.setCase_manager(getCursorValue(c, "case_manager"));
            record.setManager_date_signed(getCursorValue(c, "manager_date_signed"));
            record.setSchool_administration_name(getCursorValue(c, "school_administration_name"));
            record.setTelephone(getCursorValue(c, "telephone"));
            record.setSchool_administration_date_signed(getCursorValue(c, "school_administration_date_signed"));


            return record;
        };
    }


}

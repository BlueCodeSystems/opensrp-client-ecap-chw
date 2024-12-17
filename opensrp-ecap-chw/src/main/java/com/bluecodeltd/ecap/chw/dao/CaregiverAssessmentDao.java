package com.bluecodeltd.ecap.chw.dao;

import android.util.Log;

import com.bluecodeltd.ecap.chw.model.CaregiverAssessmentModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class CaregiverAssessmentDao extends AbstractDao {
    public static CaregiverAssessmentModel getCaregiverAssessment(String householdID) {
        if (householdID == null || householdID.isEmpty()) {
            Log.w("getCaregiverAssessment", "Household ID is null or empty.");
            return null;
        }

        try {
            String sql = "SELECT * FROM ec_caregiver_household_assessment WHERE household_id = '" + householdID + "'";

            List<CaregiverAssessmentModel> values = AbstractDao.readData(sql, getCaregiverAssessmentMap());

            if (values == null || values.isEmpty()) {
                Log.i("getCaregiverAssessment", "No caregiver assessment found for household ID: " + householdID);
                return null;
            }
            return values.get(0);
        } catch (Exception e) {
            Log.e("getCaregiverAssessment", "Error fetching caregiver assessment: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public static DataMap<CaregiverAssessmentModel> getCaregiverAssessmentMap() {
        return c -> {

            CaregiverAssessmentModel record = new CaregiverAssessmentModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setHousehold_type(getCursorValue(c, "household_type"));
            record.setMonthly_expenses(getCursorValue(c, "monthly_expenses"));
            record.setCaregiver_hiv_status(getCursorValue(c, "caregiver_hiv_status"));
            record.setTest_6months(getCursorValue(c, "test_6months"));
            record.setSymptoms(getCursorValue(c, "symptoms"));
            record.setPrivate_parts(getCursorValue(c, "private_parts"));
            record.setExposed(getCursorValue(c, "exposed"));
            record.setUnprotected(getCursorValue(c, "unprotected"));
            record.setBreastfeeding(getCursorValue(c, "breastfeeding"));
            record.setLast_year(getCursorValue(c, "last_year"));
            record.setRelation(getCursorValue(c, "relation"));
            record.setPartner_caregiver(getCursorValue(c, "partner_caregiver"));
            record.setActive_on_treatment(getCursorValue(c, "active_on_treatment"));
            record.setCaregiver_art_number(getCursorValue(c, "caregiver_art_number"));
            record.setAppointments(getCursorValue(c, "appointments"));
            record.setArt_regularly(getCursorValue(c, "art_regularly"));
            record.setHiv_adherence(getCursorValue(c, "hiv_adherence"));
            record.setMonths_medication(getCursorValue(c, "months_medication"));
            record.setViral_load_12months(getCursorValue(c, "viral_load_12months"));
            record.setViral_load_results(getCursorValue(c, "viral_load_results"));
            record.setDate_of_last_viral_load(getCursorValue(c, "date_of_last_viral_load"));
            record.setDocumented_12months(getCursorValue(c, "documented_12months"));
            record.setPmtct_enrolled(getCursorValue(c, "pmtct_enrolled"));
            record.setCancer_screened(getCursorValue(c, "cancer_screened"));
            record.setMalnutrition_screened(getCursorValue(c, "malnutrition_screened"));
            record.setHousehold_number(getCursorValue(c, "household_number"));
            record.setCaregiver_education(getCursorValue(c, "caregiver_education"));
            record.setHousehold_member(getCursorValue(c, "household_member"));
            record.setSource_income(getCursorValue(c, "source_income"));
            record.setOther_source_main(getCursorValue(c, "other_source_main"));
            record.setSource_earner(getCursorValue(c, "source_earner"));
            record.setOther_earner_other(getCursorValue(c, "other_earner_other"));
            record.setLast_time(getCursorValue(c, "last_time"));
            record.setHh_head_spouse(getCursorValue(c, "hh_head_spouse"));
            record.setAny_adult(getCursorValue(c, "any_adult"));
            record.setHousehold_stable(getCursorValue(c, "household_stable"));
            record.setEvery_member(getCursorValue(c, "every_member"));
            record.setMaterial_construction(getCursorValue(c, "material_construction"));
            record.setMaterial_construction_roof(getCursorValue(c, "material_construction_roof"));
            record.setEarly_childhood(getCursorValue(c, "early_childhood"));
            record.setChildren_adolescent_a(getCursorValue(c, "children_adolescent_a"));
            record.setChildren_adolescent_b(getCursorValue(c, "children_adolescent_b"));
            record.setDrinking_water(getCursorValue(c, "drinking_water"));
            record.setOther_water_other(getCursorValue(c, "other_water_other"));
            record.setToilet_facility(getCursorValue(c, "toilet_facility"));
            record.setSanitary_products(getCursorValue(c, "sanitary_products"));
            record.setOther_sanitary_product_other(getCursorValue(c, "other_sanitary_product_other"));
            record.setMaterial_construction_food(getCursorValue(c, "material_construction_food"));
            record.setOther_food_source_other(getCursorValue(c, "other_food_source_other"));
            record.setHousehold_eaten(getCursorValue(c, "household_eaten"));
            record.setHousehold_eaten_month(getCursorValue(c, "household_eaten_month"));
            record.setChild(getCursorValue(c, "child"));
            record.setPregnant_caregiver(getCursorValue(c, "pregnant_caregiver"));
            record.setLack_resources(getCursorValue(c, "lack_resources"));
            record.setLimited_variety(getCursorValue(c, "limited_variety"));
            record.setNight_hungry(getCursorValue(c, "night_hungry"));
            record.setChild_household(getCursorValue(c, "child_household"));
            record.setOther_obvious_issues(getCursorValue(c, "other_obvious_issues"));
            record.setNot_registered(getCursorValue(c, "not_registered"));
            record.setQuestions(getCursorValue(c, "questions"));
            record.setCaregiver_question(getCursorValue(c, "caregiver_question"));
            return record;
        };
    }

}

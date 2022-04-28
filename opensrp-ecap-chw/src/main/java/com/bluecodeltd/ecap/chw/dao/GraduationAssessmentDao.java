package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.GraduationAssessmentModel;
import com.bluecodeltd.ecap.chw.model.Household;
import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class GraduationAssessmentDao extends AbstractDao {
    public static GraduationAssessmentModel getGraduationAssessment (String vcaID) {

        String sql = "SELECT * FROM ec_ovc_graduation WHERE unique_id = '" + vcaID + "' ";

        List<GraduationAssessmentModel> values = AbstractDao.readData(sql, getGraduationAssessmentModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<GraduationAssessmentModel> getGraduationAssessmentModelMap() {
        return c -> {

            GraduationAssessmentModel record = new GraduationAssessmentModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setAssessment_date_1(getCursorValue(c, "assessment_date_1"));
            record.setAssessment_date_2(getCursorValue(c, "assessment_date_2"));
            record.setDate_family_enrolled(getCursorValue(c, "date_family_enrolled"));
            record.setAssessed_graduation(getCursorValue(c, "assessed_graduation"));
            record.setCaseworker_firstname(getCursorValue(c, "caseworker_firstname"));
            record.setCaseworker_firstname(getCursorValue(c, "caseworker_lastname"));
            record.setCaseworker_nrc(getCursorValue(c, "caseworker_nrc"));
            record.setName_rcmc_dc(getCursorValue(c, "name_rcmc_dc"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setFamily_date(getCursorValue(c, "family_date"));
            record.setCaregiver_name(getCursorValue(c, "caregiver_name"));
            record.setCaregiver_sex(getCursorValue(c, "caregiver_sex"));
            record.setCaregiver_age(getCursorValue(c, "caregiver_age"));
            record.setEnrolled_project(getCursorValue(c, "enrolled_project"));
            record.setCaregiver_household(getCursorValue(c, "caregiver_household"));
            record.setHiv_status(getCursorValue(c, "hiv_status"));
            record.setVirally_suppressed(getCursorValue(c, "virally_suppressed"));
            record.setAged_10_17(getCursorValue(c, "aged_10_17"));
            record.setMost_common_hiv_infected_hiv_ways(getCursorValue(c, "most_common_hiv_infected_hiv_ways"));
            record.setPrevent_reinfection(getCursorValue(c, "prevent_reinfection"));
            record.setPlace_for_hiv_prevention(getCursorValue(c, "place_for_hiv_prevention"));
            record.setAged_5(getCursorValue(c, "aged_5"));
            record.setUndernourished(getCursorValue(c, "undernourished"));
            record.setGrade_7(getCursorValue(c, "grade_7"));
            record.setMedical_costs(getCursorValue(c, "medical_costs"));
            record.setAdditional_question(getCursorValue(c, "additional_question"));
            record.setBeaten(getCursorValue(c, "beaten"));
            record.setPunched(getCursorValue(c, "punched"));
            record.setTouching_in_sexual(getCursorValue(c, "touching_in_sexual"));
            record.setUnder_the_care(getCursorValue(c, "under_the_care"));
            record.setSex_against_your_will(getCursorValue(c, "sex_against_your_will"));
            record.setBenchmark_8_question(getCursorValue(c, "benchmark_8_question"));
            record.setSchool_age(getCursorValue(c, "school_age"));
            record.setAttended_school_regularly(getCursorValue(c, "attended_school_regularly"));
            record.setSchool_progress(getCursorValue(c, "school_progress"));
            record.setBenchmark_description(getCursorValue(c, "benchmark_description"));
            record.setAdditional_information(getCursorValue(c, "additional_information"));
            record.setCaseworker_signature(getCursorValue(c, "caseworker_signature"));
            record.setCaseworker_date_signed(getCursorValue(c, "caseworker_date_signed"));
            record.setRcmc_signature(getCursorValue(c, "rcmc_signature"));
            record.setRcmc_date_signed(getCursorValue(c, "rcmc_date_signed"));

            return record;
        };
    }

}

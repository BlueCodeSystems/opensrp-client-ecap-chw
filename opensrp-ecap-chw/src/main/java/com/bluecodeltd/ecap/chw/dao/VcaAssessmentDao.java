package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.VcaAssessmentModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class VcaAssessmentDao extends AbstractDao {
    public static VcaAssessmentModel getVcaAssessment (String vcaID) {

        String sql = "SELECT * FROM ec_vca_assessment WHERE unique_id = '" + vcaID + "' ";

        List<VcaAssessmentModel> values = AbstractDao.readData(sql, getVcaAssessmentModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<VcaAssessmentModel> getVcaAssessmentModelMap() {
        return c -> {

            VcaAssessmentModel record = new VcaAssessmentModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setIs_hiv_positive(getCursorValue(c, "is_hiv_positive"));
            record.setVca_art(getCursorValue(c, "vca_art"));
            record.setDate_art(getCursorValue(c, "date_art"));
            record.setAppointment(getCursorValue(c, "appointment"));
            record.setTaking_art(getCursorValue(c, "taking_art"));
            record.setMonths_medication(getCursorValue(c, "months_medication"));
            record.setViral_load(getCursorValue(c, "viral_load"));
            record.setDocumented(getCursorValue(c, "documented"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setArt_health_facility(getCursorValue(c, "art_health_facility"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setVca_exposed(getCursorValue(c, "vca_exposed"));
            record.setBiological_mother(getCursorValue(c, "biological_mother"));
            record.setParents_deceased(getCursorValue(c, "parents_deceased"));
            record.setSiblings_deceased(getCursorValue(c, "siblings_deceased"));
            record.setTb_symptoms(getCursorValue(c, "tb_symptoms"));
            record.setChild_sick(getCursorValue(c, "child_sick"));
            record.setRashes(getCursorValue(c, "rashes"));
            record.setDischarge(getCursorValue(c, "discharge"));
            record.setFemale_sex_worker(getCursorValue(c, "female_sex_worker"));
            record.setSexually_active(getCursorValue(c, "sexually_active"));
            record.setSex_positive(getCursorValue(c, "sex_positive"));
            record.setSexual_partner(getCursorValue(c, "sexual_partner"));
            record.setPrivate_parts(getCursorValue(c, "private_parts"));
            record.setTransactional_sex(getCursorValue(c, "transactional_sex"));
            record.setSexually_abused(getCursorValue(c, "sexually_abused"));
            record.setPast_year(getCursorValue(c, "past_year"));
            record.setSubstance_abuse(getCursorValue(c, "substance_abuse"));
            record.setBeen_married(getCursorValue(c, "been_married"));
            record.setGiven_birth(getCursorValue(c, "given_birth"));
            record.setCurrently_pregnant(getCursorValue(c, "currently_pregnant"));
            record.setVca_response(getCursorValue(c, "vca_response"));
            record.setEyp_population(getCursorValue(c, "eyp_population"));
            record.setCaseworker_firstname(getCursorValue(c, "caseworker_firstname"));
            record.setCaseworker_lastname(getCursorValue(c, "caseworker_lastname"));
            record.setCaseworker_nrc(getCursorValue(c, "caseworker_nrc"));
            record.setDate(getCursorValue(c, "date"));
            record.setAge(getCursorValue(c,"age"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setPhone(getCursorValue(c, "phone"));

            return record;
        };
    }

}

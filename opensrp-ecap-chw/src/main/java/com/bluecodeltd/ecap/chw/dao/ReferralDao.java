package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.ReferralModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ReferralDao extends AbstractDao {
    public static ReferralModel getReferral (String vcaID) {

        String sql = "SELECT * FROM ec_referral WHERE unique_id = '" + vcaID + "' ";

        List<ReferralModel> values = AbstractDao.readData(sql, getReferralModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<ReferralModel> getReferralModelMap() {
        return c -> {

            ReferralModel record = new ReferralModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setDate_referred(getCursorValue(c, "date_referred"));
            record.setReceiving_organization(getCursorValue(c, "receiving_organization"));
            record.setCd4(getCursorValue(c, "cd4"));
            record.setHiv_adherence(getCursorValue(c, "hiv_adherence"));
            record.setHiv_counseling_testing(getCursorValue(c, "hiv_counseling_testing"));
            record.setPost_gbv(getCursorValue(c, "post_gbv"));
            record.setViral_load(getCursorValue(c, "substance_abuse"));
            record.setTb_screening(getCursorValue(c, "tb_screening"));
            record.setSupplementary(getCursorValue(c, "supplementary"));
            record.setPrep(getCursorValue(c, "prep"));
            record.setF_planning(getCursorValue(c, "f_planning"));
            record.setInsecticide(getCursorValue(c, "insecticide"));
            record.setHiv_aids_treatment(getCursorValue(c, "hiv_aids_treatment"));
            record.setF_w_health(getCursorValue(c, "f_w_health"));
            record.setVmmc(getCursorValue(c, "vmmc"));
            record.setImmunization(getCursorValue(c, "immunization"));
            record.setCondom(getCursorValue(c, "condom"));
            record.setRoutine_care(getCursorValue(c, "routine_care"));
            record.setEmergency_care(getCursorValue(c, "emergency_care"));
            record.setAge_counselling(getCursorValue(c, "age_counselling"));
            record.setH_treatment_care(getCursorValue(c, "h_treatment_care"));
            record.setPmtct(getCursorValue(c, "pmtct"));
            record.setHygiene_counselling(getCursorValue(c, "hygiene_counselling"));
            record.setTransmitted_infections(getCursorValue(c, "transmitted_infections"));
            record.setPlha(getCursorValue(c, "plha"));
            record.setViral_load(getCursorValue(c, "viral_load"));
            record.setOther_health_services(getCursorValue(c, "other_health_services"));
            record.setCare_facility(getCursorValue(c, "care_facility"));
            record.setPost_violence_trauma(getCursorValue(c, "post_violence_trauma"));
            record.setLegal_assistance(getCursorValue(c, "legal_assistance"));
            record.setOther_safety_services(getCursorValue(c, "other_safety_services"));
            record.setVca_uniforms_books(getCursorValue(c, "vca_uniforms_books"));
            record.setRe_enrollment(getCursorValue(c, "re_enrollment"));
            record.setBursaries(getCursorValue(c, "bursaries"));
            record.setOther_schooled_services(getCursorValue(c, "other_schooled_services"));
            record.setCash_transfer(getCursorValue(c, "cash_transfer"));
            record.setCash_support(getCursorValue(c, "cash_support"));
            record.setFood_security(getCursorValue(c, "food_security"));
            record.setOther_stability_services(getCursorValue(c, "other_stability_services"));
            record.setFollow_up(getCursorValue(c, "follow_up"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setJob_title(getCursorValue(c, "job_title"));
            record.setDate_of_service1(getCursorValue(c, "date_of_service1"));
            record.setDate_of_service2(getCursorValue(c, "date_of_service2"));
            record.setDate_of_service3(getCursorValue(c, "date_of_service3"));
            record.setDate_of_service4(getCursorValue(c, "date_of_service4"));
            record.setSignature(getCursorValue(c, "signature"));
            record.setCovid_19(getCursorValue(c, "covid_19"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));


            return record;
        };
    }

}

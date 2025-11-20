package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MotherLongitudinalFollowUpModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class MotherLongitudinalFollowUpDao extends AbstractDao {

    public static MotherLongitudinalFollowUpModel getLatestByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_longitudinal_follow_up WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC LIMIT 1";
        List<MotherLongitudinalFollowUpModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    public static List<MotherLongitudinalFollowUpModel> listByBaseEntityId(String baseEntityId) {
        String sql = "SELECT * FROM ec_mother_longitudinal_follow_up WHERE base_entity_id = '" + baseEntityId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static List<MotherLongitudinalFollowUpModel> listByHouseholdId(String householdId) {
        String sql = "SELECT * FROM ec_mother_longitudinal_follow_up WHERE household_id = '" + householdId + "' ORDER BY last_interacted_with DESC";
        return AbstractDao.readData(sql, getMap());
    }

    public static DataMap<MotherLongitudinalFollowUpModel> getMap() {
        return c -> {
            MotherLongitudinalFollowUpModel record = new MotherLongitudinalFollowUpModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setContact_count_number(getCursorValue(c, "contact_count_number"));
            record.setLfu_date_of_visit(getCursorValue(c, "lfu_date_of_visit"));
            record.setLfu_gestation_weeks(getCursorValue(c, "lfu_gestation_weeks"));
            record.setLfu_weight_kg(getCursorValue(c, "lfu_weight_kg"));
            record.setLfu_deworming(getCursorValue(c, "lfu_deworming"));
            record.setLfu_current_tt_doses(getCursorValue(c, "lfu_current_tt_doses"));
            record.setLfu_folate(getCursorValue(c, "lfu_folate"));
            record.setLfu_iron(getCursorValue(c, "lfu_iron"));
            record.setLfu_hiv_subsequent_test_result(getCursorValue(c, "lfu_hiv_subsequent_test_result"));
            record.setLfu_enrolled_community_pmtct_if_positive(getCursorValue(c, "lfu_enrolled_community_pmtct_if_positive"));
            record.setLfu_started_prep_if_negative(getCursorValue(c, "lfu_started_prep_if_negative"));
            record.setLfu_syphilis_test_result(getCursorValue(c, "lfu_syphilis_test_result"));
            record.setLfu_syphilis_treatment_regimen(getCursorValue(c, "lfu_syphilis_treatment_regimen"));
            record.setLfu_hepb_test_result(getCursorValue(c, "lfu_hepb_test_result"));
            record.setLfu_hepb_on_treatment(getCursorValue(c, "lfu_hepb_on_treatment"));
            record.setLfu_ipt_given_dose(getCursorValue(c, "lfu_ipt_given_dose"));
            record.setLfu_received_itn(getCursorValue(c, "lfu_received_itn"));
            record.setLfu_anc_as_couple(getCursorValue(c, "lfu_anc_as_couple"));
            record.setLfu_partner_tested_hiv(getCursorValue(c, "lfu_partner_tested_hiv"));
            record.setLfu_partner_test_result(getCursorValue(c, "lfu_partner_test_result"));
            record.setLfu_discordant(getCursorValue(c, "lfu_discordant"));
            record.setLfu_partner_on_art(getCursorValue(c, "lfu_partner_on_art"));
            record.setLfu_partner_syphilis_test(getCursorValue(c, "lfu_partner_syphilis_test"));
            record.setLfu_partner_hepb_screen(getCursorValue(c, "lfu_partner_hepb_screen"));
            record.setLfu_partner_started_prep(getCursorValue(c, "lfu_partner_started_prep"));
            record.setLfu_breast_cancer_screening(getCursorValue(c, "lfu_breast_cancer_screening"));
            record.setLfu_suspected_breast_cancer(getCursorValue(c, "lfu_suspected_breast_cancer"));
            record.setLfu_tb_status(getCursorValue(c, "lfu_tb_status"));
            record.setLfu_tpt_status(getCursorValue(c, "lfu_tpt_status"));
            record.setLfu_special_conditions(getCursorValue(c, "lfu_special_conditions"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setEntity_type(getCursorValue(c, "entity_type"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            return record;
        };
    }
}

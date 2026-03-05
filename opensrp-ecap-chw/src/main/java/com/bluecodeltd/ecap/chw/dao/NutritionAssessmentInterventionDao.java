package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.NutritionAssessmentInterventionModel;
import com.bluecodeltd.ecap.chw.model.TbScreeningModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class NutritionAssessmentInterventionDao extends AbstractDao {

    public static NutritionAssessmentInterventionModel getByVcaId(String vcaId) {
        String sql = "SELECT * FROM ec_nutrition_assessment_intervention WHERE unique_id = '" + vcaId + "'";
        List<NutritionAssessmentInterventionModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) return null;
        return values.get(0);
    }

    public static List<NutritionAssessmentInterventionModel> listByVcaId(String vcaId) {
        String sql = "SELECT * FROM ec_nutrition_assessment_intervention WHERE unique_id = '" + vcaId + "' ";
        List<NutritionAssessmentInterventionModel> values = AbstractDao.readData(sql, getMap());
        return values;

    }

    public static int countByVcaId(String vcaId) {
        String sql = "SELECT COUNT(*) AS count FROM ec_nutrition_assessment_intervention WHERE unique_id = '" + vcaId + "'";
        DataMap<Integer> mapper = c -> Integer.parseInt(getCursorValue(c, "count"));
        List<Integer> res = AbstractDao.readData(sql, mapper);
        return res != null && res.size() > 0 ? res.get(0) : 0;
    }

    public static DataMap<NutritionAssessmentInterventionModel> getMap() {
        return c -> {
            NutritionAssessmentInterventionModel record = new NutritionAssessmentInterventionModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setDate_of_assessment(getCursorValue(c, "date_of_assessment"));
            record.setMuac_category(getCursorValue(c, "muac_category"));
            record.setOedema_stage(getCursorValue(c, "oedema_stage"));
            record.setWfa_category(getCursorValue(c, "wfa_category"));
            record.setIntervention_status(getCursorValue(c, "intervention_status"));
            record.setReferral_services(getCursorValue(c, "referral_services"));
            record.setOther_referral_services(getCursorValue(c, "other_referral_services"));
            record.setWhy_not_referred(getCursorValue(c, "why_not_referred"));
            record.setReferral_completed(getCursorValue(c, "referral_completed"));
            record.setNutrition_counselling_services(getCursorValue(c, "nutrition_counselling_services"));
            record.setOther_nutrition_counselling_services(getCursorValue(c, "other_nutrition_counselling_services"));
            record.setGood_practices_services(getCursorValue(c, "good_practices_services"));
            record.setOther_good_practices_services(getCursorValue(c, "other_good_practices_services"));
            DaoModelFieldMapper.captureAdditionalFields(c, record);
            return record;
        };
    }
}



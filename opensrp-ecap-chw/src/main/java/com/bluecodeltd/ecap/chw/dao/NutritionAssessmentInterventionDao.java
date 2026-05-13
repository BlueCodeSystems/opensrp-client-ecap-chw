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
        String sql = "SELECT * FROM ec_nutrition_assessment_intervention WHERE unique_id = '" + vcaId + "' " +
                "AND (delete_status IS NULL OR delete_status <> '1')";
        List<NutritionAssessmentInterventionModel> values = AbstractDao.readData(sql, getMap());
        return values;
    }

    public static int countByVcaId(String vcaId) {
        String sql = "SELECT COUNT(*) AS count FROM ec_nutrition_assessment_intervention WHERE unique_id = '" + vcaId + "'";
        DataMap<Integer> mapper = c -> Integer.parseInt(getCursorValue(c, "count"));
        List<Integer> res = AbstractDao.readData(sql, mapper);
        return res != null && res.size() > 0 ? res.get(0) : 0;
    }

    /**
     * Returns true if every non-deleted child aged 5 years and below in the household has a latest
     * nutrition assessment where muac_category = '>12.5 cm', oedema_stage = 'Nil', wfa_category = 'Normal'.
     * Uses ORDER BY sortable date DESC LIMIT 1 per child (same pattern as getRecentServicesByVCAID).
     * Returns false if any child has no assessment or their latest record fails any criterion.
     */
    public static boolean areAllUnderFiveChildrenWellNourished(String householdId) {
        String sql = "SELECT COUNT(*) AS problem_count " +
                "FROM ec_client_index c " +
                "WHERE c.household_id = '" + householdId + "' " +
                "AND (c.deleted IS NULL OR c.deleted <> '1') " +
                "AND ((strftime('%Y', 'now') - substr(c.adolescent_birthdate, 7, 4)) * 12 + " +
                "     (strftime('%m', 'now') - substr(c.adolescent_birthdate, 4, 2))) <= 60 " +
                "AND (" +
                "    NOT EXISTS (" +
                "        SELECT 1 FROM ec_nutrition_assessment_intervention " +
                "        WHERE unique_id = c.unique_id AND (delete_status IS NULL OR delete_status <> '1')" +
                "    ) " +
                "    OR (" +
                "        SELECT TRIM(muac_category) || '|' || TRIM(oedema_stage) || '|' || TRIM(wfa_category) " +
                "        FROM ec_nutrition_assessment_intervention " +
                "        WHERE unique_id = c.unique_id " +
                "        AND (delete_status IS NULL OR delete_status <> '1') " +
                "        AND date_of_assessment IS NOT NULL " +
                "        ORDER BY strftime('%Y-%m-%d', substr(date_of_assessment,7,4) || '-' || substr(date_of_assessment,4,2) || '-' || substr(date_of_assessment,1,2)) DESC " +
                "        LIMIT 1" +
                "    ) != 'Greater than 12.5 cm|Nil|Normal'" +
                ")";
        DataMap<Integer> mapper = c -> {
            String val = getCursorValue(c, "problem_count");
            return val != null ? Integer.parseInt(val) : 0;
        };
        List<Integer> res = AbstractDao.readData(sql, mapper);
        int problemCount = (res != null && !res.isEmpty()) ? res.get(0) : 0;
        return problemCount == 0;
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
            record.setDelete_status(getCursorValue(c, "delete_status"));
            return record;
        };
    }
}

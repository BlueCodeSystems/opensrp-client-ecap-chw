package com.bluecodeltd.ecap.chw.dao;

import android.database.Cursor;
import com.bluecodeltd.ecap.chw.model.NutritionMonthlyModel;
import org.smartregister.repository.BaseRepository;
import java.util.ArrayList;
import java.util.List;

public class NutritionMonthlyDao extends BaseRepository {

    private static final String TABLE_NAME = "ec_monthly_nutrition";

    private static final String SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME;

    public NutritionMonthlyModel getNutritionMonthlyByBaseEntityId(String baseEntityId) {
        NutritionMonthlyModel model = null;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(
                    SELECT_ALL + " WHERE base_entity_id = ?",
                    new String[]{baseEntityId}
            );
            if (cursor != null && cursor.moveToFirst()) {
                model = cursorToModel(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return model;
    }

    public List<NutritionMonthlyModel> getAllNutritionMonthly() {
        List<NutritionMonthlyModel> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(
                    SELECT_ALL + " ORDER BY last_interacted_with DESC",
                    null
            );
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(cursorToModel(cursor));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    private NutritionMonthlyModel cursorToModel(Cursor cursor) {
        NutritionMonthlyModel model = new NutritionMonthlyModel();
        model.setBaseEntityId(getCursorValue(cursor, "base_entity_id"));
        model.setReportingPeriod(getCursorValue(cursor, "reporting_month"));
        model.setReportingYear(getCursorValue(cursor, "reporting_year"));
        model.setProvince(getCursorValue(cursor, "province"));
        model.setDistrict(getCursorValue(cursor, "district"));
        model.setWard(getCursorValue(cursor, "ward"));
        model.setFacilityName(getCursorValue(cursor, "facility"));
        model.setPartner(getCursorValue(cursor, "partner"));
        model.setReportStatus(getCursorValue(cursor, "report_status"));

        model.setCAlhiv(getCursorValue(cursor, "subpop_calhiv"));
        model.setHei(getCursorValue(cursor, "subpop_hei"));
        model.setCwlhiv(getCursorValue(cursor, "subpop_cml_hiv"));
        model.setCPbfa(getCursorValue(cursor, "subpop_cpbfa"));
        model.setSiblings(getCursorValue(cursor, "subpop_siblings"));
        
        model.setHhPracticingDietDiversity(getCursorValue(cursor, "hh_practicing_diet_diversity"));
        model.setHhPracticingExclusiveBf(getCursorValue(cursor, "hh_practicing_exclusive_bf"));
        model.setHhPracticingComplementaryFeeding(getCursorValue(cursor, "hh_practicing_complementary_feeding"));
        model.setHhWashActivities(getCursorValue(cursor, "hh_wash_activities"));
        model.setHhVisitedAssessment(getCursorValue(cursor, "hh_visited_assessment"));
        model.setPpmamIdentified(getCursorValue(cursor, "ppmam_identified"));
        model.setPpmamReferredCommenced(getCursorValue(cursor, "ppmam_referred_commenced"));
        model.setOtherChildrenPmam(getCursorValue(cursor, "other_children_pmam"));
        model.setPlwArtPmtctNutritionAssessment(getCursorValue(cursor, "plw_art_pmtct_nutrition_assessment"));
        model.setPlwReceivedIfas(getCursorValue(cursor, "plw_received_ifas"));
        model.setHhFoodInsecurityCounselled(getCursorValue(cursor, "hh_food_insecurity_counselled"));

        model.setMnpChildren623Received(getCursorValue(cursor, "mnp_children_6_23_received"));
        model.setMnpPlwReceived(getCursorValue(cursor, "mnp_plw_received"));
        model.setVitaChildren611Months(getCursorValue(cursor, "vita_children_6_11_months"));
        model.setVitaChildren1259Months(getCursorValue(cursor, "vita_children_12_59_months"));
        model.setVitaPlwSupplemented(getCursorValue(cursor, "vita_plw_supplemented"));
        model.setDewormingChildren1259(getCursorValue(cursor, "deworming_children_12_59"));
        model.setDewormingPlw(getCursorValue(cursor, "deworming_plw"));

        model.setEcdCentresSupportedMonitoring(getCursorValue(cursor, "ecd_centres_supported_monitoring"));
        model.setEcdCentresWithFeeding(getCursorValue(cursor, "ecd_centres_with_feeding"));
        model.setEcdChildrenEnrolled(getCursorValue(cursor, "ecd_children_enrolled"));
        model.setEcdCaregiversTrained(getCursorValue(cursor, "ecd_caregivers_trained"));
        model.setEcdDevelopmentalScreening(getCursorValue(cursor, "ecd_developmental_screening"));

        model.setWfaUnderweight(getCursorValue(cursor, "wfa_underweight"));
        model.setWfaOverweight(getCursorValue(cursor, "wfa_overweight"));
        model.setWfaNormal(getCursorValue(cursor, "wfa_normal"));

        model.setNutritionGrade1(getCursorValue(cursor, "nutrition_grade_1"));
        model.setNutritionGrade2(getCursorValue(cursor, "nutrition_grade_2"));
        model.setNutritionNr(getCursorValue(cursor, "nutrition_nr"));

        model.setMuacRedBelow115(getCursorValue(cursor, "muac_red_below_11_5"));
        model.setMuacYellow115To125(getCursorValue(cursor, "muac_yellow_11_5_to_12_5"));
        model.setMuacGreen125Plus(getCursorValue(cursor, "muac_green_12_5_plus"));
        model.setMuacOedema(getCursorValue(cursor, "muac_oedema"));

        model.setStiReferred(getCursorValue(cursor, "sti_referred"));
        model.setStiTreated(getCursorValue(cursor, "sti_treated"));

        model.setReferralNutritionToHealth(getCursorValue(cursor, "referral_nutrition_to_health"));
        model.setReferralFeedbackReceived(getCursorValue(cursor, "referral_feedback_received"));
        model.setReferralDateOfReferral(getCursorValue(cursor, "referral_date_of_referral"));
        model.setReferralDateOfFeedback(getCursorValue(cursor, "referral_date_of_feedback"));
        model.setReferralHivTbIntegration(getCursorValue(cursor, "referral_hiv_tb_integration"));

        model.setComment(getCursorValue(cursor, "comment"));

        return model;
    }

    private String getCursorValue(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index >= 0 && !cursor.isNull(index)) {
            return cursor.getString(index);
        }
        return null;
    }
}

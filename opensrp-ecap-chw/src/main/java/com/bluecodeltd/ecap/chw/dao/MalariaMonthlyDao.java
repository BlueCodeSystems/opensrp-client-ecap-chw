package com.bluecodeltd.ecap.chw.dao;

import android.database.Cursor;

import com.bluecodeltd.ecap.chw.model.MalariaMonthlyModel;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class MalariaMonthlyDao extends BaseRepository {

    private static final String TABLE_NAME = "ec_monthly_malaria";

    private static final String SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME;

    public MalariaMonthlyModel getMalariaMonthlyByBaseEntityId(String baseEntityId) {
        MalariaMonthlyModel model = null;
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

    public List<MalariaMonthlyModel> getAllMalariaMonthly() {
        List<MalariaMonthlyModel> list = new ArrayList<>();
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

    private MalariaMonthlyModel cursorToModel(Cursor cursor) {
        MalariaMonthlyModel model = new MalariaMonthlyModel();
        model.setBaseEntityId(getCursorValue(cursor, "base_entity_id"));
        model.setReportingMonth(getCursorValue(cursor, "reporting_month"));
        model.setReportingYear(getCursorValue(cursor, "reporting_year"));
        model.setFacilityName(getCursorValue(cursor, "facility_name"));
        model.setReporterName(getCursorValue(cursor, "reporter_name"));
        model.setReportStatus(getCursorValue(cursor, "report_status"));
        model.setProvince(getCursorValue(cursor, "province"));
        model.setDistrict(getCursorValue(cursor, "district"));
        model.setWard(getCursorValue(cursor, "ward"));
        model.setPartner(getCursorValue(cursor, "partner"));

        model.setSaQ1F04(getCursorValue(cursor, "sa_q1_f_0_4"));
        model.setSaQ1F515(getCursorValue(cursor, "sa_q1_f_5_15"));
        model.setSaQ1F1619(getCursorValue(cursor, "sa_q1_f_16_19"));
        model.setSaQ1F20Plus(getCursorValue(cursor, "sa_q1_f_20_plus"));
        model.setSaQ1FCalhiv(getCursorValue(cursor, "sa_q1_f_calhiv"));
        model.setSaQ1FHei(getCursorValue(cursor, "sa_q1_f_hei"));
        model.setSaQ1FWlhiv(getCursorValue(cursor, "sa_q1_f_wlhiv"));
        model.setSaQ1FSv(getCursorValue(cursor, "sa_q1_f_sv"));
        model.setSaQ1FAgyw(getCursorValue(cursor, "sa_q1_f_agyw"));
        model.setSaQ1FHivPos(getCursorValue(cursor, "sa_q1_f_hiv_pos"));
        model.setSaQ1FSiblings(getCursorValue(cursor, "sa_q1_f_siblings"));
        model.setSaQ1FCaregivers(getCursorValue(cursor, "sa_q1_f_caregivers"));

        model.setSaQ1M04(getCursorValue(cursor, "sa_q1_m_0_4"));
        model.setSaQ1M515(getCursorValue(cursor, "sa_q1_m_5_15"));
        model.setSaQ1M1619(getCursorValue(cursor, "sa_q1_m_16_19"));
        model.setSaQ1M20Plus(getCursorValue(cursor, "sa_q1_m_20_plus"));
        model.setSaQ1MCalhiv(getCursorValue(cursor, "sa_q1_m_calhiv"));
        model.setSaQ1MHei(getCursorValue(cursor, "sa_q1_m_hei"));
        model.setSaQ1MWlhiv(getCursorValue(cursor, "sa_q1_m_wlhiv"));
        model.setSaQ1MSv(getCursorValue(cursor, "sa_q1_m_sv"));
        model.setSaQ1MSiblings(getCursorValue(cursor, "sa_q1_m_siblings"));
        model.setSaQ1MCaregivers(getCursorValue(cursor, "sa_q1_m_caregivers"));

        model.setSaQ2F04(getCursorValue(cursor, "sa_q2_f_0_4"));
        model.setQ2F515(getCursorValue(cursor, "q2_f_5_15"));
        model.setQ2F1619(getCursorValue(cursor, "q2_f_16_19"));
        model.setQ2F20Plus(getCursorValue(cursor, "q2_f_20_plus"));
        model.setQ2FCalhiv(getCursorValue(cursor, "q2_f_calhiv"));
        model.setQ2FHei(getCursorValue(cursor, "q2_f_hei"));
        model.setQ2FWlhiv(getCursorValue(cursor, "q2_f_wlhiv"));
        model.setQ2FSv(getCursorValue(cursor, "q2_f_sv"));
        model.setQ2FAgyw(getCursorValue(cursor, "q2_f_agyw"));
        model.setQ2FHivPos(getCursorValue(cursor, "q2_f_hiv_pos"));
        model.setQ2FSiblings(getCursorValue(cursor, "q2_f_siblings"));
        model.setQ2FCaregivers(getCursorValue(cursor, "q2_f_caregivers"));

        model.setQ2M04(getCursorValue(cursor, "q2_m_0_4"));
        model.setQ2M515(getCursorValue(cursor, "q2_m_5_15"));
        model.setQ2M1619(getCursorValue(cursor, "q2_m_16_19"));
        model.setQ2M20Plus(getCursorValue(cursor, "q2_m_20_plus"));
        model.setQ2MCalhiv(getCursorValue(cursor, "q2_m_calhiv"));
        model.setQ2MHei(getCursorValue(cursor, "q2_m_hei"));
        model.setQ2MWlhiv(getCursorValue(cursor, "q2_m_wlhiv"));
        model.setQ2MSv(getCursorValue(cursor, "q2_m_sv"));
        model.setQ2MSiblings(getCursorValue(cursor, "q2_m_siblings"));
        model.setQ2MCaregivers(getCursorValue(cursor, "q2_m_caregivers"));

        model.setQ3F04(getCursorValue(cursor, "q3_f_0_4"));
        model.setQ3F515(getCursorValue(cursor, "q3_f_5_15"));
        model.setQ3F1619(getCursorValue(cursor, "q3_f_16_19"));
        model.setQ3F20Plus(getCursorValue(cursor, "q3_f_20_plus"));
        model.setQ3FCalhiv(getCursorValue(cursor, "q3_f_calhiv"));
        model.setQ3FHei(getCursorValue(cursor, "q3_f_hei"));
        model.setQ3FWlhiv(getCursorValue(cursor, "q3_f_wlhiv"));
        model.setQ3FSv(getCursorValue(cursor, "q3_f_sv"));
        model.setQ3FAgyw(getCursorValue(cursor, "q3_f_agyw"));
        model.setQ3FHivPos(getCursorValue(cursor, "q3_f_hiv_pos"));
        model.setQ3FSiblings(getCursorValue(cursor, "q3_f_siblings"));
        model.setQ3FCaregivers(getCursorValue(cursor, "q3_f_caregivers"));

        model.setQ3M04(getCursorValue(cursor, "q3_m_0_4"));
        model.setQ3M515(getCursorValue(cursor, "q3_m_5_15"));
        model.setQ3M1619(getCursorValue(cursor, "q3_m_16_19"));
        model.setQ3M20Plus(getCursorValue(cursor, "q3_m_20_plus"));
        model.setQ3MCalhiv(getCursorValue(cursor, "q3_m_calhiv"));
        model.setQ3MHei(getCursorValue(cursor, "q3_m_hei"));
        model.setQ3MWlhiv(getCursorValue(cursor, "q3_m_wlhiv"));
        model.setQ3MSv(getCursorValue(cursor, "q3_m_sv"));
        model.setQ3MSiblings(getCursorValue(cursor, "q3_m_siblings"));
        model.setQ3MCaregivers(getCursorValue(cursor, "q3_m_caregivers"));

        // Section A Q4-Q10
        model.setQ4F04(getCursorValue(cursor, "q4_f_0_4"));
        model.setQ4F515(getCursorValue(cursor, "q4_f_5_15"));
        model.setQ4F1619(getCursorValue(cursor, "q4_f_16_19"));
        model.setQ4F20Plus(getCursorValue(cursor, "q4_f_20_plus"));
        model.setQ4FCalhiv(getCursorValue(cursor, "q4_f_calhiv"));
        model.setQ4FHei(getCursorValue(cursor, "q4_f_hei"));
        model.setQ4FWlhiv(getCursorValue(cursor, "q4_f_wlhiv"));
        model.setQ4FSv(getCursorValue(cursor, "q4_f_sv"));
        model.setQ4FAgyw(getCursorValue(cursor, "q4_f_agyw"));
        model.setQ4FHivPos(getCursorValue(cursor, "q4_f_hiv_pos"));
        model.setQ4FSiblings(getCursorValue(cursor, "q4_f_siblings"));
        model.setQ4FCaregivers(getCursorValue(cursor, "q4_f_caregivers"));

        model.setQ4M04(getCursorValue(cursor, "q4_m_0_4"));
        model.setQ4M515(getCursorValue(cursor, "q4_m_5_15"));
        model.setQ4M1619(getCursorValue(cursor, "q4_m_16_19"));
        model.setQ4M20Plus(getCursorValue(cursor, "q4_m_20_plus"));
        model.setQ4MCalhiv(getCursorValue(cursor, "q4_m_calhiv"));
        model.setQ4MHei(getCursorValue(cursor, "q4_m_hei"));
        model.setQ4MWlhiv(getCursorValue(cursor, "q4_m_wlhiv"));
        model.setQ4MSv(getCursorValue(cursor, "q4_m_sv"));
        model.setQ4MSiblings(getCursorValue(cursor, "q4_m_siblings"));
        model.setQ4MCaregivers(getCursorValue(cursor, "q4_m_caregivers"));

        model.setQ5F04(getCursorValue(cursor, "q5_f_0_4"));
        model.setQ5F515(getCursorValue(cursor, "q5_f_5_15"));
        model.setQ5F1619(getCursorValue(cursor, "q5_f_16_19"));
        model.setQ5F20Plus(getCursorValue(cursor, "q5_f_20_plus"));
        model.setQ5FCalhiv(getCursorValue(cursor, "q5_f_calhiv"));
        model.setQ5FHei(getCursorValue(cursor, "q5_f_hei"));
        model.setQ5FWlhiv(getCursorValue(cursor, "q5_f_wlhiv"));
        model.setQ5FSv(getCursorValue(cursor, "q5_f_sv"));
        model.setQ5FAgyw(getCursorValue(cursor, "q5_f_agyw"));
        model.setQ5FHivPos(getCursorValue(cursor, "q5_f_hiv_pos"));
        model.setQ5FSiblings(getCursorValue(cursor, "q5_f_siblings"));
        model.setQ5FCaregivers(getCursorValue(cursor, "q5_f_caregivers"));

        model.setQ5M04(getCursorValue(cursor, "q5_m_0_4"));
        model.setQ5M515(getCursorValue(cursor, "q5_m_5_15"));
        model.setQ5M1619(getCursorValue(cursor, "q5_m_16_19"));
        model.setQ5M20Plus(getCursorValue(cursor, "q5_m_20_plus"));
        model.setQ5MCalhiv(getCursorValue(cursor, "q5_m_calhiv"));
        model.setQ5MHei(getCursorValue(cursor, "q5_m_hei"));
        model.setQ5MWlhiv(getCursorValue(cursor, "q5_m_wlhiv"));
        model.setQ5MSv(getCursorValue(cursor, "q5_m_sv"));
        model.setQ5MSiblings(getCursorValue(cursor, "q5_m_siblings"));
        model.setQ5MCaregivers(getCursorValue(cursor, "q5_m_caregivers"));

        model.setQ6F04(getCursorValue(cursor, "q6_f_0_4"));
        model.setQ6F515(getCursorValue(cursor, "q6_f_5_15"));
        model.setQ6F1619(getCursorValue(cursor, "q6_f_16_19"));
        model.setQ6F20Plus(getCursorValue(cursor, "q6_f_20_plus"));
        model.setQ6FCalhiv(getCursorValue(cursor, "q6_f_calhiv"));
        model.setQ6FHei(getCursorValue(cursor, "q6_f_hei"));
        model.setQ6FWlhiv(getCursorValue(cursor, "q6_f_wlhiv"));
        model.setQ6FSv(getCursorValue(cursor, "q6_f_sv"));
        model.setQ6FAgyw(getCursorValue(cursor, "q6_f_agyw"));
        model.setQ6FHivPos(getCursorValue(cursor, "q6_f_hiv_pos"));
        model.setQ6FSiblings(getCursorValue(cursor, "q6_f_siblings"));
        model.setQ6FCaregivers(getCursorValue(cursor, "q6_f_caregivers"));

        model.setQ6M04(getCursorValue(cursor, "q6_m_0_4"));
        model.setQ6M515(getCursorValue(cursor, "q6_m_5_15"));
        model.setQ6M1619(getCursorValue(cursor, "q6_m_16_19"));
        model.setQ6M20Plus(getCursorValue(cursor, "q6_m_20_plus"));
        model.setQ6MCalhiv(getCursorValue(cursor, "q6_m_calhiv"));
        model.setQ6MHei(getCursorValue(cursor, "q6_m_hei"));
        model.setQ6MWlhiv(getCursorValue(cursor, "q6_m_wlhiv"));
        model.setQ6MSv(getCursorValue(cursor, "q6_m_sv"));
        model.setQ6MSiblings(getCursorValue(cursor, "q6_m_siblings"));
        model.setQ6MCaregivers(getCursorValue(cursor, "q6_m_caregivers"));

        model.setQ7F04(getCursorValue(cursor, "q7_f_0_4"));
        model.setQ7F515(getCursorValue(cursor, "q7_f_5_15"));
        model.setQ7F1619(getCursorValue(cursor, "q7_f_16_19"));
        model.setQ7F20Plus(getCursorValue(cursor, "q7_f_20_plus"));
        model.setQ7FCalhiv(getCursorValue(cursor, "q7_f_calhiv"));
        model.setQ7FHei(getCursorValue(cursor, "q7_f_hei"));
        model.setQ7FWlhiv(getCursorValue(cursor, "q7_f_wlhiv"));
        model.setQ7FSv(getCursorValue(cursor, "q7_f_sv"));
        model.setQ7FAgyw(getCursorValue(cursor, "q7_f_agyw"));
        model.setQ7FHivPos(getCursorValue(cursor, "q7_f_hiv_pos"));
        model.setQ7FSiblings(getCursorValue(cursor, "q7_f_siblings"));
        model.setQ7FCaregivers(getCursorValue(cursor, "q7_f_caregivers"));

        model.setQ7M04(getCursorValue(cursor, "q7_m_0_4"));
        model.setQ7M515(getCursorValue(cursor, "q7_m_5_15"));
        model.setQ7M1619(getCursorValue(cursor, "q7_m_16_19"));
        model.setQ7M20Plus(getCursorValue(cursor, "q7_m_20_plus"));
        model.setQ7MCalhiv(getCursorValue(cursor, "q7_m_calhiv"));
        model.setQ7MHei(getCursorValue(cursor, "q7_m_hei"));
        model.setQ7MWlhiv(getCursorValue(cursor, "q7_m_wlhiv"));
        model.setQ7MSv(getCursorValue(cursor, "q7_m_sv"));
        model.setQ7MSiblings(getCursorValue(cursor, "q7_m_siblings"));
        model.setQ7MCaregivers(getCursorValue(cursor, "q7_m_caregivers"));

        model.setQ8F04(getCursorValue(cursor, "q8_f_0_4"));
        model.setQ8F515(getCursorValue(cursor, "q8_f_5_15"));
        model.setQ8F1619(getCursorValue(cursor, "q8_f_16_19"));
        model.setQ8F20Plus(getCursorValue(cursor, "q8_f_20_plus"));
        model.setQ8FCalhiv(getCursorValue(cursor, "q8_f_calhiv"));
        model.setQ8FHei(getCursorValue(cursor, "q8_f_hei"));
        model.setQ8FWlhiv(getCursorValue(cursor, "q8_f_wlhiv"));
        model.setQ8FSv(getCursorValue(cursor, "q8_f_sv"));
        model.setQ8FAgyw(getCursorValue(cursor, "q8_f_agyw"));
        model.setQ8FHivPos(getCursorValue(cursor, "q8_f_hiv_pos"));
        model.setQ8FSiblings(getCursorValue(cursor, "q8_f_siblings"));
        model.setQ8FCaregivers(getCursorValue(cursor, "q8_f_caregivers"));

        model.setQ8M04(getCursorValue(cursor, "q8_m_0_4"));
        model.setQ8M515(getCursorValue(cursor, "q8_m_5_15"));
        model.setQ8M1619(getCursorValue(cursor, "q8_m_16_19"));
        model.setQ8M20Plus(getCursorValue(cursor, "q8_m_20_plus"));
        model.setQ8MCalhiv(getCursorValue(cursor, "q8_m_calhiv"));
        model.setQ8MHei(getCursorValue(cursor, "q8_m_hei"));
        model.setQ8MWlhiv(getCursorValue(cursor, "q8_m_wlhiv"));
        model.setQ8MSv(getCursorValue(cursor, "q8_m_sv"));
        model.setQ8MSiblings(getCursorValue(cursor, "q8_m_siblings"));
        model.setQ8MCaregivers(getCursorValue(cursor, "q8_m_caregivers"));

        model.setQ9F04(getCursorValue(cursor, "q9_f_0_4"));
        model.setQ9F515(getCursorValue(cursor, "q9_f_5_15"));
        model.setQ9F1619(getCursorValue(cursor, "q9_f_16_19"));
        model.setQ9F20Plus(getCursorValue(cursor, "q9_f_20_plus"));
        model.setQ9FCalhiv(getCursorValue(cursor, "q9_f_calhiv"));
        model.setQ9FHei(getCursorValue(cursor, "q9_f_hei"));
        model.setQ9FWlhiv(getCursorValue(cursor, "q9_f_wlhiv"));
        model.setQ9FSv(getCursorValue(cursor, "q9_f_sv"));
        model.setQ9FAgyw(getCursorValue(cursor, "q9_f_agyw"));
        model.setQ9FHivPos(getCursorValue(cursor, "q9_f_hiv_pos"));
        model.setQ9FSiblings(getCursorValue(cursor, "q9_f_siblings"));
        model.setQ9FCaregivers(getCursorValue(cursor, "q9_f_caregivers"));

        model.setQ9M04(getCursorValue(cursor, "q9_m_0_4"));
        model.setQ9M515(getCursorValue(cursor, "q9_m_5_15"));
        model.setQ9M1619(getCursorValue(cursor, "q9_m_16_19"));
        model.setQ9M20Plus(getCursorValue(cursor, "q9_m_20_plus"));
        model.setQ9MCalhiv(getCursorValue(cursor, "q9_m_calhiv"));
        model.setQ9MHei(getCursorValue(cursor, "q9_m_hei"));
        model.setQ9MWlhiv(getCursorValue(cursor, "q9_m_wlhiv"));
        model.setQ9MSv(getCursorValue(cursor, "q9_m_sv"));
        model.setQ9MSiblings(getCursorValue(cursor, "q9_m_siblings"));
        model.setQ9MCaregivers(getCursorValue(cursor, "q9_m_caregivers"));

        model.setQ10F04(getCursorValue(cursor, "q10_f_0_4"));
        model.setQ10F515(getCursorValue(cursor, "q10_f_5_15"));
        model.setQ10F1619(getCursorValue(cursor, "q10_f_16_19"));
        model.setQ10F20Plus(getCursorValue(cursor, "q10_f_20_plus"));
        model.setQ10FCalhiv(getCursorValue(cursor, "q10_f_calhiv"));
        model.setQ10FHei(getCursorValue(cursor, "q10_f_hei"));
        model.setQ10FWlhiv(getCursorValue(cursor, "q10_f_wlhiv"));
        model.setQ10FSv(getCursorValue(cursor, "q10_f_sv"));
        model.setQ10FAgyw(getCursorValue(cursor, "q10_f_agyw"));
        model.setQ10FHivPos(getCursorValue(cursor, "q10_f_hiv_pos"));
        model.setQ10FSiblings(getCursorValue(cursor, "q10_f_siblings"));
        model.setQ10FCaregivers(getCursorValue(cursor, "q10_f_caregivers"));

        model.setQ10M04(getCursorValue(cursor, "q10_m_0_4"));
        model.setQ10M515(getCursorValue(cursor, "q10_m_5_15"));
        model.setQ10M1619(getCursorValue(cursor, "q10_m_16_19"));
        model.setQ10M20Plus(getCursorValue(cursor, "q10_m_20_plus"));
        model.setQ10MCalhiv(getCursorValue(cursor, "q10_m_calhiv"));
        model.setQ10MHei(getCursorValue(cursor, "q10_m_hei"));
        model.setQ10MWlhiv(getCursorValue(cursor, "q10_m_wlhiv"));
        model.setQ10MSv(getCursorValue(cursor, "q10_m_sv"));
        model.setQ10MSiblings(getCursorValue(cursor, "q10_m_siblings"));
        model.setQ10MCaregivers(getCursorValue(cursor, "q10_m_caregivers"));

        model.setSbQ1F04(getCursorValue(cursor, "sb_q1_f_0_4"));
        model.setSbQ1F515(getCursorValue(cursor, "sb_q1_f_5_15"));
        model.setSbQ1F1619(getCursorValue(cursor, "sb_q1_f_16_19"));
        model.setSbQ1F20Plus(getCursorValue(cursor, "sb_q1_f_20_plus"));
        model.setSbQ1M04(getCursorValue(cursor, "sb_q1_m_0_4"));
        model.setSbQ1M515(getCursorValue(cursor, "sb_q1_m_5_15"));
        model.setSbQ1M1619(getCursorValue(cursor, "sb_q1_m_16_19"));
        model.setSbQ1M20Plus(getCursorValue(cursor, "sb_q1_m_20_plus"));

        model.setSbQ2F04(getCursorValue(cursor, "sb_q2_f_0_4"));
        model.setSbQ2F515(getCursorValue(cursor, "sb_q2_f_5_15"));
        model.setSbQ2F1619(getCursorValue(cursor, "sb_q2_f_16_19"));
        model.setSbQ2F20Plus(getCursorValue(cursor, "sb_q2_f_20_plus"));
        model.setSbQ2M04(getCursorValue(cursor, "sb_q2_m_0_4"));
        model.setSbQ2M515(getCursorValue(cursor, "sb_q2_m_5_15"));
        model.setSbQ2M1619(getCursorValue(cursor, "sb_q2_m_16_19"));
        model.setSbQ2M20Plus(getCursorValue(cursor, "sb_q2_m_20_plus"));

        model.setSbQ3F04(getCursorValue(cursor, "sb_q3_f_0_4"));
        model.setSbQ3F515(getCursorValue(cursor, "sb_q3_f_5_15"));
        model.setSbQ3F1619(getCursorValue(cursor, "sb_q3_f_16_19"));
        model.setSbQ3F20Plus(getCursorValue(cursor, "sb_q3_f_20_plus"));
        model.setSbQ3M04(getCursorValue(cursor, "sb_q3_m_0_4"));
        model.setSbQ3M515(getCursorValue(cursor, "sb_q3_m_5_15"));
        model.setSbQ3M1619(getCursorValue(cursor, "sb_q3_m_16_19"));
        model.setSbQ3M20Plus(getCursorValue(cursor, "sb_q3_m_20_plus"));

        // Section B Q4-Q10
        model.setSbQ4F04(getCursorValue(cursor, "sb_q4_f_0_4"));
        model.setSbQ4F515(getCursorValue(cursor, "sb_q4_f_5_15"));
        model.setSbQ4F1619(getCursorValue(cursor, "sb_q4_f_16_19"));
        model.setSbQ4F20Plus(getCursorValue(cursor, "sb_q4_f_20_plus"));
        model.setSbQ4M04(getCursorValue(cursor, "sb_q4_m_0_4"));
        model.setSbQ4M515(getCursorValue(cursor, "sb_q4_m_5_15"));
        model.setSbQ4M1619(getCursorValue(cursor, "sb_q4_m_16_19"));
        model.setSbQ4M20Plus(getCursorValue(cursor, "sb_q4_m_20_plus"));

        model.setSbQ5F04(getCursorValue(cursor, "sb_q5_f_0_4"));
        model.setSbQ5F515(getCursorValue(cursor, "sb_q5_f_5_15"));
        model.setSbQ5F1619(getCursorValue(cursor, "sb_q5_f_16_19"));
        model.setSbQ5F20Plus(getCursorValue(cursor, "sb_q5_f_20_plus"));
        model.setSbQ5M04(getCursorValue(cursor, "sb_q5_m_0_4"));
        model.setSbQ5M515(getCursorValue(cursor, "sb_q5_m_5_15"));
        model.setSbQ5M1619(getCursorValue(cursor, "sb_q5_m_16_19"));
        model.setSbQ5M20Plus(getCursorValue(cursor, "sb_q5_m_20_plus"));

        model.setSbQ6F04(getCursorValue(cursor, "sb_q6_f_0_4"));
        model.setSbQ6F515(getCursorValue(cursor, "sb_q6_f_5_15"));
        model.setSbQ6F1619(getCursorValue(cursor, "sb_q6_f_16_19"));
        model.setSbQ6F20Plus(getCursorValue(cursor, "sb_q6_f_20_plus"));
        model.setSbQ6M04(getCursorValue(cursor, "sb_q6_m_0_4"));
        model.setSbQ6M515(getCursorValue(cursor, "sb_q6_m_5_15"));
        model.setSbQ6M1619(getCursorValue(cursor, "sb_q6_m_16_19"));
        model.setSbQ6M20Plus(getCursorValue(cursor, "sb_q6_m_20_plus"));

        model.setSbQ7F04(getCursorValue(cursor, "sb_q7_f_0_4"));
        model.setSbQ7F515(getCursorValue(cursor, "sb_q7_f_5_15"));
        model.setSbQ7F1619(getCursorValue(cursor, "sb_q7_f_16_19"));
        model.setSbQ7F20Plus(getCursorValue(cursor, "sb_q7_f_20_plus"));
        model.setSbQ7M04(getCursorValue(cursor, "sb_q7_m_0_4"));
        model.setSbQ7M515(getCursorValue(cursor, "sb_q7_m_5_15"));
        model.setSbQ7M1619(getCursorValue(cursor, "sb_q7_m_16_19"));
        model.setSbQ7M20Plus(getCursorValue(cursor, "sb_q7_m_20_plus"));

        model.setSbQ8F04(getCursorValue(cursor, "sb_q8_f_0_4"));
        model.setSbQ8F515(getCursorValue(cursor, "sb_q8_f_5_15"));
        model.setSbQ8F1619(getCursorValue(cursor, "sb_q8_f_16_19"));
        model.setSbQ8F20Plus(getCursorValue(cursor, "sb_q8_f_20_plus"));
        model.setSbQ8M04(getCursorValue(cursor, "sb_q8_m_0_4"));
        model.setSbQ8M515(getCursorValue(cursor, "sb_q8_m_5_15"));
        model.setSbQ8M1619(getCursorValue(cursor, "sb_q8_m_16_19"));
        model.setSbQ8M20Plus(getCursorValue(cursor, "sb_q8_m_20_plus"));

        model.setSbQ9F04(getCursorValue(cursor, "sb_q9_f_0_4"));
        model.setSbQ9F515(getCursorValue(cursor, "sb_q9_f_5_15"));
        model.setSbQ9F1619(getCursorValue(cursor, "sb_q9_f_16_19"));
        model.setSbQ9F20Plus(getCursorValue(cursor, "sb_q9_f_20_plus"));
        model.setSbQ9M04(getCursorValue(cursor, "sb_q9_m_0_4"));
        model.setSbQ9M515(getCursorValue(cursor, "sb_q9_m_5_15"));
        model.setSbQ9M1619(getCursorValue(cursor, "sb_q9_m_16_19"));
        model.setSbQ9M20Plus(getCursorValue(cursor, "sb_q9_m_20_plus"));

        model.setSbQ10F04(getCursorValue(cursor, "sb_q10_f_0_4"));
        model.setSbQ10F515(getCursorValue(cursor, "sb_q10_f_5_15"));
        model.setSbQ10F1619(getCursorValue(cursor, "sb_q10_f_16_19"));
        model.setSbQ10F20Plus(getCursorValue(cursor, "sb_q10_f_20_plus"));
        model.setSbQ10M04(getCursorValue(cursor, "sb_q10_m_0_4"));
        model.setSbQ10M515(getCursorValue(cursor, "sb_q10_m_5_15"));
        model.setSbQ10M1619(getCursorValue(cursor, "sb_q10_m_16_19"));
        model.setSbQ10M20Plus(getCursorValue(cursor, "sb_q10_m_20_plus"));

        model.setScQ1(getCursorValue(cursor, "sc_q1"));
        model.setScQ2(getCursorValue(cursor, "sc_q2"));
        model.setScQ3(getCursorValue(cursor, "sc_q3"));
        model.setScQ4(getCursorValue(cursor, "sc_q4"));
        model.setScQ5(getCursorValue(cursor, "sc_q5"));
        model.setScQ6(getCursorValue(cursor, "sc_q6"));
        model.setScQ7(getCursorValue(cursor, "sc_q7"));
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

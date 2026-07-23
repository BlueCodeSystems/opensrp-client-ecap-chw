package com.bluecodeltd.ecap.chw.dao;

import android.database.Cursor;
import com.bluecodeltd.ecap.chw.model.TbMonthlyModel;
import org.smartregister.repository.BaseRepository;
import java.util.ArrayList;
import java.util.List;

public class TbMonthlyDao extends BaseRepository {

    private static final String TABLE_NAME = "ec_monthly_tb";

    private static final String SELECT_ALL =
            "SELECT * FROM " + TABLE_NAME;

    public TbMonthlyModel getTbMonthlyByBaseEntityId(String baseEntityId) {
        TbMonthlyModel model = null;
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

    public List<TbMonthlyModel> getAllTbMonthly() {
        List<TbMonthlyModel> list = new ArrayList<>();
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

    private TbMonthlyModel cursorToModel(Cursor cursor) {
        TbMonthlyModel model = new TbMonthlyModel();
        model.setBaseEntityId(getCursorValue(cursor, "base_entity_id"));
        model.setReportingMonth(getCursorValue(cursor, "reporting_month"));
        model.setReportingYear(getCursorValue(cursor, "reporting_year"));
        model.setFacilityName(getCursorValue(cursor, "facility"));
        model.setReportStatus(getCursorValue(cursor, "report_status"));
        model.setComment(getCursorValue(cursor, "comment"));
        model.setProvince(getCursorValue(cursor, "province"));
        model.setDistrict(getCursorValue(cursor, "district"));
        model.setWard(getCursorValue(cursor, "ward"));
        model.setPartner(getCursorValue(cursor, "partner"));

        // Q1
        model.setQ1FLt1(getCursorValue(cursor, "q1_f_lt_1"));
        model.setQ1F14(getCursorValue(cursor, "q1_f_1_4"));
        model.setQ1F59(getCursorValue(cursor, "q1_f_5_9"));
        model.setQ1F1014(getCursorValue(cursor, "q1_f_10_14"));
        model.setQ1F1519(getCursorValue(cursor, "q1_f_15_19"));
        model.setQ1F20Plus(getCursorValue(cursor, "q1_f_20_plus"));
        model.setQ1FPc18Plus(getCursorValue(cursor, "q1_f_pc_18_plus"));
        model.setQ1FTotal(getCursorValue(cursor, "q1_f_total"));
        model.setQ1MLt1(getCursorValue(cursor, "q1_m_lt_1"));
        model.setQ1M14(getCursorValue(cursor, "q1_m_1_4"));
        model.setQ1M59(getCursorValue(cursor, "q1_m_5_9"));
        model.setQ1M1014(getCursorValue(cursor, "q1_m_10_14"));
        model.setQ1M1519(getCursorValue(cursor, "q1_m_15_19"));
        model.setQ1M20Plus(getCursorValue(cursor, "q1_m_20_plus"));
        model.setQ1MPc18Plus(getCursorValue(cursor, "q1_m_pc_18_plus"));
        model.setQ1MTotal(getCursorValue(cursor, "q1_m_total"));
        model.setQ1SubHei(getCursorValue(cursor, "q1_hei"));
        model.setQ1SubCalhiv(getCursorValue(cursor, "q1_calhiv"));
        model.setQ1SubCPlhiv(getCursorValue(cursor, "q1_wlhiv"));
        model.setQ1SubPcLhiv(getCursorValue(cursor, "q1_pc_lhiv"));
        model.setQ1SubOther(getCursorValue(cursor, "q1_other"));
        model.setQ1SubTotal(getCursorValue(cursor, "q1_subpop_total"));

        // Q2
        model.setQ2FLt1(getCursorValue(cursor, "q2_f_lt_1"));
        model.setQ2F14(getCursorValue(cursor, "q2_f_1_4"));
        model.setQ2F59(getCursorValue(cursor, "q2_f_5_9"));
        model.setQ2F1014(getCursorValue(cursor, "q2_f_10_14"));
        model.setQ2F1519(getCursorValue(cursor, "q2_f_15_19"));
        model.setQ2F20Plus(getCursorValue(cursor, "q2_f_20_plus"));
        model.setQ2FPc18Plus(getCursorValue(cursor, "q2_f_pc_18_plus"));
        model.setQ2FTotal(getCursorValue(cursor, "q2_f_total"));
        model.setQ2MLt1(getCursorValue(cursor, "q2_m_lt_1"));
        model.setQ2M14(getCursorValue(cursor, "q2_m_1_4"));
        model.setQ2M59(getCursorValue(cursor, "q2_m_5_9"));
        model.setQ2M1014(getCursorValue(cursor, "q2_m_10_14"));
        model.setQ2M1519(getCursorValue(cursor, "q2_m_15_19"));
        model.setQ2M20Plus(getCursorValue(cursor, "q2_m_20_plus"));
        model.setQ2MPc18Plus(getCursorValue(cursor, "q2_m_pc_18_plus"));
        model.setQ2MTotal(getCursorValue(cursor, "q2_m_total"));
        model.setQ2SubHei(getCursorValue(cursor, "q2_hei"));
        model.setQ2SubCalhiv(getCursorValue(cursor, "q2_calhiv"));
        model.setQ2SubCPlhiv(getCursorValue(cursor, "q2_wlhiv"));
        model.setQ2SubPcLhiv(getCursorValue(cursor, "q2_pc_lhiv"));
        model.setQ2SubOther(getCursorValue(cursor, "q2_other"));
        model.setQ2SubTotal(getCursorValue(cursor, "q2_subpop_total"));

        // Q3
        model.setQ3FLt1(getCursorValue(cursor, "q3_f_lt_1"));
        model.setQ3F14(getCursorValue(cursor, "q3_f_1_4"));
        model.setQ3F59(getCursorValue(cursor, "q3_f_5_9"));
        model.setQ3F1014(getCursorValue(cursor, "q3_f_10_14"));
        model.setQ3F1519(getCursorValue(cursor, "q3_f_15_19"));
        model.setQ3F20Plus(getCursorValue(cursor, "q3_f_20_plus"));
        model.setQ3FPc18Plus(getCursorValue(cursor, "q3_f_pc_18_plus"));
        model.setQ3FTotal(getCursorValue(cursor, "q3_f_total"));
        model.setQ3MLt1(getCursorValue(cursor, "q3_m_lt_1"));
        model.setQ3M14(getCursorValue(cursor, "q3_m_1_4"));
        model.setQ3M59(getCursorValue(cursor, "q3_m_5_9"));
        model.setQ3M1014(getCursorValue(cursor, "q3_m_10_14"));
        model.setQ3M1519(getCursorValue(cursor, "q3_m_15_19"));
        model.setQ3M20Plus(getCursorValue(cursor, "q3_m_20_plus"));
        model.setQ3MPc18Plus(getCursorValue(cursor, "q3_m_pc_18_plus"));
        model.setQ3MTotal(getCursorValue(cursor, "q3_m_total"));
        model.setQ3SubHei(getCursorValue(cursor, "q3_hei"));
        model.setQ3SubCalhiv(getCursorValue(cursor, "q3_calhiv"));
        model.setQ3SubCPlhiv(getCursorValue(cursor, "q3_wlhiv"));
        model.setQ3SubPcLhiv(getCursorValue(cursor, "q3_pc_lhiv"));
        model.setQ3SubOther(getCursorValue(cursor, "q3_other"));
        model.setQ3SubTotal(getCursorValue(cursor, "q3_subpop_total"));

        // Q4
        model.setQ4FLt1(getCursorValue(cursor, "q4_f_lt_1"));
        model.setQ4F14(getCursorValue(cursor, "q4_f_1_4"));
        model.setQ4F59(getCursorValue(cursor, "q4_f_5_9"));
        model.setQ4F1014(getCursorValue(cursor, "q4_f_10_14"));
        model.setQ4F1519(getCursorValue(cursor, "q4_f_15_19"));
        model.setQ4F20Plus(getCursorValue(cursor, "q4_f_20_plus"));
        model.setQ4FPc18Plus(getCursorValue(cursor, "q4_f_pc_18_plus"));
        model.setQ4FTotal(getCursorValue(cursor, "q4_f_total"));
        model.setQ4MLt1(getCursorValue(cursor, "q4_m_lt_1"));
        model.setQ4M14(getCursorValue(cursor, "q4_m_1_4"));
        model.setQ4M59(getCursorValue(cursor, "q4_m_5_9"));
        model.setQ4M1014(getCursorValue(cursor, "q4_m_10_14"));
        model.setQ4M1519(getCursorValue(cursor, "q4_m_15_19"));
        model.setQ4M20Plus(getCursorValue(cursor, "q4_m_20_plus"));
        model.setQ4MPc18Plus(getCursorValue(cursor, "q4_m_pc_18_plus"));
        model.setQ4MTotal(getCursorValue(cursor, "q4_m_total"));
        model.setQ4SubHei(getCursorValue(cursor, "q4_hei"));
        model.setQ4SubCalhiv(getCursorValue(cursor, "q4_calhiv"));
        model.setQ4SubCPlhiv(getCursorValue(cursor, "q4_wlhiv"));
        model.setQ4SubPcLhiv(getCursorValue(cursor, "q4_pc_lhiv"));
        model.setQ4SubOther(getCursorValue(cursor, "q4_other"));
        model.setQ4SubTotal(getCursorValue(cursor, "q4_subpop_total"));

        // Q5
        model.setQ5FLt1(getCursorValue(cursor, "q5_f_lt_1"));
        model.setQ5F14(getCursorValue(cursor, "q5_f_1_4"));
        model.setQ5F59(getCursorValue(cursor, "q5_f_5_9"));
        model.setQ5F1014(getCursorValue(cursor, "q5_f_10_14"));
        model.setQ5F1519(getCursorValue(cursor, "q5_f_15_19"));
        model.setQ5F20Plus(getCursorValue(cursor, "q5_f_20_plus"));
        model.setQ5FPc18Plus(getCursorValue(cursor, "q5_f_pc_18_plus"));
        model.setQ5FTotal(getCursorValue(cursor, "q5_f_total"));
        model.setQ5MLt1(getCursorValue(cursor, "q5_m_lt_1"));
        model.setQ5M14(getCursorValue(cursor, "q5_m_1_4"));
        model.setQ5M59(getCursorValue(cursor, "q5_m_5_9"));
        model.setQ5M1014(getCursorValue(cursor, "q5_m_10_14"));
        model.setQ5M1519(getCursorValue(cursor, "q5_m_15_19"));
        model.setQ5M20Plus(getCursorValue(cursor, "q5_m_20_plus"));
        model.setQ5MPc18Plus(getCursorValue(cursor, "q5_m_pc_18_plus"));
        model.setQ5MTotal(getCursorValue(cursor, "q5_m_total"));
        model.setQ5SubHei(getCursorValue(cursor, "q5_hei"));
        model.setQ5SubCalhiv(getCursorValue(cursor, "q5_calhiv"));
        model.setQ5SubCPlhiv(getCursorValue(cursor, "q5_wlhiv"));
        model.setQ5SubPcLhiv(getCursorValue(cursor, "q5_pc_lhiv"));
        model.setQ5SubTotal(getCursorValue(cursor, "q5_subpop_total"));

        // Q6
        model.setQ6FLt1(getCursorValue(cursor, "q6_f_lt_1"));
        model.setQ6F14(getCursorValue(cursor, "q6_f_1_4"));
        model.setQ6F59(getCursorValue(cursor, "q6_f_5_9"));
        model.setQ6F1014(getCursorValue(cursor, "q6_f_10_14"));
        model.setQ6F1519(getCursorValue(cursor, "q6_f_15_19"));
        model.setQ6F20Plus(getCursorValue(cursor, "q6_f_20_plus"));
        model.setQ6FPc18Plus(getCursorValue(cursor, "q6_f_pc_18_plus"));
        model.setQ6FTotal(getCursorValue(cursor, "q6_f_total"));
        model.setQ6MLt1(getCursorValue(cursor, "q6_m_lt_1"));
        model.setQ6M14(getCursorValue(cursor, "q6_m_1_4"));
        model.setQ6M59(getCursorValue(cursor, "q6_m_5_9"));
        model.setQ6M1014(getCursorValue(cursor, "q6_m_10_14"));
        model.setQ6M1519(getCursorValue(cursor, "q6_m_15_19"));
        model.setQ6M20Plus(getCursorValue(cursor, "q6_m_20_plus"));
        model.setQ6MPc18Plus(getCursorValue(cursor, "q6_m_pc_18_plus"));
        model.setQ6MTotal(getCursorValue(cursor, "q6_m_total"));
        model.setQ6SubHei(getCursorValue(cursor, "q6_hei"));
        model.setQ6SubCalhiv(getCursorValue(cursor, "q6_calhiv"));
        model.setQ6SubCPlhiv(getCursorValue(cursor, "q6_wlhiv"));
        model.setQ6SubPcLhiv(getCursorValue(cursor, "q6_pc_lhiv"));
        model.setQ6SubOther(getCursorValue(cursor, "q6_other"));
        model.setQ6SubTotal(getCursorValue(cursor, "q6_subpop_total"));

        // Q7
        model.setQ7FLt1(getCursorValue(cursor, "q7_f_lt_1"));
        model.setQ7F14(getCursorValue(cursor, "q7_f_1_4"));
        model.setQ7F59(getCursorValue(cursor, "q7_f_5_9"));
        model.setQ7F1014(getCursorValue(cursor, "q7_f_10_14"));
        model.setQ7F1519(getCursorValue(cursor, "q7_f_15_19"));
        model.setQ7F20Plus(getCursorValue(cursor, "q7_f_20_plus"));
        model.setQ7FPc18Plus(getCursorValue(cursor, "q7_f_pc_18_plus"));
        model.setQ7FTotal(getCursorValue(cursor, "q7_f_total"));
        model.setQ7MLt1(getCursorValue(cursor, "q7_m_lt_1"));
        model.setQ7M14(getCursorValue(cursor, "q7_m_1_4"));
        model.setQ7M59(getCursorValue(cursor, "q7_m_5_9"));
        model.setQ7M1014(getCursorValue(cursor, "q7_m_10_14"));
        model.setQ7M1519(getCursorValue(cursor, "q7_m_15_19"));
        model.setQ7M20Plus(getCursorValue(cursor, "q7_m_20_plus"));
        model.setQ7MPc18Plus(getCursorValue(cursor, "q7_m_pc_18_plus"));
        model.setQ7MTotal(getCursorValue(cursor, "q7_m_total"));
        model.setQ7SubHei(getCursorValue(cursor, "q7_hei"));
        model.setQ7SubCalhiv(getCursorValue(cursor, "q7_calhiv"));
        model.setQ7SubCPlhiv(getCursorValue(cursor, "q7_wlhiv"));
        model.setQ7SubPcLhiv(getCursorValue(cursor, "q7_pc_lhiv"));
        model.setQ7SubTotal(getCursorValue(cursor, "q7_subpop_total"));

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

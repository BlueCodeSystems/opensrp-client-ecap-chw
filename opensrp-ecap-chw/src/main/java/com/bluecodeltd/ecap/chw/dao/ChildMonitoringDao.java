package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.ChildMonitoringModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class ChildMonitoringDao  extends AbstractDao {
    public static List<ChildMonitoringModel> getPmctChildMonitoringList(String uniqueID) {

        String sql = "SELECT * FROM ec_pmtct_child_monitoring WHERE unique_id = '" + uniqueID + "' ";

        List<ChildMonitoringModel> values = AbstractDao.readData(sql, getChildMonitoringModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static ChildMonitoringModel getPMCTChildMonitoring(String uniqueID) {

        String sql = "SELECT * FROM ec_pmtct_child_monitoring WHERE unique_id = '" + uniqueID + "' ";

        List<ChildMonitoringModel> values = AbstractDao.readData(sql, getChildMonitoringModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
    public static String countChildMonitoring (String uniqueId){

        String sql = "SELECT COUNT(*) v FROM ec_pmtct_child_monitoring WHERE unique_id = '" + uniqueId + "' ";
        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }
    public static DataMap<ChildMonitoringModel> getChildMonitoringModelMap() {
        return c -> {

            ChildMonitoringModel record = new ChildMonitoringModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setPediatic_care_follow_up(getCursorValue(c, "pediatic_care_follow_up"));
            record.setDate(getCursorValue(c, "date"));
            record.setHiv_test_p_n(getCursorValue(c, "hiv_test_p_n"));
            record.setAzt_3tc_npv(getCursorValue(c, "azt_3tc_npv"));
            record.setDate_tested(getCursorValue(c, "date_tested"));
            record.setCtx(getCursorValue(c, "ctx"));
            record.setIycf_counselling(getCursorValue(c, "iycf_counselling"));
            record.setInfant_feeding_options(getCursorValue(c, "infant_feeding_options"));
            record.setHigh_risk_hei(getCursorValue(c, "high_risk_hei"));
            record.setDelete_status(getCursorValue(c,"delete_status"));

            record.setFinal_outcome(getCursorValue(c, "final_outcome"));
            record.setDate_referred_for_art_if_hiv_positive(getCursorValue(c, "date_referred_for_art_if_hiv_positive"));
            record.setDate_enrolled_in_art(getCursorValue(c,"date_enrolled_in_art"));

            return record;
        };
    }
}

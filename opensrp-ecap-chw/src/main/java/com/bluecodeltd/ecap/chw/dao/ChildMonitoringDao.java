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

    public static ChildMonitoringModel getRecentChildVisit(String uniqueID) {
        String sql = "SELECT *, strftime('%Y-%m-%d', substr(date,7,4) || '-' || substr(date,4,2) || '-' || substr(date,1,2)) as sortable_date " +
                "FROM ec_pmtct_child_monitoring " +
                "WHERE unique_id = '" + uniqueID + "' " +
                "ORDER BY sortable_date DESC LIMIT 1";

        List<ChildMonitoringModel> values = AbstractDao.readData(sql, getChildMonitoringModelMap());

        if (values == null || values.isEmpty()) {
            return null;
        }

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
            record.setHiv_test(getCursorValue(c, "hiv_test"));
            record.setAzt_3tc_npv(getCursorValue(c, "azt_3tc_npv"));
            record.setCtx(getCursorValue(c, "ctx"));
            record.setDate_tested(getCursorValue(c, "date_tested"));
            record.setIycf_counselling(getCursorValue(c, "iycf_counselling"));
            record.setInfant_feeding_options(getCursorValue(c, "infant_feeding_options"));
            record.setHigh_risk_hei(getCursorValue(c, "high_risk_hei"));
            record.setHiv_test_outcome(getCursorValue(c, "hiv_test_outcome"));
            record.setAzt_3tc_npv_outcome(getCursorValue(c, "azt_3tc_npv_outcome"));
            record.setCtx_outcome(getCursorValue(c, "ctx_outcome"));
            record.setDate_tested_outcome(getCursorValue(c, "date_tested_outcome"));
            record.setIycf_counselling_outcome(getCursorValue(c, "iycf_counselling_outcome"));
            record.setInfant_feeding_options_outcome(getCursorValue(c, "infant_feeding_options_outcome"));
            record.setHigh_risk_hei_outcome(getCursorValue(c, "high_risk_hei_outcome"));
            record.setDeleted_status(getCursorValue(c,"delete_status"));

            return record;
        };
    }
}

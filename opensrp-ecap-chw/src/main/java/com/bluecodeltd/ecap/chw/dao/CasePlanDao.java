package com.bluecodeltd.ecap.chw.dao;

import android.util.Log;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class CasePlanDao extends AbstractDao {

    public static int checkCasePlan (String childID) {

        String sql = "SELECT COUNT(*) plans FROM ec_vca_case_plan WHERE unique_id = '" + childID + "' AND case_plan_date IS NOT NULL ORDER BY case_plan_date DESC";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "plans");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return Integer.parseInt(values.get(0));

    }
}

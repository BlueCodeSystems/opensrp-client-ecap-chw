package com.bluecodeltd.ecap.chw.dao;

import android.util.Log;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class CasePlanDao extends AbstractDao {

    public static boolean checkCasePlan (String baseEntityID) {
        String sql = "SELECT COUNT(*) plans FROM ec_vca_case_plan WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "plans");

        List<String> values = AbstractDao.readData(sql, dataMap);

        Log.d(CasePlanDao.class.getName(), "xxx : "+ baseEntityID + " : " +  values.toString());

        return Integer.valueOf(values.get(0)) != 0;

    }
}

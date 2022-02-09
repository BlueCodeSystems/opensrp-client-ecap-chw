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

    public static int getByIDNumberOfCaregiverCasepalns (String Id) {

        String sql = "SELECT COUNT(*)  plans FROM ec_caregiver_case_plan WHERE household_id = '" + Id + "' AND case_plan_date IS NOT NULL ORDER BY case_plan_date DESC";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "plans");

        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null)
        {
            return 0;
        }
        else{
            return Integer.parseInt(values.get(0));
        }


    }
}

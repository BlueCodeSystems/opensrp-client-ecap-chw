package com.bluecodeltd.ecap.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class CasePlanDao extends AbstractDao {

    public static int checkCasePlan (String childID) {

        String sql = "SELECT COUNT(*) plans FROM ec_vca_case_plan WHERE unique_id = '" + childID + "' AND case_plan_date IS NOT NULL AND (delete_status IS NULL OR delete_status <> '1') ORDER BY case_plan_date DESC";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "plans");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return Integer.parseInt(values.get(0));

    }


    public static String countVulnerabilities (String uniqueId, String cpDate){

        String sql = "SELECT COUNT(*) v FROM ec_vca_case_plan_domain WHERE unique_id = '" + uniqueId + "' AND case_plan_date = '" + cpDate + "' AND case_plan_date IS NOT NULL AND (delete_status IS NULL OR delete_status <> '1')";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }

    public static int getByIDNumberOfCaregiverCasepalns (String Id) {

        String sql = "SELECT COUNT(*) plans FROM ec_caregiver_case_plan WHERE household_id = '" + Id + "' AND case_plan_date IS NOT NULL AND (delete_status IS NULL OR delete_status <> '1') ORDER BY case_plan_date DESC";

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

    public static String countCaregiverVulnerabilities (String uniqueId, String cpDate){

        String sql = "SELECT COUNT(*) v FROM ec_caregiver_case_plan_domain WHERE unique_id = '" + uniqueId + "' AND case_plan_date = '" + cpDate + "' AND case_plan_date IS NOT NULL AND (delete_status IS NULL OR delete_status <> '1')";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }
}

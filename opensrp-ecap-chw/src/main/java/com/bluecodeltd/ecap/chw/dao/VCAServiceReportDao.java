package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.VCAServiceModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class VCAServiceReportDao extends AbstractDao {

    public static List<VCAServiceModel> getRecentServicesByVCAID(String vcaID) {

        String sql = "SELECT *,is_hiv_positive, vl_last_result, child_mmd, level_mmd,date, unique_id,strftime('%Y-%m-%d', substr(date,7,4) || '-' || substr(date,4,2) || '-' || substr(date,1,2)) as sortable_date\n" +
                "FROM ec_vca_service_report WHERE  unique_id = '" + vcaID + "' AND  (delete_status IS NULL OR delete_status <> '1') ORDER BY sortable_date DESC LIMIT 1";

        List<VCAServiceModel> values = AbstractDao.readData(sql, getServiceModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static List<VCAServiceModel> getServicesByVCAID(String vcaid) {
        String sql = "SELECT *, strftime('%Y-%m-%d', substr(date,7,4) || '-' || substr(date,4,2) || '-' || substr(date,1,2)) as sortable_date\n" +
                "FROM ec_vca_service_report\n" +
                "WHERE unique_id = '" + vcaid + "' AND (delete_status IS NULL OR delete_status <> '1')\n" +
                "ORDER BY sortable_date DESC";

        List<VCAServiceModel> values = AbstractDao.readData(sql, getServiceModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static VCAServiceModel getVcaService (String vcaid) {

        String sql = "SELECT *, strftime('%Y-%m-%d', substr(date,7,4) || '-' || substr(date,4,2) || '-' || substr(date,1,2)) as sortable_date\n" +
                "FROM ec_vca_service_report\n" +
                "WHERE unique_id = '" + vcaid + "' AND (delete_status IS NULL OR delete_status <> '1')\n" +
                "ORDER BY sortable_date DESC";

        List<VCAServiceModel> values = AbstractDao.readData(sql, getServiceModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static boolean checkAllVcaViralLoads(String householdId) {
        // SQL query to get all viral load results for the given household
        String sql = "SELECT  ec_vca_service_report.vl_last_result,  ec_vca_service_report.unique_id,ec_client_index.household_id\n" +
                " FROM ec_vca_service_report\n" +
                " JOIN ec_client_index ON ec_client_index.unique_id = ec_vca_service_report.unique_id\n" +
                " WHERE household_id = '" + householdId + "' AND (ec_vca_service_report.delete_status IS NULL OR ec_vca_service_report.delete_status <> '1') \n" +
                " AND ec_vca_service_report.is_hiv_positive = 'yes' GROUP BY ec_vca_service_report.unique_id";
        // Map the result set to extract the vl_last_result as an integer
        AbstractDao.DataMap<Integer> dataMap = c -> getCursorIntValue(c, "vl_last_result");

        List<Integer> values;
        try {
            // Execute the query and get the list of vl_last_result values
            values = AbstractDao.readData(sql, dataMap);
        } catch (Exception e) {
            e.printStackTrace(); // Replace with proper logging
            return false;
        }

        // Check if all values are less than 1000
        if (values != null) {
            for (Integer result : values) {
                if (result == null || result >= 1000) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }






    public static DataMap<VCAServiceModel> getServiceModelMap() {
        return c -> {

            VCAServiceModel record = new VCAServiceModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setIs_hiv_positive(getCursorValue(c, "is_hiv_positive"));
            record.setDate(getCursorValue(c, "date"));
            record.setArt_clinic(getCursorValue(c, "art_clinic"));
            record.setDate_last_vl(getCursorValue(c, "date_last_vl"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setDate_next_vl(getCursorValue(c, "date_next_vl"));
            record.setChild_mmd(getCursorValue(c, "child_mmd"));
            record.setLevel_mmd(getCursorValue(c, "level_mmd"));
            record.setServices(getCursorValue(c, "services"));
            record.setOther_service(getCursorValue(c, "other_service"));
            record.setSchooled_services(getCursorValue(c, "schooled_services"));
            record.setOther_schooled_services(getCursorValue(c, "other_schooled_services"));
            record.setSafe_services(getCursorValue(c, "safe_services"));
            record.setOther_safe_services(getCursorValue(c, "other_safe_services"));
            record.setStable_services(getCursorValue(c, "stable_services"));
            record.setOther_stable_services(getCursorValue(c, "other_stable_services"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setVca_service_location(getCursorValue(c,"vca_service_location"));
            record.setSignature(getCursorValue(c,"signature"));

            return record;
        };
    }
}

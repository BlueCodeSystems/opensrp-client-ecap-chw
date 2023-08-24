package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.HouseholdServiceReportModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class HouseholdServiceReportDao extends AbstractDao {
    public static List<HouseholdServiceReportModel> getServicesByHousehold(String householdId) {

        String sql = "SELECT *, strftime('%Y-%m-%d', substr(date,7,4) || '-' || substr(date,4,2) || '-' || substr(date,1,2)) as sortable_date\n" +
                "FROM ec_household_service_report\n" +
                "WHERE household_id = '" + householdId + "' AND (delete_status IS NULL OR delete_status <> '1')\n" +
                "ORDER BY sortable_date DESC";

        List<HouseholdServiceReportModel> values = AbstractDao.readData(sql, getServiceModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static List<HouseholdServiceReportModel> getRecentVLServicesByHousehold(String householdId) {

        String sql = "SELECT *, is_hiv_positive, date, vl_last_result, level_mmd, caregiver_mmd,household_id,strftime('%Y-%m-%d', substr(date,7,4) || '-' || substr(date,4,2) || '-' || substr(date,1,2)) as sortable_date\n" +
                "\t\t\t\tFROM ec_household_service_report WHERE  household_id = '" + householdId + "' AND  (delete_status IS NULL OR delete_status <> '1') ORDER BY sortable_date DESC LIMIT 1";

        List<HouseholdServiceReportModel> values = AbstractDao.readData(sql, getServiceModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static AbstractDao.DataMap<HouseholdServiceReportModel> getServiceModelMap() {
        return c -> {

            HouseholdServiceReportModel record = new HouseholdServiceReportModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setServices(getCursorValue(c, "services"));
            record.setServices_household(getCursorValue(c, "services_household"));
            record.setServices_caregiver(getCursorValue(c, "services_caregiver"));
            record.setHealth_services(getCursorValue(c, "health_services"));
            record.setOther_health_services(getCursorValue(c, "other_health_services"));
            record.setSchooled_services(getCursorValue(c, "schooled_services"));
            record.setOther_schooled_services(getCursorValue(c, "other_schooled_services"));
            record.setSafe_services(getCursorValue(c, "safe_services"));
            record.setOther_safe_services(getCursorValue(c, "other_safe_services"));
            record.setStable_services(getCursorValue(c, "stable_services"));
            record.setOther_stable_services(getCursorValue(c, "other_stable_services"));
            record.setHh_level_services(getCursorValue(c, "hh_level_services"));
            record.setOther_hh_level_services(getCursorValue(c, "other_hh_level_services"));
            record.setDate(getCursorValue(c, "date"));
            record.setIs_hiv_positive(getCursorValue(c, "is_hiv_positive"));
            record.setArt_clinic(getCursorValue(c, "art_clinic"));
            record.setDate_last_vl(getCursorValue(c, "date_last_vl"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setDate_next_vl(getCursorValue(c, "date_next_vl"));
            record.setCaregiver_mmd(getCursorValue(c, "caregiver_mmd"));
            record.setLevel_mmd(getCursorValue(c, "level_mmd"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setOther_services_caregiver(getCursorValue(c, "other_services_caregiver"));
            record.setOther_services_household(getCursorValue(c, "other_services_household"));
            record.setDelete_status(getCursorValue(c, "delete_status"));

            return record;
        };
    }
}

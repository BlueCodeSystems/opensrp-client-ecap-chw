package com.bluecodeltd.ecap.chw.dao;


import android.util.Log;

import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.CaseStatusModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.VCAServiceModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexPersonDao  extends AbstractDao {

    public static String checkIndexPerson (String baseEntityID) {

        String sql = "SELECT index_check_box FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "'";

        DataMap<String> dataMap = c -> getCursorValue(c, "index_check_box");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }



    public static void deleteRecord (String vcaID) {

        String sql = "UPDATE ec_client_index SET is_closed = '1' WHERE base_entity_id = '" + vcaID + "'";
        updateDB(sql);

    }

    public static void deleteRecordfromSearch (String vcaID) {

        String sql = "UPDATE ec_client_index_search SET is_closed = '1' WHERE object_id = '" + vcaID + "'";
        updateDB(sql);

    }


    public static String countChildren(String householdID){

        String sql = "SELECT COUNT(*) AS childrenCount FROM ec_client_index WHERE  household_id = '" + householdID + "' AND deleted IS NULL OR deleted != '1'";

        DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "1";

        return values.get(0);

    }

    public static String countAllChildren(){
        try {
            String sql = "SELECT COUNT(DISTINCT base_entity_id) AS childrenCount FROM ec_client_index WHERE (deleted IS NULL OR deleted != '1') AND adolescent_birthdate IS NOT NULL";
            DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");
            List<String> values = AbstractDao.readData(sql, dataMap);
            return (values != null && !values.isEmpty()) ? values.get(0) : "0";
        } catch (Exception e) {
            Log.e("countAllChildren", "Exception", e);
            return "0";
        }
    }
    public static String countAllChildrenByCaseworkerPhoneNumber(String caseworkerPhoneNumber){

        String sql = "SELECT COUNT(DISTINCT base_entity_id ) AS childrenCount FROM ec_client_index WHERE phone = '" + caseworkerPhoneNumber + "' AND adolescent_birthdate IS NOT NULL AND deleted IS NULL OR deleted != '1'";

        DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0) {
            return "0";
        }else
        {
            return values.get(0);
        }


    }

    public static String countTestedChildren(String householdID){

        String sql = "SELECT COUNT(*) AS childrenCount FROM ec_hiv_assessment_below_15 WHERE household_id = '" + householdID + "' AND hiv_test IS NOT NULL AND hiv_test != 'never_tested'";

        DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }

    public static List<VCAServiceModel> getServicesByVCAID(String vcaid) {

        String sql = "SELECT * FROM ec_vca_service_report WHERE unique_id = '" + vcaid + "'  AND (delete_status IS NULL OR delete_status <> '1')";

        List<VCAServiceModel> values = AbstractDao.readData(sql, getServiceModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static boolean hasBeneficiary10to17InHousehold(String householdID) {

        String sql = "SELECT unique_id, adolescent_birthdate, household_id " +
                "FROM ec_client_index " +
                "WHERE ((strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) * 12 + " +
                "(strftime('%m', 'now') - substr(adolescent_birthdate, 4, 2))) BETWEEN 120 AND 204 " +
                "AND household_id = '" + householdID + "' AND (deleted IS NULL OR deleted <> '1')";


        List<String> ids = AbstractDao.readData(sql, c -> getCursorValue(c, "unique_id"));


        return ids != null && !ids.isEmpty();
    }
    public static boolean hasAtLeastOneVCABetweenSixMonthsAndFiveYearsOld(String householdID) {

        String sql = "SELECT unique_id, adolescent_birthdate, household_id " +
                "FROM ec_client_index " +
                "WHERE ((strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) * 12 + " +
                "(strftime('%m', 'now') - substr(adolescent_birthdate, 4, 2))) BETWEEN 6 AND 60 " +
                "AND household_id = '" + householdID + "' AND (deleted IS NULL OR deleted <> '1')";

        List<String> ids = AbstractDao.readData(sql, c -> getCursorValue(c, "unique_id"));

        return ids != null && !ids.isEmpty();
    }
    public static boolean hasAtLeastOneVCAFiveMonthsAndBelow(String householdID) {
        String sql = "SELECT unique_id, adolescent_birthdate, household_id " +
                "FROM ec_client_index " +
                "WHERE ((strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) * 12 + " +
                "(strftime('%m', 'now') - substr(adolescent_birthdate, 4, 2))) <= 5 " +
                "AND household_id = '" + householdID + "' AND (deleted IS NULL OR deleted <> '1')";

        List<String> ids = AbstractDao.readData(sql, c -> getCursorValue(c, "unique_id"));

        return ids != null && !ids.isEmpty();
    }

    public static DataMap<VCAServiceModel> getServiceModelMap() {
        return c -> {

            VCAServiceModel record = new VCAServiceModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setDate(getCursorValue(c, "date"));
            record.setArt_clinic(getCursorValue(c, "art_clinic"));
            record.setDate_last_vl(getCursorValue(c, "date_last_vl"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setDate_next_vl(getCursorValue(c, "date_next_vl"));
            record.setChild_mmd(getCursorValue(c, "child_mmd"));
            record.setLevel_mmd(getCursorValue(c, "level_mmd"));
            record.setServices(getCursorValue(c, "services"));
            record.setOther_service(getCursorValue(c, "other_service"));
            record.setSchooled_services(getCursorValue(c,"schooled_services"));
            record.setOther_schooled_services(getCursorValue(c,"other_schooled_services"));
            record.setStable_services(getCursorValue(c,"stable_services"));
            record.setOther_stable_services(getCursorValue(c,"other_stable_services"));
            record.setSafe_services(getCursorValue(c,"safe_services"));
            record.setOther_safe_services(getCursorValue(c,"other_safe_services"));

            return record;
        };
    }

    public static String countPositiveChildren(String householdID){

        String sql = "SELECT COUNT(*) AS childrenCount FROM ec_client_index WHERE household_id = '" + householdID + "' AND is_hiv_positive IS NOT NULL AND is_hiv_positive = 'yes'";

        DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }
    public static String countSuppressedChildren(String householdID){

        String sql = "SELECT COUNT(*) AS childrenCount FROM ec_client_index WHERE household_id = '" + householdID + "' AND CAST(vl_last_result as integer) < 1000";

        DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }
    public static boolean checkForAtLeastOnePositiveVca(String householdID) {
        String sql = "SELECT is_hiv_positive FROM ec_client_index WHERE household_id = '" + householdID + "' AND (deleted IS NULL OR deleted <> '1')";

        DataMap<String> dataMap = c -> getCursorValue(c, "is_hiv_positive");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.isEmpty()) {
            return false;
        }

        for (String value : values) {
            if (value.equals("yes")) {
                return true;
            }
        }

        return false;
    }

    public static boolean doTheVCAsMeetBenchmarkTwo(String householdID) {

        String sql1 = "SELECT is_hiv_positive, vl_last_result, household_id, unique_id " +
                "FROM ec_client_index " +
                "WHERE is_hiv_positive = 'yes' "  +
                "AND household_id = '" + householdID + "' AND (deleted IS NULL OR deleted <> '1')";

        List<String> vcaIds = AbstractDao.readData(sql1, c -> getCursorValue(c, "unique_id"));
        List<String> viralResult = AbstractDao.readData(sql1, c -> getCursorValue(c, "vl_last_result"));

        if (vcaIds == null || vcaIds.isEmpty() || viralResult.contains(null)) {
            return false;
        }

        for (String vcaId : vcaIds) {
            // Then, check if the vl_last_result conducted for a period of one year from today is less than 1000 for each VCA in the household
            String sql2 = "SELECT unique_id, vl_last_result, date as date " +
                    "FROM ec_vca_service_report " +
                    "WHERE DATE(SUBSTR(date, 7, 4) || '-' || SUBSTR(date, 4, 2) || '-' || SUBSTR(date, 1, 2)) >= DATE('now','-1 year')" + // This gets the date one year in the past from the current date in SQLite
                    "AND unique_id = '" + vcaId + "' AND (delete_status IS NULL OR delete_status <> '1')";

            List<String> values = AbstractDao.readData(sql2, c -> getCursorValue(c, "vl_last_result"));

            if (values == null || values.isEmpty()) {
                return false;
            }

            for (String value : values) {
                if (value == null || Integer.parseInt(value) >= 1001) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean doTheVCAsMeetBenchmarkOne(String householdID) {
        String sql1 = "SELECT unique_id, adolescent_birthdate, household_id " +
                "FROM ec_client_index " +
                "WHERE (is_hiv_positive = 'no' OR is_hiv_positive = 'unknown' OR is_hiv_positive = 'not_required')  " +
                "AND (strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) >= 2 " +
                "AND (strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) <= 18 " +
                "AND household_id = '" + householdID + "' AND (deleted IS NULL OR deleted <> '1')";

        List<String> vcaIds = AbstractDao.readData(sql1, c -> getCursorValue(c, "unique_id"));

        if (vcaIds == null || vcaIds.isEmpty()) {
            return false;
        }

        for (String vcaId : vcaIds) {
            String sql2 = "SELECT unique_id, is_hiv_positive, household_id " +
                    "FROM ( " +
                    "SELECT hiv_test AS is_hiv_positive, unique_id, household_id FROM ec_hiv_assessment_below_15 " +
                    "UNION ALL " +
                    "SELECT hiv_test AS is_hiv_positive, unique_id, household_id FROM ec_hiv_assessment_above_15 " +
                    ") AS combined " +
                    "WHERE unique_id = '" + vcaId + "'";

            List<String> values = AbstractDao.readData(sql2, c -> getCursorValue(c, "unique_id"));
            List<String> hivPositives = AbstractDao.readData(sql2, c -> getCursorValue(c, "is_hiv_positive"));

            if (values == null || values.isEmpty()) {
                return false;
            }

            for (String hivPositive : hivPositives) {
                if (!hivPositive.equalsIgnoreCase("yes") && !hivPositive.equalsIgnoreCase("no")  && !hivPositive.equalsIgnoreCase("positive") && !hivPositive.equalsIgnoreCase("negative")) {
                    return false;
                }
            }
        }

        return true;
    }
    public static boolean checkHivStatusInHousehold(String householdID) {

        String sql = "SELECT first_name, last_name, is_hiv_positive " +
                "FROM ec_client_index " +
                "WHERE household_id = '" + householdID + "' " +
                "AND (deleted IS NULL OR deleted <> '1')";

        List<String> hivStatuses = AbstractDao.readData(sql, c -> getCursorValue(c, "is_hiv_positive"));

        if (hivStatuses == null || hivStatuses.isEmpty()) {
            return false;
        }

        for (String status : hivStatuses) {
            if (status.equalsIgnoreCase("no") ||
                    status.equalsIgnoreCase("yes") ||
                    status.equalsIgnoreCase("not_required")) {
                return true;
            }
        }

        return false;
    }
    public static boolean returnTrueForAtLeastOneVCAISHivNegativeInHousehold(String householdID) {

        String sql = "SELECT first_name, last_name, is_hiv_positive " +
                "FROM ec_client_index " +
                "WHERE household_id = '" + householdID + "' " +
                "AND (deleted IS NULL OR deleted <> '1') " +
                "AND is_hiv_positive IN ('unknown', 'no','yes','not_required')";

        List<String> vcaIds = AbstractDao.readData(sql, c -> getCursorValue(c, "first_name"));

        return vcaIds != null && !vcaIds.isEmpty();
    }
    public static boolean hasAtLeastOneVCAUnderFiveYearsOld(String householdID) {

        String sql = "SELECT unique_id, adolescent_birthdate, household_id " +
                "FROM ec_client_index " +
                "WHERE (strftime('%Y', 'now') - substr(adolescent_birthdate, 7, 4)) <= 5 " +
                "AND household_id = '" + householdID + "' AND (deleted IS NULL OR deleted <> '1')";

        List<String> ids = AbstractDao.readData(sql, c -> getCursorValue(c, "unique_id"));

        return ids != null && !ids.isEmpty();
    }
    public static String countTestedAbove15Children(String householdID){

        String sql = "SELECT COUNT(*) AS childrenCount FROM ec_hiv_assessment_above_15 WHERE household_id = '" + householdID + "' AND hiv_test IS NOT NULL AND hiv_test != 'never_tested'";

        DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }

    public static String countFemales (String householdID){
        

        String sql = "SELECT COUNT(*) AS females FROM ec_client_index WHERE gender = 'female' AND household_id = '" + householdID + "'";

        DataMap<String> dataMap = c -> getCursorValue(c, "females");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }

    public static String countMales (String householdID){


        String sql = "SELECT COUNT(*) AS males FROM ec_client_index WHERE gender = 'male' AND household_id = '" + householdID + "'";

        DataMap<String> dataMap = c -> getCursorValue(c, "males");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "0";

        return values.get(0);

    }
    public static List<String> getMalesBirthdates (String householdID){


        String sql = "SELECT adolescent_birthdate FROM ec_client_index WHERE gender = 'male' AND household_id = '" + householdID + "' AND (deleted IS NULL OR deleted <> '1')";

        DataMap<String> dataMap = c -> getCursorValue(c, "adolescent_birthdate");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return null;
        else{
            return values;
        }


    }


    public static String getIndexStatus (String baseEntityID){

        String sql = "SELECT case_status FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "'";

        DataMap<String> dataMap = c -> getCursorValue(c, "case_status");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }

    public static String getBirthdate (String uniqueId){

        String sql = "SELECT adolescent_birthdate FROM ec_client_index WHERE unique_id = '" + uniqueId + "'";

        DataMap<String> dataMap = c -> getCursorValue(c, "adolescent_birthdate");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }
    
    public static List<String> getGenders(String household_id){

        String sql = "SELECT gender FROM ec_client_index WHERE household_id = '" + household_id + "' AND gender IS NOT NULL";

        DataMap<String> dataMap = c -> getCursorValue(c, "gender");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values;
    }

    public static List<String> getAges(String household_id){

        String sql = "SELECT adolescent_birthdate FROM ec_client_index WHERE household_id = '" + household_id + "' AND adolescent_birthdate IS NOT NULL";

        DataMap<String> dataMap = c -> getCursorValue(c, "adolescent_birthdate");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values;
    }

    public static List<Child> getFamilyChildren(String householdID) {

        String sql = "SELECT * FROM ec_client_index WHERE household_id = '"+ householdID +"' AND (deleted IS NULL OR deleted != '1') AND adolescent_birthdate IS NOT NULL";

        List<Child> values = AbstractDao.readData(sql, getChildDataMap());// Remember to edit getChildDataMap METHOD Below
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static List<CasePlanModel> getDomainsById(String childID, String caseDate) {

        String sql = "SELECT * FROM ec_vca_case_plan_domain WHERE unique_id = '" + childID + "' AND case_plan_date = '" + caseDate + "' AND case_plan_date IS NOT NULL AND (delete_status IS NULL OR delete_status <> '1') ORDER BY case_plan_date DESC";

        List<CasePlanModel> values = AbstractDao.readData(sql, getCasePlanMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static List<CasePlanModel> getAllDomainsById() {

        String sql = "SELECT * FROM ec_vca_case_plan_domain WHERE case_plan_date IS NOT NULL AND (delete_status IS NULL OR delete_status <> '1') ORDER BY case_plan_date DESC";

        List<CasePlanModel> values = AbstractDao.readData(sql, getCasePlanMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }
    public static CaseStatusModel getCaseStatus(String childID) {
//        String sql = "SELECT ec_client_index.unique_id, ec_client_index.case_status " +
//                "FROM ec_client_index " +
//                "JOIN ec_vca_case_plan_domain ON ec_vca_case_plan_domain.unique_id = ec_client_index.unique_id " +
//                "WHERE ec_client_index.unique_id ='" + childID + "' " +
//                "GROUP BY ec_client_index.unique_id, ec_client_index.case_status";
        String sql = "SELECT household_id,first_name,last_name,unique_id, case_status FROM ec_client_index  WHERE unique_id = '" + childID + "'";

        DataMap<CaseStatusModel> dataMap = c -> {
            CaseStatusModel model = new CaseStatusModel();
            model.setFirst_name(getCursorValue(c, "first_name"));
            model.setLast_name(getCursorValue(c, "last_name"));
            model.setUnique_id(getCursorValue(c, "unique_id"));
            model.setCase_status(getCursorValue(c, "case_status"));
            model.setHousehold_id(getCursorValue(c, "household_id"));

            return model;
        };

        List<CaseStatusModel> models = AbstractDao.readData(sql, dataMap);

        if (models == null || models.isEmpty()) {
            return null;
        }

        return models.get(0);
    }

    public static List<Child> getDomainActiveStatus(String childID) {

        String sql = "SELECT ec_client_index.unique_id, ec_client_index.case_status \n" +
                "FROM ec_client_index \n" +
                "JOIN ec_vca_case_plan_domain ON ec_vca_case_plan_domain.unique_id = ec_client_index.unique_id WHERE ec_client_index.unique_id ='" + childID + "'\n" +
                "GROUP BY ec_client_index.unique_id, ec_client_index.case_status";

        List<Child> values = AbstractDao.readData(sql, getChildDataMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static List<CasePlanModel> getCasePlansById(String childID) {

        String sql = "SELECT *, strftime('%Y-%m-%d', substr(case_plan_date,7,4) || '-' || substr(case_plan_date,4,2) || '-' || substr(case_plan_date,1,2)) as sortable_date FROM ec_vca_case_plan WHERE unique_id = '" + childID + "' AND case_plan_date IS NOT NULL AND (delete_status IS NULL OR delete_status <> '1') ORDER BY sortable_date DESC";

        List<CasePlanModel> values = AbstractDao.readData(sql, getCasePlanMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static DataMap<CasePlanModel> getCasePlanMap() {
        return c -> {

            CasePlanModel record = new CasePlanModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setCase_plan_date(getCursorValue(c, "case_plan_date"));
            record.setCase_plan_status(getCursorValue(c, "case_plan_status"));
            record.setType(getCursorValue(c, "type"));
            record.setVulnerability(getCursorValue(c, "vulnerability"));
            record.setGoal(getCursorValue(c, "goal"));
            record.setServices(getCursorValue(c, "services"));
            record.setService_referred(getCursorValue(c, "service_referred"));
            record.setInstitution(getCursorValue(c, "institution"));
            record.setDue_date(getCursorValue(c, "due_date"));
            record.setQuarter(getCursorValue(c, "quarter"));
            record.setStatus(getCursorValue(c, "status"));
            record.setComment(getCursorValue(c, "comment"));
            record.setCase_plan_id(getCursorValue(c, "case_plan_id"));

            return record;
          };
        }


    public static DataMap<Child> getChildDataMap() {
        return c -> {
            Child record = new Child();
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setEntity_id(getCursorValue(c, "base_entity_id"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            record.setGender(getCursorValue(c, "gender"));
            record.setSchool(getCursorValue(c, "school"));
            record.setIs_hiv_positive(getCursorValue(c, "is_hiv_positive"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setDate_last_vl(getCursorValue(c, "date_last_vl"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setSubpop1(getCursorValue(c, "subpop1"));
            record.setIndex_check_box(getCursorValue(c, "index_check_box"));
            record.setCase_plan_id(getCursorValue(c, "case_plan_id"));
            record.setDeleted(getCursorValue(c, "deleted"));
            return record;
        };
    }


    public static Child getChildByBaseId(String UID){
        String sql = "SELECT *, first_name AS adolescent_first_name,last_name As adolescent_last_name, gender as adolescent_gender FROM ec_client_index WHERE unique_id = '" + UID + "' AND (adolescent_first_name IS NOT NULL OR adolescent_last_name IS NOT NULL OR adolescent_birthdate IS NOT NULL)";
        DataMap<Child> dataMap = c -> {
            return new Child(
                    getCursorValue(c, "last_interacted_with"),
                    getCursorValue(c, "phone"),
                    getCursorValue(c, "caseworker_name"),
                    getCursorValue(c, "date_edited"),
                    getCursorValue(c, "vl_check_box"),
                    getCursorValue(c, "landmark"),
                    getCursorValue(c, "client_screened"),
                    getCursorValue(c, "client_result"),
                    getCursorValue(c, "tpt_client_eligibility"),
                    getCursorValue(c, "tpt_client_initiated"),
                    getCursorValue(c, "case_status"),
                    getCursorValue(c, "reason"),
                    getCursorValue(c, "other_reason"),
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "household_id"),
                    getCursorValue(c, "unique_id"),
                    getCursorValue(c, "first_name"),
                    getCursorValue(c, "last_name"),
                    getCursorValue(c, "adolescent_gender"),
                    getCursorValue(c, "adolescent_birthdate"),
                    getCursorValue(c, "subpop1"),
                    getCursorValue(c, "subpop2"),
                    getCursorValue(c, "subpop3"),
                    getCursorValue(c, "subpop4"),
                    getCursorValue(c, "subpop5"),
                    getCursorValue(c, "subpop"),
                    getCursorValue(c, "is_biological_child_of_mother_living_with_hiv"),
                    getCursorValue(c, "date_referred"),
                    getCursorValue(c, "date_enrolled"),
                    getCursorValue(c, "art_check_box"),
                    getCursorValue(c, "art_number"),
                    getCursorValue(c, "date_started_art"),
                    getCursorValue(c, "date_last_vl"),
                    getCursorValue(c, "date_next_vl"),
                    getCursorValue(c, "vl_last_result"),
                    getCursorValue(c, "vl_suppressed"),
                    getCursorValue(c, "child_mmd"),
                    getCursorValue(c, "level_mmd"),
                    getCursorValue(c, "caregiver_name"),
                    getCursorValue(c, "caregiver_birth_date"),
                    getCursorValue(c, "caregiver_sex"),
                    getCursorValue(c, "caregiver_hiv_status"),
                    getCursorValue(c, "relation"),
                    getCursorValue(c, "caregiver_phone"),
                    getCursorValue(c, "facility"),
                    getCursorValue(c, "gender"),
                    getCursorValue(c, "relational_id"),
                    getCursorValue(c, "index_check_box"),
                    getCursorValue(c, "date_removed"),
                    getCursorValue(c, "acceptance"),
                    getCursorValue(c, "date_screened"),
                    getCursorValue(c, "date_hiv_known"),
                    getCursorValue(c, "is_hiv_positive"),
                    getCursorValue(c, "is_on_hiv_treatment"),
                    getCursorValue(c, "adolescent_first_name"),
                    getCursorValue(c, "adolescent_last_name"),
                    getCursorValue(c, "province"),
                    getCursorValue(c, "district"),
                    getCursorValue(c, "ward"),
                    getCursorValue(c, "village"),
                    getCursorValue(c, "partner"),
                    getCursorValue(c, "is_viral_load_test_results_on_file"),
                    getCursorValue(c, "is_tb_screening_results_on_file"),
                    getCursorValue(c, "screened_for_malnutrition"),
                    getCursorValue(c, "gets_tb_preventive_therapy"),
                    getCursorValue(c, "takes_drugs_to_prevent_other_diseases"),
                    getCursorValue(c, "less_3"),
                    getCursorValue(c, "positive_mother"),
                    getCursorValue(c, "is_mother_currently_on_treatment"),
                    getCursorValue(c, "mother_art_number"),
                    getCursorValue(c, "is_mother_adhering_to_treatment"),
                    getCursorValue(c, "is_mother_virally_suppressed"),
                    getCursorValue(c, "is_child_hiv_positive"),
                    getCursorValue(c, "child_receiving_breastfeeding"),
                    getCursorValue(c, "child_tested_for_hiv_inline_with_guidelines"),
                    getCursorValue(c, "receives_drugs_to_prevent_hiv_and_other_illnesses"),
                    getCursorValue(c, "child_been_screened_for_malnutrition"),
                    getCursorValue(c, "child_gets_drugs_to_prevent_tb"),
                    getCursorValue(c, "child_enrolled_in_early_childhood_development_program"),
                    getCursorValue(c, "school"),
                    getCursorValue(c, "other_school"),
                    getCursorValue(c, "caregiver_nrc"),
                    getCursorValue(c, "vl_next_result"),
                    getCursorValue(c, "physical_address"),
                    getCursorValue(c, "date_offered_enrollment"),
//                    getCursorValue(c, "school_name"),
                    getCursorValue(c, "schoolName"),
                    getCursorValue(c, "signature")


            );
        };
        List <Child> children =  AbstractDao.readData(sql, dataMap);
        if (children == null || children.isEmpty()) {
            return null;
        }

        return children.get(0);
    }
    public static List<Child> getAllChildrenSubpops(){
        String sql = "SELECT *, first_name AS adolescent_first_name,last_name As adolescent_last_name, gender as adolescent_gender FROM ec_client_index WHERE is_closed = 0 AND (deleted IS NULL OR deleted != '1')";
        DataMap<Child> dataMap = c -> {
            return new Child(
                    getCursorValue(c, "last_interacted_with"),
                    getCursorValue(c, "phone"),
                    getCursorValue(c, "caseworker_name"),
                    getCursorValue(c, "date_edited"),
                    getCursorValue(c, "vl_check_box"),
                    getCursorValue(c, "landmark"),
                    getCursorValue(c, "client_screened"),
                    getCursorValue(c, "client_result"),
                    getCursorValue(c, "tpt_client_eligibility"),
                    getCursorValue(c, "tpt_client_initiated"),
                    getCursorValue(c, "case_status"),
                    getCursorValue(c, "reason"),
                    getCursorValue(c, "other_reason"),
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "household_id"),
                    getCursorValue(c, "unique_id"),
                    getCursorValue(c, "first_name"),
                    getCursorValue(c, "last_name"),
                    getCursorValue(c, "adolescent_gender"),
                    getCursorValue(c, "adolescent_birthdate"),
                    getCursorValue(c, "subpop1"),
                    getCursorValue(c, "subpop2"),
                    getCursorValue(c, "subpop3"),
                    getCursorValue(c, "subpop4"),
                    getCursorValue(c, "subpop5"),
                    getCursorValue(c, "subpop"),
                    getCursorValue(c, "is_biological_child_of_mother_living_with_hiv"),
                    getCursorValue(c, "date_referred"),
                    getCursorValue(c, "date_enrolled"),
                    getCursorValue(c, "art_check_box"),
                    getCursorValue(c, "art_number"),
                    getCursorValue(c, "date_started_art"),
                    getCursorValue(c, "date_last_vl"),
                    getCursorValue(c, "date_next_vl"),
                    getCursorValue(c, "vl_last_result"),
                    getCursorValue(c, "vl_suppressed"),
                    getCursorValue(c, "child_mmd"),
                    getCursorValue(c, "level_mmd"),
                    getCursorValue(c, "caregiver_name"),
                    getCursorValue(c, "caregiver_birth_date"),
                    getCursorValue(c, "caregiver_sex"),
                    getCursorValue(c, "caregiver_hiv_status"),
                    getCursorValue(c, "relation"),
                    getCursorValue(c, "caregiver_phone"),
                    getCursorValue(c, "facility"),
                    getCursorValue(c, "gender"),
                    getCursorValue(c, "relational_id"),
                    getCursorValue(c, "index_check_box"),
                    getCursorValue(c, "date_removed"),
                    getCursorValue(c, "acceptance"),
                    getCursorValue(c, "date_screened"),
                    getCursorValue(c, "date_hiv_known"),
                    getCursorValue(c, "is_hiv_positive"),
                    getCursorValue(c, "is_on_hiv_treatment"),
                    getCursorValue(c, "adolescent_first_name"),
                    getCursorValue(c, "adolescent_last_name"),
                    getCursorValue(c, "province"),
                    getCursorValue(c, "district"),
                    getCursorValue(c, "ward"),
                    getCursorValue(c, "village"),
                    getCursorValue(c, "partner"),
                    getCursorValue(c, "is_viral_load_test_results_on_file"),
                    getCursorValue(c, "is_tb_screening_results_on_file"),
                    getCursorValue(c, "screened_for_malnutrition"),
                    getCursorValue(c, "gets_tb_preventive_therapy"),
                    getCursorValue(c, "takes_drugs_to_prevent_other_diseases"),
                    getCursorValue(c, "less_3"),
                    getCursorValue(c, "positive_mother"),
                    getCursorValue(c, "is_mother_currently_on_treatment"),
                    getCursorValue(c, "mother_art_number"),
                    getCursorValue(c, "is_mother_adhering_to_treatment"),
                    getCursorValue(c, "is_mother_virally_suppressed"),
                    getCursorValue(c, "is_child_hiv_positive"),
                    getCursorValue(c, "child_receiving_breastfeeding"),
                    getCursorValue(c, "child_tested_for_hiv_inline_with_guidelines"),
                    getCursorValue(c, "receives_drugs_to_prevent_hiv_and_other_illnesses"),
                    getCursorValue(c, "child_been_screened_for_malnutrition"),
                    getCursorValue(c, "child_gets_drugs_to_prevent_tb"),
                    getCursorValue(c, "child_enrolled_in_early_childhood_development_program"),
                    getCursorValue(c, "school"),
                    getCursorValue(c, "other_school"),
                    getCursorValue(c, "caregiver_nrc"),
                    getCursorValue(c, "vl_next_result"),
                    getCursorValue(c, "physical_address"),
                    getCursorValue(c, "date_offered_enrollment"),
                    getCursorValue(c, "school_name"),
                    getCursorValue(c, "signature")

            );
        };
        List<Child> children = null;
        try {
            children = AbstractDao.readData(sql, dataMap);
        } catch (NullPointerException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            return null;
        }
        return children;

    }
    public static boolean checkGraduationStatus(String householdID) {
        String sql = "SELECT case_status, last_name, is_hiv_positive " +
                "FROM ec_client_index " +
                "WHERE household_id = '" + householdID + "' " +
                "AND (deleted IS NULL OR deleted <> '1')";

        List<String> status = AbstractDao.readData(sql, c -> getCursorValue(c, "case_status"));

        for (String caseStatus : status) {
            if (caseStatus == null || "1".equals(caseStatus) || "2".equals(caseStatus) || "3".equals(caseStatus)) {
                return false;
            }
        }

        return true;
    }
    public static String returnVcaNames(String householdID) {
        String sql = "SELECT case_status, first_name, last_name, is_hiv_positive " +
                "FROM ec_client_index " +
                "WHERE household_id = '" + householdID + "' " +
                "AND (deleted IS NULL OR deleted <> '1') AND case_status != '0'";

        final AtomicInteger counter = new AtomicInteger(1);
        List<String> names = AbstractDao.readData(sql, c -> {
            String firstName = getCursorValue(c, "first_name");
            String lastName = getCursorValue(c, "last_name");
            return counter.getAndIncrement() + ". " + firstName + " " + lastName;
        });

        return String.join("\n", names) + "\n";
    }
    public static List<Child> getAllChildrenSubpopsByCaseworkerPhoneNumber(String caseworkerPhoneNumber){
        String sql = "SELECT *, first_name AS adolescent_first_name,last_name As adolescent_last_name, gender as adolescent_gender FROM ec_client_index WHERE phone = '" + caseworkerPhoneNumber + "' AND is_closed = 0 AND (deleted IS NULL OR deleted != '1')";
        DataMap<Child> dataMap = c -> {
            return new Child(
                    getCursorValue(c, "last_interacted_with"),
                    getCursorValue(c, "phone"),
                    getCursorValue(c, "caseworker_name"),
                    getCursorValue(c, "date_edited"),
                    getCursorValue(c, "vl_check_box"),
                    getCursorValue(c, "landmark"),
                    getCursorValue(c, "client_screened"),
                    getCursorValue(c, "client_result"),
                    getCursorValue(c, "tpt_client_eligibility"),
                    getCursorValue(c, "tpt_client_initiated"),
                    getCursorValue(c, "case_status"),
                    getCursorValue(c, "reason"),
                    getCursorValue(c, "other_reason"),
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "household_id"),
                    getCursorValue(c, "unique_id"),
                    getCursorValue(c, "first_name"),
                    getCursorValue(c, "last_name"),
                    getCursorValue(c, "adolescent_gender"),
                    getCursorValue(c, "adolescent_birthdate"),
                    getCursorValue(c, "subpop1"),
                    getCursorValue(c, "subpop2"),
                    getCursorValue(c, "subpop3"),
                    getCursorValue(c, "subpop4"),
                    getCursorValue(c, "subpop5"),
                    getCursorValue(c, "subpop"),
                    getCursorValue(c, "is_biological_child_of_mother_living_with_hiv"),
                    getCursorValue(c, "date_referred"),
                    getCursorValue(c, "date_enrolled"),
                    getCursorValue(c, "art_check_box"),
                    getCursorValue(c, "art_number"),
                    getCursorValue(c, "date_started_art"),
                    getCursorValue(c, "date_last_vl"),
                    getCursorValue(c, "date_next_vl"),
                    getCursorValue(c, "vl_last_result"),
                    getCursorValue(c, "vl_suppressed"),
                    getCursorValue(c, "child_mmd"),
                    getCursorValue(c, "level_mmd"),
                    getCursorValue(c, "caregiver_name"),
                    getCursorValue(c, "caregiver_birth_date"),
                    getCursorValue(c, "caregiver_sex"),
                    getCursorValue(c, "caregiver_hiv_status"),
                    getCursorValue(c, "relation"),
                    getCursorValue(c, "caregiver_phone"),
                    getCursorValue(c, "facility"),
                    getCursorValue(c, "gender"),
                    getCursorValue(c, "relational_id"),
                    getCursorValue(c, "index_check_box"),
                    getCursorValue(c, "date_removed"),
                    getCursorValue(c, "acceptance"),
                    getCursorValue(c, "date_screened"),
                    getCursorValue(c, "date_hiv_known"),
                    getCursorValue(c, "is_hiv_positive"),
                    getCursorValue(c, "is_on_hiv_treatment"),
                    getCursorValue(c, "adolescent_first_name"),
                    getCursorValue(c, "adolescent_last_name"),
                    getCursorValue(c, "province"),
                    getCursorValue(c, "district"),
                    getCursorValue(c, "ward"),
                    getCursorValue(c, "village"),
                    getCursorValue(c, "partner"),
                    getCursorValue(c, "is_viral_load_test_results_on_file"),
                    getCursorValue(c, "is_tb_screening_results_on_file"),
                    getCursorValue(c, "screened_for_malnutrition"),
                    getCursorValue(c, "gets_tb_preventive_therapy"),
                    getCursorValue(c, "takes_drugs_to_prevent_other_diseases"),
                    getCursorValue(c, "less_3"),
                    getCursorValue(c, "positive_mother"),
                    getCursorValue(c, "is_mother_currently_on_treatment"),
                    getCursorValue(c, "mother_art_number"),
                    getCursorValue(c, "is_mother_adhering_to_treatment"),
                    getCursorValue(c, "is_mother_virally_suppressed"),
                    getCursorValue(c, "is_child_hiv_positive"),
                    getCursorValue(c, "child_receiving_breastfeeding"),
                    getCursorValue(c, "child_tested_for_hiv_inline_with_guidelines"),
                    getCursorValue(c, "receives_drugs_to_prevent_hiv_and_other_illnesses"),
                    getCursorValue(c, "child_been_screened_for_malnutrition"),
                    getCursorValue(c, "child_gets_drugs_to_prevent_tb"),
                    getCursorValue(c, "child_enrolled_in_early_childhood_development_program"),
                    getCursorValue(c, "school"),
                    getCursorValue(c, "other_school"),
                    getCursorValue(c, "caregiver_nrc"),
                    getCursorValue(c, "vl_next_result"),
                    getCursorValue(c, "physical_address"),
                    getCursorValue(c, "date_offered_enrollment"),
                    getCursorValue(c, "school_name"),
                    getCursorValue(c, "signature")

            );
        };
        List <Child> children =  AbstractDao.readData(sql, dataMap);
        if (children == null) {
            return null;
        }
        return children;
    }
    public static List<String> getAllFemalesBirthdate(String householdID) {

        String sql = "SELECT adolescent_birthdate FROM ec_client_index WHERE gender = 'female' AND household_id = '" + householdID + "' AND (deleted IS NULL OR deleted <> '1')";

        DataMap<String> dataMap = c -> getCursorValue(c, "adolescent_birthdate");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return null;
        else{
            return values;
        }

    }

    public static List<String> getAllChildrenBirthdate(String householdID) {

        String sql = "SELECT adolescent_birthdate FROM ec_client_index WHERE household_id = '" + householdID + "' AND case_status = '1' AND (deleted IS NULL OR deleted <> '1') AND adolescent_birthdate IS NOT NULL";

        DataMap<String> dataMap = c -> getCursorValue(c, "adolescent_birthdate");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return null;
        else{
            return values;
        }

    }
}

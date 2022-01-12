package com.bluecodeltd.ecap.chw.dao;


import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.Child;
import com.bluecodeltd.ecap.chw.model.Household;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class IndexPersonDao  extends AbstractDao {

    public static String checkIndexPerson (String baseEntityID) {

        String sql = "SELECT index_check_box FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "index_check_box");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }


    public static String countChildren(String householdID){

        String sql = "SELECT COUNT(*) AS childrenCount FROM ec_client_index WHERE household_id = '" + householdID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values == null || values.size() == 0)
            return "1";

        return values.get(0);

    }

    public static String getIndexStatus (String baseEntityID){

        String sql = "SELECT case_status FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "case_status");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }
    
    public static List<String> getGenders(String household_id){

        String sql = "SELECT gender FROM ec_client_index WHERE household_id =  '" + household_id + "' AND gender IS NOT NULL";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "gender");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values;
    }

    public static List<Child> getFamilyChildren(String householdID) {

        String sql = "SELECT unique_id, base_entity_id, first_name, last_name, adolescent_birthdate FROM ec_client_index WHERE household_id = '"+ householdID +"' ";

        List<Child> values = AbstractDao.readData(sql, getChildDataMap());// Remember to edit getChildDataMap METHOD Below
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static List<CasePlanModel> getDomainsById(String childID, String caseDate) {

        String sql = "SELECT * FROM ec_case_plan_domain WHERE unique_id = '" + childID + "' AND case_plan_date = '" + caseDate + "' AND case_plan_date IS NOT NULL ORDER BY case_plan_date DESC ";

        List<CasePlanModel> values = AbstractDao.readData(sql, getCasePlanMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }


    public static List<CasePlanModel> getCasePlansById(String childID) {

        String sql = "SELECT * FROM ec_vca_case_plan WHERE unique_id = '" + childID + "' AND case_plan_date IS NOT NULL ORDER BY case_plan_date DESC ";

        List<CasePlanModel> values = AbstractDao.readData(sql, getCasePlanMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static DataMap<CasePlanModel> getCasePlanMap() {
        return c -> {

            CasePlanModel record = new CasePlanModel();
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

            return record;
          };
        }


    public static DataMap<Child> getChildDataMap() {
        return c -> {
            Child record = new Child();
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setEntity_id(getCursorValue(c, "base_entity_id"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            return record;
        };
    }


    public static Child getChildByBaseId(String baseEntityID){
        String sql = "SELECT *, first_name AS adolescent_first_name,last_name As adolescent_last_name, gender as adolescent_gender FROM ec_client_index WHERE unique_id = '" + baseEntityID + "' ";
        DataMap<Child> dataMap = c -> {
            return new Child(
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
                    getCursorValue(c, "health_facility"),
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
                    getCursorValue(c, "date_offered_enrollment")

            );
        };
        List <Child> children =  AbstractDao.readData(sql, dataMap);
        if (children == null) {
            return null;
        }
        return children.get(0);
    }

}

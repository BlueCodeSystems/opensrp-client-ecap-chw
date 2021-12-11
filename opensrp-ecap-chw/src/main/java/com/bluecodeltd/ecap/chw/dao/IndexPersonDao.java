package com.bluecodeltd.ecap.chw.dao;

import android.util.Log;

import com.bluecodeltd.ecap.chw.domain.Mother;
import com.bluecodeltd.ecap.chw.model.Child;

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


    public static String countChildren(String baseEntityID){

        String sql = "SELECT COUNT(*) AS childrenCount FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "' OR unique_id = '"+ baseEntityID +"'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }

    public static String getIndexStatus (String baseEntityID){

        String sql = "SELECT case_status FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "case_status");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }
    
    public static List<String> getGenders(String baseEntityID){

        String sql = "SELECT gender FROM ec_client_index WHERE (base_entity_id = '" + baseEntityID + "' OR unique_id = '"+ baseEntityID +"') AND gender IS NOT NULL";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "gender");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values;
    }

    public static List<Child> getFamilyChildren(String familyBaseEntityID) {

        String sql = "SELECT base_entity_id, first_name, last_name, adolescent_birthdate FROM ec_client_index WHERE (base_entity_id = '" + familyBaseEntityID + "' OR unique_id = '"+ familyBaseEntityID +"')";

        List<Child> values = AbstractDao.readData(sql, getChildDataMap());// Remember to edit getChildDataMap METHOD Below
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }


    public static DataMap<Child> getChildDataMap() {
        return c -> {
            Child record = new Child();
            record.setEntity_id(getCursorValue(c, "base_entity_id"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            return record;
        };
    }


    public static Child getChildByBaseId(String baseEntityID){
        String sql = "SELECT * FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "' OR unique_id = '"+ baseEntityID +"'";
        DataMap<Child> dataMap = c -> {
            return new Child(
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "unique_id"),
                    getCursorValue(c, "first_name"),
                    getCursorValue(c, "last_name"),
                    getCursorValue(c, "adolescent_birthdate"),
                    getCursorValue(c, "subpop1"),
                    getCursorValue(c, "subpop2"),
                    getCursorValue(c, "subpop3"),
                    getCursorValue(c, "subpop4"),
                    getCursorValue(c, "subpop5"),
                    getCursorValue(c, "subpop6"),
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
                    getCursorValue(c, "gender")
            );
        };
        List <Child> children =  AbstractDao.readData(sql, dataMap);
        if (children == null) {
            return null;
        }
        return children.get(0);
    }

}

package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.domain.Mother;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class PMTCTMotherDao extends AbstractDao {


    public static Mother getMotherByBaseEntityId(String baseEntityID) {
        String sql = "select ec_mother_index.base_entity_id , ec_mother_index.mother_first_name AS first_name, ec_mother_index.mother_surname AS last_name from ec_mother_index where ec_mother_index.base_entity_id = '" + baseEntityID + "' "  ;
        DataMap<Mother> dataMap = c -> {
            return new Mother(
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "deleted"),
                    getCursorValue(c, "caregiver_name")
            );
        };

      List <Mother> mother =  AbstractDao.readData(sql, dataMap);
        if (mother == null) {
            return null;
        }
        return mother.get(0);
    }

    public static List<Mother> getMothers(String householdID) {

        String sql = "SELECT * FROM ec_mother_index WHERE household_id = '"+ householdID +"' ";

        DataMap<Mother> dataMap = c -> {
            return new Mother(
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "deleted"),
                    getCursorValue(c, "caregiver_name")
            );
        };

        List<Mother> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

}

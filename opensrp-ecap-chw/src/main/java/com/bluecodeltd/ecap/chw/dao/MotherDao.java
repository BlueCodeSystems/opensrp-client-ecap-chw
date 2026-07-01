package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.domain.PncBaby;

import com.bluecodeltd.ecap.chw.domain.Mother;
import com.bluecodeltd.ecap.chw.model.Child;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.smartregister.dao.AbstractDao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MotherDao  extends AbstractDao {


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

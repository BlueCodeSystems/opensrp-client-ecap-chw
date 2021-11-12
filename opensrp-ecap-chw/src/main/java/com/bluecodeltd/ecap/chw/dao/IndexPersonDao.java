package com.bluecodeltd.ecap.chw.dao;

import android.util.Log;

import com.bluecodeltd.ecap.chw.model.Child;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class IndexPersonDao  extends AbstractDao {

    public static String checkIndexPerson (String baseEntityID) {

        String sql = "SELECT index_check_box FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "index_check_box");

        List<String> values = AbstractDao.readData(sql, dataMap);

       // Log.d("mytagd", "jacob : " + values.get(0));

        return values.get(0);

    }

    public static String countChildren(String baseEntityID){

        String sql = "SELECT COUNT(*) AS childrenCount FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "childrenCount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }
    
    public static List<String> getGenders(String baseEntityID){

        String sql = "SELECT gender FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "gender");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values;
    }

    public static List<Child> getFamilyChildren(String familyBaseEntityID) {

        String sql = "SELECT base_entity_id, first_name, last_name FROM ec_client_index WHERE base_entity_id = '" + familyBaseEntityID + "'";

        List<Child> values = AbstractDao.readData(sql, getChildDataMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static DataMap<Child> getChildDataMap() {
        return c -> {
            Child record = new Child();
            record.setEntity_id(getCursorValue(c, "base_entity_id"));
            record.setFirstname(getCursorValue(c, "first_name"));
            record.setLastname(getCursorValue(c, "last_name"));
            return record;
        };
    }

}

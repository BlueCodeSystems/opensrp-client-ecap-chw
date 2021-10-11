package com.bluecodeltd.ecap.chw.dao;

import android.util.Log;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class IndexPersonDao  extends AbstractDao {

    public static String checkIndexPerson (String baseEntityID) {

        String sql = "SELECT index_check_box FROM ec_client_index WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "index_check_box");

        List<String> values = AbstractDao.readData(sql, dataMap);

       // Log.d("mytagd", "jacob : " + values.get(0));

        return values.get(0);

    }



}

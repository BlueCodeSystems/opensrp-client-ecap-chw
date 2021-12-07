package com.bluecodeltd.ecap.chw.dao;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HouseholdDao extends AbstractDao {

    public static String checkIfScreened (String baseEntityID) {

        String sql = "SELECT screened FROM ec_household WHERE base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "screened");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return values.get(0);

    }

}

package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.util.ServiceRegister;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ServiceRegisterDao extends AbstractDao {

    public static String countClients(ServiceRegister register) {
        String sql = "SELECT COUNT(*) v FROM " + register.table + " WHERE (delete_status IS NULL OR delete_status != '1')";
        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "v");
        List<String> values = AbstractDao.readData(sql, dataMap);
        if (values == null || values.isEmpty()) return "0";
        return values.get(0);
    }
}

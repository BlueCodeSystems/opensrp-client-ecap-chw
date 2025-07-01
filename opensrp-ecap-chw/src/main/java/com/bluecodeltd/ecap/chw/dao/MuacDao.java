package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MuacModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class MuacDao extends AbstractDao {

    public static MuacModel getMuac (String vcaID) {

        String sql = "SELECT * FROM ec_muac WHERE unique_id = '" + vcaID + "' ";

        List<MuacModel> values = AbstractDao.readData(sql, getMuacModelMap());

        if (values.size() == 0) {
            return null;
        }

        return values.get(0);
    }
    public static boolean areAllMuacGreen(String householdID) {
        String sql = "SELECT ec_muac.muac, ec_client_index.household_id " +
                "FROM ec_muac " +
                "JOIN ec_client_index ON ec_muac.unique_id = ec_client_index.unique_id " +
                "WHERE ec_client_index.household_id = '" + householdID + "'";

        List<MuacModel> values = AbstractDao.readData(sql, getMuacModelMap());

        if (values.isEmpty()) {
            return false;
        }

        for (MuacModel muacModel : values) {
            if (!"green".equalsIgnoreCase(muacModel.getMuac())) {
                return false;
            }
        }

        return true;
    }

    public static DataMap<MuacModel> getMuacModelMap() {
        return c -> {

            MuacModel record = new MuacModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setMuac(getCursorValue(c, "muac"));

            return record;
        };
    }
}

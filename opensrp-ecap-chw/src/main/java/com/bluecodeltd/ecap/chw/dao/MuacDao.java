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

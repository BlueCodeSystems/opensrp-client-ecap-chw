package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.PmtctChildOutcomeModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class PmtctChildOutcomeDao extends AbstractDao {
    public static PmtctChildOutcomeModel getPMCTChildOutcome(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_child_outcome WHERE unique_id = '" + pmtctID + "' ";

        List<PmtctChildOutcomeModel> values = AbstractDao.readData(sql, getPmtctChildOutcomeModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
    public static DataMap<PmtctChildOutcomeModel> getPmtctChildOutcomeModelMap() {
        return c -> {

            PmtctChildOutcomeModel record = new PmtctChildOutcomeModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setChild_outcome(getCursorValue(c, "child_outcome"));


            return record;
        };
    }
}

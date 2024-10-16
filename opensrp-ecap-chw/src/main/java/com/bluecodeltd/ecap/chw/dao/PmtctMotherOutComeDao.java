package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.PmctMotherOutcomeModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class PmtctMotherOutComeDao extends AbstractDao {
    public static PmctMotherOutcomeModel getPMCTmothersOutcome(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_mother_outcome WHERE pmtct_id = '" + pmtctID + "' ";

        List<PmctMotherOutcomeModel> values = AbstractDao.readData(sql, getPmctMotherOutcomeModelMap());

        if (values.size() == 0) {
            return null;
        }

        return values.get(0);
    }
    public static DataMap<PmctMotherOutcomeModel> getPmctMotherOutcomeModelMap() {
        return c -> {

            PmctMotherOutcomeModel record = new PmctMotherOutcomeModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setMothers_outcome(getCursorValue(c, "mothers_outcome"));


            return record;
        };
    }
}

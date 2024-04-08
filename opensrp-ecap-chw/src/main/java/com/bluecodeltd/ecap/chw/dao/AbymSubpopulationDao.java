package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.AbymSubpopulationModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class AbymSubpopulationDao extends AbstractDao {
    public static AbymSubpopulationModel getAbymSubpopulation(String vcaID) {

        String sql = "SELECT * FROM ec_client_index WHERE unique_id = '" + vcaID + "' ";

        List<AbymSubpopulationModel> values = AbstractDao.readData(sql, getAbymSubpopulationModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<AbymSubpopulationModel> getAbymSubpopulationModelMap() {
        return c -> {

            AbymSubpopulationModel record = new AbymSubpopulationModel();
            record.setAbym_years(getCursorValue(c, "abym_years"));
            record.setAbym_sexually_active(getCursorValue(c, "abym_sexually_active"));
            record.setAbym_preventions(getCursorValue(c, "abym_preventions"));
            record.setAbym_preventions_other(getCursorValue(c, "abym_preventions_other"));
            record.setAbym_sex_older_women(getCursorValue(c, "abym_sex_older_women"));
            record.setAbym_transactional_sex(getCursorValue(c, "abym_transactional_sex"));
            record.setAbym_sex_work(getCursorValue(c, "abym_sex_work"));
            record.setAbym_economically_insecure(getCursorValue(c, "abym_economically_insecure"));
            record.setAbym_violent_partner(getCursorValue(c, "abym_violent_partner"));
            record.setAbym_diagnosed(getCursorValue(c, "abym_diagnosed"));
            record.setAbym_hiv_tested(getCursorValue(c, "abym_hiv_tested"));
            record.setAbym_test_positive(getCursorValue(c, "abym_test_positive"));
            record.setAbym_undergone_vmmc(getCursorValue(c, "abym_undergone_vmmc"));
            record.setAbym_in_school(getCursorValue(c, "abym_in_school"));
            record.setAbym_economic_strengthening(getCursorValue(c, "abym_economic_strengthening"));



            return record;
        };
    }
}

package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.GradModel;


import org.smartregister.dao.AbstractDao;

import java.util.List;

public class GradDao extends AbstractDao {

    public static GradModel getGrad (String vcaID) {

        String sql = "SELECT ec_grad.*, ec_client_index.adolescent_birthdate, ec_client_index.is_hiv_positive, ec_client_index.art_number, ec_client_index.facility, ec_client_index.date_last_vl, ec_client_index.vl_last_result FROM ec_grad JOIN ec_client_index ON ec_grad.unique_id = ec_client_index.unique_id WHERE ec_grad.unique_id = '" + vcaID + "'";

        List<GradModel> values = AbstractDao.readData(sql, getGradModelMap());

        if (values.size() == 0) {
            return null;
        }

        return values.get(0);
    }


    public static DataMap<GradModel> getGradModelMap() {
        return c -> {

            GradModel record = new GradModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            record.setIs_hiv_positive(getCursorValue(c, "is_hiv_positive"));
            record.setArt_number(getCursorValue(c, "art_number"));
            record.setFacility(getCursorValue(c, "facility"));
            record.setDate_last_vl(getCursorValue(c, "date_last_vl"));
            record.setVl_last_result(getCursorValue(c, "vl_last_result"));
            record.setInfected_community(getCursorValue(c, "infected_community"));
            record.setInfection_correct(getCursorValue(c, "infection_correct"));
            record.setProtect_infection(getCursorValue(c, "protect_infection"));
            record.setPrevention_support(getCursorValue(c, "prevention_support"));
            record.setPrevention_correct(getCursorValue(c, "prevention_correct"));
            record.setSign_malnutrition(getCursorValue(c, "sign_malnutrition"));

            return record;
        };
    }

}

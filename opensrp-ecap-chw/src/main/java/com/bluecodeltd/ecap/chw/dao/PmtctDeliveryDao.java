package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.PmtctDeliveryDetailsModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class PmtctDeliveryDao extends AbstractDao {

    public static PmtctDeliveryDetailsModel getPmtctDeliveryDetails(String pmtctID) {

        String sql = "SELECT * FROM ec_pmtct_delivery_details WHERE pmtct_id = '" + pmtctID + "' ";

        List<PmtctDeliveryDetailsModel> values = AbstractDao.readData(sql, getPmtctDeliveryDetailsModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }
    public static DataMap<PmtctDeliveryDetailsModel> getPmtctDeliveryDetailsModelMap() {
        return c -> {

            PmtctDeliveryDetailsModel record = new PmtctDeliveryDetailsModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setPmtct_id(getCursorValue(c, "pmtct_id"));
            record.setDate_of_delivery(getCursorValue(c, "date_of_delivery"));
            record.setPlace_of_delivery(getCursorValue(c, "place_of_delivery"));
            record.setOn_art_at_time_of_delivery(getCursorValue(c, "on_art_at_time_of_delivery"));



            return record;
        };
    }
}

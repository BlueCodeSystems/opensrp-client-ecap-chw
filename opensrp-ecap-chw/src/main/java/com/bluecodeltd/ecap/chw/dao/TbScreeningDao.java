package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.TbScreeningModel;
import org.smartregister.dao.AbstractDao;

import java.util.List;

public class TbScreeningDao extends AbstractDao {

    public static TbScreeningModel getByVcaId(String vcaId) {
        String sql = "SELECT * FROM ec_tb_screening WHERE unique_id = '" + vcaId + "'";
        List<TbScreeningModel> values = AbstractDao.readData(sql, getMap());
        if (values == null || values.size() == 0) return null;
        return values.get(0);
    }

    public static DataMap<TbScreeningModel> getMap() {
        return c -> {
            TbScreeningModel record = new TbScreeningModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setUnique_tb_id(getCursorValue(c, "unique_tb_id"));
            record.setHistory_close_tb_contact(getCursorValue(c, "history_close_tb_contact"));
            record.setHistory_close_tb_contact_year(getCursorValue(c, "history_close_tb_contact_year"));
            record.setTb_symptoms_child_lt10(getCursorValue(c, "tb_symptoms_child_lt10"));
            record.setTb_symptoms_child_lt10_other(getCursorValue(c, "tb_symptoms_child_lt10_other"));
            record.setTb_symptoms_10plus(getCursorValue(c, "tb_symptoms_10plus"));
            record.setTb_symptoms_10plus_other(getCursorValue(c, "tb_symptoms_10plus_other"));
            record.setReferred_for_tb_evaluation(getCursorValue(c, "referred_for_tb_evaluation"));
            record.setTb_referral_comment(getCursorValue(c, "tb_referral_comment"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            return record;
        };
    }

    public static List<TbScreeningModel> listByVcaId(String vcaId) {
        String sql = "SELECT * FROM ec_tb_screening WHERE unique_id = '" + vcaId + "' ORDER BY last_interacted_with DESC";
        List<TbScreeningModel> values = AbstractDao.readData(sql, getMap());
        return values;
    }
}

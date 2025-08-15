package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.VcaCasePlanModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class VcaCasePlanDao extends AbstractDao {

    public static VcaCasePlanModel getVcaCasePlan (String vcaID) {

        String sql = "SELECT * FROM ec_vca_case_plan WHERE unique_id = '" + vcaID + "' AND (delete_status IS NULL OR delete_status <> '1')";

        List<VcaCasePlanModel> values = AbstractDao.readData(sql, getVcaCasePlanModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<VcaCasePlanModel> getVcaCasePlanModelMap() {
        return c -> {

            VcaCasePlanModel record = new VcaCasePlanModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setCase_plan_date(getCursorValue(c, "case_plan_date"));
            record.setCase_plan_status(getCursorValue(c, "case_plan_status"));
            record.setCase_plan_id(getCursorValue(c, "case_plan_id"));


            return record;
        };
    }



}

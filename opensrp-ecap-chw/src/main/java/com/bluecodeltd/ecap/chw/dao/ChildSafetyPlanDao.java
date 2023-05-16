package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.ChildSafetyPlanModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class ChildSafetyPlanDao extends AbstractDao {
    public static List<ChildSafetyPlanModel> getChildSafetyPlanModel (String vcaID) {

        String sql = "SELECT * FROM ec_child_safety_plan WHERE unique_id = '" + vcaID + "' AND (delete_status IS NULL OR delete_status <> '1') GROUP BY initial_date";

        List<ChildSafetyPlanModel> values = AbstractDao.readData(sql, getChildSafetyPlanModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }

    public static DataMap<ChildSafetyPlanModel> getChildSafetyPlanModelMap() {
        return c -> {

            ChildSafetyPlanModel record = new ChildSafetyPlanModel();
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setCaseworker_name(getCursorValue(c, "caseworker_name"));
            record.setPhone(getCursorValue(c, "phone"));
            record.setInitial_date(getCursorValue(c, "initial_date"));
            record.setCompletion_date(getCursorValue(c, "completion_date"));
            record.setSafety_threats(getCursorValue(c, "safety_threats"));
            record.setSafety_action(getCursorValue(c, "safety_action"));
            record.setSafety_protection(getCursorValue(c, "safety_protection"));
            record.setSafety_plans(getCursorValue(c, "safety_plans"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));

            return record;
        };
    }

}

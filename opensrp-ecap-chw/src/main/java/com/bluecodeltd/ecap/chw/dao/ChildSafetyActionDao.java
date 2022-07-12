package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.ChildSafetyActionModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class ChildSafetyActionDao extends AbstractDao {
    public static ChildSafetyActionModel getChildSafetyActionModel(String childId, String vcaID) {

        String sql = "SELECT * FROM ec_child_safely_action WHERE unique_id = '" + vcaID + "' ";

        List<ChildSafetyActionModel> values = AbstractDao.readData(sql, getChildSafetyActionModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static List<ChildSafetyActionModel> getActionsById(String childID, String actionDate) {

        String sql = "SELECT * FROM ec_child_safety_actions WHERE unique_id = '" + childID + "' AND initial_date = '" + actionDate + "' AND initial_date IS NOT NULL ORDER BY initial_date DESC";
        //String sql = "SELECT * FROM ec_child_safely_action WHERE unique_id = '" + childID + "' ";
        List<ChildSafetyActionModel> values = AbstractDao.readData(sql, getChildSafetyActionModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;

    }

    public static DataMap<ChildSafetyActionModel> getChildSafetyActionModelMap() {
        return c -> {

            ChildSafetyActionModel record = new ChildSafetyActionModel();
            record.setSafety_threats(getCursorValue(c, "safety_threats"));
            record.setSafety_action(getCursorValue(c, "safety_action"));
            record.setSafety_protection(getCursorValue(c, "safety_protection"));
            record.setSafety_plans(getCursorValue(c, "safety_plans"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setInitial_date(getCursorValue(c,"initial_date"));
            record.setUnique_id(getCursorValue(c,"unique_id"));

            return record;
        };
    }

}

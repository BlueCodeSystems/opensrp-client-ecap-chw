package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.ChildSafetyActionModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class ChildSafetyActionDao extends AbstractDao {
    public static ChildSafetyActionModel getChildSafetyActionModel (String vcaID) {

        String sql = "SELECT * FROM ec_child_safely_action WHERE unique_id = '" + vcaID + "' ";

        List<ChildSafetyActionModel> values = AbstractDao.readData(sql, getChildSafetyActionModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<ChildSafetyActionModel> getChildSafetyActionModelMap() {
        return c -> {

            ChildSafetyActionModel record = new ChildSafetyActionModel();
            record.setSafety_threats(getCursorValue(c, "safety_threats"));
            record.setSafety_action(getCursorValue(c, "safety_action"));
            record.setSafety_protection(getCursorValue(c, "safety_protection"));
            record.setSafety_plans(getCursorValue(c, "safety_plans"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));

            return record;
        };
    }

}

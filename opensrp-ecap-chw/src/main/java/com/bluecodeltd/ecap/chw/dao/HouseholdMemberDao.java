package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.HouseholdMemberModel;

import org.smartregister.dao.AbstractDao;

import java.util.List;

public class HouseholdMemberDao extends AbstractDao {
    public static HouseholdMemberModel getMember(String vcaID) {

        String sql = "SELECT * FROM ec_household_member WHERE unique_id = '" + vcaID + "' ";

        List<HouseholdMemberModel> values = AbstractDao.readData(sql, getHouseholdMemberModelMap());

        if (values.size() == 0) {
            return null;
        }


        return values.get(0);
    }

    public static DataMap<HouseholdMemberModel> getHouseholdMemberModelMap() {
        return c -> {

            HouseholdMemberModel record = new HouseholdMemberModel();
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setHousehold_id(getCursorValue(c, "household_id"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setMember_type(getCursorValue(c, "member_type"));
            record.setAdolescent_birthdate(getCursorValue(c, "adolescent_birthdate"));
            record.setGender(getCursorValue(c, "gender"));
            record.setIs_hiv_positive(getCursorValue(c, "is_hiv_positive"));
            record.setIs_biological(getCursorValue(c, "is_biological"));
            return record;
        };
    }
}

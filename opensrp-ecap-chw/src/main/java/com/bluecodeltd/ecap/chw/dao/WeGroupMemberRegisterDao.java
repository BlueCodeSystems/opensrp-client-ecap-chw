package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberRegisterModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class WeGroupMemberRegisterDao extends AbstractDao {

    public static List<WeGroupMemberRegisterModel> getWeGroupMembersByGroupId (String groupId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND group_id = '" + groupId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<WeGroupMemberRegisterModel> values = AbstractDao.readData(sql, getWeGroupMembersModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }
    public static DataMap<WeGroupMemberRegisterModel> getWeGroupMembersModelMap() {
        return c -> {

            WeGroupMemberRegisterModel record = new WeGroupMemberRegisterModel();


            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setFirst_name(getCursorValue(c, "first_name"));
            record.setLast_name(getCursorValue(c, "last_name"));
            record.setGender(getCursorValue(c, "gender"));

            return record;
        };
    }
}

package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.WeGroupMemberMeetingRegisterModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class WeGroupMemberMeetingRegisterDao extends AbstractDao {
    public static List<WeGroupMemberMeetingRegisterModel> getWeGroupMemberMeetingByGroupId(String groupId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_register_conducted, 7, 4) || '-' || substr(date_register_conducted, 4, 2) || '-' || substr(date_register_conducted, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member_register\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1')  AND group_id = '" + groupId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<WeGroupMemberMeetingRegisterModel> values = AbstractDao.readData(sql, getWeGroupMemberMeetingRegisterModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }
    public static AbstractDao.DataMap<WeGroupMemberMeetingRegisterModel> getWeGroupMemberMeetingRegisterModelMap() {
        return c -> {

            WeGroupMemberMeetingRegisterModel record = new WeGroupMemberMeetingRegisterModel();
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setGroup_id(getCursorValue(c, "group_id"));
            record.setDate_register_conducted(getCursorValue(c, "date_register_conducted"));
            record.setAttendance(getCursorValue(c, "attendance"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setFacilitator_id(getCursorValue(c, "facilitator_id"));



            return record;
        };
    }
}

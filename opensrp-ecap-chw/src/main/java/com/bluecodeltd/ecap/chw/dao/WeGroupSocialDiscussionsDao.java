package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MemberSocialFundModel;
import com.bluecodeltd.ecap.chw.model.WeGroupSocialDiscussionsModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class WeGroupSocialDiscussionsDao extends AbstractDao {
    public static List<WeGroupSocialDiscussionsModel> getWeGroupSocialDiscussionsByGroup (String groupId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_social_discussions\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND group_id = '" + groupId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<WeGroupSocialDiscussionsModel> values = AbstractDao.readData(sql, getWeGroupSocialDiscussionsModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }
    public static AbstractDao.DataMap<WeGroupSocialDiscussionsModel> getWeGroupSocialDiscussionsModelMap() {
        return c -> {

            WeGroupSocialDiscussionsModel record = new WeGroupSocialDiscussionsModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setDate_created(getCursorValue(c, "date_created"));
            record.setGroup_name(getCursorValue(c, "group_name"));
            record.setCycle_number(getCursorValue(c, "cycle_number"));
            record.setGroup_number(getCursorValue(c, "group_number"));
            record.setGroup_id(getCursorValue(c, "group_id"));
            record.setDate_of_meeting(getCursorValue(c, "date_of_meeting"));
            record.setMeeting_number(getCursorValue(c, "meeting_number"));
            record.setName_of_moderator(getCursorValue(c, "name_of_moderator"));
            record.setMeeting_agenda(getCursorValue(c, "meeting_agenda"));
            record.setMeeting_decisions(getCursorValue(c, "meeting_decisions"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setFacilitator_id(getCursorValue(c, "facilitator_id"));




            return record;
        };
    }
}

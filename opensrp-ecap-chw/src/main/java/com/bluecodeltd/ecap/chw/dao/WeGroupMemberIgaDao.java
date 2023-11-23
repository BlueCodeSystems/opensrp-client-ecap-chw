package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.MemberIGAModel;
import com.bluecodeltd.ecap.chw.model.MemberLoanModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

public class WeGroupMemberIgaDao extends AbstractDao {


    public static List<MemberIGAModel> getWeGroupMembersIgaByGroupId (String groupId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member_iga\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND group_id = '" + groupId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<MemberIGAModel> values = AbstractDao.readData(sql, getMemberIGAModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }
    public static List<MemberIGAModel> getWeGroupMembersIgaByUniqueId (String uniqueId) {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group_member_iga\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND unique_id = '" + uniqueId + "'\n" +
                "ORDER BY sortable_date DESC";

        List<MemberIGAModel> values = AbstractDao.readData(sql, getMemberIGAModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }



    public static int getTotalAmount(String memberID) {
        String sql = "SELECT SUM(amount) as amount\n" +
                "FROM ec_we_group_member_iga\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND unique_id = '" + memberID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "amount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values.get(0) != null) {
            try {
                return Integer.parseInt(values.get(0));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }
    public static int getTotalGroupAmount(String groupID) {
        String sql = "SELECT SUM(amount) as amount\n" +
                "FROM ec_we_group_member_iga\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1') AND group_id = '" + groupID + "'";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "amount");

        List<String> values = AbstractDao.readData(sql, dataMap);

        if (values.get(0) != null) {
            try {
                return Integer.parseInt(values.get(0));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static DataMap<MemberIGAModel> getMemberIGAModelMap() {

        return c -> {

            MemberIGAModel record = new MemberIGAModel();
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setDate_created(getCursorValue(c, "date_created"));
            record.setGroup_id(getCursorValue(c, "group_id"));
            record.setUnique_id(getCursorValue(c, "unique_id"));
            record.setAmount(getCursorValue(c, "amount"));
            record.setMeeting_number(getCursorValue(c, "meeting_number"));
            record.setDate_of_meeting(getCursorValue(c, "date_of_meeting"));
            record.setCycle_number(getCursorValue(c, "cycle_number"));
            record.setMeeting_agenda(getCursorValue(c, "meeting_agenda"));
            record.setDecisions(getCursorValue(c, "decisions"));
            record.setIga_type(getCursorValue(c, "iga_type"));
            record.setNumber_of_igas(getCursorValue(c, "number_of_igas"));
            record.setModerator(getCursorValue(c, "moderator"));
            record.setType_of_group_iga(getCursorValue(c, "type_of_group_iga"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setFacilitator_id(getCursorValue(c, "facilitator_id"));


            return record;
        };
    }
}

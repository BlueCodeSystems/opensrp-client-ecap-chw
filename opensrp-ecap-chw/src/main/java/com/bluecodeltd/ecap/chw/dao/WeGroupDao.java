package com.bluecodeltd.ecap.chw.dao;

import com.bluecodeltd.ecap.chw.model.HTSlinksModel;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import org.smartregister.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeGroupDao extends AbstractDao {
    public static int groupCount(){

        String sql = "SELECT COUNT(*) as groups\n" +
                "FROM ec_we_group\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1')";

        AbstractDao.DataMap<String> dataMap = c -> getCursorValue(c, "groups");

        List<String> values = AbstractDao.readData(sql, dataMap);

        return Integer.parseInt(values.get(0));

    }

    public static List<WeGroupModel> getWeGroups () {


        String sql = "SELECT *,\n" +
                "       strftime('%Y-%m-%d', substr(date_created, 7, 4) || '-' || substr(date_created, 4, 2) || '-' || substr(date_created, 1, 2)) as sortable_date\n" +
                "FROM ec_we_group\n" +
                "WHERE (delete_status IS NULL OR delete_status <> '1')\n" +
                "ORDER BY sortable_date DESC";

        List<WeGroupModel> values = AbstractDao.readData(sql, getWeGroupModelMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }

    public static DataMap<WeGroupModel> getWeGroupModelMap() {
        return c -> {

            WeGroupModel record = new WeGroupModel();
            record.setBase_entity_id(getCursorValue(c, "base_entity_id"));
            record.setDelete_status(getCursorValue(c, "delete_status"));
            record.setLast_interacted_with(getCursorValue(c, "last_interacted_with"));
            record.setDate_client_created(getCursorValue(c, "date_client_created"));
            record.setGroup_name(getCursorValue(c, "group_name"));
            record.setCycle_number(getCursorValue(c, "cycle_number"));
            record.setGroup_id(getCursorValue(c, "group_id"));
            record.setAnnual_interest_rate(getCursorValue(c, "annual_interest_rate"));
            record.setFirst_training_meeting_date(getCursorValue(c, "first_training_meeting_date"));
            record.setDate_savings_started(getCursorValue(c, "date_savings_started"));
            record.setReinvested_savings_cycle_start(getCursorValue(c, "reinvested_savings_cycle_start"));
            record.setRegistered_members_cycle_start(getCursorValue(c, "registered_members_cycle_start"));
            record.setGroup_mgt(getCursorValue(c, "group_mgt"));


            return record;
        };
    }
}


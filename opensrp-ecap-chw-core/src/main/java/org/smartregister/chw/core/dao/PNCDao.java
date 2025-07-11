package org.smartregister.chw.core.dao;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.model.ChildModel;
import org.smartregister.dao.AbstractDao;

import java.util.Date;
import java.util.List;

public class PNCDao extends AbstractDao {

    @Nullable
    public static Date getPNCDeliveryDate(String baseEntityID) {
        String sql = "select delivery_date from ec_pregnancy_outcome where base_entity_id = '" + baseEntityID + "'";

        AbstractDao.DataMap<Date> dataMap = cursor -> getCursorValueAsDate(cursor, "delivery_date", getNativeFormsDateFormat());

        List<Date> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static boolean isPNCMember(String baseEntityID) {
        String sql = "select count(ec_pregnancy_outcome.base_entity_id) count from ec_pregnancy_outcome where base_entity_id = '" + baseEntityID + "' and is_closed = 0 and delivery_date IS NOT NULL";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static int getPncWomenCount(String familyBaseID) {

        String sql = "SELECT count(ear.base_entity_id) count FROM ec_pregnancy_outcome as ear " +
                "INNER JOIN ec_family_member efm ON ear.base_entity_id = efm.base_entity_id " +
                "WHERE ear.relational_id = '" + familyBaseID + "' " +
                "AND ear.delivery_date IS NOT NULL AND ear.is_closed =0 AND efm.is_closed= 0";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        return res.get(0);
    }

    public static MemberObject getMember(String baseEntityID) {
        String sql = "select m.base_entity_id , m.unique_id , m.relational_id , m.dob , m.first_name , m.middle_name , m.last_name , m.gender , " +
                "m.phone_number , m.other_phone_number , f.first_name family_name , f.primary_caregiver , f.family_head , " +
                "fh.first_name family_head_first_name , fh.middle_name family_head_middle_name, fh.last_name family_head_last_name, " +
                "fh.phone_number family_head_phone_number , ar.confirmed_visits , f.village_town , ar.last_interacted_with , " +
                "ar.last_contact_visit , ar.visit_not_done , ar.last_menstrual_period  , al.date_created  , ar.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join ec_anc_register ar on ar.base_entity_id = m.base_entity_id " +
                "inner join ec_anc_log al on al.base_entity_id =ar.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "where m.base_entity_id = '" + baseEntityID + "' ";

        DataMap<MemberObject> dataMap = cursor -> {
            MemberObject memberObject = new MemberObject();
            memberObject.setLastMenstrualPeriod(getCursorValue(cursor, "last_menstrual_period"));
            memberObject.setChwMemberId(getCursorValue(cursor, "unique_id", ""));
            memberObject.setBaseEntityId(getCursorValue(cursor, "base_entity_id", ""));
            memberObject.setFamilyBaseEntityId(getCursorValue(cursor, "relational_id", ""));
            memberObject.setFamilyHead(getCursorValue(cursor, "family_head", ""));

            String familyHeadName = getCursorValue(cursor, "family_head_first_name", "") + " "
                    + getCursorValue(cursor, "family_head_middle_name", "");

            familyHeadName = (familyHeadName.trim() + " " + getCursorValue(cursor, "family_head_last_name", "")).trim();

            memberObject.setFamilyHeadName(familyHeadName);
            memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "family_head_phone_number", ""));
            memberObject.setPrimaryCareGiver(getCursorValue(cursor, "primary_caregiver"));
            memberObject.setFamilyName(getCursorValue(cursor, "family_name", ""));
            memberObject.setLastContactVisit(getCursorValue(cursor, "last_contact_visit"));
            memberObject.setLastInteractedWith(getCursorValue(cursor, "last_interacted_with"));
            memberObject.setFirstName(getCursorValue(cursor, "first_name", ""));
            memberObject.setMiddleName(getCursorValue(cursor, "middle_name", ""));
            memberObject.setLastName(getCursorValue(cursor, "last_name", ""));
            memberObject.setDob(getCursorValue(cursor, "dob"));
            memberObject.setPhoneNumber(getCursorValue(cursor, "phone_number", ""));
            memberObject.setConfirmedContacts(getCursorIntValue(cursor, "confirmed_visits", 0));
            memberObject.setDateCreated(getCursorValue(cursor, "date_created"));
            memberObject.setAddress(getCursorValue(cursor, "village_town"));
            memberObject.setHasAncCard(getCursorValue(cursor, "has_anc_card", ""));

            return memberObject;
        };

        List<MemberObject> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static List<ChildModel> childrenForPncWoman(String baseEntityId) {
        String sql = String.format("select c.first_name || ' ' || c.middle_name || ' ' || c.last_name as child_name, c.dob , c.first_name, c.base_entity_id " +
                "FROM ec_child c " +
                "INNER JOIN ec_family_member fm on fm.base_entity_id = c.base_entity_id " +
                "WHERE c.mother_entity_id = '" + baseEntityId + "' COLLATE NOCASE " +
                "AND c.entry_point = 'PNC' " +
                "AND c.is_closed = 0 " +
                "AND fm.is_closed = 0 " +
                "AND ( date (c.dob, '+28 days') > date()) " +
                "ORDER by c.first_name ASC");

        DataMap<ChildModel> dataMap = cursor ->
                new ChildModel(getCursorValue(cursor, "child_name"), getCursorValue(cursor, "dob"), getCursorValue(cursor, "first_name"), getCursorValue(cursor, "base_entity_id"));

        return readData(sql, dataMap);
    }

    public static String earlyBreastFeeding(String motherBaseEntityId, String visitId) {
        String sql = "SELECT early_bf_1hr " +
                "FROM ec_child " +
                "INNER JOIN visits v ON v.visit_id = '" + visitId + "' COLLATE NOCASE " +
                "AND mother_entity_id = '" + motherBaseEntityId + "' COLLATE NOCASE ";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "early_bf_1hr");

        List<String> res = readData(sql, dataMap);
        if (res != null && res.size() > 0) {
            return res.get(0);
        }
        return null;
    }
}

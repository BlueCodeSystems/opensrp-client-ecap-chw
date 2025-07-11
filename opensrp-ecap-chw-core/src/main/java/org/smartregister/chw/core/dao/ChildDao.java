package org.smartregister.chw.core.dao;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.smartregister.chw.core.domain.Child;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.dao.AbstractDao;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.CoreReferralUtils.getCommonRepository;
import static org.smartregister.chw.core.utils.Utils.getDateDifferenceInDays;

public class ChildDao extends AbstractDao {

    public static List<Child> getFamilyChildren(String familyBaseEntityID) {
        String sql = "select  c.base_entity_id , c.first_name , c.last_name , c.middle_name , c.mother_entity_id , c.relational_id , c.dob , c.date_created ,  lastVisit.last_visit_date , last_visit_not_done_date " +
                "from ec_child c " +
                "inner join ec_family_member m on c.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE  " +
                "left join ( " +
                " select base_entity_id , max(visit_date) last_visit_date " +
                " from visits " +
                " where visit_type in ('Child Home Visit') " +
                " group by base_entity_id " +
                ") lastVisit on lastVisit.base_entity_id = c.base_entity_id " +
                "left join ( " +
                " select base_entity_id , max(visit_date) last_visit_not_done_date " +
                " from visits " +
                " where visit_type in ('Visit not done') " +
                " group by base_entity_id " +
                ") lastVisitNotDone on lastVisitNotDone.base_entity_id = c.base_entity_id " +
                "where f.base_entity_id = '" + familyBaseEntityID + "' " +
                "and  m.date_removed is null and m.is_closed = 0 " +
                "and ((( julianday('now') - julianday(c.dob))/365.25) < 5) and c.is_closed = 0  " +
                " and (( ( ifnull(entry_point,'') <> 'PNC' ) ) or (ifnull(entry_point,'') = 'PNC' and ( date (c.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = mother_entity_id ) = 0)))  or (ifnull(entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = mother_entity_id ) = 1)) ";

        List<Child> values = AbstractDao.readData(sql, getChildDataMap());
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }

    public static boolean isPhysicallyChallenged(String baseEntityId) {
        String sql = "select count(*) count FROM ec_child\n" +
                "    where base_entity_id = '" + baseEntityId + "'" +
                "    and physically_challenged = 'Yes'";

        AbstractDao.DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        return res != null && res.size() != 0 && res.get(0) != 0;
    }

    public static String getChildQuery(String baseEntityID) {
        return "select  c.base_entity_id , c.first_name , c.last_name , c.middle_name , c.mother_entity_id , c.relational_id , c.dob , c.date_created ,  lastVisit.last_visit_date , last_visit_not_done_date " +
                "from ec_child c " +
                "inner join ec_family_member m on c.base_entity_id = m.base_entity_id COLLATE NOCASE " +
                "inner join ec_family f on f.base_entity_id = m.relational_id COLLATE NOCASE  " +
                "left join ( " +
                " select base_entity_id , max(visit_date) last_visit_date " +
                " from visits " +
                " where visit_type in ('Child Home Visit') " +
                " group by base_entity_id " +
                ") lastVisit on lastVisit.base_entity_id = c.base_entity_id " +
                "left join ( " +
                " select base_entity_id , max(visit_date) last_visit_not_done_date " +
                " from visits " +
                " where visit_type in ('Visit not done') " +
                " group by base_entity_id " +
                ") lastVisitNotDone on lastVisitNotDone.base_entity_id = c.base_entity_id " +
                "where c.base_entity_id = '" + baseEntityID + "' " +
                "and  m.date_removed is null and m.is_closed = 0 " +
                "and ((( julianday('now') - julianday(c.dob))/365.25) < 5) and c.is_closed = 0  " +
                " and (( ( ifnull(entry_point,'') <> 'PNC' ) ) or (ifnull(entry_point,'') = 'PNC' and ( date (c.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = mother_entity_id ) = 0)))  or (ifnull(entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = mother_entity_id ) = 1)) ";
    }

    public static Child getChild(String baseEntityID) {
        String sql = getChildQuery(baseEntityID);

        List<Child> values = AbstractDao.readData(sql, getChildDataMap());
        if (values == null || values.size() != 1)
            return null;

        return values.get(0);
    }


    public static DataMap<Child> getChildDataMap() {
        return c -> {
            Child record = new Child();
            record.setBaseEntityID(getCursorValue(c, "base_entity_id"));
            record.setFirstName(getCursorValue(c, "first_name"));
            record.setLastName(getCursorValue(c, "last_name"));
            record.setMiddleName(getCursorValue(c, "middle_name"));
            record.setMotherBaseEntityID(getCursorValue(c, "mother_entity_id"));
            record.setFamilyBaseEntityID(getCursorValue(c, "relational_id"));
            record.setDateOfBirth(getCursorValueAsDate(c, "dob", getDobDateFormat()));
            record.setDateCreated(getCursorValueAsDate(c, "date_created", getDobDateFormat()));
            record.setLastVisitDate(getCursorValueAsDate(c, "last_visit_date"));
            record.setLastVisitNotDoneDate(getCursorValueAsDate(c, "last_visit_not_done_date"));
            return record;
        };
    }

    public static boolean isChild(String baseEntityID) {
        String sql = "select count(c.base_entity_id) count from ec_child c where c.base_entity_id = '" + baseEntityID + "' " +
                "and c.is_closed = 0" +
                " and (( ( ifnull(entry_point,'') <> 'PNC' ) ) or (ifnull(entry_point,'') = 'PNC' and ( date (c.dob, '+28 days') <= date() and ((SELECT is_closed FROM ec_family_member WHERE base_entity_id = mother_entity_id ) = 0)))  or (ifnull(entry_point,'') = 'PNC'  and (SELECT is_closed FROM ec_family_member WHERE base_entity_id = mother_entity_id ) = 1)) ";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static String queryColumnWithIdentifier(String identifierType, String id, String requiredField) {
        String selection = identifierType + " = ? ";
        String[] selectionArgs = new String[]{id};
        return queryColumnWithEntityId(selection, selectionArgs, requiredField);
    }

    private static String queryColumnWithEntityId(String selection, String[] selectionArgs, String columnName) {
        SQLiteDatabase database = getRepository().getReadableDatabase();
        if (database == null) {
            return null;
        }
        String[] projectionArgs = new String[]{columnName};
        try (net.sqlcipher.Cursor cursor = database.query(CoreConstants.TABLE_NAME.CHILD, projectionArgs, selection, selectionArgs, null, null, null);) {

            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(columnName));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public static boolean isMotherAlive(String motherBaseEntityId) {
        String sql = "SELECT is_closed FROM ec_family_member WHERE base_entity_id = mother_entity_id";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "is_closed");

        List<Integer> res = readData(sql, dataMap);
        return res != null && res.equals(0);
    }


    public static Triple<String, String, String> getChildProfileData(String childBaseEntityId) {
        String query = CoreChildUtils.mainSelect(CoreConstants.TABLE_NAME.CHILD, CoreConstants.TABLE_NAME.FAMILY, CoreConstants.TABLE_NAME.FAMILY_MEMBER, childBaseEntityId);
        try (Cursor cursor = getCommonRepository(CoreConstants.TABLE_NAME.CHILD).rawCustomQueryForAdapter(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                CommonPersonObject personObject = getCommonRepository(CoreConstants.TABLE_NAME.CHILD).readAllcommonforCursorAdapter(cursor);
                String gender = org.smartregister.family.util.Utils.getValue(personObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
                String dob = org.smartregister.family.util.Utils.getValue(personObject.getColumnmaps(), DBConstants.KEY.DOB, false);
                String ageInDays = getDateDifferenceInDays(new Date(), DateTime.parse(dob).toDate());

                return Triple.of(ageInDays, dob, gender);
            }
        } catch (Exception ex) {
            Timber.e(ex, "getChildProfileData");
        }
        return null;
    }

    public static List<String> getFamilyMembers(String baseEntityId) {
        String sql = "SELECT base_entity_id from ec_family_member" +
                " where relational_id = '" + baseEntityId + "'" +
                " and is_closed = 0";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "base_entity_id");

        List<String> values = readData(sql, dataMap);
        if (values == null || values.size() == 0)
            return new ArrayList<>();

        return values;
    }
}

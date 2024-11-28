package org.smartregister.chw.core.repository;

import net.sqlcipher.Cursor;

import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.domain.Task;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.repository.TaskRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class ChwTaskRepository extends TaskRepository {

    public ChwTaskRepository(TaskNotesRepository taskNotesRepository) {
        super(taskNotesRepository);
    }

    public List<Task> getTasksWithoutClientsAndEvents() {
        List<Task> tasks = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(String.format(
                "SELECT * FROM %s " +
                        " WHERE %s NOT IN " +
                        " (SELECT %s FROM %s " +
                        " INNER JOIN %s ON %s.%s = %s.%s " +
                        " INNER JOIN %s ON %s.%s = %s.%s " +
                        " WHERE %s =? OR %s IS NULL) AND %s <> %S AND %s IS NOT NULL "
                , "task", "_id", "_id", "task", "ec_family_member", "ec_family_member", "base_entity_id",
//                "task", "for", EventClientRepository.Table.event.name(), EventClientRepository.Table.event.name(), Event.form_submission_id_key,
                "task", "reason_reference", "sync_status", "server_version","status","'COMPLETED'","reason_reference"), new String[]{BaseRepository.TYPE_Created})) {
            while (cursor.moveToNext()) {
                tasks.add(readCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return tasks;
    }

    public Set<Task> getReferralTasksForClientByStatus(String planId, String forEntity, String businessStatus ) {
        Cursor cursor = null;
        Set<Task> taskSet = new HashSet<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT * FROM %s WHERE %s=? COLLATE NOCASE AND %s =? AND %s = ? COLLATE NOCASE AND code = 'Referral' ",
                    TASK_TABLE, CoreConstants.DB_CONSTANTS.PLAN_ID, CoreConstants.DB_CONSTANTS.BUSINESS_STATUS, CoreConstants.DB_CONSTANTS.FOR), new String[]{planId, businessStatus, forEntity});
            while (cursor.moveToNext()) {
                Task task = readCursor(cursor);
                taskSet.add(task);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return taskSet;
    }

    public List<Map<String, String>> getReferredTaskEvents() {
        List<Map<String, String>> tasksEvents = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(
                String.format(
                        "SELECT * FROM %s LEFT JOIN %s ON %s.%s = %s.%s WHERE %s = ?  ORDER BY %s DESC",
                        CoreConstants.TABLE_NAME.TASK, CoreConstants.TABLE_NAME.REFERRAL, CoreConstants.TABLE_NAME.TASK, ChwDBConstants.TaskTable.REASON_REFERENCE, CoreConstants.TABLE_NAME.REFERRAL, org.smartregister.family.util.DBConstants.KEY.BASE_ENTITY_ID,
                        ChwDBConstants.TaskTable.BUSINESS_STATUS, ChwDBConstants.TaskTable.START),
                new String[]{CoreConstants.BUSINESS_STATUS.REFERRED})) {
            while (cursor.moveToNext()) {
                HashMap<String, String> columns = new HashMap<String, String>();
                int columnCount = cursor.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    columns.put(cursor.getColumnName(i), cursor.getString(i));
                }
                tasksEvents.add(columns);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return tasksEvents;
    }

}

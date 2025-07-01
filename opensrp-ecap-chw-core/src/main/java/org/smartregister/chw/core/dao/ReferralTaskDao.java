package org.smartregister.chw.core.dao;

import org.joda.time.DateTime;
import org.smartregister.dao.AbstractDao;
import org.smartregister.domain.Period;
import org.smartregister.domain.Task;

import java.util.List;

public class ReferralTaskDao extends AbstractDao {
    /**
     * This method return a list of referral tasks that were marked as done on Facility and are now
     * supposed to be dismissed (Completed) by the chw; 3 days after the CHW marked them as done.
     *
     * @return list of referral tasks to be completed
     */
    public static List<Task> getToBeCompletedReferralTasks() {
        String queryStatement =
                "SELECT task.*\n" +
                        "FROM task\n" +
                        "         INNER JOIN ec_referral_dismissal on task._id = ec_referral_dismissal.referral_task\n" +
                        "WHERE (Cast((JulianDay(ec_referral_dismissal.notification_dismissal_date) -\n" +
                        "             JulianDay(date('now'))) As Integer) < 0\n" +
                        "    OR ec_referral_dismissal.notification_dismissal_date is null)\n" +
                        "  AND task.status = 'READY'\n" +
                        "  AND task.business_status = 'Complete'";

        return AbstractDao.readData(queryStatement, mapColumnValuesToTask());
    }

    private static AbstractDao.DataMap<Task> mapColumnValuesToTask() {
        return row -> {
            Task task = new Task();
            task.setIdentifier(getCursorValue(row, "_id"));
            task.setPlanIdentifier(getCursorValue(row, "plan_id"));
            task.setGroupIdentifier(getCursorValue(row, "group_id"));
            task.setStatus(Task.TaskStatus.valueOf(getCursorValue(row, "status")));
            task.setBusinessStatus(getCursorValue(row, "business_status"));
            task.setPriority(Task.TaskPriority.valueOf(getCursorValue(row, "priority")));
            task.setCode(getCursorValue(row, "code"));
            task.setDescription(getCursorValue(row, "description"));
            task.setFocus(getCursorValue(row, "focus"));
            task.setForEntity(getCursorValue(row, "for"));

            Period period = new Period();
            period.setStart(new DateTime(getCursorValueAsDate(row, "start")));
            period.setEnd(new DateTime(getCursorValueAsDate(row, "end")));
            task.setExecutionPeriod(period);

            task.setAuthoredOn(new DateTime(getCursorValueAsDate(row, "authored_on")));
            task.setLastModified(new DateTime(getCursorValueAsDate(row, "last_modified")));
            task.setOwner(getCursorValue(row, "owner"));
            task.setSyncStatus(getCursorValue(row, "sync_status"));
            task.setServerVersion(getCursorLongValue(row, "server_version"));
            task.setStructureId(getCursorValue(row, "structure_id"));
            task.setReasonReference(getCursorValue(row, "reason_reference"));
            task.setLocation(getCursorValue(row, "location"));
            task.setRequester(getCursorValue(row, "requester"));
            return task;
        };
    }
}

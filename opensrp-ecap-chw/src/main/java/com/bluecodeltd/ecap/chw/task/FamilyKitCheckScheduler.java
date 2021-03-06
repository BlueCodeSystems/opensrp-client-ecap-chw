package com.bluecodeltd.ecap.chw.task;

import org.joda.time.DateTime;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.rule.FamilyKitAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.dao.FamilyDao;
import com.bluecodeltd.ecap.chw.dao.FamilyKitDao;

import java.util.Date;
import java.util.List;

public class FamilyKitCheckScheduler extends BaseTaskExecutor {

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {
        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);

        long lastFamilyKitDate = FamilyKitDao.getLastFamilyKitDate(baseEntityID);
        long dateCreatedFamily = FamilyDao.getFamilyCreateDate(baseEntityID);

        FamilyKitAlertRule alertRule = new FamilyKitAlertRule(ChwApplication.getInstance().getApplicationContext(), lastFamilyKitDate, dateCreatedFamily);
        baseScheduleTask.setScheduleDueDate(alertRule.getDueDate());
        baseScheduleTask.setScheduleExpiryDate(alertRule.getExpiryDate());
        baseScheduleTask.setScheduleCompletionDate(alertRule.getCompletionDate());
        baseScheduleTask.setScheduleOverDueDate(new DateTime(alertRule.getLastDayOfMonth(alertRule.getDueDate())).plusDays(1).toDate());
        if (FamilyDao.familyHasChildUnderFive(baseEntityID))
            return toScheduleList(baseScheduleTask);
        else
            return null;
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.FAMILY_KIT;
    }

    @Override
    public String getScheduleGroup() {
        return CoreConstants.SCHEDULE_GROUPS.HOME_VISIT;
    }
}

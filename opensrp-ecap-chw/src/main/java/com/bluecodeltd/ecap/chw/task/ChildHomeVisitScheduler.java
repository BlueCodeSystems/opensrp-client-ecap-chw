package com.bluecodeltd.ecap.chw.task;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.dao.ChwChildDao;
import com.bluecodeltd.ecap.chw.service.ChildAlertService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChildHomeVisitScheduler extends BaseTaskExecutor {
    private Flavor flavor = new ChildHomeVisitSchedulerFlv();

    @Override
    public void resetSchedule(String baseEntityID, String scheduleName) {
        super.resetSchedule(baseEntityID, scheduleName);
        ChwApplication.getInstance().getScheduleRepository().deleteScheduleByGroup(getScheduleGroup(), baseEntityID);
        ChildAlertService.updateAlerts(baseEntityID);
    }

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate) {
        // recompute the home visit task for this child
        BaseScheduleTask baseScheduleTask = prepareNewTaskObject(baseEntityID);
        if (ChwChildDao.isPNCChild(baseEntityID)) return new ArrayList<>();

        return flavor.generateTasks(baseEntityID, eventName, eventDate, baseScheduleTask);
    }

    @Override
    public String getScheduleName() {
        return CoreConstants.SCHEDULE_TYPES.CHILD_VISIT;
    }

    @Override
    public String getScheduleGroup() {
        return CoreConstants.SCHEDULE_GROUPS.HOME_VISIT;
    }


    public interface Flavor {
        List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate, BaseScheduleTask baseScheduleTask);
    }

}

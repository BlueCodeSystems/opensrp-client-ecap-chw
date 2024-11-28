package org.smartregister.chw.core.sync.intent;

import android.app.IntentService;
import android.content.Intent;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.repository.ChwTaskRepository;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.referral.util.DBConstants;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.Period;
import org.smartregister.domain.Task;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.ChwDBConstants.TaskTable;
import static org.smartregister.chw.core.utils.CoreConstants.DB_CONSTANTS.ID;
import static org.smartregister.chw.core.utils.CoreConstants.TASKS_FOCUS;

/**
 * Created by cozej4 on 2020-02-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class CloseExpiredReferralsIntentService extends IntentService {

    private static final String TAG = CloseExpiredReferralsIntentService.class.getSimpleName();
    private final ChwTaskRepository taskRepository;
    private AllSharedPreferences sharedPreferences;
    private ECSyncHelper syncHelper;


    public CloseExpiredReferralsIntentService() {
        super(TAG);
        taskRepository = (ChwTaskRepository) CoreChwApplication.getInstance()
                .getTaskRepository();
        sharedPreferences = Utils.getAllSharedPreferences();
        syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Map<String, String>> referredTasks = taskRepository.getReferredTaskEvents();

        for (Map<String, String> task : referredTasks) {
            String appointmentDate = task.get(DBConstants.Key.REFERRAL_APPOINTMENT_DATE);
            String startDate = task.get(TaskTable.START);

            String focus = task.get(ChwDBConstants.TaskTable.FOCUS);
            if (focus != null && startDate != null) {
                Calendar expiredCalendar = Calendar.getInstance();
                switch (focus) {
                    case TASKS_FOCUS.ANC_DANGER_SIGNS:
                    case TASKS_FOCUS.PNC_DANGER_SIGNS:
                    case TASKS_FOCUS.SUSPECTED_MALARIA:
                        expiredCalendar.setTimeInMillis(Long.parseLong(startDate));
                        expiredCalendar.add(Calendar.HOUR_OF_DAY, 24);
                        checkIfExpired(expiredCalendar, task);
                        break;
                    case TASKS_FOCUS.SICK_CHILD:
                    case TASKS_FOCUS.FP_SIDE_EFFECTS:
                        Calendar referralNotYetDoneCalendar = Calendar.getInstance();
                        referralNotYetDoneCalendar.setTimeInMillis(Long.parseLong(startDate));
                        referralNotYetDoneCalendar.add(Calendar.DAY_OF_MONTH, 3);

                        expiredCalendar.setTimeInMillis(Long.parseLong(startDate));
                        expiredCalendar.add(Calendar.DAY_OF_MONTH, 7);

                        boolean isExpired = checkIfExpired(expiredCalendar, task);
                        if (Objects.requireNonNull(task.get(TaskTable.STATUS)).equals(Task.TaskStatus.READY.name()) && !isExpired) {
                            checkIfNotYetDone(referralNotYetDoneCalendar, task);
                        }
                        break;
                    case TASKS_FOCUS.SUSPECTED_TB:
                        if (appointmentDate != null && !appointmentDate.isEmpty()) {
                            expiredCalendar.setTimeInMillis(new BigDecimal(appointmentDate).longValue());
                            expiredCalendar.add(Calendar.DAY_OF_MONTH, 3);
                        }
                        checkIfExpired(expiredCalendar, task);
                        break;
                    default:
                        if (appointmentDate != null && !appointmentDate.isEmpty()) {
                            expiredCalendar.setTimeInMillis(new BigDecimal(appointmentDate).longValue());
                        } else {
                            expiredCalendar.setTimeInMillis(new BigDecimal(startDate).longValue());
                        }
                        expiredCalendar.add(Calendar.DAY_OF_MONTH, 7);

                        checkIfExpired(expiredCalendar, task);

                        break;
                }
            }
        }
    }

    public boolean checkIfExpired(Calendar expiredCalendar, Map<String, String> taskEvent) {
        if (Calendar.getInstance().getTime().after(expiredCalendar.getTime())) {
            saveExpiredReferralEvent(
                    taskEvent.get(ChwDBConstants.TaskTable.FOR),
                    taskEvent.get(ChwDBConstants.TaskTable.LOCATION),
                    taskEvent.get(ID),
                    taskEvent.get(ChwDBConstants.TaskTable.STATUS),
                    taskEvent.get(ChwDBConstants.TaskTable.BUSINESS_STATUS)
            );
            expireTask(taskEvent.get(ID), taskEvent.get(ChwDBConstants.TaskTable.FOR));
            return true;
        }
        return false;
    }

    public void checkIfNotYetDone(Calendar referralNotYetDoneCalendar, Map<String, String> taskEvent) {
        if (Calendar.getInstance().getTime().after(referralNotYetDoneCalendar.getTime())) {
            saveNotYetDoneReferralEvent(
                    taskEvent.get(ChwDBConstants.TaskTable.FOR),
                    taskEvent.get(ChwDBConstants.TaskTable.LOCATION),
                    taskEvent.get(ID),
                    taskEvent.get(ChwDBConstants.TaskTable.STATUS)
            );
            referralNotYetDoneTask(taskEvent.get(ID), taskEvent.get(ChwDBConstants.TaskTable.FOR));
        }
    }

    private void saveExpiredReferralEvent(String baseEntityId, String userLocationId, String referralTaskId, String taskStatus, String businessStatus) {
        try {

            Event baseEvent = generateEvent(baseEntityId, userLocationId, referralTaskId, taskStatus);
            baseEvent.setEventType(CoreConstants.EventType.EXPIRED_REFERRAL);
            baseEvent.setEntityType((CoreConstants.TABLE_NAME.CLOSE_REFERRAL));

            baseEvent.addObs((new Obs())
                    .withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_BUSINESS_STATUS)
                    .withValue(businessStatus)
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_BUSINESS_STATUS)
                    .withFieldType(CoreConstants.FORMSUBMISSION_FIELD).withFieldDataType(CoreConstants.TEXT).withParentCode("")
                    .withHumanReadableValues(new ArrayList<>()));

            CoreJsonFormUtils.tagSyncMetadata(sharedPreferences, baseEvent);

            baseEvent.setLocationId(userLocationId); //setting the location uuid of the referral initiator so that to allow the event to sync back to the chw app since it sync data by location.
            syncEvents(baseEvent, referralTaskId);
        } catch (Exception e) {
            Timber.e(e, "CloseExpiredReferralsIntentService --> saveExpiredReferralEvent");
        }
    }

    private void saveNotYetDoneReferralEvent(String baseEntityId, String userLocationId, String referralTaskId, String taskStatus) {
        try {
            Event baseEvent = generateEvent(baseEntityId, userLocationId, referralTaskId, taskStatus);
            baseEvent.setEventType((CoreConstants.EventType.NOT_YET_DONE_REFERRAL));
            baseEvent.setEntityType((CoreConstants.TABLE_NAME.NOT_YET_DONE_REFERRAL));

            CoreJsonFormUtils.tagSyncMetadata(sharedPreferences, baseEvent);
            baseEvent.setLocationId(userLocationId);  //setting the location uuid of the referral initiator so that to allow the event to sync back to the chw app since it sync data by location.

            syncEvents(baseEvent, referralTaskId);
        } catch (Exception e) {
            Timber.e(e, "CloseExpiredReferralsIntentService --> saveExpiredReferralEvent");
        }
    }

    private void syncEvents(Event baseEvent, String referralTaskId) {
        try {
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
            syncHelper.addEvent(referralTaskId, eventJson);
            long lastSyncTimeStamp = sharedPreferences.fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            List<String> formSubmissionIds = new ArrayList<>();
            formSubmissionIds.add(baseEvent.getFormSubmissionId());
            CoreChwApplication.getInstance().getClientProcessorForJava().processClient(syncHelper.getEvents(formSubmissionIds));
            sharedPreferences.saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e, "CloseExpiredReferralsIntentService --> syncEvents");
        }
    }

    private Event generateEvent(String baseEntityId, String userLocationId, String referralTaskId, String taskStatus) {
        Event baseEvent = null;
        try {
            AllSharedPreferences sharedPreferences = org.smartregister.family.util.Utils.getAllSharedPreferences();
            baseEvent = (Event) new Event()
                    .withBaseEntityId(baseEntityId)
                    .withEventDate(new Date())
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withProviderId(sharedPreferences.fetchRegisteredANM())
                    .withLocationId(userLocationId)
                    .withTeamId(sharedPreferences.fetchDefaultTeamId(sharedPreferences.fetchRegisteredANM()))
                    .withTeam(sharedPreferences.fetchDefaultTeam(sharedPreferences.fetchRegisteredANM()))
                    .withDateCreated(new Date());

            baseEvent.addObs((new Obs())
                    .withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK)
                    .withValue(referralTaskId)
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK)
                    .withFieldType(CoreConstants.FORMSUBMISSION_FIELD).withFieldDataType(CoreConstants.TEXT)
                    .withParentCode("").withHumanReadableValues(new ArrayList<>()));

            baseEvent.addObs((new Obs())
                    .withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_STATUS)
                    .withValue(taskStatus)
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_PREVIOUS_STATUS)
                    .withFieldType(CoreConstants.FORMSUBMISSION_FIELD).withFieldDataType(CoreConstants.TEXT)
                    .withParentCode("")
                    .withHumanReadableValues(new ArrayList<>()));

            CoreJsonFormUtils.tagSyncMetadata(sharedPreferences, baseEvent);
            //setting the location uuid of the referral initiator so that to allow the event to sync back to the chw app since it sync data by location.
            baseEvent.setLocationId(userLocationId);
        } catch (Exception e) {
            Timber.e(e, "CloseExpiredReferralsIntentService --> saveExpiredReferralEvent");
        }
        return baseEvent;
    }

    private void expireTask(String taskId, String baseEntityId) {
        Task updatedTask = updateCurrentTask(taskId, baseEntityId);
        updatedTask.setStatus(Task.TaskStatus.FAILED);
        updatedTask.setBusinessStatus(CoreConstants.BUSINESS_STATUS.EXPIRED);
        CoreChwApplication.getInstance().getTaskRepository().addOrUpdate(updatedTask);
    }

    private void referralNotYetDoneTask(String taskId, String baseEntityId) {
        Task updatedTask = updateCurrentTask(taskId, baseEntityId);
        updatedTask.setStatus(Task.TaskStatus.CANCELLED);
        CoreChwApplication.getInstance().getTaskRepository().addOrUpdate(updatedTask);
    }

    private Task updateCurrentTask(String taskId, String baseEntityId) {
        Task currentTask = taskRepository.getTaskByIdentifier(taskId);
        DateTime now = new DateTime();
        Period period = new Period();
        period.setEnd(now);
        currentTask.setExecutionPeriod(period);
        currentTask.setLastModified(now);
        currentTask.setForEntity(baseEntityId);
        currentTask.setSyncStatus(BaseRepository.TYPE_Unsynced);
        currentTask.setLastModified(now);
        return currentTask;
    }
}

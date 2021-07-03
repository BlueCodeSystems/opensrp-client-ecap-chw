package com.bluecodeltd.ecap.chw.schedulers;

import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.contract.ScheduleService;
import org.smartregister.chw.core.schedulers.ScheduleTaskExecutor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.malaria.util.Constants;
import com.bluecodeltd.ecap.chw.task.ANCVisitScheduler;
import com.bluecodeltd.ecap.chw.task.ChildHomeVisitScheduler;
import com.bluecodeltd.ecap.chw.task.FamilyKitCheckScheduler;
import com.bluecodeltd.ecap.chw.task.FpVisitScheduler;
import com.bluecodeltd.ecap.chw.task.MalariaScheduler;
import com.bluecodeltd.ecap.chw.task.PNCVisitScheduler;
import com.bluecodeltd.ecap.chw.task.RoutineHouseHoldVisitScheduler;
import com.bluecodeltd.ecap.chw.task.WashCheckScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bluecodeltd.ecap.chw.util.Constants.EncounterType.PNC_CHILD_REGISTRATION;

public class ChwScheduleTaskExecutor extends ScheduleTaskExecutor {

    private static ChwScheduleTaskExecutor scheduleTaskExecutor;

    public static ChwScheduleTaskExecutor getInstance() {
        if (scheduleTaskExecutor == null) {
            scheduleTaskExecutor = new ChwScheduleTaskExecutor();
        }
        return scheduleTaskExecutor;
    }

    protected ChwScheduleTaskExecutor() {
        //scheduleServiceMap.put();
    }

    @Override
    protected Map<String, List<ScheduleService>> getClassifier() {
        if (scheduleServiceMap == null || scheduleServiceMap.size() == 0) {
            scheduleServiceMap = new HashMap<>();

            initializeChildClassifier(scheduleServiceMap);

            if (ChwApplication.getApplicationFlavor().hasANC())
                initializeANCClassifier(scheduleServiceMap);

            if (ChwApplication.getApplicationFlavor().hasPNC())
                initializePNCClassifier(scheduleServiceMap);

            if (ChwApplication.getApplicationFlavor().hasMalaria())
                initializeMalariaClassifier(scheduleServiceMap);

            if (ChwApplication.getApplicationFlavor().hasWashCheck())
                initializeWashClassifier(scheduleServiceMap);

            if (ChwApplication.getApplicationFlavor().hasFamilyKitCheck())
                initializeFamilyKitClassifier(scheduleServiceMap);

            if (ChwApplication.getApplicationFlavor().hasFamilyPlanning())
                initializeFPClassifier(scheduleServiceMap);

            if (ChwApplication.getApplicationFlavor().hasRoutineVisit())
                initializeRoutineHouseholdClassifier(scheduleServiceMap);

        }
        return scheduleServiceMap;
    }

    private void addToClassifers(String eventType, Map<String, List<ScheduleService>> classifier, List<ScheduleService> scheduleServices) {
        List<ScheduleService> services = classifier.get(eventType);
        if (services == null)
            services = new ArrayList<>();

        services.addAll(scheduleServices);
        classifier.put(eventType, services);
    }

    private void initializeChildClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new ChildHomeVisitScheduler());

        addToClassifers(CoreConstants.EventType.CHILD_HOME_VISIT, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.CHILD_VISIT_NOT_DONE, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.CHILD_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.UPDATE_CHILD_REGISTRATION, classifier, scheduleServices);
    }

    private void initializeANCClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new ANCVisitScheduler());

        addToClassifers(CoreConstants.EventType.ANC_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.ANC_HOME_VISIT, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.ANC_HOME_VISIT_NOT_DONE, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.ANC_HOME_VISIT_NOT_DONE_UNDO, classifier, scheduleServices);
    }

    private void initializePNCClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new PNCVisitScheduler());

        addToClassifers(CoreConstants.EventType.PREGNANCY_OUTCOME, classifier, scheduleServices);
        addToClassifers(PNC_CHILD_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.PNC_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.PNC_HOME_VISIT, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.PNC_HOME_VISIT_NOT_DONE, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.PNC_HOME_VISIT_NOT_DONE_UNDO, classifier, scheduleServices);
    }

    private void initializeMalariaClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new MalariaScheduler());
        addToClassifers(Constants.EVENT_TYPE.MALARIA_FOLLOW_UP_VISIT, classifier, scheduleServices);
        addToClassifers(Constants.EVENT_TYPE.MALARIA_CONFIRMATION, classifier, scheduleServices);
    }

    private void initializeWashClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new WashCheckScheduler());

        addToClassifers(CoreConstants.EventType.FAMILY_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.UPDATE_FAMILY_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.WASH_CHECK, classifier, scheduleServices);
    }

    private void initializeFamilyKitClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new FamilyKitCheckScheduler());

        addToClassifers(CoreConstants.EventType.FAMILY_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.PREGNANCY_OUTCOME, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.UPDATE_FAMILY_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.UPDATE_CHILD_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.FAMILY_KIT, classifier, scheduleServices);
    }

    private void initializeFPClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new FpVisitScheduler());
        addToClassifers(FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT, classifier, scheduleServices);
        addToClassifers(FamilyPlanningConstants.EventType.FAMILY_PLANNING_REGISTRATION, classifier, scheduleServices);
        addToClassifers(FamilyPlanningConstants.EventType.FAMILY_PLANNING_CHANGE_METHOD, classifier, scheduleServices);
    }

    private void initializeRoutineHouseholdClassifier(Map<String, List<ScheduleService>> classifier) {
        List<ScheduleService> scheduleServices = new ArrayList<>();
        scheduleServices.add(new RoutineHouseHoldVisitScheduler());

        addToClassifers(CoreConstants.EventType.FAMILY_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.UPDATE_FAMILY_REGISTRATION, classifier, scheduleServices);
        addToClassifers(CoreConstants.EventType.ROUTINE_HOUSEHOLD_VISIT, classifier, scheduleServices);
    }
}

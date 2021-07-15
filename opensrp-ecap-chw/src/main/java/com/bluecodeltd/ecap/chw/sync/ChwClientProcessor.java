package com.bluecodeltd.ecap.chw.sync;


import android.content.Context;

import org.smartregister.CoreLibrary;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.domain.Child;
import org.smartregister.chw.core.sync.CoreClientProcessor;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.dao.ChwChildDao;
import com.bluecodeltd.ecap.chw.dao.FamilyDao;
import com.bluecodeltd.ecap.chw.schedulers.ChwScheduleTaskExecutor;
import com.bluecodeltd.ecap.chw.service.ChildAlertService;
import com.bluecodeltd.ecap.chw.util.Constants;

import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.sync.ClientProcessorForJava;

public class ChwClientProcessor extends CoreClientProcessor {

    private ChwClientProcessor(Context context) {
        super(context);
    }

    public static ClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new ChwClientProcessor(context);
        }
        return instance;
    }

    @Override
    public void processEvents(ClientClassification clientClassification, Table vaccineTable, Table serviceTable, EventClient eventClient, Event event, String eventType) throws Exception {
        if (eventClient != null && eventClient.getEvent() != null) {
            String baseEntityID = eventClient.getEvent().getBaseEntityId();

            switch (eventType) {
                case Constants.CHILD_INDEX:
                    processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                    break;
                case CoreConstants.EventType.REMOVE_FAMILY:
                    ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByFamilyEntityID(baseEntityID);
                case CoreConstants.EventType.REMOVE_MEMBER:
                    ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByEntityID(baseEntityID);
                case CoreConstants.EventType.REMOVE_CHILD:
                    if (!CoreLibrary.getInstance().isPeerToPeerProcessing() && !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
                        Child child = ChwChildDao.getChild(baseEntityID);
                        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByEntityID(baseEntityID);
                        if (child != null) {
                            String familyBaseEntityID = child.getFamilyBaseEntityID();
                            ChwApplication.getInstance().getScheduleRepository().closeChildMember(baseEntityID);
                            if (!FamilyDao.familyHasChildUnderFive(familyBaseEntityID) && ChwApplication.getApplicationFlavor().hasFamilyKitCheck()) {
                                ChwApplication.getInstance().getScheduleRepository().deleteFamilyKitSchedule(familyBaseEntityID);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        super.processEvents(clientClassification, vaccineTable, serviceTable, eventClient, event, eventType);
        if (eventClient != null && eventClient.getEvent() != null) {
            String baseEntityID = eventClient.getEvent().getBaseEntityId();
            switch (eventType) {
                case Constants.CHILD_INDEX:
                    processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                    break;
                case CoreConstants.EventType.CHILD_HOME_VISIT:
                case CoreConstants.EventType.CHILD_VISIT_NOT_DONE:
                case CoreConstants.EventType.CHILD_REGISTRATION:
                case CoreConstants.EventType.PREGNANCY_OUTCOME:
                case CoreConstants.EventType.UPDATE_CHILD_REGISTRATION:
                    if (!CoreLibrary.getInstance().isPeerToPeerProcessing() && !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
                        ChildAlertService.updateAlerts(baseEntityID); // 495 seconds , needs optimization
                    }
                default:
                    break;
            }
        }
        if (!CoreLibrary.getInstance().isPeerToPeerProcessing() && !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            ChwScheduleTaskExecutor.getInstance().execute(event.getBaseEntityId(), event.getEventType(), event.getEventDate().toDate());
        }
    }
}

package com.bluecodeltd.ecap.chw.sync;

import android.content.Context;

import com.bluecodeltd.ecap.chw.util.Constants;

import org.smartregister.chw.core.sync.CoreClientProcessor;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.sync.ClientProcessorForJava;

import timber.log.Timber;

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
        try {
            if (eventClient != null && eventClient.getEvent() != null) {
                switch (eventType) {
                    case Constants.EcapEncounterType.CHILD_INDEX:
                    case Constants.EcapEncounterType.MOTHER_INDEX:
                        processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                        break;
                    default:
                        super.processEvents(clientClassification, vaccineTable, serviceTable, eventClient, event, eventType);
                        break;
                }
            }
        } catch (Exception exception) {
            Timber.e(exception);
        }
    }
}

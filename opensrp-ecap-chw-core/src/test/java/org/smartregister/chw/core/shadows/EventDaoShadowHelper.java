package org.smartregister.chw.core.shadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.chw.core.dao.EventDao;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;

import java.util.List;

@Implements(EventDao.class)
public class EventDaoShadowHelper {

    @Implementation
    public static Event getLatestEvent(String baseEntityID, List<String> eventTypes) {
        Event latestEvent = new Event();
        latestEvent.addObs(new Obs());
        return latestEvent;
    }
}

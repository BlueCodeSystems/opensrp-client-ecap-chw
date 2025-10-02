package com.bluecodeltd.ecap.chw.util;

import org.smartregister.clientandeventmodel.Event;

public final class EventConversionUtils {

    private EventConversionUtils() {
    }

    public static org.smartregister.domain.Event toDomainEvent(Event clientEvent) {
        if (clientEvent == null) {
            return null;
        }

        org.smartregister.domain.Event event = new org.smartregister.domain.Event();
        event.setBaseEntityId(clientEvent.getBaseEntityId());
        event.setEventType(clientEvent.getEventType());
        event.setEntityType(clientEvent.getEntityType());
        event.setProviderId(clientEvent.getProviderId());
        event.setLocationId(clientEvent.getLocationId());
        event.setChildLocationId(clientEvent.getChildLocationId());
        event.setTeam(clientEvent.getTeam());
        event.setTeamId(clientEvent.getTeamId());
        event.setFormSubmissionId(clientEvent.getFormSubmissionId());
        event.setClientApplicationVersion(clientEvent.getClientApplicationVersion());
        event.setClientDatabaseVersion(clientEvent.getClientDatabaseVersion());
        event.setDetails(clientEvent.getDetails());
        return event;
    }
}

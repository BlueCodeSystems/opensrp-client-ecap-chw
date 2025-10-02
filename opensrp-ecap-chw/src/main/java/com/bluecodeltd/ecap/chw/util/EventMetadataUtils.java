package com.bluecodeltd.ecap.chw.util;

import com.bluecodeltd.ecap.chw.application.ChwApplication;

import org.smartregister.clientandeventmodel.Event;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.repository.AllSharedPreferences;

/**
 * Utility methods for tagging OpenSRP metadata on client-event-model events without depending on
 * the upstream OPD utility (which expects the domain event type).
 */
public final class EventMetadataUtils {

    private EventMetadataUtils() {
        // no-op
    }

    public static void tagSyncMetadata(Event event) {
        if (event == null) {
            return;
        }

        AllSharedPreferences sharedPreferences = ChwApplication.getInstance()
                .getContext()
                .allSharedPreferences();

        String providerId = sharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(sharedPreferences.fetchUserLocalityId(providerId));
        event.setChildLocationId(sharedPreferences.fetchCurrentLocality());
        event.setTeam(sharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(sharedPreferences.fetchDefaultTeamId(providerId));

        if (FamilyLibrary.getInstance() != null) {
            event.setClientApplicationVersion(FamilyLibrary.getInstance().getApplicationVersion());
            event.setClientDatabaseVersion(FamilyLibrary.getInstance().getDatabaseVersion());
        }
    }
}

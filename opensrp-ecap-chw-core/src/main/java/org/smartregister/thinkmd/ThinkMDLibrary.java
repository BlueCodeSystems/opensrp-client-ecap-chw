package org.smartregister.thinkmd;

import android.content.Context;

import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.thinkmd.model.FHIRBundleModel;

import java.util.ArrayList;
import java.util.UUID;

import timber.log.Timber;

/**
 * Lightweight stub for the ThinkMD library. The original artifact is hosted in
 * a private repository that currently requires credentials. To allow the app to
 * compile and run without that dependency, we provide a no-op implementation of
 * the subset of methods referenced in the codebase. Runtime behaviour is
 * reduced to logging and returning empty payloads, which keeps the rest of the
 * workflow operational without ThinkMD features.
 */
public class ThinkMDLibrary {

    private static final ThinkMDLibrary INSTANCE = new ThinkMDLibrary();

    private Context applicationContext;
    private ThinkMDConfig config;

    private ThinkMDLibrary() {
        // singleton
    }

    public static ThinkMDLibrary getInstance() {
        return INSTANCE;
    }

    public static void init(Context context, ThinkMDConfig config) {
        INSTANCE.applicationContext = context != null ? context.getApplicationContext() : null;
        INSTANCE.config = config;
        Timber.w("ThinkMD library stub initialised - ThinkMD features disabled");
    }

    public ThinkMDConfig getConfig() {
        return config;
    }

    /**
     * The upstream implementation extracts a patient identifier from the
     * encoded FHIR bundle. When the ThinkMD backend is unavailable we return
     * {@code null} so callers can skip any further processing.
     */
    public String getThinkMDPatientId(String encodedBundle) {
        Timber.w("ThinkMD library stub in use - unable to extract patient id from bundle");
        return null;
    }

    /**
     * Generates a minimal event structure so the rest of the sync pipeline can
     * continue to operate. The event contains only metadata sourced from the
     * provided {@link FormTag}; no observations are generated because the
     * upstream bundle parser is unavailable.
     */
    public Event createCarePlanEvent(String encodedBundle, FormTag formTag, String baseEntityId) {
        Event event = new Event()
                .withBaseEntityId(baseEntityId)
                .withEventType("thinkmd_care_plan")
                .withEntityType("thinkmd_care_plan")
                .withFormSubmissionId(UUID.randomUUID().toString());

        if (formTag != null) {
            event.withProviderId(formTag.providerId)
                    .withLocationId(formTag.locationId)
                    .withTeam(formTag.team)
                    .withTeamId(formTag.teamId);
        }

        if (event.getObs() == null) {
            event.setObs(new ArrayList<Obs>());
        }

        Timber.w("ThinkMD library stub in use - generated placeholder care plan event");
        return event;
    }

    public void processHealthAssessment(Context context, FHIRBundleModel bundle) {
        Timber.w("ThinkMD library stub in use - skipping health assessment launch for %s",
                bundle != null ? bundle.getUniqueIdGeneratedForThinkMD() : "unknown_bundle");
    }
}

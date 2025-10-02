package org.smartregister.thinkmd.utils;

/**
 * Minimal ThinkMD constants stub to allow the application to build without the
 * upstream ThinkMD dependency. Downstream code only consumes the bundle key
 * when storing cached care plan payloads, so we retain the same identifier
 * value used across the project.
 */
public final class Constants {

    private Constants() {
        // no-op
    }

    public static final String THINKMD_FHIR_BUNDLE = "thinkmd_fhir_bundle";
}

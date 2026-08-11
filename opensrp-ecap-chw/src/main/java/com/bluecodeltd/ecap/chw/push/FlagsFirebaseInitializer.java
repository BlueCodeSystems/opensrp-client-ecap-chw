package com.bluecodeltd.ecap.chw.push;

import android.content.Context;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import timber.log.Timber;

/**
 * Initializes the default {@link FirebaseApp} from build-config values so FCM can register this
 * device and receive push notifications from the PMP API.
 *
 * <p>This project does not apply the {@code google-services} Gradle plugin nor ship a
 * {@code google-services.json}; instead the Firebase Android client config is supplied via
 * {@code BuildConfig} (env vars / local.properties), consistent with how Directus/Mapbox/OAuth
 * config is handled. The four required values come from the Firebase console
 * (Project settings &rarr; your Android app) or the equivalent fields of a {@code google-services.json}:
 * <ul>
 *     <li>{@code FIREBASE_APP_ID}    &larr; {@code mobilesdk_app_id} (e.g. {@code 1:123:android:abc})</li>
 *     <li>{@code FIREBASE_API_KEY}   &larr; {@code api_key.current_key}</li>
 *     <li>{@code FIREBASE_PROJECT_ID}&larr; {@code project_info.project_id}</li>
 *     <li>{@code FIREBASE_SENDER_ID} &larr; {@code project_info.project_number}</li>
 * </ul>
 *
 * <p>If any required value is missing the initializer no-ops (no crash); FCM stays dormant and the
 * app continues to rely on {@link FlagsPollWorker} polling for flag notifications.</p>
 */
public final class FlagsFirebaseInitializer {

    private FlagsFirebaseInitializer() {}

    public static void init(Context context) {
        try {
            if (!FirebaseApp.getApps(context).isEmpty()) {
                return; // already initialized
            }

            String appId = BuildConfig.FIREBASE_APP_ID;
            String apiKey = BuildConfig.FIREBASE_API_KEY;
            String projectId = BuildConfig.FIREBASE_PROJECT_ID;
            String senderId = BuildConfig.FIREBASE_SENDER_ID;

            if (isBlank(appId) || isBlank(apiKey) || isBlank(senderId)) {
                Timber.w("FlagsFirebaseInitializer: Firebase config not set; FCM disabled, using polling");
                return;
            }

            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setApplicationId(appId.trim())
                    .setApiKey(apiKey.trim())
                    .setGcmSenderId(senderId.trim());
            if (!isBlank(projectId)) {
                builder.setProjectId(projectId.trim());
            }

            FirebaseApp.initializeApp(context, builder.build());
            Timber.i("FlagsFirebaseInitializer: Firebase initialized for project %s", projectId);
        } catch (Throwable t) {
            Timber.e(t, "FlagsFirebaseInitializer: failed to initialize Firebase");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

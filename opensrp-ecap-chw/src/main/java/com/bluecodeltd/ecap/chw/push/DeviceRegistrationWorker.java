package com.bluecodeltd.ecap.chw.push;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.api.ItemApi;
import com.bluecodeltd.ecap.chw.configs.Config;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Registers this device's FCM token with the PMP API so the backend
 * (flag-fcm-notify hook) can push flag notifications to it.
 *
 * <p>POSTs to {@link Config#MOBILE_DEVICES_URL} with a Directus bearer token:
 * {@code {provider_id, fcm_token, platform, app}}.</p>
 *
 * <p>The registration is de-duplicated: it only fires when the (provider_id, fcm_token)
 * pair differs from the last successful registration, so re-scheduling on every app
 * launch is cheap.</p>
 */
public class DeviceRegistrationWorker extends Worker {
    private static final String PREFS = "flags_device_registration";
    private static final String KEY_LAST_REGISTERED = "last_registered_signature";
    static final String TOKEN_PREF_KEY = "flags_fcm_token";

    /**
     * Bump this whenever the backend changes what {@code /register} captures (e.g. new fields it
     * resolves server-side like {@code facility}). It is part of the de-dup signature, so a new
     * value invalidates every install's stored signature and forces exactly one re-registration on
     * the next app foreground — the way to backfill existing devices without waiting for a token
     * refresh. v2: backend switched flag routing to resolve {@code facility} at register time.
     */
    private static final String REGISTRATION_VERSION = "2";

    public DeviceRegistrationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (BuildConfig.DIRECTUS_EMAIL.isEmpty() || BuildConfig.DIRECTUS_PASSWORD.isEmpty()) {
            Timber.w("DeviceRegistrationWorker: Directus credentials not configured; skipping");
            return Result.success();
        }

        String providerId = currentProviderId();
        if (providerId.isEmpty()) {
            Timber.w("DeviceRegistrationWorker: no provider_id yet; will retry after login");
            return Result.retry();
        }

        String fcmToken = resolveFcmToken();
        if (fcmToken.isEmpty()) {
            Timber.w("DeviceRegistrationWorker: no FCM token available (is Firebase initialized?); retrying");
            return Result.retry();
        }

        String signature = REGISTRATION_VERSION + "|" + providerId + "|" + fcmToken;
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (signature.equals(prefs.getString(KEY_LAST_REGISTERED, ""))) {
            Timber.d("DeviceRegistrationWorker: device already registered for this provider/token");
            return Result.success();
        }

        try {
            ItemApi api = api();

            JsonObject loginBody = new JsonObject();
            loginBody.addProperty("email", BuildConfig.DIRECTUS_EMAIL);
            loginBody.addProperty("password", BuildConfig.DIRECTUS_PASSWORD);
            loginBody.addProperty("mode", "json");

            Response<JsonObject> loginResponse = api.login(loginBody).execute();
            if (!loginResponse.isSuccessful() || loginResponse.body() == null) {
                Timber.e("DeviceRegistrationWorker: login failed %s", loginResponse.code());
                return Result.retry();
            }
            JsonObject data = loginResponse.body().getAsJsonObject("data");
            String token = data != null && data.has("access_token") ? data.get("access_token").getAsString() : "";
            if (token.isEmpty()) {
                Timber.e("DeviceRegistrationWorker: no access token");
                return Result.retry();
            }

            JsonObject body = new JsonObject();
            body.addProperty("provider_id", providerId);
            body.addProperty("fcm_token", fcmToken);
            body.addProperty("platform", "android");
            body.addProperty("app", BuildConfig.APPLICATION_ID);

            Response<JsonObject> response = api.registerDevice(
                    Config.MOBILE_DEVICES_URL, "Bearer " + token, body).execute();
            if (!response.isSuccessful()) {
                Timber.e("DeviceRegistrationWorker: register failed %s", response.code());
                return Result.retry();
            }

            prefs.edit().putString(KEY_LAST_REGISTERED, signature).apply();
            Timber.i("DeviceRegistrationWorker: device registered for provider %s", providerId);
            return Result.success();
        } catch (Exception e) {
            Timber.e(e, "DeviceRegistrationWorker: registration failed");
            return Result.retry();
        }
    }

    /**
     * Prefer the token cached by {@link FlagsFirebaseMessagingService#onNewToken}; fall back to
     * requesting it from Firebase directly. Returns "" if Firebase is not initialized or the
     * request fails.
     */
    private String resolveFcmToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String cached = prefs.getString(TOKEN_PREF_KEY, "");
        if (cached != null && !cached.trim().isEmpty()) {
            return cached.trim();
        }
        try {
            String token = com.google.android.gms.tasks.Tasks.await(
                    FirebaseMessaging.getInstance().getToken(), 20, TimeUnit.SECONDS);
            if (token != null && !token.trim().isEmpty()) {
                prefs.edit().putString(TOKEN_PREF_KEY, token.trim()).apply();
                return token.trim();
            }
        } catch (Exception e) {
            Timber.w(e, "DeviceRegistrationWorker: could not obtain FCM token");
        }
        return "";
    }

    private String currentProviderId() {
        try {
            String registeredAnm = org.smartregister.Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            if (isMeaningful(registeredAnm)) {
                return registeredAnm.trim();
            }
        } catch (Exception ignored) {
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] keys = new String[]{"provider_id", "providerId", "provider", "sub"};
        for (String key : keys) {
            String value = prefs.getString(key, "");
            if (isMeaningful(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private static boolean isMeaningful(String value) {
        return value != null && !value.trim().isEmpty()
                && !"anonymous".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim());
    }

    private static ItemApi api() {
        return new Retrofit.Builder()
                .baseUrl(Config.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build()
                .create(ItemApi.class);
    }
}

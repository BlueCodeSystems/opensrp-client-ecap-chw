package com.bluecodeltd.ecap.chw.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.FlagActivity;
import com.bluecodeltd.ecap.chw.api.ItemApi;
import com.bluecodeltd.ecap.chw.configs.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Periodically polls Directus for new ACTIVE flags and raises a local notification
 * for any flag id not seen before. Runs via WorkManager (see {@link FlagsNotificationScheduler}).
 */
public class FlagsPollWorker extends Worker {
    private static final String CHANNEL_ID = "flags_channel";
    private static final String PREFS = "flags_poll_prefs";
    private static final String KEY_SEEN_IDS = "seen_flag_ids";
    private static final String KEY_INITIALIZED = "seen_initialized";
    private static final String KEY_SCOPE = "seen_scope";
    private static final String[] FIELDS = new String[]{
            "id", "status", "household_id", "vca_id", "caregiver_name", "facility", "district", "caseworker_name", "caseworker_phone", "provider_id"
    };

    public FlagsPollWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (BuildConfig.DIRECTUS_EMAIL.isEmpty() || BuildConfig.DIRECTUS_PASSWORD.isEmpty()) {
            Timber.w("FlagsPollWorker: Directus credentials not configured; skipping");
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
                Timber.e("FlagsPollWorker: login failed %s", loginResponse.code());
                return Result.retry();
            }
            JsonObject data = loginResponse.body().getAsJsonObject("data");
            String token = data != null && data.has("access_token") ? data.get("access_token").getAsString() : "";
            if (token.isEmpty()) {
                Timber.e("FlagsPollWorker: no access token");
                return Result.retry();
            }

            Response<JsonObject> itemsResponse = fetchItems(api, token, buildProviderScopedQuery());
            if (!itemsResponse.isSuccessful() || itemsResponse.body() == null) {
                Timber.w("FlagsPollWorker: provider scoped fetch failed %s; retrying with caseworker_phone", itemsResponse.code());
                itemsResponse = fetchItems(api, token, buildPhoneScopedQuery());
            }
            if (!itemsResponse.isSuccessful() || itemsResponse.body() == null) {
                Timber.e("FlagsPollWorker: fetch failed %s", itemsResponse.code());
                return Result.retry();
            }

            List<JsonObject> flags = new ArrayList<>();
            JsonArray arr = itemsResponse.body().getAsJsonArray("data");
            if (arr != null) {
                for (JsonElement element : arr) {
                    if (element != null && element.isJsonObject()) {
                        JsonObject flag = element.getAsJsonObject();
                        if (matchesCurrentProvider(flag)) {
                            flags.add(flag);
                        }
                    }
                }
            }

            notifyNewFlags(flags);
            return Result.success();
        } catch (Exception e) {
            Timber.e(e, "FlagsPollWorker: poll failed");
            return Result.retry();
        }
    }

    private List<JsonObject> extractScopedFlags(JsonObject responseBody) {
        List<JsonObject> flags = new ArrayList<>();
        JsonArray arr = responseBody.getAsJsonArray("data");
        if (arr == null) {
            return flags;
        }
        for (JsonElement element : arr) {
            if (element != null && element.isJsonObject()) {
                JsonObject flag = element.getAsJsonObject();
                if (matchesCurrentProvider(flag)) {
                    flags.add(flag);
                }
            }
        }
        return flags;
    }
    private Response<JsonObject> fetchItems(ItemApi api, String token, Map<String, String> query) throws Exception {
        return api.getItems(
                Config.BASEURL + "items/" + BuildConfig.DIRECTUS_FLAGS_COLLECTION,
                "Bearer " + token,
                query).execute();
    }

    private Map<String, String> buildProviderScopedQuery() {
        // Flags are routed by facility (and district) on the PMP backend; scope by the logged-in
        // facility OR district first and fall back to legacy provider/phone scoping only when
        // both are unknown.
        String facility = currentFacility();
        String district = currentDistrict();
        if (!facility.isEmpty() || !district.isEmpty()) {
            Map<String, String> query = baseFlagsQuery();
            if (!facility.isEmpty() && !district.isEmpty()) {
                query.put("filter[_or][0][facility][_eq]", facility);
                query.put("filter[_or][1][district][_eq]", district);
            } else if (!facility.isEmpty()) {
                query.put("filter[facility][_eq]", facility);
            } else {
                query.put("filter[district][_eq]", district);
            }
            return query;
        }
        String field = providerFilterField();
        if ("caseworker_phone".equals(field)) {
            return buildPhoneScopedQuery();
        }
        Map<String, String> query = baseFlagsQuery();
        String value = providerFilterValue(field);
        if (!value.isEmpty()) {
            query.put("filter[" + field + "][_eq]", value);
        }
        return query;
    }

    private Map<String, String> buildPhoneScopedQuery() {
        // PMP phone values are free text (+260..., 260..., 09..., spaces). Do not use
        // Directus exact matching here; fetch lightweight fields and normalize locally.
        return baseFlagsQuery(false);
    }

    private Map<String, String> baseFlagsQuery() {
        return baseFlagsQuery(true);
    }

    private Map<String, String> baseFlagsQuery(boolean includeProviderId) {
        Map<String, String> query = new HashMap<>();
        query.put("fields", fields(includeProviderId, FIELDS));
        query.put("sort", "-date_created");
        query.put("limit", "-1");
        return query;
    }


    private static String fields(boolean includeProviderId, String[] fields) {
        if (includeProviderId) {
            return String.join(",", fields);
        }
        List<String> selected = new ArrayList<>();
        for (String field : fields) {
            if (!"provider_id".equals(field)) {
                selected.add(field);
            }
        }
        return String.join(",", selected);
    }
    private boolean matchesCurrentProvider(JsonObject flag) {
        String facility = currentFacility();
        if (!facility.isEmpty() && facility.equalsIgnoreCase(value(flag, "facility"))) {
            return true;
        }
        String district = currentDistrict();
        if (!district.isEmpty() && district.equalsIgnoreCase(value(flag, "district"))) {
            return true;
        }
        String providerId = currentProviderId();
        if (!providerId.isEmpty() && providerId.equalsIgnoreCase(value(flag, "provider_id"))) {
            return true;
        }
        String currentPhone = normalizedPhone(currentUserPhone());
        String flagPhone = normalizedPhone(value(flag, "caseworker_phone"));
        if (!currentPhone.isEmpty() && currentPhone.equals(flagPhone)) {
            return true;
        }
        String field = providerFilterField();
        if (!field.equals("provider_id") && !field.equals("caseworker_phone")) {
            String expected = providerFilterValue(field);
            return !expected.isEmpty() && expected.equalsIgnoreCase(value(flag, field));
        }
        return false;
    }

    private String providerFilterField() {
        String field = BuildConfig.DIRECTUS_FLAGS_CASEWORKER_FIELD;
        return field == null || field.trim().isEmpty() ? "caseworker_phone" : field.trim();
    }

    private String providerFilterValue(String field) {
        if ("caseworker_phone".equals(field)) {
            return normalizedPhone(currentUserPhone());
        }
        if ("caseworker_name".equals(field)) {
            return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("caseworker_name", "").trim();
        }
        return currentProviderId();
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

    private String currentUserPhone() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("phone", "");
    }

    /** The facility the current user is logged into (stored at login, see DashboardActivity). */
    private String currentFacility() {
        String facility = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("facility", "");
        if (facility == null) {
            return "";
        }
        facility = facility.trim();
        if ("anonymous".equalsIgnoreCase(facility) || "null".equalsIgnoreCase(facility)) {
            return "";
        }
        return facility;
    }

    /** The district the current user is logged into (stored at login, see DashboardActivity). */
    private String currentDistrict() {
        String district = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("district", "");
        if (district == null) {
            return "";
        }
        district = district.trim();
        if ("anonymous".equalsIgnoreCase(district) || "null".equalsIgnoreCase(district)) {
            return "";
        }
        return district;
    }

    private static boolean isMeaningful(String value) {
        return value != null && !value.trim().isEmpty()
                && !"anonymous".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim());
    }

    private static String normalizedPhone(String phone) {
        if (phone == null) {
            return "";
        }
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("260") && digits.length() == 12) {
            return "0" + digits.substring(3);
        }
        return digits;
    }
    private void notifyNewFlags(List<JsonObject> flags) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String scope = scopeKey();
        String previousScope = prefs.getString(KEY_SCOPE, "");
        Set<String> seen = new HashSet<>(prefs.getStringSet(KEY_SEEN_IDS, new HashSet<>()));
        boolean initialized = prefs.getBoolean(KEY_INITIALIZED, false);

        if (!scope.equals(previousScope)) {
            seen.clear();
            initialized = false;
        }

        Set<String> currentIds = new HashSet<>();
        List<JsonObject> fresh = new ArrayList<>();
        for (JsonObject flag : flags) {
            String id = value(flag, "id");
            if (id.isEmpty()) continue;
            currentIds.add(id);
            if (!seen.contains(id)) {
                fresh.add(flag);
            }
        }

        // First run for this provider: seed the baseline silently so existing flags do not spam users.
        if (!initialized) {
            prefs.edit()
                    .putStringSet(KEY_SEEN_IDS, currentIds)
                    .putBoolean(KEY_INITIALIZED, true)
                    .putString(KEY_SCOPE, scope)
                    .apply();
            return;
        }

        if (!fresh.isEmpty()) {
            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(new Intent(FlagActivity.ACTION_FLAGS_UPDATED));
            createChannel();
            if (fresh.size() == 1) {
                notify(notificationId(fresh.get(0)), "New flag raised", describe(fresh.get(0)));
            } else {
                notify((int) System.currentTimeMillis(), fresh.size() + " new flags raised", "Tap to review newly flagged records.");
            }
        }

        // Persist the current set so cleared/resolved flags can re-notify if they return.
        prefs.edit()
                .putStringSet(KEY_SEEN_IDS, currentIds)
                .putString(KEY_SCOPE, scope)
                .apply();
    }

    private String scopeKey() {
        String facility = currentFacility();
        String district = currentDistrict();
        if (!facility.isEmpty() || !district.isEmpty()) {
            return "geo:" + facility.toLowerCase() + "|" + district.toLowerCase();
        }
        String field = providerFilterField();
        String value = providerFilterValue(field);
        if ("caseworker_phone".equals(field)) {
            value = normalizedPhone(value);
        }
        if (value.isEmpty()) {
            return "unscoped";
        }
        return field + ":" + value.toLowerCase();
    }

    private int notificationId(JsonObject flag) {
        String id = value(flag, "id");
        return id.isEmpty() ? (int) System.currentTimeMillis() : id.hashCode();
    }
    private void notify(int id, String title, String body) {
        Intent intent = new Intent(getApplicationContext(), FlagActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        try {
            NotificationManagerCompat.from(getApplicationContext()).notify(id, builder.build());
        } catch (SecurityException e) {
            // POST_NOTIFICATIONS not granted (Android 13+); nothing we can do from the worker.
            Timber.w("FlagsPollWorker: notification permission not granted");
        }
    }

    private static String describe(JsonObject flag) {
        String caregiver = value(flag, "caregiver_name");
        String household = value(flag, "household_id");
        String vca = value(flag, "vca_id");
        StringBuilder sb = new StringBuilder();
        if (!caregiver.isEmpty()) sb.append(caregiver);
        if (!household.isEmpty()) sb.append(sb.length() > 0 ? " - HH " : "HH ").append(household);
        if (!vca.isEmpty()) sb.append(sb.length() > 0 ? " - VCA " : "VCA ").append(vca);
        return sb.length() > 0 ? sb.toString() : "A new record was flagged. Tap to review.";
    }

    private static String value(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) return "";
        String v = object.get(key).getAsString();
        return v == null ? "" : v.trim();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Flags", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notifications for new Program Management Platform flags");
        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
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



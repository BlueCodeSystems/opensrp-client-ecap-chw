package com.bluecodeltd.ecap.chw.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bluecodeltd.ecap.chw.BuildConfig;
import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.adapter.FlagsAdapter;
import com.bluecodeltd.ecap.chw.api.ItemApi;
import com.bluecodeltd.ecap.chw.configs.Config;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import timber.log.Timber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlagActivity extends AppCompatActivity {
    public static final String ACTION_FLAGS_UPDATED = "com.bluecodeltd.ecap.chw.ACTION_FLAGS_UPDATED";
    private static final String[] DISPLAY_FIELDS = new String[]{
            "id",
            "status",
            "date_created",
            "household_id",
            "vca_id",
            "caseworker_phone",
            "provider_id",
            "facility",
            "district",
            "comment",
            "caregiver_name",
            "verifier",
            "caseworker_name"
    };
    private static final String TYPE_ALL = "ALL";
    private static final String TYPE_CA = "CA";
    private static final String TYPE_CAREGIVER = "CAREGIVER";
    private static final String TAG = "PmpFlags";
    private static final long FOREGROUND_REFRESH_INTERVAL_MS = 10 * 1000L;

    private final List<JsonObject> allItems = new ArrayList<>();
    private final List<JsonObject> items = new ArrayList<>();
    private FlagsAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private LinearLayout typePills;
    private LinearLayout statusPills;
    private String typeFilter = TYPE_ALL;
    private String statusFilter = null; // null = All statuses
    private boolean flagsRequestInFlight = false;
    private final BroadcastReceiver flagsUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadFlags();
        }
    };
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadFlags(false);
            handler.postDelayed(this, FOREGROUND_REFRESH_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag);

        Toolbar toolbar = findViewById(R.id.toolbar_flags);
        setSupportActionBar(toolbar);
        NavigationMenu.getInstance(this, null, toolbar);

        progressBar = findViewById(R.id.progress);
        emptyView = findViewById(R.id.empty_view);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        typePills = findViewById(R.id.type_pills);
        statusPills = findViewById(R.id.status_pills);
        RecyclerView recyclerView = findViewById(R.id.flagRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FlagsAdapter(items);
        recyclerView.setAdapter(adapter);
        refreshFilters();

        swipeRefreshLayout.setOnRefreshListener(this::loadFlags);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                flagsUpdatedReceiver,
                new IntentFilter(ACTION_FLAGS_UPDATED)
        );
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(flagsUpdatedReceiver);
        handler.removeCallbacks(refreshRunnable);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFlags();
        handler.removeCallbacks(refreshRunnable);
        handler.postDelayed(refreshRunnable, FOREGROUND_REFRESH_INTERVAL_MS);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(refreshRunnable);
        super.onPause();
    }

    private void loadFlags() {
        loadFlags(true);
    }

    private void loadFlags(boolean showLoading) {
        if (flagsRequestInFlight) {
            Log.d(TAG, "PMP flags refresh skipped because another request is still running");
            return;
        }
        flagsRequestInFlight = true;
        Log.d(TAG, "PMP flags refresh started facility=" + currentFacility() + " district=" + currentDistrict() + " field=" + providerFilterField() + " value=" + providerFilterValue(providerFilterField()));
        if (showLoading) {
            progressBar.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(BuildConfig.DEBUG ? new OkHttpClient.Builder().build() : new OkHttpClient())
                .build();

        ItemApi api = retrofit.create(ItemApi.class);
        JsonObject loginBody = new JsonObject();
        loginBody.addProperty("email", BuildConfig.DIRECTUS_EMAIL);
        loginBody.addProperty("password", BuildConfig.DIRECTUS_PASSWORD);
        loginBody.addProperty("mode", "json");

        if (BuildConfig.DIRECTUS_EMAIL.isEmpty() || BuildConfig.DIRECTUS_PASSWORD.isEmpty()) {
            Timber.e("Directus credentials are not configured (DIRECTUS_EMAIL/DIRECTUS_PASSWORD are empty)");
            showError("Flags are not configured on this build");
            return;
        }

        api.login(loginBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Timber.e("Directus login failed: %s %s", response.code(), response.message());
                    showError("Sign-in to flags service failed");
                    return;
                }

                JsonObject data = response.body().getAsJsonObject("data");
                String token = data != null && data.has("access_token") ? data.get("access_token").getAsString() : "";
                if (token.isEmpty()) {
                    Timber.e("Directus login returned no access token");
                    showError("Sign-in to flags service failed");
                    return;
                }

                fetchScopedFlags(api, token, buildProviderScopedQuery(), true);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Timber.e(t, "Directus login failed");
                showError("Could not reach flags service");
            }
        });
    }
    private void fetchScopedFlags(ItemApi api, String token, Map<String, String> query, boolean allowPhoneFallback) {
        api.getItems(Config.BASEURL + "items/" + BuildConfig.DIRECTUS_FLAGS_COLLECTION, "Bearer " + token, query).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if ((!response.isSuccessful() || response.body() == null) && allowPhoneFallback) {
                    Map<String, String> fallback = buildPhoneScopedQuery();
                    if (!fallback.equals(query)) {
                        Timber.w("Provider-scoped flags fetch failed (%s); retrying with caseworker_phone", response.code());
                        fetchScopedFlags(api, token, fallback, false);
                        return;
                    }
                }

                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Timber.e("Flags fetch failed: %s %s", response.code(), response.message());
                    showError("Could not load flags");
                    return;
                }

                List<JsonObject> fetched = new ArrayList<>();
                JsonArray data = response.body().getAsJsonArray("data");
                if (data != null) {
                    for (JsonElement element : data) {
                        if (element != null && element.isJsonObject()) {
                            JsonObject flag = element.getAsJsonObject();
                            if (matchesCurrentProvider(flag)) {
                                fetched.add(flag);
                            }
                        }
                    }
                }
                if (fetched.isEmpty() && allowPhoneFallback && !normalizedPhone(currentUserPhone()).isEmpty()) {
                    fetchScopedFlags(api, token, buildPhoneScopedQuery(), false);
                    return;
                }
                flagsRequestInFlight = false;
                allItems.clear();
                allItems.addAll(fetched);
                Log.d(TAG, "PMP flags refresh success accepted=" + fetched.size() + " statuses=" + statusSummary(fetched));
                refreshFilters();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (allowPhoneFallback) {
                    Timber.w(t, "Provider-scoped flags fetch failed; retrying with caseworker_phone");
                    fetchScopedFlags(api, token, buildPhoneScopedQuery(), false);
                    return;
                }
                Timber.e(t, "Flags fetch failed");
                showError("Could not reach flags service");
            }
        });
    }

    private Map<String, String> buildProviderScopedQuery() {
        // Flags are routed by facility (and district) on the PMP backend, so scope by the
        // logged-in facility OR district first. Fall back to the legacy provider/phone
        // scoping only when both are unknown.
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
        query.put("fields", fields(includeProviderId, DISPLAY_FIELDS));
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
        // Primary scope: the logged-in facility or district (matches PMP's geo-based routing).
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
            return currentCaseworkerName();
        }
        return currentProviderId();
    }

    private String currentProviderId() {
        try {
            String registeredAnm = org.smartregister.Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            if (FlagsAdapter.isMeaningful(registeredAnm)) {
                return registeredAnm.trim();
            }
        } catch (Exception ignored) {
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String[] keys = new String[]{"provider_id", "providerId", "provider", "sub"};
        for (String key : keys) {
            String value = prefs.getString(key, "");
            if (FlagsAdapter.isMeaningful(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private String currentUserPhone() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("phone", "");
    }

    /** The facility the current user is logged into (stored at login, see DashboardActivity). */
    private String currentFacility() {
        String facility = PreferenceManager.getDefaultSharedPreferences(this).getString("facility", "");
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
        String district = PreferenceManager.getDefaultSharedPreferences(this).getString("district", "");
        if (district == null) {
            return "";
        }
        district = district.trim();
        if ("anonymous".equalsIgnoreCase(district) || "null".equalsIgnoreCase(district)) {
            return "";
        }
        return district;
    }

    private String currentCaseworkerName() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("caseworker_name", "").trim();
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
    private void showError(String message) {
        flagsRequestInFlight = false;
        Log.w(TAG, "PMP flags refresh failed: " + message);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        // Keep already-displayed flags on screen; don't show the empty view or a
        // toast over a populated list (e.g. a transient blip during auto-refresh).
        if (!allItems.isEmpty()) {
            return;
        }
        emptyView.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // ---- Filtering -------------------------------------------------------

    /** Rebuilds the filter pills (with counts) and re-applies the active filters. */
    private void refreshFilters() {
        rebuildTypePills();
        rebuildStatusPills();
        applyFilters();
    }

    private void applyFilters() {
        items.clear();
        for (JsonObject flag : allItems) {
            if (matchesType(flag) && matchesStatus(flag)) {
                items.add(flag);
            }
        }
        adapter.notifyDataSetChanged();
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean matchesType(JsonObject flag) {
        if (TYPE_ALL.equals(typeFilter)) {
            return true;
        }
        boolean isCa = FlagsAdapter.isMeaningful(value(flag, "vca_id"));
        if (TYPE_CA.equals(typeFilter)) {
            return isCa;
        }
        // Caregiver-level: a household flag that is not tied to a specific VCA.
        return !isCa && FlagsAdapter.isMeaningful(value(flag, "household_id"));
    }

    private boolean matchesStatus(JsonObject flag) {
        if (statusFilter == null) {
            return true;
        }
        return statusFilter.equalsIgnoreCase(value(flag, "status"));
    }

    private void rebuildTypePills() {
        int all = allItems.size();
        int ca = 0;
        int caregiver = 0;
        for (JsonObject flag : allItems) {
            if (FlagsAdapter.isMeaningful(value(flag, "vca_id"))) {
                ca++;
            } else if (FlagsAdapter.isMeaningful(value(flag, "household_id"))) {
                caregiver++;
            }
        }
        typePills.removeAllViews();
        typePills.addView(makePill("All - " + all, TYPE_ALL.equals(typeFilter), v -> selectType(TYPE_ALL)));
        typePills.addView(makePill("CA (child) - " + ca, TYPE_CA.equals(typeFilter), v -> selectType(TYPE_CA)));
        typePills.addView(makePill("Caregiver (household) - " + caregiver, TYPE_CAREGIVER.equals(typeFilter), v -> selectType(TYPE_CAREGIVER)));
    }

    private void rebuildStatusPills() {
        // Distinct statuses in the order they first appear, with counts.
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (JsonObject flag : allItems) {
            String raw = value(flag, "status");
            if (raw.isEmpty()) {
                continue;
            }
            counts.merge(raw, 1, Integer::sum);
        }

        // Reset a status filter that no longer exists in the data.
        if (statusFilter != null) {
            boolean stillPresent = false;
            for (String raw : counts.keySet()) {
                if (statusFilter.equalsIgnoreCase(raw)) {
                    stillPresent = true;
                    break;
                }
            }
            if (!stillPresent) {
                statusFilter = null;
            }
        }

        statusPills.removeAllViews();
        statusPills.addView(makePill("All - " + allItems.size(), statusFilter == null, v -> selectStatus(null)));
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String raw = entry.getKey();
            boolean selected = statusFilter != null && statusFilter.equalsIgnoreCase(raw);
            statusPills.addView(makePill(capitalize(raw) + " - " + entry.getValue(), selected, v -> selectStatus(raw)));
        }
    }

    private void selectType(String type) {
        typeFilter = type;
        refreshFilters();
    }

    private void selectStatus(String status) {
        statusFilter = status;
        refreshFilters();
    }

    private TextView makePill(String text, boolean selected, View.OnClickListener onClick) {
        TextView pill = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(dp(8));
        pill.setLayoutParams(lp);
        pill.setText(text);
        pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        pill.setTextColor(ContextCompat.getColorStateList(this, R.color.filter_pill_text));
        pill.setBackgroundResource(R.drawable.bg_filter_pill);
        pill.setGravity(Gravity.CENTER);
        pill.setPadding(dp(14), dp(6), dp(14), dp(6));
        pill.setSelected(selected);
        pill.setOnClickListener(onClick);
        return pill;
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }

    private static String statusSummary(List<JsonObject> flags) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (JsonObject flag : flags) {
            String status = value(flag, "status");
            if (status.isEmpty()) {
                status = "unknown";
            }
            counts.merge(status, 1, Integer::sum);
        }
        return counts.toString();
    }
    private static String value(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return "";
        }
        String v = object.get(key).getAsString();
        return v == null ? "" : v.trim();
    }

    private static String capitalize(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        String lower = trimmed.toLowerCase(Locale.US);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}


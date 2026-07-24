package org.smartregister.chw.core.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.core.BuildConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class DirectusFlagsRepository {
    private DirectusFlagsRepository() {}

    /**
     * Debug switch: set to true to bypass caseworker scoping and show every flag
     * (useful for QA on accounts that have no flags of their own). Keep false for release.
     */
    public static final boolean SHOW_ALL_FLAGS = false;


    public interface CountCallback {
        void onResult(int count);
        void onError(Throwable throwable);
    }

    public static void fetchCount(String caseworkerName, String facility, String phone, String email, String password, String caseworkerField, String collection, CountCallback callback) {
        new Thread(() -> {
            try {
                String token = login(email, password);
                if (token == null || token.isEmpty()) {
                    throw new IllegalStateException("Missing access token");
                }
                List<JSONObject> items;
                if ("caseworker_phone".equals(caseworkerField)) {
                    items = fetchItems(token, collection, null, null, false);
                    String expectedPhone = normalizedPhone(phone);
                    int count = 0;
                    for (JSONObject item : items) {
                        if (!expectedPhone.isEmpty() && expectedPhone.equals(normalizedPhone(item.optString("caseworker_phone", "")))) {
                            count++;
                        }
                    }
                    callback.onResult(count);
                    return;
                }
                if (caseworkerName != null && !caseworkerName.trim().isEmpty()) {
                    items = fetchItems(token, collection, caseworkerField, caseworkerName, true);
                } else {
                    items = new ArrayList<>();
                }
                if (items.isEmpty() && phone != null && !phone.trim().isEmpty()) {
                    items = fetchItems(token, collection, null, null, false);
                    String expectedPhone = normalizedPhone(phone);
                    int count = 0;
                    for (JSONObject item : items) {
                        if (!expectedPhone.isEmpty() && expectedPhone.equals(normalizedPhone(item.optString("caseworker_phone", "")))) {
                            count++;
                        }
                    }
                    callback.onResult(count);
                    return;
                }
                callback.onResult(items.size());
            } catch (Throwable t) {
                callback.onError(t);
            }
        }).start();
    }

    private static String login(String email, String password) throws Exception {
        HttpURLConnection connection = openConnection("auth/login", "POST");
        JSONObject body = new JSONObject();
        body.put("email", email == null ? "" : email);
        body.put("password", password == null ? "" : password);
        body.put("mode", "json");
        writeBody(connection, body.toString());
        JSONObject response = readJson(connection);
        JSONObject data = response.optJSONObject("data");
        return data != null ? data.optString("access_token", "") : "";
    }

    private static List<JSONObject> fetchItems(String token, String collection, String filterField, String filterValue, boolean includeProviderId) throws Exception {
        StringBuilder path = new StringBuilder("items/").append(collection)
                .append("?fields=").append(fields(includeProviderId))
                .append("&sort=-date_created")
                .append("&limit=-1");
        if (!SHOW_ALL_FLAGS && filterField != null && !filterField.trim().isEmpty() && filterValue != null && !filterValue.trim().isEmpty()) {
            path.append("&filter[").append(filterField.trim()).append("][_eq]=")
                    .append(java.net.URLEncoder.encode(filterValue.trim(), "UTF-8"));
        }
        HttpURLConnection connection = openConnection(path.toString(), "GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        JSONObject response = readJson(connection);
        JSONArray data = response.optJSONArray("data");
        List<JSONObject> items = new ArrayList<>();
        if (data == null) return items;
        for (int i = 0; i < data.length(); i++) {
            JSONObject item = data.optJSONObject(i);
            if (item != null) items.add(item);
        }
        return items;
    }

    private static String fields(boolean includeProviderId) {
        String fields = "id,status,date_created,household_id,vca_id,caseworker_phone,facility,comment,caregiver_name,verifier,caseworker_name";
        return includeProviderId ? fields + ",provider_id" : fields;
    }

    private static String normalizedPhone(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("260") && digits.length() == 12) {
            return "0" + digits.substring(3);
        }
        return digits;
    }
    private static HttpURLConnection openConnection(String path, String method) throws Exception {
        String base = BuildConfig.DIRECTUS_BASE_URL.endsWith("/") ? BuildConfig.DIRECTUS_BASE_URL : BuildConfig.DIRECTUS_BASE_URL + "/";
        URL url = new URL(base + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

    private static void writeBody(HttpURLConnection connection, String body) throws Exception {
        connection.setDoOutput(true);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(body.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static JSONObject readJson(HttpURLConnection connection) throws Exception {
        int code = connection.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream(),
                StandardCharsets.UTF_8
        ));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return new JSONObject(builder.toString());
    }

}

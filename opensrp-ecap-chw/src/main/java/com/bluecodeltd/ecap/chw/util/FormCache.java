package com.bluecodeltd.ecap.chw.util;

import android.content.Context;

import com.bluecodeltd.ecap.chw.util.Threading;

import org.json.JSONObject;
import org.smartregister.util.FormUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import timber.log.Timber;

/**
 * Lightweight JSON form cache to avoid repeated disk reads for large forms.
 * Provides async warming plus synchronous retrieval with a small LRU eviction policy.
 */
public final class FormCache {

    private static final int MAX_CACHE_SIZE = 16;
    private static final Map<String, String> CACHE = Collections.synchronizedMap(
            new LinkedHashMap<String, String>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            });

    private FormCache() {
        // Utility
    }

    public static void warmFormAsync(Context context, String formName) {
        warmFormsAsync(context, formName);
    }

    public static void warmFormsAsync(Context context, String... formNames) {
        if (context == null || formNames == null || formNames.length == 0) {
            return;
        }
        Threading.io(() -> {
            for (String formName : formNames) {
                if (formName == null) {
                    continue;
                }
                try {
                    boolean alreadyCached;
                    synchronized (CACHE) {
                        alreadyCached = CACHE.containsKey(formName);
                    }
                    if (!alreadyCached) {
                        loadAndCache(context, formName);
                    }
                } catch (Exception e) {
                    Timber.w(e, "Unable to warm form %s", formName);
                }
            }
        });
    }

    public static boolean isWarmed(String formName) {
        synchronized (CACHE) {
            return CACHE.containsKey(formName);
        }
    }

    public static JSONObject obtainFormTemplate(Context context, String formName) throws Exception {
        String serialized;
        synchronized (CACHE) {
            serialized = CACHE.get(formName);
        }
        if (serialized == null) {
            serialized = loadAndCache(context, formName);
        }
        return new JSONObject(serialized);
    }

    private static String loadAndCache(Context context, String formName) throws Exception {
        if (context == null || formName == null) {
            return null;
        }
        FormUtils formUtils = new FormUtils(context);
        JSONObject formJson = formUtils.getFormJson(formName);
        String serialized = formJson.toString();
        synchronized (CACHE) {
            CACHE.put(formName, serialized);
        }
        return serialized;
    }
}

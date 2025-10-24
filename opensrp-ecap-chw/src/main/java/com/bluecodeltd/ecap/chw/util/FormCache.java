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
                    String key = cacheKey(context, formName);
                    boolean alreadyCached;
                    synchronized (CACHE) {
                        alreadyCached = CACHE.containsKey(key);
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

    public static boolean isWarmed(Context context, String formName) {
        if (formName == null) {
            return false;
        }
        synchronized (CACHE) {
            return CACHE.containsKey(cacheKey(context, formName));
        }
    }

    public static JSONObject obtainFormTemplate(Context context, String formName) throws Exception {
        if (formName == null) {
            return null;
        }
        String key = cacheKey(context, formName);
        String serialized;
        synchronized (CACHE) {
            serialized = CACHE.get(key);
        }
        if (serialized == null) {
            serialized = loadAndCache(context, formName);
        }
        return serialized == null ? null : new JSONObject(serialized);
    }

    public static void clear() {
        synchronized (CACHE) {
            CACHE.clear();
        }
    }

    private static String loadAndCache(Context context, String formName) throws Exception {
        if (context == null || formName == null) {
            return null;
        }
        FormUtils formUtils = new FormUtils(context);
        JSONObject formJson = formUtils.getFormJson(formName);
        if (formJson == null) {
            return null;
        }
        String serialized = formJson.toString();
        synchronized (CACHE) {
            CACHE.put(cacheKey(context, formName), serialized);
        }
        return serialized;
    }

    private static String cacheKey(Context context, String formName) {
        String localeTag = resolveLocaleTag(context);
        return formName + "|" + localeTag;
    }

    private static String resolveLocaleTag(Context context) {
        if (context == null) {
            return "";
        }
        try {
            android.content.res.Configuration configuration = context.getResources().getConfiguration();
            java.util.Locale locale;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                locale = configuration.getLocales().isEmpty() ? java.util.Locale.getDefault() : configuration.getLocales().get(0);
            } else {
                locale = configuration.locale;
            }
            if (locale == null) {
                return "";
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                return locale.toLanguageTag();
            }
            StringBuilder builder = new StringBuilder(locale.getLanguage());
            if (!locale.getCountry().isEmpty()) {
                builder.append('-').append(locale.getCountry());
            }
            if (!locale.getVariant().isEmpty()) {
                builder.append('-').append(locale.getVariant());
            }
            return builder.toString();
        } catch (Exception e) {
            Timber.w(e, "Unable to resolve locale tag");
            return "";
        }
    }
}

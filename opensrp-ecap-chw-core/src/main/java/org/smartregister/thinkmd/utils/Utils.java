package org.smartregister.thinkmd.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

/**
 * Minimal helper utilities used by the ThinkMD integration points. The original
 * library exposes a base64 helper; we provide the same behaviour here so the
 * surrounding features continue to operate when the upstream artifact is
 * unavailable.
 */
public final class Utils {

    private Utils() {
        // no instances
    }

    public static String decodeBase64(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        byte[] decoded = Base64.decode(value, Base64.DEFAULT);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}

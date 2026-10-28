package com.bluecodeltd.ecap.chw.util;

import org.json.JSONArray;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Reads the values the service register forms store: dd-MM-yyyy dates, ticked options and option keys. */
public final class ServiceRegisterValues {

    public static final DateTimeFormatter STORED = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter SHOWN = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    private ServiceRegisterValues() {
    }

    public static String get(Map<String, String> columns, String key) {
        String value = columns != null ? columns.get(key) : null;
        return value == null ? "" : value.trim();
    }

    public static boolean is(Map<String, String> columns, String key, String value) {
        return value.equalsIgnoreCase(get(columns, key));
    }

    /** A stored date, or null when it is missing or not a date. */
    public static LocalDate date(String stored) {
        if (stored == null || stored.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(stored.trim(), STORED);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDate date(Map<String, String> columns, String key) {
        return date(get(columns, key));
    }

    public static String stored(LocalDate date) {
        return date == null ? "" : date.format(STORED);
    }

    public static String shown(LocalDate date) {
        return date == null ? "" : date.format(SHOWN);
    }

    /** Days from {@code from} to {@code to}; negative when {@code to} is earlier. */
    public static long days(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }

    /** The keys ticked in a check box field, stored as a JSON array or a comma separated list. */
    public static List<String> ticked(String stored) {
        if (stored == null || stored.trim().isEmpty()) return Collections.emptyList();
        List<String> keys = new ArrayList<>();
        String value = stored.trim();
        if (value.startsWith("[")) {
            try {
                JSONArray array = new JSONArray(value);
                for (int i = 0; i < array.length(); i++) {
                    String key = array.optString(i).trim();
                    if (!key.isEmpty()) keys.add(key);
                }
                return keys;
            } catch (Exception ignored) {
                value = value.replaceAll("[\\[\\]\"]", "");
            }
        }
        for (String key : value.split(",")) {
            if (!key.trim().isEmpty()) keys.add(key.trim());
        }
        return keys;
    }

    /** The label an option key is shown with, or the key itself when the field has no such option. */
    public static String label(String field, String key) {
        Map<String, String> labels = ServiceRegisterFields.OPTION_LABELS.get(field);
        String label = labels != null ? labels.get(key) : null;
        return label != null ? label : key;
    }

    /** A column as it reads in the details sheet: dates spelt out, options by label, ticked lists joined. */
    public static String display(String field, String stored) {
        if (stored == null || stored.trim().isEmpty()) return "";
        LocalDate date = date(stored);
        if (date != null) return shown(date);
        if (stored.trim().startsWith("[")) {
            List<String> labels = new ArrayList<>();
            for (String key : ticked(stored)) labels.add(label(field, key));
            return String.join(", ", labels);
        }
        return label(field, stored.trim());
    }
}

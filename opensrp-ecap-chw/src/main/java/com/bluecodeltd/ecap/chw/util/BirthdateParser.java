package com.bluecodeltd.ecap.chw.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Lenient birthdate parsing for display code. Most records store "dd-MM-yyyy", but caregiver and
 * mother birthdates from older forms/imports are "dd MMM yyyy" (e.g. "09 Mar 1993"), and a few use
 * "dd/MM/yyyy" or ISO. Parsing with a single strict pattern crashed profile screens and registers.
 */
public final class BirthdateParser {

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("d-M-uuuu"),
            DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/uuuu"),
            DateTimeFormatter.ISO_LOCAL_DATE,
    };

    private BirthdateParser() {
    }

    /** Returns the parsed date, or null when the value is empty or in no known format. */
    public static LocalDate parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }
}

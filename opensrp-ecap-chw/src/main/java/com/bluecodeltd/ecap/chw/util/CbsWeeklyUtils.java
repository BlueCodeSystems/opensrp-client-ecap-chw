package com.bluecodeltd.ecap.chw.util;

import com.bluecodeltd.ecap.chw.model.MonthlyReportModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Shared Sunday-to-Saturday week math and numeric-field aggregation for Community Alert (CBS)
 * reports, used by both the submissions list (grouping rows by week) and the report detail
 * screen (rolling up totals for every report submitted in the same week as the one being viewed).
 */
public final class CbsWeeklyUtils {

    public static final String[] AFFECTED_FIELD_KEYS = {
            "affected_f_0_4", "affected_f_5_9", "affected_f_10_17", "affected_f_18_plus",
            "affected_m_0_4", "affected_m_5_9", "affected_m_10_17", "affected_m_18_plus"
    };
    public static final String[] DEATH_FIELD_KEYS = {
            "dead_f_0_4", "dead_f_5_9", "dead_f_10_17", "dead_f_18_plus",
            "dead_m_0_4", "dead_m_5_9", "dead_m_10_17", "dead_m_18_plus"
    };
    public static final String[] SUSPECTED_FIELD_KEYS = {"suspected_female", "suspected_male"};

    private static final String REPORTING_DATE_PATTERN = "dd-MM-yyyy";

    private CbsWeeklyUtils() {
    }

    public static final class Week {
        public final String key;
        public final String label;
        public final Date start;
        public final Date end;

        private Week(String key, String label, Date start, Date end) {
            this.key = key;
            this.label = label;
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Rolls the given date back day-by-day to the Sunday that starts its week. Done by hand
     * (rather than Calendar.set(DAY_OF_WEEK, SUNDAY)) so the Sunday..Saturday boundary is fixed
     * regardless of the device locale's configured first-day-of-week.
     */
    public static Week weekFor(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        Date start = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        Date end = calendar.getTime();

        String key = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(start);
        SimpleDateFormat dayFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String label = dayFormat.format(start) + " - " + dayFormat.format(end) + ", " + yearFormat.format(end);
        return new Week(key, label, start, end);
    }

    /** Parses a report's "reporting_month" value (actually a full "dd-MM-yyyy" date) and returns its week. */
    public static Week weekForReportingDate(String reportingMonth) {
        if (reportingMonth == null || reportingMonth.trim().isEmpty()) {
            return null;
        }
        try {
            Date date = new SimpleDateFormat(REPORTING_DATE_PATTERN, Locale.getDefault()).parse(reportingMonth.trim());
            return weekFor(date);
        } catch (Exception e) {
            return null;
        }
    }

    /** Returns every report from {@code source} that falls in the same Sunday..Saturday week as {@code referenceReportingMonth}. */
    public static List<MonthlyReportModel> filterSameWeek(List<MonthlyReportModel> source, String referenceReportingMonth) {
        List<MonthlyReportModel> result = new ArrayList<>();
        Week referenceWeek = weekForReportingDate(referenceReportingMonth);
        if (source == null || referenceWeek == null) {
            return result;
        }
        for (MonthlyReportModel item : source) {
            Week week = weekForReportingDate(item.getReporting_month());
            if (week != null && referenceWeek.key.equals(week.key)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Counts distinct Sunday-to-Saturday weeks represented in {@code reports} -- i.e. how many
     * reports would show up in the submissions list once same-week reports are collapsed into
     * one row. Reports whose date can't be parsed are each counted as their own "week" so they
     * aren't silently dropped from the count.
     */
    public static int countDistinctWeeks(List<MonthlyReportModel> reports) {
        if (reports == null || reports.isEmpty()) {
            return 0;
        }
        java.util.Set<String> weekKeys = new java.util.HashSet<>();
        int unparseable = 0;
        for (MonthlyReportModel report : reports) {
            Week week = weekForReportingDate(report.getReporting_month());
            if (week != null) {
                weekKeys.add(week.key);
            } else {
                unparseable++;
            }
        }
        return weekKeys.size() + unparseable;
    }

    /** Sums the CBS numeric fields (affected/dead/suspected) across every report in {@code weekReports}. */
    public static java.util.Map<String, String> aggregateNumericFields(List<MonthlyReportModel> weekReports) {
        java.util.Map<String, String> totals = new java.util.HashMap<>();
        for (String key : AFFECTED_FIELD_KEYS) {
            totals.put(key, String.valueOf(sumField(weekReports, key)));
        }
        for (String key : DEATH_FIELD_KEYS) {
            totals.put(key, String.valueOf(sumField(weekReports, key)));
        }
        for (String key : SUSPECTED_FIELD_KEYS) {
            totals.put(key, String.valueOf(sumField(weekReports, key)));
        }
        return totals;
    }

    private static int sumField(List<MonthlyReportModel> reports, String key) {
        int sum = 0;
        for (MonthlyReportModel item : reports) {
            String value = item.getAdditionalField(key);
            if (value == null || value.trim().isEmpty()) {
                continue;
            }
            try {
                sum += Integer.parseInt(value.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return sum;
    }
}

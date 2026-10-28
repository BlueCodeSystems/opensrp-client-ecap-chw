package com.bluecodeltd.ecap.chw.util;

import static com.bluecodeltd.ecap.chw.util.ServiceRegisterValues.get;
import static com.bluecodeltd.ecap.chw.util.ServiceRegisterValues.is;
import static com.bluecodeltd.ecap.chw.util.ServiceRegisterValues.shown;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.view_holder.RegisterRow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * What a service register row says about its record (status chip, due chip, detail line) and the
 * filters above the list. The status rules follow the AFGHS-PCZ tools: missed appointments are traced,
 * PrEP clients are reviewed after a month and reassessed every three months, and a referral not
 * completed within a month is overdue.
 */
public final class ServiceRegisterStatus {

    public enum Tone {
        GOOD(R.color.dash_status_good, R.drawable.reg_pill_good, R.color.reg_pill_good_text),
        WARNING(R.color.dash_status_warning, R.drawable.reg_pill_warning, R.color.reg_pill_warning_text),
        CRITICAL(R.color.dash_status_critical, R.drawable.reg_pill_critical, R.color.reg_pill_critical_text),
        INFO(R.color.dash_cat_blue, R.drawable.reg_pill_info, R.color.reg_pill_info_text),
        NEUTRAL(R.color.reg_status_none, R.drawable.reg_pill_neutral, R.color.reg_pill_neutral_text);

        @ColorRes public final int stripe;
        @DrawableRes public final int pill;
        @ColorRes public final int text;

        Tone(int stripe, int pill, int text) {
            this.stripe = stripe;
            this.pill = pill;
            this.text = text;
        }
    }

    /** What one row shows. */
    public static final class Row {
        public Tone tone = Tone.NEUTRAL;
        public String status = "";
        public String detail = "";
        public String due = "";
        public Tone dueTone = Tone.INFO;
        /** Whether the follow-up action still applies; closed records only open their details. */
        public boolean open = true;
    }

    /** A filter chip: its label and the condition it adds to the list query. */
    public static final class Filter {
        @StringRes public final int label;
        public final String condition;

        Filter(int label, String condition) {
            this.label = label;
            this.condition = condition;
        }
    }

    private static final int OVERDUE_REFERRAL_DAYS = 30;
    private static final int DUE_SOON_DAYS = 7;

    private static final Map<String, String> SHORT_SUBPOP = new HashMap<>();

    static {
        SHORT_SUBPOP.put("calhiv", "C/ALHIV");
        SHORT_SUBPOP.put("hei", "HEI");
        SHORT_SUBPOP.put("ca_wlhiv", "C/A of WLHIV");
        SHORT_SUBPOP.put("child_plhiv", "Child of PLHIV");
        SHORT_SUBPOP.put("ssv", "SSV");
        SHORT_SUBPOP.put("hiv_pos_caregiver", "HIV+ caregiver");
        SHORT_SUBPOP.put("hiv_pos_pbfw", "HIV+ PBFW");
        SHORT_SUBPOP.put("hiv_neg_pbfw", "HIV− PBF AGYW");
        SHORT_SUBPOP.put("hiv_neg_pbfw_25", "HIV− PBFW 25+");
        SHORT_SUBPOP.put("ayp_high_risk", "AYP at risk");
        SHORT_SUBPOP.put("caregiver", "Caregiver");
        SHORT_SUBPOP.put("other", "Other");
    }

    private ServiceRegisterStatus() {
    }

    // ------------------------------------------------------------------ rows

    public static Row row(Context context, ServiceRegister register, Map<String, String> columns) {
        LocalDate today = LocalDate.now();
        switch (register) {
            case APPOINTMENTS:
                return appointment(context, columns, today);
            case PREP:
                return prep(context, columns, today);
            case REFERRAL_LINKAGES:
                return referral(context, columns, today);
            default:
                return new Row();
        }
    }

    private static Row appointment(Context context, Map<String, String> c, LocalDate today) {
        Row row = new Row();
        LocalDate date = ServiceRegisterValues.date(c, "appointment_date");
        String type = is(c, "appointment_type", "other") && !get(c, "appointment_type_other").isEmpty()
                ? get(c, "appointment_type_other")
                : ServiceRegisterValues.label("appointment_type", get(c, "appointment_type"));
        StringBuilder detail = new StringBuilder(type);
        if (date != null) detail.append(detail.length() > 0 ? " · " : "").append(shown(date));
        if (!get(c, "appointment_time").isEmpty()) detail.append(", ").append(get(c, "appointment_time"));
        row.detail = detail.toString();

        String outcome = get(c, "tracking_outcome");
        if (is(c, "appointment_kept", "Yes")) {
            close(row, Tone.GOOD, context.getString(R.string.srv_kept));
        } else if ("bbtc".equals(outcome)) {
            close(row, Tone.GOOD, context.getString(R.string.srv_back_in_care));
        } else if ("traced_served".equals(outcome)) {
            close(row, Tone.GOOD, context.getString(R.string.srv_traced_served));
        } else if ("refused".equals(outcome)) {
            close(row, Tone.CRITICAL, context.getString(R.string.srv_refused));
        } else if (is(c, "appointment_kept", "No") || (date != null && date.isBefore(today))) {
            long late = date != null ? Math.max(0, ServiceRegisterValues.days(date, today)) : 0;
            row.tone = Tone.WARNING;
            row.status = context.getString("sbf".equals(outcome) || !get(c, "date_tracking_initiated").isEmpty()
                    ? R.string.srv_being_traced : R.string.srv_missed, days(context, late));
        } else if (date != null) {
            long until = ServiceRegisterValues.days(today, date);
            row.tone = Tone.INFO;
            row.status = until == 0 ? context.getString(R.string.srv_due_today)
                    : until == 1 ? context.getString(R.string.srv_due_tomorrow)
                    : context.getString(R.string.srv_due_in, days(context, until));
        }
        if (row.open && is(c, "reminder_given", "Yes")) {
            row.due = context.getString(R.string.srv_reminder_given);
            row.dueTone = Tone.NEUTRAL;
        }
        return row;
    }

    private static Row prep(Context context, Map<String, String> c, LocalDate today) {
        Row row = new Row();
        String status = get(c, "prep_status");
        LocalDate start = ServiceRegisterValues.date(c, "prep_start_date");
        if (start == null) start = ServiceRegisterValues.date(c, "facility_prep_start_date");
        switch (status) {
            case "on_prep":
                row.tone = Tone.GOOD;
                row.status = context.getString(R.string.srv_on_prep);
                String type = get(c, "prep_type").isEmpty() ? get(c, "facility_prep_type") : get(c, "prep_type");
                row.detail = start != null ? context.getString(R.string.srv_prep_since, shown(start)) : "";
                if (!type.isEmpty()) {
                    row.detail += (row.detail.isEmpty() ? "" : " · ") + ServiceRegisterValues.label("prep_type", type);
                }
                break;
            case "referred":
                row.tone = Tone.WARNING;
                row.status = context.getString(R.string.srv_referral_pending);
                row.detail = context.getString(R.string.srv_prep_referred);
                break;
            case "eligible":
                row.tone = Tone.INFO;
                row.status = context.getString(R.string.srv_eligible);
                row.detail = context.getString(R.string.srv_prep_eligible);
                break;
            case "deferred":
                row.tone = Tone.WARNING;
                row.status = context.getString(R.string.srv_deferred);
                row.detail = context.getString(R.string.srv_prep_deferred);
                break;
            case "not_at_risk":
                close(row, Tone.NEUTRAL, context.getString(R.string.srv_not_at_risk));
                row.detail = context.getString(R.string.srv_prep_not_at_risk);
                break;
            case "hiv_positive":
                boolean linked = is(c, "linked_to_art", "Yes");
                row.tone = linked ? Tone.NEUTRAL : Tone.CRITICAL;
                row.status = context.getString(linked ? R.string.srv_linked_art : R.string.srv_link_art);
                row.detail = context.getString(R.string.srv_prep_positive);
                row.open = !linked;
                break;
            case "stopped":
                close(row, Tone.NEUTRAL, context.getString(R.string.srv_stopped));
                row.detail = get(c, "stop_reason");
                break;
            case "no_longer_at_risk":
                close(row, Tone.NEUTRAL, context.getString(R.string.srv_no_longer_at_risk));
                break;
            case "awaiting_test":
                row.tone = Tone.WARNING;
                row.status = context.getString(R.string.srv_awaiting_test);
                break;
            case "Initiated":
            case "Continuing":
            case "Restarted":
                // Saved with the interim form, before the PrEP screening steps existed
                row.tone = Tone.GOOD;
                row.status = context.getString(R.string.srv_on_prep);
                break;
            case "Discontinued":
                close(row, Tone.NEUTRAL, context.getString(R.string.srv_stopped));
                break;
            default:
                row.status = "";
                break;
        }

        LocalDate review = ServiceRegisterValues.date(c, "review_1m_date");
        LocalDate reassess = ServiceRegisterValues.date(c, "next_reassessment_date");
        if ("on_prep".equals(status) && review != null && !is(c, "review_1m_completed", "Yes")) {
            row.dueTone = review.isBefore(today) ? Tone.WARNING : Tone.INFO;
            row.due = review.isBefore(today) ? context.getString(R.string.srv_review_overdue)
                    : context.getString(R.string.srv_review_due, shown(review));
        } else if (reassess != null && row.open && !"hiv_positive".equals(status)) {
            row.dueTone = reassess.isBefore(today) ? Tone.WARNING : Tone.INFO;
            row.due = reassess.isBefore(today) ? context.getString(R.string.srv_reassess_overdue)
                    : context.getString(R.string.srv_reassess_due, shown(reassess));
        }
        return row;
    }

    private static Row referral(Context context, Map<String, String> c, LocalDate today) {
        Row row = new Row();
        String service = is(c, "referral_service", "other") && !get(c, "referral_service_other").isEmpty()
                ? get(c, "referral_service_other")
                : ServiceRegisterValues.label("referral_service", get(c, "referral_service"));
        String from = place(c, "referred_from");
        String to = place(c, "referred_to");
        StringBuilder detail = new StringBuilder(service);
        if (!from.isEmpty() || !to.isEmpty()) {
            detail.append(detail.length() > 0 ? " · " : "").append(from).append(" → ").append(to);
        }
        row.detail = detail.toString();

        LocalDate referred = ServiceRegisterValues.date(c, "date_referral_received");
        if (is(c, "service_received", "Yes")) {
            LocalDate received = ServiceRegisterValues.date(c, "date_service_received");
            close(row, Tone.GOOD, received != null ? context.getString(R.string.srv_received_on, shown(received))
                    : context.getString(R.string.srv_received));
        } else if (is(c, "service_received", "No")) {
            row.tone = Tone.CRITICAL;
            row.status = context.getString(R.string.srv_not_received);
        } else if (referred != null) {
            long waiting = Math.max(0, ServiceRegisterValues.days(referred, today));
            row.tone = waiting > OVERDUE_REFERRAL_DAYS ? Tone.WARNING : Tone.INFO;
            row.status = context.getString(waiting > OVERDUE_REFERRAL_DAYS ? R.string.srv_overdue : R.string.srv_pending,
                    days(context, waiting));
        }
        if (referred != null) {
            row.due = context.getString(R.string.srv_referred_on, shown(referred));
            row.dueTone = Tone.NEUTRAL;
        }
        return row;
    }

    private static void close(Row row, Tone tone, String status) {
        row.tone = tone;
        row.status = status;
        row.open = false;
    }

    private static String place(Map<String, String> c, String key) {
        String value = get(c, key);
        if ("other".equals(value) && !get(c, key + "_other").isEmpty()) return get(c, key + "_other");
        if ("facility".equals(value)) return "Facility";
        return ServiceRegisterValues.label(key, value);
    }

    private static String days(Context context, long days) {
        int count = (int) Math.min(Integer.MAX_VALUE, days);
        return context.getResources().getQuantityString(R.plurals.srv_days, count, count);
    }

    // ------------------------------------------------------------------ second line of the row

    /** "Female · 4y · C/ALHIV" style summary: sex, age, the first sub-population and pregnancy. */
    public static String summary(Map<String, String> c) {
        List<String> parts = new ArrayList<>();
        parts.add(RegisterRow.sex(get(c, "gender")));
        parts.add(RegisterRow.age(get(c, "birthdate")));
        List<String> subpops = ServiceRegisterValues.ticked(get(c, "sub_population"));
        if (!subpops.isEmpty()) {
            String first = SHORT_SUBPOP.containsKey(subpops.get(0)) ? SHORT_SUBPOP.get(subpops.get(0)) : subpops.get(0);
            parts.add(subpops.size() > 1 ? first + " +" + (subpops.size() - 1) : first);
        }
        if (is(c, "pregnant", "Yes")) parts.add("Pregnant");
        else if (is(c, "breastfeeding", "Yes")) parts.add("Breastfeeding");
        return RegisterRow.join(parts.toArray(new String[0]));
    }

    // ------------------------------------------------------------------ filters

    public static List<Filter> filters(ServiceRegister register) {
        String t = register.table + ".";
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String soon = LocalDate.now().plusDays(DUE_SOON_DAYS).format(DateTimeFormatter.BASIC_ISO_DATE);
        String monthAgo = LocalDate.now().minusDays(OVERDUE_REFERRAL_DAYS).format(DateTimeFormatter.BASIC_ISO_DATE);
        switch (register) {
            case APPOINTMENTS: {
                String open = blank(t + "appointment_kept");
                String date = iso(t + "appointment_date");
                String resolved = t + "tracking_outcome IN ('bbtc','traced_served','refused')";
                return Arrays.asList(
                        new Filter(R.string.srv_filter_all, ""),
                        new Filter(R.string.srv_filter_upcoming, open + " AND " + date + " >= '" + today + "'"),
                        new Filter(R.string.srv_filter_missed, "((" + open + " AND " + date + " < '" + today + "') OR ("
                                + t + "appointment_kept = 'No' AND IFNULL(" + t + "tracking_outcome,'') NOT IN ('bbtc','traced_served','refused')))"),
                        new Filter(R.string.srv_filter_completed, "(" + t + "appointment_kept = 'Yes' OR " + resolved + ")"));
            }
            case PREP: {
                String review = "(" + t + "prep_status = 'on_prep' AND IFNULL(" + t + "review_1m_completed,'') != 'Yes' AND "
                        + iso(t + "review_1m_date") + " <= '" + soon + "')";
                String reassess = "(IFNULL(" + t + "prep_status,'') NOT IN ('hiv_positive','not_at_risk','stopped','no_longer_at_risk') AND "
                        + "IFNULL(" + t + "next_reassessment_date,'') != '' AND " + iso(t + "next_reassessment_date") + " <= '" + soon + "')";
                return Arrays.asList(
                        new Filter(R.string.srv_filter_all, ""),
                        new Filter(R.string.srv_filter_on_prep, t + "prep_status = 'on_prep'"),
                        new Filter(R.string.srv_filter_review_due, "(" + review + " OR " + reassess + ")"),
                        new Filter(R.string.srv_filter_referral_pending, t + "prep_status = 'referred'"),
                        new Filter(R.string.srv_filter_deferred, t + "prep_status = 'deferred'"));
            }
            case REFERRAL_LINKAGES: {
                String open = blank(t + "service_received");
                String date = iso(t + "date_referral_received");
                return Arrays.asList(
                        new Filter(R.string.srv_filter_all, ""),
                        new Filter(R.string.srv_filter_pending, open + " AND " + date + " >= '" + monthAgo + "'"),
                        new Filter(R.string.srv_filter_overdue, "((" + open + " AND " + date + " < '" + monthAgo + "') OR "
                                + t + "service_received = 'No')"),
                        new Filter(R.string.srv_filter_received, t + "service_received = 'Yes'"));
            }
            default:
                return Collections.emptyList();
        }
    }

    /** A dd-MM-yyyy column as yyyyMMdd, so it sorts and compares as a date. */
    private static String iso(String column) {
        return "(substr(" + column + ",7,4)||substr(" + column + ",4,2)||substr(" + column + ",1,2))";
    }

    private static String blank(String column) {
        return "IFNULL(" + column + ",'') = ''";
    }
}

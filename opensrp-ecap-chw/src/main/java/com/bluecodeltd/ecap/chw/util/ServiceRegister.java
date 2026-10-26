package com.bluecodeltd.ecap.chw.util;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.bluecodeltd.ecap.chw.R;

/**
 * The client registers that share one list screen (ServiceRegisterActivity/ServiceRegisterFragment).
 * Each entry names its table, its (placeholder) enrollment form and which columns the list row shows.
 */
public enum ServiceRegister {

    APPOINTMENTS(Constants.EcapClientTable.EC_APPOINTMENT, Constants.DrawerMenu.APPOINTMENTS,
            R.string.appointments_register, R.string.appointments_register_subtitle,
            R.drawable.ic_service_calendar, R.color.register_appointments_icon, R.color.register_appointments_bg,
            "appointment_date", "Appointment", "appointment_type",
            "appointment_register", "Appointment"),

    PREP(Constants.EcapClientTable.EC_PREP, Constants.DrawerMenu.PREP,
            R.string.prep_register, R.string.prep_register_subtitle,
            R.drawable.ic_shield_cross, R.color.register_prep_icon, R.color.register_prep_bg,
            "prep_status", "PrEP Status", "next_refill_date",
            "prep_register", "PrEP"),

    REFERRAL_LINKAGES(Constants.EcapClientTable.EC_REFERRAL_LINKAGE, Constants.DrawerMenu.REFERRAL_LINKAGES,
            R.string.referral_linkages_register, R.string.referral_linkages_register_subtitle,
            R.drawable.ic_referral_link, R.color.register_linkages_icon, R.color.register_linkages_bg,
            "referral_service", "Referred For", "linkage_status",
            "referral_linkage_register", "Referral Linkage");

    public final String table;
    public final String drawerTag;
    @StringRes public final int title;
    @StringRes public final int subtitle;
    @DrawableRes public final int icon;
    @ColorRes public final int iconColor;
    @ColorRes public final int iconBackground;
    /** Column shown as "label: value" under the client's name. */
    public final String detailColumn;
    public final String detailLabel;
    /** Column appended after the detail value, e.g. the appointment type. */
    public final String secondaryColumn;
    /** JSON form in assets/json.form opened by the register's "Add" button. */
    public final String formName;
    public final String encounterType;

    ServiceRegister(String table, String drawerTag, int title, int subtitle, int icon, int iconColor,
                    int iconBackground, String detailColumn, String detailLabel, String secondaryColumn,
                    String formName, String encounterType) {
        this.table = table;
        this.drawerTag = drawerTag;
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.iconColor = iconColor;
        this.iconBackground = iconBackground;
        this.detailColumn = detailColumn;
        this.detailLabel = detailLabel;
        this.secondaryColumn = secondaryColumn;
        this.formName = formName;
        this.encounterType = encounterType;
    }

    /** Columns every register table has, so the list, search and counts work before the forms exist. */
    public static final String[] COMMON_COLUMNS = {
            "base_entity_id", "relational_id", "client_number", "first_name", "middle_name", "last_name",
            "gender", "birthdate", "phone", "address", "health_facility", "caseworker_name",
            "last_interacted_with", "date_created", "date_edited", "delete_status"
    };

    public String[] specificColumns() {
        switch (this) {
            case APPOINTMENTS:
                return new String[]{"appointment_date", "appointment_type", "appointment_status", "service_point", "comment"};
            case PREP:
                return new String[]{"prep_status", "date_initiated", "prep_regimen", "next_refill_date", "hiv_status", "date_tested", "comment"};
            case REFERRAL_LINKAGES:
                return new String[]{"date_referred", "referral_service", "receiving_facility", "linkage_status", "date_linked", "comment"};
            default:
                return new String[0];
        }
    }
}

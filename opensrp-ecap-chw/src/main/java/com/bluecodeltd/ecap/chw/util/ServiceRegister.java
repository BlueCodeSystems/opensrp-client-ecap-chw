package com.bluecodeltd.ecap.chw.util;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.bluecodeltd.ecap.chw.R;

/**
 * The client registers that share one list screen (ServiceRegisterActivity/ServiceRegisterFragment).
 * Each entry names its table, the form that adds a record, the follow-up form that updates it and the
 * columns its forms fill (see {@link ServiceRegisterFields}).
 */
public enum ServiceRegister {

    APPOINTMENTS(Constants.EcapClientTable.EC_APPOINTMENT, Constants.DrawerMenu.APPOINTMENTS,
            R.string.appointments_register, R.string.appointments_register_subtitle,
            R.drawable.ic_service_calendar, R.color.register_appointments_icon, R.color.register_appointments_bg,
            "appointment_register", "Appointment",
            "appointment_outcome", "Appointment Outcome", R.string.service_register_record_outcome),

    PREP(Constants.EcapClientTable.EC_PREP, Constants.DrawerMenu.PREP,
            R.string.prep_register, R.string.prep_register_subtitle,
            R.drawable.ic_shield_cross, R.color.register_prep_icon, R.color.register_prep_bg,
            "prep_register", "PrEP",
            "prep_follow_up", "PrEP Follow-up", R.string.service_register_review),

    REFERRAL_LINKAGES(Constants.EcapClientTable.EC_REFERRAL_LINKAGE, Constants.DrawerMenu.REFERRAL_LINKAGES,
            R.string.referral_linkages_register, R.string.referral_linkages_register_subtitle,
            R.drawable.ic_referral_link, R.color.register_linkages_icon, R.color.register_linkages_bg,
            "referral_linkage_register", "Referral Linkage",
            "referral_closure", "Referral Closure", R.string.service_register_close_referral);

    public final String table;
    public final String drawerTag;
    @StringRes public final int title;
    @StringRes public final int subtitle;
    @DrawableRes public final int icon;
    @ColorRes public final int iconColor;
    @ColorRes public final int iconBackground;
    /** JSON form in assets/json.form opened by the register's "Add" button. */
    public final String formName;
    public final String encounterType;
    /** Form that updates an existing record, e.g. the appointment's outcome. */
    public final String followUpFormName;
    public final String followUpEncounterType;
    @StringRes public final int followUpAction;

    ServiceRegister(String table, String drawerTag, int title, int subtitle, int icon, int iconColor,
                    int iconBackground, String formName, String encounterType,
                    String followUpFormName, String followUpEncounterType, int followUpAction) {
        this.table = table;
        this.drawerTag = drawerTag;
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.iconColor = iconColor;
        this.iconBackground = iconBackground;
        this.formName = formName;
        this.encounterType = encounterType;
        this.followUpFormName = followUpFormName;
        this.followUpEncounterType = followUpEncounterType;
        this.followUpAction = followUpAction;
    }

    /** Columns every register table has, so the list, search and counts work for each register. */
    public static final String[] COMMON_COLUMNS = {
            "base_entity_id", "relational_id", "client_number", "first_name", "middle_name", "last_name",
            "gender", "birthdate", "phone", "address", "health_facility", "caseworker_name",
            "last_interacted_with", "date_created", "date_edited", "delete_status"
    };

    public String[] specificColumns() {
        switch (this) {
            case APPOINTMENTS:
                return ServiceRegisterFields.APPOINTMENT_COLUMNS;
            case PREP:
                return ServiceRegisterFields.PREP_COLUMNS;
            case REFERRAL_LINKAGES:
                return ServiceRegisterFields.REFERRAL_COLUMNS;
            default:
                return new String[0];
        }
    }

    /** The fields the details sheet lists, as column then label. */
    public String[][] details() {
        switch (this) {
            case APPOINTMENTS:
                return ServiceRegisterFields.APPOINTMENT_DETAILS;
            case PREP:
                return ServiceRegisterFields.PREP_DETAILS;
            case REFERRAL_LINKAGES:
                return ServiceRegisterFields.REFERRAL_DETAILS;
            default:
                return new String[0][];
        }
    }

    public boolean handles(String encounter) {
        return encounterType.equalsIgnoreCase(encounter) || followUpEncounterType.equalsIgnoreCase(encounter);
    }
}

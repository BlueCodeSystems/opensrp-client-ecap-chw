package com.bluecodeltd.ecap.chw.util;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.bluecodeltd.ecap.chw.R;

/**
 * The subpopulation flags on ec_client_index. IndexPersonDao counts every entry and the dashboard
 * charts one column per entry with {@link #onDashboard} set, so a new subpopulation needs only a new
 * entry here (its column must exist on ec_client_index) and its description string. Each entry keeps
 * its own colour, so adding or hiding a group never repaints the others; pick the next unused
 * dash_cat_* colour, and re-run the palette check if you add more than these six.
 */
public enum Subpopulation {

    CALHIV("subpop1", "CALHIV", R.string.dash_subpop_calhiv_desc, R.color.dash_cat_blue, true),
    HEI("subpop2", "HEI", R.string.dash_subpop_hei_desc, R.color.dash_cat_orange, true),
    CWLHIV("subpop3", "CWLHIV", R.string.dash_subpop_cwlhiv_desc, R.color.dash_cat_aqua, true),
    AGYW("subpop4", "AGYW", R.string.dash_subpop_agyw_desc, R.color.dash_cat_violet, false),
    CASSV("subpop5", "C/ASSV", R.string.dash_subpop_cassv_desc, R.color.dash_cat_yellow, true),
    FSW("subpop", "FSW", 0, R.color.dash_cat_magenta, false);

    /** Column on ec_client_index holding 'true' for clients in this subpopulation. */
    public final String column;
    public final String shortName;
    /** Full name shown under the short name, or 0 for none. */
    @StringRes public final int description;
    /** Column colour on the dashboard chart. */
    @ColorRes public final int color;
    public final boolean onDashboard;

    Subpopulation(String column, String shortName, @StringRes int description, @ColorRes int color, boolean onDashboard) {
        this.column = column;
        this.shortName = shortName;
        this.description = description;
        this.color = color;
        this.onDashboard = onDashboard;
    }
}

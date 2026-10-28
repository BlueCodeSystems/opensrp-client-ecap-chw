package com.bluecodeltd.ecap.chw.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bluecodeltd.ecap.chw.R;

/**
 * Profile tabs on a white strip under the coloured header: titles in grey, the selected one dark, and the
 * record counts as soft grey badges. The tab title layouts are shared with profiles whose tabs stay on colour,
 * so each white-strip profile restyles its custom tab views with this rather than the layouts changing.
 */
public final class ProfileTabs {

    private ProfileTabs() {
    }

    /** Restyles a custom tab view (title, then count) for the white strip and returns it. */
    public static View onWhite(Context context, View tabView) {
        if (!(tabView instanceof ViewGroup)) return tabView;
        ViewGroup group = (ViewGroup) tabView;
        ColorStateList titleColours = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_selected}, new int[]{}},
                new int[]{ContextCompat.getColor(context, R.color.dash_text),
                        ContextCompat.getColor(context, R.color.dash_text_secondary)});
        boolean title = true;
        for (int i = 0; i < group.getChildCount(); i++) {
            if (!(group.getChildAt(i) instanceof TextView)) continue;
            TextView text = (TextView) group.getChildAt(i);
            if (title) {
                text.setTextColor(titleColours);
                text.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                title = false;
            } else {
                text.setBackgroundResource(R.drawable.profile_tab_badge);
                text.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 11);
                text.setTypeface(Typeface.DEFAULT_BOLD);
                text.setTextColor(ContextCompat.getColor(context, R.color.dash_text_secondary));
            }
        }
        return tabView;
    }

    /** The profile's own colour on the status bar (a shade darker) and the selected tab's underline. */
    public static void headerColour(android.app.Activity activity, com.google.android.material.tabs.TabLayout tabs, int colourRes) {
        int colour = ContextCompat.getColor(activity, colourRes);
        activity.getWindow().setStatusBarColor(androidx.core.graphics.ColorUtils.blendARGB(colour, android.graphics.Color.BLACK, 0.25f));
        if (tabs != null) tabs.setSelectedTabIndicatorColor(colour);
    }
}

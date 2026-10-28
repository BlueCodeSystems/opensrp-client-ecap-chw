package com.bluecodeltd.ecap.chw.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.view_holder.RegisterRow;

import java.util.Locale;

/**
 * Tidies the child profile overview once its values are in: missing values read as a muted dash, dates as
 * "5 Aug 2026" and lower-case words start with a capital. Rows are found by the "overview_row" tag the
 * OverviewRow style sets, so the many places that fill them need not change.
 */
final class OverviewFormat {

    static final String ROW_TAG = "overview_row";
    private static final String DASH = "—";

    private OverviewFormat() {
    }

    static void tidy(View root, ViewGroup subPopulations) {
        tidyRows(root);
        if (subPopulations != null) {
            // A section with no sub-population chips is just a header, so it hides
            View card = (View) subPopulations.getParent().getParent().getParent();
            card.setVisibility(hasVisibleChild(subPopulations) ? View.VISIBLE : View.GONE);
        }
        tidyDividers(root);
        hideEmptyCards(root);
    }

    /**
     * Shows a row divider only between two visible rows; the dividers around hidden rows (such as the
     * new-caregiver block) would otherwise stack into a thick grey bar.
     */
    private static void tidyDividers(View view) {
        if (!(view instanceof ViewGroup)) return;
        ViewGroup group = (ViewGroup) view;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (isDivider(child)) {
                child.setVisibility(visibleRowBefore(group, i) && visibleRowAfter(group, i) ? View.VISIBLE : View.GONE);
            } else {
                tidyDividers(child);
            }
        }
    }

    private static boolean isDivider(View view) {
        return view.getClass() == View.class && view.getLayoutParams() != null
                && view.getLayoutParams().height > 0 && view.getLayoutParams().height <= 3 * view.getResources().getDisplayMetrics().density;
    }

    private static boolean visibleRowBefore(ViewGroup group, int index) {
        for (int i = index - 1; i >= 0; i--) {
            View view = group.getChildAt(i);
            if (isDivider(view)) return false;
            if (view.getVisibility() == View.VISIBLE) return ROW_TAG.equals(view.getTag());
        }
        return false;
    }

    private static boolean visibleRowAfter(ViewGroup group, int index) {
        for (int i = index + 1; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (isDivider(view)) continue;
            if (view.getVisibility() == View.VISIBLE) return ROW_TAG.equals(view.getTag());
        }
        return false;
    }

    /** A section card whose content is all hidden would show as a thin empty line, so it hides too. */
    private static void hideEmptyCards(View view) {
        if (view instanceof androidx.cardview.widget.CardView) {
            if (!hasVisibleText(view)) view.setVisibility(View.GONE);
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) hideEmptyCards(group.getChildAt(i));
        }
    }

    private static boolean hasVisibleText(View view) {
        if (view.getVisibility() != View.VISIBLE) return false;
        if (view instanceof TextView) return ((TextView) view).getText().length() > 0;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                if (hasVisibleText(group.getChildAt(i))) return true;
            }
        }
        return false;
    }

    private static void tidyRows(View view) {
        if (ROW_TAG.equals(view.getTag()) && view instanceof ViewGroup && ((ViewGroup) view).getChildCount() >= 2
                && ((ViewGroup) view).getChildAt(1) instanceof TextView) {
            tidyValue((TextView) ((ViewGroup) view).getChildAt(1));
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) tidyRows(group.getChildAt(i));
        }
    }

    private static void tidyValue(TextView value) {
        String text = value.getText() == null ? "" : value.getText().toString().trim();
        String lower = text.toLowerCase(Locale.ENGLISH);
        boolean missing = text.isEmpty() || lower.equals("n/a") || lower.equals("not set") || lower.equals("null")
                || lower.equals(DASH);
        if (missing) {
            value.setText(DASH);
            value.setTextColor(ContextCompat.getColor(value.getContext(), R.color.dash_text_muted));
            return;
        }
        value.setTextColor(ContextCompat.getColor(value.getContext(), R.color.dash_text));
        if (RegisterRow.date(text) != null) {
            value.setText(RegisterRow.shownDate(text));
        } else if (lower.equals("yes") || lower.equals("no")) {
            value.setText(Character.toUpperCase(lower.charAt(0)) + lower.substring(1));
        } else if (Character.isLowerCase(text.charAt(0)) && !text.contains("@")) {
            value.setText(Character.toUpperCase(text.charAt(0)) + text.substring(1));
        }
    }

    private static boolean hasVisibleChild(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i).getVisibility() == View.VISIBLE) return true;
        }
        return false;
    }
}

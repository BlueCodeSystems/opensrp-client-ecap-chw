package com.bluecodeltd.ecap.chw.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bluecodeltd.ecap.chw.R;

/**
 * The header every register shares: a medium-weight title on the green bar and a rounded search field with
 * a hint, so the registers read as one app whichever fragment draws them. Call from setupViews, after the
 * fragment's own search bar setup.
 */
public final class RegisterHeader {

    private RegisterHeader() {
    }

    public static void style(View root, EditText search) {
        if (root == null) return;
        Context context = root.getContext();

        TextView title = root.findViewById(org.smartregister.R.id.txt_title_label);
        if (title != null) {
            title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }

        View bar = root.findViewById(R.id.search_bar_layout);
        if (bar != null) {
            bar.setBackgroundResource(R.color.primary);
            ViewGroup.LayoutParams params = bar.getLayoutParams();
            if (params != null) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                bar.setLayoutParams(params);
            }
            bar.setPadding(dp(context, 4), dp(context, 2), dp(context, 4), dp(context, 12));
        }

        if (search == null) return;
        search.setBackgroundResource(R.drawable.register_search_field);
        search.setHint(R.string.register_search_hint);
        search.setHintTextColor(ContextCompat.getColor(context, R.color.dash_text_muted));
        search.setTextColor(ContextCompat.getColor(context, R.color.dash_text));
        search.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        search.setSingleLine(true);
        search.setPadding(dp(context, 14), 0, dp(context, 14), 0);
        search.setCompoundDrawablePadding(dp(context, 10));
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_register_search);
        if (icon != null) {
            icon = DrawableCompat.wrap(icon.mutate());
            DrawableCompat.setTintList(icon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dash_text_muted)));
            search.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
        }
        View box = (View) search.getParent();
        if (box != null && box.getLayoutParams() != null) {
            box.getLayoutParams().height = dp(context, 46);
            box.setLayoutParams(box.getLayoutParams());
        }
    }

    private static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}

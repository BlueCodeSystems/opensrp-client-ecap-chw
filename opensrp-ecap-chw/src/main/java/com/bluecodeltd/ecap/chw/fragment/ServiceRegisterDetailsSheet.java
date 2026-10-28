package com.bluecodeltd.ecap.chw.fragment;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.util.ServiceRegister;
import com.bluecodeltd.ecap.chw.util.ServiceRegisterStatus;
import com.bluecodeltd.ecap.chw.util.ServiceRegisterValues;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDate;
import java.util.Map;

/** Bottom sheet listing what was recorded for one service register record, with its follow-up action. */
public final class ServiceRegisterDetailsSheet {

    private static final float MAX_LIST_SHARE = 0.55f;

    private ServiceRegisterDetailsSheet() {
    }

    public static void show(Context context, ServiceRegister register, Map<String, String> columns, Runnable followUp) {
        BottomSheetDialog dialog = new BottomSheetDialog(context,
                com.google.android.material.R.style.Theme_MaterialComponents_Light_BottomSheetDialog);
        View sheet = LayoutInflater.from(context).inflate(R.layout.service_register_details_sheet, null, false);
        ServiceRegisterStatus.Row row = ServiceRegisterStatus.row(context, register, columns);

        String name = (ServiceRegisterValues.get(columns, "first_name") + " "
                + ServiceRegisterValues.get(columns, "last_name")).trim();
        ((TextView) sheet.findViewById(R.id.service_details_name)).setText(name);

        String iccmId = ServiceRegisterValues.get(columns, "iccm_id");
        String summary = ServiceRegisterStatus.summary(columns);
        String id = iccmId.isEmpty() ? context.getString(R.string.service_register_not_enrolled)
                : context.getString(R.string.service_register_iccm_id, iccmId);
        ((TextView) sheet.findViewById(R.id.service_details_summary))
                .setText(summary.isEmpty() ? id : id + " · " + summary);

        pill(sheet.findViewById(R.id.service_details_status), row.status, row.tone);
        pill(sheet.findViewById(R.id.service_details_due), row.due, row.dueTone);

        TextView updated = sheet.findViewById(R.id.service_details_updated);
        LocalDate edited = ServiceRegisterValues.date(columns, "date_edited");
        if (edited == null) edited = ServiceRegisterValues.date(columns, "date_created");
        updated.setVisibility(edited == null ? View.GONE : View.VISIBLE);
        if (edited != null) updated.setText(context.getString(R.string.service_register_history, ServiceRegisterValues.shown(edited)));

        LinearLayout rows = sheet.findViewById(R.id.service_details_rows);
        int shown = 0;
        for (String[] detail : register.details()) {
            String value = ServiceRegisterValues.display(detail[0], ServiceRegisterValues.get(columns, detail[0]));
            if (value.isEmpty()) continue;
            rows.addView(detailRow(context, detail[1], value, shown > 0));
            shown++;
        }
        if (shown == 0) {
            rows.addView(detailRow(context, "", context.getString(R.string.service_register_nothing_recorded), false));
        }
        capHeight(context, sheet.findViewById(R.id.service_details_scroll), rows);

        sheet.findViewById(R.id.service_details_close).setOnClickListener(v -> dialog.dismiss());
        TextView action = sheet.findViewById(R.id.service_details_action);
        action.setText(register.followUpAction);
        action.setVisibility(row.open ? View.VISIBLE : View.GONE);
        action.setOnClickListener(v -> {
            dialog.dismiss();
            followUp.run();
        });

        dialog.setContentView(sheet);
        dialog.setOnShowListener(d -> {
            View container = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (container != null) {
                container.setBackgroundColor(Color.TRANSPARENT);
                BottomSheetBehavior.from(container).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        dialog.show();
    }

    private static View detailRow(Context context, String label, String value, boolean divider) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(context, 10);
        row.setPadding(0, padding, 0, padding);
        if (divider) {
            View line = new View(context);
            line.setBackgroundColor(ContextCompat.getColor(context, R.color.dash_divider));
            row.addView(line, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 1)));
            row.setPadding(0, 0, 0, padding);
        }
        if (!label.isEmpty()) {
            TextView labelView = new TextView(context);
            labelView.setText(label);
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            labelView.setTextColor(ContextCompat.getColor(context, R.color.dash_text_muted));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = divider ? padding : 0;
            row.addView(labelView, params);
        }
        TextView valueView = new TextView(context);
        valueView.setText(value);
        valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        valueView.setTextColor(ContextCompat.getColor(context, R.color.dash_text));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(context, 2);
        row.addView(valueView, params);
        return row;
    }

    /** Keeps a long record from pushing the buttons off screen: the list scrolls past about half the screen. */
    private static void capHeight(Context context, NestedScrollView scroll, View content) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        content.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int max = (int) (context.getResources().getDisplayMetrics().heightPixels * MAX_LIST_SHARE);
        if (content.getMeasuredHeight() > max) {
            ViewGroup.LayoutParams params = scroll.getLayoutParams();
            params.height = max;
            scroll.setLayoutParams(params);
        }
    }

    private static void pill(TextView view, String text, ServiceRegisterStatus.Tone tone) {
        view.setText(text);
        view.setVisibility(text == null || text.isEmpty() ? View.GONE : View.VISIBLE);
        view.setBackgroundResource(tone.pill);
        view.setTextColor(ContextCompat.getColor(view.getContext(), tone.text));
    }

    private static int dp(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}

package com.bluecodeltd.ecap.chw.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.ReportRegisterActivity;
import com.bluecodeltd.ecap.chw.activity.ReportSubmissionListActivity;
import com.bluecodeltd.ecap.chw.model.MonthlyReportModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportSubmissionAdapter extends RecyclerView.Adapter<ReportSubmissionAdapter.ViewHolder> {

    public interface Listener {
        void onView(MonthlyReportModel item);
        void onEdit(MonthlyReportModel item);
        void onDelete(MonthlyReportModel item);
    }

    private final Context context;
    private final List<MonthlyReportModel> items;
    private final Listener listener;
    private final String reportType;
    private final boolean weeklyReportLayout;

    public ReportSubmissionAdapter(Context context, List<MonthlyReportModel> items, Listener listener) {
        this(context, items, null, listener);
    }

    /**
     * @param reportType one of {@link ReportRegisterActivity}'s REPORT_TYPE_* constants -- drives
     *                   which card layout is used (the weekly stat-chip card for Community Alert,
     *                   the generic one-report-per-row card for everything else) and, for the
     *                   generic card, which icon/accent color represents that report type.
     */
    public ReportSubmissionAdapter(Context context, List<MonthlyReportModel> items, String reportType, Listener listener) {
        this.context = context;
        this.items = items;
        this.reportType = reportType;
        this.weeklyReportLayout = ReportRegisterActivity.REPORT_TYPE_COMMUNITY_ALERT.equals(reportType)
                || ReportRegisterActivity.REPORT_TYPE_COMMUNITY.equals(reportType);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = weeklyReportLayout ? R.layout.report_submission_item_cbs : R.layout.report_submission_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonthlyReportModel item = items.get(position);

        if (weeklyReportLayout) {
            String weekLabel = item.getAdditionalField(ReportSubmissionListActivity.ADDITIONAL_FIELD_WEEK_LABEL);
            setText(holder.title, weekLabel != null && !weekLabel.trim().isEmpty() ? weekLabel : emptyDash(item.getReporting_month()));

            int reportCount = parseIntSafe(item.getAdditionalField(ReportSubmissionListActivity.ADDITIONAL_FIELD_REPORT_COUNT));
            setText(holder.formId, context.getString(R.string.report_submission_week_report_count, reportCount));

            setText(holder.statAffected, String.valueOf(parseIntSafe(item.getAdditionalField(ReportSubmissionListActivity.ADDITIONAL_FIELD_TOTAL_AFFECTED))));
            setText(holder.statDeaths, String.valueOf(parseIntSafe(item.getAdditionalField(ReportSubmissionListActivity.ADDITIONAL_FIELD_TOTAL_DEATHS))));
            setText(holder.statSuspected, String.valueOf(parseIntSafe(item.getAdditionalField(ReportSubmissionListActivity.ADDITIONAL_FIELD_TOTAL_SUSPECTED))));
        } else {
            setText(holder.title, emptyDash(item.getReporting_month()));
            setText(holder.formId, context.getString(R.string.report_submission_form_id_prefix, emptyDash(item.getForm_id())));
            applyReportTypeStyle(holder);

            if (holder.edit != null) {
                holder.edit.setVisibility(View.VISIBLE);
                holder.edit.setOnClickListener(v -> listener.onEdit(item));
            }
            if (holder.delete != null) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(v -> showDeleteConfirm(item));
            }
        }

        setText(holder.location, buildLocationText(item));
        setText(holder.lastUpdated, buildLastUpdatedText(item.getLast_interacted_with()));
        holder.itemView.setOnClickListener(v -> listener.onView(item));
        if (holder.card != null) {
            holder.card.setOnClickListener(v -> listener.onView(item));
        }
    }

    /**
     * Gives each report type its own icon/accent color on the generic card -- the same palette
     * already used for these types on the Reports home grid -- instead of every type sharing one
     * generic blue "folder" look.
     */
    private void applyReportTypeStyle(ViewHolder holder) {
        int iconRes = R.drawable.ic_report_folder_24;
        int colorRes = R.color.register_report_icon;
        int bgColorRes = R.color.register_report_bg;

        if (ReportRegisterActivity.REPORT_TYPE_MALARIA.equals(reportType)) {
            iconRes = R.drawable.ic_malaria_24;
            colorRes = R.color.register_malaria_icon;
            bgColorRes = R.color.register_malaria_bg;
        } else if (ReportRegisterActivity.REPORT_TYPE_TB.equals(reportType)) {
            iconRes = R.drawable.ic_tb_24;
            colorRes = R.color.register_tb_icon;
            bgColorRes = R.color.register_tb_bg;
        } else if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(reportType)) {
            iconRes = R.drawable.ic_nutrition_24;
            colorRes = R.color.register_nutrition_icon;
            bgColorRes = R.color.register_nutrition_bg;
        }

        int color = ContextCompat.getColor(context, colorRes);
        int bgColor = ContextCompat.getColor(context, bgColorRes);

        if (holder.accent != null) {
            holder.accent.setBackgroundColor(color);
        }
        if (holder.icon != null) {
            holder.icon.setImageResource(iconRes);
            holder.icon.setColorFilter(color);
            Drawable badgeBg = ContextCompat.getDrawable(context, R.drawable.bg_report_icon_circle);
            if (badgeBg != null) {
                badgeBg = badgeBg.mutate();
                DrawableCompat.setTint(badgeBg, bgColor);
                holder.icon.setBackground(badgeBg);
            }
        }
    }

    private void setText(TextView view, String value) {
        if (view != null) {
            view.setText(value);
        }
    }

    private int parseIntSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void showDeleteConfirm(MonthlyReportModel item) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.delete)
                .setMessage(context.getString(R.string.delete_report_prompt))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> listener.onDelete(item))
                .show();
    }

    private String buildLocationText(MonthlyReportModel item) {
        StringBuilder builder = new StringBuilder();
        appendPart(builder, item.getProvince());
        appendPart(builder, item.getDistrict());
        appendPart(builder, item.getWard());
        appendPart(builder, item.getFacility());
        appendPart(builder, item.getPartner());
        return builder.length() == 0 ? context.getString(R.string.not_available) : builder.toString();
    }

    private void appendPart(StringBuilder builder, String part) {
        if (part == null || part.trim().isEmpty()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(" | ");
        }
        builder.append(part.trim());
    }

    private String emptyDash(String value) {
        return value == null || value.trim().isEmpty() ? context.getString(R.string.not_available) : value;
    }

    private String buildLastUpdatedText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return context.getString(R.string.report_submission_last_updated_not_available);
        }
        String trimmed = value.trim();
        try {
            long timestamp = Long.parseLong(trimmed);
            if (timestamp < 1000000000000L) {
                timestamp *= 1000L;
            }
            String formatted = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date(timestamp));
            return context.getString(R.string.report_submission_last_updated_prefix, formatted);
        } catch (NumberFormatException e) {
            return context.getString(R.string.report_submission_last_updated_prefix, trimmed);
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView card;
        private final TextView title;
        private final TextView formId;
        private final TextView location;
        private final TextView lastUpdated;
        private final ImageButton edit;
        private final ImageButton delete;
        private final TextView statAffected;
        private final TextView statDeaths;
        private final TextView statSuspected;
        private final View accent;
        private final ImageView icon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.report_submission_card);
            title = itemView.findViewById(R.id.report_submission_title);
            formId = itemView.findViewById(R.id.report_submission_form_id);
            location = itemView.findViewById(R.id.report_submission_location);
            lastUpdated = itemView.findViewById(R.id.report_submission_last_updated);
            edit = itemView.findViewById(R.id.report_submission_edit);
            delete = itemView.findViewById(R.id.report_submission_delete);
            statAffected = itemView.findViewById(R.id.report_submission_total_affected);
            statDeaths = itemView.findViewById(R.id.report_submission_total_deaths);
            statSuspected = itemView.findViewById(R.id.report_submission_total_suspected);
            accent = itemView.findViewById(R.id.report_submission_accent);
            icon = itemView.findViewById(R.id.report_submission_icon);
        }
    }
}

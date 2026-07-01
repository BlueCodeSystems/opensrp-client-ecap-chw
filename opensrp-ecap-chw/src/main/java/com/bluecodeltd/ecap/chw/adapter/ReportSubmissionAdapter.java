package com.bluecodeltd.ecap.chw.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.MonthlyReportModel;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public ReportSubmissionAdapter(Context context, List<MonthlyReportModel> items, Listener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_submission_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonthlyReportModel item = items.get(position);
        holder.title.setText(emptyDash(item.getReporting_month()));
        holder.formId.setText(emptyDash(item.getForm_id()));
        holder.location.setText(buildLocationText(item));
        holder.lastUpdated.setText(buildLastUpdatedText(item.getLast_interacted_with()));
        holder.itemView.setOnClickListener(v -> listener.onView(item));
        holder.card.setOnClickListener(v -> listener.onView(item));
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> showDeleteConfirm(item));
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
            builder.append("  •  ");
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

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.report_submission_card);
            title = itemView.findViewById(R.id.report_submission_title);
            formId = itemView.findViewById(R.id.report_submission_form_id);
            location = itemView.findViewById(R.id.report_submission_location);
            lastUpdated = itemView.findViewById(R.id.report_submission_last_updated);
            edit = itemView.findViewById(R.id.report_submission_edit);
            delete = itemView.findViewById(R.id.report_submission_delete);
        }
    }
}

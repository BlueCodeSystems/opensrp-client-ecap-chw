package com.bluecodeltd.ecap.chw.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.ReportRegisterActivity;
import com.bluecodeltd.ecap.chw.domain.ReportType;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportFormOptionAdapter extends RecyclerView.Adapter<ReportFormOptionAdapter.ReportFormOptionViewHolder> {

    public interface Listener {
        void onClick(ReportType reportType);
    }

    private final List<ReportType> items;
    private final Listener listener;

    public ReportFormOptionAdapter(List<ReportType> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportFormOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_form_option_item, parent, false);
        return new ReportFormOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportFormOptionViewHolder holder, int position) {
        ReportType item = items.get(position);
        holder.titleView.setText(item.getName());
        holder.descriptionView.setText(getDescription(holder.itemView, item));
        holder.countView.setText(String.valueOf(item.getCount()));
        holder.lastSubmittedView.setText(buildLastSubmittedText(holder.itemView, item.getLastSubmitted()));
        bindVisuals(holder, item);
        holder.container.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    private String getDescription(View view, ReportType item) {
        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(item.getID())) {
            return view.getContext().getString(R.string.report_nutrition_description);
        }
        if (ReportRegisterActivity.REPORT_TYPE_TB.equals(item.getID())) {
            return view.getContext().getString(R.string.report_tb_description);
        }
        return view.getContext().getString(R.string.report_malaria_description);
    }

    private void bindVisuals(ReportFormOptionViewHolder holder, ReportType item) {
        int cardColor;
        int accentColor;
        int bubbleColor;
        int iconColor;
        int iconRes;

        if (ReportRegisterActivity.REPORT_TYPE_NUTRITION.equals(item.getID())) {
            cardColor = R.color.register_nutrition_bg;
            accentColor = R.color.register_nutrition_icon;
            bubbleColor = R.color.register_nutrition_bg;
            iconColor = R.color.register_nutrition_icon;
            iconRes = R.drawable.ic_nutrition_24;
        } else if (ReportRegisterActivity.REPORT_TYPE_TB.equals(item.getID())) {
            cardColor = R.color.register_tb_bg;
            accentColor = R.color.register_tb_icon;
            bubbleColor = R.color.register_tb_bg;
            iconColor = R.color.register_tb_icon;
            iconRes = R.drawable.ic_tb_24;
        } else if (ReportRegisterActivity.REPORT_TYPE_COMMUNITY_ALERT.equals(item.getID())) {
            cardColor = R.color.register_community_alert_bg;
            accentColor = R.color.register_community_alert_icon;
            bubbleColor = R.color.register_community_alert_bg;
            iconColor = R.color.register_community_alert_icon;
            iconRes = R.drawable.ic_notification;
        } else {
            cardColor = R.color.register_malaria_bg;
            accentColor = R.color.register_malaria_icon;
            bubbleColor = R.color.register_malaria_bg;
            iconColor = R.color.register_malaria_icon;
            iconRes = R.drawable.ic_malaria_24;
        }

        holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), cardColor));
        holder.iconBubble.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), bubbleColor));
        holder.iconView.setImageResource(iconRes);
        holder.iconView.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), iconColor));
        holder.accentView.setImageResource(R.drawable.ic_report_folder_24);
        holder.accentView.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), accentColor));
        holder.countView.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), accentColor));
    }

    private String buildLastSubmittedText(View view, String lastSubmitted) {
        if (lastSubmitted == null || lastSubmitted.trim().isEmpty()) {
            return view.getContext().getString(R.string.report_last_submitted_not_available);
        }
        String value = lastSubmitted.trim();
        try {
            long timestamp = Long.parseLong(value);
            if (timestamp < 1000000000000L) {
                timestamp *= 1000L;
            }
            String formatted = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date(timestamp));
            return view.getContext().getString(R.string.report_last_submitted_prefix, formatted);
        } catch (NumberFormatException e) {
            return view.getContext().getString(R.string.report_last_submitted_prefix, value);
        }
    }

    static class ReportFormOptionViewHolder extends RecyclerView.ViewHolder {
        private final CardView container;
        private final ImageView accentView;
        private final FrameLayout iconBubble;
        private final ImageView iconView;
        private final TextView countView;
        private final TextView titleView;
        private final TextView descriptionView;
        private final TextView lastSubmittedView;

        ReportFormOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.report_form_card);
            accentView = itemView.findViewById(R.id.report_form_accent);
            iconBubble = itemView.findViewById(R.id.report_form_icon_bubble);
            iconView = itemView.findViewById(R.id.report_form_icon);
            countView = itemView.findViewById(R.id.report_form_count);
            titleView = itemView.findViewById(R.id.report_form_title);
            descriptionView = itemView.findViewById(R.id.report_form_description);
            lastSubmittedView = itemView.findViewById(R.id.report_form_last_submitted);
        }
    }
}

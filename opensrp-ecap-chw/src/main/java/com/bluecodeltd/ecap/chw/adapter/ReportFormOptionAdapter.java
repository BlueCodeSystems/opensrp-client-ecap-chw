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
        } else {
            cardColor = R.color.register_malaria_bg;
            accentColor = R.color.register_malaria_icon;
            bubbleColor = R.color.register_malaria_bg;
            iconColor = R.color.register_malaria_icon;
            iconRes = R.mipmap.sidemenu_malaria;
        }

        holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), cardColor));
        holder.iconBubble.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), bubbleColor));
        holder.iconView.setImageResource(iconRes);
        holder.iconView.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), iconColor));
        holder.accentView.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), accentColor));
    }

    static class ReportFormOptionViewHolder extends RecyclerView.ViewHolder {
        private final CardView container;
        private final View accentView;
        private final FrameLayout iconBubble;
        private final ImageView iconView;
        private final TextView titleView;
        private final TextView descriptionView;

        ReportFormOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.report_form_card);
            accentView = itemView.findViewById(R.id.report_form_accent);
            iconBubble = itemView.findViewById(R.id.report_form_icon_bubble);
            iconView = itemView.findViewById(R.id.report_form_icon);
            titleView = itemView.findViewById(R.id.report_form_title);
            descriptionView = itemView.findViewById(R.id.report_form_description);
        }
    }
}

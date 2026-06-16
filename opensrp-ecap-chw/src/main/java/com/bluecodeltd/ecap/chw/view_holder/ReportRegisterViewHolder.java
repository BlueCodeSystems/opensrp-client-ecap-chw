package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;

public class ReportRegisterViewHolder extends RecyclerView.ViewHolder {

    private final TextView titleView;
    private final TextView householdIdView;
    private final TextView summaryView;
    private final TextView dateView;

    public ReportRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.reportTitleTextView);
        householdIdView = itemView.findViewById(R.id.reportHouseholdIdTextView);
        summaryView = itemView.findViewById(R.id.reportSummaryTextView);
        dateView = itemView.findViewById(R.id.reportDateTextView);
    }

    public void setupViews(String title, String householdId, String summary, String date) {
        titleView.setText(title != null ? title : "");
        householdIdView.setText(householdId != null ? householdId : "");
        summaryView.setText(summary != null ? summary : "");
        dateView.setText(date != null ? date : "");
    }
}

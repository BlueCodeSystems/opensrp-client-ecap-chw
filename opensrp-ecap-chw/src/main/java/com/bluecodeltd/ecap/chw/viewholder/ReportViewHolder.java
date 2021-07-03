package com.bluecodeltd.ecap.chw.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.contract.ListContract;
import com.bluecodeltd.ecap.chw.domain.ReportType;

public class ReportViewHolder extends ListableViewHolder<ReportType> {

    private TextView tvName;
    private View currentView;

    public ReportViewHolder(@NonNull View itemView) {
        super(itemView);
        currentView = itemView;
        tvName = itemView.findViewById(R.id.tvName);
    }

    @Override
    public void bindView(ReportType reportType, ListContract.View<ReportType> view) {
        tvName.setText(reportType.getName());
        currentView.setOnClickListener(v -> view.onListItemClicked(reportType, v.getId()));
    }

    @Override
    public void resetView() {
        tvName.setText("");
    }
}

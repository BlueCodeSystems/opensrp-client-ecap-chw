package org.smartregister.chw.core.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.model.MonthStockUsageModel;

import java.util.List;

public class CoreStockMonthlyReportAdapter extends BaseAdapter implements SpinnerAdapter {
    private List<MonthStockUsageModel> monthStockUsageReport;
    private Context context;

    public CoreStockMonthlyReportAdapter(List<MonthStockUsageModel> monthStockUsageReport, Context context) {
        this.monthStockUsageReport = monthStockUsageReport;
        this.context = context;
    }

    @Override
    public int getCount() {
        return monthStockUsageReport.size();
    }

    @Override
    public Object getItem(int position) {
        return monthStockUsageReport.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView.inflate(context, R.layout.stock_usage_month_report_item, null);

        MonthStockUsageModel monthStockUsageModel = (MonthStockUsageModel) getItem(position);
        TextView tvMonth = view.findViewById(R.id.stock_usage_report_month);
        tvMonth.setText(String.format("%s %s", monthStockUsageModel.getMonth(), monthStockUsageModel.getYear()));

        return view;
    }

}

package org.smartregister.chw.core.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.model.StockUsageItemDetailsModel;

import java.util.List;

public class CoreStockUsageItemDetailsAdapter extends RecyclerView.Adapter<CoreStockUsageItemDetailsAdapter.CoreStockUsageReportDetailsViewHolder> {
    protected LayoutInflater inflater;
    private List<StockUsageItemDetailsModel> stockUsageItemDetailsModelList;

    public CoreStockUsageItemDetailsAdapter(List<StockUsageItemDetailsModel> stockUsageItemDetailsModelList) {
        this.stockUsageItemDetailsModelList = stockUsageItemDetailsModelList;
    }

    @NonNull
    @Override
    public CoreStockUsageItemDetailsAdapter.CoreStockUsageReportDetailsViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                                     int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.stock_usage_report_item_details, parent, false);
        return new CoreStockUsageItemDetailsAdapter.CoreStockUsageReportDetailsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CoreStockUsageReportDetailsViewHolder holder, int position) {
        StockUsageItemDetailsModel stockUsageItemDetailsModel = stockUsageItemDetailsModelList.get(position);
        holder.itemDetailsMonth.setText(stockUsageItemDetailsModel.getItemDetailsMonth());
        holder.itemDetailsYear.setText(stockUsageItemDetailsModel.getItemDetailsYear());
        holder.itemDetailsStockCount.setText(String.format("%s", stockUsageItemDetailsModel.getItemDetailsStockValue()));
    }

    @Override
    public int getItemCount() {
        return stockUsageItemDetailsModelList.size();
    }

    public static class CoreStockUsageReportDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView itemDetailsMonth;
        private TextView itemDetailsYear;
        private TextView itemDetailsStockCount;

        private CoreStockUsageReportDetailsViewHolder(View v) {
            super(v);
            itemDetailsMonth = v.findViewById(R.id.item_detail_month);
            itemDetailsYear = v.findViewById(R.id.item_detail_year);
            itemDetailsStockCount = v.findViewById(R.id.item_detail_stock_count);
        }
    }
}
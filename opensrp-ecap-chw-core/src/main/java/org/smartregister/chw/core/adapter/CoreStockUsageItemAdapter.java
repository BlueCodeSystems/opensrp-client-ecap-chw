package org.smartregister.chw.core.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.activity.CoreStockInventoryItemDetailsReportActivity;
import org.smartregister.chw.core.model.StockUsageItemModel;
import org.smartregister.chw.core.utils.CoreConstants;

import java.util.List;

public class CoreStockUsageItemAdapter extends RecyclerView.Adapter<CoreStockUsageItemAdapter.CoreStockUsageReportViewHolder> {
    protected LayoutInflater inflater;
    protected List<StockUsageItemModel> stockUsageItemModelList;
    private Context context;

    public CoreStockUsageItemAdapter(List<StockUsageItemModel> stockUsageItemModelList, Context context) {
        this.stockUsageItemModelList = stockUsageItemModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public CoreStockUsageItemAdapter.CoreStockUsageReportViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                       int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.stock_usage_report_item, parent, false);
        return new CoreStockUsageItemAdapter.CoreStockUsageReportViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CoreStockUsageReportViewHolder holder, int position) {
        StockUsageItemModel usageModelItem = stockUsageItemModelList.get(position);
        holder.stockItemName.setText(usageModelItem.getStockName());
        holder.stockItemUnitOfMeasure.setVisibility(View.VISIBLE);
        holder.stockItemUnitOfMeasure.setText(usageModelItem.getUnitsOfMeasure());
        holder.stockItemCount.setText(String.format("%s", usageModelItem.getStockValue()));
        holder.goToItemDetails.setVisibility(View.VISIBLE);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stockName = CoreConstants.HfStockUsageUtil.STOCK_NAME;
                String providerId = CoreConstants.HfStockUsageUtil.PROVIDER_NAME;
                Intent intent = new Intent(context, CoreStockInventoryItemDetailsReportActivity.class);
                intent.putExtra(stockName, usageModelItem.getStockName());
                intent.putExtra(providerId, usageModelItem.getProviderName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockUsageItemModelList.size();
    }

    public static class CoreStockUsageReportViewHolder extends RecyclerView.ViewHolder {
        private TextView stockItemName;
        private TextView stockItemUnitOfMeasure;
        private TextView stockItemCount;
        private ImageView goToItemDetails;
        protected View view;

        private CoreStockUsageReportViewHolder(View v) {
            super(v);
            view = v;
            stockItemName = v.findViewById(R.id.stock_name);
            stockItemUnitOfMeasure = v.findViewById(R.id.stock_unit_of_measure);
            stockItemCount = v.findViewById(R.id.stock_count);
            goToItemDetails = v.findViewById(R.id.go_to_item_details_image_view);
        }

    }
}
package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.WeGroupDataCollectionModel;

import java.util.List;

public class WeGroupDataCollectionAdapter extends RecyclerView.Adapter<WeGroupDataCollectionAdapter.ViewHolder> {

    private List<WeGroupDataCollectionModel> dataList;
    private Context context;

    public WeGroupDataCollectionAdapter(Context context, List<WeGroupDataCollectionModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_collection_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeGroupDataCollectionModel dataItem = dataList.get(position);

        // Bind data to ViewHolder views
        holder.reports.setText(dataItem.getDate_data_collection());
//        holder.descriptionTextView.setText(dataItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reports;
        TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            reports = itemView.findViewById(R.id.report_date);
//            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}

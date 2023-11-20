package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.WeServicesModel;

import java.util.List;

public class WeServicesAdapter extends RecyclerView.Adapter<WeServicesAdapter.ViewHolder> {

    private final Context context;
    private final List<WeServicesModel> dataSet;

    // Constructor
    public WeServicesAdapter(Context context, List<WeServicesModel> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    // ViewHolder class representing each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView serviceName;
        public TextView serviceImg;

        public ViewHolder(View view) {
            super(view);
            serviceName = view.findViewById(R.id.serviceName);
            serviceImg = view.findViewById(R.id.serviceImg);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wegroup_service_list, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeServicesModel currentItem = dataSet.get(position);

        // Set data to the views in the ViewHolder
//        holder.titleTextView.setText(currentItem.getTitle());
//        holder.descriptionTextView.setText(currentItem.getDescription());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

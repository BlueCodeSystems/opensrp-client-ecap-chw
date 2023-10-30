package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.ServicesModel;

import java.util.List;

public class ServicesModeAdapter extends RecyclerView.Adapter<ServicesModeAdapter.ServicesModelViewHolder> {
    private Context context;
    private List<ServicesModel> servicesList;

    public ServicesModeAdapter(Context context, List<ServicesModel> servicesList) {
        this.context = context;
        this.servicesList = servicesList;
    }

    @Override
    public ServicesModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ServicesModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServicesModelViewHolder holder, int position) {
        ServicesModel servicesModel = servicesList.get(position);

        holder.serviceNameTextView.setText(servicesModel.getServiceName());
        holder.userServiceTextView.setText(servicesModel.getUserService());
        holder.groupServiceTextView.setText(servicesModel.getGroupService());
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    public class ServicesModelViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView;
        TextView userServiceTextView;
        TextView groupServiceTextView;

        public ServicesModelViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.title);
            userServiceTextView = itemView.findViewById(R.id.user_service);
            groupServiceTextView = itemView.findViewById(R.id.user_group);
        }
    }
}

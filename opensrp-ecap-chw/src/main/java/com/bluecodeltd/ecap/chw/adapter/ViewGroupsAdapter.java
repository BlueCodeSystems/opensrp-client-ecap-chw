package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.WeGroupMemberProfileActivity;
import com.bluecodeltd.ecap.chw.activity.WeGroupProfileActivity;
import com.bluecodeltd.ecap.chw.dao.WeGroupDao;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.ArrayList;

public class ViewGroupsAdapter extends RecyclerView.Adapter<ViewGroupsAdapter.ViewHolder> {
    Context context;
    ArrayList<WeGroupModel> groups;
    ArrayList<WeGroupModel> filteredGroups; // New list for filtered data

    public ViewGroupsAdapter(Context context, ArrayList<WeGroupModel> facilitatorViewGroupsModels) {
        this.context = context;
        this.groups = facilitatorViewGroupsModels;
        this.filteredGroups = new ArrayList<>(facilitatorViewGroupsModels); // Initialize with the original data
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group, parent, false);
        ViewHolder bind = new ViewHolder(view);
        return bind;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final WeGroupModel model = filteredGroups.get(position); // Use filtered data

        if (model != null) {
            if (model.getGroup_name() != null) {
                holder.group_name.setText(model.getGroup_name());
            } else {
                holder.group_name.setText("Admin");
            }

            if (model.getGroup_id() != null) {
                String groupIdText = "ID: " + model.getGroup_id();
                holder.group_id.setText(groupIdText);
            } else {
                holder.group_id.setText("Group ID: ");
            }
        }

        holder.linear_group_layout.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), WeGroupProfileActivity.class);
            intent.putExtra("groupName", model.getGroup_name());
            intent.putExtra("groupId", model.getGroup_id());
            v.getContext().startActivity(intent);
            ((Activity) v.getContext()).finish();
        });
    }

    @Override
    public int getItemCount() {
        return filteredGroups.size(); // Return the count of filtered data
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView group_name, group_id;
        LinearLayout linear_group_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_name = itemView.findViewById(R.id.group_name);
            group_id = itemView.findViewById(R.id.group_id);
            linear_group_layout = itemView.findViewById(R.id.linear_group_layout);
        }
    }

    // Filter the data based on a search query
    public void filter(String query) {
        filteredGroups.clear();
        for (WeGroupModel model : groups) {
            if (model.getGroup_name() != null && model.getGroup_name().toLowerCase().contains(query.toLowerCase())) {
                filteredGroups.add(model);
            }
        }
        notifyDataSetChanged();
    }
}

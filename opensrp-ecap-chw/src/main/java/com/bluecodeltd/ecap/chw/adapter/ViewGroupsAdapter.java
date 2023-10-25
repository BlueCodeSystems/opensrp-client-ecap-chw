package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.WeGroupDao;
import com.bluecodeltd.ecap.chw.model.WeGroupModel;

import java.util.ArrayList;

public class ViewGroupsAdapter extends RecyclerView.Adapter<ViewGroupsAdapter.ViewHolder> {
    Context context;
    ArrayList<WeGroupModel> groups;


    public ViewGroupsAdapter(Context context, ArrayList<WeGroupModel> facilitatorViewGroupsModels) {
        this.context = context;
        this.groups = facilitatorViewGroupsModels;

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
        final WeGroupModel model = groups.get(position);
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
        } else {
            // Handle the case where model is null
        }
//        String dateCreatedText = "Group Number " + model.getDate_created() ;
//        holder.date_created.setText(dateCreatedText);
//        holder.linear_group_layout.setOnClickListener(v -> {
//            Intent intent = new Intent(v.getContext(), FacilitatorNewMemberActivity.class);
//            intent.putExtra("groupName", model.getGroup_name());
//            intent.putExtra("groupId", model.getGroup_id());
//            v.getContext().startActivity(intent);
//            ((Activity) v.getContext()).finish();
//        });

    }


    @Override
    public int getItemCount() {
        return groups.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        TextView group_name, group_id,date_created;
        LinearLayout linear_group_layout;
        public  ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_name = itemView.findViewById(R.id.group_name);
            group_id = itemView.findViewById(R.id.group_id);
//            date_created = itemView.findViewById(R.id.date_created);
            linear_group_layout = itemView.findViewById(R.id.linear_group_layout);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (recyclerViewInterface != null){
//
//                        int pos = getAdapterPosition();
//                        if (pos != RecyclerView.NO_POSITION){
//                            recyclerViewInterface.onItemClick(pos);
//
//                        }
//                    }
//                }
//            });

        }
    }
}

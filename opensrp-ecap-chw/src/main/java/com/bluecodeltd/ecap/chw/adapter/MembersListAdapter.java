package com.bluecodeltd.ecap.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CircularImageView;
import com.bluecodeltd.ecap.chw.activity.WeGroupMemberProfileActivity;
import com.bluecodeltd.ecap.chw.model.MembersModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.ViewHolder> {
    Context context;
    ArrayList<MembersModel> memberListModels;

    public MembersListAdapter(Context context, ArrayList<MembersModel> memberListModels) {
        this.context = context;
        this.memberListModels = memberListModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item_list,parent,false);
        ViewHolder bind = new ViewHolder(view);

        return bind;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final MembersModel model = memberListModels.get(position);
//        String memberId = model.getId().replaceAll("[^\\d]", "");
//        String memberIdFirst6 = memberId.substring(0, 6);
        holder.id.setText("ID: " + model.getUnique_id());
        holder.name.setText(model.getFirst_name()+" "+model.getLast_name());
        String gender = model.getGender();

        int drawableResource;

        if (gender != null && gender.equals("male")) {
            drawableResource = R.drawable.blue_background;
        } else {
            drawableResource = R.drawable.pink_background;
        }

        holder.tvIcon.setBackgroundResource(drawableResource);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), WeGroupMemberProfileActivity.class);
                intent.putExtra("userName", model.getFirst_name()+" "+model.getLast_name());
                intent.putExtra("userId", model.getUnique_id());
//                intent.putExtra("groupId", model.getGroup_id());
                v.getContext().startActivity(intent);
                ((Activity) v.getContext()).finish();

            }
        });
    }

    @Override
    public int getItemCount() {
        return memberListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, id;
        CircularImageView tvIcon;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.member_ID);
            name = itemView.findViewById(R.id.member_name);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}

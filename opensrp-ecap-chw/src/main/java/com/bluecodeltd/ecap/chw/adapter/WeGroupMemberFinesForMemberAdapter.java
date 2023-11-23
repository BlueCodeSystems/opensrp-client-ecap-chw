package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CircularImageView;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.model.MemberFineModel;
import com.bluecodeltd.ecap.chw.model.MembersModel;

import java.util.List;

public class WeGroupMemberFinesForMemberAdapter extends RecyclerView.Adapter<WeGroupMemberFinesForMemberAdapter.ViewHolder> {
    private final Context context;
    private final List<MemberFineModel> dataSet;

    public WeGroupMemberFinesForMemberAdapter(Context context, List<MemberFineModel> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public WeGroupMemberFinesForMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_member_service, parent, false);
        return new WeGroupMemberFinesForMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeGroupMemberFinesForMemberAdapter.ViewHolder holder, int position) {
        MemberFineModel currentItem = dataSet.get(position);

        MembersModel member = WeGroupMembersDao.getWeGroupMemberById(currentItem.getUnique_id());
        String gender = member.getGender();

        holder.memberNameTextView.setText(member.getLast_name() + " " + member.getLast_name() + ", ID: " + currentItem.getUnique_id());
        holder.dateTextView.setText(" Fine Deposited K:" + currentItem.getAmount() + " Date Fine Deposited: " + currentItem.getDate_of_fine());

        int drawableResource;

        if (gender != null && gender.equals("male")) {
            drawableResource = R.drawable.blue_background;
        } else {
            drawableResource = R.drawable.pink_background;
        }

        holder.genderIcon.setBackgroundResource(drawableResource);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberNameTextView;
        public TextView dateTextView;
        CircularImageView genderIcon;

        public ViewHolder(View view) {
            super(view);
            memberNameTextView = view.findViewById(R.id.we_member_name);
            dateTextView = view.findViewById(R.id.date);
            genderIcon = view.findViewById(R.id.genderIcon);
        }
    }
}

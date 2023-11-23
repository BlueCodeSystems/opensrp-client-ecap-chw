package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CircularImageView;
import com.bluecodeltd.ecap.chw.dao.WeGroupMembersDao;
import com.bluecodeltd.ecap.chw.model.MembersModel;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberSavings;

import java.util.List;

public class WeGroupMemberSavingsForMemberAdapter extends RecyclerView.Adapter<WeGroupMemberSavingsForMemberAdapter.ViewHolder> {

    private final Context context;
    private final List<WeGroupMemberSavings> dataSet;

    // Constructor
    public WeGroupMemberSavingsForMemberAdapter(Context context, List<WeGroupMemberSavings> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    // ViewHolder class representing each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
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

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_member_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeGroupMemberSavings currentItem = dataSet.get(position);

        MembersModel member = WeGroupMembersDao.getWeGroupMemberById(currentItem.getUnique_id());
        String gender = member.getGender();

        holder.memberNameTextView.setText(member.getLast_name() + " " + member.getLast_name() + ", ID: " + currentItem.getUnique_id());
        holder.dateTextView.setText(" Savings Deposited K:" + currentItem.getAmount() + " Date Savings Deposited: " + currentItem.getContribution_date());

        int drawableResource;

        if (gender != null && gender.equals("male")) {
            drawableResource = R.drawable.blue_background;
        } else {
            drawableResource = R.drawable.pink_background;
        }

        holder.genderIcon.setBackgroundResource(drawableResource);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}


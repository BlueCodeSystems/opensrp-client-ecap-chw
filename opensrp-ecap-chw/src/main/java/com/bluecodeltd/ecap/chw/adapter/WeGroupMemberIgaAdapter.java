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
import com.bluecodeltd.ecap.chw.model.MemberIGAModel;
import com.bluecodeltd.ecap.chw.model.MembersModel;

import java.util.List;

public class WeGroupMemberIgaAdapter extends RecyclerView.Adapter<WeGroupMemberIgaAdapter.ViewHolder> {

    private final Context context;
    private final List<MemberIGAModel> dataSet;

    public WeGroupMemberIgaAdapter(Context context, List<MemberIGAModel> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public WeGroupMemberIgaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_member_service, parent, false);
        return new WeGroupMemberIgaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeGroupMemberIgaAdapter.ViewHolder holder, int position) {
        MemberIGAModel currentItem = dataSet.get(position);

        MembersModel member = WeGroupMembersDao.getWeGroupMemberById(currentItem.getUnique_id());
        String gender = member.getGender();

        holder.memberNameTextView.setText(member.getLast_name()+" "+member.getLast_name()+", ID: "+currentItem.getUnique_id());
        holder.dateTextView.setText(" IGA Deposited K:"+currentItem.getAmount()+" Date IGA Deposited: "+currentItem.getDate_of_meeting());



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

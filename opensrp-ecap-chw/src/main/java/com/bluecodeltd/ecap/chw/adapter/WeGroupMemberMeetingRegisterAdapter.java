package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberMeetingRegisterModel;

import java.util.List;

public class WeGroupMemberMeetingRegisterAdapter extends RecyclerView.Adapter<WeGroupMemberMeetingRegisterAdapter.ViewHolder> {
    private List<WeGroupMemberMeetingRegisterModel> memberList;
    private Context context;

    public WeGroupMemberMeetingRegisterAdapter(List<WeGroupMemberMeetingRegisterModel> memberList, Context context) {
        this.memberList = memberList;
        this.context = context;
    }



    @NonNull
    @Override
    public WeGroupMemberMeetingRegisterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.we_group_member_meeting_register_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeGroupMemberMeetingRegisterAdapter.ViewHolder holder, int position) {
        WeGroupMemberMeetingRegisterModel currentItem = memberList.get(position);
        holder.tvDateRegisterConducted.setText("Meet Date: "+currentItem.getDate_register_conducted());

        String attendance = currentItem.getAttendance();

        if (attendance != null) {
            String[] attendanceArray = attendance.split(", ");
            StringBuilder formattedAttendance = new StringBuilder();
            for (int i = 0; i < attendanceArray.length; i++) {
                formattedAttendance.append(i + 1).append(". ").append(attendanceArray[i]);
                if (i < attendanceArray.length - 1) {
                    formattedAttendance.append("\n\n");
                }
            }
            holder.tvAttendance.setText(formattedAttendance.toString());
        } else {
            holder.tvAttendance.setText("No attendance recorded");
        }





        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                holder.exPandableView.setVisibility(View.VISIBLE);
                holder.expMore.setVisibility(View.GONE);
                holder.expLess.setVisibility(View.VISIBLE);
            }
        });

        holder.expMore.setOnClickListener(v -> {

            if (v.getId() == R.id.expand_more) {

                holder.exPandableView.setVisibility(View.VISIBLE);
                holder.expMore.setVisibility(View.GONE);
                holder.expLess.setVisibility(View.VISIBLE);
            }
        });

        holder.expLess.setOnClickListener(v -> {

            if (v.getId() == R.id.expand_less) {

                holder.exPandableView.setVisibility(View.GONE);
                holder.expMore.setVisibility(View.GONE);
                holder.expLess.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDateRegisterConducted;
        LinearLayout linearLayout, exPandableView;

        ImageView expMore, expLess;

        private TextView tvAttendance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateRegisterConducted = itemView.findViewById(R.id.date);
            tvAttendance = itemView.findViewById(R.id.attendance);
            expLess = itemView.findViewById(R.id.expand_less);
            expMore = itemView.findViewById(R.id.expand_more);
            linearLayout = itemView.findViewById(R.id.itemm);
            exPandableView = itemView.findViewById(R.id.expandable);
        }
    }
}

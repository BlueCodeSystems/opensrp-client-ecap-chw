package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.WeGroupSocialDiscussionsModel;

import java.util.List;

public class WeGroupSocialDiscussionsAdapter extends RecyclerView.Adapter<WeGroupSocialDiscussionsAdapter.ViewHolder> {

    private List<WeGroupSocialDiscussionsModel> dataList;
    private Context context;

    public WeGroupSocialDiscussionsAdapter(Context context, List<WeGroupSocialDiscussionsModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_discussions_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeGroupSocialDiscussionsModel model = dataList.get(position);

        holder.date.setText(model.getDate_of_meeting());
        holder.meet_number.setText(model.getMeeting_number());
        holder.meeting_agenda.setText(model.getMeeting_agenda());
        holder.moderator_name.setText(model.getName_of_moderator());
        holder.meeting_decision.setText(model.getMeeting_decisions());

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

            if (v.getId() == R.id.expand_more) {

                holder.exPandableView.setVisibility(View.GONE);
                holder.expMore.setVisibility(View.VISIBLE);
                holder.expLess.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView meeting_decision;
        TextView meeting_agenda;
        TextView moderator_name;
        TextView meet_number;

        LinearLayout linearLayout, exPandableView;

        ImageView expMore, expLess;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            meeting_decision = itemView.findViewById(R.id.meeting_decision);
            meeting_agenda = itemView.findViewById(R.id.meeting_agenda);
            moderator_name = itemView.findViewById(R.id.moderator_name);
            meet_number = itemView.findViewById(R.id.meet_number);

            expLess = itemView.findViewById(R.id.expand_less);
            expMore = itemView.findViewById(R.id.expand_more);
            linearLayout = itemView.findViewById(R.id.itemm);
            exPandableView = itemView.findViewById(R.id.expandable);
        }
        }
    }



package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.WeGroupMemberSavings;

import java.util.List;

public class WeGroupMemberSavingsAdapter extends RecyclerView.Adapter<WeGroupMemberSavingsAdapter.ViewHolder> {

    private final Context context;
    private final List<WeGroupMemberSavings> dataSet;

    // Constructor
    public WeGroupMemberSavingsAdapter(Context context, List<WeGroupMemberSavings> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    // ViewHolder class representing each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberNameTextView;
        public TextView dateTextView;

        public ViewHolder(View view) {
            super(view);
            memberNameTextView = view.findViewById(R.id.name_amount);
            dateTextView = view.findViewById(R.id.date);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_member_service, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeGroupMemberSavings currentItem = dataSet.get(position);

        // Set data to the views in the ViewHolder
        holder.memberNameTextView.setText(currentItem.getUnique_id()+" Amount K:"+currentItem.getAmount());
        holder.dateTextView.setText(currentItem.getContribution_date());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

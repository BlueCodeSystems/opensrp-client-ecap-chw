package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.model.HTSlinksModel;

import java.util.ArrayList;

public class HTSlinksAdapter extends RecyclerView.Adapter<HTSlinksAdapter.View> {
    Context context;

    ArrayList<HTSlinksModel> links;

    public HTSlinksAdapter(Context context, ArrayList<HTSlinksModel> links) {
        this.context = context;
        this.links = links;
    }

    @NonNull
    @Override
    public HTSlinksAdapter.View onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View binder = LayoutInflater.from(parent.getContext()).inflate(R.layout.links_row, parent, false);

        HTSlinksAdapter.View viewHolder = new HTSlinksAdapter.View(binder);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HTSlinksAdapter.View holder, int position) {
        final  HTSlinksModel client = links.get(position);
        holder.clientNameTextView.setText(client.getFirst_name()+" "+ client.getLast_name());
        holder.clientAgeTextView.setText(client.getBirthdate());
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    public class View extends RecyclerView.ViewHolder {
        TextView clientNameTextView, clientAgeTextView;
        public View(@NonNull android.view.View itemView) {
            super(itemView);
            clientNameTextView = itemView.findViewById(R.id.clientNameTextView);
            clientAgeTextView = itemView.findViewById(R.id.clientAgeTextView);
        }
    }
}

package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CasePlan;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VisitAdapter extends RecyclerView.Adapter<VisitAdapter.ViewHolder> {

    Context context;

    List<VcaVisitationModel> visits;

    public VisitAdapter(List<VcaVisitationModel> visits, Context context){

        super();

        this.visits = visits;
        this.context = context;
    }

    @Override
    public VisitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_visit, parent, false);

        VisitAdapter.ViewHolder viewHolder = new VisitAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VisitAdapter.ViewHolder holder, final int position) {

        final VcaVisitationModel visit = visits.get(position);

        holder.setIsRecyclable(false);

        holder.txtDate.setText(visit.getVisit_date());

        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {



            }
        });
    }

    @Override
    public int getItemCount() {

        return visits.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtDate;

        LinearLayout linearLayout;

        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtDate  = itemView.findViewById(R.id.date);

        }


        @Override
        public void onClick(View v) {

        }
    }

}

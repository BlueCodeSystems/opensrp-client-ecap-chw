package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.Child;

import java.util.List;

public class CasePlanAdapter extends RecyclerView.Adapter<CasePlanAdapter.ViewHolder>{

    Context context;

    List<CasePlanModel> caseplans;

    public CasePlanAdapter(List<CasePlanModel> caseplans, Context context){

        super();

        this.caseplans = caseplans;
        this.context = context;
    }

    @Override
    public CasePlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_plan, parent, false);

        CasePlanAdapter.ViewHolder viewHolder = new CasePlanAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CasePlanAdapter.ViewHolder holder, final int position) {

        final CasePlanModel casePlan = caseplans.get(position);


    }

    @Override
    public int getItemCount() {

        return caseplans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtCaseDate, txtQuarter, txtType, txtVulnerability,
                txtGoal, txtServices, txtServicesReferred, txtInstitution, txtDueDate, txtStatus, txtComment;
        RelativeLayout lview;

        public ViewHolder(View itemView) {

            super(itemView);


            txtCaseDate  = (TextView) itemView.findViewById(R.id.case_date);
            txtQuarter  = (TextView) itemView.findViewById(R.id.quarter);
            txtType = (TextView) itemView.findViewById(R.id.type);
            txtVulnerability = (TextView) itemView.findViewById(R.id.vulnerability);
            txtGoal = (TextView) itemView.findViewById(R.id.goal);
            txtServices = (TextView) itemView.findViewById(R.id.service);
            txtServicesReferred = (TextView) itemView.findViewById(R.id.services_referred);
            txtInstitution = (TextView) itemView.findViewById(R.id.institution);
            txtDueDate = (TextView) itemView.findViewById(R.id.due_date);
            txtStatus = (TextView) itemView.findViewById(R.id.status);
            txtComment = (TextView) itemView.findViewById(R.id.comment);

        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }
    }

}

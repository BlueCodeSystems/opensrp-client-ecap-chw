package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.CasePlan;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.Child;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CasePlanAdapter extends RecyclerView.Adapter<CasePlanAdapter.ViewHolder>{

    Context context;
    List<CasePlanModel> caseplans;
    String hivStatus;

    public CasePlanAdapter(List<CasePlanModel> caseplans, Context context, String hivStatus){

        super();

        this.caseplans = caseplans;
        this.context = context;
        this.hivStatus = hivStatus;
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

        holder.setIsRecyclable(false);

        holder.txtCaseDate.setText(casePlan.getCase_plan_date());
        holder.txtCasePlanStatus.setText(casePlan.getCase_plan_status());

        String vulnerabilities = CasePlanDao.countVulnerabilities(casePlan.getUnique_id(), casePlan.getCase_plan_date());

        holder.txtVulnerabilities.setText(vulnerabilities + " Vulnerabilities");

        try {
            Date thedate = new SimpleDateFormat("dd-MM-yyyy").parse(casePlan.getCase_plan_date());
            String month = (String) DateFormat.format("M", thedate);
            int m = Integer.parseInt(month);


          if(m >= 1 && m <= 3){
              holder.txtQuarter.setText(R.string.quarter_two);

          } else if(m > 3 && m <= 6){

              holder.txtQuarter.setText(R.string.quarter_three);

          } else if (m > 6 && m <=9 ){

              holder.txtQuarter.setText(R.string.quarter_four);

          } else if (m > 9 && m <= 12) {

              holder.txtQuarter.setText(R.string.quarter_one);

          }

        } catch (ParseException e) {
            e.printStackTrace();
        }



        holder.linearLayout.setOnClickListener(v -> {

            if (v.getId() == R.id.itemm) {

                Intent i = new Intent(context, CasePlan.class);
                i.putExtra("childId",  casePlan.getUnique_id());
                i.putExtra("dateId",  casePlan.getCase_plan_date());
                i.putExtra("hivStatus",  hivStatus);
                context.startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {

        return caseplans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtCaseDate, txtQuarter, txtCasePlanStatus, txtVulnerabilities;

        LinearLayout linearLayout;


        public ViewHolder(View itemView) {

            super(itemView);

            linearLayout = itemView.findViewById(R.id.itemm);
            txtCaseDate  = itemView.findViewById(R.id.case_date);
            txtQuarter = itemView.findViewById(R.id.quarter);
            txtCasePlanStatus = itemView.findViewById(R.id.case_plan_status);
            txtVulnerabilities = itemView.findViewById(R.id.vulnerabilities);

        }


        @Override
        public void onClick(View v) {

        }
    }

}

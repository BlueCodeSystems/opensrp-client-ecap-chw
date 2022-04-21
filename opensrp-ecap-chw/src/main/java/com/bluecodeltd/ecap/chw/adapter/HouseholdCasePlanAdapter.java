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
import com.bluecodeltd.ecap.chw.activity.HouseholdCasePlanActivity;
import com.bluecodeltd.ecap.chw.dao.CasePlanDao;
import com.bluecodeltd.ecap.chw.model.CasePlanModel;
import com.bluecodeltd.ecap.chw.model.Household;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HouseholdCasePlanAdapter extends RecyclerView.Adapter<HouseholdCasePlanAdapter.ViewHolder>{

    Context context;

    List<CasePlanModel> caseplans;
    Household house;

    public HouseholdCasePlanAdapter(List<CasePlanModel> caseplans, Context context, Household household){

        super();

        this.caseplans = caseplans;
        this.context = context;
        this.house =household;
    }

    @Override
    public HouseholdCasePlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_plan, parent, false);

        HouseholdCasePlanAdapter.ViewHolder viewHolder = new HouseholdCasePlanAdapter.ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HouseholdCasePlanAdapter.ViewHolder holder, final int position) {

        final CasePlanModel casePlan = caseplans.get(position);

        holder.setIsRecyclable(false);
        String vulnerabilities = CasePlanDao.countCaregiverVulnerabilities(casePlan.getUnique_id(),casePlan.getCase_plan_date());
        holder.txtCaseDate.setText(casePlan.getCase_plan_date());
        holder.txtCasePlanStatus.setText(casePlan.getCase_plan_status());
         if (vulnerabilities != null)
         {
             holder.txtVulnerabilities.setText(vulnerabilities + " Vulnerabilities");
         }

        try {
            Date thedate = new SimpleDateFormat("dd-MM-yyyy").parse(casePlan.getCase_plan_date());
            String month = (String) DateFormat.format("M", thedate); // 6
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

                Intent i = new Intent(context, HouseholdCasePlanActivity.class);
                i.putExtra("unique_id",  house.getUnique_id());
                i.putExtra("householdId",  house.getHousehold_id());
                i.putExtra("dateId",  casePlan.getCase_plan_date());
                context.startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {

        return caseplans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtCaseDate, txtQuarter, txtCasePlanStatus,txtVulnerabilities;

        LinearLayout linearLayout;

        ImageView expMore, expLess;

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

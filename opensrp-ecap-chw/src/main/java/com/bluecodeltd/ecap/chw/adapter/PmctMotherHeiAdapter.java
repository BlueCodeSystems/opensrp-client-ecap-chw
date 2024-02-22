package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HeiDetailsActivity;
import com.bluecodeltd.ecap.chw.model.PtmctMotherMonitoringModel;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class PmctMotherHeiAdapter extends  RecyclerView.Adapter<PmctMotherHeiAdapter.ViewHolder>{
ArrayList<PtmctMotherMonitoringModel> model;
Context context;

    public PmctMotherHeiAdapter(ArrayList<PtmctMotherMonitoringModel> model, Context context) {
        this.model = model;
        this.context = context;
    }

    @NonNull
    @Override
    public PmctMotherHeiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_child_hei, parent, false);
        PmctMotherHeiAdapter.ViewHolder  viewHolder = new PmctMotherHeiAdapter.ViewHolder (v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PmctMotherHeiAdapter.ViewHolder holder, int position) {

           final PtmctMotherMonitoringModel monitoringModel = model.get(position);
                holder.fullName.setText(monitoringModel.getInfant_first_name() + " " + monitoringModel.getInfant_lastname());
                holder.age.setText("Age : " + getAge(monitoringModel.getInfants_date_of_birth()));
                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent heiProfile = new Intent(context, HeiDetailsActivity.class);
                        heiProfile.putExtra("client_id",monitoringModel.getUnique_id());
                        context.startActivity(heiProfile);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullName,age;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.familyNameTextView);
            age = itemView.findViewById(R.id.child_age);
            relativeLayout = itemView.findViewById(R.id.register_columns);
        }
    }

    private String getAge(String birthdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            LocalDate localDateBirthdate = LocalDate.parse(birthdate, formatter);
            LocalDate today = LocalDate.now();
            Period periodBetweenDateOfBirthAndNow = Period.between(localDateBirthdate, today);
            if(periodBetweenDateOfBirthAndNow.getYears() > 0) {
                return periodBetweenDateOfBirthAndNow.getYears() +" Years";
            } else if (periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() > 0) {
                return periodBetweenDateOfBirthAndNow.getMonths() +" Months ";
            } else if(periodBetweenDateOfBirthAndNow.getYears() == 0 && periodBetweenDateOfBirthAndNow.getMonths() == 0) {
                return periodBetweenDateOfBirthAndNow.getDays() +" Days ";
            } else return "Not Set";
        } catch (DateTimeParseException e) {
            Log.e("TAG", "Invalid birthdate format: " + e.getMessage());
            return "Invalid birthdate format";
        }
    }
}

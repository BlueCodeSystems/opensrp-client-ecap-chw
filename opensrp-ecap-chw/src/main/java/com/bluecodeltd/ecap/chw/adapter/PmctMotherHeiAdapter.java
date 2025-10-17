package com.bluecodeltd.ecap.chw.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HeiDetailsActivity;
import com.bluecodeltd.ecap.chw.model.PmtctChildModel;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class PmctMotherHeiAdapter extends  RecyclerView.Adapter<PmctMotherHeiAdapter.ViewHolder>{
ArrayList<PmtctChildModel> model;
Context context;

    public PmctMotherHeiAdapter(ArrayList<PmtctChildModel> model, Context context) {
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

           final PmtctChildModel monitoringModel = model.get(position);
                holder.fullName.setText(monitoringModel.getInfant_first_name() + " " + monitoringModel.getInfant_lastname());
                holder.age.setText("Age : " + getAge(monitoringModel.getInfants_date_of_birth()));



                holder.genderIcon.setImageResource((monitoringModel.getInfants_sex() != null && monitoringModel.getInfants_sex().equals("male")) ? org.smartregister.R.drawable.child_boy_infant : org.smartregister.R.drawable.child_girl_infant);

        // Flag HEI as high risk when mother has unsuppressed VL
        boolean heiHighRisk = false;
        try {
            String pmtctId = monitoringModel.getPmtct_id();
            if (pmtctId != null && !pmtctId.trim().isEmpty()) {
                com.bluecodeltd.ecap.chw.model.PtctMotherModel mother = com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao.getPMCTMother(pmtctId);
                if (mother != null) {
                    String agywUnsupp = safe(mother.getAgyw_unsuppressed_vl_1st());
                    String unsupp = safe(mother.getUnsuppressed_vl_1st());
                    heiHighRisk = "yes".equalsIgnoreCase(agywUnsupp) || "yes".equalsIgnoreCase(unsupp);
                }
            }
        } catch (Throwable ignored) {}

        holder.heiHighRiskButton.setVisibility(heiHighRisk ? View.VISIBLE : View.GONE);
        if (!heiHighRisk) {
            holder.heiAlert.setVisibility(View.GONE);
        }
        holder.heiHighRiskButton.setOnClickListener(v -> {
            toggleAlert(holder);
            try { timber.log.Timber.i("HEI_HIGH_RISK_ALERT_VIEWED pmtct_id=%s", monitoringModel.getPmtct_id()); } catch (Throwable ignored) {}
        });
        View close = holder.itemView.findViewById(R.id.btn_close_alert);
        if (close != null) close.setOnClickListener(v -> hideAlert(holder));

        // Visual stripe: red when high risk, default otherwise
        View stripe = holder.itemView.findViewById(R.id.hei_status_stripe);
        if (stripe != null) {
            if (heiHighRisk) {
                stripe.setBackgroundColor(0xFFE53935);
            } else {
                stripe.setBackgroundResource(R.drawable.bg_status_stripe);
            }
        }

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
        ImageView genderIcon;
        RelativeLayout relativeLayout;
        View heiAlert;
        android.widget.Button heiHighRiskButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.familyNameTextView);
            age = itemView.findViewById(R.id.child_age);
            genderIcon = itemView.findViewById(R.id.gender_icon);
            relativeLayout = itemView.findViewById(R.id.register_columns);
            heiAlert = itemView.findViewById(R.id.hei_unsuppressed_alert);
            heiHighRiskButton = itemView.findViewById(R.id.hei_high_risk_flag);
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
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private void toggleAlert(ViewHolder holder) {
        if (holder.heiAlert == null) return;
        if (holder.heiAlert.getVisibility() == View.VISIBLE) {
            hideAlert(holder);
        } else {
            holder.heiAlert.clearAnimation();
            holder.heiAlert.setVisibility(View.VISIBLE);
            try {
                android.view.animation.Animation a = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.expand_in);
                holder.heiAlert.startAnimation(a);
            } catch (Throwable ignored) {}
        }
    }

    private void hideAlert(ViewHolder holder) {
        if (holder.heiAlert == null) return;
        try {
            android.view.animation.Animation a = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.collapse_out);
            a.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                @Override public void onAnimationStart(android.view.animation.Animation animation) {}
                @Override public void onAnimationEnd(android.view.animation.Animation animation) { holder.heiAlert.setVisibility(View.GONE); }
                @Override public void onAnimationRepeat(android.view.animation.Animation animation) {}
            });
            holder.heiAlert.startAnimation(a);
        } catch (Throwable e) {
            holder.heiAlert.setVisibility(View.GONE);
        }
    }
}

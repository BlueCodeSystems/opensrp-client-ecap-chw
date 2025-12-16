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
import com.bluecodeltd.ecap.chw.util.Threading;

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

        final String rowTag = monitoringModel.getBase_entity_id() != null ? monitoringModel.getBase_entity_id()
                : (monitoringModel.getUnique_id() != null ? monitoringModel.getUnique_id() : String.valueOf(position));
        holder.itemView.setTag(R.id.tag_row_id, rowTag);

        // Flag conditions (compute mother VL in background)
        boolean heiHivPositive = false; // HEI tests HIV-positive at birth
        try {
            String trBirth = safe(monitoringModel.getTest_result_at_birth());
            heiHivPositive = "d".equalsIgnoreCase(trBirth);
        } catch (Throwable ignored) {}

        holder.heiHighRiskButton.setVisibility(View.GONE);
        if (holder.heiUnsuppressedAlert != null) holder.heiUnsuppressedAlert.setVisibility(View.GONE);

        if (holder.heiHivPositiveButton != null)
            holder.heiHivPositiveButton.setVisibility(heiHivPositive ? View.VISIBLE : View.GONE);
        if (!heiHivPositive && holder.heiHivPositiveAlert != null) holder.heiHivPositiveAlert.setVisibility(View.GONE);

        final boolean finalHeiHivPositive = heiHivPositive;
        if (holder.heiHivPositiveButton != null) {
            holder.heiHivPositiveButton.setOnClickListener(v -> {
                if (holder.heiHivPositiveAlert != null) toggleAlert(holder.heiHivPositiveAlert);
                if (holder.heiUnsuppressedAlert != null) hideAlert(holder.heiUnsuppressedAlert);
                try {
                    timber.log.Timber.i("HEI_ALERT_HIV_POS_VIEWED pmtct_id=%s hiv_positive=%s",
                            monitoringModel.getPmtct_id(), String.valueOf(finalHeiHivPositive));
                } catch (Throwable ignored) {}
            });
        }

        // Close buttons for each inline alert
        if (holder.heiUnsuppressedAlert != null) {
            View close1 = holder.heiUnsuppressedAlert.findViewById(R.id.btn_close_alert);
            if (close1 != null) close1.setOnClickListener(v -> hideAlert(holder.heiUnsuppressedAlert));
        }
        if (holder.heiHivPositiveAlert != null) {
            View close2 = holder.heiHivPositiveAlert.findViewById(R.id.btn_close_alert);
            if (close2 != null) close2.setOnClickListener(v -> hideAlert(holder.heiHivPositiveAlert));
        }

        // Visual stripe: red when any flagged, default otherwise
        View stripe = holder.itemView.findViewById(R.id.hei_status_stripe);
        if (stripe != null) {
            if (heiHivPositive) {
                stripe.setBackgroundColor(0xFFE53935);
            } else {
                stripe.setBackgroundResource(R.drawable.bg_status_stripe);
            }
        }

        final String householdId = monitoringModel.getHousehold_id();
        Threading.ioBestEffort(() -> {
            boolean heiHighRisk = false;
            try {
                String hid = safe(householdId);
                String pmtctId = safe(monitoringModel.getHousehold_id());
                com.bluecodeltd.ecap.chw.model.PtctMotherModel mother = null;
                if (!hid.isEmpty()) {
                    mother = com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao.getPMCTMother(hid);
                }
                if (mother == null && !pmtctId.isEmpty()) {
                    mother = com.bluecodeltd.ecap.chw.dao.PMTCTMotherDao.getPMCTMother(pmtctId);
                }
                if (mother != null) {
                    String agywUnsupp = safe(mother.getAgyw_unsuppressed_vl_1st());
                    String unsupp = safe(mother.getUnsuppressed_vl_1st());
                    heiHighRisk = "yes".equalsIgnoreCase(agywUnsupp) || "yes".equalsIgnoreCase(unsupp);
                }
            } catch (Throwable ignored) {}

            final boolean finalHeiHighRisk = heiHighRisk;
            Threading.main(() -> {
                Object tag = holder.itemView.getTag(R.id.tag_row_id);
                if (!(tag instanceof String) || !rowTag.equals(tag)) return;

                holder.heiHighRiskButton.setVisibility(finalHeiHighRisk ? View.VISIBLE : View.GONE);
                if (!finalHeiHighRisk && holder.heiUnsuppressedAlert != null) holder.heiUnsuppressedAlert.setVisibility(View.GONE);

                holder.heiHighRiskButton.setOnClickListener(v -> {
                    if (holder.heiUnsuppressedAlert != null) toggleAlert(holder.heiUnsuppressedAlert);
                    if (holder.heiHivPositiveAlert != null) hideAlert(holder.heiHivPositiveAlert);
                    try {
                        timber.log.Timber.i("HEI_ALERT_UNSUPPRESSED_VIEWED pmtct_id=%s mother_unsuppressed=%s",
                                monitoringModel.getPmtct_id(), String.valueOf(finalHeiHighRisk));
                    } catch (Throwable ignored) {}
                });

                if (stripe != null) {
                    if (finalHeiHighRisk || finalHeiHivPositive) {
                        stripe.setBackgroundColor(0xFFE53935);
                    } else {
                        stripe.setBackgroundResource(R.drawable.bg_status_stripe);
                    }
                }
            });
        });

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
        View heiUnsuppressedAlert;
        View heiHivPositiveAlert;
        android.widget.Button heiHighRiskButton;
        android.widget.Button heiHivPositiveButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.familyNameTextView);
            age = itemView.findViewById(R.id.child_age);
            genderIcon = itemView.findViewById(R.id.gender_icon);
            relativeLayout = itemView.findViewById(R.id.register_columns);
            heiUnsuppressedAlert = itemView.findViewById(R.id.hei_unsuppressed_alert);
            heiHivPositiveAlert = itemView.findViewById(R.id.hei_hiv_positive_alert);
            heiHighRiskButton = itemView.findViewById(R.id.hei_high_risk_flag);
            heiHivPositiveButton = itemView.findViewById(R.id.hei_hiv_positive_flag);
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

    private void toggleAlert(View alertView) {
        if (alertView == null) return;
        if (alertView.getVisibility() == View.VISIBLE) {
            hideAlert(alertView);
        } else {
            alertView.clearAnimation();
            alertView.setVisibility(View.VISIBLE);
            try {
                android.view.animation.Animation a = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.expand_in);
                alertView.startAnimation(a);
            } catch (Throwable ignored) {}
        }
    }

    private void hideAlert(View alertView) {
        if (alertView == null) return;
        try {
            android.view.animation.Animation a = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.collapse_out);
            a.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                @Override public void onAnimationStart(android.view.animation.Animation animation) {}
                @Override public void onAnimationEnd(android.view.animation.Animation animation) { alertView.setVisibility(View.GONE); }
                @Override public void onAnimationRepeat(android.view.animation.Animation animation) {}
            });
            alertView.startAnimation(a);
        } catch (Throwable e) {
            alertView.setVisibility(View.GONE);
        }
    }
}

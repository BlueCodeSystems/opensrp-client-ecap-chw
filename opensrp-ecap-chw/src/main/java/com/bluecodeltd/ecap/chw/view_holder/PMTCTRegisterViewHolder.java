package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;

public class PMTCTRegisterViewHolder extends RecyclerView.ViewHolder {

    private TextView familyNameTextView;

    private TextView villageTextView, gender_age,client_type;

    //public Button caseplan_layout;

    private View myStatus;

    private final ImageView  visitLayout, caseplan_layout;
    private final TextView unsuppressedVlFlag;
    private final TextView suppressedVlFlag;
    private final View unsuppressedAlert;
    private final TextView index_icon_layout;

    public PMTCTRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        caseplan_layout = itemView.findViewById(R.id.index_case_plan);
        index_icon_layout = itemView.findViewById(R.id.index_icon);
        myStatus = itemView.findViewById(R.id.mystatusx);
        visitLayout = itemView.findViewById(R.id.index_visit);
        gender_age = itemView.findViewById(R.id.gender_age);
        // index_warning removed from layout
        client_type = itemView.findViewById(R.id.client_type);
        unsuppressedVlFlag = itemView.findViewById(R.id.unsuppressed_vl_flag);
        suppressedVlFlag = itemView.findViewById(R.id.suppressed_vl_flag);
        unsuppressedAlert = itemView.findViewById(R.id.unsuppressed_alert);

    }


    public void setupViews(String family, String village,String gender,String age,String client){

        //check if gender is null
        if(gender == null)
        {
            gender = "";
        }

        if(family == null)
        {
            family = "";
        }
        if(age == null)
        {
            age = "";
        }


        villageTextView.setText(village);
        familyNameTextView.setText(family);
        gender_age.setText(gender + "  " + age);
        if(client.equals("Other Community")){
            gender_age.setVisibility(View.GONE);
        }
        //client_type.setText("Testing Modality: "+client);

    }

    public void setUnsuppressedVlFlag(boolean flagged) {
        if (unsuppressedVlFlag != null) {
            unsuppressedVlFlag.setVisibility(flagged ? View.VISIBLE : View.GONE);
            try {
                if (flagged) {
                    android.view.animation.Animation anim = android.view.animation.AnimationUtils.loadAnimation(itemView.getContext(), com.bluecodeltd.ecap.chw.R.anim.pulse);
                    unsuppressedVlFlag.startAnimation(anim);
                } else {
                    unsuppressedVlFlag.clearAnimation();
                }
            } catch (Throwable ignored) { }
        }
        if (unsuppressedAlert != null && !flagged) {
            unsuppressedAlert.setVisibility(View.GONE);
        }
        if (myStatus != null) {
            if (flagged) {
                // Highlight left stripe in red for visibility
                myStatus.setBackgroundColor(0xFFE53935); // colorRed-ish
            } else {
                // Reset to default primary stripe
                myStatus.setBackgroundResource(R.drawable.bg_status_stripe);
            }
        }
        // no warning icon in register row
    }

    public void setSuppressedVlFlag(boolean flagged) {
        if (suppressedVlFlag != null) {
            suppressedVlFlag.setVisibility(flagged ? View.VISIBLE : View.GONE);
        }
        // When suppressed, ensure unsuppressed alert is hidden and stripe set to default
        if (flagged) {
            if (unsuppressedAlert != null) unsuppressedAlert.setVisibility(View.GONE);
            if (myStatus != null) myStatus.setBackgroundResource(R.drawable.bg_status_stripe);
            if (unsuppressedVlFlag != null) unsuppressedVlFlag.clearAnimation();
        }
    }

    public void toggleUnsuppressedAlert() {
        if (unsuppressedAlert == null) return;
        int vis = unsuppressedAlert.getVisibility();
        if (vis != View.VISIBLE) {
            unsuppressedAlert.clearAnimation();
            unsuppressedAlert.setVisibility(View.VISIBLE);
            try {
                android.view.animation.Animation a = android.view.animation.AnimationUtils.loadAnimation(itemView.getContext(), com.bluecodeltd.ecap.chw.R.anim.expand_in);
                unsuppressedAlert.startAnimation(a);
            } catch (Throwable ignored) {}
        } else {
            try {
                android.view.animation.Animation a = android.view.animation.AnimationUtils.loadAnimation(itemView.getContext(), com.bluecodeltd.ecap.chw.R.anim.collapse_out);
                a.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                    @Override public void onAnimationStart(android.view.animation.Animation animation) {}
                    @Override public void onAnimationEnd(android.view.animation.Animation animation) { unsuppressedAlert.setVisibility(View.GONE); }
                    @Override public void onAnimationRepeat(android.view.animation.Animation animation) {}
                });
                unsuppressedAlert.startAnimation(a);
            } catch (Throwable e) {
                unsuppressedAlert.setVisibility(View.GONE);
            }
        }
    }


}

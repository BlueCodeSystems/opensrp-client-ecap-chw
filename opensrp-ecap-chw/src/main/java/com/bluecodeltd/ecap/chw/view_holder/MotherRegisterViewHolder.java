package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MotherRegisterViewHolder extends RecyclerView.ViewHolder {

    private TextView motherNameTextView;
    private TextView householdIdTextView;
    private TextView motherAgeTextView;
    private TextView childrenTextView;
    private TextView referralDateTextView;

    public MotherRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        motherNameTextView = itemView.findViewById(R.id.motherNameTextView);
        householdIdTextView = itemView.findViewById(R.id.facilityTextView);
        motherAgeTextView = itemView.findViewById(R.id.motherAgeTextView);
        childrenTextView = itemView.findViewById(R.id.childrenTextView);
        referralDateTextView = itemView.findViewById(R.id.referralDateTextView);
    }

    public void setupViews(String fullName, String hh_id, String age, String childrenSummary, String enrollmentDate){
        motherNameTextView.setText(fullName != null ? fullName : "");
        householdIdTextView.setText(hh_id != null ? hh_id : "");
        if (motherAgeTextView != null) {
            motherAgeTextView.setText(age != null ? age : "");
        }
        if (childrenTextView != null) {
            childrenTextView.setText(childrenSummary != null ? childrenSummary : "");
        }
        if (referralDateTextView != null) {
            referralDateTextView.setText(enrollmentDate != null ? enrollmentDate : "");
        }
    }

}

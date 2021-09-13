package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;

public class IndexRegisterViewHolder extends RecyclerView.ViewHolder {

    private TextView familyNameTextView;

    private TextView villageTextView;

    private LinearLayout caseplan_layout;

    public IndexRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        caseplan_layout = itemView.findViewById(R.id.case_plan_wrapper);
    }

    public void setupViews(String family, String village, boolean plan){
        familyNameTextView.setText(family);
        villageTextView.setText(village);

        if(plan){
            caseplan_layout.setVisibility(View.VISIBLE);
        } else {
            caseplan_layout.setVisibility(View.GONE);
        }

    }

}

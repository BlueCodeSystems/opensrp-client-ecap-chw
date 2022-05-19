package com.bluecodeltd.ecap.chw.view_holder;

import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;

import org.json.JSONException;
import org.smartregister.view.contract.Village;

public class IndexRegisterViewHolder extends RecyclerView.ViewHolder {

    private TextView familyNameTextView;

    private TextView villageTextView, gender_age;

    //public Button caseplan_layout;

    private View myStatus;

    private final ImageView  visitLayout, caseplan_layout, warningIcon;
    private final TextView index_icon_layout;

    public IndexRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        caseplan_layout = itemView.findViewById(R.id.index_case_plan);
        index_icon_layout = itemView.findViewById(R.id.index_icon);
        myStatus = itemView.findViewById(R.id.mystatusx);
        visitLayout = itemView.findViewById(R.id.index_visit);
        gender_age = itemView.findViewById(R.id.gender_age);
        warningIcon = itemView.findViewById(R.id.index_warning);

    }


    public void setupViews(String family, String village, int plans, int visits, String is_index, String status, String gender, String age, String is_screened){

        familyNameTextView.setText(family);
        villageTextView.setText(village);
        gender_age.setText(gender + " : " + age);

        if(status != null && status.equals("1")){
            myStatus.setBackgroundColor(0xff05b714);
        } else if (status != null && status.equals("0")) {
            myStatus.setBackgroundColor(0xffff0000);
        } else if(status != null && status.equals("2")){
            myStatus.setBackgroundColor(0xffffa500);
        }

        if (is_index != null && is_index.equals("1")){

            index_icon_layout.setVisibility(View.VISIBLE);
        } else {

            index_icon_layout.setVisibility(View.GONE);
        }


        if(plans > 0){
            caseplan_layout.setVisibility(View.VISIBLE);
        } else {
            caseplan_layout.setVisibility(View.GONE);
        }

        if(visits > 0){
            visitLayout.setVisibility(View.VISIBLE);
        } else {
            visitLayout.setVisibility(View.GONE);
        }


        if(is_screened != null && is_screened.equals("true")){
            warningIcon.setVisibility(View.GONE);
        } else {
            warningIcon.setVisibility(View.VISIBLE);
        }

    }


}

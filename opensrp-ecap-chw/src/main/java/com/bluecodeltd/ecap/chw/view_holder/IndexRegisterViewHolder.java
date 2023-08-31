package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.dao.VcaVisitationDao;
import com.bluecodeltd.ecap.chw.model.VcaVisitationModel;

public class IndexRegisterViewHolder extends RecyclerView.ViewHolder {

    private TextView familyNameTextView;

    private TextView villageTextView, gender_age;

    //public Button caseplan_layout;

    private View myStatus;

    private final ImageView  visitLayout, caseplan_layout, warningIcon;
    private final TextView index_icon_layout;
    private final Button dueButton;

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
        dueButton = itemView.findViewById(R.id.due_button);

    }


    public void setupViews(String family, String village, int plans, int visits, String is_index, String status, String gender, String age, String is_screened){

        familyNameTextView.setText(family);
        villageTextView.setText("ID : "+village);
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
        VcaVisitationModel visitStatus = VcaVisitationDao.getVcaVisitationNotification(village);

        if (visitStatus == null) {
            dueButton.setBackgroundResource(R.drawable.due_contact);
            dueButton.setText("Conduct Visit");
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.btn_blue));
            return;
        }

        String statusColor = visitStatus.getStatus_color();

        if (statusColor != null) {
            statusColor = statusColor.trim();
        }

        if (statusColor != null && statusColor.equalsIgnoreCase("green")) {
            dueButton.setBackgroundResource(R.drawable.home_visit_due);
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.colorGreen));
            dueButton.setText("Visit Due: "+visitStatus.getVisit_date());
        } else if (statusColor != null && statusColor.equalsIgnoreCase("yellow")) {
            dueButton.setBackgroundResource(R.drawable.home_visit_10days_less);
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.pie_chart_yellow));
            dueButton.setText("Visit Due: "+visitStatus.getVisit_date());
        } else if (statusColor != null && statusColor.equalsIgnoreCase("red")) {
            dueButton.setBackgroundResource(R.drawable.home_visit_overdue);
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.red_overlay));
            dueButton.setText("Visit Overdue: "+visitStatus.getVisit_date());
        } else  {
            dueButton.setBackgroundResource(R.drawable.due_contact);
            dueButton.setText("Conduct Visit");
            dueButton.setTextColor(ContextCompat.getColor(dueButton.getContext(), R.color.btn_blue));
        }

    }


}

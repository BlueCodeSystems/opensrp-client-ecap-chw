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

import org.smartregister.view.contract.Village;

public class IndexRegisterViewHolder extends RecyclerView.ViewHolder {

    private TextView familyNameTextView;

    private TextView villageTextView;

    private Button caseplan_layout;

    private View myStatus;

    private final ImageView index_icon_layout;

    public IndexRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
        caseplan_layout = itemView.findViewById(R.id.due_button);
        index_icon_layout = itemView.findViewById(R.id.index_icon);
        myStatus = itemView.findViewById(R.id.mystatus);
    }

    public void setupViews(String family, String village, boolean plan, String is_index, String status){
        familyNameTextView.setText(family);
        villageTextView.setText(village);

        if(status == "0"){

            myStatus.setBackgroundDrawable(new ColorDrawable(0xffFF0000));

        } else if (status == "2") {

            myStatus.setBackgroundDrawable(new ColorDrawable(0xffFFA500));

        } else if (status == "1") {

            myStatus.setBackgroundDrawable(new ColorDrawable(0xff05b714));
        }


        if (is_index != null){

            index_icon_layout.setVisibility(View.VISIBLE);
        } else {

            index_icon_layout.setVisibility(View.GONE);
        }


        if(plan){
            caseplan_layout.setVisibility(View.VISIBLE);
        } else {
            caseplan_layout.setVisibility(View.GONE);
        }

    }

}

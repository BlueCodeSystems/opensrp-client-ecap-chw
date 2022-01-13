package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MotherRegisterViewHolder extends RecyclerView.ViewHolder {

    private TextView familyNameTextView;

    private TextView villageTextView;

    public MotherRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        familyNameTextView = itemView.findViewById(R.id.familyNameTextView);
        villageTextView = itemView.findViewById(R.id.villageTextView);
    }

    public void setupViews(String fullName, String hh_id){
        familyNameTextView.setText(fullName);
        villageTextView.setText(hh_id);
    }

}

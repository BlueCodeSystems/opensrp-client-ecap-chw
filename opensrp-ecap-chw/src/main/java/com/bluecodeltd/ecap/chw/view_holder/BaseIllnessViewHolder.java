package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;

public class BaseIllnessViewHolder extends RecyclerView.ViewHolder {
    public TextView tvQuestion;

    public BaseIllnessViewHolder(View view) {
        super(view);
        tvQuestion = view.findViewById(R.id.tvQuestion);
    }
}

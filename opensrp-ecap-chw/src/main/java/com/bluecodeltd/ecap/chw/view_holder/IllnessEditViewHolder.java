package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.TextView;

import com.bluecodeltd.ecap.chw.R;

public class IllnessEditViewHolder extends BaseIllnessViewHolder {
    public TextView tvValue;

    public IllnessEditViewHolder(View view) {
        super(view);
        tvValue = view.findViewById(R.id.tvValue);
    }
}

package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;
import android.widget.LinearLayout;

import com.bluecodeltd.ecap.chw.R;

public class IllnessCheckViewHolder extends BaseIllnessViewHolder {
    public LinearLayout checkboxParentLayout;

    public IllnessCheckViewHolder(View view) {
        super(view);
        checkboxParentLayout = view.findViewById(R.id.checkBoxParent);
    }
}

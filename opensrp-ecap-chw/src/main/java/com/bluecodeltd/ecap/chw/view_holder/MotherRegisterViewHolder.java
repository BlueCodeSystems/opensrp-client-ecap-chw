package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.util.ServiceRegisterStatus.Tone;

/** Mother index register row on the shared register row. */
public class MotherRegisterViewHolder extends RecyclerView.ViewHolder {

    private final RegisterRow row;

    public MotherRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        row = new RegisterRow(itemView).avatar(R.drawable.ic_mother_holding_baby, R.color.register_mother_icon, R.color.register_mother_bg);
    }

    public void setupViews(String fullName, String householdId, String age, String childrenSummary, String enrollment) {
        row.name(fullName)
                .id(householdId)
                .summary(RegisterRow.join("Female", age, childrenSummary))
                .stripe(Tone.NEUTRAL)
                .due(enrollment, Tone.NEUTRAL);
    }
}

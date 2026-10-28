package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.util.ServiceRegisterStatus.Tone;

/**
 * PMTCT register row on the shared register row. The viral load result is the status: unsuppressed shows a
 * red stripe and chip so it stands out in the list, suppressed a green chip.
 */
public class PMTCTRegisterViewHolder extends RecyclerView.ViewHolder {

    private final RegisterRow row;
    private boolean unsuppressed;
    private boolean suppressed;

    public PMTCTRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        row = new RegisterRow(itemView).avatar(R.drawable.ic_mother_child, R.color.register_pmtct_icon, R.color.register_pmtct_bg);
    }

    public void setupViews(String name, String id, String birthdate) {
        row.name(name)
                .id(id == null || id.trim().isEmpty() ? "" : id.trim())
                .summary(RegisterRow.join("Female", RegisterRow.age(birthdate)));
        showStatus();
    }

    public void setEnrolledDate(String date) {
        row.due(date == null || date.trim().isEmpty() ? "" : "Enrolled " + RegisterRow.shownDate(date), Tone.NEUTRAL);
    }

    public void setUnsuppressedVlFlag(boolean flagged) {
        unsuppressed = flagged;
        showStatus();
    }

    public void setSuppressedVlFlag(boolean flagged) {
        suppressed = flagged;
        showStatus();
    }

    private void showStatus() {
        if (unsuppressed) {
            row.stripe(Tone.CRITICAL).status("Unsuppressed VL", Tone.CRITICAL);
        } else if (suppressed) {
            row.stripe(Tone.GOOD).status("Suppressed VL", Tone.GOOD);
        } else {
            row.stripe(Tone.NEUTRAL).status("", Tone.NEUTRAL);
        }
    }

    /** The inline alert was replaced by the status chip; kept so callers need not change. */
    public void toggleUnsuppressedAlert() {
        // Nothing to toggle
    }
}

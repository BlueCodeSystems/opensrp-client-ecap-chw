package com.bluecodeltd.ecap.chw.view_holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.util.ServiceRegisterStatus.Tone;

/** HTS register row on the shared register row: who was tested, how, and the result. */
public class HivTestingServicesRegisterViewHolder extends RecyclerView.ViewHolder {

    private final RegisterRow row;

    public HivTestingServicesRegisterViewHolder(@NonNull View itemView) {
        super(itemView);
        row = new RegisterRow(itemView).avatar(R.drawable.ic_shield_droplet, R.color.register_hts_icon, R.color.register_hts_bg);
    }

    public void setupViews(String name, String clientId, String gender, String birthdate, String modality,
                           String dateTested, String result, String artStarted) {
        boolean onArt = artStarted != null && !artStarted.trim().isEmpty();
        row.name(name)
                .id(clientId == null || clientId.trim().isEmpty() ? "" : "ID " + clientId.trim())
                .summary(RegisterRow.join(RegisterRow.sex(gender), RegisterRow.age(birthdate)))
                .detail(modality == null || modality.trim().isEmpty() ? "" : "Testing modality: " + modality.trim())
                .stripe(onArt ? Tone.GOOD : Tone.NEUTRAL)
                .status(onArt ? "On ART" : resultLabel(result), onArt ? Tone.GOOD : Tone.NEUTRAL)
                .due(dateTested == null || dateTested.trim().isEmpty() ? "" : "Tested " + RegisterRow.shownDate(dateTested), Tone.NEUTRAL);
    }

    /** The result as staff read it: "HIV negative", "Newly tested HIV positive" and so on. */
    private static String resultLabel(String result) {
        if (result == null || result.trim().isEmpty()) return "";
        String value = result.trim().toLowerCase();
        String status = value.contains("hiv+") || value.contains("positive") ? "HIV positive"
                : value.contains("hiv-") || value.contains("negative") ? "HIV negative" : null;
        if (status == null) return Character.toUpperCase(value.charAt(0)) + value.substring(1);
        return value.contains("newly") ? "Newly tested " + status : status;
    }
}

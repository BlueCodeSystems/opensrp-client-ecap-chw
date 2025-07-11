package org.smartregister.chw.core.holders;

import android.view.View;
import android.widget.TextView;

import org.smartregister.chw.core.R;
import org.smartregister.opd.holders.OpdRegisterViewHolder;

public class CoreAllClientsRegisterViewHolder extends OpdRegisterViewHolder {
    public TextView textViewReferralDay;

    public CoreAllClientsRegisterViewHolder(View itemView) {
        super(itemView);
        textViewReferralDay = itemView.findViewById(R.id.text_view_referral_day);
    }

}

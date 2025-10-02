package org.smartregister.chw.anc.listener;


import android.view.View;

import org.smartregister.chw.anc.fragment.BaseAncRespondersCallDialogFragment;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.opensrp_chw_anc.R;

import timber.log.Timber;

public class BaseAncResponderCallWidgetDialogListener implements View.OnClickListener {

    private BaseAncRespondersCallDialogFragment callDialogFragment;

    public BaseAncResponderCallWidgetDialogListener(BaseAncRespondersCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.anc_call_close) {
            callDialogFragment.dismiss();
        } else if (i == R.id.anc_call_responder_phone) {
            try {
                String phoneNumber = (String) v.getTag();
                NCUtils.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}

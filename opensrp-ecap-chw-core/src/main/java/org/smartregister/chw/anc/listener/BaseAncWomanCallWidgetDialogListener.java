package org.smartregister.chw.anc.listener;


import android.view.View;

import org.smartregister.chw.anc.fragment.BaseAncWomanCallDialogFragment;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.opensrp_chw_anc.R;

import timber.log.Timber;

public class BaseAncWomanCallWidgetDialogListener implements View.OnClickListener {

    private BaseAncWomanCallDialogFragment callDialogFragment;

    public BaseAncWomanCallWidgetDialogListener(BaseAncWomanCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.anc_call_close) {
            callDialogFragment.dismiss();
        } else if (i == R.id.anc_call_head_phone) {
            try {
                String phoneNumber = (String) v.getTag();
                NCUtils.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                callDialogFragment.dismiss();
            } catch (Exception e) {
                Timber.e(e);
            }
        } else if (i == R.id.call_anc_woman_phone) {
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

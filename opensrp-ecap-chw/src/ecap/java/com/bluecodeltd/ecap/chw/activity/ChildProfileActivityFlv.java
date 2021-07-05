package com.bluecodeltd.ecap.chw.activity;

import android.app.Activity;

import com.bluecodeltd.ecap.chw.R;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import com.bluecodeltd.ecap.chw.presenter.ChildProfilePresenter;

public class ChildProfileActivityFlv extends DefaultChildProfileActivityFlv {

    @Override
    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final ChildProfilePresenter presenter) {
        return viewId -> {
            if (presenter != null) {
                switch (viewId) {
                    case R.id.call_layout:
                        FamilyCallDialogFragment.launchDialog(activity, presenter.getFamilyId());
                        break;
                    case R.id.refer_to_facility_layout:
                        presenter.referToFacility();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}

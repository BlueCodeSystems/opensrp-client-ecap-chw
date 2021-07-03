package com.bluecodeltd.ecap.chw.fragment;

import android.app.Activity;

import com.bluecodeltd.ecap.chw.activity.AncHomeVisitActivity;
import com.bluecodeltd.ecap.chw.activity.AncMemberProfileActivity;
import org.smartregister.chw.core.fragment.CoreAncRegisterFragment;
import com.bluecodeltd.ecap.chw.model.AncRegisterFragmentModel;
import com.bluecodeltd.ecap.chw.presenter.ChwAncRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class AncRegisterFragment extends CoreAncRegisterFragment {
    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new ChwAncRegisterFragmentPresenter(this, new AncRegisterFragmentModel(), null);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        AncMemberProfileActivity.startMe(getActivity(), client.getCaseId());
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        Activity activity = getActivity();
        if (activity == null)
            return;

        AncHomeVisitActivity.startMe(activity, client.getCaseId(), false);
    }
}

package com.bluecodeltd.ecap.chw.fragment;

import com.bluecodeltd.ecap.chw.activity.PncHomeVisitActivity;
import com.bluecodeltd.ecap.chw.activity.PncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.fragment.CorePncRegisterFragment;
import com.bluecodeltd.ecap.chw.model.ChwPncRegisterFragmentModel;
import com.bluecodeltd.ecap.chw.presenter.PncRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class PncRegisterFragment extends CorePncRegisterFragment {

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        PncHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false);
    }

    @Override
    protected void openPncMemberProfile(CommonPersonObjectClient client) {
        MemberObject memberObject = new MemberObject(client);
        PncMemberProfileActivity.startMe(getActivity(), memberObject.getBaseEntityId());
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new PncRegisterFragmentPresenter(this, new ChwPncRegisterFragmentModel(), null);
    }

}

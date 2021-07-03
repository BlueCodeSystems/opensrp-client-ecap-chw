package com.bluecodeltd.ecap.chw.fragment;

import com.bluecodeltd.ecap.chw.activity.FamilyPlanningMemberProfileActivity;
import com.bluecodeltd.ecap.chw.activity.FpFollowUpVisitActivity;
import org.smartregister.chw.core.fragment.CoreFpRegisterFragment;
import org.smartregister.chw.fp.dao.FpDao;
import com.bluecodeltd.ecap.chw.model.FpRegisterFragmentModel;
import com.bluecodeltd.ecap.chw.presenter.FpRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class FpRegisterFragment extends CoreFpRegisterFragment {

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new FpRegisterFragmentPresenter(this, new FpRegisterFragmentModel());
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        FamilyPlanningMemberProfileActivity.startFpMemberProfileActivity(getActivity(), FpDao.getMember(client.getCaseId()));
    }

    @Override
    protected void openFollowUpVisit(CommonPersonObjectClient client) {
        FpFollowUpVisitActivity.startMe(getActivity(), FpDao.getMember(client.getCaseId()), false);
    }


}



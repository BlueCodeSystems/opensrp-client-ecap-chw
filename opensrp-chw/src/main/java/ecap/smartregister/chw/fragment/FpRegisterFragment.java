package ecap.smartregister.chw.fragment;

import ecap.smartregister.chw.activity.FamilyPlanningMemberProfileActivity;
import ecap.smartregister.chw.activity.FpFollowUpVisitActivity;
import org.smartregister.chw.core.fragment.CoreFpRegisterFragment;
import org.smartregister.chw.fp.dao.FpDao;
import ecap.smartregister.chw.model.FpRegisterFragmentModel;
import ecap.smartregister.chw.presenter.FpRegisterFragmentPresenter;
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



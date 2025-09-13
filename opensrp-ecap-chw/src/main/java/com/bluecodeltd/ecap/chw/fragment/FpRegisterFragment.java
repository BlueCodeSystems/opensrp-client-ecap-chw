package com.bluecodeltd.ecap.chw.fragment;

import com.bluecodeltd.ecap.chw.activity.FamilyPlanningMemberProfileActivity;
import com.bluecodeltd.ecap.chw.activity.FpFollowUpVisitActivity;
import org.smartregister.chw.core.fragment.CoreFpRegisterFragment;
import org.smartregister.chw.fp.dao.FpDao;
import com.bluecodeltd.ecap.chw.model.FpRegisterFragmentModel;
import com.bluecodeltd.ecap.chw.presenter.FpRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class FpRegisterFragment extends CoreFpRegisterFragment {
    private com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding binding;

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


    @Override
    public void setupViews(android.view.View view) {
        try { binding = com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding.bind(view); } catch (Throwable ignored) {}
        super.setupViews(view);
        try {
            if (clientsView == null && binding != null && binding.recyclerView != null) {
                clientsView = binding.recyclerView;
                if (clientsView.getLayoutManager() == null && getContext() != null) {
                    clientsView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                }
                clientsView.setHasFixedSize(true);
            }
        } catch (Exception ignored) { }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


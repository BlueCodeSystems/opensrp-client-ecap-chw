package com.bluecodeltd.ecap.chw.fragment;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.ChildHomeVisitActivity;
import com.bluecodeltd.ecap.chw.activity.ChildProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import com.bluecodeltd.ecap.chw.application.ChwApplication;
import org.smartregister.chw.core.fragment.CoreChildRegisterFragment;
import com.bluecodeltd.ecap.chw.model.ChildRegisterFragmentModel;
import com.bluecodeltd.ecap.chw.presenter.ChildRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.ChildRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.Set;

import timber.log.Timber;

import static org.smartregister.chw.core.utils.ChildDBConstants.KEY.FAMILY_LAST_NAME;

public class ChildRegisterFragment extends CoreChildRegisterFragment {

    @Override
    protected void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
        if (view.getTag() instanceof CommonPersonObjectClient
                && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
            CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
            ChildHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false, ChildHomeVisitActivity.class);
        }
    }

    @Override
    public void goToChildDetailActivity(CommonPersonObjectClient patient, boolean launchDialog) {
        if (launchDialog) {
            Timber.i(patient.name);
        }
        MemberObject memberObject = new MemberObject(patient);
        memberObject.setFamilyName(Utils.getValue(patient.getColumnmaps(), FAMILY_LAST_NAME, false));
        ChildProfileActivity.startMe(getActivity(), memberObject, ChildProfileActivity.class);
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        ChildRegisterProvider childRegisterProvider = new ChildRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new ChildRegisterFragmentPresenter(this, new ChildRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);

        if (ChwApplication.getApplicationFlavor().hasDefaultDueFilterForChildClient()) {
            android.view.View dueOnlyLayout = view.findViewById(org.smartregister.R.id.due_only_layout);
            dueOnlyLayout.setVisibility(android.view.View.VISIBLE);
            dueOnlyLayout.setOnClickListener(registerActionHandler);
            dueOnlyLayout.setTag(null);
            toggleFilterSelection(dueOnlyLayout);
        }
    }

}

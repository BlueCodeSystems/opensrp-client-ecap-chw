package com.bluecodeltd.ecap.chw.fragment;

import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.loader.content.Loader;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.ChildHomeVisitActivity;
import com.bluecodeltd.ecap.chw.activity.ChildProfileActivity;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
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
    private static final long SEARCH_DEBOUNCE_MS = 350L;

    private com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding binding;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private String pendingSearchText = "";
    private final Runnable searchRunnable = () -> filter(pendingSearchText, "", getActiveMainCondition(), false);
    private final TextWatcher debouncedSearchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            // no-op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            org.smartregister.Context opensrpContext = CoreLibrary.getInstance().context();
            if (opensrpContext.getAppProperties().isTrue(AllConstants.PROPERTY.ENABLE_SEARCH_BUTTON)
                    && charSequence != null && charSequence.length() > 0) {
                return;
            }

            pendingSearchText = charSequence == null ? "" : charSequence.toString();
            searchHandler.removeCallbacks(searchRunnable);
            searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no-op
        }
    };

    @Override
    protected void onViewClicked(android.view.View view) {
        super.onViewClicked(view);
        if (view.getTag() instanceof CommonPersonObjectClient
                && view.getTag(org.smartregister.chw.core.R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
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

        if (ChwApplication.getApplicationFlavor().hasDefaultDueFilterForChildClient()) {
            android.view.View dueOnlyLayout = view.findViewById(org.smartregister.R.id.due_only_layout);
            dueOnlyLayout.setVisibility(android.view.View.VISIBLE);
            dueOnlyLayout.setOnClickListener(registerActionHandler);
            dueOnlyLayout.setTag(null);
            toggleFilterSelection(dueOnlyLayout);
        }
    }

    @Override
    protected void updateSearchView() {
        if (getSearchView() != null) {
            getSearchView().removeTextChangedListener(textWatcher);
            getSearchView().removeTextChangedListener(debouncedSearchWatcher);
            getSearchView().addTextChangedListener(debouncedSearchWatcher);
            getSearchView().setOnKeyListener(hideKeyboard);
        }
    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        if (getSearchCancelView() != null) {
            getSearchCancelView().setVisibility(filterString == null || filterString.isEmpty()
                    ? android.view.View.INVISIBLE : android.view.View.VISIBLE);
        }
        if (filterString == null || filterString.isEmpty()) {
            org.smartregister.util.Utils.hideKeyboard(getActivity());
        }

        this.filters = filterString;
        this.joinTable = joinTableString;
        this.mainCondition = mainConditionString;

        if (clientAdapter.getCurrentlimit() == 0) {
            clientAdapter.setCurrentlimit(20);
        }
        clientAdapter.setCurrentoffset(0);

        showProgressView();
        filterAndSortExecute();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        super.onLoadFinished(loader, cursor);
        setTotalPatients();
    }

    @Override
    public void onDestroyView() {
        searchHandler.removeCallbacks(searchRunnable);
        if (getSearchView() != null) {
            getSearchView().removeTextChangedListener(debouncedSearchWatcher);
        }
        super.onDestroyView();
        binding = null;
    }

    private String getActiveMainCondition() {
        return dueFilterActive ? getDueFilterCondition() : presenter().getMainCondition();
    }

}

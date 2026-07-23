package com.bluecodeltd.ecap.chw.fragment;

import android.database.Cursor;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import androidx.loader.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;

import com.bluecodeltd.ecap.chw.R;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.fragment.CoreAllClientsRegisterFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import com.bluecodeltd.ecap.chw.dao.FamilyDao;
import com.bluecodeltd.ecap.chw.model.FamilyDetailsModel;
import com.bluecodeltd.ecap.chw.util.AllClientsUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Constants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.util.Utils;

public class AllClientsRegisterFragment extends CoreAllClientsRegisterFragment {
    private static final long SEARCH_DEBOUNCE_MS = 350L;

    private com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding binding;
    public static final String REGISTER_TYPE = "register_type";
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private String pendingSearchText = "";
    private final Runnable searchRunnable = () -> filter(pendingSearchText, "", getMainCondition(), false);
    private final TextWatcher debouncedSearchWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            // no-op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            org.smartregister.Context opensrpContext = CoreLibrary.getInstance().context();
            if (opensrpContext.getAppProperties().isTrue(AllConstants.PROPERTY.ENABLE_SEARCH_BUTTON)
                    && StringUtils.isNotEmpty(charSequence.toString())) {
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
    public void setupViews(View view) {
        try { binding = com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding.bind(view); } catch (Throwable ignored) {}
        super.setupViews(view);
        // Ensure clientsView references the bound RecyclerView
        try {
            if (clientsView == null && binding != null && binding.recyclerView != null) {
                clientsView = binding.recyclerView;
                if (clientsView.getLayoutManager() == null && getContext() != null) {
                    clientsView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                }
                clientsView.setHasFixedSize(true);
            }
        } catch (Exception ignored) { }
        View dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.GONE);
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
        if (qrCode) {
            super.filter(filterString, joinTableString, mainConditionString, true);
            return;
        }

        if (getSearchCancelView() != null) {
            getSearchCancelView().setVisibility(StringUtils.isEmpty(filterString) ? View.INVISIBLE : View.VISIBLE);
        }
        if (StringUtils.isEmpty(filterString)) {
            Utils.hideKeyboard(getActivity());
        }

        this.filters = filterString;
        this.joinTable = joinTableString;
        this.mainCondition = mainConditionString;

        if (clientAdapter.getCurrentlimit() == 0) {
            clientAdapter.setCurrentlimit(20);
        }
        clientAdapter.setCurrentoffset(0);

        showProgressView();
        filterandSortExecute(countBundle());
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

    @Override
    protected void goToClientDetailActivity(@NonNull CommonPersonObjectClient commonPersonObjectClient) {

        String registerType = commonPersonObjectClient.getDetails().get(REGISTER_TYPE);

        Bundle bundle = new Bundle();
        FamilyDetailsModel familyDetailsModel = FamilyDao.getFamilyDetail(commonPersonObjectClient.entityId());

        if (familyDetailsModel != null) {
            bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyDetailsModel.getBaseEntityId());
            bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, familyDetailsModel.getFamilyHead());
            bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, familyDetailsModel.getPrimaryCareGiver());
            bundle.putString(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, familyDetailsModel.getFamilyName());
            bundle.putString(Constants.INTENT_KEY.VILLAGE_TOWN, commonPersonObjectClient.getDetails().get(OpdDbConstants.KEY.HOME_ADDRESS));
        }

        if (registerType != null) {
            switch (registerType) {
                case CoreConstants.REGISTER_TYPE.CHILD:
                    AllClientsUtils.goToChildProfile(this.getActivity(), commonPersonObjectClient, bundle);
                    break;
                case CoreConstants.REGISTER_TYPE.ANC:
                    AllClientsUtils.goToAncProfile(this.getActivity(), commonPersonObjectClient, bundle);
                    break;
                case CoreConstants.REGISTER_TYPE.PNC:
                    AllClientsUtils.gotToPncProfile(this.getActivity(), commonPersonObjectClient, bundle);
                    break;
                case CoreConstants.REGISTER_TYPE.MALARIA:
                    AllClientsUtils.gotToMalariaProfile(this.getActivity(), commonPersonObjectClient);
                    break;
                case CoreConstants.REGISTER_TYPE.FAMILY_PLANNING:
                    AllClientsUtils.goToFamilyPlanningProfile(this.getActivity(), commonPersonObjectClient);
                    break;
                default:
                    AllClientsUtils.goToOtherMemberProfile(this.getActivity(), commonPersonObjectClient, bundle,
                            familyDetailsModel.getFamilyHead(), familyDetailsModel.getPrimaryCareGiver());
                    break;
            }
        } else {
            if (familyDetailsModel != null) {
                AllClientsUtils.goToOtherMemberProfile(this.getActivity(), commonPersonObjectClient, bundle,
                        familyDetailsModel.getFamilyHead(), familyDetailsModel.getPrimaryCareGiver());
            }
        }
    }
}

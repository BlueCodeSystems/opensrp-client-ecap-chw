package com.bluecodeltd.ecap.chw.fragment;

import android.view.View;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.IndexRegisterProvider;

import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

public class IndexFragmentRegister extends BaseRegisterFragment implements IndexRegisterFragmentContract.View {

    @Override
    protected void initializePresenter() {
        this.presenter = new IndexRegisterFragmentPresenter();
        ((IndexRegisterFragmentPresenter)this.presenter).initView(this);
    }


    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        View topRightLayout = view.findViewById(R.id.top_right_layout);
        topRightLayout.setVisibility(View.GONE);
        View topLeftLayout = view.findViewById(org.smartregister.chw.core.R.id.top_left_layout);
        topLeftLayout.setVisibility(View.GONE);
        View sortFilterBarLayout = view.findViewById(org.smartregister.chw.core.R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(View.GONE);
        View filterSortLayout = view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout);
        filterSortLayout.setVisibility(View.GONE);
    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return "is_closed != 1";
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(View view) {

    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    public void initializeAdapter() {
        IndexRegisterProvider registerProvider = new IndexRegisterProvider(requireContext());
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository("ec_family"));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }
}

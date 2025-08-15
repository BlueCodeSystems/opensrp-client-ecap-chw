

package com.bluecodeltd.ecap.chw.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.contract.IdentificationRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.presenter.IdentificationRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.IndexRegisterProvider;

import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

import androidx.appcompat.widget.Toolbar;

public class IdentificationFragmentRegister extends BaseRegisterFragment implements IdentificationRegisterFragmentContract.View {

    @Override
    protected void initializePresenter() {
        this.presenter = new IdentificationRegisterFragmentPresenter();
        ((IdentificationRegisterFragmentPresenter)this.presenter).initView(this);
    }


    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);
        View navbarContainer = view.findViewById(org.smartregister.R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setLayoutParams(params);
        searchBarLayout.setBackgroundResource(R.color.primary);
        searchBarLayout.setPadding(searchBarLayout.getPaddingLeft(), searchBarLayout.getPaddingTop(), searchBarLayout.getPaddingRight(), (int) org.smartregister.chw.core.utils.Utils.convertDpToPixel(10, getActivity()));

        ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }
        CustomFontTextView titleView = view.findViewById(org.smartregister.R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(getString(R.string.all_index_title));
            titleView.setFontVariant(FontVariant.REGULAR);
            titleView.setClickable(false);

        }
        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(org.smartregister.R.color.text_black));
        }
        View topRightLayout = view.findViewById(org.smartregister.R.id.top_right_layout);
        topRightLayout.setVisibility(View.GONE);
        View topLeftLayout = view.findViewById(org.smartregister.R.id.top_left_layout);
        topLeftLayout.setVisibility(View.GONE);
        View sortFilterBarLayout = view.findViewById(org.smartregister.R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(View.GONE);
        View filterSortLayout = view.findViewById(org.smartregister.R.id.filter_sort_layout);
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
        IndexRegisterProvider registerProvider = new IndexRegisterProvider(requireContext(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository("ec_family"));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

}

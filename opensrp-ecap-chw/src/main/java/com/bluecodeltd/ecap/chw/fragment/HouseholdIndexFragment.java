package com.bluecodeltd.ecap.chw.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.HouseholdDetails;
import com.bluecodeltd.ecap.chw.contract.HouseholdIndexFragmentContract;
import com.bluecodeltd.ecap.chw.dao.HouseholdDao;
import com.bluecodeltd.ecap.chw.presenter.HouseholdIndexFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.HouseholdRegisterProvider;
import com.bluecodeltd.ecap.chw.util.Constants;

import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class HouseholdIndexFragment extends BaseRegisterFragment implements HouseholdIndexFragmentContract.View{

    @Override
    protected void initializePresenter() {
        this.presenter = new HouseholdIndexFragmentPresenter();
        ((HouseholdIndexFragmentPresenter)this.presenter).initView(this);
    }


    @Override
    public void setupViews(View view) {

        try {
            super.setupViews(view);

            // Toolbar customization
            Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
            if (toolbar != null) {
                toolbar.setContentInsetsAbsolute(0, 0);
                toolbar.setContentInsetsRelative(0, 0);
                toolbar.setContentInsetStartWithNavigation(0);
                NavigationMenu.getInstance(getActivity(), null, toolbar);
            }

            // Navbar container
            View navbarContainer = view.findViewById(org.smartregister.R.id.register_nav_bar_container);
            if (navbarContainer != null) {
                navbarContainer.setFocusable(false);
            }

            // Search bar layout
            View searchBarLayout = view.findViewById(R.id.search_bar_layout);
            if (searchBarLayout != null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                searchBarLayout.setLayoutParams(params);
                searchBarLayout.setBackgroundResource(R.color.primary);
                searchBarLayout.setPadding(
                        searchBarLayout.getPaddingLeft(),
                        searchBarLayout.getPaddingTop(),
                        searchBarLayout.getPaddingRight(),
                        (int) org.smartregister.chw.core.utils.Utils.convertDpToPixel(10, getActivity())
                );
            }

            // Logo visibility
            ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
            if (logo != null) {
                logo.setVisibility(View.GONE);
            }

            // Title view customization
            CustomFontTextView titleView = view.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleView != null) {
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(getString(R.string.all_households_title));
                titleView.setFontVariant(FontVariant.REGULAR);
                titleView.setClickable(false);
            }

            // Search view customization
            if (getSearchView() != null) {
                getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
                getSearchView().setCompoundDrawablesWithIntrinsicBounds(
                        org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0
                );
                getSearchView().setTextColor(getResources().getColor(org.smartregister.R.color.text_black));
            }

            // Hide unused layouts
            View topRightLayout = view.findViewById(org.smartregister.R.id.top_right_layout);
            if (topRightLayout != null) {
                topRightLayout.setVisibility(View.GONE);
            }

            View topLeftLayout = view.findViewById(org.smartregister.R.id.top_left_layout);
            if (topLeftLayout != null) {
                topLeftLayout.setVisibility(View.GONE);
            }

            View sortFilterBarLayout = view.findViewById(org.smartregister.R.id.register_sort_filter_bar_layout);
            if (sortFilterBarLayout != null) {
                sortFilterBarLayout.setVisibility(View.GONE);
            }

            View filterSortLayout = view.findViewById(org.smartregister.R.id.filter_sort_layout);
            if (filterSortLayout != null) {
                filterSortLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SetupViews", "Error setting up views: " + e.getMessage());
        }

    }


    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return " status IS NULL";
    }

    @Override
    protected String getDefaultSortQuery() {
        return "last_interacted_with DESC";
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(View view) {

        CommonPersonObjectClient client =(CommonPersonObjectClient) view.getTag();

        //String isClosed = client.getColumnmaps().get("is_closed");
        String isClosed = HouseholdDao.getHouseholdByBaseId(client.getColumnmaps().get("base_entity_id")).getStatus();

         if(isClosed != null && isClosed.equals("1")){
             Toasty.warning(getActivity(), "This household has been deleted", Toast.LENGTH_LONG, true).show();
         } else {
             goToIndexDetailActivity(client);
         }

    }

    protected void goToIndexDetailActivity(CommonPersonObjectClient client) {

            Intent intent = new Intent(getActivity(), HouseholdDetails.class);
            intent.putExtra("householdId",  client.getColumnmaps().get("hid"));
            intent.putExtra("household",  client);
            startActivity(intent);

    }
    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    public void initializeAdapter() {
        HouseholdRegisterProvider registerProvider = new HouseholdRegisterProvider(requireContext(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(Constants.EcapClientTable.EC_HOUSEHOLD));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }
}

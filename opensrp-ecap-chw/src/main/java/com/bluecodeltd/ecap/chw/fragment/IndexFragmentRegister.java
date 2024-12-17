package com.bluecodeltd.ecap.chw.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.IndexRegisterProvider;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.github.javiersantos.appupdater.AppUpdater;

import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.Utils;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

public class IndexFragmentRegister extends BaseRegisterFragment implements IndexRegisterFragmentContract.View {

    AlertDialog.Builder builder;

    @Override
    protected void initializePresenter() {
        this.presenter = new IndexRegisterFragmentPresenter();
        ((IndexRegisterFragmentPresenter)this.presenter).initView(this);
    }


    @Override
    public void setupViews(View view) {
        if (view == null) {
            Log.e("setupViews", "View is null. Aborting setup.");
            return; // Exit if the view itself is null
        }

        try {
            super.setupViews(view);

            // Toolbar Setup
            Toolbar toolbar = view.findViewById(R.id.register_toolbar);
            if (toolbar != null) {
                toolbar.setContentInsetsAbsolute(0, 0);
                toolbar.setContentInsetsRelative(0, 0);
                toolbar.setContentInsetStartWithNavigation(0);
                NavigationMenu navigationMenu = NavigationMenu.getInstance(getActivity(), null, toolbar);
                if (navigationMenu == null) {
                    Log.w("setupViews", "NavigationMenu is null. Skipping toolbar setup.");
                }
            } else {
                Log.w("setupViews", "Toolbar is null.");
            }

            // Navbar Setup
            View navbarContainer = view.findViewById(R.id.register_nav_bar_container);
            if (navbarContainer != null) {
                navbarContainer.setFocusable(false);
            } else {
                Log.w("setupViews", "Navbar container is null.");
            }

            // Search Bar Layout Customization
            View searchBarLayout = view.findViewById(R.id.search_bar_layout);
            if (searchBarLayout != null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                );
                searchBarLayout.setLayoutParams(params);
                searchBarLayout.setBackgroundResource(R.color.primary);
                searchBarLayout.setPadding(
                        searchBarLayout.getPaddingLeft(),
                        searchBarLayout.getPaddingTop(),
                        searchBarLayout.getPaddingRight(),
                        (int) org.smartregister.chw.core.utils.Utils.convertDpToPixel(10, getActivity())
                );
            } else {
                Log.w("setupViews", "Search bar layout is null.");
            }

            // Logo Visibility
            ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
            if (logo != null) {
                logo.setVisibility(View.GONE);
            } else {
                Log.w("setupViews", "Logo view is null.");
            }

            // Title Setup
            CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
            if (titleView != null) {
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(getString(R.string.all_index_title));
                titleView.setFontVariant(FontVariant.REGULAR);
                titleView.setClickable(false);
            } else {
                Log.w("setupViews", "Title view is null.");
            }

            // Search View Customization
            if (getSearchView() != null) {
                getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
                getSearchView().setCompoundDrawablesWithIntrinsicBounds(
                        org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0
                );
                getSearchView().setTextColor(getResources().getColor(R.color.text_black));
            } else {
                Log.w("setupViews", "Search view is null.");
            }

            // Hide Top Layouts
            View topRightLayout = view.findViewById(R.id.top_right_layout);
            if (topRightLayout != null) {
                topRightLayout.setVisibility(View.GONE);
            } else {
                Log.w("setupViews", "Top-right layout is null.");
            }

            View topLeftLayout = view.findViewById(org.smartregister.chw.core.R.id.top_left_layout);
            if (topLeftLayout != null) {
                topLeftLayout.setVisibility(View.GONE);
            } else {
                Log.w("setupViews", "Top-left layout is null.");
            }

            View sortFilterBarLayout = view.findViewById(org.smartregister.chw.core.R.id.register_sort_filter_bar_layout);
            if (sortFilterBarLayout != null) {
                sortFilterBarLayout.setVisibility(View.GONE);
            } else {
                Log.w("setupViews", "Sort filter bar layout is null.");
            }

            View filterSortLayout = view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout);
            if (filterSortLayout != null) {
                filterSortLayout.setVisibility(View.GONE);
            } else {
                Log.w("setupViews", "Filter sort layout is null.");
            }

            // AlertDialog Setup
            Activity activity = getActivity();
            if (activity != null) {
                builder = new AlertDialog.Builder(activity);
            } else {
                Log.w("setupViews", "Activity is null. AlertDialog builder not initialized.");
            }

        /* Uncomment and modify if needed
        if (!isSyncing()) {
            AppUpdater appUpdater = new AppUpdater(getActivity());
            appUpdater.start();
        }
        */
        } catch (Exception e) {
            Log.e("setupViews", "Error occurred during setup: " + e.getMessage());
            e.printStackTrace();
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
        //return "case_status > 0 AND is_closed = 0 ";
        return "(deleted IS NULL OR deleted != '1') AND (subpop1 = 'true' OR subpop2 = 'true' OR subpop3 = 'true' OR subpop4 = 'true' OR subpop5 = 'true' OR subpop = 'true') AND first_name IS NOT NULL";
    }
    @Override
    protected String getDefaultSortQuery() {
        return "last_interacted_with DESC ";
    }

    @Override
    protected void startRegistration() {

    }


    @Override
    protected void onViewClicked(View view) {

        if(view.getId() == R.id.index_warning){

            builder.setMessage("\u2022  Household has not been Screened");
            builder.setNegativeButton("OK", (dialog, id) -> {
                //  Action for 'NO' Button
                dialog.cancel();

            });

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Alert");
            alert.show();

        } else if (view.getId() == R.id.register_columns){

            CommonPersonObjectClient client =(CommonPersonObjectClient) view.getTag();
            String childId = client.getColumnmaps().get("unique_id");

            goToIndexDetailActivity(childId,client);
        }
    }

    protected void goToIndexDetailActivity(String childId, CommonPersonObjectClient client) {

        Intent intent = new Intent(getActivity(), IndexDetailsActivity.class);
        intent.putExtra("Child",  childId);
        intent.putExtra("baseId",  client);
        startActivity(intent);
    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    public void initializeAdapter() {
        IndexRegisterProvider registerProvider = new IndexRegisterProvider(requireContext(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(Constants.EcapClientTable.EC_CLIENT_INDEX));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);


    }

    @Override
    protected boolean isSyncing() {
        return super.isSyncing();
    }

    @Override
    protected void onResumption() {

            super.onResumption();

    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus))) {
            Utils.showShortToast(getActivity(), getString(org.smartregister.chw.core.R.string.sync_complete));
            getActivity().recreate();
            AppUpdater appUpdater = new AppUpdater(getActivity());
            appUpdater.start();
        } else {
            super.onSyncComplete(fetchStatus);
        }
    }
}

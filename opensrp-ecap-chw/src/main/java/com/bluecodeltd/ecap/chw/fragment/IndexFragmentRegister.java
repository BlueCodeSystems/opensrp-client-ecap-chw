package com.bluecodeltd.ecap.chw.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import android.widget.ProgressBar;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.IndexRegisterProvider;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.bluecodeltd.ecap.chw.util.ViewTagUtils;
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

public class IndexFragmentRegister extends BaseSafeRegisterFragment implements IndexRegisterFragmentContract.View {

    private com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding binding;

    AlertDialog.Builder builder;

    @Override
    protected void initializePresenter() {
        this.presenter = new IndexRegisterFragmentPresenter();
        ((IndexRegisterFragmentPresenter)this.presenter).initView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        View root = getView();
        if (root != null) {
            android.widget.TextView titleLabel = root.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleLabel != null) {
                titleLabel.setVisibility(View.VISIBLE);
                titleLabel.setText(getString(R.string.all_index_title));
            }
            Toolbar toolbar = root.findViewById(org.smartregister.R.id.register_toolbar);
            if (toolbar != null) {
                toolbar.setTitle("");
            }
        }
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity act = (AppCompatActivity) getActivity();
            if (act.getSupportActionBar() != null) {
                act.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }


    @Override
    public void setupViews(View view) {
        try { binding = com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding.bind(view); } catch (Throwable ignored) {}

        if (view == null) {
            Log.e("setupViews", "View is null. Aborting setup.");
            return; // Exit if the view itself is null
        }

        try {
            // Ensure a RecyclerView exists before base setup tries to configure it
            try {
                androidx.recyclerview.widget.RecyclerView rv = null;
                try { rv = view.findViewById(R.id.recycler_view); } catch (Exception ignored) {}
                if (rv == null) { try { rv = view.findViewById(org.smartregister.R.id.recycler_view); } catch (Exception ignored) {} }
                if (rv == null && view instanceof android.view.ViewGroup) {
                    rv = new androidx.recyclerview.widget.RecyclerView(requireContext());
                    rv.setId(org.smartregister.R.id.recycler_view);
                    rv.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    ));
                    ((android.view.ViewGroup) view).addView(rv);
                }
            } catch (Exception ignored) { }

            // Ensure a ProgressBar exists (BaseRegisterFragment toggles its visibility during setup)
            try {
                android.widget.ProgressBar pb = null;
                try { pb = view.findViewById(R.id.client_list_progress); } catch (Exception ignored) {}
                if (pb == null && view instanceof android.view.ViewGroup) {
                    pb = new android.widget.ProgressBar(requireContext());
                    pb.setId(R.id.client_list_progress);
                    android.view.ViewGroup parent = (android.view.ViewGroup) view;
                    if (parent instanceof android.widget.RelativeLayout) {
                        android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
                                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        lp.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT, android.widget.RelativeLayout.TRUE);
                        parent.addView(pb, lp);
                    } else {
                        android.view.ViewGroup.LayoutParams lp = new android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        parent.addView(pb, lp);
                    }
                    pb.setVisibility(View.GONE);
                }
            } catch (Exception ignored) { }

            super.setupViews(view);

            // Fallback: if base did not bind the RecyclerView, try to resolve it manually
            try {
                if (clientsView == null && view != null) {
                    androidx.recyclerview.widget.RecyclerView rv = null;
                    try { rv = view.findViewById(R.id.recycler_view); } catch (Exception ignored) {}
                    if (rv == null) { try { rv = view.findViewById(org.smartregister.R.id.recycler_view); } catch (Exception ignored) {} }
                    if (rv == null) {
                        // Scan the view hierarchy for the first RecyclerView instance
                        rv = findFirstRecyclerView(view);
                    }
                    if (rv != null) {
                        clientsView = rv;
                        if (clientsView.getLayoutManager() == null && getContext() != null) {
                            clientsView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                        }
                        clientsView.setHasFixedSize(true);
                        if (clientAdapter == null) {
                            try { initializeAdapter(); } catch (Exception ignoredInitAdapter) {}
                        }
                    }
                }
            } catch (Exception ignored) { }

            // Toolbar Setup
            Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
            if (toolbar != null) {
                
                if (getActivity() instanceof AppCompatActivity) {
                    AppCompatActivity act = (AppCompatActivity) getActivity();
                    act.setSupportActionBar(toolbar);
                    if (act.getSupportActionBar() != null) {
                        act.getSupportActionBar().setDisplayShowTitleEnabled(false);
                    }
                }
                toolbar.setTitle("");
                android.widget.TextView titleLabel = toolbar.findViewById(org.smartregister.R.id.txt_title_label);
                if (titleLabel != null) {
                    titleLabel.setVisibility(View.GONE);
                }
                NavigationMenu navigationMenu = NavigationMenu.getInstance(getActivity(), null, toolbar);
                try {
                    if (navigationMenu != null && getActivity() != null) {
                        androidx.drawerlayout.widget.DrawerLayout drawer = navigationMenu.getDrawer();
                        androidx.appcompat.graphics.drawable.DrawerArrowDrawable arrow = new androidx.appcompat.graphics.drawable.DrawerArrowDrawable(getActivity());
                        arrow.setColor(android.graphics.Color.WHITE);
                        toolbar.setNavigationIcon(arrow);
                        toolbar.setNavigationOnClickListener(v -> {
                            if (drawer != null) drawer.openDrawer(androidx.core.view.GravityCompat.START);
                        });
                    }
                } catch (Throwable ignored) {}
                if (navigationMenu == null) {
                    Log.w("setupViews", "NavigationMenu is null. Skipping toolbar setup.");
                }
            } else {
                Log.w("setupViews", "Toolbar is null.");
            }

            // Navbar Setup
            View navbarContainer = view.findViewById(org.smartregister.R.id.register_nav_bar_container);
            if (navbarContainer != null) {
                navbarContainer.setFocusable(false);
                navbarContainer.bringToFront();
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
            android.widget.TextView titleView = view.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleView != null) {
                titleView.setVisibility(View.GONE);
            }

            // Search View Customization
            if (getSearchView() != null) {
                getSearchView().setBackgroundResource(org.smartregister.R.color.white);
                getSearchView().setCompoundDrawablesWithIntrinsicBounds(
                        org.smartregister.R.drawable.ic_action_search, 0, 0, 0
                );
                getSearchView().setTextColor(getResources().getColor(org.smartregister.R.color.text_black));
            } else {
                Log.w("setupViews", "Search view is null.");
            }

            // Hide Top Layouts
            View topRightLayout = view.findViewById(org.smartregister.R.id.top_right_layout);
            if (topRightLayout != null) {
                topRightLayout.setVisibility(View.GONE);
            } else {
                Log.w("setupViews", "Top-right layout is null.");
            }

            View topLeftLayout = view.findViewById(org.smartregister.R.id.top_left_layout);
            if (topLeftLayout != null) {
                topLeftLayout.setVisibility(View.GONE);
            } else {
                Log.w("setupViews", "Top-left layout is null.");
            }

            View sortFilterBarLayout = view.findViewById(org.smartregister.R.id.register_sort_filter_bar_layout);
            if (sortFilterBarLayout != null) {
                sortFilterBarLayout.setVisibility(View.GONE);
            } else {
                Log.w("setupViews", "Sort filter bar layout is null.");
            }

            View filterSortLayout = view.findViewById(org.smartregister.R.id.filter_sort_layout);
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

            // Attempt last-resort RecyclerView binding so the screen remains usable
            try {
                if (clientsView == null && view != null) {
                    androidx.recyclerview.widget.RecyclerView rv = null;
                    try { rv = view.findViewById(R.id.recycler_view); } catch (Exception ignored) {}
                    if (rv == null) {
                        rv = findFirstRecyclerView(view);
                    }
                    if (rv != null) {
                        clientsView = rv;
                        if (clientsView.getLayoutManager() == null && getContext() != null) {
                            clientsView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                        }
                        clientsView.setHasFixedSize(true);
                        if (clientAdapter == null) {
                            try { initializeAdapter(); } catch (Exception ignoredInitAdapter2) {}
                        }
                    }
                }
            } catch (Exception ignored2) { }

        }
    }

    private androidx.recyclerview.widget.RecyclerView findFirstRecyclerView(View root) {
        if (root instanceof androidx.recyclerview.widget.RecyclerView) {
            return (androidx.recyclerview.widget.RecyclerView) root;
        }
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                androidx.recyclerview.widget.RecyclerView found = findFirstRecyclerView(vg.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }

    @Override
    protected void setUpActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null) return;
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null) {
            View root = getView();
            if (root != null) {
                Toolbar toolbar = root.findViewById(org.smartregister.R.id.register_toolbar);
                if (toolbar != null) {
                    activity.setSupportActionBar(toolbar);
                    actionBar = activity.getSupportActionBar();
                }
            }
        }
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            View root = getView();
            if (root != null) {
                android.widget.TextView titleLabel = root.findViewById(org.smartregister.R.id.txt_title_label);
                if (titleLabel != null) {
                    titleLabel.setVisibility(View.VISIBLE);
                    titleLabel.setText(getString(R.string.all_index_title));
                }
            }
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
        return "(is_closed IS NULL OR is_closed != 1)";
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

            CommonPersonObjectClient client = ViewTagUtils.getTaggedClient(view);
            if (client == null) return;
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
        // Ensure RecyclerView is bound before setting adapter
        try {
            if (clientsView == null) {
                View root = getView();
                if (root != null) {
                    androidx.recyclerview.widget.RecyclerView rv = null;
                    try { rv = root.findViewById(R.id.recycler_view); } catch (Exception ignored) {}
                    if (rv == null) { try { rv = root.findViewById(org.smartregister.R.id.recycler_view); } catch (Exception ignored) {} }
                    if (rv == null) { rv = findFirstRecyclerView(root); }
                    if (rv != null) {
                        clientsView = rv;
                        if (clientsView.getLayoutManager() == null && getContext() != null) {
                            clientsView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                        }
                        clientsView.setHasFixedSize(true);
                    }
                }
            }
        } catch (Exception ignored) { }

        if (clientsView == null) {
            Log.e("IndexFragmentRegister", "RecyclerView not found; skipping adapter initialization");
            return;
        }

        IndexRegisterProvider registerProvider = new IndexRegisterProvider(requireContext(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(Constants.EcapClientTable.EC_CLIENT_INDEX));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);

        Log.d("IndexFragmentRegister", "Adapter initialized and attached to RecyclerView");


    }

    @Override
    protected boolean isSyncing() {
        return super.isSyncing();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        // Only refresh adapter; let base/presenter manage queries to avoid null repositories
        if (clientAdapter != null) {
            try { clientAdapter.notifyDataSetChanged(); } catch (Exception ignored) {}
        }
    }

    @Override
    public void showProgressView() {
        try {
            ProgressBar progressBar = null;
            if (binding != null) {
                progressBar = binding.clientListProgress;
            }
            if (progressBar == null && getView() != null) {
                // Try the most common progress bar IDs that actually exist
                try {
                    progressBar = getView().findViewById(R.id.progress_bar);
                } catch (Exception ignored) {}
                
                if (progressBar == null) {
                    try {
                        progressBar = getView().findViewById(R.id.progress_loading);
                    } catch (Exception ignored) {}
                }
                
                if (progressBar == null) {
                    try {
                        progressBar = getView().findViewById(R.id.client_list_progress);
                    } catch (Exception ignored) {}
                }
            }
            
            if (progressBar != null && progressBar.getVisibility() != View.VISIBLE) {
                progressBar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            // Defensive catch to prevent crashes - progress indicators are not critical
            Log.w("IndexFragmentRegister", "Could not show progress view: " + e.getMessage());
        }
    }

    @Override
    public void hideProgressView() {
        try {
            ProgressBar progressBar = null;
            if (binding != null) {
                progressBar = binding.clientListProgress;
            }
            if (progressBar == null && getView() != null) {
                // Try the most common progress bar IDs that actually exist
                try {
                    progressBar = getView().findViewById(R.id.progress_bar);
                } catch (Exception ignored) {}
                
                if (progressBar == null) {
                    try {
                        progressBar = getView().findViewById(R.id.progress_loading);
                    } catch (Exception ignored) {}
                }
                
                if (progressBar == null) {
                    try {
                        progressBar = getView().findViewById(R.id.client_list_progress);
                    } catch (Exception ignored) {}
                }
            }
            
            if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            // Defensive catch to prevent crashes - progress indicators are not critical
            Log.w("IndexFragmentRegister", "Could not hide progress view: " + e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus))) {
            Utils.showShortToast(getActivity(), getString(org.smartregister.R.string.sync_complete));
            getActivity().recreate();
            AppUpdater appUpdater = new AppUpdater(getActivity());
            appUpdater.start();
        } else {
            super.onSyncComplete(fetchStatus);
        }
    }
}


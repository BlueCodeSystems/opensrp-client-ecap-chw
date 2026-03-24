package com.bluecodeltd.ecap.chw.fragment;

import android.content.Intent;
import android.util.Log;
import com.bluecodeltd.ecap.chw.util.Threading;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

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
 

public class HouseholdIndexFragment extends BaseSafeRegisterFragment implements HouseholdIndexFragmentContract.View{

    private com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding binding;
    // Use centralized Threading

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
            Log.w("HouseholdIndexFragment", "Could not show progress view: " + e.getMessage());
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
            Log.w("HouseholdIndexFragment", "Could not hide progress view: " + e.getMessage());
        }
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new HouseholdIndexFragmentPresenter();
        ((HouseholdIndexFragmentPresenter)this.presenter).initView(this);
    }


    @Override
    public void setupViews(View view) {
        try { binding = com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding.bind(view); } catch (Throwable ignored) {}

        try {
            // Ensure required views exist before BaseRegisterFragment.setupViews(view)
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
            // Ensure clientsView references the bound RecyclerView
            try {
                if (clientsView == null && binding != null && binding.recyclerView != null) {
                    clientsView = binding.recyclerView;
                    if (clientsView.getLayoutManager() == null && getContext() != null) {
                        clientsView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
                    }
                    clientsView.setHasFixedSize(true);
                }
            } catch (Exception ignored2) { }

            // Toolbar customization
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
                NavigationMenu menu = NavigationMenu.getInstance(getActivity(), null, toolbar);
                if (menu != null && menu.getNavigationAdapter() != null) {
                    menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.HOUSEHOLD_REGISTER);
                }
                try {
                    if (menu != null && getActivity() != null) {
                        androidx.drawerlayout.widget.DrawerLayout drawer = menu.getDrawer();
                        androidx.appcompat.graphics.drawable.DrawerArrowDrawable arrow = new androidx.appcompat.graphics.drawable.DrawerArrowDrawable(getActivity());
                        arrow.setColor(android.graphics.Color.WHITE);
                        toolbar.setNavigationIcon(arrow);
                        toolbar.setNavigationOnClickListener(v -> {
                            if (drawer != null) drawer.openDrawer(androidx.core.view.GravityCompat.START);
                        });
                    }
                } catch (Throwable ignored) {}
            } else {
                NavigationMenu menu = NavigationMenu.getInstance(getActivity(), null, null);
                if (menu != null && menu.getNavigationAdapter() != null) {
                    menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.HOUSEHOLD_REGISTER);
                }
            }

            // Navbar container
            View navbarContainer = view.findViewById(org.smartregister.R.id.register_nav_bar_container);
            if (navbarContainer != null) {
                navbarContainer.setFocusable(false);
                navbarContainer.bringToFront();
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
            android.widget.TextView titleView = view.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleView != null) {
                titleView.setVisibility(View.GONE);
            }

            // Search view customization
            if (getSearchView() != null) {
                getSearchView().setBackgroundResource(R.color.white);
                getSearchView().setCompoundDrawablesWithIntrinsicBounds(
                        org.smartregister.R.drawable.ic_action_search, 0, 0, 0
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        View root = getView();
        if (root != null) {
            android.widget.TextView titleLabel = root.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleLabel != null) {
                titleLabel.setVisibility(View.VISIBLE);
                titleLabel.setText(getString(R.string.all_households_title));
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
                    titleLabel.setText(getString(R.string.all_households_title));
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
        return " status IS NULL AND caregiver_name IS NOT NULL";
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

        Object tag = view.getTag();
        if (!(tag instanceof CommonPersonObjectClient)) return;
        CommonPersonObjectClient client = (CommonPersonObjectClient) tag;

        Threading.io(() -> {
            String isClosed = null;
            try {
                isClosed = HouseholdDao.getHouseholdByBaseId(client.getColumnmaps().get("base_entity_id")).getStatus();
            } catch (Exception ignored) {}

            final String finalIsClosed = isClosed;
            Threading.main(() -> {
                if (finalIsClosed != null && finalIsClosed.equals("1")){
                    Toasty.warning(getActivity(), "This household has been deleted", Toast.LENGTH_LONG, true).show();
                } else {
                    goToIndexDetailActivity(client);
                }
            });
        });

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
        // Ensure RecyclerView is bound before setting adapter
        try {
            if (clientsView == null) {
                View root = getView();
                if (root != null) {
                    androidx.recyclerview.widget.RecyclerView rv = null;
                    try { rv = root.findViewById(R.id.recycler_view); } catch (Exception ignored) {}
                    if (rv == null) { try { rv = root.findViewById(org.smartregister.R.id.recycler_view); } catch (Exception ignored) {} }
                    if (rv == null && getView() instanceof android.view.ViewGroup) {
                        rv = findFirstRecyclerView(root);
                    }
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
            Log.e("HouseholdIndexFragment", "RecyclerView not found; skipping adapter initialization");
            return;
        }

        HouseholdRegisterProvider registerProvider = new HouseholdRegisterProvider(requireContext(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(Constants.EcapClientTable.EC_HOUSEHOLD));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
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
}

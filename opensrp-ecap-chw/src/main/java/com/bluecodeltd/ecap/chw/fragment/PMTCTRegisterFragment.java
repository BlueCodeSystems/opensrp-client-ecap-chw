package com.bluecodeltd.ecap.chw.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.MotherPmtctProfileActivity;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.presenter.PMTCTRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.PMTCTRegisterProvider;
import com.bluecodeltd.ecap.chw.util.Constants;
import com.github.javiersantos.appupdater.AppUpdater;
import com.bluecodeltd.ecap.chw.util.UpdateManager;

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

public class PMTCTRegisterFragment extends BaseSafeRegisterFragment implements IndexRegisterFragmentContract.View {

    private com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding binding;

    AlertDialog.Builder builder;

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
            Log.w("PMTCTRegisterFragment", "Could not show progress view: " + e.getMessage());
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
            Log.w("PMTCTRegisterFragment", "Could not hide progress view: " + e.getMessage());
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
                    titleLabel.setText(R.string.pmtct_services);
                }
            }
        }
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new PMTCTRegisterFragmentPresenter();
        ((PMTCTRegisterFragmentPresenter)this.presenter).initView(this);
    }


    @Override
    public void setupViews(View view) {
        try { binding = com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding.bind(view); } catch (Throwable ignored) {}
        // Ensure required views exist before BaseRegisterFragment.setupViews(view)
        try {
            // Ensure Toolbar exists for BaseRegisterFragment/NavigationMenu
            try {
                androidx.appcompat.widget.Toolbar tb = null;
                try { tb = view.findViewById(org.smartregister.R.id.register_toolbar); } catch (Exception ignored) {}
                if (tb == null && view instanceof android.view.ViewGroup) {
                    tb = new androidx.appcompat.widget.Toolbar(requireContext());
                    tb.setId(org.smartregister.R.id.register_toolbar);
                    android.view.ViewGroup parent = (android.view.ViewGroup) view;
                    if (parent instanceof android.widget.LinearLayout) {
                        parent.addView(tb, 0);
                    } else {
                        parent.addView(tb, new android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                (int) org.smartregister.chw.core.utils.Utils.convertDpToPixel(56, getActivity())
                        ));
                    }
                }
            } catch (Exception ignored) { }

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
        } catch (Exception ignored) { }
        Toolbar toolbar = null;
        try { toolbar = view.findViewById(org.smartregister.R.id.register_toolbar); } catch (Exception ignored) {}
        if (toolbar != null) {
            if (getActivity() instanceof AppCompatActivity) {
                AppCompatActivity act = (AppCompatActivity) getActivity();
                act.setSupportActionBar(toolbar);
                if (act.getSupportActionBar() != null) {
                    act.getSupportActionBar().setDisplayShowTitleEnabled(false);
                }
            }
            NavigationMenu menu = NavigationMenu.getInstance(getActivity(), null, toolbar);
            toolbar.setTitle("");
            android.widget.TextView titleLabelInner = toolbar.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleLabelInner != null) {
                titleLabelInner.setVisibility(View.GONE);
            }
            if (menu != null && menu.getNavigationAdapter() != null) {
                menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.PMTCT);
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
                menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.PMTCT);
            }
        }
        View navbarContainer = null;
        try { navbarContainer = view.findViewById(org.smartregister.R.id.register_nav_bar_container); } catch (Exception ignored) {}
        if (navbarContainer != null) {
            navbarContainer.setFocusable(false);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View searchBarLayout = null;
        try { searchBarLayout = view.findViewById(R.id.search_bar_layout); } catch (Exception ignored) {}
        if (searchBarLayout != null) {
            searchBarLayout.setLayoutParams(params);
            searchBarLayout.setBackgroundResource(R.color.primary);
            searchBarLayout.setPadding(searchBarLayout.getPaddingLeft(), searchBarLayout.getPaddingTop(), searchBarLayout.getPaddingRight(), (int) org.smartregister.chw.core.utils.Utils.convertDpToPixel(10, getActivity()));
        }

         ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }
        android.widget.TextView titleView = view.findViewById(org.smartregister.R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.GONE);
        }
        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(org.smartregister.R.color.text_black));
        }
        View topRightLayout = null;
        try { topRightLayout = view.findViewById(org.smartregister.R.id.top_right_layout); } catch (Exception ignored) {}
        if (topRightLayout != null) topRightLayout.setVisibility(View.GONE);
        View topLeftLayout = null;
        try { topLeftLayout = view.findViewById(org.smartregister.R.id.top_left_layout); } catch (Exception ignored) {}
        if (topLeftLayout != null) topLeftLayout.setVisibility(View.GONE);
        View sortFilterBarLayout = null;
        try { sortFilterBarLayout = view.findViewById(org.smartregister.R.id.register_sort_filter_bar_layout); } catch (Exception ignored) {}
        if (sortFilterBarLayout != null) sortFilterBarLayout.setVisibility(View.GONE);
        View filterSortLayout = null;
        try { filterSortLayout = view.findViewById(org.smartregister.R.id.filter_sort_layout); } catch (Exception ignored) {}
        if (filterSortLayout != null) filterSortLayout.setVisibility(View.GONE);

        builder = new AlertDialog.Builder(getActivity());

      /*  if (!isSyncing()){
            AppUpdater appUpdater = new AppUpdater(getActivity());
            appUpdater.start();
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        androidx.appcompat.widget.Toolbar toolbar = null;
        View root = getView();
        if (root != null) {
            try { toolbar = root.findViewById(org.smartregister.R.id.register_toolbar); } catch (Exception ignored) {}
        }
        if (toolbar != null) {
            toolbar.setTitle("");
            android.widget.TextView titleLabel = toolbar.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleLabel != null) {
                titleLabel.setText(R.string.pmtct_services);
                titleLabel.setVisibility(View.VISIBLE);
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
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        //return "case_status > 0 AND is_closed = 0 ";
        return "(delete_status IS NULL OR delete_status != '1') AND first_name IS NOT NULL AND last_name IS NOT NULL";
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
            String childId = client.getColumnmaps().get("base_entity_id");
            String clientId = client.getColumnmaps().get("pmtct_id");
//         Toasty.success(getActivity(),"Clicked the person",Toasty.LENGTH_LONG).show();
           goToMotherDetailActivity(clientId,client);
        }
    }

    protected void goToMotherDetailActivity(String clientId, CommonPersonObjectClient client) {

        Intent intent = new Intent(getActivity(), MotherPmtctProfileActivity.class);
        intent.putExtra("client_id",  clientId);
       intent.putExtra("baseId",  client);
        startActivity(intent);
    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    public void initializeAdapter() {
        // Bind RecyclerView defensively
        try {
            if (clientsView == null) {
                View root = getView();
                if (root != null) {
                    androidx.recyclerview.widget.RecyclerView rv = null;
                    try { rv = root.findViewById(R.id.recycler_view); } catch (Exception ignored) {}
                    if (rv == null) { try { rv = root.findViewById(org.smartregister.R.id.recycler_view); } catch (Exception ignored) {} }
                    if (rv == null && root instanceof android.view.ViewGroup) {
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
            Log.e("PMTCTRegisterFragment", "RecyclerView not found; skipping adapter initialization");
            return;
        }

        PMTCTRegisterProvider registerProvider = new PMTCTRegisterProvider(requireContext(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(Constants.EcapClientTable.EC_MOTHER_PMTCT));
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
            Utils.showShortToast(getActivity(), getString(org.smartregister.R.string.sync_complete));
            getActivity().recreate();
            UpdateManager.startOnce(getActivity());
        } else {
            super.onSyncComplete(fetchStatus);
        }
    }
}

package com.bluecodeltd.ecap.chw.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.IndexDetailsActivity;
import com.bluecodeltd.ecap.chw.activity.MotherDetail;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.contract.MotherIndexFragmentContract;
import com.bluecodeltd.ecap.chw.presenter.IndexRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.presenter.MotherIndexFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.IndexRegisterProvider;
import com.bluecodeltd.ecap.chw.provider.MotherRegisterProvider;
import com.bluecodeltd.ecap.chw.util.Constants;

import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

public class MotherIndexFragment extends BaseSafeRegisterFragment implements MotherIndexFragmentContract.View {

    private com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding binding;

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
            Log.w("MotherIndexFragment", "Could not show progress view: " + e.getMessage());
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
            Log.w("MotherIndexFragment", "Could not hide progress view: " + e.getMessage());
        }
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new MotherIndexFragmentPresenter();
        ((MotherIndexFragmentPresenter)this.presenter).initView(this);
    }


    @Override
    public void setupViews(View view) {
        try { binding = com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding.bind(view); } catch (Throwable ignored) {}
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
        } catch (Exception ignored) { }
        Toolbar toolbar = null;
        try { toolbar = view.findViewById(org.smartregister.R.id.register_toolbar); } catch (Exception ignored) {}
        if (toolbar != null) {
            toolbar.setContentInsetsAbsolute(0, 0);
            toolbar.setContentInsetsRelative(0, 0);
            toolbar.setContentInsetStartWithNavigation(0);
            if (getActivity() instanceof AppCompatActivity) {
                AppCompatActivity act = (AppCompatActivity) getActivity();
                act.setSupportActionBar(toolbar);
                if (act.getSupportActionBar() != null) {
                    act.getSupportActionBar().setDisplayShowTitleEnabled(false);
                    act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    act.getSupportActionBar().setHomeButtonEnabled(true);
                }
            }
            toolbar.setTitle("");
            android.widget.TextView titleLabel = toolbar.findViewById(org.smartregister.R.id.txt_title_label);
            if (titleLabel != null) {
                titleLabel.setVisibility(View.VISIBLE);
                titleLabel.setText(R.string.all_mother_title);
            }
            NavigationMenu menu = NavigationMenu.getInstance(getActivity(), null, toolbar);
            if (menu != null && menu.getNavigationAdapter() != null) {
                menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.MOTHER_REGISTER);
            }
        } else {
            NavigationMenu menu = NavigationMenu.getInstance(getActivity(), null, null);
            if (menu != null && menu.getNavigationAdapter() != null) {
                menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.MOTHER_REGISTER);
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
            searchBarLayout.setPadding(
                    searchBarLayout.getPaddingLeft(),
                    searchBarLayout.getPaddingTop(),
                    searchBarLayout.getPaddingRight(),
                    (int) org.smartregister.chw.core.utils.Utils.convertDpToPixel(10, getActivity())
            );
        }

        ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }
        android.widget.TextView titleView = view.findViewById(org.smartregister.R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(getString(R.string.all_mother_title));
            titleView.setClickable(false);
            if (titleView instanceof CustomFontTextView) {
                ((CustomFontTextView) titleView).setFontVariant(FontVariant.REGULAR);
            }
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
                titleLabel.setText(getString(R.string.all_mother_title));
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
            actionBar.setDisplayHomeAsUpEnabled(true);
            View root = getView();
            if (root != null) {
                android.widget.TextView titleLabel = root.findViewById(org.smartregister.R.id.txt_title_label);
                if (titleLabel != null) {
                    titleLabel.setVisibility(View.VISIBLE);
                    titleLabel.setText(getString(R.string.all_mother_title));
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
        return "deleted IS NULL";
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

        goToIndexDetailActivity((CommonPersonObjectClient) view.getTag());

    }

    protected void goToIndexDetailActivity(CommonPersonObjectClient commonPersonObjectClient) {

        Intent intent = new Intent(getActivity(), MotherDetail.class);
        intent.putExtra("mother",  commonPersonObjectClient);
        intent.putExtra("refresh",  "1");
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
            Log.e("MotherIndexFragment", "RecyclerView not found; skipping adapter initialization");
            return;
        }

        MotherRegisterProvider registerProvider = new MotherRegisterProvider(requireContext(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(Constants.EcapClientTable.EC_MOTHER_INDEX));
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

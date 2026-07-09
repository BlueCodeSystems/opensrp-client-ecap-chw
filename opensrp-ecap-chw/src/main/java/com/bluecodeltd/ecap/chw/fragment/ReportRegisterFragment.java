package com.bluecodeltd.ecap.chw.fragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.ReportRegisterActivity;
import com.bluecodeltd.ecap.chw.adapter.ReportFormOptionAdapter;
import com.bluecodeltd.ecap.chw.contract.ReportRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.domain.ReportType;
import com.bluecodeltd.ecap.chw.dao.HouseholdServiceReportDao;
import com.bluecodeltd.ecap.chw.presenter.ReportRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.util.Constants;

import org.smartregister.chw.core.custom_views.NavigationMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportRegisterFragment extends BaseSafeRegisterFragment implements ReportRegisterFragmentContract.View {

    private com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding binding;
    private RecyclerView.Adapter<?> formsAdapter;
    private boolean reportGridSpacingApplied;

    @Override
    protected void initializePresenter() {
        presenter = new ReportRegisterFragmentPresenter();
        ((ReportRegisterFragmentPresenter) presenter).initView(this);
    }

    @Override
    public void setupViews(View view) {
        try {
            binding = com.bluecodeltd.ecap.chw.databinding.FragmentBaseRegisterBinding.bind(view);
        } catch (Throwable ignored) {
        }

        super.setupViews(view);

        try {
            ensureReportGridConfig();
        } catch (Exception ignored) {
        }

        Toolbar toolbar = null;
        try {
            toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        } catch (Exception ignored) {
        }
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
                menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.REPORT_REGISTER);
            }
            try {
                if (menu != null && getActivity() != null) {
                    androidx.drawerlayout.widget.DrawerLayout drawer = menu.getDrawer();
                    androidx.appcompat.graphics.drawable.DrawerArrowDrawable arrow = new androidx.appcompat.graphics.drawable.DrawerArrowDrawable(getActivity());
                    arrow.setColor(android.graphics.Color.WHITE);
                    toolbar.setNavigationIcon(arrow);
                    toolbar.setNavigationOnClickListener(v -> {
                        if (drawer != null) {
                            drawer.openDrawer(androidx.core.view.GravityCompat.START);
                        }
                    });
                }
            } catch (Throwable ignored) {
            }
        } else {
            NavigationMenu menu = NavigationMenu.getInstance(getActivity(), null, null);
            if (menu != null && menu.getNavigationAdapter() != null) {
                menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.REPORT_REGISTER);
            }
        }

        View navbarContainer = view.findViewById(org.smartregister.R.id.register_nav_bar_container);
        if (navbarContainer != null) {
            navbarContainer.setFocusable(false);
            navbarContainer.bringToFront();
        }

        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        if (searchBarLayout != null) {
            searchBarLayout.setVisibility(View.GONE);
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

        hideIfPresent(view, org.smartregister.R.id.top_right_layout);
        hideIfPresent(view, org.smartregister.R.id.top_left_layout);
        hideIfPresent(view, org.smartregister.R.id.register_sort_filter_bar_layout);
        hideIfPresent(view, org.smartregister.R.id.filter_sort_layout);
    }

    private void hideIfPresent(View root, int viewId) {
        View target = null;
        try {
            target = root.findViewById(viewId);
        } catch (Exception ignored) {
        }
        if (target != null) {
            target.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setUpActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null) {
            return;
        }
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
                    titleLabel.setText(getReportTitle());
                }
            }
        }
    }

    @Override
    public String getSelectedReportType() {
        if (getActivity() instanceof com.bluecodeltd.ecap.chw.activity.ReportRegisterActivity) {
            return ((com.bluecodeltd.ecap.chw.activity.ReportRegisterActivity) getActivity()).getSelectedReportType();
        }
        return com.bluecodeltd.ecap.chw.activity.ReportRegisterActivity.REPORT_TYPE_MALARIA;
    }

    private String getReportTitle() {
        return getString(R.string.report_forms_title);
    }

    @Override
    public void setUniqueID(String s) {
        // No-op
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        // No-op
    }

    @Override
    protected String getMainCondition() {
        return "(delete_status IS NULL OR delete_status <> '1')";
    }

    @Override
    protected String getDefaultSortQuery() {
        return "substr(date,7,4) DESC, substr(date,4,2) DESC, substr(date,1,2) DESC";
    }

    @Override
    protected void startRegistration() {
        // No-op
    }

    @Override
    protected void onViewClicked(View view) {
        // No-op
    }

    @Override
    public void showNotFoundPopup(String s) {
        // No-op
    }

    @Override
    public void initializeAdapter() {
        try {
            ensureReportGridConfig();
        } catch (Exception ignored) {
        }

        if (clientsView == null) {
            Log.e("ReportRegisterFragment", "RecyclerView not found; skipping adapter initialization");
            return;
        }

        formsAdapter = new ReportFormOptionAdapter(buildFormOptions(), reportType -> {
            if (getActivity() instanceof ReportRegisterActivity) {
                ((ReportRegisterActivity) getActivity()).openReportList(reportType.getID());
            }
        });
        clientsView.setAdapter(formsAdapter);
    }

    public void refreshReportCards() {
        if (getActivity() == null || clientsView == null) {
            return;
        }
        initializeAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshReportCards();
    }

    @Override
    public void showProgressView() {
        try {
            ProgressBar progressBar = null;
            if (binding != null) {
                progressBar = binding.clientListProgress;
            }
            if (progressBar == null && getView() != null) {
                progressBar = getView().findViewById(R.id.client_list_progress);
            }
            if (progressBar != null && progressBar.getVisibility() != View.VISIBLE) {
                progressBar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.w("ReportRegisterFragment", "Could not show progress view: " + e.getMessage());
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
                progressBar = getView().findViewById(R.id.client_list_progress);
            }
            if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.w("ReportRegisterFragment", "Could not hide progress view: " + e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<ReportType> buildFormOptions() {
        String caseworkerName = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("caseworker_name", "");
        if (caseworkerName == null || caseworkerName.trim().isEmpty()) {
            caseworkerName = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getString("last_logged_in_username", "");
        }
        List<ReportType> options = new ArrayList<>();
        options.add(new ReportType(
                ReportRegisterActivity.REPORT_TYPE_MALARIA,
                getString(R.string.menu_malaria),
                HouseholdServiceReportDao.getMonthlyReportCount(ReportRegisterActivity.REPORT_TABLE_MALARIA, caseworkerName),
                getLastSubmitted(ReportRegisterActivity.REPORT_TABLE_MALARIA, caseworkerName)
        ));
        options.add(new ReportType(
                ReportRegisterActivity.REPORT_TYPE_NUTRITION,
                getString(R.string.report_nutrition),
                HouseholdServiceReportDao.getMonthlyReportCount(ReportRegisterActivity.REPORT_TABLE_NUTRITION, caseworkerName),
                getLastSubmitted(ReportRegisterActivity.REPORT_TABLE_NUTRITION, caseworkerName)
        ));
        options.add(new ReportType(
                ReportRegisterActivity.REPORT_TYPE_TB,
                getString(R.string.report_tb),
                HouseholdServiceReportDao.getMonthlyReportCount(ReportRegisterActivity.REPORT_TABLE_TB, caseworkerName),
                getLastSubmitted(ReportRegisterActivity.REPORT_TABLE_TB, caseworkerName)
        ));
        options.add(new ReportType(
                ReportRegisterActivity.REPORT_TYPE_COMMUNITY_ALERT,
                getString(R.string.report_community_alert),
                HouseholdServiceReportDao.getMonthlyReportCount(ReportRegisterActivity.REPORT_TABLE_COMMUNITY_ALERT, caseworkerName),
                getLastSubmitted(ReportRegisterActivity.REPORT_TABLE_COMMUNITY_ALERT, caseworkerName)
        ));
        return options;
    }

    private String getLastSubmitted(String tableName, String caseworkerName) {
        com.bluecodeltd.ecap.chw.model.MonthlyReportModel latest = com.bluecodeltd.ecap.chw.dao.MonthlyReportDao.getLatestReport(tableName);
        if (latest == null) {
            return "";
        }
        return latest.getLast_interacted_with();
    }

    private void ensureReportGridConfig() {
        if (clientsView == null) {
            View root = getView();
            if (root != null) {
                androidx.recyclerview.widget.RecyclerView recyclerView = root.findViewById(org.smartregister.R.id.recycler_view);
                if (recyclerView != null) {
                    clientsView = recyclerView;
                }
            }
        }

        if (clientsView == null || getContext() == null) {
            return;
        }

        int spanCount = getSpanCount();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (spanCount <= 1) {
                    return 1;
                }
                int itemCount = formsAdapter == null ? 0 : formsAdapter.getItemCount();
                if (itemCount > 0 && position == itemCount - 1 && (itemCount % 2 != 0)) {
                    return spanCount;
                }
                return 1;
            }
        });
        clientsView.setLayoutManager(gridLayoutManager);
        clientsView.setHasFixedSize(true);
        clientsView.setClipToPadding(false);

        int horizontalPadding = dpToPx(16);
        int verticalPadding = dpToPx(12);
        clientsView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);

        ViewGroup.LayoutParams layoutParams = clientsView.getLayoutParams();
        if (layoutParams instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) layoutParams;
            relativeLayoutParams.topMargin = dpToPx(16);
            clientsView.setLayoutParams(relativeLayoutParams);
        }

        if (!reportGridSpacingApplied) {
            int spacing = dpToPx(12);
            clientsView.addItemDecoration(new GridSpacingDecoration(spacing));
            reportGridSpacingApplied = true;
        }
    }

    private int getSpanCount() {
        if (getContext() == null) {
            return 1;
        }
        android.content.res.Configuration configuration = getResources().getConfiguration();
        if (configuration.smallestScreenWidthDp >= 600 || configuration.screenWidthDp >= 720) {
            return 2;
        }
        return 1;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private static class GridSpacingDecoration extends ItemDecoration {
        private final int spacing;

        private GridSpacingDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            int halfSpacing = spacing / 2;
            outRect.left = halfSpacing;
            outRect.right = halfSpacing;
            outRect.top = halfSpacing;
            outRect.bottom = halfSpacing;
        }
    }
}




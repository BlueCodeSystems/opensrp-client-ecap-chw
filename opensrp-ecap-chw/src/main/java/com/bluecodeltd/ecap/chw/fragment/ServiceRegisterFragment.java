package com.bluecodeltd.ecap.chw.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.ServiceRegisterActivity;
import com.bluecodeltd.ecap.chw.contract.IndexRegisterFragmentContract;
import com.bluecodeltd.ecap.chw.presenter.ServiceRegisterFragmentPresenter;
import com.bluecodeltd.ecap.chw.provider.ServiceRegisterProvider;
import com.bluecodeltd.ecap.chw.util.ServiceRegister;
import com.bluecodeltd.ecap.chw.util.Threading;
import com.bluecodeltd.ecap.chw.util.UpdateManager;

import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.Utils;

import java.util.HashMap;

/**
 * List screen shared by the Appointments, PrEP and Referral Linkages registers, modelled on
 * HivTestingServiceRegisterFragment. Which register it shows comes from the hosting
 * {@link ServiceRegisterActivity}, falling back to the {@link #ARG_REGISTER} argument.
 */
public class ServiceRegisterFragment extends BaseSafeRegisterFragment implements IndexRegisterFragmentContract.View {

    public static final String ARG_REGISTER = "service_register";

    private ServiceRegister register;

    public static ServiceRegisterFragment newInstance(ServiceRegister register) {
        ServiceRegisterFragment fragment = new ServiceRegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REGISTER, register.name());
        fragment.setArguments(args);
        return fragment;
    }

    public ServiceRegister getServiceRegister() {
        // BaseRegisterActivity replaces the fragment's arguments with the intent extras, so the
        // host activity is the reliable source.
        if (register == null && getActivity() instanceof ServiceRegisterActivity) {
            register = ((ServiceRegisterActivity) getActivity()).getServiceRegister();
        }
        if (register == null) {
            String name = getArguments() != null ? getArguments().getString(ARG_REGISTER) : null;
            register = name != null ? ServiceRegister.valueOf(name) : ServiceRegister.APPOINTMENTS;
        }
        return register;
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new ServiceRegisterFragmentPresenter(getServiceRegister());
        ((ServiceRegisterFragmentPresenter) this.presenter).initView(this);
    }

    @Override
    protected void setUpActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null) return;
        View root = getView();
        if (activity.getSupportActionBar() == null && root != null) {
            Toolbar toolbar = root.findViewById(org.smartregister.R.id.register_toolbar);
            if (toolbar != null) activity.setSupportActionBar(toolbar);
        }
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        showTitle();
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        Toolbar toolbar = null;
        try { toolbar = view.findViewById(org.smartregister.R.id.register_toolbar); } catch (Exception ignored) {}
        NavigationMenu menu;
        if (toolbar != null) {
            if (getActivity() instanceof AppCompatActivity) {
                AppCompatActivity act = (AppCompatActivity) getActivity();
                act.setSupportActionBar(toolbar);
                if (act.getSupportActionBar() != null) {
                    act.getSupportActionBar().setDisplayShowTitleEnabled(false);
                }
            }
            menu = NavigationMenu.getInstance(getActivity(), null, toolbar);
            toolbar.setTitle("");
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
            menu = NavigationMenu.getInstance(getActivity(), null, null);
        }
        if (menu != null && menu.getNavigationAdapter() != null) {
            menu.getNavigationAdapter().setSelectedView(getServiceRegister().drawerTag);
        }

        View navbarContainer = view.findViewById(org.smartregister.R.id.register_nav_bar_container);
        if (navbarContainer != null) {
            navbarContainer.setFocusable(false);
        }
        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        if (searchBarLayout != null) {
            searchBarLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            searchBarLayout.setBackgroundResource(R.color.primary);
            searchBarLayout.setPadding(searchBarLayout.getPaddingLeft(), searchBarLayout.getPaddingTop(), searchBarLayout.getPaddingRight(), (int) org.smartregister.chw.core.utils.Utils.convertDpToPixel(10, getActivity()));
        }
        ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }
        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(org.smartregister.R.color.text_black));
        }
        hide(view, org.smartregister.R.id.top_right_layout);
        hide(view, org.smartregister.R.id.top_left_layout);
        hide(view, org.smartregister.R.id.register_sort_filter_bar_layout);
        hide(view, org.smartregister.R.id.filter_sort_layout);
    }

    private void hide(View root, int id) {
        View view = root.findViewById(id);
        if (view != null) view.setVisibility(View.GONE);
    }

    private void showTitle() {
        View root = getView();
        if (root == null) return;
        Toolbar toolbar = root.findViewById(org.smartregister.R.id.register_toolbar);
        if (toolbar != null) toolbar.setTitle("");
        TextView titleLabel = root.findViewById(org.smartregister.R.id.txt_title_label);
        if (titleLabel != null) {
            titleLabel.setText(getServiceRegister().title);
            titleLabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity act = (AppCompatActivity) getActivity();
            if (act.getSupportActionBar() != null) {
                act.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        showTitle();
    }

    @Override
    public void showProgressView() {
        setProgressVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressView() {
        setProgressVisibility(View.GONE);
    }

    private void setProgressVisibility(int visibility) {
        View root = getView();
        if (root == null) return;
        ProgressBar progressBar = root.findViewById(R.id.client_list_progress);
        if (progressBar != null && progressBar.getVisibility() != visibility) {
            progressBar.setVisibility(visibility);
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
        // Applied in ServiceRegisterFragmentPresenter's selects; see the comment there.
        return "";
    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        super.filter(toSearchCondition(filterString), joinTableString, mainConditionString, qrCode);
    }

    /**
     * The non-FTS query path appends the filter verbatim, so turn the typed text into a
     * LIKE condition instead of letting it reach the SQL as-is.
     */
    private String toSearchCondition(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return "";
        }
        String term = searchText.trim().replace("'", "''");
        StringBuilder condition = new StringBuilder("AND (");
        String[] columns = {"first_name", "middle_name", "last_name", "client_number", "phone"};
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) condition.append(" OR ");
            condition.append(getServiceRegister().table).append('.').append(columns[i])
                    .append(" LIKE '%").append(term).append("%'");
        }
        return condition.append(")").toString();
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
        if (view.getId() == R.id.register_columns) {
            // No client profile yet; the details screen comes with the register's forms.
            Utils.showShortToast(getActivity(), getString(R.string.service_register_profile_pending));
        }
    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    public void initializeAdapter() {
        View root = getView();
        if (clientsView == null && root != null) {
            RecyclerView rv = root.findViewById(org.smartregister.R.id.recycler_view);
            if (rv == null) rv = root.findViewById(R.id.recycler_view);
            if (rv != null) {
                clientsView = rv;
                if (clientsView.getLayoutManager() == null && getContext() != null) {
                    clientsView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
                clientsView.setHasFixedSize(true);
            }
        }
        if (clientsView == null) {
            Log.e("ServiceRegisterFragment", "RecyclerView not found; skipping adapter initialization");
            return;
        }

        ServiceRegisterProvider registerProvider = new ServiceRegisterProvider(requireContext(), getServiceRegister(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(getServiceRegister().table));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void countExecute() {
        // Same reasoning as HivTestingServiceRegisterFragment: the base COUNT(*) runs on the
        // calling thread and can ANR while a sync holds the database lock.
        Threading.io(() -> {
            try {
                super.countExecute();
            } catch (Throwable ignored) { }
            Threading.main(() -> {
                if (!isAdded() || getActivity() == null) return;
                setTotalPatients();
            });
        });
    }

    @Override
    public void onLoadFinished(androidx.loader.content.Loader<android.database.Cursor> loader, android.database.Cursor cursor) {
        super.onLoadFinished(loader, cursor);
        setTotalPatients();
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

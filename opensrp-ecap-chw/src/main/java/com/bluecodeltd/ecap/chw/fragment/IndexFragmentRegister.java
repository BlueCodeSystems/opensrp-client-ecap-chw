package com.bluecodeltd.ecap.chw.fragment;

import android.app.AlertDialog;
import android.content.Intent;
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
        super.setupViews(view);
       Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);
        View navbarContainer = view.findViewById(R.id.register_nav_bar_container);
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
        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(getString(R.string.all_index_title));
            titleView.setFontVariant(FontVariant.REGULAR);
            titleView.setClickable(false);

        }
        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(org.smartregister.family.R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(org.smartregister.family.R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(R.color.text_black));
        }
        View topRightLayout = view.findViewById(R.id.top_right_layout);
        topRightLayout.setVisibility(View.GONE);
        View topLeftLayout = view.findViewById(org.smartregister.chw.core.R.id.top_left_layout);
        topLeftLayout.setVisibility(View.GONE);
        View sortFilterBarLayout = view.findViewById(org.smartregister.chw.core.R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(View.GONE);
        View filterSortLayout = view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout);
        filterSortLayout.setVisibility(View.GONE);

        builder = new AlertDialog.Builder(getActivity());

      /*  if (!isSyncing()){
            AppUpdater appUpdater = new AppUpdater(getActivity());
            appUpdater.start();
        }*/
    }



    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return "case_status != '3' AND is_closed != '1' AND (subpop1 = 'true' OR subpop2 = 'true' OR subpop3 = 'true' OR subpop4 = 'true' OR subpop5 = 'true' OR subpop = 'true')";
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
